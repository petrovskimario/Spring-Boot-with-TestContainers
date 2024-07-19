package com.iwlabs.validator.service;

import com.iwlabs.validator.dto.input.StudentValidationInput;
import com.iwlabs.validator.dto.output.StudentValidationOutput;
import org.springframework.stereotype.Service;

@Service
public class StudentValidationService {

    public StudentValidationOutput validateGpa(StudentValidationInput input) {
        StudentValidationOutput output = new StudentValidationOutput();
        output.setFirstName(input.getFirstName());
        output.setLastName(input.getLastName());
        output.setGpa(input.getGpa());
        output.setValid(input.getGpa() >= 2.0);
        return output;
    }
}
