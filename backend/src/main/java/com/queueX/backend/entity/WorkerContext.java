package com.queueX.backend.entity;


import java.util.UUID;

public record WorkerContext(
        UUID id,
        String workerName,
        String redisKey
) {}
