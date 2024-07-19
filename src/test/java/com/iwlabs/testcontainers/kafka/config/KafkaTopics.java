package com.iwlabs.testcontainers.kafka.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class KafkaTopics {

    @Value("${kafka.topic.student}")
    private String studentTopic;
}
