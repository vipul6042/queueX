package com.queueX.backend.services.worker;

import com.queueX.backend.entity.Worker;
import com.queueX.backend.entity.WorkerContext;
import com.queueX.backend.services.job.JobProcessingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Service;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
@RequiredArgsConstructor
public class WorkerManagerService implements SmartLifecycle {
    private static final int WORKER_COUNT = 5;
    private final JobProcessingService jobProcessingService;
    private final WorkerService workerService;
    private final RedisHeartbeatService redisHeartbeatService;
    private final ExecutorService executorService = Executors.newFixedThreadPool(WORKER_COUNT);
    private final ScheduledExecutorService heartbeatScheduler = Executors.newScheduledThreadPool(WORKER_COUNT);
    private volatile boolean running = false;

    @Override
    public void start() {
        running = true;
        log.info("Starting {} workers...", WORKER_COUNT);

        for (int i = 1; i <= WORKER_COUNT; i++) {
            final int workerId = i;
            final String workerName = "worker-" + i;
            executorService.submit(() -> {

                Worker registeredWorker = workerService.registerWorker(workerName);
                log.info("Worker {} started", workerId);

                WorkerContext workerContext = new WorkerContext(
                        registeredWorker.getId(),
                        registeredWorker.getWorkerName(),
                        "worker:" + registeredWorker.getId() + ":heartbeat"
                );
                log.info(workerContext.redisKey());

                AtomicInteger heartbeatCounter=new AtomicInteger(0);//to avoid race condition

                ScheduledFuture<?> heartbeatTask =heartbeatScheduler.scheduleAtFixedRate(
                        () -> {
                            try {
                                redisHeartbeatService.setHeartbeat(workerContext);
                                if(heartbeatCounter.incrementAndGet()>=6){
                                    workerService.updateDbHeartbeat(workerContext);
                                    heartbeatCounter.set(0);
                                }
                            } catch (Exception e) {
                                log.error("Heartbeat failrd for {}", workerContext.workerName(), e);
                            }

                        },
                        0, 5, TimeUnit.SECONDS
                );

                while (running && !Thread.currentThread().isInterrupted()) {
                    try {
                        jobProcessingService.processNextJob(workerContext);
                    } catch (Exception e) {

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
        heartbeatScheduler.shutdownNow();
        try {
            if(!running)return;
            if (!executorService.awaitTermination(10, TimeUnit.SECONDS) &&
                    heartbeatScheduler.awaitTermination(10, TimeUnit.SECONDS)) {
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
}