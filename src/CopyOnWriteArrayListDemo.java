import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Demonstrates thread-safe, read-heavy concurrent operations using {@link CopyOnWriteArrayList}
 * in a simulated multiplayer game lobby chat log.
 *
 * <p>A persistent broadcaster thread iterates over the shared chat log via snapshot iteration,
 * ensuring safe traversal without explicit synchronization or risk of {@link java.util.ConcurrentModificationException}.
 * Simultaneously, multiple player threads concurrently append new messages using {@link CopyOnWriteArrayList#add(Object)},
 * creating a fresh copy of the underlying array on each write while existing iterators continue reading undisturbed.</p>
 */
public class CopyOnWriteArrayListDemo {
    private static final int NUM_PLAYERS = 2;

    public static void main(String[] args) throws InterruptedException {
        // The shared chat log
        CopyOnWriteArrayList<String> chatLog = new CopyOnWriteArrayList<>();

        // Seed the chat with an initial greeting
        chatLog.add("[System]: Welcome to the game lobby.");

        // 1. Broadcaster thread: Continuously reads and displays the entire chat history
        Thread broadcasterThread = new Thread(() -> {
            String threadName = Thread.currentThread().getName();
            try {
                System.out.printf("%n[%s] --- Streaming current chat snapshot ---%n", threadName);

                // The iterator captures a snapshot of the array at this exact moment
                for (String message : chatLog) {
                    System.out.printf("   %s%n", message);
                    Thread.sleep(100); // Intentionally slow down reading to allow writes mid-stream
                }
                Thread.sleep(400);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "Broadcaster-Thread");

        // 2. Player threads: Simulating active players posting messages concurrently
        try (ExecutorService playerPool = Executors.newFixedThreadPool(NUM_PLAYERS)) {
            String[] playerActions = {
                    "Player_Alpha: Match starting soon?",
                    "Player_Beta: I am ready!",
                    "Player_Alpha: Let's go!"
            };

            broadcasterThread.start();
            Thread.sleep(50); // Let the broadcaster print the initial snapshot first

            // Submit player messages to execute concurrently
            for (String action : playerActions) {
                playerPool.submit(() -> {
                    String threadName = Thread.currentThread().getName();

                    // Track the array reference before the writing
                    int oldIdentity = System.identityHashCode(chatLog.toArray());
                    chatLog.add("[" + threadName + "] " + action);

                    // To prove that a fresh copy is created on every write, print the
                    // internal array's memory identity (using System.identityHashCode())
                    int newIdentity = System.identityHashCode(chatLog.toArray());
                    System.out.printf(">> [%s] typed a message. (Underlying array modified -- old ID: %d -> new ID: %d)%n",
                            threadName, oldIdentity, newIdentity);
                });
                Thread.sleep(150); // Stagger player posts slightly
            }

            // Clean up threads
            playerPool.shutdown();
            playerPool.awaitTermination(5, TimeUnit.SECONDS);
        }
        broadcasterThread.join();

        System.out.println("\n=== Chat history saved ===");
        System.out.println(chatLog);
    }
}
