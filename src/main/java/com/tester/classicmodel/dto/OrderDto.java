package com.tester.classicmodel.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public class OrderDto {
    @JsonProperty("orderNumber")
    private Integer orderNumber;

    @NotNull(message = "Order date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonProperty("orderDate")
    private LocalDate orderDate;

    @NotNull(message = "Required date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonProperty("requiredDate")
    private LocalDate requiredDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonProperty("shippedDate")
    private LocalDate shippedDate;

    @NotNull(message = "Status is required")
    @Size(max = 15, message = "Status must not exceed 15 characters")
    @JsonProperty("status")
    private String status;

    @Size(max = 255, message = "Comments must not exceed 255 characters")
    @JsonProperty("comments")
    private String comments;

    @NotNull(message = "Customer number is required")
    @JsonProperty("customerNumber")
    private Integer customerNumber;

    // Default constructor
    public OrderDto() {}

    // Constructor with required fields
    public OrderDto(LocalDate orderDate, LocalDate requiredDate, String status, Integer customerNumber) {
        this.orderDate = orderDate;
        this.requiredDate = requiredDate;
        this.status = status;
        this.customerNumber = customerNumber;
    }

    // Getters and Setters
    public Integer getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(Integer orderNumber) {
        this.orderNumber = orderNumber;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public LocalDate getRequiredDate() {
        return requiredDate;
    }

    public void setRequiredDate(LocalDate requiredDate) {
        this.requiredDate = requiredDate;
    }

    public LocalDate getShippedDate() {
        return shippedDate;
    }

    public void setShippedDate(LocalDate shippedDate) {
        this.shippedDate = shippedDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Integer getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(Integer customerNumber) {
        this.customerNumber = customerNumber;
    }
}