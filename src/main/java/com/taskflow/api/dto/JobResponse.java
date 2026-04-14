package com.taskflow.api.dto;

import com.taskflow.domain.model.JobPriority;
import com.taskflow.domain.model.JobStatus;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class JobResponse {
    private UUID id;
    private String type;
    private String payload;
    private JobStatus status;
    private JobPriority priority;
    private int retryCount;
    private int maxRetries;
    private String idempotencyKey;
    private LocalDateTime createdAt;
    private LocalDataTime startedAt;
    private LocalDateTime completedAt;
    private String errorMessage;
}
