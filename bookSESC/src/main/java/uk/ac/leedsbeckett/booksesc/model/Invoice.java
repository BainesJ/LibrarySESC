package uk.ac.leedsbeckett.booksesc.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import java.time.LocalDate;

@Data
public class Invoice {
    private Long id; // The unique identifier of the invoice.
    private String reference; // The reference code of the invoice.
    private Double amount; // The amount of the invoice.
    private LocalDate due; // The due date of the invoice.
    private String type; // The type of the invoice.
    private String status; // The status of the invoice (e.g., paid, unpaid).
    private String studentId; // The ID of the associated student.
}
