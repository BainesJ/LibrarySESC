package uk.ac.leedsbeckett.booksesc.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long>{
    Student findStudentByStudentId(String studentId);
    Boolean existsStudentByStudentId(String studentId);
    Boolean existsStudentByStudentIdAndPassword(String studentId, String password);
}