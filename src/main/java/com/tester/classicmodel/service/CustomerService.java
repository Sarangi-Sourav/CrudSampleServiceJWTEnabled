package com.tester.classicmodel.service;

import com.tester.classicmodel.dto.CustomerDto;
import com.tester.classicmodel.exception.DatabaseException;
import com.tester.classicmodel.exception.ResourceNotFoundException;
import com.tester.classicmodel.model.Customer;
import com.tester.classicmodel.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    /**
     * Get all customers
     * @return List of CustomerDto
     */
    public List<CustomerDto> getAllCustomers() {
        try {
            List<Customer> customers = customerRepository.findAll();
            return customers.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new DatabaseException("Error retrieving customers: " + e.getMessage(), e);
        }
    }

    /**
     * Get customer by ID
     * @param customerNumber Customer ID
     * @return CustomerDto if found
     * @throws RuntimeException if customer not found
     */
    public CustomerDto getCustomerById(Integer customerNumber) {
        if (customerNumber == null) {
            throw new IllegalArgumentException("Customer number cannot be null");
        }

        try {
            Optional<Customer> customer = customerRepository.findById(customerNumber);
            return customer.map(this::convertToDto)
                    .orElseThrow(() -> new ResourceNotFoundException("Customer", customerNumber));
        } catch (Exception e) {
            if (e instanceof ResourceNotFoundException) {
                throw e;
            }
            throw new DatabaseException("Error retrieving customer with ID " + customerNumber + ": " + e.getMessage(), e);
        }
    }

    /**
     * Create a new customer
     * @param customerDto Customer data
     * @return Created CustomerDto
     * @throws IllegalArgumentException if validation fails
     * @throws RuntimeException if database constraint violation occurs
     */
    public CustomerDto createCustomer(CustomerDto customerDto) {
        if (customerDto == null) {
            throw new IllegalArgumentException("Customer data cannot be null");
        }

        // Validate required fields
        validateCustomerDto(customerDto);

        try {
            Customer customer = convertToEntity(customerDto);
            Customer savedCustomer = customerRepository.save(customer);
            return convertToDto(savedCustomer);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Database constraint violation: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new DatabaseException("Error creating customer: " + e.getMessage(), e);
        }
    }

    /**
     * Update an existing customer
     * @param customerNumber Customer ID
     * @param customerDto Updated customer data
     * @return Updated CustomerDto
     * @throws RuntimeException if customer not found or validation fails
     */
    public CustomerDto updateCustomer(Integer customerNumber, CustomerDto customerDto) {
        if (customerNumber == null) {
            throw new IllegalArgumentException("Customer number cannot be null");
        }
        if (customerDto == null) {
            throw new IllegalArgumentException("Customer data cannot be null");
        }

        // Validate required fields
        validateCustomerDto(customerDto);

        try {
            // Check if customer exists
            Optional<Customer> existingCustomer = customerRepository.findById(customerNumber);
            if (existingCustomer.isEmpty()) {
                throw new ResourceNotFoundException("Customer", customerNumber);
            }

            Customer customer = convertToEntity(customerDto);
            customer.setCustomerNumber(customerNumber);
            Customer updatedCustomer = customerRepository.update(customer);
            return convertToDto(updatedCustomer);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Database constraint violation: " + e.getMessage(), e);
        } catch (Exception e) {
            if (e instanceof ResourceNotFoundException) {
                throw e;
            }
            throw new DatabaseException("Error updating customer with ID " + customerNumber + ": " + e.getMessage(), e);
        }
    }

    /**
     * Delete a customer
     * @param customerNumber Customer ID
     * @throws RuntimeException if customer not found
     */
    public void deleteCustomer(Integer customerNumber) {
        if (customerNumber == null) {
            throw new IllegalArgumentException("Customer number cannot be null");
        }

        try {
            // Check if customer exists
            Optional<Customer> existingCustomer = customerRepository.findById(customerNumber);
            if (existingCustomer.isEmpty()) {
                throw new ResourceNotFoundException("Customer", customerNumber);
            }

            customerRepository.deleteById(customerNumber);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Cannot delete customer due to existing references: " + e.getMessage(), e);
        } catch (Exception e) {
            if (e instanceof ResourceNotFoundException) {
                throw e;
            }
            throw new DatabaseException("Error deleting customer with ID " + customerNumber + ": " + e.getMessage(), e);
        }
    }

    /**
     * Convert Customer entity to CustomerDto
     * @param customer Customer entity
     * @return CustomerDto
     */
    private CustomerDto convertToDto(Customer customer) {
        CustomerDto dto = new CustomerDto();
        dto.setCustomerNumber(customer.getCustomerNumber());
        dto.setCustomerName(customer.getCustomerName());
        dto.setContactLastName(customer.getContactLastName());
        dto.setContactFirstName(customer.getContactFirstName());
        dto.setPhone(customer.getPhone());
        dto.setAddressLine1(customer.getAddressLine1());
        dto.setAddressLine2(customer.getAddressLine2());
        dto.setCity(customer.getCity());
        dto.setState(customer.getState());
        dto.setPostalCode(customer.getPostalCode());
        dto.setCountry(customer.getCountry());
        dto.setSalesRepEmployeeNumber(customer.getSalesRepEmployeeNumber());
        dto.setCreditLimit(customer.getCreditLimit());
        return dto;
    }

    /**
     * Convert CustomerDto to Customer entity
     * @param dto CustomerDto
     * @return Customer entity
     */
    private Customer convertToEntity(CustomerDto dto) {
        Customer customer = new Customer();
        customer.setCustomerNumber(dto.getCustomerNumber());
        customer.setCustomerName(dto.getCustomerName());
        customer.setContactLastName(dto.getContactLastName());
        customer.setContactFirstName(dto.getContactFirstName());
        customer.setPhone(dto.getPhone());
        customer.setAddressLine1(dto.getAddressLine1());
        customer.setAddressLine2(dto.getAddressLine2());
        customer.setCity(dto.getCity());
        customer.setState(dto.getState());
        customer.setPostalCode(dto.getPostalCode());
        customer.setCountry(dto.getCountry());
        customer.setSalesRepEmployeeNumber(dto.getSalesRepEmployeeNumber());
        customer.setCreditLimit(dto.getCreditLimit());
        return customer;
    }

    /**
     * Validate CustomerDto required fields
     * @param customerDto CustomerDto to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateCustomerDto(CustomerDto customerDto) {
        if (customerDto.getCustomerName() == null || customerDto.getCustomerName().trim().isEmpty()) {
            throw new IllegalArgumentException("Customer name is required");
        }
        if (customerDto.getContactLastName() == null || customerDto.getContactLastName().trim().isEmpty()) {
            throw new IllegalArgumentException("Contact last name is required");
        }
        if (customerDto.getContactFirstName() == null || customerDto.getContactFirstName().trim().isEmpty()) {
            throw new IllegalArgumentException("Contact first name is required");
        }
        if (customerDto.getPhone() == null || customerDto.getPhone().trim().isEmpty()) {
            throw new IllegalArgumentException("Phone is required");
        }
        if (customerDto.getAddressLine1() == null || customerDto.getAddressLine1().trim().isEmpty()) {
            throw new IllegalArgumentException("Address line 1 is required");
        }
        if (customerDto.getCity() == null || customerDto.getCity().trim().isEmpty()) {
            throw new IllegalArgumentException("City is required");
        }
        if (customerDto.getCountry() == null || customerDto.getCountry().trim().isEmpty()) {
            throw new IllegalArgumentException("Country is required");
        }
        
        // Validate credit limit if provided
        if (customerDto.getCreditLimit() != null && customerDto.getCreditLimit().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Credit limit must be positive");
        }
    }
}