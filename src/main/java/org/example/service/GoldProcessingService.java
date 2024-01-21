package org.example.service;

import lombok.SneakyThrows;
import lombok.val;
import org.example.model.Event;
import org.example.repository.ClanRepository;

import java.time.Instant;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Service for processing gold updates.
 * This service handles the updates of gold amounts and logs the operations.
 */
public class GoldProcessingService {

    private static GoldProcessingService goldProcessingService;
    private final ClanRepository clanRepository;
    private final ExecutorService goldUpdateExecutor = Executors.newSingleThreadExecutor();
    private final BlockingQueue<Integer> queue;
    private final BlockingQueue<Event> telemetryQueue;
    private final AtomicInteger atomicInteger = new AtomicInteger(0);

    /**
     * Private constructor for GoldProcessingService.
     *
     * @param clanRepository the repository to handle gold data persistence.
     * @param queue          the queue of gold update values.
     * @param telemetryQueue the queue for telemetry events.
     */
    private GoldProcessingService(ClanRepository clanRepository, BlockingQueue<Integer> queue, BlockingQueue<Event> telemetryQueue) {
        this.clanRepository = clanRepository;
        this.queue = queue;
        this.telemetryQueue = telemetryQueue;
    }

    /**
     * Provides a singleton instance of GoldProcessingService.
     *
     * @param clanRepository the repository to handle gold data persistence.
     * @param queue          the queue of gold update values.
     * @param telemetryQueue the queue for telemetry events.
     * @return the singleton instance of GoldProcessingService.
     */
    public static GoldProcessingService getInstance(ClanRepository clanRepository, BlockingQueue<Integer> queue, BlockingQueue<Event> telemetryQueue) {
        GoldProcessingService localGoldProcessingService = goldProcessingService;
        if (localGoldProcessingService == null) {
            synchronized (GoldProcessingService.class) {
                localGoldProcessingService = goldProcessingService;
                if (localGoldProcessingService == null) {
                    goldProcessingService = localGoldProcessingService = new GoldProcessingService(clanRepository, queue, telemetryQueue);
                }
            }
        }
        return localGoldProcessingService;
    }

    /**
     * Starts the gold update processing. This method listens for new values in the queue,
     * processes them, and logs the operation.
     */
    @SneakyThrows
    public void updateGoldState() {
        goldUpdateExecutor.submit(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    val value = queue.take();
                    val currentGoldAmount = clanRepository.updateGold(value);
                    telemetryQueue.put(Event.of(value, currentGoldAmount));

                    System.out.println("Operation number: " + atomicInteger.incrementAndGet() + ". " + Instant.now());
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }
}
