package com.queueX.backend.dto;

import com.queueX.backend.enums.JobStatus;
import com.queueX.backend.enums.JobType;

import java.util.UUID;

public record GetJobResponse(
        UUID id,
        JobType jobType,
        String payload,
        JobStatus jobStatus,
        int retryCount,
        String errorMessage
) {}
