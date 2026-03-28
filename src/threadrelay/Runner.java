package threadrelay;

import java.util.ArrayList;
import java.util.List;

public class Runner implements Runnable {

    public static final int SLOW    = 50;
    public static final int REGULAR = 30;
    public static final int FAST    = 10;

    private final int delay;
    private final int id;
    private int count;

    private final List<RunnerListener> listeners = new ArrayList<>();

    public Runner(int id, int delay) {
        this.id    = id;
        this.delay = delay;
    }

    // ── Gestione listener ────────────────────────────────────────────────────

    public void addListener(RunnerListener listener) {
        listeners.add(listener);
    }

    public void removeListener(RunnerListener listener) {
        listeners.remove(listener);
    }

    private void notifyCountUpdated() {
        for (RunnerListener l : listeners) {
            l.onCountUpdated(id, count);
        }
    }

    private void notifyFinished() {
        for (RunnerListener l : listeners) {
            l.onRunnerFinished(id);
        }
    }

    // ── Getter ───────────────────────────────────────────────────────────────

    public int getCount() {
        return count;
    }

    // ── Sospensione / ripresa ─────────────────────────────────────────────────

    private boolean suspended = false;

    public synchronized void suspend() {
        suspended = true;
    }

    public synchronized void resume() {
        suspended = false;
        notifyAll();
    }

    // Controlla il flag di sospensione: se attivo, blocca il thread finché non
    // viene chiamato resume(). Il lock viene rilasciato durante l'attesa.
    private synchronized void checkSuspended() throws InterruptedException {
        while (suspended) {
            wait();
        }
    }

    // ── Runnable ─────────────────────────────────────────────────────────────

    @Override
    public void run() {
        for (count = 0; count <= 99; count++) {
            try {
                Thread.sleep(delay);
                checkSuspended();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
            notifyCountUpdated();
        }
        notifyFinished();
    }
}
