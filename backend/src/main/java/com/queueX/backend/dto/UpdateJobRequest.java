package com.queueX.backend.dto;

import com.queueX.backend.enums.JobStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateJobRequest(
        @NotNull JobStatus status
) {
}
