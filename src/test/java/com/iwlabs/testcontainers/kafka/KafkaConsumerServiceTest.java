package com.iwlabs.testcontainers.kafka;

import com.iwlabs.testcontainers.configuration.TestcontainersConfiguration;
import com.iwlabs.testcontainers.dto.StudentDTO;
import com.iwlabs.testcontainers.kafka.producer.KafkaSenderService;
import com.iwlabs.testcontainers.repository.StudentRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@ContextConfiguration(classes = TestcontainersConfiguration.class)
@SpringBootTest
@Testcontainers
@ActiveProfiles("testcontainers")
public class KafkaConsumerServiceTest {

    @Container
    public static GenericContainer<?> gpaValidatorApp = new GenericContainer<>("gpavalidator")
            .withExposedPorts(8081);

    @DynamicPropertySource
    static void registerValidatorBaseUrl(DynamicPropertyRegistry registry) {
        String baseUrl = "http://" + gpaValidatorApp.getHost() + ":" + gpaValidatorApp.getMappedPort(8081);
        registry.add("validator.baseUrl", () -> baseUrl);
    }

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private KafkaSenderService kafkaSenderService;

    @Test
    public void testKafkaListenerWithLowGpa() throws InterruptedException {
        StudentDTO message = new StudentDTO();
        message.setFirstName("John");
        message.setLastName("Doe");
        message.setEmail("john@example.com");
        message.setGpa(1.5);

        kafkaSenderService.sendStudentDTO(message);

        Thread.sleep(5000);

        assertThat(studentRepository.count()).isEqualTo(0);
        assertThat(studentRepository.findByEmail(message.getEmail())).isEmpty();
    }

    @Test
    public void testKafkaListenerWithHighGpa() throws InterruptedException {
        StudentDTO message = new StudentDTO();
        message.setFirstName("Mark");
        message.setLastName("Doe");
        message.setEmail("mark@example.com");
        message.setGpa(3.5);

        kafkaSenderService.sendStudentDTO(message);

        Thread.sleep(5000);

        assertThat(studentRepository.count()).isEqualTo(1);
        assertThat(studentRepository.findByEmail(message.getEmail())).isNotEmpty();
    }

    @AfterEach
    public void tearDown() {
        studentRepository.deleteAll();
    }
}
