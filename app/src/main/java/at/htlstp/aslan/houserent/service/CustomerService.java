package at.htlstp.aslan.houserent.service;

import at.htlstp.aslan.houserent.model.Customer;
import at.htlstp.aslan.houserent.repository.CustomerRepository;
import at.htlstp.aslan.houserent.util.MessagesBean;
import jakarta.persistence.EntityExistsException;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

    private final MessagesBean messages;
    private final CustomerRepository customerRepository;

    public CustomerService(MessagesBean messages, CustomerRepository customerRepository) {
        this.messages = messages;
        this.customerRepository = customerRepository;
    }

    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    /**
     * Saves the given customer.
     *
     * @throws EntityExistsException if the customer number is already taken
     */
    public Customer create(Customer customer) {
        if (customerRepository.existsById(customer.getCustomerNumber())) {
            throw new EntityExistsException(messages.get("customerAlreadyExists"));
        }
        return customerRepository.save(customer);
    }

    public boolean existsById(Integer id) {
        return customerRepository.existsById(id);
    }
}
