package com.iwlabs.testcontainers.kafka.producer;

import com.iwlabs.testcontainers.dto.StudentDTO;
import com.iwlabs.testcontainers.kafka.config.KafkaTopics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaSenderService {

    @Autowired
    private KafkaTemplate<String, StudentDTO> kafkaTemplate;

    @Autowired
    private KafkaTopics kafkaTopics;

    public void sendStudentDTO(StudentDTO studentDTO) {
        kafkaTemplate.send(kafkaTopics.getStudentTopic(), studentDTO);
    }
}
