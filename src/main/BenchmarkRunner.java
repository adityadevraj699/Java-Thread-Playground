package main;

import strategies.*;
import utils.FileLogger;

public class BenchmarkRunner {
    public static Metrics run(BenchmarkStrategy strategy, String taskType, int taskCount) throws InterruptedException {
        Metrics result = strategy.execute(taskType, taskCount);
        System.out.println(result.format());
        FileLogger.log(result);
        return result;
    }
}