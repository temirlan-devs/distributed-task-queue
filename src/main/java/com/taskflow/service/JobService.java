package com.taskflow.service;

import com.taskflow.api.dto.JobRequest;
import com.taskflow.api.dto.JobResponse;
import com.taskflow.domain.model.Job;
import com.taskflow.domain.model.JobStatus;
import com.taskflow.domain.repository.JobRepository;
import com.taskflow.kafka.producer.JobProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobService {
    
    private final JobRepository jobRepository;
    private final JobProducer jobProducer;

    @Transactional
    public JobResponse enqueue(JobRequest request) {

        // Idempotency check - don't enqueue the same job twice
        if (request.getIdempotencyKey() != null) {
            var existing = jobRepository.findByIdempotencyKey(request.getIdempotencyKey());
            if (existing.isPresent()) {
                log.info("Duplicate job detected for idempotency key {}",
                        request.getIdempotencyKey());
                return toResponse(existing.get());
            }
        }

        Job job = Job.builder()
                .type(request.getType())
                .payload(request.getPayload())
                .priority(request.getPriority())
                .status(JobStatus.PENDING)
                .retryCount(0)
                .maxRetries(request.getMaxRetries())
                .idempotencyKey(request.getIdempotencyKey())
                .build();
        
        job = jobRepository.save(job);
        jobProducer.sendJob(job);

        log.info("Job {} enqueued with priority {}", job.getId(), job.getPriority());
        return toResponse(job);
    }

    public JobResponse getJob(UUID id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found: " + id));
                return toResponse(job);
    }

    public List<JobResponse> getJobsByStatus(JobStatus status) {
        return jobRepository.findByStatus(status)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<JobResponse> getDeadLetterJobs() {
        return getJobsByStatus(JobStatus.DEAD);
    }

    @Transactional
    public JobResponse replayJob(UUID id) {
        Job job = jobRepostory.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found: " + id));
        
        job.setStatus(JobStatus.PENDING);
        job.setRetryCount(0);
        job.setErrorMessage(null);
        job = jobRepository.save(job);

        jobProducer.sendJob(job);
        log.info("Job {} replayed from dead letter queue", job.getId());
        return toResponse(job);
    }

    private JobResponse toResponse(Job job) {
        return JobResponse.builder()
                .id(job.getId())
                .type(job.getType())
                .payload(job.getPayload())
                .status(job.getStatus())
                .priority(job.getPriority())
                .retryCount(job.getRetryCount())
                .maxRetries(job.getMaxRetries())
                .idempotencyKey(job.getIdempotencyKey())
                .createdAt(job.getCreatedAt())
                .startedAt(job.getStartedAt())
                .completedAt(job.getCompletedAt())
                .errorMessage(job.getErrorMessage())
                .build();
    }


}
