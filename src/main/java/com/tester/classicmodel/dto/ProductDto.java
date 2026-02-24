package com.tester.classicmodel.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public class ProductDto {
    @NotBlank(message = "Product code is required")
    @Size(max = 15, message = "Product code must not exceed 15 characters")
    @JsonProperty("productCode")
    private String productCode;

    @NotBlank(message = "Product name is required")
    @Size(max = 70, message = "Product name must not exceed 70 characters")
    @JsonProperty("productName")
    private String productName;

    @NotBlank(message = "Product line is required")
    @Size(max = 50, message = "Product line must not exceed 50 characters")
    @JsonProperty("productLine")
    private String productLine;

    @NotBlank(message = "Product scale is required")
    @Size(max = 10, message = "Product scale must not exceed 10 characters")
    @JsonProperty("productScale")
    private String productScale;

    @NotBlank(message = "Product vendor is required")
    @Size(max = 50, message = "Product vendor must not exceed 50 characters")
    @JsonProperty("productVendor")
    private String productVendor;

    @Size(max = 255, message = "Product description must not exceed 255 characters")
    @JsonProperty("productDescription")
    private String productDescription;

    @NotNull(message = "Quantity in stock is required")
    @Min(value = 0, message = "Quantity in stock must be non-negative")
    @JsonProperty("quantityInStock")
    private Short quantityInStock;

    @NotNull(message = "Buy price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Buy price must be positive")
    @JsonProperty("buyPrice")
    private BigDecimal buyPrice;

    @NotNull(message = "MSRP is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "MSRP must be positive")
    @JsonProperty("msrp")
    private BigDecimal msrp;

    // Default constructor
    public ProductDto() {}

    // Constructor with required fields
    public ProductDto(String productCode, String productName, String productLine, String productScale, 
                     String productVendor, Short quantityInStock, BigDecimal buyPrice, BigDecimal msrp) {
        this.productCode = productCode;
        this.productName = productName;
        this.productLine = productLine;
        this.productScale = productScale;
        this.productVendor = productVendor;
        this.quantityInStock = quantityInStock;
        this.buyPrice = buyPrice;
        this.msrp = msrp;
    }

    // Getters and Setters
    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductLine() {
        return productLine;
    }

    public void setProductLine(String productLine) {
        this.productLine = productLine;
    }

    public String getProductScale() {
        return productScale;
    }

    public void setProductScale(String productScale) {
        this.productScale = productScale;
    }

    public String getProductVendor() {
        return productVendor;
    }

    public void setProductVendor(String productVendor) {
        this.productVendor = productVendor;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public Short getQuantityInStock() {
        return quantityInStock;
    }

    public void setQuantityInStock(Short quantityInStock) {
        this.quantityInStock = quantityInStock;
    }

    public BigDecimal getBuyPrice() {
        return buyPrice;
    }

    public void setBuyPrice(BigDecimal buyPrice) {
        this.buyPrice = buyPrice;
    }

    public BigDecimal getMsrp() {
        return msrp;
    }

    public void setMsrp(BigDecimal msrp) {
        this.msrp = msrp;
    }
}