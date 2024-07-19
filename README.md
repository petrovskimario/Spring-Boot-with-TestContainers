# Spring Boot and TestContainers integration

This is a POC application that demonstrates the benefits of using TestContainers in a Spring Boot application.
As all enterprise applications are using some external dependencies (such as databases, message brokers, etc.), it is important to have a way to test the application with these dependencies.
In this repository, we will see the key benefits of using TestContainers versus Mockito and H2 database.
We will compare both approaches and see the pros and cons of each one.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites

* Java JDK 21
* Docker

### Installing

* Clone repo
* Import the project in any IDE
* Build the validator service with maven ( mvn clean package )
* Build the docker image for the validator service ( docker build -t gpavalidator . )

### Use cases and tests

This application is built around a simple use case: receiving a message from a Kafka topic, validating it against an external API and saving it to the database.
There are two different tests for this use case:
* One using TestContainers to start a PostgreSQL database and a Kafka broker
* One using Mockito to mock the Validation Service call and H2 Database

The architecture can be illustrated as the following :
![Arch - Copy - Copy](https://github.com/user-attachments/assets/345f7ed8-9aea-4816-afc0-e641c49dd0ac)
 
Let’s see how much coverage they provide.
We will use the following colors to identify what is being mocked and what isn’t.
RED → Mocked
BLUE → Real Integration

With TestContainers 
![TestContainers](https://github.com/user-attachments/assets/0d682585-b763-49ad-9ac3-f7685c7ee5dc)

With Mockito
![Mockito](https://github.com/user-attachments/assets/04a4d076-78a7-45af-9096-2d4dbf3767e1)




## Built With

* [Spring Boot](https://spring.io/projects/spring-boot)
* [Maven](https://maven.apache.org/) - Dependency Management
* [Docker](https://www.docker.com/)
* [TestContainers](https://testcontainers.com/)
* [Mockito](https://site.mockito.org/)
* [H2 Database](https://www.h2database.com)
* [PostgreSQL Database](https://www.postgresql.org/)
* [Kafka](https://kafka.apache.org/)
