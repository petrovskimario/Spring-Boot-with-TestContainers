package com.iwlabs.mockito.kafka;

import com.iwlabs.testcontainers.dto.StudentDTO;
import com.iwlabs.testcontainers.kafka.consumer.KafkaConsumerService;
import com.iwlabs.testcontainers.mapper.StudentMapper;
import com.iwlabs.testcontainers.model.Student;
import com.iwlabs.testcontainers.repository.StudentRepository;
import com.iwlabs.testcontainers.validator.ValidateGpaHttpService;
import com.iwlabs.testcontainers.validator.input.StudentValidationInput;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class KafkaConsumerServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private StudentMapper studentMapper;

    @Mock
    private ValidateGpaHttpService validateGpaHttpService;

    @InjectMocks
    private KafkaConsumerService kafkaConsumerService;

    @Test
    public void testListenWithHighGpa() {
        StudentDTO message = new StudentDTO(1L,"Jane", "Doe", "jane.doe@example.com", 3.5);
        StudentValidationInput input = new StudentValidationInput(message.getFirstName(), message.getLastName(), message.getGpa());
        Student student = new Student();

        when(validateGpaHttpService.validateGpa(input)).thenReturn(true);
        when(studentMapper.studentDTOToStudent(message)).thenReturn(student);

        kafkaConsumerService.listen(message);

        verify(studentRepository, times(1)).save(student);
    }

    @Test
    public void testListenWithLowGpa() {
        StudentDTO message = new StudentDTO(1L,"Jane", "Doe", "jane.doe@example.com", 1.5);
        StudentValidationInput input = new StudentValidationInput(message.getFirstName(), message.getLastName(), message.getGpa());

        when(validateGpaHttpService.validateGpa(input)).thenReturn(false);

        kafkaConsumerService.listen(message);

        verify(studentRepository, never()).save(any(Student.class));
    }
}
