package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.example.model.Event;
import org.example.repository.EventRepository;

import java.util.concurrent.BlockingQueue;

/**
 * Service for processing event telemetry.
 * This service continuously reads events from a telemetry queue and logs them using an EventRepository.
 */
@RequiredArgsConstructor
public class EventProcessingService {
    private final BlockingQueue<Event> telemetryQueue;
    private final EventRepository eventRepository;

    /**
     * Starts the event processing in a separate thread.
     * This method initializes a thread that continuously processes events from the telemetry queue.
     */
    public void startProcessing() {
        val processingThread = new Thread(this::processEvents);
        processingThread.start();
    }

    /**
     * Continuously processes events from the telemetry queue.
     * Events are taken from the queue and logged using the EventRepository. This method runs in a loop until the thread is interrupted.
     */
    private void processEvents() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                val event = telemetryQueue.take();
                eventRepository.logEvent(event.getGoldDelta(), event.getCurrentGoldBalance());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
