package strategies;

import main.Metrics;

public interface BenchmarkStrategy {
    Metrics execute(String taskType, int taskCount) throws InterruptedException;
}