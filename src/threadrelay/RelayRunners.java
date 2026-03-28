package threadrelay;

import javax.swing.*;
import java.awt.*;

public class RelayRunners extends JFrame implements RunnerListener {

    // Corsie: layout null per permettere lo spostamento animato delle icone
    private final JPanel[]  trackPanels  = new JPanel[4];
    private final JLabel[]  runnerIcons  = new JLabel[4];

    // Pannello destra
    private final JLabel[]  nameLabels   = new JLabel[4];
    private final JLabel[]  valueLabels  = new JLabel[4];

    // Controlli
    final JButton startButton = new JButton("Avvia");
    final JButton stopButton  = new JButton("Ferma");

    // Thread di coordinamento e runner attivo (volatile: accesso da più thread)
    private volatile Thread coordinatorThread;
    private volatile Thread currentRunnerThread;

    // Velocità assegnata a ciascun runner (modificabile in futuro)
    private static final int[] DELAYS = {
        Runner.SLOW, Runner.REGULAR, Runner.FAST, Runner.REGULAR
    };

    public RelayRunners() {
        setTitle("Relay Runners");
        setSize(700, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initComponents();
    }

    private void initComponents() {
        String[] names = {"Runner 1", "Runner 2", "Runner 3", "Runner 4"};

        // ── Pannello corsie (parte superiore sinistra) ───────────────────────
        // GridLayout(4,1) garantisce 4 righe di altezza uguale → allineamento
        // con le label del pannello destra
        JPanel tracksPanel = new JPanel(new GridLayout(4, 1, 0, 4));
        tracksPanel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));

        for (int i = 0; i < 4; i++) {
            trackPanels[i] = new JPanel(null);   // null layout per animazione
            trackPanels[i].setBackground(new Color(235, 235, 235));
            trackPanels[i].setBorder(BorderFactory.createLineBorder(Color.GRAY));

            runnerIcons[i] = new JLabel("\uD83C\uDFC3"); // 🏃
            runnerIcons[i].setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));

            // Posizionamento verticale centrato a ogni ridimensionamento
            final int idx = i;
            trackPanels[i].addComponentListener(new java.awt.event.ComponentAdapter() {
                @Override
                public void componentResized(java.awt.event.ComponentEvent e) {
                    int h = trackPanels[idx].getHeight();
                    int iconH = runnerIcons[idx].getPreferredSize().height;
                    int y = Math.max(0, (h - iconH) / 2);
                    // x invariato (gestito dall'animazione); all'avvio resta a 0
                    runnerIcons[idx].setBounds(
                        runnerIcons[idx].getX(), y, 32, iconH);
                }
            });

            runnerIcons[i].setBounds(0, 0, 32, 28);
            trackPanels[i].add(runnerIcons[i]);
            tracksPanel.add(trackPanels[i]);
        }

        // ── Pannello destra (striscia verticale) ─────────────────────────────
        // Stesso GridLayout(4,1) con stesso vgap → le righe si allineano 1:1
        // con le corsie a sinistra
        JPanel rightPanel = new JPanel(new GridLayout(4, 1, 0, 4));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 6));
        rightPanel.setPreferredSize(new Dimension(160, 0));

        for (int i = 0; i < 4; i++) {
            JPanel row = new JPanel(new BorderLayout());
            row.setBackground(new Color(220, 230, 245));
            row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(150, 170, 200)),
                BorderFactory.createEmptyBorder(0, 6, 0, 6)));

            nameLabels[i]  = new JLabel(names[i],  SwingConstants.LEFT);
            valueLabels[i] = new JLabel("0",        SwingConstants.RIGHT);
            valueLabels[i].setFont(valueLabels[i].getFont().deriveFont(Font.BOLD));

            row.add(nameLabels[i],  BorderLayout.CENTER);
            row.add(valueLabels[i], BorderLayout.EAST);
            rightPanel.add(row);
        }

        // ── Area superiore: corsie + striscia destra ─────────────────────────
        JPanel topArea = new JPanel(new BorderLayout());
        topArea.add(tracksPanel, BorderLayout.CENTER);
        topArea.add(rightPanel,  BorderLayout.EAST);

        // ── Area inferiore: pulsanti ─────────────────────────────────────────
        stopButton.setEnabled(false);
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 24, 8));
        buttonsPanel.add(startButton);
        buttonsPanel.add(stopButton);

        // ── Listener pulsanti ────────────────────────────────────────────────
        startButton.addActionListener(e -> startRelay());
        stopButton.addActionListener(e  -> stopRelay());

        // ── Layout principale ─────────────────────────────────────────────────
        setLayout(new BorderLayout());
        add(topArea,      BorderLayout.CENTER);
        add(buttonsPanel, BorderLayout.SOUTH);
    }

    // ── Logica relay ─────────────────────────────────────────────────────────

    private void startRelay() {
        startButton.setEnabled(false);
        stopButton.setEnabled(true);
        resetUI();

        // Thread coordinatore: esegue i runner uno alla volta in sequenza
        coordinatorThread = new Thread(() -> {
            for (int i = 0; i < 4; i++) {
                Runner runner = new Runner(i, DELAYS[i]);
                runner.addListener(this);

                currentRunnerThread = new Thread(runner);
                currentRunnerThread.start();

                try {
                    currentRunnerThread.join(); // attende la fine prima di passare al successivo
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    return; // stop richiesto: esce dal ciclo
                }
            }
            // Tutti e 4 i runner completati
            SwingUtilities.invokeLater(() -> {
                startButton.setEnabled(true);
                stopButton.setEnabled(false);
            });
        });
        coordinatorThread.start();
    }

    private void stopRelay() {
        // Interrompe il coordinatore; il coordinatore interromperà il runner attivo
        if (coordinatorThread != null) coordinatorThread.interrupt();
        if (currentRunnerThread != null) currentRunnerThread.interrupt();
        startButton.setEnabled(true);
        stopButton.setEnabled(false);
    }

    private void resetUI() {
        for (int i = 0; i < 4; i++) {
            valueLabels[i].setText("0");
            runnerIcons[i].setLocation(0, runnerIcons[i].getY());
        }
    }

    // ── RunnerListener ───────────────────────────────────────────────────────

    @Override
    public void onCountUpdated(int runnerId, int count) {
        // Chiamato da un thread secondario: aggiornamento UI sull'EDT
        SwingUtilities.invokeLater(() -> {
            valueLabels[runnerId].setText(String.valueOf(count));

            // Sposta l'icona proporzionalmente al conteggio (0-99)
            int trackWidth  = trackPanels[runnerId].getWidth();
            int iconWidth   = runnerIcons[runnerId].getWidth();
            int x = (int) ((trackWidth - iconWidth) * (count / 99.0));
            int y = runnerIcons[runnerId].getY();
            runnerIcons[runnerId].setLocation(x, y);
        });
    }

    @Override
    public void onRunnerFinished(int runnerId) {
        SwingUtilities.invokeLater(() ->
            valueLabels[runnerId].setText("Fine")
        );
    }
}
