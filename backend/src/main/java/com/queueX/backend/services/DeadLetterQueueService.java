package com.queueX.backend.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor

public class DeadLetterQueueService {
    private static final String DLQ="dead-letter-queue";
    private final RedisTemplate<String,String> redisTemplate;

    public void push(String id){
        redisTemplate.opsForList().leftPush(DLQ,id);
    }

    public String pop(){
        return redisTemplate.opsForList().rightPop(DLQ);
    }
}
