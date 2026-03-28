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

    // ── Runnable ─────────────────────────────────────────────────────────────

    @Override
    public void run() {
        for (count = 0; count <= 99; count++) {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
            notifyCountUpdated();
        }
        notifyFinished();
    }
}
