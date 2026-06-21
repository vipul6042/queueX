package com.queueX.backend.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QueueService {
    private static final String QUEUE_NAME = "job-queue";
    private final RedisTemplate<String, String> redisTemplate;

    public void push(String jobId) {
        redisTemplate.opsForList().leftPush(QUEUE_NAME, jobId);
    }

    public void pop() {
        redisTemplate.opsForList().rightPop(QUEUE_NAME);
    }

    public long queueSize() {
        return redisTemplate.opsForList().size(QUEUE_NAME);
    }

}
