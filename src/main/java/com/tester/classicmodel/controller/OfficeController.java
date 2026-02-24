package com.tester.classicmodel.controller;

import com.tester.classicmodel.dto.OfficeDto;
import com.tester.classicmodel.service.OfficeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/offices")
public class OfficeController {

    private final OfficeService officeService;

    @Autowired
    public OfficeController(OfficeService officeService) {
        this.officeService = officeService;
    }

    /**
     * Get all offices
     * @return List of all offices
     */
    @GetMapping
    public ResponseEntity<List<OfficeDto>> getAllOffices() {
        try {
            List<OfficeDto> offices = officeService.getAllOffices();
            return ResponseEntity.ok(offices);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get office by code
     * @param officeCode Office code
     * @return Office if found, 404 if not found
     */
    @GetMapping("/{officeCode}")
    public ResponseEntity<OfficeDto> getOfficeById(@PathVariable String officeCode) {
        try {
            OfficeDto office = officeService.getOfficeById(officeCode);
            return ResponseEntity.ok(office);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Create a new office
     * @param officeDto Office data
     * @return Created office with 201 status
     */
    @PostMapping
    public ResponseEntity<OfficeDto> createOffice(@RequestBody @Valid OfficeDto officeDto) {
        try {
            OfficeDto createdOffice = officeService.createOffice(officeDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdOffice);
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
     * Update an existing office
     * @param officeCode Office code
     * @param officeDto Updated office data
     * @return Updated office with 200 status
     */
    @PutMapping("/{officeCode}")
    public ResponseEntity<OfficeDto> updateOffice(@PathVariable String officeCode, 
                                                 @RequestBody @Valid OfficeDto officeDto) {
        try {
            OfficeDto updatedOffice = officeService.updateOffice(officeCode, officeDto);
            return ResponseEntity.ok(updatedOffice);
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
     * Delete an office
     * @param officeCode Office code
     * @return 204 No Content if successful
     */
    @DeleteMapping("/{officeCode}")
    public ResponseEntity<Void> deleteOffice(@PathVariable String officeCode) {
        try {
            officeService.deleteOffice(officeCode);
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