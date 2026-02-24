package com.tester.classicmodel.service;

import com.tester.classicmodel.dto.ProductDto;
import com.tester.classicmodel.model.Product;
import com.tester.classicmodel.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Get all products
     * @return List of ProductDto
     */
    public List<ProductDto> getAllProducts() {
        try {
            List<Product> products = productRepository.findAll();
            return products.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving products: " + e.getMessage(), e);
        }
    }

    /**
     * Get product by code
     * @param productCode Product code
     * @return ProductDto if found
     * @throws RuntimeException if product not found
     */
    public ProductDto getProductById(String productCode) {
        if (productCode == null || productCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Product code cannot be null or empty");
        }

        try {
            Optional<Product> product = productRepository.findById(productCode);
            return product.map(this::convertToDto)
                    .orElseThrow(() -> new RuntimeException("Product with code " + productCode + " not found"));
        } catch (Exception e) {
            if (e instanceof RuntimeException && e.getMessage().contains("not found")) {
                throw e;
            }
            throw new RuntimeException("Error retrieving product with code " + productCode + ": " + e.getMessage(), e);
        }
    }

    /**
     * Create a new product
     * @param productDto Product data
     * @return Created ProductDto
     * @throws IllegalArgumentException if validation fails
     * @throws RuntimeException if database constraint violation occurs
     */
    public ProductDto createProduct(ProductDto productDto) {
        if (productDto == null) {
            throw new IllegalArgumentException("Product data cannot be null");
        }

        // Validate required fields
        validateProductDto(productDto);

        try {
            Product product = convertToEntity(productDto);
            Product savedProduct = productRepository.save(product);
            return convertToDto(savedProduct);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Database constraint violation: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error creating product: " + e.getMessage(), e);
        }
    }

    /**
     * Update an existing product
     * @param productCode Product code
     * @param productDto Updated product data
     * @return Updated ProductDto
     * @throws RuntimeException if product not found or validation fails
     */
    public ProductDto updateProduct(String productCode, ProductDto productDto) {
        if (productCode == null || productCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Product code cannot be null or empty");
        }
        if (productDto == null) {
            throw new IllegalArgumentException("Product data cannot be null");
        }

        // Validate required fields
        validateProductDto(productDto);

        try {
            // Check if product exists
            Optional<Product> existingProduct = productRepository.findById(productCode);
            if (existingProduct.isEmpty()) {
                throw new RuntimeException("Product with code " + productCode + " not found");
            }

            Product product = convertToEntity(productDto);
            product.setProductCode(productCode);
            Product updatedProduct = productRepository.update(product);
            return convertToDto(updatedProduct);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Database constraint violation: " + e.getMessage(), e);
        } catch (Exception e) {
            if (e instanceof RuntimeException && e.getMessage().contains("not found")) {
                throw e;
            }
            throw new RuntimeException("Error updating product with code " + productCode + ": " + e.getMessage(), e);
        }
    }

    /**
     * Delete a product
     * @param productCode Product code
     * @throws RuntimeException if product not found
     */
    public void deleteProduct(String productCode) {
        if (productCode == null || productCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Product code cannot be null or empty");
        }

        try {
            // Check if product exists
            Optional<Product> existingProduct = productRepository.findById(productCode);
            if (existingProduct.isEmpty()) {
                throw new RuntimeException("Product with code " + productCode + " not found");
            }

            productRepository.deleteById(productCode);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Cannot delete product due to existing references: " + e.getMessage(), e);
        } catch (Exception e) {
            if (e instanceof RuntimeException && e.getMessage().contains("not found")) {
                throw e;
            }
            throw new RuntimeException("Error deleting product with code " + productCode + ": " + e.getMessage(), e);
        }
    }

    /**
     * Convert Product entity to ProductDto
     * @param product Product entity
     * @return ProductDto
     */
    private ProductDto convertToDto(Product product) {
        ProductDto dto = new ProductDto();
        dto.setProductCode(product.getProductCode());
        dto.setProductName(product.getProductName());
        dto.setProductLine(product.getProductLine());
        dto.setProductScale(product.getProductScale());
        dto.setProductVendor(product.getProductVendor());
        dto.setProductDescription(product.getProductDescription());
        dto.setQuantityInStock(product.getQuantityInStock());
        dto.setBuyPrice(product.getBuyPrice());
        dto.setMsrp(product.getMsrp());
        return dto;
    }

    /**
     * Convert ProductDto to Product entity
     * @param dto ProductDto
     * @return Product entity
     */
    private Product convertToEntity(ProductDto dto) {
        Product product = new Product();
        product.setProductCode(dto.getProductCode());
        product.setProductName(dto.getProductName());
        product.setProductLine(dto.getProductLine());
        product.setProductScale(dto.getProductScale());
        product.setProductVendor(dto.getProductVendor());
        product.setProductDescription(dto.getProductDescription());
        product.setQuantityInStock(dto.getQuantityInStock());
        product.setBuyPrice(dto.getBuyPrice());
        product.setMsrp(dto.getMsrp());
        return product;
    }

    /**
     * Validate ProductDto required fields
     * @param productDto ProductDto to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateProductDto(ProductDto productDto) {
        if (productDto.getProductCode() == null || productDto.getProductCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Product code is required");
        }
        if (productDto.getProductName() == null || productDto.getProductName().trim().isEmpty()) {
            throw new IllegalArgumentException("Product name is required");
        }
        if (productDto.getProductLine() == null || productDto.getProductLine().trim().isEmpty()) {
            throw new IllegalArgumentException("Product line is required");
        }
        if (productDto.getProductScale() == null || productDto.getProductScale().trim().isEmpty()) {
            throw new IllegalArgumentException("Product scale is required");
        }
        if (productDto.getProductVendor() == null || productDto.getProductVendor().trim().isEmpty()) {
            throw new IllegalArgumentException("Product vendor is required");
        }
        if (productDto.getQuantityInStock() == null) {
            throw new IllegalArgumentException("Quantity in stock is required");
        }
        if (productDto.getBuyPrice() == null) {
            throw new IllegalArgumentException("Buy price is required");
        }
        if (productDto.getMsrp() == null) {
            throw new IllegalArgumentException("MSRP is required");
        }
        
        // Validate field constraints
        if (productDto.getQuantityInStock() < 0) {
            throw new IllegalArgumentException("Quantity in stock must be non-negative");
        }
        if (productDto.getBuyPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Buy price must be positive");
        }
        if (productDto.getMsrp().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("MSRP must be positive");
        }
        
        // Validate field lengths
        if (productDto.getProductCode().length() > 15) {
            throw new IllegalArgumentException("Product code must not exceed 15 characters");
        }
        if (productDto.getProductName().length() > 70) {
            throw new IllegalArgumentException("Product name must not exceed 70 characters");
        }
        if (productDto.getProductLine().length() > 50) {
            throw new IllegalArgumentException("Product line must not exceed 50 characters");
        }
        if (productDto.getProductScale().length() > 10) {
            throw new IllegalArgumentException("Product scale must not exceed 10 characters");
        }
        if (productDto.getProductVendor().length() > 50) {
            throw new IllegalArgumentException("Product vendor must not exceed 50 characters");
        }
        if (productDto.getProductDescription() != null && productDto.getProductDescription().length() > 255) {
            throw new IllegalArgumentException("Product description must not exceed 255 characters");
        }
        
        // Business rule validations
        if (productDto.getBuyPrice().compareTo(productDto.getMsrp()) > 0) {
            throw new IllegalArgumentException("Buy price cannot be greater than MSRP");
        }
        
        // Validate product scale format (e.g., "1:18", "1:24")
        if (!productDto.getProductScale().matches("^1:\\d+$")) {
            throw new IllegalArgumentException("Product scale must be in format '1:XX' (e.g., '1:18')");
        }
        
        // Validate reasonable price limits
        BigDecimal maxPrice = new BigDecimal("100000.00"); // $100,000 limit
        if (productDto.getBuyPrice().compareTo(maxPrice) > 0 || productDto.getMsrp().compareTo(maxPrice) > 0) {
            throw new IllegalArgumentException("Prices cannot exceed $100,000.00");
        }
    }
}