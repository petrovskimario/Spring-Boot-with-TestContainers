package com.iwlabs.testcontainers.service;


import com.iwlabs.testcontainers.dto.StudentDTO;
import com.iwlabs.testcontainers.dto.StudentWithRankProjection;

import java.util.List;

public interface StudentService {
    StudentDTO createStudent(StudentDTO studentDTO);
    List<StudentWithRankProjection> getRankedStudents();
}
