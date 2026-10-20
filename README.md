# Java Concurrent Collections Examples

A collection of Java examples demonstrating thread-safe data structures and concurrent collections provided by the `java.util.concurrent` package.

---

## 📚 Overview

When writing multi-threaded applications in Java, using standard non-thread-safe collections (such as `ArrayList` or `HashMap`) can lead to race conditions, data corruption, or `ConcurrentModificationException`. While traditional synchronization (such as `Collections.synchronizedMap` or coarse `synchronized` blocks) ensures thread safety, it often introduces significant performance bottlenecks due to thread contention.

The `java.util.concurrent` package provides specialized, high-performance concurrent collections optimized for specific concurrency patterns (lock-free CAS operations, fine-grained bucket locking, copy-on-write snapshots, and blocking synchronization).

This repository contains clear, runnable demonstrations of the most commonly used concurrent data structures in Java.

---

## 📂 Included Examples

| Class | Collection | Concurrency Model | Key Operations / Features |
|---|---|---|---|
| [`BlockingQueueDemo`](src/BlockingQueueDemo.java) | `LinkedBlockingQueue` | Lock / Condition Blocking | `put()`, `take()`, Bounded queue, Producer-Consumer |
| [`ConcurrentHashMapDemo`](src/ConcurrentHashMapDemo.java) | `ConcurrentHashMap` | Bucket-level Locking & CAS | `compute()`, `put()`, Fine-grained lock striping |
| [`ConcurrentLinkedQueueDemo`](src/ConcurrentLinkedQueueDemo.java) | `ConcurrentLinkedQueue` | Lock-Free (CAS) | `offer()`, `poll()`, Unbounded, Non-blocking |
| [`CopyOnWriteArrayListDemo`](src/CopyOnWriteArrayListDemo.java) | `CopyOnWriteArrayList` | Copy-on-Write | Snapshot iteration, lock-free reads, array cloning on `add()` |

---

### 1. BlockingQueueDemo (`LinkedBlockingQueue`)
- **Use Case:** Producer-Consumer coordination with automatic backpressure and rate limiting.
- **Key Concepts:**
  - Uses a bounded queue (`capacity = 2`).
  - `put(item)` automatically blocks when the queue reaches capacity until a consumer removes an item.
  - `take()` automatically blocks when the queue is empty until a producer inserts an item.
  - Eliminates the need for manual `wait()` / `notify()` synchronization.

### 2. ConcurrentHashMapDemo (`ConcurrentHashMap`)
- **Use Case:** High-throughput shared key-value store (e.g., warehouse inventory tracking).
- **Key Concepts:**
  - Provides thread-safe reads and writes without locking the entire map.
  - Uses `compute(key, biFunction)` for atomic read-modify-write compound operations.
  - Synchronizes only the specific hash bucket being updated, allowing concurrent updates to different keys simultaneously.

### 3. ConcurrentLinkedQueueDemo (`ConcurrentLinkedQueue`)
- **Use Case:** High-performance, multi-producer logging / task dispatching.
- **Key Concepts:**
  - An unbounded, thread-safe, lock-free FIFO queue based on the Michael & Scott non-blocking queue algorithm.
  - Leverages Compare-And-Swap (CAS) atomic operations to avoid thread blocking and locking overhead.
  - `poll()` immediately retrieves an element or returns `null` if the queue is empty.

### 4. CopyOnWriteArrayListDemo (`CopyOnWriteArrayList`)
- **Use Case:** Read-heavy, rarely-modified shared lists (e.g., multiplayer game lobby chat / event listeners).
- **Key Concepts:**
  - Iterator captures an immutable snapshot of the underlying array at the time of creation.
  - Guarantees fail-safe iteration without explicit synchronization and without throwing `ConcurrentModificationException`.
  - Every write operation (`add()`) creates a fresh copy of the internal array, making writes expensive but reads extremely fast and lock-free.

---

## 🚀 Getting Started

### Prerequisites
- **JDK 17** or higher (compatible with JDK 8+)
- **IntelliJ IDEA** or any standard Java IDE / terminal

### Compilation and Execution

#### Compiling all examples:
```bash
javac src/*.java
```

#### Running individual demos:
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

---

## 📖 Key Takeaways & Comparison

| Collection | Best Suited For | Read Performance | Write Performance | Blocking? |
|---|---|---|---|---|
| `ConcurrentHashMap` | General-purpose shared key-value storage | Very High (lock-free) | High (bucket-level synchronization) | No |
| `CopyOnWriteArrayList` | Read-heavy lists, event listener registries | Extremely High (lock-free snapshots) | Low (copies underlying array) | No |
| `ConcurrentLinkedQueue` | High-throughput non-blocking producer-consumer | Very High (CAS) | Very High (CAS) | No |
| `LinkedBlockingQueue` | Thread coordination / rate-limiting producer-consumer | Moderate (lock-guarded) | Moderate (lock-guarded) | Yes (blocks on full/empty) |

---

## 📄 License
This repository is open-source and intended for educational purposes.
