package uk.ac.leedsbeckett.booksesc.service;

import org.springframework.stereotype.Service;
import uk.ac.leedsbeckett.booksesc.model.Book;
import uk.ac.leedsbeckett.booksesc.model.BookRepository;
import uk.ac.leedsbeckett.booksesc.model.Student;

import java.util.ArrayList;
import java.util.List;

@Service
public class BookService {

    private BookRepository bookRepository;


    public BookService(BookRepository bookRepository) { //Dependency injection of the book repository.
        this.bookRepository = bookRepository;
    }

    /**
     * Gets all books from the book repository.
     * @return All book objects from the database.
     */
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    /**
     * Queries books against a search parameter.
     * @param query Search parameter to use.
     * @return Books containing the query.
     */
    public List<Book> queryBooks(String query) {
        List<Book> books = bookRepository.findAll();
        List<Book> matches = new ArrayList<>();
        for (Book c : books) {
            if (c.getBookISBN().toString().contains(query)) {
                matches.add(c);
            }
        }
        return matches;
    }

    /**
     * Saves a book object to the database.
     * @param book Book object to save.
     */
    public void saveBook(Book book)
    {
        bookRepository.save(book);
    }

    /**
     * Gets a book object from an ISBN.
     * @param ISBN ISBN of book to find.
     * @return Book object.
     */
    public Book getBookByISBN(Long ISBN){
        try {
            if (!bookRepository.existsBookByBookISBN(ISBN)){
                throw new RuntimeException("Invalid ISBN");
            } else {
                return bookRepository.findBookByBookISBN(ISBN);
            }
        } catch (Exception E) {
            throw new RuntimeException("There has been an issue with the ID search process. Contact site administrator.");
        }
    }
    
}
