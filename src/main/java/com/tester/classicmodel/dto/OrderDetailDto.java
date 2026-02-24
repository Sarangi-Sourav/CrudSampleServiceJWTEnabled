package com.tester.classicmodel.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public class OrderDetailDto {
    @NotNull(message = "Order number is required")
    @JsonProperty("orderNumber")
    private Integer orderNumber;

    @NotBlank(message = "Product code is required")
    @Size(max = 15, message = "Product code must not exceed 15 characters")
    @JsonProperty("productCode")
    private String productCode;

    @NotNull(message = "Quantity ordered is required")
    @Min(value = 1, message = "Quantity ordered must be at least 1")
    @JsonProperty("quantityOrdered")
    private Integer quantityOrdered;

    @NotNull(message = "Price each is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price each must be positive")
    @JsonProperty("priceEach")
    private BigDecimal priceEach;

    @NotNull(message = "Order line number is required")
    @Min(value = 1, message = "Order line number must be at least 1")
    @JsonProperty("orderLineNumber")
    private Short orderLineNumber;

    // Default constructor
    public OrderDetailDto() {}

    // Constructor with required fields (composite key)
    public OrderDetailDto(Integer orderNumber, String productCode, Integer quantityOrdered, 
                         BigDecimal priceEach, Short orderLineNumber) {
        this.orderNumber = orderNumber;
        this.productCode = productCode;
        this.quantityOrdered = quantityOrdered;
        this.priceEach = priceEach;
        this.orderLineNumber = orderLineNumber;
    }

    // Getters and Setters
    public Integer getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(Integer orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public Integer getQuantityOrdered() {
        return quantityOrdered;
    }

    public void setQuantityOrdered(Integer quantityOrdered) {
        this.quantityOrdered = quantityOrdered;
    }

    public BigDecimal getPriceEach() {
        return priceEach;
    }

    public void setPriceEach(BigDecimal priceEach) {
        this.priceEach = priceEach;
    }

    public Short getOrderLineNumber() {
        return orderLineNumber;
    }

    public void setOrderLineNumber(Short orderLineNumber) {
        this.orderLineNumber = orderLineNumber;
    }
}