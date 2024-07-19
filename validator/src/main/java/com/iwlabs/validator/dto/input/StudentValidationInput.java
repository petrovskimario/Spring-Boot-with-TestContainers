package com.iwlabs.validator.dto.input;

import lombok.Data;

@Data
public class StudentValidationInput {
    private String firstName;
    private String lastName;
    private Double gpa;
}
