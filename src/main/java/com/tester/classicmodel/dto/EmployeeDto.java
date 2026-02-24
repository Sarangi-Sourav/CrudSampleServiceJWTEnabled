package com.tester.classicmodel.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class EmployeeDto {
    @JsonProperty("employeeNumber")
    private Integer employeeNumber;

    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name must not exceed 50 characters")
    @JsonProperty("lastName")
    private String lastName;

    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name must not exceed 50 characters")
    @JsonProperty("firstName")
    private String firstName;

    @Size(max = 10, message = "Extension must not exceed 10 characters")
    @JsonProperty("extension")
    private String extension;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    @JsonProperty("email")
    private String email;

    @NotBlank(message = "Office code is required")
    @Size(max = 10, message = "Office code must not exceed 10 characters")
    @JsonProperty("officeCode")
    private String officeCode;

    @JsonProperty("reportsTo")
    private Integer reportsTo;

    @NotBlank(message = "Job title is required")
    @Size(max = 50, message = "Job title must not exceed 50 characters")
    @JsonProperty("jobTitle")
    private String jobTitle;

    // Default constructor
    public EmployeeDto() {}

    // Constructor with required fields
    public EmployeeDto(String lastName, String firstName, String email, String officeCode, String jobTitle) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.email = email;
        this.officeCode = officeCode;
        this.jobTitle = jobTitle;
    }

    // Getters and Setters
    public Integer getEmployeeNumber() {
        return employeeNumber;
    }

    public void setEmployeeNumber(Integer employeeNumber) {
        this.employeeNumber = employeeNumber;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOfficeCode() {
        return officeCode;
    }

    public void setOfficeCode(String officeCode) {
        this.officeCode = officeCode;
    }

    public Integer getReportsTo() {
        return reportsTo;
    }

    public void setReportsTo(Integer reportsTo) {
        this.reportsTo = reportsTo;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }
}