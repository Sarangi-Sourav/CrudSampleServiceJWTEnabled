package com.tester.classicmodel.controller;

import com.tester.classicmodel.dto.PaymentDto;
import com.tester.classicmodel.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /**
     * Get all payments
     * @return List of all payments
     */
    @GetMapping
    public ResponseEntity<List<PaymentDto>> getAllPayments() {
        try {
            List<PaymentDto> payments = paymentService.getAllPayments();
            return ResponseEntity.ok(payments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get payment by composite key (customerNumber and checkNumber)
     * @param customerNumber Customer number
     * @param checkNumber Check number
     * @return Payment if found, 404 if not found
     */
    @GetMapping("/{customerNumber}/{checkNumber}")
    public ResponseEntity<PaymentDto> getPaymentById(@PathVariable Integer customerNumber, 
                                                    @PathVariable String checkNumber) {
        try {
            PaymentDto payment = paymentService.getPaymentById(customerNumber, checkNumber);
            return ResponseEntity.ok(payment);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Create a new payment
     * @param paymentDto Payment data
     * @return Created payment with 201 status
     */
    @PostMapping
    public ResponseEntity<PaymentDto> createPayment(@RequestBody @Valid PaymentDto paymentDto) {
        try {
            PaymentDto createdPayment = paymentService.createPayment(paymentDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdPayment);
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
     * Update an existing payment
     * @param customerNumber Customer number
     * @param checkNumber Check number
     * @param paymentDto Updated payment data
     * @return Updated payment with 200 status
     */
    @PutMapping("/{customerNumber}/{checkNumber}")
    public ResponseEntity<PaymentDto> updatePayment(@PathVariable Integer customerNumber,
                                                   @PathVariable String checkNumber,
                                                   @RequestBody @Valid PaymentDto paymentDto) {
        try {
            PaymentDto updatedPayment = paymentService.updatePayment(customerNumber, checkNumber, paymentDto);
            return ResponseEntity.ok(updatedPayment);
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
     * Delete a payment
     * @param customerNumber Customer number
     * @param checkNumber Check number
     * @return 204 No Content if successful
     */
    @DeleteMapping("/{customerNumber}/{checkNumber}")
    public ResponseEntity<Void> deletePayment(@PathVariable Integer customerNumber, 
                                             @PathVariable String checkNumber) {
        try {
            paymentService.deletePayment(customerNumber, checkNumber);
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