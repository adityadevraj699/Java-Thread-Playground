package strategies;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import main.Metrics;
import main.TaskSimulator;

public class StructuredScopedStrategy implements BenchmarkStrategy {

    private final boolean enableDiagnostics;

    public StructuredScopedStrategy() {
        this(false); // default: diagnostics disabled
    }

    public StructuredScopedStrategy(boolean enableDiagnostics) {
        this.enableDiagnostics = enableDiagnostics;
    }

    @SuppressWarnings("preview")
    @Override
    public Metrics execute(String taskType, int taskCount) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(taskCount);
        AtomicInteger completedTasks = new AtomicInteger(0);
        Instant start = Instant.now();

        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            for (int i = 0; i < taskCount; i++) {
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

            // Wait up to 5 minutes for all tasks to finish
            boolean completed = latch.await(5, TimeUnit.MINUTES);
            if (!completed) {
                System.err.println("⚠️ Timeout: Not all structured tasks completed.");
            }

            scope.join();
        }

        Instant end = Instant.now();
        return Metrics.calculate("StructuredScoped", taskType, start, end, taskCount, completedTasks.get());
    }
}
