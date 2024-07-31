package com.iwlabs.testcontainers.mapper;

import com.iwlabs.testcontainers.dto.StudentDTO;
import com.iwlabs.testcontainers.model.Student;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StudentMapper {
    Student studentDTOToStudent(StudentDTO studentDTO);
    StudentDTO studentToStudentDTO(Student student);
}
