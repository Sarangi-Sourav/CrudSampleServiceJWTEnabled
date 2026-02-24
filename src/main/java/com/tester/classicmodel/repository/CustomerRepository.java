package com.tester.classicmodel.repository;

import com.tester.classicmodel.model.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class CustomerRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public CustomerRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // SQL Queries
    private static final String FIND_ALL_SQL = 
        "SELECT customerNumber, customerName, contactLastName, contactFirstName, " +
        "phone, addressLine1, addressLine2, city, state, postalCode, country, " +
        "salesRepEmployeeNumber, creditLimit FROM customers";

    private static final String FIND_BY_ID_SQL = FIND_ALL_SQL + " WHERE customerNumber = ?";

    private static final String INSERT_SQL = 
        "INSERT INTO customers (customerName, contactLastName, contactFirstName, " +
        "phone, addressLine1, addressLine2, city, state, postalCode, country, " +
        "salesRepEmployeeNumber, creditLimit) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String UPDATE_SQL = 
        "UPDATE customers SET customerName = ?, contactLastName = ?, contactFirstName = ?, " +
        "phone = ?, addressLine1 = ?, addressLine2 = ?, city = ?, state = ?, postalCode = ?, " +
        "country = ?, salesRepEmployeeNumber = ?, creditLimit = ? WHERE customerNumber = ?";

    private static final String DELETE_SQL = "DELETE FROM customers WHERE customerNumber = ?";

    // RowMapper for Customer
    private static class CustomerRowMapper implements RowMapper<Customer> {
        @Override
        public Customer mapRow(ResultSet rs, int rowNum) throws SQLException {
            Customer customer = new Customer();
            customer.setCustomerNumber(rs.getInt("customerNumber"));
            customer.setCustomerName(rs.getString("customerName"));
            customer.setContactLastName(rs.getString("contactLastName"));
            customer.setContactFirstName(rs.getString("contactFirstName"));
            customer.setPhone(rs.getString("phone"));
            customer.setAddressLine1(rs.getString("addressLine1"));
            customer.setAddressLine2(rs.getString("addressLine2"));
            customer.setCity(rs.getString("city"));
            customer.setState(rs.getString("state"));
            customer.setPostalCode(rs.getString("postalCode"));
            customer.setCountry(rs.getString("country"));
            
            // Handle nullable integer
            Integer salesRepEmployeeNumber = rs.getObject("salesRepEmployeeNumber", Integer.class);
            customer.setSalesRepEmployeeNumber(salesRepEmployeeNumber);
            
            customer.setCreditLimit(rs.getBigDecimal("creditLimit"));
            return customer;
        }
    }

    private final CustomerRowMapper customerRowMapper = new CustomerRowMapper();

    // CRUD Operations
    public List<Customer> findAll() {
        return jdbcTemplate.query(FIND_ALL_SQL, customerRowMapper);
    }

    public Optional<Customer> findById(Integer customerNumber) {
        try {
            Customer customer = jdbcTemplate.queryForObject(FIND_BY_ID_SQL, customerRowMapper, customerNumber);
            return Optional.ofNullable(customer);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Customer save(Customer customer) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, customer.getCustomerName());
            ps.setString(2, customer.getContactLastName());
            ps.setString(3, customer.getContactFirstName());
            ps.setString(4, customer.getPhone());
            ps.setString(5, customer.getAddressLine1());
            ps.setString(6, customer.getAddressLine2());
            ps.setString(7, customer.getCity());
            ps.setString(8, customer.getState());
            ps.setString(9, customer.getPostalCode());
            ps.setString(10, customer.getCountry());
            
            if (customer.getSalesRepEmployeeNumber() != null) {
                ps.setInt(11, customer.getSalesRepEmployeeNumber());
            } else {
                ps.setNull(11, java.sql.Types.INTEGER);
            }
            
            if (customer.getCreditLimit() != null) {
                ps.setBigDecimal(12, customer.getCreditLimit());
            } else {
                ps.setNull(12, java.sql.Types.DECIMAL);
            }
            
            return ps;
        }, keyHolder);

        // Set the generated customer number
        Number generatedKey = keyHolder.getKey();
        if (generatedKey != null) {
            customer.setCustomerNumber(generatedKey.intValue());
        }
        
        return customer;
    }

    public Customer update(Customer customer) {
        int rowsAffected = jdbcTemplate.update(UPDATE_SQL,
            customer.getCustomerName(),
            customer.getContactLastName(),
            customer.getContactFirstName(),
            customer.getPhone(),
            customer.getAddressLine1(),
            customer.getAddressLine2(),
            customer.getCity(),
            customer.getState(),
            customer.getPostalCode(),
            customer.getCountry(),
            customer.getSalesRepEmployeeNumber(),
            customer.getCreditLimit(),
            customer.getCustomerNumber()
        );
        
        if (rowsAffected == 0) {
            throw new RuntimeException("Customer with ID " + customer.getCustomerNumber() + " not found for update");
        }
        
        return customer;
    }

    public void deleteById(Integer customerNumber) {
        int rowsAffected = jdbcTemplate.update(DELETE_SQL, customerNumber);
        if (rowsAffected == 0) {
            throw new RuntimeException("Customer with ID " + customerNumber + " not found for deletion");
        }
    }
}