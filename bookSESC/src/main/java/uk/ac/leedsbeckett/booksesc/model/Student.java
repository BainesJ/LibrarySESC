package uk.ac.leedsbeckett.booksesc.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id; // The unique identifier of the student.

    @Column(unique = true, nullable = false)
    private String studentId; // The student ID of the student.

    @Column(nullable = false)
    private String password; // The password of the student.

    @Column(nullable = false)
    private Boolean admin; // Indicates whether the student is an admin or not.

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "book_student",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "book_isbn"))
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    Set < Book > booksBorrowed; // The set of books borrowed by the student.

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(
            name = "student_transaction_list",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "transaction_id")
    )
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    Set < Transaction > transactionStudentList; // The set of transactions associated with the student.

    public void addTransaction(Transaction transaction) {
        if (transactionStudentList == null) {
            transactionStudentList = new HashSet < > ();
        }
        transactionStudentList.add(transaction);
    }

    public Student() {
        this.admin = false;
    }

    public void borrowBook(Book book) {
        if (booksBorrowed == null) {
            booksBorrowed = new HashSet < > ();
        }
        if (booksBorrowed.contains(book)) {
            throw new RuntimeException("Attempting to borrow. " + studentId + " = student id, " + book + "= book, already in the system");
        } else {
            booksBorrowed.add(book);
        }
    }

    public void returnBook(Book book) {
        if (!booksBorrowed.contains(book)) {
            throw new RuntimeException("Attempting to return. " + studentId + " = student id, " + book + "= book, not found in the system");
        } else {
            booksBorrowed.remove(book);
        }
    }
}