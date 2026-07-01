package com.queueX.backend.services.worker;

import com.queueX.backend.entity.WorkerContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class RedisHeartbeatService {
    private final RedisTemplate<String,String>redisTemplate;
    private static final Duration HEARTBEAT_TTL =Duration.ofSeconds(15);

    public void setHeartbeat(WorkerContext workerContext){
        log.info("Sending heartbeat for {}", workerContext.workerName());
        redisTemplate.opsForValue().set(
                workerContext.redisKey(),"alive",HEARTBEAT_TTL
        );
    }

    Boolean isAlive(UUID id){
        String key="worker:" + id + ":heartbeat";
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

}
