package strategies;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.atomic.AtomicInteger;

import main.Metrics;
import main.TaskSimulator;

public class StructuredBatchingStrategy implements BenchmarkStrategy {

    private final int batchSize;
    private final boolean enableDiagnostics;

    public StructuredBatchingStrategy() {
        this(1000, false);  // default values
    }

    public StructuredBatchingStrategy(int batchSize, boolean enableDiagnostics) {
        this.batchSize = batchSize;
        this.enableDiagnostics = enableDiagnostics;
    }

    @SuppressWarnings("preview")
    @Override
    public Metrics execute(String taskType, int taskCount) throws InterruptedException {
        Instant start = Instant.now();
        AtomicInteger completedTasks = new AtomicInteger(0); // Track how many tasks completed

        for (int batchStart = 0; batchStart < taskCount; batchStart += batchSize) {
            int currentBatchSize = Math.min(batchSize, taskCount - batchStart);
            CountDownLatch latch = new CountDownLatch(currentBatchSize);

            try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
                for (int i = 0; i < currentBatchSize; i++) {
                    scope.fork(() -> {
                        Instant taskStart = null;
                        if (enableDiagnostics) {
                            taskStart = Instant.now();
                        }

                        try {
                            TaskSimulator.run(taskType);
                            completedTasks.incrementAndGet();
                        } catch (Exception e) {
                            System.err.println("Task failed: " + e.getMessage());
                        } finally {
                            if (enableDiagnostics && taskStart != null) {
                                Instant taskEnd = Instant.now();
                                long duration = Duration.between(taskStart, taskEnd).toMillis();
                                System.out.println("🧪 Task duration: " + duration + " ms");
                            }
                            latch.countDown();
                        }
                        return null;
                    });
                }

                // Safety: wait max 5 minutes for each batch
                boolean completed = latch.await(5, TimeUnit.MINUTES);
                if (!completed) {
                    System.err.println("⚠️ Timeout: Batch did not complete in time.");
                }

                scope.join(); // Wait for all tasks
            }
        }

        Instant end = Instant.now();
        return Metrics.calculate("StructuredBatching", taskType, start, end, taskCount, completedTasks.get());
    }
}
