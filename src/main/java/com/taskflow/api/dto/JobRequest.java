package com.taskflow.api.dto;

import com.taskflow.domain.model.JobPriority;
import lobok.Data;

@Data
public class JobRequest {
    private String type;
    private String payload;
    private JobPriority priority;
    private String idempotencyKey;
    private int maxRetries = 3;
}
