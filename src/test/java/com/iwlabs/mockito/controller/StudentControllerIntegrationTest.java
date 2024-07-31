package com.iwlabs.mockito.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iwlabs.testcontainers.TestContainersApplication;
import com.iwlabs.testcontainers.dto.StudentDTO;
import com.iwlabs.testcontainers.infrastructure.Endpoints;
import com.iwlabs.testcontainers.infrastructure.exception.LowGpaException;
import com.iwlabs.testcontainers.service.StudentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = TestContainersApplication.class)
@AutoConfigureMockMvc
public class StudentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    private StudentService studentService;

    @Test
    public void testCreateStudent() throws Exception {
        StudentDTO studentDTO = new StudentDTO(1L, "John", "Doe", "john.doe@example.com", 3.5);

        when(studentService.createStudent(any(StudentDTO.class))).thenReturn(studentDTO);

        String input = objectMapper.writeValueAsString(studentDTO);

        mockMvc.perform(post(Endpoints.STUDENT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(input))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(studentDTO.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(studentDTO.getLastName()))
                .andExpect(jsonPath("$.email").value(studentDTO.getEmail()))
                .andExpect(jsonPath("$.gpa").value(studentDTO.getGpa()));
    }

    @Test
    public void testCreateStudentWithLowGpa() throws Exception {
        StudentDTO studentDTO = new StudentDTO(1L, "Jane", "Doe", "jane.doe@example.com", 1.0);

        doThrow(new LowGpaException("Student with firstName : " + studentDTO.getFirstName() + " has low GPA. Not saving to DB"))
                .when(studentService).createStudent(any(StudentDTO.class));

        String input = objectMapper.writeValueAsString(studentDTO);

        mockMvc.perform(post(Endpoints.STUDENT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(input))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value("Student with firstName : " + studentDTO.getFirstName() + " has low GPA. Not saving to DB"));
    }
}
