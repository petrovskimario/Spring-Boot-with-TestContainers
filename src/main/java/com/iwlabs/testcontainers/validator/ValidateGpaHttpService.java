package com.iwlabs.testcontainers.validator;

import com.iwlabs.testcontainers.validator.input.StudentValidationInput;
import com.iwlabs.testcontainers.validator.output.StudentValidationOutput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class ValidateGpaHttpService {

    @Autowired
    private WebClient webClient;

    public boolean validateGpa(StudentValidationInput input) {
        return callStudentValidationService(input).isValid();
    }

    public StudentValidationOutput callStudentValidationService(StudentValidationInput input) {
        return webClient.post()
                .uri("/api/v1/validateGpa")
                .bodyValue(input)
                .retrieve()
                .bodyToMono(StudentValidationOutput.class)
                .block();
    }
}
