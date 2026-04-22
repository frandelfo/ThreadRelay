package threadrelay;

public interface IRunnerSubject {
    void addListener(IRunnerListener listener);
    void removeListener(IRunnerListener listener);
}
