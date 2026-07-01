package com.queueX.backend.services.queues;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class QueueService {
    private static final String QUEUE_NAME = "job-queue";
    private final RedisTemplate<String, String> redisTemplate;

    public void push(String jobId) {
        redisTemplate.opsForList().leftPush(QUEUE_NAME, jobId);
    }

    public String pop() {
        return redisTemplate.opsForList().rightPop(QUEUE_NAME, Duration.ofSeconds(59));
    }

}
