package main;
import java.time.Duration;
import java.time.Instant;

public class Metrics {
    public String threadType;
    public String taskType;
    public long totalTimeMs;
    public long throughput;
    public double avgLatency;
    public long memoryUsedKB;
    public long threadsCreated;
    public int completedTasks; // ✅ ADD this field

    public Metrics(String threadType, String taskType, long totalTimeMs, long throughput,
                   double avgLatency, long memoryUsedKB, long threadsCreated, int completedTasks) {
        this.threadType = threadType;
        this.taskType = taskType;
        this.totalTimeMs = totalTimeMs;
        this.throughput = throughput;
        this.avgLatency = avgLatency;
        this.memoryUsedKB = memoryUsedKB;
        this.threadsCreated = threadsCreated;
        this.completedTasks = completedTasks;
    }

    public String format() {
        return "--- Results ---\n" +
               "Thread Type     : " + threadType + "\n" +
               "Task Type       : " + taskType + "\n" +
               "Total Time      : " + totalTimeMs + " ms\n" +
               "Throughput      : " + throughput + " tasks/sec\n" +
               "Avg Latency     : " + String.format("%.2f", avgLatency) + " ms\n" +
               "Memory Used     : " + memoryUsedKB + " KB\n" +
               "Threads Created : " + threadsCreated + "\n" +
               "Tasks Completed : " + completedTasks + "\n"; // ✅ ADD this
    }

    public static Metrics calculate(String threadType, String taskType, Instant start, Instant end, int taskCount, int completedTasks) {
        long totalTimeMs = Duration.between(start, end).toMillis();
        long throughput = (totalTimeMs > 0) ? (completedTasks * 1000L / totalTimeMs) : completedTasks;
        double avgLatency = (double) totalTimeMs / (completedTasks > 0 ? completedTasks : 1);
        long memoryUsedKB = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024;
        long threadsCreated = Thread.activeCount();
        return new Metrics(threadType, taskType, totalTimeMs, throughput, avgLatency, memoryUsedKB, threadsCreated, completedTasks);
    }
}
