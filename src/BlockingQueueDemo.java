import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Demonstrates a producer-consumer workflow using a bounded {@link BlockingQueue}
 * (specifically {@link LinkedBlockingQueue}).
 *
 * <p>A producer thread pushes items into the queue and automatically blocks via
 * {@link BlockingQueue#put(Object)} when the queue reaches its capacity. Meanwhile,
 * a delayed consumer thread processes items out of the queue, automatically blocking via
 * {@link BlockingQueue#take()} when the queue is empty until new tasks become available.</p>
 */
public class BlockingQueueDemo {
    private static final int QUEUE_CAPACITY = 2;

    public static void main(String[] args) {
        // A bounded queue capped at two elements
        BlockingQueue<String> taskQueue = new LinkedBlockingQueue<>(QUEUE_CAPACITY);

        // Producer thread: Pushes items into the queue
        Thread producer = new Thread(() -> {
            try {
                String[] tasks = { "Task-A", "Task-B", "Task-C" };
                for (String task : tasks) {
                    System.out.println("   Producer attempting to add " + task);
                    if (taskQueue.remainingCapacity() == 0) {
                        System.out.println("⚠️ Queue is full. Producer is now waiting.");
                    }

                    taskQueue.put(task);    // Blocks automatically if the queue reaches its cap
                    System.out.println("✅ Producer successfully added " + task);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        // Consumer thread: Processes items out of the queue
        Thread consumer = new Thread(() -> {
            try {
                Thread.sleep(1000); // Intentionally delay consumer to let queue fill up
                while (true) {
                    System.out.println("   Consumer checking for available tasks...");
                    if (taskQueue.isEmpty()) {
                        System.out.println("⚠️ Queue is empty; no tasks available. Consumer is now waiting.");
                    }

                    // Blocks automatically if the queue is empty, waiting for new inputs
                    String task = taskQueue.take();
                    System.out.println("✅ Consumer processed " + task);
                    if (task.equals("Task-C")) break;   // Exit condition for the demo
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        producer.start();
        consumer.start();
    }
}
