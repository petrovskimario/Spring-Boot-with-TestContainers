# Spring Boot and TestContainers integration

TestContainers is a powerful technology that enables the developers to test the entire end-to-end flow in their unit test cases. TestContainers is built on top of Docker and it is a technology that can create Docker Containers when the tests are started and destroy the containers once the tests have finished. For some common images there are already pre-configured test containers. It can run and manage containers from any docker image.

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
RED → Mocked <br/>
BLUE → Real Integration


#### With TestContainers <br/>
![TestContainers](https://github.com/user-attachments/assets/0d682585-b763-49ad-9ac3-f7685c7ee5dc)

Here we can identify 3 external dependencies for which we will use TestContainers to spin containers. <br/>
• Kafka <br/>
• Validation Service <br/>
• PostgreSQL Databases 

#### With Mockito <br/>
![Mockito](https://github.com/user-attachments/assets/04a4d076-78a7-45af-9096-2d4dbf3767e1)

Here we can see the following problems : <br/>
• We cannot produce/consume messages <br/>
• We cannot call the Validation Service – options are either Mockito or mock HTTP server ( such as WireMock) <br/>
• Database will be H2 instead of PostgreSQL ( This is crucial if the application is using native database specific queries ) <br/>



## Built With

* [Spring Boot](https://spring.io/projects/spring-boot)
* [Maven](https://maven.apache.org/) - Dependency Management
* [Docker](https://www.docker.com/)
* [TestContainers](https://testcontainers.com/)
* [Mockito](https://site.mockito.org/)
* [H2 Database](https://www.h2database.com)
* [PostgreSQL Database](https://www.postgresql.org/)
* [Kafka](https://kafka.apache.org/)
