package com.tester.classicmodel.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;

public class CustomerDto {
    @JsonProperty("customerNumber")
    private Integer customerNumber;

    @NotBlank(message = "Customer name is required")
    @Size(max = 50, message = "Customer name must not exceed 50 characters")
    @JsonProperty("customerName")
    private String customerName;

    @NotBlank(message = "Contact last name is required")
    @Size(max = 50, message = "Contact last name must not exceed 50 characters")
    @JsonProperty("contactLastName")
    private String contactLastName;

    @NotBlank(message = "Contact first name is required")
    @Size(max = 50, message = "Contact first name must not exceed 50 characters")
    @JsonProperty("contactFirstName")
    private String contactFirstName;

    @NotBlank(message = "Phone is required")
    @Size(max = 50, message = "Phone must not exceed 50 characters")
    @JsonProperty("phone")
    private String phone;

    @NotBlank(message = "Address line 1 is required")
    @Size(max = 50, message = "Address line 1 must not exceed 50 characters")
    @JsonProperty("addressLine1")
    private String addressLine1;

    @Size(max = 50, message = "Address line 2 must not exceed 50 characters")
    @JsonProperty("addressLine2")
    private String addressLine2;

    @NotBlank(message = "City is required")
    @Size(max = 50, message = "City must not exceed 50 characters")
    @JsonProperty("city")
    private String city;

    @Size(max = 50, message = "State must not exceed 50 characters")
    @JsonProperty("state")
    private String state;

    @Size(max = 15, message = "Postal code must not exceed 15 characters")
    @JsonProperty("postalCode")
    private String postalCode;

    @NotBlank(message = "Country is required")
    @Size(max = 50, message = "Country must not exceed 50 characters")
    @JsonProperty("country")
    private String country;

    @JsonProperty("salesRepEmployeeNumber")
    private Integer salesRepEmployeeNumber;

    @DecimalMin(value = "0.0", inclusive = false, message = "Credit limit must be positive")
    @JsonProperty("creditLimit")
    private BigDecimal creditLimit;

    // Default constructor
    public CustomerDto() {}

    // Constructor with required fields
    public CustomerDto(String customerName, String contactLastName, String contactFirstName, 
                      String phone, String addressLine1, String city, String country) {
        this.customerName = customerName;
        this.contactLastName = contactLastName;
        this.contactFirstName = contactFirstName;
        this.phone = phone;
        this.addressLine1 = addressLine1;
        this.city = city;
        this.country = country;
    }

    // Getters and Setters
    public Integer getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(Integer customerNumber) {
        this.customerNumber = customerNumber;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getContactLastName() {
        return contactLastName;
    }

    public void setContactLastName(String contactLastName) {
        this.contactLastName = contactLastName;
    }

    public String getContactFirstName() {
        return contactFirstName;
    }

    public void setContactFirstName(String contactFirstName) {
        this.contactFirstName = contactFirstName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Integer getSalesRepEmployeeNumber() {
        return salesRepEmployeeNumber;
    }

    public void setSalesRepEmployeeNumber(Integer salesRepEmployeeNumber) {
        this.salesRepEmployeeNumber = salesRepEmployeeNumber;
    }

    public BigDecimal getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(BigDecimal creditLimit) {
        this.creditLimit = creditLimit;
    }
}