package com.tester.classicmodel.model;

public class ProductLine {
    private String productLine;
    private String textDescription;
    private String htmlDescription;
    private byte[] image;

    // Default constructor
    public ProductLine() {}

    // Constructor with required fields
    public ProductLine(String productLine, String textDescription) {
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

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "ProductLine{" +
                "productLine='" + productLine + '\'' +
                ", textDescription='" + textDescription + '\'' +
                ", htmlDescription='" + (htmlDescription != null ? "present" : "null") + '\'' +
                ", image=" + (image != null ? "present" : "null") +
                '}';
    }
}