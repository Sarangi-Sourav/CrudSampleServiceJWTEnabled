package com.tester.classicmodel.service;

import com.tester.classicmodel.dto.PaymentDto;
import com.tester.classicmodel.model.Payment;
import com.tester.classicmodel.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    @Autowired
    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    /**
     * Get all payments
     * @return List of PaymentDto
     */
    public List<PaymentDto> getAllPayments() {
        try {
            List<Payment> payments = paymentRepository.findAll();
            return payments.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving payments: " + e.getMessage(), e);
        }
    }

    /**
     * Get payment by composite key (customerNumber and checkNumber)
     * @param customerNumber Customer number
     * @param checkNumber Check number
     * @return PaymentDto if found
     * @throws RuntimeException if payment not found
     */
    public PaymentDto getPaymentById(Integer customerNumber, String checkNumber) {
        if (customerNumber == null) {
            throw new IllegalArgumentException("Customer number cannot be null");
        }
        if (checkNumber == null || checkNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Check number cannot be null or empty");
        }

        try {
            Optional<Payment> payment = paymentRepository.findById(customerNumber, checkNumber);
            return payment.map(this::convertToDto)
                    .orElseThrow(() -> new RuntimeException("Payment with customerNumber " + customerNumber + 
                        " and checkNumber " + checkNumber + " not found"));
        } catch (Exception e) {
            if (e instanceof RuntimeException && e.getMessage().contains("not found")) {
                throw e;
            }
            throw new RuntimeException("Error retrieving payment with customerNumber " + customerNumber + 
                " and checkNumber " + checkNumber + ": " + e.getMessage(), e);
        }
    }

    /**
     * Create a new payment
     * @param paymentDto Payment data
     * @return Created PaymentDto
     * @throws IllegalArgumentException if validation fails
     * @throws RuntimeException if database constraint violation occurs
     */
    public PaymentDto createPayment(PaymentDto paymentDto) {
        if (paymentDto == null) {
            throw new IllegalArgumentException("Payment data cannot be null");
        }

        // Validate required fields
        validatePaymentDto(paymentDto);

        try {
            Payment payment = convertToEntity(paymentDto);
            Payment savedPayment = paymentRepository.save(payment);
            return convertToDto(savedPayment);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Database constraint violation: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error creating payment: " + e.getMessage(), e);
        }
    }

    /**
     * Update an existing payment
     * @param customerNumber Customer number
     * @param checkNumber Check number
     * @param paymentDto Updated payment data
     * @return Updated PaymentDto
     * @throws RuntimeException if payment not found or validation fails
     */
    public PaymentDto updatePayment(Integer customerNumber, String checkNumber, PaymentDto paymentDto) {
        if (customerNumber == null) {
            throw new IllegalArgumentException("Customer number cannot be null");
        }
        if (checkNumber == null || checkNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Check number cannot be null or empty");
        }
        if (paymentDto == null) {
            throw new IllegalArgumentException("Payment data cannot be null");
        }

        // Validate required fields
        validatePaymentDto(paymentDto);

        try {
            // Check if payment exists
            Optional<Payment> existingPayment = paymentRepository.findById(customerNumber, checkNumber);
            if (existingPayment.isEmpty()) {
                throw new RuntimeException("Payment with customerNumber " + customerNumber + 
                    " and checkNumber " + checkNumber + " not found");
            }

            Payment payment = convertToEntity(paymentDto);
            payment.setCustomerNumber(customerNumber);
            payment.setCheckNumber(checkNumber);
            Payment updatedPayment = paymentRepository.update(payment);
            return convertToDto(updatedPayment);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Database constraint violation: " + e.getMessage(), e);
        } catch (Exception e) {
            if (e instanceof RuntimeException && e.getMessage().contains("not found")) {
                throw e;
            }
            throw new RuntimeException("Error updating payment with customerNumber " + customerNumber + 
                " and checkNumber " + checkNumber + ": " + e.getMessage(), e);
        }
    }

    /**
     * Delete a payment
     * @param customerNumber Customer number
     * @param checkNumber Check number
     * @throws RuntimeException if payment not found
     */
    public void deletePayment(Integer customerNumber, String checkNumber) {
        if (customerNumber == null) {
            throw new IllegalArgumentException("Customer number cannot be null");
        }
        if (checkNumber == null || checkNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Check number cannot be null or empty");
        }

        try {
            // Check if payment exists
            Optional<Payment> existingPayment = paymentRepository.findById(customerNumber, checkNumber);
            if (existingPayment.isEmpty()) {
                throw new RuntimeException("Payment with customerNumber " + customerNumber + 
                    " and checkNumber " + checkNumber + " not found");
            }

            paymentRepository.deleteById(customerNumber, checkNumber);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Cannot delete payment due to existing references: " + e.getMessage(), e);
        } catch (Exception e) {
            if (e instanceof RuntimeException && e.getMessage().contains("not found")) {
                throw e;
            }
            throw new RuntimeException("Error deleting payment with customerNumber " + customerNumber + 
                " and checkNumber " + checkNumber + ": " + e.getMessage(), e);
        }
    }

    /**
     * Convert Payment entity to PaymentDto
     * @param payment Payment entity
     * @return PaymentDto
     */
    private PaymentDto convertToDto(Payment payment) {
        PaymentDto dto = new PaymentDto();
        dto.setCustomerNumber(payment.getCustomerNumber());
        dto.setCheckNumber(payment.getCheckNumber());
        dto.setPaymentDate(payment.getPaymentDate());
        dto.setAmount(payment.getAmount());
        return dto;
    }

    /**
     * Convert PaymentDto to Payment entity
     * @param dto PaymentDto
     * @return Payment entity
     */
    private Payment convertToEntity(PaymentDto dto) {
        Payment payment = new Payment();
        payment.setCustomerNumber(dto.getCustomerNumber());
        payment.setCheckNumber(dto.getCheckNumber());
        payment.setPaymentDate(dto.getPaymentDate());
        payment.setAmount(dto.getAmount());
        return payment;
    }

    /**
     * Validate PaymentDto required fields
     * @param paymentDto PaymentDto to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validatePaymentDto(PaymentDto paymentDto) {
        if (paymentDto.getCustomerNumber() == null) {
            throw new IllegalArgumentException("Customer number is required");
        }
        if (paymentDto.getCheckNumber() == null || paymentDto.getCheckNumber().trim().isEmpty()) {
            throw new IllegalArgumentException("Check number is required");
        }
        if (paymentDto.getPaymentDate() == null) {
            throw new IllegalArgumentException("Payment date is required");
        }
        if (paymentDto.getAmount() == null) {
            throw new IllegalArgumentException("Amount is required");
        }
        
        // Validate field constraints
        if (paymentDto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (paymentDto.getCheckNumber().length() > 50) {
            throw new IllegalArgumentException("Check number must not exceed 50 characters");
        }
        
        // Validate payment date is not in the future
        if (paymentDto.getPaymentDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Payment date cannot be in the future");
        }
        
        // Validate reasonable amount limits (business rule)
        BigDecimal maxAmount = new BigDecimal("1000000.00"); // 1 million limit
        if (paymentDto.getAmount().compareTo(maxAmount) > 0) {
            throw new IllegalArgumentException("Payment amount cannot exceed $1,000,000.00");
        }
    }
}