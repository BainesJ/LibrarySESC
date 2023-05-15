package uk.ac.leedsbeckett.booksesc.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>{
    Book findBookByBookISBN(Long BookISBN);
    Boolean existsBookByBookISBN(Long ISBN);
}