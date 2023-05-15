package uk.ac.leedsbeckett.booksesc.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import uk.ac.leedsbeckett.booksesc.model.Book;
import uk.ac.leedsbeckett.booksesc.service.BookService;
import uk.ac.leedsbeckett.booksesc.service.TransactionService;

import java.util.List;

@RestController
public class BookController {

    private TransactionService transactionService;
    private BookService bookService;

    public BookController(BookService bookService, TransactionService transactionService) {
        this.bookService = bookService;
        this.transactionService = transactionService; //Construction of the book controller, demonstrating MVC and DI.
    }

    /**
     * GET mapping for the view all books page.
     *
     * @return ModelAndView of the books page, with all books on it.
     */
    @GetMapping("/books")
    public ModelAndView viewAllBooks() {
        List<Book> bookList = bookService.getAllBooks();
        return new ModelAndView("books").addObject("books", bookList);
    }

    /**
     * POST mapping for the view all books page.
     *
     * @param query Query from the search bar to subsearch the book collection with.
     * @return All book objects matching the query.
     */
    @PostMapping("/books")
    public ModelAndView searchBooksPost(@RequestParam("query") String query) {
        List<Book> matches = bookService.queryBooks(query);
        return new ModelAndView("books").addObject("books", matches);
    }

    /**
     * GET mapping for a given book, using ISBN as a path variable.
     *
     * @param ISBN The ISBN of the book that is being displayed.
     * @return ModelAndView of the book page, with all information on it.
     */
    @GetMapping("/books/{ISBN}")
    public ModelAndView viewBook(@PathVariable Long ISBN) {
        Book book = bookService.getBookByISBN(ISBN);
        ModelAndView modelAndView = new ModelAndView("book");
        return modelAndView.addObject("book", book).addObject("borrowed", transactionService.studentBorrowedBook(transactionService.getStudent(), book));
    }

    /**
     * POST mapping for a given book, if the borrow button is pressed.
     *
     * @param ISBN    The ISBN of the book to borrow.
     * @param message Success or failure of borrowing process.
     * @return ModelAndView page of the user's currently loaned books.
     */
    @PostMapping("/books/{ISBN}/borrow")
    public ModelAndView BorrowBook(@PathVariable Long ISBN, @RequestParam(name = "message", required = false) String message) {
        message = transactionService.lendStudentBook(bookService.getBookByISBN(ISBN));
        return new ModelAndView("mybooks").addObject("message", message);
    }

    /**
     * POST mapping for a given book, if the return button is pressed.
     *
     * @param ISBN    The ISBN of the book to return.
     * @param message Success or failure of returning process.
     * @return ModelAndView page of the user's currently loaned books.
     */
    @PostMapping("/books/{ISBN}/return")
    public ModelAndView ReturnBook(@PathVariable Long ISBN, @RequestParam(name = "message", required = false) String message) {
        message = transactionService.returnStudentBook(bookService.getBookByISBN(ISBN));
        return new ModelAndView("mybooks").addObject("message", message);
    }

    /**
     * GET mapping for administrative view page of all books.
     *
     * @return ModelAndView of admin all books page.
     */
    @GetMapping(value = "/admin/books")
    public ModelAndView adminBooks() {

        return new ModelAndView("admin-allbooks").addObject("books", bookService.getAllBooks());
    }

    /**
     * GET mapping for the admin add book page.
     *
     * @return ModelAndView of admin add book page.
     */
    @GetMapping(value = "/admin/add-title")
    public ModelAndView adminAddPage() {
        return new ModelAndView("admin-addtitle").addObject("book", new Book());
    }

    /**
     * POST mapping for the admin add book page.
     *
     * @param book book object to be filled and added to the database.
     * @return A new book to be saved in the database.
     */
    @PostMapping(value = "/admin/add-title")
    public ModelAndView adminAddTitle(Book book) {
        String message;
        try {
            bookService.saveBook(book);
            message = "Successfully created new book " + book.getTitle() + "."; //Try catch in case the book exists, will implement validation in the future.
            return new ModelAndView("admin-allbooks").addObject("message", message);
        } catch (Exception e) {
            throw new RuntimeException("Unable to create book.");
        }
    }
}