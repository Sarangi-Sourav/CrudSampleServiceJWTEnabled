package com.tester.classicmodel.controller;

import com.tester.classicmodel.dto.OrderDetailDto;
import com.tester.classicmodel.service.OrderDetailService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orderdetails")
public class OrderDetailController {

    private final OrderDetailService orderDetailService;

    @Autowired
    public OrderDetailController(OrderDetailService orderDetailService) {
        this.orderDetailService = orderDetailService;
    }

    /**
     * Get all order details
     * @return List of all order details
     */
    @GetMapping
    public ResponseEntity<List<OrderDetailDto>> getAllOrderDetails() {
        try {
            List<OrderDetailDto> orderDetails = orderDetailService.getAllOrderDetails();
            return ResponseEntity.ok(orderDetails);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get order detail by composite key (orderNumber and productCode)
     * @param orderNumber Order number
     * @param productCode Product code
     * @return Order detail if found, 404 if not found
     */
    @GetMapping("/{orderNumber}/{productCode}")
    public ResponseEntity<OrderDetailDto> getOrderDetailById(@PathVariable Integer orderNumber, 
                                                            @PathVariable String productCode) {
        try {
            OrderDetailDto orderDetail = orderDetailService.getOrderDetailById(orderNumber, productCode);
            return ResponseEntity.ok(orderDetail);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Create a new order detail
     * @param orderDetailDto Order detail data
     * @return Created order detail with 201 status
     */
    @PostMapping
    public ResponseEntity<OrderDetailDto> createOrderDetail(@RequestBody @Valid OrderDetailDto orderDetailDto) {
        try {
            OrderDetailDto createdOrderDetail = orderDetailService.createOrderDetail(orderDetailDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdOrderDetail);
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
     * Update an existing order detail
     * @param orderNumber Order number
     * @param productCode Product code
     * @param orderDetailDto Updated order detail data
     * @return Updated order detail with 200 status
     */
    @PutMapping("/{orderNumber}/{productCode}")
    public ResponseEntity<OrderDetailDto> updateOrderDetail(@PathVariable Integer orderNumber,
                                                           @PathVariable String productCode,
                                                           @RequestBody @Valid OrderDetailDto orderDetailDto) {
        try {
            OrderDetailDto updatedOrderDetail = orderDetailService.updateOrderDetail(orderNumber, productCode, orderDetailDto);
            return ResponseEntity.ok(updatedOrderDetail);
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
     * Delete an order detail
     * @param orderNumber Order number
     * @param productCode Product code
     * @return 204 No Content if successful
     */
    @DeleteMapping("/{orderNumber}/{productCode}")
    public ResponseEntity<Void> deleteOrderDetail(@PathVariable Integer orderNumber, 
                                                 @PathVariable String productCode) {
        try {
            orderDetailService.deleteOrderDetail(orderNumber, productCode);
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