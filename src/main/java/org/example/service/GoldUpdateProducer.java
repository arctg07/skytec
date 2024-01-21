package org.example.service;

import lombok.RequiredArgsConstructor;

import java.util.concurrent.BlockingQueue;

/**
 * Producer class responsible for updating gold values.
 * This class provides functionality to add gold update tasks to a processing queue.
 */
@RequiredArgsConstructor
public class GoldUpdateProducer {
    private final BlockingQueue<Integer> queue;

    /**
     * Adds a gold update value to the processing queue.
     * This method places a new gold value update task into the queue for further processing.
     *
     * @param gold The amount of gold to be updated (positive for addition, negative for subtraction).
     */
    public void updateGoldValue(int gold) {
        try {
            queue.put(gold);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

