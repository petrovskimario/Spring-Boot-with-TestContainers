package com.iwlabs.testcontainers.repository;

import com.iwlabs.testcontainers.configuration.TestcontainersConfiguration;
import com.iwlabs.testcontainers.dto.StudentWithRankProjection;
import com.iwlabs.testcontainers.model.Student;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ContextConfiguration(classes = TestcontainersConfiguration.class)
@ActiveProfiles("testcontainers")
//@ActiveProfiles("h2")
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

    /*
        This test is used to demonstrate that the native query from StudentRepository which is using the
        RANK() function is not working the same in PostgreSQL and H2.
        It is correctly ranking the students but the order is different.
     */
    @Test
    public void testFindStudentsWithRankByGpa() {
        List<StudentWithRankProjection> rankedStudents = studentRepository.findStudentsWithRankByGpa();

        assertThat(rankedStudents).isNotNull();
        assertThat(rankedStudents.size()).isEqualTo(3);

        // In case you want to see the order of the students
//        rankedStudents.forEach(student -> {
//            System.out.println("Rank: " + student.getRank() + " GPA: " + student.getGpa());
//        });

        assertThat(rankedStudents.get(0).getRank()).isEqualTo(1);
        assertThat(rankedStudents.get(0).getGpa()).isEqualTo(3.8);

        assertThat(rankedStudents.get(1).getRank()).isEqualTo(2);
        assertThat(rankedStudents.get(1).getGpa()).isEqualTo(3.5);

        assertThat(rankedStudents.get(2).getRank()).isEqualTo(3);
        assertThat(rankedStudents.get(2).getGpa()).isEqualTo(3.2);
    }

}
