package com.taskflow.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;


@Configuration
public class KafkaConfig {
    
    public static final String HIGH_PRIORITY_TOPIC = "jobs.high";
    public static final String MEDIUM_PRIORITY_TOPIC = "jobs.medium";
    public static final String LOW_PRIORITY_TOPIC = "jobs.low";
    public static final String DEAD_LETTER_TOPIC = "jobs.dead";

    @Bean
    public NewTopic highPriorityTopic() {
        return TopicBuilder.name(HIGH_PRIORITY_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic mediumPriorityTopic() {
        return TopicBuilder.name(MEDIUM_PRIORITY_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic lowPriorityTopic() {
        return TopicBuilder.name(LOW_PRIORITY_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic deadLetterTopic() {
        return TopicBuilder.name(DEAD_LETTER_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
