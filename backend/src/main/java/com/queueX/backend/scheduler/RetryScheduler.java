package com.queueX.backend.scheduler;

import com.queueX.backend.services.DelayedQueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor

public class RetryScheduler {
    private final DelayedQueueService delayedQueueService;

    @Scheduled(fixedDelay = 5000)
    public void processDelayedJobs() {
        delayedQueueService.retryJob();
    }
}
