package com.tester.classicmodel.controller;

import com.tester.classicmodel.dto.CustomerDto;
import com.tester.classicmodel.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    /**
     * Get all customers
     * @return List of all customers
     */
    @GetMapping
    public ResponseEntity<List<CustomerDto>> getAllCustomers() {
        try {
            List<CustomerDto> customers = customerService.getAllCustomers();
            return ResponseEntity.ok(customers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get customer by ID
     * @param customerNumber Customer ID
     * @return Customer if found, 404 if not found
     */
    @GetMapping("/{customerNumber}")
    public ResponseEntity<CustomerDto> getCustomerById(@PathVariable Integer customerNumber) {
        try {
            CustomerDto customer = customerService.getCustomerById(customerNumber);
            return ResponseEntity.ok(customer);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Create a new customer
     * @param customerDto Customer data
     * @return Created customer with 201 status
     */
    @PostMapping
    public ResponseEntity<CustomerDto> createCustomer(@RequestBody @Valid CustomerDto customerDto) {
        try {
            CustomerDto createdCustomer = customerService.createCustomer(customerDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdCustomer);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            if (e.getMessage().contains("constraint violation")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Update an existing customer
     * @param customerNumber Customer ID
     * @param customerDto Updated customer data
     * @return Updated customer with 200 status
     */
    @PutMapping("/{customerNumber}")
    public ResponseEntity<CustomerDto> updateCustomer(@PathVariable Integer customerNumber, 
                                                     @RequestBody @Valid CustomerDto customerDto) {
        try {
            CustomerDto updatedCustomer = customerService.updateCustomer(customerNumber, customerDto);
            return ResponseEntity.ok(updatedCustomer);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            if (e.getMessage().contains("constraint violation")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Delete a customer
     * @param customerNumber Customer ID
     * @return 204 No Content if successful
     */
    @DeleteMapping("/{customerNumber}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Integer customerNumber) {
        try {
            customerService.deleteCustomer(customerNumber);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            if (e.getMessage().contains("existing references")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}