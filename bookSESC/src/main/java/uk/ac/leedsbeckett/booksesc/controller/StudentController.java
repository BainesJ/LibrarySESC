package uk.ac.leedsbeckett.booksesc.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import uk.ac.leedsbeckett.booksesc.model.Student;
import uk.ac.leedsbeckett.booksesc.service.StudentService;
import uk.ac.leedsbeckett.booksesc.service.TransactionService;

@RestController
public class StudentController {

    private final StudentService studentService;
    private final TransactionService transactionService;

    public StudentController(StudentService studentService, TransactionService transactionService) {
        this.studentService = studentService;

        this.transactionService = transactionService; //Construction of student controller, demonstrating DI and MVC pattern.
    }

    /**
     * GET mapping for the login page, allowing users to enter their credentials into a field.
     * @return the login modelAndView, with a student object to fill and POST.
     */
    @GetMapping(value = "/login")
    public ModelAndView logIn() {
        ModelAndView modelAndView = new ModelAndView("login");
        modelAndView.addObject("student", new Student());
        return modelAndView;
    }

    /**
     * POST mapping for the login page, taking the user input and redirecting them to the required endpoints.
     * @param student Student object with user details to be checked against the system.
     * @return ModelAndView of necessary page, depending on admin status and credential authenticity.
     */
    @PostMapping(value = "/login")
    public ModelAndView checkLogin(Student student) {
        if (studentService.studentExistsByStudentId(student.getStudentId())) {
            if (studentService.studentExistsByStudentIdAndPassword(student.getStudentId(), student.getPassword())) {
                Student stud = studentService.getStudentByStudentId(student.getStudentId());
                studentService.setCurrentUser(stud);
                if (stud.getAdmin() == true) {
                    return new ModelAndView("admin-allbooks");
                } else return new ModelAndView("redirect:/books");
            } else {
                return new ModelAndView("login").addObject("message", "Login Failed! Incorrect password.");
            }
        } else
            return new ModelAndView("login").addObject("message", "Login failed. No user exists under that student ID. Please check spelling.");
    }

    /**
     * GET mapping for logout page.
     * @return A redirect to the login page.
     */
    @GetMapping("/logout")
    public ModelAndView logOut() {
        //transactionService.alterTransactionTest(); //Test for demonstrative purposes.
        studentService.clearCurrentUser();
        //Resetting current user session.
        return new ModelAndView("redirect:/login");
    }

    /**
     * POST mapping for an API endpoint, allowing other microservices to register students in this microservice.
     * @param studentId Student ID of the student to register.
     * @param password Password of the student to register.
     * @return ResponseEntity validation of whether the use was successfully registered or not.
     */
    @PostMapping(value = "/api/register")
    public ResponseEntity < String > register(@RequestParam String studentId, @RequestParam String password) {
        System.out.println(studentId);
        if (studentService.studentExistsByStudentId(studentId)) {
            return ResponseEntity.badRequest()
                    .body("Account " + studentId + " creation failed. Account already exists.");
        } else {
            studentService.registerStudent(studentId, password);
            return ResponseEntity.ok("Registration of user " + studentId + "!");
        }
    }

    /**
     * GET mapping for a list of all loaned books for the current student.
     * @return ModelAndView of the student's loaned books page.
     */
    @GetMapping(value = "/mybooks")
    public ModelAndView myCourses() {
        Student student = studentService.getCurrentUser();
        if (student.getBooksBorrowed().isEmpty()) {
            return new ModelAndView("mybooks");
        }
        return new ModelAndView("mybooks").addObject("transactions", transactionService.getCurrentTransactions(student));
    }

    /**
     * GET mapping for a list of all past student transactions under the 'myaccount' page.
     * @return ModelAndView of the student account page, with all past transactions.
     */
    @GetMapping(value = "/myaccount")
    public ModelAndView myAccount() {
        Student student = studentService.getCurrentUser();
        return new ModelAndView("myaccount").addObject("transactions", transactionService.getPastTransactions(student));
    }

    /**
     * GET mapping for a list of all students for an admin to see. Contains numbers of loaned and overdue books per each student.
     * @return ModelAndView of a page containing all students, with loaned and overdue information.
     */
    @GetMapping(value = "/admin/students")
    public ModelAndView adminStudents() {

        return new ModelAndView("admin-students").addObject("students", studentService.getLoanedInformation());
    }

    /**
     * GET mapping for a page showing an admin the list of all current student loans.
     * @return The ModelAndView for a page of all current student loans.
     */
    @GetMapping(value = "/admin/current-loans")
    public ModelAndView adminCurrentLoans() {

        return new ModelAndView("admin-currentloans").addObject("loans", transactionService.getCurrentLoans());
    }

    /**
     * GET mapping for a page showing an admin the list of all overdue student loans.
     * @return The ModelAndView for a page of all overdue student loans.
     */
    @GetMapping(value = "/admin/overdue")
    public ModelAndView adminOverdue() {

        return new ModelAndView("admin-overdue").addObject("loans", transactionService.getOverdueLoans());
    }
}