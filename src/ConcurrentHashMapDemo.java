import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Demonstrates thread-safe inventory tracking using {@link ConcurrentHashMap}.
 *
 * <p>Multiple warehouse worker threads concurrently modify stock levels for various items.
 * Atomic compound updates are performed using {@link ConcurrentHashMap#compute(Object, java.util.function.BiFunction)},
 * which synchronizes only the specific hash bucket for the target key during mutation rather than
 * locking the entire collection, maximizing concurrent throughput.</p>
 */
public class ConcurrentHashMapDemo {
    private static final int NUM_THREADS = 3;

    public static void main(String[] args) throws InterruptedException {
        // Shared live inventory map (Product Name -> Stock Quantity)
        ConcurrentHashMap<String, Integer> inventory = new ConcurrentHashMap<>();

        // Initialize stock levels
        inventory.put("Laptop", 50);
        inventory.put("Smartphone", 30);
        inventory.put("Headphones", 20);

        // A thread pool for warehouse order processing
        try (ExecutorService warehousePool = Executors.newFixedThreadPool(NUM_THREADS)) {

            System.out.println("=== Initial state of the inventory ===");
            System.out.println(inventory + "\n");

            // Simulate nine quick purchase events distributed across the threads
            String[] itemsToBuy = { "Laptop", "Smartphone", "Headphones", "Laptop",
                    "Headphones", "Smartphone", "Laptop", "Laptop", "Headphones" };

            for (String item : itemsToBuy) {
                warehousePool.submit(() -> {
                    String threadName = Thread.currentThread().getName();

                    // compute() is an atomic compound operation.
                    // It locks only the specific bucket for this item while updating.
                    inventory.compute(item, (key, currentStock) -> {
                        if (currentStock != null && currentStock > 0) {
                            int updatedStock = currentStock - 1;
                            // Printing inside the atomic block shows exactly which thread captured the lock
                            System.out.printf("[%s] successfully reserved 1 %s. Remaining stock: %d%n",
                                    threadName, key, updatedStock);
                            return updatedStock;
                        }
                        return currentStock;
                    });
                });
            }

            // Wait for all purchases to finalize
            warehousePool.shutdown();
            warehousePool.awaitTermination(5, TimeUnit.SECONDS);
        }

        System.out.println("\n=== Final state of the inventory ===");
        System.out.println(inventory);
    }
}
