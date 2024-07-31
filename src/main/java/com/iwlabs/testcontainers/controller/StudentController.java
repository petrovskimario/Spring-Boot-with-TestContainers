package com.iwlabs.testcontainers.controller;

import com.iwlabs.testcontainers.dto.StudentDTO;
import com.iwlabs.testcontainers.dto.StudentWithRankProjection;
import com.iwlabs.testcontainers.infrastructure.Endpoints;
import com.iwlabs.testcontainers.service.StudentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(Endpoints.STUDENT)
public class StudentController {

    private StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/ranked")
    public List<StudentWithRankProjection> getRankedStudents() {
        return studentService.getRankedStudents();
    }

    @PostMapping
    public StudentDTO createStudent(@RequestBody StudentDTO studentDTO) {
        return studentService.createStudent(studentDTO);
    }

}
