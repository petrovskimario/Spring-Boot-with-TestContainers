package com.iwlabs.testcontainers.configuration;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

	private static final GenericContainer<?> gpaValidatorContainer = new GenericContainer<>(DockerImageName.parse("gpavalidator:latest"))
			.withExposedPorts(8081);

	@Bean
	@ServiceConnection
	KafkaContainer kafkaContainer() {
		return new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"));
	}

	@Bean
	@ServiceConnection
	PostgreSQLContainer<?> postgresContainer() {
		return new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"));
	}

	@Bean
	GenericContainer<?> gpaValidatorContainer() {
		return gpaValidatorContainer;
	}

	@PostConstruct
	public void startGpaValidatorContainer() {
		gpaValidatorContainer.start();
		String baseUrl = "http://" + gpaValidatorContainer.getHost() + ":" + gpaValidatorContainer.getMappedPort(8081);
		System.setProperty("validator.baseUrl", baseUrl);
	}
}
