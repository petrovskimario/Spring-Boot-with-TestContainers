package com.iwlabs.testcontainers.kafka.consumer;

import com.iwlabs.testcontainers.dto.StudentDTO;
import com.iwlabs.testcontainers.mapper.StudentMapper;
import com.iwlabs.testcontainers.model.Student;
import com.iwlabs.testcontainers.repository.StudentRepository;
import com.iwlabs.testcontainers.validator.ValidateGpaHttpService;
import com.iwlabs.testcontainers.validator.input.StudentValidationInput;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaConsumerService {

    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;
    private final ValidateGpaHttpService validateGpaHttpService;

    public KafkaConsumerService(StudentRepository studentRepository, StudentMapper studentMapper, ValidateGpaHttpService validateGpaHttpService) {
        this.studentRepository = studentRepository;
        this.studentMapper = studentMapper;
        this.validateGpaHttpService = validateGpaHttpService;
    }

    @KafkaListener(topics = "${kafka.topic.student}")
    public void listen(StudentDTO message) {
        log.info("Received message: {}", message);
        StudentValidationInput studentValidationInput = new StudentValidationInput(message.getFirstName(), message.getLastName(), message.getGpa());
        boolean isStudentValid = validateGpaHttpService.validateGpa(studentValidationInput);
        log.info("Student with firstName : " + message.getFirstName() + " is valid = " + isStudentValid);
        if(isStudentValid) {
            Student student = studentMapper.studentDTOToStudent(message);
            studentRepository.save(student);
        } else {
            log.info("Student with firstName : {} has low GPA. Not saving to DB", message.getFirstName());
        }

    }
}
