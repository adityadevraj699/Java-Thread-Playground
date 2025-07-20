package main;

import java.util.Scanner;

import strategies.BenchmarkStrategy;
import strategies.ForkJoinStrategy;
import strategies.PlatformStrategy;
import strategies.StructuredBatchingStrategy;
import strategies.StructuredScopedStrategy;
import strategies.VirtualStrategy;

public class Main {
    @SuppressWarnings("unused")
	public static void main(String[] args) throws InterruptedException {
        try (Scanner scanner = new Scanner(System.in)) {
			System.out.println("Welcome to JavaThreadPlayground!\n");

			System.out.println("Select Execution Strategy:");
			System.out.println("1. Platform Threads");
			System.out.println("2. Virtual Threads");
			System.out.println("3. ForkJoin Pool");
			System.out.println("4. Structured Scoped");
			System.out.println("5. Structured Batching");
			System.out.print("Enter your choice: ");
			int strategyChoice = scanner.nextInt();

			System.out.println("\nSelect Task Type:");
			System.out.println("1. CPU-bound");
			System.out.println("2. IO-bound");
			System.out.println("3. Mixed");
			System.out.print("Enter your choice: ");
			int taskTypeChoice = scanner.nextInt();

			System.out.print("\nEnter number of tasks: ");
			int taskCount = scanner.nextInt();

			BenchmarkStrategy strategy = switch (strategyChoice) {
			    case 1 -> new PlatformStrategy();
			    case 2 -> new VirtualStrategy();
			    case 3 -> new ForkJoinStrategy();
			    case 4 -> new StructuredScopedStrategy();
			    case 5 -> new StructuredBatchingStrategy();
			    default -> throw new IllegalArgumentException("Invalid strategy");
			};

			String taskType = switch (taskTypeChoice) {
			    case 1 -> "CPU";
			    case 2 -> "IO";
			    case 3 -> "MIXED";
			    default -> throw new IllegalArgumentException("Invalid task type");
			};

			System.out.println("\nRunning benchmark...\nPlease wait...\n");
			Metrics metrics = BenchmarkRunner.run(strategy, taskType, taskCount);
		}

        System.out.println("\n Result saved to results.txt");
    }
}