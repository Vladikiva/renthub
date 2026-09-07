package at.htlstp.aslan.houserent.repository;

import at.htlstp.aslan.houserent.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {}
