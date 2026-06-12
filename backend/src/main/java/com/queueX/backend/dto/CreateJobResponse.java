package com.queueX.backend.dto;

import java.util.UUID;

public record CreateJobResponse(
        UUID id,
        String status
) {}
