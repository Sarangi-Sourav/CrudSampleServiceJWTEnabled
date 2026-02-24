package com.tester.classicmodel.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class OfficeDto {
    @NotBlank(message = "Office code is required")
    @Size(max = 10, message = "Office code must not exceed 10 characters")
    @JsonProperty("officeCode")
    private String officeCode;

    @NotBlank(message = "City is required")
    @Size(max = 50, message = "City must not exceed 50 characters")
    @JsonProperty("city")
    private String city;

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

    @Size(max = 50, message = "State must not exceed 50 characters")
    @JsonProperty("state")
    private String state;

    @NotBlank(message = "Country is required")
    @Size(max = 50, message = "Country must not exceed 50 characters")
    @JsonProperty("country")
    private String country;

    @Size(max = 15, message = "Postal code must not exceed 15 characters")
    @JsonProperty("postalCode")
    private String postalCode;

    @NotBlank(message = "Territory is required")
    @Size(max = 10, message = "Territory must not exceed 10 characters")
    @JsonProperty("territory")
    private String territory;

    // Default constructor
    public OfficeDto() {}

    // Constructor with required fields
    public OfficeDto(String officeCode, String city, String phone, String addressLine1, String country, String territory) {
        this.officeCode = officeCode;
        this.city = city;
        this.phone = phone;
        this.addressLine1 = addressLine1;
        this.country = country;
        this.territory = territory;
    }

    // Getters and Setters
    public String getOfficeCode() {
        return officeCode;
    }

    public void setOfficeCode(String officeCode) {
        this.officeCode = officeCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getTerritory() {
        return territory;
    }

    public void setTerritory(String territory) {
        this.territory = territory;
    }
}