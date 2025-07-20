package strategies;

import java.time.Instant;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import main.Metrics;
import main.TaskSimulator;

public class VirtualStrategy implements BenchmarkStrategy {

    private final boolean enableDiagnostics;

    public VirtualStrategy() {
        this(false); // default: diagnostics disabled
    }

    public VirtualStrategy(boolean enableDiagnostics) {
        this.enableDiagnostics = enableDiagnostics;
    }

    @Override
    public Metrics execute(String taskType, int taskCount) throws InterruptedException {
        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
        CountDownLatch latch = new CountDownLatch(taskCount);
        AtomicInteger threadCounter = new AtomicInteger();
        AtomicInteger completedTasks = new AtomicInteger();

        // Optional: Warm-up run (helps JVM JIT warm up)
        for (int i = 0; i < 100; i++) {
            TaskSimulator.run(taskType);
        }

        Instant start = Instant.now();

        for (int i = 0; i < taskCount; i++) {
            int threadId = threadCounter.incrementAndGet();
            executor.submit(() -> {
                Instant taskStart = null;
                if (enableDiagnostics) {
                    taskStart = Instant.now();
                }

                try {
                    Thread.currentThread().setName("VirtualThread-" + threadId);
                    TaskSimulator.run(taskType);
                    completedTasks.incrementAndGet();
                } catch (Exception e) {
                    System.err.println("❌ Error in task " + threadId + ": " + e.getMessage());
                } finally {
                    if (enableDiagnostics && taskStart != null) {
                        Instant taskEnd = Instant.now();
                        long duration = taskEnd.toEpochMilli() - taskStart.toEpochMilli();
                        System.out.println("🧪 Task " + threadId + " duration: " + duration + " ms");
                    }
                    latch.countDown();
                }
            });
        }

        // Wait with timeout (optional safety)
        if (!latch.await(5, TimeUnit.MINUTES)) {
            System.err.println("⛔ Benchmark timed out after 5 minutes.");
        }

        Instant end = Instant.now();
        executor.shutdown();

        return Metrics.calculate("Virtual", taskType, start, end, taskCount, completedTasks.get());
    }
}
