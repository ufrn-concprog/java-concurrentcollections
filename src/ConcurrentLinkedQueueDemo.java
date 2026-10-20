import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Demonstrates high-performance, non-blocking producer-consumer operations using
 * {@link ConcurrentLinkedQueue} in a simulated real-time streaming log dashboard.
 *
 * <p>Multiple background server threads concurrently publish log events into an unbounded, lock-free queue
 * via {@link ConcurrentLinkedQueue#offer(Object)}. Simultaneously, a real-time monitoring dashboard thread
 * continuously polls items using {@link ConcurrentLinkedQueue#poll()}, which immediately retrieves an
 * available element or returns {@code null} without blocking, maximizing throughput via atomic
 * compare-and-swap (CAS) operations.</p>
 */
public class ConcurrentLinkedQueueDemo {
    private static final int NUM_SERVER_THREADS = 2;

    private ConcurrentLinkedQueueDemo() {}

    /**
     * The entry point of the application.
     * @param args Command line arguments (not used in this program).
     * @throws InterruptedException if any thread performing operations is interrupted.
     */
    public static void main(String[] args) throws InterruptedException {
        // High-performance, lock-free, unbounded queue using compare-and-swap operations
        ConcurrentLinkedQueue<String> logQueue = new ConcurrentLinkedQueue<>();

        // 1. Reader thread (simulating a real-time monitoring dashboard)
        Thread dashboardReader = new Thread(() -> {
            int emptyChecksCount = 0;
            System.out.println(" 📊 [Dashboard] Monitoring started...");

            // Loop until the system is shut down and the queue is completely drained
            while (!Thread.currentThread().isInterrupted()) {
                // poll() instantly returns the item OR returns null immediately if empty
                String log = logQueue.poll();

                if (log != null) {
                    System.out.printf(" 📊 [Dashboard] Processed: %s%n", log);
                    // Reset the empty counter if the log is successfully processed
                    emptyChecksCount = 0;
                } else {
                    // This block proves the thread did not block/sleep automatically
                    emptyChecksCount++;
                    System.out.printf(" 🔍 [Empty queue] Attempt #%d: No logs found. Thread did not block; checking again soon...%n", emptyChecksCount);

                    if (emptyChecksCount >= 5) {
                        System.out.println("[📊 Dashboard] No logs for a while. Stopping stream.");
                        break;
                    }
                }

                try {
                    Thread.sleep(300); // Manually throttle our loop so the output is readable
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        // 2. Server threads (producers dropping background events into the queue)
        try (ExecutorService serverPool = Executors.newFixedThreadPool(NUM_SERVER_THREADS)) {

            // Start reading immediately (it will encounter an empty queue first!)
            dashboardReader.start();
            Thread.sleep(500); // Give the dashboard time to perform a couple of empty checks

            // Staggered log generation from background server instances
            serverPool.submit(() -> {
                try {
                    Thread.sleep(200);
                    logQueue.offer("Server_Alpha: CPU Usage at 85%");
                    Thread.sleep(400);
                    logQueue.offer("Server_Alpha: Database connection established");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });

            serverPool.submit(() -> {
                try {
                    Thread.sleep(400);
                    logQueue.offer("Server_Beta: User 'admin' logged in");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });

            // Graceful cleanup and shutdown
            serverPool.shutdown();
            serverPool.awaitTermination(5, TimeUnit.SECONDS);
        }
        dashboardReader.join();

        System.out.println("\n=== Monitoring session finished ===");
    }
}
