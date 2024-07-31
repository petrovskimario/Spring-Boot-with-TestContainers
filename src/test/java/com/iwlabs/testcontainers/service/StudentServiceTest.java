package com.iwlabs.testcontainers.service;

import com.iwlabs.testcontainers.configuration.TestcontainersConfiguration;
import com.iwlabs.testcontainers.dto.StudentDTO;
import com.iwlabs.testcontainers.infrastructure.exception.LowGpaException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ContextConfiguration(classes = TestcontainersConfiguration.class)
@SpringBootTest
@Testcontainers
@ActiveProfiles("testcontainers")
public class StudentServiceTest {

    @Autowired
    private StudentService studentService;

    @Test
    public void testCreateStudentWithLowGpa() {
        StudentDTO studentDTO = new StudentDTO(1L, "Jane", "Doe", "jane.doe@example.com", 1.0);
        assertThrows(LowGpaException.class, () -> studentService.createStudent(studentDTO));
    }

    @Test
    public void testCreateStudentWithHighGpa() {
        StudentDTO studentDTO = new StudentDTO(1L, "John", "Doe", "john.doe@example.com", 3.5);
        StudentDTO result = studentService.createStudent(studentDTO);
        assertEquals(studentDTO, result);
    }
}
