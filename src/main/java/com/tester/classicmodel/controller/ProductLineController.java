package com.tester.classicmodel.controller;

import com.tester.classicmodel.dto.ProductLineDto;
import com.tester.classicmodel.service.ProductLineService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productlines")
public class ProductLineController {

    private final ProductLineService productLineService;

    @Autowired
    public ProductLineController(ProductLineService productLineService) {
        this.productLineService = productLineService;
    }

    /**
     * Get all product lines
     * @return List of all product lines
     */
    @GetMapping
    public ResponseEntity<List<ProductLineDto>> getAllProductLines() {
        try {
            List<ProductLineDto> productLines = productLineService.getAllProductLines();
            return ResponseEntity.ok(productLines);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get product line by name
     * @param productLine Product line name
     * @return Product line if found, 404 if not found
     */
    @GetMapping("/{productLine}")
    public ResponseEntity<ProductLineDto> getProductLineById(@PathVariable String productLine) {
        try {
            ProductLineDto productLineDto = productLineService.getProductLineById(productLine);
            return ResponseEntity.ok(productLineDto);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Create a new product line
     * @param productLineDto Product line data
     * @return Created product line with 201 status
     */
    @PostMapping
    public ResponseEntity<ProductLineDto> createProductLine(@RequestBody @Valid ProductLineDto productLineDto) {
        try {
            ProductLineDto createdProductLine = productLineService.createProductLine(productLineDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProductLine);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            if (e.getMessage().contains("constraint violation")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Update an existing product line
     * @param productLine Product line name
     * @param productLineDto Updated product line data
     * @return Updated product line with 200 status
     */
    @PutMapping("/{productLine}")
    public ResponseEntity<ProductLineDto> updateProductLine(@PathVariable String productLine, 
                                                           @RequestBody @Valid ProductLineDto productLineDto) {
        try {
            ProductLineDto updatedProductLine = productLineService.updateProductLine(productLine, productLineDto);
            return ResponseEntity.ok(updatedProductLine);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            if (e.getMessage().contains("constraint violation")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Delete a product line
     * @param productLine Product line name
     * @return 204 No Content if successful
     */
    @DeleteMapping("/{productLine}")
    public ResponseEntity<Void> deleteProductLine(@PathVariable String productLine) {
        try {
            productLineService.deleteProductLine(productLine);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            if (e.getMessage().contains("existing references")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}