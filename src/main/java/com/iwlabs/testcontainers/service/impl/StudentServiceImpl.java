package com.iwlabs.testcontainers.service.impl;

import com.iwlabs.testcontainers.dto.StudentDTO;
import com.iwlabs.testcontainers.dto.StudentWithRankProjection;
import com.iwlabs.testcontainers.infrastructure.exception.LowGpaException;
import com.iwlabs.testcontainers.mapper.StudentMapper;
import com.iwlabs.testcontainers.model.Student;
import com.iwlabs.testcontainers.repository.StudentRepository;
import com.iwlabs.testcontainers.service.StudentService;
import com.iwlabs.testcontainers.validator.ValidateGpaHttpService;
import com.iwlabs.testcontainers.validator.input.StudentValidationInput;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentServiceImpl implements StudentService {

    private StudentRepository studentRepository;
    private StudentMapper studentMapper;
    private ValidateGpaHttpService validateGpaHttpService;

    public StudentServiceImpl(StudentRepository studentRepository, StudentMapper studentMapper, ValidateGpaHttpService validateGpaHttpService) {
        this.studentRepository = studentRepository;
        this.studentMapper = studentMapper;
        this.validateGpaHttpService = validateGpaHttpService;
    }

    @Override
    public StudentDTO createStudent(StudentDTO studentDTO) {
        Student student = studentMapper.studentDTOToStudent(studentDTO);
        StudentValidationInput studentValidationInput = new StudentValidationInput(studentDTO.getFirstName(), studentDTO.getLastName(), studentDTO.getGpa());
        boolean isStudentValid = validateGpaHttpService.validateGpa(studentValidationInput);
        if(!isStudentValid) {
            throw new LowGpaException("Student with firstName : " + studentDTO.getFirstName() + " has low GPA. Not saving to DB");
        }
        Student savedStudent = studentRepository.save(student);
        return studentMapper.studentToStudentDTO(savedStudent);
    }

    @Override
    public List<StudentWithRankProjection> getRankedStudents() {
        return studentRepository.findStudentsWithRankByGpa();
    }
}
