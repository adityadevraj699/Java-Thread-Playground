# Java Thread Playground

This project explores and benchmarks various Java 21 threading strategies, including Platform Threads, Virtual Threads (Project Loom), ForkJoin, and Structured Concurrency.

## Overview

With the introduction of virtual threads in Java 21, there is now a wide range of options for handling concurrent tasks in Java applications. This project evaluates the performance and scalability of different threading models by simulating CPU-bound, I/O-bound, and mixed workloads.

## Threading Strategies Implemented

- **Platform Threads**: Traditional thread model backed by OS-level threads.
- **Virtual Threads**: Lightweight threads introduced in Java 21 (Project Loom).
- **ForkJoin Pool**: Parallelism based on work-stealing, suitable for CPU-intensive tasks.
- **Structured Concurrency (Scoped Values)**: Modern thread lifecycle management using structured task scopes.
- **Structured Batching**: Batches and executes tasks in controlled structured scopes.

## Workload Types Simulated

- **CPU-bound**: Computationally intensive tasks (e.g., prime number checking).
- **I/O-bound**: Simulated using `Thread.sleep()` to mimic blocking operations.
- **Mixed**: A combination of both CPU and I/O operations.

## Key Findings

- **CPU-bound Tasks**: Best performance achieved with ForkJoin due to optimized parallel processing.
- **I/O-bound Tasks**: Virtual Threads and Structured Concurrency handle blocking more efficiently than platform threads.
- **Mixed Workloads**: Virtual Threads offer the best trade-off between performance and resource utilization.

## How to Run

### Prerequisites

- Java 21 or higher with `--enable-preview` enabled
- Git (optional for cloning)

### Compile

```bash
javac --enable-preview --release 21 -d out src/**/*.java
````

### Run

```bash
java --enable-preview -cp out main.BenchmarkRunner
```

You can edit `BenchmarkRunner.java` to change task type or task count.

## Results Output

The results are printed to the console and optionally logged to a file for analysis. Each strategy reports:

* Task type executed
* Total execution time
* Throughput and task completion rate
* Thread usage statistics

## Repository

GitHub: [Java Thread Playground](https://github.com/adityadevraj699/Java-Thread-Playground)

---

## License

This project is licensed under the MIT License.
