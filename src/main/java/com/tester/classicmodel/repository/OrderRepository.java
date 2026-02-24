package com.tester.classicmodel.repository;

import com.tester.classicmodel.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class OrderRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public OrderRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // SQL Queries
    private static final String FIND_ALL_SQL = 
        "SELECT orderNumber, orderDate, requiredDate, shippedDate, " +
        "status, comments, customerNumber FROM orders";

    private static final String FIND_BY_ID_SQL = FIND_ALL_SQL + " WHERE orderNumber = ?";

    private static final String INSERT_SQL = 
        "INSERT INTO orders (orderDate, requiredDate, shippedDate, " +
        "status, comments, customerNumber) VALUES (?, ?, ?, ?, ?, ?)";

    private static final String UPDATE_SQL = 
        "UPDATE orders SET orderDate = ?, requiredDate = ?, shippedDate = ?, " +
        "status = ?, comments = ?, customerNumber = ? WHERE orderNumber = ?";

    private static final String DELETE_SQL = "DELETE FROM orders WHERE orderNumber = ?";

    // RowMapper for Order
    private static class OrderRowMapper implements RowMapper<Order> {
        @Override
        public Order mapRow(ResultSet rs, int rowNum) throws SQLException {
            Order order = new Order();
            order.setOrderNumber(rs.getInt("orderNumber"));
            
            // Handle LocalDate conversion
            Date orderDate = rs.getDate("orderDate");
            if (orderDate != null) {
                order.setOrderDate(orderDate.toLocalDate());
            }
            
            Date requiredDate = rs.getDate("requiredDate");
            if (requiredDate != null) {
                order.setRequiredDate(requiredDate.toLocalDate());
            }
            
            Date shippedDate = rs.getDate("shippedDate");
            if (shippedDate != null) {
                order.setShippedDate(shippedDate.toLocalDate());
            }
            
            order.setStatus(rs.getString("status"));
            order.setComments(rs.getString("comments"));
            order.setCustomerNumber(rs.getInt("customerNumber"));
            
            return order;
        }
    }

    private final OrderRowMapper orderRowMapper = new OrderRowMapper();

    // CRUD Operations
    public List<Order> findAll() {
        return jdbcTemplate.query(FIND_ALL_SQL, orderRowMapper);
    }

    public Optional<Order> findById(Integer orderNumber) {
        try {
            Order order = jdbcTemplate.queryForObject(FIND_BY_ID_SQL, orderRowMapper, orderNumber);
            return Optional.ofNullable(order);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Order save(Order order) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
            
            // Handle LocalDate to Date conversion
            if (order.getOrderDate() != null) {
                ps.setDate(1, Date.valueOf(order.getOrderDate()));
            } else {
                ps.setNull(1, java.sql.Types.DATE);
            }
            
            if (order.getRequiredDate() != null) {
                ps.setDate(2, Date.valueOf(order.getRequiredDate()));
            } else {
                ps.setNull(2, java.sql.Types.DATE);
            }
            
            if (order.getShippedDate() != null) {
                ps.setDate(3, Date.valueOf(order.getShippedDate()));
            } else {
                ps.setNull(3, java.sql.Types.DATE);
            }
            
            ps.setString(4, order.getStatus());
            ps.setString(5, order.getComments());
            ps.setInt(6, order.getCustomerNumber());
            
            return ps;
        }, keyHolder);

        // Set the generated order number
        Number generatedKey = keyHolder.getKey();
        if (generatedKey != null) {
            order.setOrderNumber(generatedKey.intValue());
        }
        
        return order;
    }

    public Order update(Order order) {
        int rowsAffected = jdbcTemplate.update(UPDATE_SQL,
            order.getOrderDate() != null ? Date.valueOf(order.getOrderDate()) : null,
            order.getRequiredDate() != null ? Date.valueOf(order.getRequiredDate()) : null,
            order.getShippedDate() != null ? Date.valueOf(order.getShippedDate()) : null,
            order.getStatus(),
            order.getComments(),
            order.getCustomerNumber(),
            order.getOrderNumber()
        );
        
        if (rowsAffected == 0) {
            throw new RuntimeException("Order with ID " + order.getOrderNumber() + " not found for update");
        }
        
        return order;
    }

    public void deleteById(Integer orderNumber) {
        int rowsAffected = jdbcTemplate.update(DELETE_SQL, orderNumber);
        if (rowsAffected == 0) {
            throw new RuntimeException("Order with ID " + orderNumber + " not found for deletion");
        }
    }
}