package com.tester.classicmodel.service;

import com.tester.classicmodel.dto.OrderDetailDto;
import com.tester.classicmodel.model.OrderDetail;
import com.tester.classicmodel.repository.OrderDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderDetailService {

    private final OrderDetailRepository orderDetailRepository;

    @Autowired
    public OrderDetailService(OrderDetailRepository orderDetailRepository) {
        this.orderDetailRepository = orderDetailRepository;
    }

    /**
     * Get all order details
     * @return List of OrderDetailDto
     */
    public List<OrderDetailDto> getAllOrderDetails() {
        try {
            List<OrderDetail> orderDetails = orderDetailRepository.findAll();
            return orderDetails.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving order details: " + e.getMessage(), e);
        }
    }

    /**
     * Get order detail by composite key (orderNumber and productCode)
     * @param orderNumber Order number
     * @param productCode Product code
     * @return OrderDetailDto if found
     * @throws RuntimeException if order detail not found
     */
    public OrderDetailDto getOrderDetailById(Integer orderNumber, String productCode) {
        if (orderNumber == null) {
            throw new IllegalArgumentException("Order number cannot be null");
        }
        if (productCode == null || productCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Product code cannot be null or empty");
        }

        try {
            Optional<OrderDetail> orderDetail = orderDetailRepository.findById(orderNumber, productCode);
            return orderDetail.map(this::convertToDto)
                    .orElseThrow(() -> new RuntimeException("OrderDetail with orderNumber " + orderNumber + 
                        " and productCode " + productCode + " not found"));
        } catch (Exception e) {
            if (e instanceof RuntimeException && e.getMessage().contains("not found")) {
                throw e;
            }
            throw new RuntimeException("Error retrieving order detail with orderNumber " + orderNumber + 
                " and productCode " + productCode + ": " + e.getMessage(), e);
        }
    }

    /**
     * Create a new order detail
     * @param orderDetailDto Order detail data
     * @return Created OrderDetailDto
     * @throws IllegalArgumentException if validation fails
     * @throws RuntimeException if database constraint violation occurs
     */
    public OrderDetailDto createOrderDetail(OrderDetailDto orderDetailDto) {
        if (orderDetailDto == null) {
            throw new IllegalArgumentException("Order detail data cannot be null");
        }

        // Validate required fields
        validateOrderDetailDto(orderDetailDto);

        try {
            OrderDetail orderDetail = convertToEntity(orderDetailDto);
            OrderDetail savedOrderDetail = orderDetailRepository.save(orderDetail);
            return convertToDto(savedOrderDetail);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Database constraint violation: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error creating order detail: " + e.getMessage(), e);
        }
    }

    /**
     * Update an existing order detail
     * @param orderNumber Order number
     * @param productCode Product code
     * @param orderDetailDto Updated order detail data
     * @return Updated OrderDetailDto
     * @throws RuntimeException if order detail not found or validation fails
     */
    public OrderDetailDto updateOrderDetail(Integer orderNumber, String productCode, OrderDetailDto orderDetailDto) {
        if (orderNumber == null) {
            throw new IllegalArgumentException("Order number cannot be null");
        }
        if (productCode == null || productCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Product code cannot be null or empty");
        }
        if (orderDetailDto == null) {
            throw new IllegalArgumentException("Order detail data cannot be null");
        }

        // Validate required fields
        validateOrderDetailDto(orderDetailDto);

        try {
            // Check if order detail exists
            Optional<OrderDetail> existingOrderDetail = orderDetailRepository.findById(orderNumber, productCode);
            if (existingOrderDetail.isEmpty()) {
                throw new RuntimeException("OrderDetail with orderNumber " + orderNumber + 
                    " and productCode " + productCode + " not found");
            }

            OrderDetail orderDetail = convertToEntity(orderDetailDto);
            orderDetail.setOrderNumber(orderNumber);
            orderDetail.setProductCode(productCode);
            OrderDetail updatedOrderDetail = orderDetailRepository.update(orderDetail);
            return convertToDto(updatedOrderDetail);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Database constraint violation: " + e.getMessage(), e);
        } catch (Exception e) {
            if (e instanceof RuntimeException && e.getMessage().contains("not found")) {
                throw e;
            }
            throw new RuntimeException("Error updating order detail with orderNumber " + orderNumber + 
                " and productCode " + productCode + ": " + e.getMessage(), e);
        }
    }

    /**
     * Delete an order detail
     * @param orderNumber Order number
     * @param productCode Product code
     * @throws RuntimeException if order detail not found
     */
    public void deleteOrderDetail(Integer orderNumber, String productCode) {
        if (orderNumber == null) {
            throw new IllegalArgumentException("Order number cannot be null");
        }
        if (productCode == null || productCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Product code cannot be null or empty");
        }

        try {
            // Check if order detail exists
            Optional<OrderDetail> existingOrderDetail = orderDetailRepository.findById(orderNumber, productCode);
            if (existingOrderDetail.isEmpty()) {
                throw new RuntimeException("OrderDetail with orderNumber " + orderNumber + 
                    " and productCode " + productCode + " not found");
            }

            orderDetailRepository.deleteById(orderNumber, productCode);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Cannot delete order detail due to existing references: " + e.getMessage(), e);
        } catch (Exception e) {
            if (e instanceof RuntimeException && e.getMessage().contains("not found")) {
                throw e;
            }
            throw new RuntimeException("Error deleting order detail with orderNumber " + orderNumber + 
                " and productCode " + productCode + ": " + e.getMessage(), e);
        }
    }

    /**
     * Convert OrderDetail entity to OrderDetailDto
     * @param orderDetail OrderDetail entity
     * @return OrderDetailDto
     */
    private OrderDetailDto convertToDto(OrderDetail orderDetail) {
        OrderDetailDto dto = new OrderDetailDto();
        dto.setOrderNumber(orderDetail.getOrderNumber());
        dto.setProductCode(orderDetail.getProductCode());
        dto.setQuantityOrdered(orderDetail.getQuantityOrdered());
        dto.setPriceEach(orderDetail.getPriceEach());
        dto.setOrderLineNumber(orderDetail.getOrderLineNumber());
        return dto;
    }

    /**
     * Convert OrderDetailDto to OrderDetail entity
     * @param dto OrderDetailDto
     * @return OrderDetail entity
     */
    private OrderDetail convertToEntity(OrderDetailDto dto) {
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrderNumber(dto.getOrderNumber());
        orderDetail.setProductCode(dto.getProductCode());
        orderDetail.setQuantityOrdered(dto.getQuantityOrdered());
        orderDetail.setPriceEach(dto.getPriceEach());
        orderDetail.setOrderLineNumber(dto.getOrderLineNumber());
        return orderDetail;
    }

    /**
     * Validate OrderDetailDto required fields
     * @param orderDetailDto OrderDetailDto to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateOrderDetailDto(OrderDetailDto orderDetailDto) {
        if (orderDetailDto.getOrderNumber() == null) {
            throw new IllegalArgumentException("Order number is required");
        }
        if (orderDetailDto.getProductCode() == null || orderDetailDto.getProductCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Product code is required");
        }
        if (orderDetailDto.getQuantityOrdered() == null) {
            throw new IllegalArgumentException("Quantity ordered is required");
        }
        if (orderDetailDto.getPriceEach() == null) {
            throw new IllegalArgumentException("Price each is required");
        }
        if (orderDetailDto.getOrderLineNumber() == null) {
            throw new IllegalArgumentException("Order line number is required");
        }
        
        // Validate field constraints
        if (orderDetailDto.getQuantityOrdered() < 1) {
            throw new IllegalArgumentException("Quantity ordered must be at least 1");
        }
        if (orderDetailDto.getPriceEach().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price each must be positive");
        }
        if (orderDetailDto.getOrderLineNumber() < 1) {
            throw new IllegalArgumentException("Order line number must be at least 1");
        }
        if (orderDetailDto.getProductCode().length() > 15) {
            throw new IllegalArgumentException("Product code must not exceed 15 characters");
        }
    }
}