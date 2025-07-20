package strategies;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import main.Metrics;
import main.TaskSimulator;

public class ForkJoinStrategy implements BenchmarkStrategy {

    private final int parallelism;
    private final boolean enableDiagnostics;

    public ForkJoinStrategy() {
        this(Runtime.getRuntime().availableProcessors(), false); // default: system CPU cores
    }

    public ForkJoinStrategy(int parallelism, boolean enableDiagnostics) {
        this.parallelism = parallelism;
        this.enableDiagnostics = enableDiagnostics;
    }

    @Override
    public Metrics execute(String taskType, int taskCount) throws InterruptedException {
        try (ForkJoinPool pool = new ForkJoinPool(parallelism)) {
            CountDownLatch latch = new CountDownLatch(taskCount);
            AtomicInteger completedTasks = new AtomicInteger(0);

            Instant start = Instant.now();

            for (int i = 0; i < taskCount; i++) {
                pool.submit(() -> {
                    Instant taskStart = null;
                    if (enableDiagnostics) {
                        taskStart = Instant.now();
                    }

                    try {
                        TaskSimulator.run(taskType);
                        completedTasks.incrementAndGet();
                    } catch (Exception e) {
                        System.err.println("Task error: " + e.getMessage());
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
                System.err.println("⚠️ Timeout: Some tasks did not complete.");
            }

            pool.shutdown();
            if (!pool.awaitTermination(1, TimeUnit.MINUTES)) {
                System.err.println("⚠️ ForkJoinPool did not shut down cleanly.");
            }

            return Metrics.calculate("ForkJoin", taskType, start, end, taskCount, completedTasks.get());
        }
    }
}
