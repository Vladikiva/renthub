package at.htlstp.aslan.houserent.controller.rest;

import at.htlstp.aslan.houserent.model.Customer;
import at.htlstp.aslan.houserent.model.House;
import at.htlstp.aslan.houserent.model.Rental;
import at.htlstp.aslan.houserent.service.CustomerService;
import at.htlstp.aslan.houserent.service.HouseService;
import at.htlstp.aslan.houserent.service.RentalService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("admin")
public class AdminController {

    private final HouseService houseService;
    private final CustomerService customerService;
    private final RentalService rentalService;

    public AdminController(HouseService houseService, CustomerService customerService, RentalService rentalService) {
        this.houseService = houseService;
        this.customerService = customerService;
        this.rentalService = rentalService;
    }

    @GetMapping("running-rentals")
    public List<Rental> findRunningRentals() {
        return rentalService.findRunningRentals();
    }

    @GetMapping("houses")
    public List<House> findHouses() {
        return houseService.findAll();
    }

    @GetMapping("customers")
    public List<Customer> findCustomers() {
        return customerService.findAll();
    }

    @PostMapping("create-house")
    public ResponseEntity<House> createHouse(@RequestBody @Valid House house) {
        House created = houseService.create(house);
        return ResponseEntity.created(URI.create("/admin/houses/" + created.getHouseNr()))
                .body(created);
    }

    @PostMapping("create-customer")
    public ResponseEntity<Customer> createCustomer(@RequestBody @Valid Customer customer) {
        Customer created = customerService.create(customer);
        return ResponseEntity.created(URI.create("/admin/customers/" + created.getCustomerNumber()))
                .body(created);
    }

    @DeleteMapping("delete-house/{houseNr}")
    public ResponseEntity<Void> deleteHouse(@PathVariable String houseNr) {
        houseService.deleteById(houseNr);
        return ResponseEntity.noContent().build();
    }
}
