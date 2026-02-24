package com.tester.classicmodel.repository;

import com.tester.classicmodel.model.Payment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class PaymentRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public PaymentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // SQL Queries
    private static final String FIND_ALL_SQL = 
        "SELECT customerNumber, checkNumber, paymentDate, amount FROM payments";

    private static final String FIND_BY_ID_SQL = FIND_ALL_SQL + " WHERE customerNumber = ? AND checkNumber = ?";

    private static final String INSERT_SQL = 
        "INSERT INTO payments (customerNumber, checkNumber, paymentDate, amount) VALUES (?, ?, ?, ?)";

    private static final String UPDATE_SQL = 
        "UPDATE payments SET paymentDate = ?, amount = ? WHERE customerNumber = ? AND checkNumber = ?";

    private static final String DELETE_SQL = "DELETE FROM payments WHERE customerNumber = ? AND checkNumber = ?";

    // RowMapper for Payment
    private static class PaymentRowMapper implements RowMapper<Payment> {
        @Override
        public Payment mapRow(ResultSet rs, int rowNum) throws SQLException {
            Payment payment = new Payment();
            payment.setCustomerNumber(rs.getInt("customerNumber"));
            payment.setCheckNumber(rs.getString("checkNumber"));
            
            // Handle LocalDate conversion
            Date paymentDate = rs.getDate("paymentDate");
            if (paymentDate != null) {
                payment.setPaymentDate(paymentDate.toLocalDate());
            }
            
            payment.setAmount(rs.getBigDecimal("amount"));
            return payment;
        }
    }

    private final PaymentRowMapper paymentRowMapper = new PaymentRowMapper();

    // CRUD Operations
    public List<Payment> findAll() {
        return jdbcTemplate.query(FIND_ALL_SQL, paymentRowMapper);
    }

    public Optional<Payment> findById(Integer customerNumber, String checkNumber) {
        try {
            Payment payment = jdbcTemplate.queryForObject(FIND_BY_ID_SQL, paymentRowMapper, customerNumber, checkNumber);
            return Optional.ofNullable(payment);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Payment save(Payment payment) {
        jdbcTemplate.update(INSERT_SQL,
            payment.getCustomerNumber(),
            payment.getCheckNumber(),
            payment.getPaymentDate() != null ? Date.valueOf(payment.getPaymentDate()) : null,
            payment.getAmount()
        );
        
        return payment;
    }

    public Payment update(Payment payment) {
        int rowsAffected = jdbcTemplate.update(UPDATE_SQL,
            payment.getPaymentDate() != null ? Date.valueOf(payment.getPaymentDate()) : null,
            payment.getAmount(),
            payment.getCustomerNumber(),
            payment.getCheckNumber()
        );
        
        if (rowsAffected == 0) {
            throw new RuntimeException("Payment with customerNumber " + payment.getCustomerNumber() + 
                " and checkNumber " + payment.getCheckNumber() + " not found for update");
        }
        
        return payment;
    }

    public void deleteById(Integer customerNumber, String checkNumber) {
        int rowsAffected = jdbcTemplate.update(DELETE_SQL, customerNumber, checkNumber);
        if (rowsAffected == 0) {
            throw new RuntimeException("Payment with customerNumber " + customerNumber + 
                " and checkNumber " + checkNumber + " not found for deletion");
        }
    }
}