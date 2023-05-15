package uk.ac.leedsbeckett.booksesc.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDate;

@Entity
@Data
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // The unique identifier of the transaction.

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Student student; // The student associated with the transaction.

    private Long ISBN; // The ISBN of the book involved in the transaction.

    private LocalDate DateBorrowed; // The date when the book was borrowed.

    private LocalDate dateDue; // The due date for returning the book.

    private LocalDate dateReturned; // The date when the book was returned.

    private int daysOverdue; // The number of days the book is overdue.
}