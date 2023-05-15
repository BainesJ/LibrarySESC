package uk.ac.leedsbeckett.booksesc.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.util.Date;
import java.util.Set;

@Entity
@Data
public class Book {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long id; // The unique identifier of the book.

    @Column(nullable = false, unique = true, name = "bookISBN")
    private Long bookISBN; // The ISBN (International Standard Book Number) of the book.

    @Column(nullable = false, name = "title")
    private String title; // The title of the book.

    @Column(nullable = false, name = "author")
    private String author; // The author of the book.

    @Column(nullable = false, name = "publication_year")
    private int year; // The year of publication of the book.

    @Column(nullable = false, name = "copies")
    private int copies; // The number of copies available for the book.

    @ManyToMany(mappedBy = "booksBorrowed")
    @JsonIgnore
    @ToString.Exclude
    Set<Student> studentsBorrowed; // The set of students who have borrowed this book.
}
