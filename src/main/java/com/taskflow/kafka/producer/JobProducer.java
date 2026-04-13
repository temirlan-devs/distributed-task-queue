package com.taskflow.kafka.producer;

import com.taskflow.domain.model.Job;
import com.taskflow.domain.model.JobPriority;
import com.taskflow.kafka.config.KafkaConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
@Slf4j
public class JobProducer {
    
    private final KafkaTemplate<String, Job> kafkaTemplate;

    public void sendJob(Job job) {
        String topic = resolveTopic(job.getPriority());
        kafkaTemplate.send(topic, job.getId().toString(), job)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to send job {} to topic {}: {}", job.getId(), topic, ex.getMessage());
                    } else {
                        log.info("Job {} sent to topic {} partition {}",
                                job.getId(),
                                topic,
                                result.getRecordMetadata().partition()
                        )
                    }
                });
    }

    private String resolveTopic(JobPriority priority) {
        return switch (priority) {
            case HIGH -> KafkaConfig.HIGH_PRIORITY_TOPIC;
            case MEDIUM -> KafkaConfig.MEDIUM_PRIORITY_TOPIC;
            case LOW -> KafkaConfig.LOW_PRIORITY_TOPIC;
        };
    }

}
