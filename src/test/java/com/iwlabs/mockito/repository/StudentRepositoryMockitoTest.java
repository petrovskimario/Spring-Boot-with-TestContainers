package com.iwlabs.mockito.repository;

import com.iwlabs.testcontainers.dto.StudentWithRankProjection;
import com.iwlabs.testcontainers.repository.StudentRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
