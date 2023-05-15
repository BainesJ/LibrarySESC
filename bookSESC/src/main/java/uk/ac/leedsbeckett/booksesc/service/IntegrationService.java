package uk.ac.leedsbeckett.booksesc.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uk.ac.leedsbeckett.booksesc.model.Invoice;

@Service
public class IntegrationService {

    private final RestTemplate restTemplate; //Rest template for sending and receiving RESTful requests.

    public IntegrationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    } //Injecting and instantiating the rest template class.

    /**
     * POSTs an invoice to the finance server, creating an invoice in their database for the associated student ID.
     * @param invoice Invoice to pass to the finance server.
     * @return Information around the created invoice.
     */
    public Invoice createBookInvoice(Invoice invoice) {
        System.out.println(invoice);
        return restTemplate.postForObject("http://localhost:5000/api/invoice/create_invoice", invoice, Invoice.class);
    }

}