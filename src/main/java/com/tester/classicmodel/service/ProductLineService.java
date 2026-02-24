package com.tester.classicmodel.service;

import com.tester.classicmodel.dto.ProductLineDto;
import com.tester.classicmodel.model.ProductLine;
import com.tester.classicmodel.repository.ProductLineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductLineService {

    private final ProductLineRepository productLineRepository;

    @Autowired
    public ProductLineService(ProductLineRepository productLineRepository) {
        this.productLineRepository = productLineRepository;
    }

    /**
     * Get all product lines
     * @return List of ProductLineDto
     */
    public List<ProductLineDto> getAllProductLines() {
        try {
            List<ProductLine> productLines = productLineRepository.findAll();
            return productLines.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving product lines: " + e.getMessage(), e);
        }
    }

    /**
     * Get product line by name
     * @param productLine Product line name
     * @return ProductLineDto if found
     * @throws RuntimeException if product line not found
     */
    public ProductLineDto getProductLineById(String productLine) {
        if (productLine == null || productLine.trim().isEmpty()) {
            throw new IllegalArgumentException("Product line name cannot be null or empty");
        }

        try {
            Optional<ProductLine> productLineEntity = productLineRepository.findById(productLine);
            return productLineEntity.map(this::convertToDto)
                    .orElseThrow(() -> new RuntimeException("ProductLine with name " + productLine + " not found"));
        } catch (Exception e) {
            if (e instanceof RuntimeException && e.getMessage().contains("not found")) {
                throw e;
            }
            throw new RuntimeException("Error retrieving product line with name " + productLine + ": " + e.getMessage(), e);
        }
    }

    /**
     * Create a new product line
     * @param productLineDto Product line data
     * @return Created ProductLineDto
     * @throws IllegalArgumentException if validation fails
     * @throws RuntimeException if database constraint violation occurs
     */
    public ProductLineDto createProductLine(ProductLineDto productLineDto) {
        if (productLineDto == null) {
            throw new IllegalArgumentException("Product line data cannot be null");
        }

        // Validate required fields
        validateProductLineDto(productLineDto);

        try {
            ProductLine productLine = convertToEntity(productLineDto);
            ProductLine savedProductLine = productLineRepository.save(productLine);
            return convertToDto(savedProductLine);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Database constraint violation: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error creating product line: " + e.getMessage(), e);
        }
    }

    /**
     * Update an existing product line
     * @param productLine Product line name
     * @param productLineDto Updated product line data
     * @return Updated ProductLineDto
     * @throws RuntimeException if product line not found or validation fails
     */
    public ProductLineDto updateProductLine(String productLine, ProductLineDto productLineDto) {
        if (productLine == null || productLine.trim().isEmpty()) {
            throw new IllegalArgumentException("Product line name cannot be null or empty");
        }
        if (productLineDto == null) {
            throw new IllegalArgumentException("Product line data cannot be null");
        }

        // Validate required fields
        validateProductLineDto(productLineDto);

        try {
            // Check if product line exists
            Optional<ProductLine> existingProductLine = productLineRepository.findById(productLine);
            if (existingProductLine.isEmpty()) {
                throw new RuntimeException("ProductLine with name " + productLine + " not found");
            }

            ProductLine productLineEntity = convertToEntity(productLineDto);
            productLineEntity.setProductLine(productLine);
            ProductLine updatedProductLine = productLineRepository.update(productLineEntity);
            return convertToDto(updatedProductLine);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Database constraint violation: " + e.getMessage(), e);
        } catch (Exception e) {
            if (e instanceof RuntimeException && e.getMessage().contains("not found")) {
                throw e;
            }
            throw new RuntimeException("Error updating product line with name " + productLine + ": " + e.getMessage(), e);
        }
    }

    /**
     * Delete a product line
     * @param productLine Product line name
     * @throws RuntimeException if product line not found
     */
    public void deleteProductLine(String productLine) {
        if (productLine == null || productLine.trim().isEmpty()) {
            throw new IllegalArgumentException("Product line name cannot be null or empty");
        }

        try {
            // Check if product line exists
            Optional<ProductLine> existingProductLine = productLineRepository.findById(productLine);
            if (existingProductLine.isEmpty()) {
                throw new RuntimeException("ProductLine with name " + productLine + " not found");
            }

            productLineRepository.deleteById(productLine);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Cannot delete product line due to existing references: " + e.getMessage(), e);
        } catch (Exception e) {
            if (e instanceof RuntimeException && e.getMessage().contains("not found")) {
                throw e;
            }
            throw new RuntimeException("Error deleting product line with name " + productLine + ": " + e.getMessage(), e);
        }
    }

    /**
     * Convert ProductLine entity to ProductLineDto
     * @param productLine ProductLine entity
     * @return ProductLineDto
     */
    private ProductLineDto convertToDto(ProductLine productLine) {
        ProductLineDto dto = new ProductLineDto();
        dto.setProductLine(productLine.getProductLine());
        dto.setTextDescription(productLine.getTextDescription());
        dto.setHtmlDescription(productLine.getHtmlDescription());
        // Note: Image is excluded from DTO as it's typically handled separately
        return dto;
    }

    /**
     * Convert ProductLineDto to ProductLine entity
     * @param dto ProductLineDto
     * @return ProductLine entity
     */
    private ProductLine convertToEntity(ProductLineDto dto) {
        ProductLine productLine = new ProductLine();
        productLine.setProductLine(dto.getProductLine());
        productLine.setTextDescription(dto.getTextDescription());
        productLine.setHtmlDescription(dto.getHtmlDescription());
        // Note: Image is not set from DTO, would be handled separately if needed
        return productLine;
    }

    /**
     * Validate ProductLineDto required fields
     * @param productLineDto ProductLineDto to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateProductLineDto(ProductLineDto productLineDto) {
        if (productLineDto.getProductLine() == null || productLineDto.getProductLine().trim().isEmpty()) {
            throw new IllegalArgumentException("Product line name is required");
        }
        
        // Validate field lengths
        if (productLineDto.getProductLine().length() > 50) {
            throw new IllegalArgumentException("Product line name must not exceed 50 characters");
        }
        if (productLineDto.getTextDescription() != null && productLineDto.getTextDescription().length() > 4000) {
            throw new IllegalArgumentException("Text description must not exceed 4000 characters");
        }
        if (productLineDto.getHtmlDescription() != null && productLineDto.getHtmlDescription().length() > 4000) {
            throw new IllegalArgumentException("HTML description must not exceed 4000 characters");
        }
        
        // Validate product line name format (business rule - alphanumeric and spaces only)
        if (!productLineDto.getProductLine().matches("^[a-zA-Z0-9\\s]+$")) {
            throw new IllegalArgumentException("Product line name can only contain letters, numbers, and spaces");
        }
        
        // Validate HTML description doesn't contain potentially dangerous tags (basic validation)
        if (productLineDto.getHtmlDescription() != null) {
            String htmlDesc = productLineDto.getHtmlDescription().toLowerCase();
            if (htmlDesc.contains("<script") || htmlDesc.contains("javascript:") || htmlDesc.contains("onclick")) {
                throw new IllegalArgumentException("HTML description contains potentially unsafe content");
            }
        }
    }
}