package com.tester.classicmodel.controller;

import com.tester.classicmodel.dto.EmployeeDto;
import com.tester.classicmodel.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    /**
     * Get all employees
     * @return List of all employees
     */
    @GetMapping
    public ResponseEntity<List<EmployeeDto>> getAllEmployees() {
        try {
            List<EmployeeDto> employees = employeeService.getAllEmployees();
            return ResponseEntity.ok(employees);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get employee by ID
     * @param employeeNumber Employee ID
     * @return Employee if found, 404 if not found
     */
    @GetMapping("/{employeeNumber}")
    public ResponseEntity<EmployeeDto> getEmployeeById(@PathVariable Integer employeeNumber) {
        try {
            EmployeeDto employee = employeeService.getEmployeeById(employeeNumber);
            return ResponseEntity.ok(employee);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Create a new employee
     * @param employeeDto Employee data
     * @return Created employee with 201 status
     */
    @PostMapping
    public ResponseEntity<EmployeeDto> createEmployee(@RequestBody @Valid EmployeeDto employeeDto) {
        try {
            EmployeeDto createdEmployee = employeeService.createEmployee(employeeDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdEmployee);
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
     * Update an existing employee
     * @param employeeNumber Employee ID
     * @param employeeDto Updated employee data
     * @return Updated employee with 200 status
     */
    @PutMapping("/{employeeNumber}")
    public ResponseEntity<EmployeeDto> updateEmployee(@PathVariable Integer employeeNumber, 
                                                     @RequestBody @Valid EmployeeDto employeeDto) {
        try {
            EmployeeDto updatedEmployee = employeeService.updateEmployee(employeeNumber, employeeDto);
            return ResponseEntity.ok(updatedEmployee);
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
     * Delete an employee
     * @param employeeNumber Employee ID
     * @return 204 No Content if successful
     */
    @DeleteMapping("/{employeeNumber}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Integer employeeNumber) {
        try {
            employeeService.deleteEmployee(employeeNumber);
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