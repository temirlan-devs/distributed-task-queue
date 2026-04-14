package com.taskflow.api.controller;

import com.taskflow.api.dto.JobRequest;
import com.taskflow.api.dto.JobResponse;
import com.taskflow.domain.model.JobStatus;
import com.taskflow.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/jobs")
@RequiredArgsConstructor
public class JobController {
    
    private final JobService jobService;

    @PostMapping
    public ResponseEntity<JobResponse> enqueue(@RequestBody JobRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(jobService.enqueue(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobResponse> getJob(@PathVariable UUID id) {
        return ResponseEntity.ok(jobService.getJob(id));
    }

    @GetMapping
    public ResponseEntity<List<JobResponse>> getJobsByStatus(@RequestParam(required = false) JobStatus status) {
        if (status != null) {
            return ResponseEntity.ok(jobService.getJobsByStatus(status));
        }
        return ResponseEntity.ok(jobService.getJobsByStatus(JobStatus.PENDING));
    }


    @GetMappiong("/dead-letter")
    public ResponseEntity<List<JobResponse>> getDeadLetterJobs() {
        return ResponseEntity.ok(jobService.getDeadLetterJobs());
    }

    @PostMapping("/{id}/replay")
    public ResponseEntity<JobResponse> replayJob(@PathVariable UUID id) {
        return ResponseEntity.ok(jobService.replayJob(id));
    }

}
