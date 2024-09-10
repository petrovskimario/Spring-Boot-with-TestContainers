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
There are two different kinds of tests for this use case:
* One using TestContainers to start a PostgreSQL database and a Kafka broker
* One using Mockito to mock the Validation Service call and H2 Database

The architecture can be illustrated as the following :
![Arch - Copy - Copy](https://github.com/user-attachments/assets/345f7ed8-9aea-4816-afc0-e641c49dd0ac)
 
Let’s see how much coverage they provide.
We will use the following colors to identify what is being mocked and what isn’t. <br/>
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
• Database will be H2 instead of PostgreSQL ( This is crucial if the application is using native database specific queries, we will see this at the last example) <br/>

### Running the tests

There are multiple tests written in the project. Let's review them one by one.

#### TestContainers

1. First we have the TestcontainersConfiguration class which will spin a Kafka container and a PostgreSQL Container. <br/>
2. In the same configuration we will also add the Validation Service container. <br/>
The class is having the following code :
```   
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
```

That's it. Spring Boot will use auto configuration to connect our application to the Kafka and PostgreSQL containers.

2. Then we have the first test using TestContainers which is in the class KafkaConsumerServiceTest that will also spin up a custom docker container from our validation service : <br/>
```
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

        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            assertThat(studentRepository.count()).isEqualTo(0);
            assertThat(studentRepository.findByEmail(message.getEmail())).isEmpty();
        });
    }
```

This test assures that the student is not saved in the database since he has a low GPA.
Notice here that we are testing the Kafka Integration as well as the Validation Service Integration.

The next test in this class is with high GPA and it should be saved in the database.
```
    @Test
    public void testKafkaListenerWithHighGpa() throws InterruptedException {
        StudentDTO message = new StudentDTO();
        message.setFirstName("Mark");
        message.setLastName("Doe");
        message.setEmail("mark@example.com");
        message.setGpa(3.5);

        kafkaSenderService.sendStudentDTO(message);

        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            assertThat(studentRepository.count()).isEqualTo(1);
            assertThat(studentRepository.findByEmail(message.getEmail())).isNotEmpty();
        });
    }
```

3. The next test is a test using TestContainers to spin PostgreSQL instance and test a native query.
This test purpose is to demonstrate that the RANK() function is not working the same in H2 and PostgreSQL. <br/>
The test class is : StudentRepositoryTest <br/>
We have the following native query : 
```
    @Query(value = "SELECT s.id, s.first_name AS firstName, s.last_name AS lastName, s.email, s.gpa, " +
            "RANK() OVER (ORDER BY s.gpa DESC) AS rank " +
            "FROM students s", nativeQuery = true)
    List<StudentWithRankProjection> findStudentsWithRankByGpa();
```
We have the following test : 
```
class StudentRepositoryTest {

    @Autowired
    private StudentRepository studentRepository;

    @BeforeAll
    void setUp() {
        Student student1 = new Student();
        student1.setFirstName("John");
        student1.setLastName("Doe");
        student1.setEmail("john.doe@example.com");
        student1.setGpa(3.5);
        studentRepository.save(student1);

        Student student2 = new Student();
        student2.setFirstName("Jane");
        student2.setLastName("Doe");
        student2.setEmail("jane.doe@example.com");
        student2.setGpa(3.8);
        studentRepository.save(student2);

        Student student3 = new Student();
        student3.setFirstName("Jim");
        student3.setLastName("Beam");
        student3.setEmail("jim.beam@example.com");
        student3.setGpa(3.2);
        studentRepository.save(student3);
    }

    @Test
    public void testFindStudentsWithRankByGpa() {
        List<StudentWithRankProjection> rankedStudents = studentRepository.findStudentsWithRankByGpa();

        assertThat(rankedStudents).isNotNull();
        assertThat(rankedStudents.size()).isEqualTo(3);

        assertThat(rankedStudents.get(0).getRank()).isEqualTo(1);
        assertThat(rankedStudents.get(0).getGpa()).isEqualTo(3.8);

        assertThat(rankedStudents.get(1).getRank()).isEqualTo(2);
        assertThat(rankedStudents.get(1).getGpa()).isEqualTo(3.5);

        assertThat(rankedStudents.get(2).getRank()).isEqualTo(3);
        assertThat(rankedStudents.get(2).getGpa()).isEqualTo(3.2);
    }
```

This test passes when using TestContainers but fails when using H2. Try with changing the profile to h2 and comment out the ContextConfiguration.

### Mockito

With mockito we have the same tests but we are mocking the Validation Service and the Kafka Service.
Let's review the tests.

1. The first test is in the mockito package called KafkaConsumerServiceTest
The code is the following : 
```
public class KafkaConsumerServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private StudentMapper studentMapper;

    @Mock
    private ValidateGpaHttpService validateGpaHttpService;

    @InjectMocks
    private KafkaConsumerService kafkaConsumerService;

    @Test
    public void testListenWithHighGpa() {
        StudentDTO message = new StudentDTO(1L,"Jane", "Doe", "jane.doe@example.com", 3.5);
        StudentValidationInput input = new StudentValidationInput(message.getFirstName(), message.getLastName(), message.getGpa());
        Student student = new Student();

        when(validateGpaHttpService.validateGpa(input)).thenReturn(true);
        when(studentMapper.studentDTOToStudent(message)).thenReturn(student);

        kafkaConsumerService.listen(message);

        verify(studentRepository, times(1)).save(student);
    }
```

We can see here that we are mocking the call to Validation Service so whatever the student GPA is, he will always be saved in database since the actual call to Validation Service is never made. <br/>
Same can be seen for the test with low GPA
```
    @Test
    public void testListenWithLowGpa() {
        StudentDTO message = new StudentDTO(1L,"Jane", "Doe", "jane.doe@example.com", 1.5);
        StudentValidationInput input = new StudentValidationInput(message.getFirstName(), message.getLastName(), message.getGpa());

        when(validateGpaHttpService.validateGpa(input)).thenReturn(false);

        kafkaConsumerService.listen(message);

        verify(studentRepository, never()).save(any(Student.class));
    }
```

Be careful when to use when() method!
Also here we are not actually listening to any Kafka topic, we are just calling the method directly.

2. The next test is for the native query for the StudentRepository which is in the StudentRepositoryMockitoTest class
This test is the same as the one with TestContainers/H2 but the difference is that we are using Mockito.
The test code is the following :
```
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class StudentRepositoryMockitoTest {
    @Mock
    private StudentRepository studentRepository;

    private List<StudentWithRankProjection> mockRankedStudents;

    @BeforeAll
    void setUp() {
        StudentWithRankProjection student1 = mock(StudentWithRankProjection.class);
        when(student1.getRank()).thenReturn(1);
        when(student1.getGpa()).thenReturn(3.8);

        StudentWithRankProjection student2 = mock(StudentWithRankProjection.class);
        when(student2.getRank()).thenReturn(2);
        when(student2.getGpa()).thenReturn(3.5);

        StudentWithRankProjection student3 = mock(StudentWithRankProjection.class);
        when(student3.getRank()).thenReturn(3);
        when(student3.getGpa()).thenReturn(3.2);

        mockRankedStudents = Arrays.asList(student1, student2, student3);
    }

    @Test
    public void testFindStudentsWithRankByGpa() {
        when(studentRepository.findStudentsWithRankByGpa()).thenReturn(mockRankedStudents);

        List<StudentWithRankProjection> rankedStudents = studentRepository.findStudentsWithRankByGpa();

        assertThat(rankedStudents).isNotNull();
        assertThat(rankedStudents.size()).isEqualTo(3);

        assertThat(rankedStudents.get(0).getRank()).isEqualTo(1);
        assertThat(rankedStudents.get(0).getGpa()).isEqualTo(3.8);

        assertThat(rankedStudents.get(1).getRank()).isEqualTo(2);
        assertThat(rankedStudents.get(1).getGpa()).isEqualTo(3.5);

        assertThat(rankedStudents.get(2).getRank()).isEqualTo(3);
        assertThat(rankedStudents.get(2).getGpa()).isEqualTo(3.2);
    }
}
```

This test doesn't actually test anything. Since the query is mocked, even if the query is invalid SQL this query will pass. <br/>

## Key Benefits of Using TestContainers
1. **Realistic Testing Environment:** TestContainers allows us to test with the same dependencies we use in production, reducing the mismatches between testing and live environments.
2. **Comprehensive Coverage:** It provides end-to-end testing capabilities, ensuring that all components of the system interact correctly.
3. **Ease of Setup:** With pre-configured containers for common services, setting up and tearing down environments becomes effortless.

## Limitations of Mockito + H2 Approach
1. **Limited Integration Testing:** Mocking services means we cannot fully test the integration points, potentially leading to missed issues.
2. **Different Database Behavior:** Using H2 instead of PostgreSQL can lead to differences in query behavior and missed database-specific issues.
3. **Maintenance Overhead:** Managing mocks and maintaining their accuracy as the application evolves can become a headache.

Always remember, with great power comes great responsibility! <br/>

Happy Coding!


## Built With

* [Spring Boot](https://spring.io/projects/spring-boot)
* [Maven](https://maven.apache.org/) - Dependency Management
* [Docker](https://www.docker.com/)
* [TestContainers](https://testcontainers.com/)
* [Mockito](https://site.mockito.org/)
* [H2 Database](https://www.h2database.com)
* [PostgreSQL Database](https://www.postgresql.org/)
* [Kafka](https://kafka.apache.org/)
