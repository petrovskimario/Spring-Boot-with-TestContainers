package com.iwlabs.testcontainers.repository;

import com.iwlabs.testcontainers.dto.StudentWithRankProjection;
import com.iwlabs.testcontainers.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    @Query(value = "SELECT s.id, s.first_name AS firstName, s.last_name AS lastName, s.email, s.gpa, " +
            "RANK() OVER (ORDER BY s.gpa DESC) AS rank " +
            "FROM students s", nativeQuery = true)
    List<StudentWithRankProjection> findStudentsWithRankByGpa();

    Optional<Student> findByEmail(String email);

}
