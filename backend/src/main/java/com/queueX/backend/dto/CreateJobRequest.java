package com.queueX.backend.dto;

import com.queueX.backend.enums.JobType;
import jakarta.validation.constraints.NotNull;

public record CreateJobRequest(
        @NotNull
        JobType jobType,
        @NotNull
        String payload
) {}
