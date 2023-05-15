package uk.ac.leedsbeckett.booksesc;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.ToString;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import uk.ac.leedsbeckett.booksesc.model.*;
import uk.ac.leedsbeckett.booksesc.service.StudentService;
import uk.ac.leedsbeckett.booksesc.service.TransactionService;

import java.time.LocalDate;
import java.time.Month;
import java.util.Set;

import static org.hibernate.internal.util.collections.CollectionHelper.setOf;

@Configuration
public class MiscellaneousBeans {

    @Bean
    CommandLineRunner initDatabase(BookRepository bookRepository, StudentService studentService, StudentRepository studentRepository, TransactionRepository transactionRepository, TransactionService transactionService) {
        return args -> {

//            Book book1 = new Book();
//            book1.setAuthor("Bach");
//            book1.setCopies(3);
//            book1.setYear(2004);
//            book1.setTitle("Toccata and Fugue in D minor");
//            book1.setBookISBN(7960985411889L);
//
//            Book book2 = new Book();
//            book2.setAuthor("Beethoven");
//            book2.setCopies(1);
//            book2.setYear(2006);
//            book2.setTitle("Symphony No. 5 in C minor");
//            book2.setBookISBN(7886369756903L);
//
//            Book book3 = new Book();
//            book3.setAuthor("Mozart");
//            book3.setCopies(4);
//            book3.setYear(2011);
//            book3.setTitle("Eine kleine Nachtmusik");
//            book3.setBookISBN(1737002852639L);
//
//            Book book4 = new Book();
//            book4.setAuthor("Shakespeare");
//            book4.setCopies(0);
//            book4.setYear(2000);
//            book4.setTitle("Macbeth");
//            book4.setBookISBN(2904067498776L);
//
//            Book book5 = new Book();
//            book5.setAuthor("Chopin");
//            book5.setCopies(5);
//            book5.setYear(1998);
//            book5.setTitle("Nocturne in E-flat Major, Op. 9, No. 2");
//            book5.setBookISBN(8777004413787L);
//
//            Student student1 = new Student();
//            student1.setStudentId("C9999999");
//            student1.setAdmin(false);
//            student1.setPassword("password");
//
//            Student student2 = new Student();
//            student2.setStudentId("C1111111");
//            student2.setAdmin(true);
//            student2.setPassword("password");
//
//            studentRepository.saveAll(setOf(student1, student2));
//            bookRepository.saveAll(setOf(book1, book2, book3, book4, book5));



        };
    }

    @Bean //Creates a resttemplate.
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }
}
