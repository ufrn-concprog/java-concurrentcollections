import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ConcurrentLinkedQueueDemo {
    public static void main(String[] args) throws InterruptedException {
        // High-performance, lock-free, unbounded queue using CAS operations
        ConcurrentLinkedQueue<String> logQueue = new ConcurrentLinkedQueue<>();
        try (ExecutorService executor = Executors.newFixedThreadPool(3)) {

            // Simulating multiple threads dumping logs into the same queue concurrently
            for (int i = 1; i <= 3; i++) {
                final int threadId = i;
                executor.submit(() -> {
                    logQueue.offer("Log-Event from Thread " + threadId);
                });
            }

            executor.shutdown();
            executor.awaitTermination(5, TimeUnit.SECONDS);
        }

        String log;
        while ((log = logQueue.poll()) != null) {
            // poll() instantly returns null if empty instead of blocking
            System.out.println("Processing: " + log);
        }
    }
}
