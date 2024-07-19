package com.iwlabs.testcontainers.validator.input;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StudentValidationInput {
    private String firstName;
    private String lastName;
    private Double gpa;
}
