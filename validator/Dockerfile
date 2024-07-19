FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY target/validator-0.0.1-SNAPSHOT.jar /app/validator.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "validator.jar"]
