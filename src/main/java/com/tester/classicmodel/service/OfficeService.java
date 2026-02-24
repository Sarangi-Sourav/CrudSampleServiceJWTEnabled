package com.tester.classicmodel.service;

import com.tester.classicmodel.dto.OfficeDto;
import com.tester.classicmodel.model.Office;
import com.tester.classicmodel.repository.OfficeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OfficeService {

    private final OfficeRepository officeRepository;

    @Autowired
    public OfficeService(OfficeRepository officeRepository) {
        this.officeRepository = officeRepository;
    }

    /**
     * Get all offices
     * @return List of OfficeDto
     */
    public List<OfficeDto> getAllOffices() {
        try {
            List<Office> offices = officeRepository.findAll();
            return offices.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving offices: " + e.getMessage(), e);
        }
    }

    /**
     * Get office by code
     * @param officeCode Office code
     * @return OfficeDto if found
     * @throws RuntimeException if office not found
     */
    public OfficeDto getOfficeById(String officeCode) {
        if (officeCode == null || officeCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Office code cannot be null or empty");
        }

        try {
            Optional<Office> office = officeRepository.findById(officeCode);
            return office.map(this::convertToDto)
                    .orElseThrow(() -> new RuntimeException("Office with code " + officeCode + " not found"));
        } catch (Exception e) {
            if (e instanceof RuntimeException && e.getMessage().contains("not found")) {
                throw e;
            }
            throw new RuntimeException("Error retrieving office with code " + officeCode + ": " + e.getMessage(), e);
        }
    }

    /**
     * Create a new office
     * @param officeDto Office data
     * @return Created OfficeDto
     * @throws IllegalArgumentException if validation fails
     * @throws RuntimeException if database constraint violation occurs
     */
    public OfficeDto createOffice(OfficeDto officeDto) {
        if (officeDto == null) {
            throw new IllegalArgumentException("Office data cannot be null");
        }

        // Validate required fields
        validateOfficeDto(officeDto);

        try {
            Office office = convertToEntity(officeDto);
            Office savedOffice = officeRepository.save(office);
            return convertToDto(savedOffice);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Database constraint violation: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error creating office: " + e.getMessage(), e);
        }
    }

    /**
     * Update an existing office
     * @param officeCode Office code
     * @param officeDto Updated office data
     * @return Updated OfficeDto
     * @throws RuntimeException if office not found or validation fails
     */
    public OfficeDto updateOffice(String officeCode, OfficeDto officeDto) {
        if (officeCode == null || officeCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Office code cannot be null or empty");
        }
        if (officeDto == null) {
            throw new IllegalArgumentException("Office data cannot be null");
        }

        // Validate required fields
        validateOfficeDto(officeDto);

        try {
            // Check if office exists
            Optional<Office> existingOffice = officeRepository.findById(officeCode);
            if (existingOffice.isEmpty()) {
                throw new RuntimeException("Office with code " + officeCode + " not found");
            }

            Office office = convertToEntity(officeDto);
            office.setOfficeCode(officeCode);
            Office updatedOffice = officeRepository.update(office);
            return convertToDto(updatedOffice);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Database constraint violation: " + e.getMessage(), e);
        } catch (Exception e) {
            if (e instanceof RuntimeException && e.getMessage().contains("not found")) {
                throw e;
            }
            throw new RuntimeException("Error updating office with code " + officeCode + ": " + e.getMessage(), e);
        }
    }

    /**
     * Delete an office
     * @param officeCode Office code
     * @throws RuntimeException if office not found
     */
    public void deleteOffice(String officeCode) {
        if (officeCode == null || officeCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Office code cannot be null or empty");
        }

        try {
            // Check if office exists
            Optional<Office> existingOffice = officeRepository.findById(officeCode);
            if (existingOffice.isEmpty()) {
                throw new RuntimeException("Office with code " + officeCode + " not found");
            }

            officeRepository.deleteById(officeCode);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Cannot delete office due to existing references: " + e.getMessage(), e);
        } catch (Exception e) {
            if (e instanceof RuntimeException && e.getMessage().contains("not found")) {
                throw e;
            }
            throw new RuntimeException("Error deleting office with code " + officeCode + ": " + e.getMessage(), e);
        }
    }

    /**
     * Convert Office entity to OfficeDto
     * @param office Office entity
     * @return OfficeDto
     */
    private OfficeDto convertToDto(Office office) {
        OfficeDto dto = new OfficeDto();
        dto.setOfficeCode(office.getOfficeCode());
        dto.setCity(office.getCity());
        dto.setPhone(office.getPhone());
        dto.setAddressLine1(office.getAddressLine1());
        dto.setAddressLine2(office.getAddressLine2());
        dto.setState(office.getState());
        dto.setCountry(office.getCountry());
        dto.setPostalCode(office.getPostalCode());
        dto.setTerritory(office.getTerritory());
        return dto;
    }

    /**
     * Convert OfficeDto to Office entity
     * @param dto OfficeDto
     * @return Office entity
     */
    private Office convertToEntity(OfficeDto dto) {
        Office office = new Office();
        office.setOfficeCode(dto.getOfficeCode());
        office.setCity(dto.getCity());
        office.setPhone(dto.getPhone());
        office.setAddressLine1(dto.getAddressLine1());
        office.setAddressLine2(dto.getAddressLine2());
        office.setState(dto.getState());
        office.setCountry(dto.getCountry());
        office.setPostalCode(dto.getPostalCode());
        office.setTerritory(dto.getTerritory());
        return office;
    }

    /**
     * Validate OfficeDto required fields
     * @param officeDto OfficeDto to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateOfficeDto(OfficeDto officeDto) {
        if (officeDto.getOfficeCode() == null || officeDto.getOfficeCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Office code is required");
        }
        if (officeDto.getCity() == null || officeDto.getCity().trim().isEmpty()) {
            throw new IllegalArgumentException("City is required");
        }
        if (officeDto.getPhone() == null || officeDto.getPhone().trim().isEmpty()) {
            throw new IllegalArgumentException("Phone is required");
        }
        if (officeDto.getAddressLine1() == null || officeDto.getAddressLine1().trim().isEmpty()) {
            throw new IllegalArgumentException("Address line 1 is required");
        }
        if (officeDto.getCountry() == null || officeDto.getCountry().trim().isEmpty()) {
            throw new IllegalArgumentException("Country is required");
        }
        if (officeDto.getTerritory() == null || officeDto.getTerritory().trim().isEmpty()) {
            throw new IllegalArgumentException("Territory is required");
        }
        
        // Validate field lengths
        if (officeDto.getOfficeCode().length() > 10) {
            throw new IllegalArgumentException("Office code must not exceed 10 characters");
        }
        if (officeDto.getCity().length() > 50) {
            throw new IllegalArgumentException("City must not exceed 50 characters");
        }
        if (officeDto.getPhone().length() > 50) {
            throw new IllegalArgumentException("Phone must not exceed 50 characters");
        }
        if (officeDto.getAddressLine1().length() > 50) {
            throw new IllegalArgumentException("Address line 1 must not exceed 50 characters");
        }
        if (officeDto.getCountry().length() > 50) {
            throw new IllegalArgumentException("Country must not exceed 50 characters");
        }
        if (officeDto.getTerritory().length() > 10) {
            throw new IllegalArgumentException("Territory must not exceed 10 characters");
        }
        if (officeDto.getAddressLine2() != null && officeDto.getAddressLine2().length() > 50) {
            throw new IllegalArgumentException("Address line 2 must not exceed 50 characters");
        }
        if (officeDto.getState() != null && officeDto.getState().length() > 50) {
            throw new IllegalArgumentException("State must not exceed 50 characters");
        }
        if (officeDto.getPostalCode() != null && officeDto.getPostalCode().length() > 15) {
            throw new IllegalArgumentException("Postal code must not exceed 15 characters");
        }
    }
}