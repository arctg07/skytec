package org.example;

import org.example.model.Event;
import org.example.repository.ClanRepository;
import org.example.repository.EventRepository;
import org.example.service.EventProcessingService;
import org.example.service.GoldProcessingService;
import org.example.service.GoldUpdateProducer;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

/**
 * The main entry point for the gold processing application.
 * This class sets up the necessary components for gold transaction processing and event logging,
 * and then starts the processing services.
 */
public class Main {
    private static final ClanRepository clanRepository = new ClanRepository();
    private static final EventRepository eventRepository = new EventRepository();
    private static final BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(1000);
    private static final BlockingQueue<Event> telemetryQueue = new ArrayBlockingQueue<>(1000);

    /**
     * The main method to start the application.
     * Initializes the database, processing services, and starts the simulation of gold transactions.
     *
     * @param args The command line arguments (not used).
     * @throws InterruptedException if the thread is interrupted while waiting.
     */
    public static void main(String[] args) throws InterruptedException {
        // Initialization of repositories and services
        clanRepository.initClanDataBase();
        GoldProcessingService goldProcessingService = GoldProcessingService.getInstance(clanRepository, queue, telemetryQueue);
        EventProcessingService eventProcessingService = new EventProcessingService(telemetryQueue, eventRepository);
        goldProcessingService.updateGoldState();
        eventProcessingService.startProcessing();

        // Simulation for testing the functionality of gold processing and event logging
        int numThreads = 100;
        CountDownLatch latch = new CountDownLatch(numThreads);

        for (int i = 0; i < numThreads; i++) {
            Thread thread = new Thread(() -> {
                GoldUpdateProducer goldUpdateProducer = new GoldUpdateProducer(queue);
                goldUpdateProducer.updateGoldValue(1);
                latch.countDown();
            });
            thread.start();

            Thread thread2 = new Thread(() -> {
                GoldUpdateProducer goldUpdateProducer = new GoldUpdateProducer(queue);
                goldUpdateProducer.updateGoldValue(-1);
                latch.countDown();
            });
            thread2.start();
        }

        latch.await();
        Thread.sleep(10000);
    }
}
