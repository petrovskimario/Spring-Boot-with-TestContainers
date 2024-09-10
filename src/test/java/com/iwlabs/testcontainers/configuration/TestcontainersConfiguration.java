package com.iwlabs.testcontainers.configuration;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

	@Container
	private static final GenericContainer<?> gpaValidatorContainer = new GenericContainer<>(DockerImageName.parse("gpavalidator:latest"))
			.withExposedPorts(8081);

	@Container
	private static final KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"));

	@Container
	private static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:latest");

	@Bean
	@ServiceConnection
	public KafkaContainer kafkaContainer() {
		return kafkaContainer;
	}

	@Bean
	@ServiceConnection
	public PostgreSQLContainer<?> postgresContainer() {
		return postgresContainer;
	}

	@PostConstruct
	public void startGpaValidatorContainer() {
		if(!gpaValidatorContainer.isRunning()) {
			gpaValidatorContainer.start();
		}
		String baseUrl = "http://" + gpaValidatorContainer.getHost() + ":" + gpaValidatorContainer.getMappedPort(8081);
		System.setProperty("validator.baseUrl", baseUrl);
	}
}
