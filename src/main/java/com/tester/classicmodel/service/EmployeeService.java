package com.tester.classicmodel.service;

import com.tester.classicmodel.dto.EmployeeDto;
import com.tester.classicmodel.model.Employee;
import com.tester.classicmodel.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    /**
     * Get all employees
     * @return List of EmployeeDto
     */
    public List<EmployeeDto> getAllEmployees() {
        try {
            List<Employee> employees = employeeRepository.findAll();
            return employees.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving employees: " + e.getMessage(), e);
        }
    }

    /**
     * Get employee by ID
     * @param employeeNumber Employee ID
     * @return EmployeeDto if found
     * @throws RuntimeException if employee not found
     */
    public EmployeeDto getEmployeeById(Integer employeeNumber) {
        if (employeeNumber == null) {
            throw new IllegalArgumentException("Employee number cannot be null");
        }

        try {
            Optional<Employee> employee = employeeRepository.findById(employeeNumber);
            return employee.map(this::convertToDto)
                    .orElseThrow(() -> new RuntimeException("Employee with ID " + employeeNumber + " not found"));
        } catch (Exception e) {
            if (e instanceof RuntimeException && e.getMessage().contains("not found")) {
                throw e;
            }
            throw new RuntimeException("Error retrieving employee with ID " + employeeNumber + ": " + e.getMessage(), e);
        }
    }

    /**
     * Create a new employee
     * @param employeeDto Employee data
     * @return Created EmployeeDto
     * @throws IllegalArgumentException if validation fails
     * @throws RuntimeException if database constraint violation occurs
     */
    public EmployeeDto createEmployee(EmployeeDto employeeDto) {
        if (employeeDto == null) {
            throw new IllegalArgumentException("Employee data cannot be null");
        }

        // Validate required fields
        validateEmployeeDto(employeeDto);

        try {
            Employee employee = convertToEntity(employeeDto);
            Employee savedEmployee = employeeRepository.save(employee);
            return convertToDto(savedEmployee);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Database constraint violation: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error creating employee: " + e.getMessage(), e);
        }
    }

    /**
     * Update an existing employee
     * @param employeeNumber Employee ID
     * @param employeeDto Updated employee data
     * @return Updated EmployeeDto
     * @throws RuntimeException if employee not found or validation fails
     */
    public EmployeeDto updateEmployee(Integer employeeNumber, EmployeeDto employeeDto) {
        if (employeeNumber == null) {
            throw new IllegalArgumentException("Employee number cannot be null");
        }
        if (employeeDto == null) {
            throw new IllegalArgumentException("Employee data cannot be null");
        }

        // Validate required fields
        validateEmployeeDto(employeeDto);

        try {
            // Check if employee exists
            Optional<Employee> existingEmployee = employeeRepository.findById(employeeNumber);
            if (existingEmployee.isEmpty()) {
                throw new RuntimeException("Employee with ID " + employeeNumber + " not found");
            }

            Employee employee = convertToEntity(employeeDto);
            employee.setEmployeeNumber(employeeNumber);
            Employee updatedEmployee = employeeRepository.update(employee);
            return convertToDto(updatedEmployee);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Database constraint violation: " + e.getMessage(), e);
        } catch (Exception e) {
            if (e instanceof RuntimeException && e.getMessage().contains("not found")) {
                throw e;
            }
            throw new RuntimeException("Error updating employee with ID " + employeeNumber + ": " + e.getMessage(), e);
        }
    }

    /**
     * Delete an employee
     * @param employeeNumber Employee ID
     * @throws RuntimeException if employee not found
     */
    public void deleteEmployee(Integer employeeNumber) {
        if (employeeNumber == null) {
            throw new IllegalArgumentException("Employee number cannot be null");
        }

        try {
            // Check if employee exists
            Optional<Employee> existingEmployee = employeeRepository.findById(employeeNumber);
            if (existingEmployee.isEmpty()) {
                throw new RuntimeException("Employee with ID " + employeeNumber + " not found");
            }

            employeeRepository.deleteById(employeeNumber);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Cannot delete employee due to existing references: " + e.getMessage(), e);
        } catch (Exception e) {
            if (e instanceof RuntimeException && e.getMessage().contains("not found")) {
                throw e;
            }
            throw new RuntimeException("Error deleting employee with ID " + employeeNumber + ": " + e.getMessage(), e);
        }
    }

    /**
     * Convert Employee entity to EmployeeDto
     * @param employee Employee entity
     * @return EmployeeDto
     */
    private EmployeeDto convertToDto(Employee employee) {
        EmployeeDto dto = new EmployeeDto();
        dto.setEmployeeNumber(employee.getEmployeeNumber());
        dto.setLastName(employee.getLastName());
        dto.setFirstName(employee.getFirstName());
        dto.setExtension(employee.getExtension());
        dto.setEmail(employee.getEmail());
        dto.setOfficeCode(employee.getOfficeCode());
        dto.setReportsTo(employee.getReportsTo());
        dto.setJobTitle(employee.getJobTitle());
        return dto;
    }

    /**
     * Convert EmployeeDto to Employee entity
     * @param dto EmployeeDto
     * @return Employee entity
     */
    private Employee convertToEntity(EmployeeDto dto) {
        Employee employee = new Employee();
        employee.setEmployeeNumber(dto.getEmployeeNumber());
        employee.setLastName(dto.getLastName());
        employee.setFirstName(dto.getFirstName());
        employee.setExtension(dto.getExtension());
        employee.setEmail(dto.getEmail());
        employee.setOfficeCode(dto.getOfficeCode());
        employee.setReportsTo(dto.getReportsTo());
        employee.setJobTitle(dto.getJobTitle());
        return employee;
    }

    /**
     * Validate EmployeeDto required fields
     * @param employeeDto EmployeeDto to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateEmployeeDto(EmployeeDto employeeDto) {
        if (employeeDto.getLastName() == null || employeeDto.getLastName().trim().isEmpty()) {
            throw new IllegalArgumentException("Last name is required");
        }
        if (employeeDto.getFirstName() == null || employeeDto.getFirstName().trim().isEmpty()) {
            throw new IllegalArgumentException("First name is required");
        }
        if (employeeDto.getEmail() == null || employeeDto.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (!EMAIL_PATTERN.matcher(employeeDto.getEmail()).matches()) {
            throw new IllegalArgumentException("Email should be valid");
        }
        if (employeeDto.getOfficeCode() == null || employeeDto.getOfficeCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Office code is required");
        }
        if (employeeDto.getJobTitle() == null || employeeDto.getJobTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Job title is required");
        }
        
        // Validate field lengths
        if (employeeDto.getLastName().length() > 50) {
            throw new IllegalArgumentException("Last name must not exceed 50 characters");
        }
        if (employeeDto.getFirstName().length() > 50) {
            throw new IllegalArgumentException("First name must not exceed 50 characters");
        }
        if (employeeDto.getEmail().length() > 100) {
            throw new IllegalArgumentException("Email must not exceed 100 characters");
        }
        if (employeeDto.getOfficeCode().length() > 10) {
            throw new IllegalArgumentException("Office code must not exceed 10 characters");
        }
        if (employeeDto.getJobTitle().length() > 50) {
            throw new IllegalArgumentException("Job title must not exceed 50 characters");
        }
        if (employeeDto.getExtension() != null && employeeDto.getExtension().length() > 10) {
            throw new IllegalArgumentException("Extension must not exceed 10 characters");
        }
    }
}