package com.taskflow.kafka.consumer;

import com.taskflow.domain.model.Job;
import com.taskflow.domain.model.JobStatus;
import com.taskflow.domain.repository.JobRepository;
import com.taskflow.kafka.config.KafkaConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

import org.springframework.cglib.core.Local;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class JobConsumer {
    
    private final JobRepository jobRepository;

    @KafkaListener(
        topics = {
            KafkaConfig.HIGH_PRIORITY_TOPIC,
            KafkaConfig.MEDIUM_PRIORITY_TOPIC,
            KafkaConfig.LOW_PRIORITY_TOPIC
        },
        groupId = "taskflow-workers",
        concurrency = "3"
    )

    public void consume(Job job, 
                        @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                        Acknowledgement acknowledgment) {

        log.info("Worker picked up job {} from topic {}", job.getId(), topic);

        try {
            job.setStatus(JobStatus.RUNNING);
            job.setStartedAt(LocalDateTime.now());
            jobRepository.save(job);

            processJob(job);

            job.setStatus(JobStatus.COMPLETED);
            job.setCompletedAt(LocalDateTime.now());
            jobRepository.save(job);

            acknowledgment.acknowledge();
            log.info("Job {} completed successfully", job.getId());

        } catch (Exception e) {
            log.error("Job {} failed: {}", job.getId(), e.getMessage());
            handleFailure(job, e);
            acknowledgment.acknowledge();
        }
    }

    private void processJob(Job job) throws Exception {
        // Simulate job processing - replace with real logic later
        log.info("Processing job type '{}' with payload: {}",
                job.getType(),
                job.getPayload()
        );
        Thread.sleep(100);
    }

    private void handleFailure(Job job, Exception e) {
        job.setRetryCount(job.getRetryCount() + 1);
        job.setErrorMessage(e.getMessage());

        if (job.getRetryCount() >= job.getMaxRetries()) {
            job.setStatus(JobStatus.DEAD);
            log.warn("Job {} exhauseted retries. Moving to deal letter queue.", job.getId());
        } else {
            job.setStatus(JobStatus.FAILED);
            log.warn("Job {} failed. Retry {}/{}", job.getId(), job.getRetryCount(), job.getMaxRetries());
        }

        jobRepository.save(job);
    }
    

}
