package com.iwlabs.validator.api;

import com.iwlabs.validator.dto.input.StudentValidationInput;
import com.iwlabs.validator.dto.output.StudentValidationOutput;
import com.iwlabs.validator.endpoints.Endpoints;
import com.iwlabs.validator.service.StudentValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Endpoints.VALIDATE_GPA)
public class GpaValidatorController {

    @Autowired
    private StudentValidationService studentValidationService;

    @PostMapping
    public StudentValidationOutput validateGpa(@RequestBody StudentValidationInput input) {
        return studentValidationService.validateGpa(input);
    }
}
