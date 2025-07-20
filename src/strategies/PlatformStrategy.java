package strategies;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import main.Metrics;
import main.TaskSimulator;

public class PlatformStrategy implements BenchmarkStrategy {

    private final int threadPoolSize;
    private final boolean enableDiagnostics;

    public PlatformStrategy() {
        this(100, false); // Default values
    }

    public PlatformStrategy(int threadPoolSize, boolean enableDiagnostics) {
        this.threadPoolSize = threadPoolSize;
        this.enableDiagnostics = enableDiagnostics;
    }

    @Override
    public Metrics execute(String taskType, int taskCount) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(threadPoolSize);
        CountDownLatch latch = new CountDownLatch(taskCount);
        AtomicInteger completedTasks = new AtomicInteger(0);

        Instant start = Instant.now();

        for (int i = 0; i < taskCount; i++) {
            executor.submit(() -> {
                Instant taskStart = null;
                if (enableDiagnostics) {
                    taskStart = Instant.now();
                }

                try {
                    TaskSimulator.run(taskType);
                    completedTasks.incrementAndGet();
                } catch (Exception e) {
                    System.err.println("Task failed: " + e.getMessage());
                    e.printStackTrace();
                } finally {
                    if (enableDiagnostics && taskStart != null) {
                        Instant taskEnd = Instant.now();
                        long duration = Duration.between(taskStart, taskEnd).toMillis();
                        System.out.println("🧪 Task duration: " + duration + " ms");
                    }
                    latch.countDown();
                }
            });
        }

        boolean completed = latch.await(10, TimeUnit.MINUTES);
        Instant end = Instant.now();

        if (!completed) {
            System.err.println("⚠️ Timeout: Some tasks did not finish within expected time.");
        }

        executor.shutdown();
        if (!executor.awaitTermination(1, TimeUnit.MINUTES)) {
            System.err.println("⚠️ Executor did not shut down properly.");
        }

        return Metrics.calculate("Platform", taskType, start, end, taskCount, completedTasks.get());
    }
}