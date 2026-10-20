# Java Concurrent Collections Examples

[![Java](https://img.shields.io/badge/Java-17%2B-orange?logo=java)](https://www.oracle.com/java/technologies/javase-downloads.html)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Docs](https://img.shields.io/badge/docs-Javadoc-green)](./doc/index.html)

This educational repository features a set of examples demonstrating thread-safe data structures and concurrent collections provided by the [`java.util.concurrent`](https://docs.oracle.com/en/java/javase/26/docs/api/java.base/java/util/concurrent/package-summary.html) package in Java.

This project is part of the Concurrent Programming module at the [Federal University of Rio Grande do Norte (UFRN)](https://www.ufrn.br/), Natal, Brazil.

## 📚 Overview

When writing multi-threaded applications in Java, using standard non-thread-safe collections (such as [`java.util.ArrayList`](https://docs.oracle.com/en/java/javase/26/docs/api/java.base/java/util/ArrayList.html) or [`java.util.HashMap`](https://docs.oracle.com/en/java/javase/26/docs/api/java.base/java/util/HashMap.html)) can lead to race conditions, data corruption, or [`ConcurrentModificationException`](https://docs.oracle.com/en/java/javase/26/docs/api/java.base/java/util/ConcurrentModificationException.html). While traditional synchronization (such as [`Collections.synchronizedMap`](https://docs.oracle.com/en/java/javase/26/docs/api/java.base/java/util/Collections.html#synchronizedMap(java.util.Map)) or coarse synchronized blocks and methods) ensures thread safety, it often introduces significant performance bottlenecks due to thread contention.

The [`java.util.concurrent` package](https://docs.oracle.com/en/java/javase/26/docs/api/java.base/java/util/concurrent/package-summary.html) provides specialized, high-performance concurrent collections optimized for specific concurrency patterns, such as lock-free compare-and-swap operations, fine-grained bucket locking, copy-on-write snapshots, and blocking synchronization.

## 📂 Included Examples

This repository contains executable demos of the most commonly used concurrent data structures in Java.

| Class | Description | Collection |
| :---- | :---------- | :--------- |
| [`BlockingQueueDemo`](src/BlockingQueueDemo.java) | A generic producer-consumer workflow using a bounded queue | [`LinkedBlockingQueue`](https://docs.oracle.com/en/java/javase/26/docs/api/java.base/java/util/concurrent/LinkedBlockingQueue.html) |
| [`ConcurrentHashMapDemo`](src/ConcurrentHashMapDemo.java) | Inventory tracking with multiple warehouse worker threads concurrently modifying stock levels for various items | [`ConcurrentHashMap`](https://docs.oracle.com/en/java/javase/26/docs/api/java.base/java/util/concurrent/ConcurrentHashMap.html) |
| [`ConcurrentLinkedQueueDemo`](src/ConcurrentLinkedQueueDemo.java) | A simulated real-Time streaming log dashboard | [`ConcurrentLinkedQueue`](https://docs.oracle.com/en/java/javase/26/docs/api/java.base/java/util/concurrent/ConcurrentLinkedQueue.html) |
| [`CopyOnWriteArrayListDemo`](src/CopyOnWriteArrayListDemo.java) | A simulated multiplayer game lobby chat log with a broadcaster thread and multiple player threads | [`CopyOnWriteArrayList`](https://docs.oracle.com/en/java/javase/26/docs/api/java.base/java/util/concurrent/CopyOnWriteArrayList.html) |

### `BlockingQueueDemo`
- **Use case:** Thread coordination, rate-limiting, and producer-consumer workflows with backpressure.
- **Key concepts:**
  - Uses a bounded queue with moderate, lock-guarded read and write performance.
  - Blocking synchronization: `put(item)` automatically blocks when the queue reaches capacity until a consumer removes an item; `take()` automatically blocks when the queue is empty until a producer inserts an item.
  - Eliminates the need for manual `wait()`/`notify()` synchronization.

### `ConcurrentHashMapDemo`
- **Use case:** General-purpose, high-throughput shared key-value storage.
- **Key concepts:**
  - Non-blocking, very high-performance reads (lock-free) and high-performance concurrent writes.
  - Fine-grained bucket-level synchronization: locks only the specific hash bucket being updated rather than locking the entire map, allowing concurrent updates to different keys simultaneously.
  - Uses `compute(key, biFunction)` for atomic read-modify-write compound operations.

### `ConcurrentLinkedQueueDemo`
- **Use case:** High-throughput, non-blocking multi-producer/multi-consumer logging and task dispatching.
- **Key concepts:**
  - An unbounded, thread-safe, lock-free queue offering very high read and write performance.
  - Non-blocking semantics: leverages compare-and-swap (CAS) atomic operations to avoid thread blocking and locking overhead.
  - `poll()` immediately retrieves an element or returns `null` if the queue is empty without blocking.

### `CopyOnWriteArrayListDemo`
- **Use case:** Read-heavy, rarely modified shared lists and event listener registries.
- **Key concepts:**
  - Extremely high read performance: iterators capture an immutable snapshot of the underlying array at the time of creation, enabling lock-free reads.
  - Guarantees fail-safe iteration without explicit synchronization and without throwing `ConcurrentModificationException`.
  - Non-blocking reads with low write performance: every write operation (`add()`) creates a fresh copy of the internal array, making writes expensive while keeping reads fast and unblocked.

## 🚀 Getting Started

### Prerequisites
- JDK 17 or higher (compatible with JDK 8+)
- A terminal or IDE (IntelliJ, Eclipse, VS Code, etc.)

#### Running the Demos

```bash
# Run BlockingQueue Demo
java -cp src BlockingQueueDemo

# Run ConcurrentHashMap Demo
java -cp src ConcurrentHashMapDemo

# Run ConcurrentLinkedQueue Demo
java -cp src ConcurrentLinkedQueueDemo

# Run CopyOnWriteArrayList Demo
java -cp src CopyOnWriteArrayListDemo
```

## 📄 License

This project is licensed under the MIT License.
