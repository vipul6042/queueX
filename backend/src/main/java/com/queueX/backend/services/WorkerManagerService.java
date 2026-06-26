package com.queueX.backend.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j

public class WorkerManagerService implements SmartLifecycle {
    private static final int WORKER_COUNT = 5;
    private final WorkerService workerService;
    private final ExecutorService executorService = Executors.newFixedThreadPool(WORKER_COUNT);
    private volatile boolean running = false;

    public WorkerManagerService(WorkerService workerService) {
        this.workerService = workerService;
    }

    @Override
    public void start() {
        running = true;
        log.info("Starting {} workers...", WORKER_COUNT);

        for (int i = 1; i <= WORKER_COUNT; i++) {
            final int workerId = i;
            executorService.submit(() -> {
                log.info("Worker {} started", workerId);
                while (running && !Thread.currentThread().isInterrupted()) {
                    try {
                        workerService.processNextJob();
                    }
//                    catch (InterruptedException e) {
//                        Thread.currentThread().interrupt();
//                        log.info("Worker {} interrupted. Stopping...", workerId);
//                        break;
//
//                    }
                    catch (Exception e) {

                        if (!running) {
                            log.debug("Worker {} got exception during shutdown (expected): {}", workerId, e.getMessage());
                            break;
                        }

                        log.error("Worker {} failed while processing job", workerId, e);

                    }
                }
                log.info("Worker {} stopped.", workerId);
            });
        }
    }

    @Override
    public void stop() {
        log.info("Stopping workers...");
        running = false;
        executorService.shutdownNow();
        try {
            if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
                log.warn("Workers did not stop within timeout.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        log.info("WorkerManager stopped.");
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public int getPhase() {
        // Higher phase = starts later, stops EARLIER
        // Redis connection factory is phase 0, so this stops before it
        return Integer.MAX_VALUE;
    }

//    @Override
//    public boolean isAutoStartup() {
//        return true;
//    }
}