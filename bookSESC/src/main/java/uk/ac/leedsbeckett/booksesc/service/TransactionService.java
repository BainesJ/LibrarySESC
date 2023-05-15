package uk.ac.leedsbeckett.booksesc.service;

import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAttributeSourceAdvisor;
import uk.ac.leedsbeckett.booksesc.model.*;

import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class TransactionService {

    private final StudentService studentService;
    private final BookService bookService;
    private final IntegrationService integrationService;
    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository, StudentService studentService, BookService bookService, IntegrationService integrationService) {
        this.transactionRepository = transactionRepository;
        this.studentService = studentService;
        this.bookService = bookService;
        this.integrationService = integrationService; //Dependency injection of all necessary components for the transaction service.
    }

    /**
     * Lends a book to a student, creating a transaction.
     * @param book Book to be lent to the student.
     * @return String value containing a message on success or failure of lending.
     */
    public String lendStudentBook(Book book) {
        Student student = getStudent();
        if (studentBorrowedBook(student, book)) {
            return "Student " + getStudent().getStudentId() + " has already borrowed " + book.getTitle() + ". Please return before borrowing again.";
        } else if (book.getCopies() > 0) {
            student.borrowBook(book);
            System.out.println("Adding " + book + "to student. Unsaved.");
            Transaction transaction = createTransaction(student, book);
            System.out.println("Transaction:" + transaction);
            book.setCopies(book.getCopies() - 1);
            System.out.println("Saving " + book);
            bookService.saveBook(book);
            System.out.println("Saving " + student);
            studentService.saveStudent(student);
            return "Student " + student.getStudentId() + " borrowed " + book.getTitle() + ". Due date is " + transaction.getDateDue() + ".";
        } else return "No copies currently available for " + book.getTitle() + ".";
    }

    /**
     * Returns a book for a student. Creates and posts invoice, informing user, if overdue.
     * @param book Book for the student to return.
     * @return String message informing the user on whether return was successful or failed.
     */
    public String returnStudentBook(Book book) {
        Student student = getStudent();
        System.out.println(book);
        System.out.println(student.getStudentId());
        Transaction transaction = getCurrentTransaction(student, book); //Gets the current transaction to handle.
        if (!studentBorrowedBook(student, book)) { //Ensures the student has borrowed the book before adding a copy into database.
            return "Student " + getStudent().getStudentId() + " has not borrowed " + book.getTitle() + ". You cannot return unborrowed books.";
        } else

            student.returnBook(book);
        transaction.setDateReturned(LocalDate.now());
        if ((int) ChronoUnit.DAYS.between(transaction.getDateDue(), transaction.getDateReturned()) > 0) {
            transaction.setDaysOverdue((int) ChronoUnit.DAYS.between(transaction.getDateDue(), transaction.getDateReturned()));
        } else {
            transaction.setDaysOverdue(0);
        } //Checks whether the book is overdue, before creating an invoice if it is.
        book.setCopies(book.getCopies() + 1);
        transactionRepository.save(transaction);
        studentService.saveStudent(student);
        bookService.saveBook(book);
        if (transaction.getDaysOverdue() > 0) //Informs user whether they have been charged or not.
        {
            Invoice invoice = createBookInvoice(student, transaction);
            return "Student " + getStudent().getStudentId() + " returned " + book.getTitle() + ". You have been charged " + invoice.getAmount() + ", reference " + invoice.getReference();
        }
        return "Student " + getStudent().getStudentId() + " returned " + book.getTitle() + ". You have not been charged.";
    }

    /**
     * Checks whether a student has borrowed a book.
     * @param student Student to check.
     * @param book Book to check.
     * @return True or false on whether the student has a current loan of the book.
     */
    public boolean studentBorrowedBook(Student student, Book book) {
        Set < Book > borrowedBooks = student.getBooksBorrowed();
        for (Book b: borrowedBooks) {
            if (b.getBookISBN().equals(book.getBookISBN())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the current student object.
     * @return Current student object.
     */
    public Student getStudent() {
        return studentService.getCurrentUser();
    }

    /**
     * Creates a transaction when a book is loaned.
     * @param student Student associated with the transaction.
     * @param book Book loaned in the transaction.
     * @return A transaction object for the student and book.
     */
    public Transaction createTransaction(Student student, Book book) {
        Transaction transaction = new Transaction();
        transaction.setStudent(student);
        transaction.setDateBorrowed(LocalDate.now());
        transaction.setDateDue(LocalDate.now().plusWeeks(2));
        transaction.setISBN(book.getBookISBN());
        transactionRepository.save(transaction);
        student.addTransaction(transaction);
        return transaction;
    }

    /**
     * Code for demonstrating an overdue return. H2 database is hard to alter manually, so this method was made.
     */
    public void alterTransactionTest() {
        try {
            Transaction transactionToAlter = getCurrentTransaction(studentService.getStudentByStudentId("C9999999"), bookService.getBookByISBN(1737002852639L));
            transactionToAlter.setDateBorrowed(LocalDate.of(2020, Month.JANUARY, 8));
            transactionToAlter.setDateDue(LocalDate.of(2020, Month.JANUARY, 22));
            System.out.println("Altered. " + transactionToAlter.getDateBorrowed());
            transactionRepository.save(transactionToAlter);
        } catch (Exception e) {};
    }

    /**
     * Gets a list of all past transactions of a student.
     * @param student Student to receive past transactions of.
     * @return A list of past transactions.
     */
    public List < Transaction > getPastTransactions(Student student) {
        List < Transaction > tranList = new ArrayList < > ();
        for (Transaction t: student.getTransactionStudentList()) {
            if (t.getDateReturned() != null) //If the book has already been returned, and the transaction is in the past.
            {
                tranList.add(t);
            }
        }
        return tranList;
    }

    /**
     * Gets the current transaction of a book for a student.
     * @param student Student to receive current transaction of.
     * @param book Book to receive current transaction for.
     * @return The transaction for a student and a book.
     */
    public Transaction getCurrentTransaction(Student student, Book book) {
        for (Transaction t: student.getTransactionStudentList()) {
            if (t.getISBN().equals(book.getBookISBN()) && t.getDateReturned() == null) { //If the book has not been returned, and the book is valid.
                return t;
            }
        }
        throw new RuntimeException("No current transaction found for given student and book.");
    }

    /**
     * Gets a list of all current transactions of a student.
     * @param student Student to receive current transactions of.
     * @return A list of current transactions.
     */
    public List < Transaction > getCurrentTransactions(Student student) {
        List < Transaction > tranList = new ArrayList < > ();
        for (Transaction t: student.getTransactionStudentList()) {
            if (t.getDateReturned() == null) {
                tranList.add(t);
            }
        }
        return tranList;
    }

    /**
     * Creates an invoice to send to the finance service.
     * @param student The student to be given an invoice.
     * @param transaction The transaction object containing transaction information.
     * @return Information regarding the created invoice.
     */
    public Invoice createBookInvoice(Student student, Transaction transaction) {
        double fee = (Math.ceil(transaction.getDaysOverdue() / 7.0)) * 5;
        Invoice invoice = new Invoice();
        invoice.setAmount(fee);
        invoice.setDue(transaction.getDateDue());
        invoice.setType("BOOK_FEE");
        invoice.setStudentId(student.getStudentId());
        return integrationService.createBookInvoice(invoice);
    }

    /**
     * A list of all current loans, for admin view.
     * @return An array list of the ISBN, title, student ID and date borrowed of all current loans.
     */
    public List < String[] > getCurrentLoans() {
        List < String[] > ret = new ArrayList < String[] > ();
        for (Transaction t: transactionRepository.findAll()) {
            if (t.getDateReturned() == null && t.getDateDue().isAfter(LocalDate.now())) { //If not returned, and not overdue.
                ret.add(new String[] {
                        t.getISBN().toString(),
                        bookService.getBookByISBN(t.getISBN()).getTitle(),
                        t.getStudent().getStudentId(),
                        t.getDateBorrowed().toString()
                });
            }
        }
        return ret;
    }

    /**
     * A list of all overdue loans, for admin view.
     * @return An array list of the ISBN, title, student ID and date borrowed of all overdue loans.
     */
    public List < String[] > getOverdueLoans() {
        List < String[] > ret = new ArrayList < > ();
        for (Transaction t: transactionRepository.findAll()) {
            if (t.getDateReturned() == null && t.getDateDue().isBefore(LocalDate.now())) { //If not returned, and overdue.
                ret.add(new String[] //Storing all information required in an arraylist, to pass to the html page for use with thymeleaf.
                        {
                                t.getISBN().toString(),
                                bookService.getBookByISBN(t.getISBN()).getTitle(),
                                t.getStudent().getStudentId(),
                                t.getDateBorrowed().toString()
                        });
            }
        }
        return ret;
    }

}