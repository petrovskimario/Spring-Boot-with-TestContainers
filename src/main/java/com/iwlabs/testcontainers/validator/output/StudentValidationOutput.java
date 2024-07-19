package com.iwlabs.testcontainers.validator.output;

import lombok.Data;

@Data
public class StudentValidationOutput {
    private String firstName;
    private String lastName;
    private Double gpa;
    private boolean valid;
}
