package main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Random;

public class TaskSimulator {

    // Simulates CPU-bound task by checking a large prime
    public static void simulateCPU() {
        final int num = 15485863;
        for (int i = 2; i * i <= num; i++) {
            if (num % i == 0) return;
        }
    }

    // Simulates I/O-bound task with sleep and optional file access
    public static void simulateIO() {
        try {
            Thread.sleep(50); // simulate latency

            // Optional: simulate file read latency
            try (BufferedReader br = new BufferedReader(new FileReader("src/data/sample.txt"))) {
                br.readLine(); // simulate small file read
            } catch (Exception ignored) {
                // File may not exist, fallback silently
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // Simulates mixed CPU + IO-bound task
    public static void simulateMixed() {
        simulateIO();
        for (int i = 0; i < 10_000; i++) {
            Math.log(i + 1);
        }
    }

    // Simulates memory-bound workload
    public static void simulateMemory() {
        int size = 5_000_000;
        int[] memoryHog = new int[size];
        Random rand = new Random();
        for (int i = 0; i < size; i++) {
            memoryHog[i] = rand.nextInt();
        }
    }

    public static void run(String taskType) {
        switch (taskType.toUpperCase()) {
            case "CPU" -> simulateCPU();
            case "IO" -> simulateIO();
            case "MIXED" -> simulateMixed();
            case "MEMORY" -> simulateMemory(); // NEW
            default -> throw new IllegalArgumentException("Unknown task type: " + taskType);
        }
    }
}
