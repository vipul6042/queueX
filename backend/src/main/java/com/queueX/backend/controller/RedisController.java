package com.queueX.backend.controller;

import com.queueX.backend.services.queues.QueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/redis")
@RequiredArgsConstructor
public class RedisController {
    private final RedisTemplate<String,String>redisTemplate;
    private final QueueService queueService;

    @GetMapping("/test")
    public String test(){
        redisTemplate.opsForValue().set("name","vipul");
        return redisTemplate.opsForValue().get("name");
    }
    @PostMapping("/{id}")
    public String push(@PathVariable String id){
        queueService.push(id);
        return "added";

    }
}
