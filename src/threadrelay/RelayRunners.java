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

        // ── Layout principale ─────────────────────────────────────────────────
        setLayout(new BorderLayout());
        add(topArea,      BorderLayout.CENTER);
        add(buttonsPanel, BorderLayout.SOUTH);
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
