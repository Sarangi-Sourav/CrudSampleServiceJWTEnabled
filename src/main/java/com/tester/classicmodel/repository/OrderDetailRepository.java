package com.tester.classicmodel.repository;

import com.tester.classicmodel.model.OrderDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class OrderDetailRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public OrderDetailRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // SQL Queries
    private static final String FIND_ALL_SQL = 
        "SELECT orderNumber, productCode, quantityOrdered, priceEach, orderLineNumber FROM orderdetails";

    private static final String FIND_BY_ID_SQL = FIND_ALL_SQL + " WHERE orderNumber = ? AND productCode = ?";

    private static final String INSERT_SQL = 
        "INSERT INTO orderdetails (orderNumber, productCode, quantityOrdered, priceEach, orderLineNumber) " +
        "VALUES (?, ?, ?, ?, ?)";

    private static final String UPDATE_SQL = 
        "UPDATE orderdetails SET quantityOrdered = ?, priceEach = ?, orderLineNumber = ? " +
        "WHERE orderNumber = ? AND productCode = ?";

    private static final String DELETE_SQL = "DELETE FROM orderdetails WHERE orderNumber = ? AND productCode = ?";

    // RowMapper for OrderDetail
    private static class OrderDetailRowMapper implements RowMapper<OrderDetail> {
        @Override
        public OrderDetail mapRow(ResultSet rs, int rowNum) throws SQLException {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderNumber(rs.getInt("orderNumber"));
            orderDetail.setProductCode(rs.getString("productCode"));
            orderDetail.setQuantityOrdered(rs.getInt("quantityOrdered"));
            orderDetail.setPriceEach(rs.getBigDecimal("priceEach"));
            orderDetail.setOrderLineNumber(rs.getShort("orderLineNumber"));
            return orderDetail;
        }
    }

    private final OrderDetailRowMapper orderDetailRowMapper = new OrderDetailRowMapper();

    // CRUD Operations
    public List<OrderDetail> findAll() {
        return jdbcTemplate.query(FIND_ALL_SQL, orderDetailRowMapper);
    }

    public Optional<OrderDetail> findById(Integer orderNumber, String productCode) {
        try {
            OrderDetail orderDetail = jdbcTemplate.queryForObject(FIND_BY_ID_SQL, orderDetailRowMapper, orderNumber, productCode);
            return Optional.ofNullable(orderDetail);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public OrderDetail save(OrderDetail orderDetail) {
        jdbcTemplate.update(INSERT_SQL,
            orderDetail.getOrderNumber(),
            orderDetail.getProductCode(),
            orderDetail.getQuantityOrdered(),
            orderDetail.getPriceEach(),
            orderDetail.getOrderLineNumber()
        );
        
        return orderDetail;
    }

    public OrderDetail update(OrderDetail orderDetail) {
        int rowsAffected = jdbcTemplate.update(UPDATE_SQL,
            orderDetail.getQuantityOrdered(),
            orderDetail.getPriceEach(),
            orderDetail.getOrderLineNumber(),
            orderDetail.getOrderNumber(),
            orderDetail.getProductCode()
        );
        
        if (rowsAffected == 0) {
            throw new RuntimeException("OrderDetail with orderNumber " + orderDetail.getOrderNumber() + 
                " and productCode " + orderDetail.getProductCode() + " not found for update");
        }
        
        return orderDetail;
    }

    public void deleteById(Integer orderNumber, String productCode) {
        int rowsAffected = jdbcTemplate.update(DELETE_SQL, orderNumber, productCode);
        if (rowsAffected == 0) {
            throw new RuntimeException("OrderDetail with orderNumber " + orderNumber + 
                " and productCode " + productCode + " not found for deletion");
        }
    }
}