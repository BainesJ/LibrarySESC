package uk.ac.leedsbeckett.booksesc.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uk.ac.leedsbeckett.booksesc.model.Student;
import uk.ac.leedsbeckett.booksesc.model.StudentRepository;
import uk.ac.leedsbeckett.booksesc.model.Transaction;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class StudentService { //Student service class to handle business logic for student objects.

    private Student student; //Reference to the current student object. Primitive form of session management.
    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) { //Dependency injection.
        this.studentRepository = studentRepository;
    }

    /**
     * Setter method for the current user session.
     * @param stud the student to be set as current, upon login.
     */
    public void setCurrentUser(Student stud) {
        this.student = stud;
    }

    /**
     * Clear method for the current user session
     */
    public void clearCurrentUser() {
        this.student = null;
    }

    /**
     * Gets a list of every student object within the database.
     * @return A list student objects.
     */
    public List < Student > getAllStudents() {
        return studentRepository.findAll();
    }

    /**
     * Getter method for current user session.
     * @return Current student object.
     */
    public Student getCurrentUser() {
        return this.student;
    }

    /**
     * Checks whether a student exists by a given student ID.
     * @param studentId A student ID to check for student existence.
     * @return True if student exists, false otherwise.
     */
    public Boolean studentExistsByStudentId(String studentId) {
        return studentRepository.existsStudentByStudentId(studentId);
    }

    /**
     * Gets a student from their student ID.
     * @param StudentId A student ID to get student object.
     * @return The student object.
     */
    public Student getStudentByStudentId(String StudentId) {
        return studentRepository.findStudentByStudentId(StudentId);
    }

    /**
     * Checks whether a student exists with a student ID and password, allowing successful login.
     * @param studentId The ID of the student.
     * @param password The password of the student.
     * @return A true or false bool dependent on the existence of the student.
     */
    public Boolean studentExistsByStudentIdAndPassword(String studentId, String password) {
        return studentRepository.existsStudentByStudentIdAndPassword(studentId, password);
    }

    /**
     * Saves a student object in the database.
     * @param student Student to be saved.
     */
    public void saveStudent(Student student) {
        System.out.println("saveStudent " + student);
        studentRepository.save(student);
    }

    /**
     * Gets a list of all students, and the anumber of loaned/overdue books they have.
     * @return The aforementioned list.
     */
    public List < String[] > getLoanedInformation() {
        List < String[] > ret = new ArrayList < String[] > ();
        for (Student s: getAllStudents()) {
            String sid = s.getStudentId();
            String loan = Integer.toString(getLoanedNum(s));
            String overdue = Integer.toString(getOverdueNum(s));
            ret.add(new String[] {
                    sid,
                    loan,
                    overdue
            });
        }
        return ret;
    }

    /**
     * Gets the number of loaned books for a given student object.
     * @param student The student object to check for loans.
     * @return The number of loans.
     */
    public int getLoanedNum(Student student) {
        int loans = 0;
        Set <Transaction> loaned = student.getTransactionStudentList();
        for (Transaction t: loaned) {
            if (t.getDateDue().isAfter(LocalDate.now()) && t.getDateReturned() == null) {
                loans++;
                //Incrementing the number of loans each time an open transaction that is not overdue is found.
            }
        }
        return loans;
    }

    /**
     * Gets the number of overdue books for a given student object.
     * @param student The student object to check for overdue books.
     * @return The number of overdue books.
     */
    public int getOverdueNum(Student student) {
        int overdue = 0;
        Set < Transaction > trans = student.getTransactionStudentList();
        for (Transaction t: trans) {
            if (t.getDateDue().isBefore(LocalDate.now()) && t.getDateReturned() == null) {
                overdue++;
                //Incrementing the number of loans each time an open transaction that is overdue is found.
            }
        }
        return overdue;
    }

    /**
     * Registers a student in the library service.
     * @param studentId The ID of the student to register.
     * @param password The password of the student to register.
     */
    public void registerStudent(String studentId, String password) {
        if (studentId == null || password == null) {
            throw new RuntimeException("Error: Missing a username or password.");
        } else {
            Student regStudent = new Student();
            regStudent.setStudentId(studentId);
            regStudent.setPassword(password);
            studentRepository.save(regStudent);
            //Facilitates login with the details created in the student portal.
        }
    }
}