package threadrelay;

public class Runner implements Runnable {

    public static final int SLOW    = 500;
    public static final int REGULAR = 300;
    public static final int FAST    = 100;

    private final int delay;
    private int count;

    public Runner(int delay) {
        this.delay = delay;
    }

    public int getCount() {
        return count;
    }

    @Override
    public void run() {
        for (count = 0; count <= 99; count++) {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }
}
