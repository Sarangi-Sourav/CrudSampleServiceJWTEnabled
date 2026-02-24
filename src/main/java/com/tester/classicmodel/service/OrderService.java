package com.tester.classicmodel.service;

import com.tester.classicmodel.dto.OrderDto;
import com.tester.classicmodel.model.Order;
import com.tester.classicmodel.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    /**
     * Get all orders
     * @return List of OrderDto
     */
    public List<OrderDto> getAllOrders() {
        try {
            List<Order> orders = orderRepository.findAll();
            return orders.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving orders: " + e.getMessage(), e);
        }
    }

    /**
     * Get order by ID
     * @param orderNumber Order ID
     * @return OrderDto if found
     * @throws RuntimeException if order not found
     */
    public OrderDto getOrderById(Integer orderNumber) {
        if (orderNumber == null) {
            throw new IllegalArgumentException("Order number cannot be null");
        }

        try {
            Optional<Order> order = orderRepository.findById(orderNumber);
            return order.map(this::convertToDto)
                    .orElseThrow(() -> new RuntimeException("Order with ID " + orderNumber + " not found"));
        } catch (Exception e) {
            if (e instanceof RuntimeException && e.getMessage().contains("not found")) {
                throw e;
            }
            throw new RuntimeException("Error retrieving order with ID " + orderNumber + ": " + e.getMessage(), e);
        }
    }

    /**
     * Create a new order
     * @param orderDto Order data
     * @return Created OrderDto
     * @throws IllegalArgumentException if validation fails
     * @throws RuntimeException if database constraint violation occurs
     */
    public OrderDto createOrder(OrderDto orderDto) {
        if (orderDto == null) {
            throw new IllegalArgumentException("Order data cannot be null");
        }

        // Validate required fields
        validateOrderDto(orderDto);

        try {
            Order order = convertToEntity(orderDto);
            Order savedOrder = orderRepository.save(order);
            return convertToDto(savedOrder);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Database constraint violation: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error creating order: " + e.getMessage(), e);
        }
    }

    /**
     * Update an existing order
     * @param orderNumber Order ID
     * @param orderDto Updated order data
     * @return Updated OrderDto
     * @throws RuntimeException if order not found or validation fails
     */
    public OrderDto updateOrder(Integer orderNumber, OrderDto orderDto) {
        if (orderNumber == null) {
            throw new IllegalArgumentException("Order number cannot be null");
        }
        if (orderDto == null) {
            throw new IllegalArgumentException("Order data cannot be null");
        }

        // Validate required fields
        validateOrderDto(orderDto);

        try {
            // Check if order exists
            Optional<Order> existingOrder = orderRepository.findById(orderNumber);
            if (existingOrder.isEmpty()) {
                throw new RuntimeException("Order with ID " + orderNumber + " not found");
            }

            Order order = convertToEntity(orderDto);
            order.setOrderNumber(orderNumber);
            Order updatedOrder = orderRepository.update(order);
            return convertToDto(updatedOrder);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Database constraint violation: " + e.getMessage(), e);
        } catch (Exception e) {
            if (e instanceof RuntimeException && e.getMessage().contains("not found")) {
                throw e;
            }
            throw new RuntimeException("Error updating order with ID " + orderNumber + ": " + e.getMessage(), e);
        }
    }

    /**
     * Delete an order
     * @param orderNumber Order ID
     * @throws RuntimeException if order not found
     */
    public void deleteOrder(Integer orderNumber) {
        if (orderNumber == null) {
            throw new IllegalArgumentException("Order number cannot be null");
        }

        try {
            // Check if order exists
            Optional<Order> existingOrder = orderRepository.findById(orderNumber);
            if (existingOrder.isEmpty()) {
                throw new RuntimeException("Order with ID " + orderNumber + " not found");
            }

            orderRepository.deleteById(orderNumber);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Cannot delete order due to existing references: " + e.getMessage(), e);
        } catch (Exception e) {
            if (e instanceof RuntimeException && e.getMessage().contains("not found")) {
                throw e;
            }
            throw new RuntimeException("Error deleting order with ID " + orderNumber + ": " + e.getMessage(), e);
        }
    }

    /**
     * Convert Order entity to OrderDto
     * @param order Order entity
     * @return OrderDto
     */
    private OrderDto convertToDto(Order order) {
        OrderDto dto = new OrderDto();
        dto.setOrderNumber(order.getOrderNumber());
        dto.setOrderDate(order.getOrderDate());
        dto.setRequiredDate(order.getRequiredDate());
        dto.setShippedDate(order.getShippedDate());
        dto.setStatus(order.getStatus());
        dto.setComments(order.getComments());
        dto.setCustomerNumber(order.getCustomerNumber());
        return dto;
    }

    /**
     * Convert OrderDto to Order entity
     * @param dto OrderDto
     * @return Order entity
     */
    private Order convertToEntity(OrderDto dto) {
        Order order = new Order();
        order.setOrderNumber(dto.getOrderNumber());
        order.setOrderDate(dto.getOrderDate());
        order.setRequiredDate(dto.getRequiredDate());
        order.setShippedDate(dto.getShippedDate());
        order.setStatus(dto.getStatus());
        order.setComments(dto.getComments());
        order.setCustomerNumber(dto.getCustomerNumber());
        return order;
    }

    /**
     * Validate OrderDto required fields
     * @param orderDto OrderDto to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateOrderDto(OrderDto orderDto) {
        if (orderDto.getOrderDate() == null) {
            throw new IllegalArgumentException("Order date is required");
        }
        if (orderDto.getRequiredDate() == null) {
            throw new IllegalArgumentException("Required date is required");
        }
        if (orderDto.getStatus() == null || orderDto.getStatus().trim().isEmpty()) {
            throw new IllegalArgumentException("Status is required");
        }
        if (orderDto.getCustomerNumber() == null) {
            throw new IllegalArgumentException("Customer number is required");
        }
        
        // Validate field lengths
        if (orderDto.getStatus().length() > 15) {
            throw new IllegalArgumentException("Status must not exceed 15 characters");
        }
        if (orderDto.getComments() != null && orderDto.getComments().length() > 255) {
            throw new IllegalArgumentException("Comments must not exceed 255 characters");
        }
        
        // Validate date logic
        if (orderDto.getRequiredDate().isBefore(orderDto.getOrderDate())) {
            throw new IllegalArgumentException("Required date cannot be before order date");
        }
        if (orderDto.getShippedDate() != null && orderDto.getShippedDate().isBefore(orderDto.getOrderDate())) {
            throw new IllegalArgumentException("Shipped date cannot be before order date");
        }
        
        // Validate status values (common order statuses)
        String status = orderDto.getStatus().toUpperCase();
        if (!isValidOrderStatus(status)) {
            throw new IllegalArgumentException("Invalid order status. Valid statuses are: In Process, Shipped, Cancelled, On Hold, Resolved, Disputed");
        }
    }

    /**
     * Check if order status is valid
     * @param status Order status
     * @return true if valid, false otherwise
     */
    private boolean isValidOrderStatus(String status) {
        return status.equals("IN PROCESS") || 
               status.equals("SHIPPED") || 
               status.equals("CANCELLED") || 
               status.equals("ON HOLD") || 
               status.equals("RESOLVED") || 
               status.equals("DISPUTED");
    }

    public void helloWorld(){
        System.out.println("Form "+provideTheName()+": Hello World");
    }

    private String provideTheName(){
        return "Code-analyzer";
    }
}