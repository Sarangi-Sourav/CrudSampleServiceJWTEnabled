package com.tester.classicmodel.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ProductLineDto {
    @NotBlank(message = "Product line is required")
    @Size(max = 50, message = "Product line must not exceed 50 characters")
    @JsonProperty("productLine")
    private String productLine;

    @Size(max = 4000, message = "Text description must not exceed 4000 characters")
    @JsonProperty("textDescription")
    private String textDescription;

    @Size(max = 4000, message = "HTML description must not exceed 4000 characters")
    @JsonProperty("htmlDescription")
    private String htmlDescription;

    // Note: Excluding image field from DTO as it's typically handled separately in file uploads
    // If needed, it can be added as Base64 encoded string or handled via separate endpoint

    // Default constructor
    public ProductLineDto() {}

    // Constructor with required fields
    public ProductLineDto(String productLine, String textDescription) {
        this.productLine = productLine;
        this.textDescription = textDescription;
    }

    // Getters and Setters
    public String getProductLine() {
        return productLine;
    }

    public void setProductLine(String productLine) {
        this.productLine = productLine;
    }

    public String getTextDescription() {
        return textDescription;
    }

    public void setTextDescription(String textDescription) {
        this.textDescription = textDescription;
    }

    public String getHtmlDescription() {
        return htmlDescription;
    }

    public void setHtmlDescription(String htmlDescription) {
        this.htmlDescription = htmlDescription;
    }
}