package com.queueX.backend.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor

public class DelayedQueueService {
    private static final String QUEUE_NAME = "delayed-queue";
    private final RedisTemplate<String, String> redisTemplate;
    private final QueueService queueService;

    public void push(String id, long score) {
        redisTemplate.opsForZSet().add(QUEUE_NAME, id, score);
    }

    public void retryJob() {
        long currTime = System.currentTimeMillis();
        Set<String> readyJobs = redisTemplate.opsForZSet().rangeByScore(QUEUE_NAME, 0, currTime);

        if (readyJobs == null || readyJobs.isEmpty()) return;

        for (String jobId : readyJobs) {
//            make it an atomic event
            redisTemplate.opsForZSet().remove(QUEUE_NAME, jobId);
            queueService.push(jobId);
        }
    }
}
