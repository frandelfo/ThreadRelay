package threadrelay;

public interface RunnerListener {
    void onCountUpdated(int runnerId, int count);
    void onRunnerFinished(int runnerId);
}
