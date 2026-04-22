package threadrelay;

public interface IRunnerListener {
    void onCountUpdated(int runnerId, int count);
    void onRunnerFinished(int runnerId);
}
