package com.iwlabs.mockito.service;

import com.iwlabs.testcontainers.dto.StudentDTO;
import com.iwlabs.testcontainers.infrastructure.exception.LowGpaException;
import com.iwlabs.testcontainers.mapper.StudentMapper;
import com.iwlabs.testcontainers.model.Student;
import com.iwlabs.testcontainers.repository.StudentRepository;
import com.iwlabs.testcontainers.service.impl.StudentServiceImpl;
import com.iwlabs.testcontainers.validator.ValidateGpaHttpService;
import com.iwlabs.testcontainers.validator.input.StudentValidationInput;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private StudentMapper studentMapper;

    @Mock
    private ValidateGpaHttpService validateGpaHttpService;

    @InjectMocks
    private StudentServiceImpl studentService;

    @Test
    public void testCreateStudentWithLowGpa() {
        StudentDTO studentDTO = new StudentDTO(1L, "Jane", "Doe", "jane.doe@example.com", 1.0);
        Student student = new Student();

        when(studentMapper.studentDTOToStudent(any(StudentDTO.class))).thenReturn(student);
        when(validateGpaHttpService.validateGpa(any(StudentValidationInput.class))).thenReturn(false);

        assertThrows(LowGpaException.class, () -> studentService.createStudent(studentDTO));
    }

    @Test
    public void testCreateStudentWithHighGpa() {
        StudentDTO studentDTO = new StudentDTO(1L, "John", "Doe", "john.doe@example.com", 3.5);
        Student student = new Student();
        Student savedStudent = new Student();

        when(studentMapper.studentDTOToStudent(any(StudentDTO.class))).thenReturn(student);
        when(validateGpaHttpService.validateGpa(any(StudentValidationInput.class))).thenReturn(true);
        when(studentRepository.save(any(Student.class))).thenReturn(savedStudent);
        when(studentMapper.studentToStudentDTO(any(Student.class))).thenReturn(studentDTO);

        StudentDTO result = studentService.createStudent(studentDTO);
        assertEquals(studentDTO.getFirstName(), result.getFirstName());
        assertEquals(studentDTO.getLastName(), result.getLastName());
        assertEquals(studentDTO.getEmail(), result.getEmail());
        assertEquals(studentDTO.getGpa(), result.getGpa());
    }
}
