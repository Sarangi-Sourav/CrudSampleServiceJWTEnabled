package com.tester.classicmodel.repository;

import com.tester.classicmodel.model.Employee;
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
public class EmployeeRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public EmployeeRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // SQL Queries
    private static final String FIND_ALL_SQL = 
        "SELECT employeeNumber, lastName, firstName, extension, email, " +
        "officeCode, reportsTo, jobTitle FROM employees";

    private static final String FIND_BY_ID_SQL = FIND_ALL_SQL + " WHERE employeeNumber = ?";

    private static final String INSERT_SQL = 
        "INSERT INTO employees (lastName, firstName, extension, email, " +
        "officeCode, reportsTo, jobTitle) VALUES (?, ?, ?, ?, ?, ?, ?)";

    private static final String UPDATE_SQL = 
        "UPDATE employees SET lastName = ?, firstName = ?, extension = ?, email = ?, " +
        "officeCode = ?, reportsTo = ?, jobTitle = ? WHERE employeeNumber = ?";

    private static final String DELETE_SQL = "DELETE FROM employees WHERE employeeNumber = ?";

    // RowMapper for Employee
    private static class EmployeeRowMapper implements RowMapper<Employee> {
        @Override
        public Employee mapRow(ResultSet rs, int rowNum) throws SQLException {
            Employee employee = new Employee();
            employee.setEmployeeNumber(rs.getInt("employeeNumber"));
            employee.setLastName(rs.getString("lastName"));
            employee.setFirstName(rs.getString("firstName"));
            employee.setExtension(rs.getString("extension"));
            employee.setEmail(rs.getString("email"));
            employee.setOfficeCode(rs.getString("officeCode"));
            
            // Handle nullable integer
            Integer reportsTo = rs.getObject("reportsTo", Integer.class);
            employee.setReportsTo(reportsTo);
            
            employee.setJobTitle(rs.getString("jobTitle"));
            return employee;
        }
    }

    private final EmployeeRowMapper employeeRowMapper = new EmployeeRowMapper();

    // CRUD Operations
    public List<Employee> findAll() {
        return jdbcTemplate.query(FIND_ALL_SQL, employeeRowMapper);
    }

    public Optional<Employee> findById(Integer employeeNumber) {
        try {
            Employee employee = jdbcTemplate.queryForObject(FIND_BY_ID_SQL, employeeRowMapper, employeeNumber);
            return Optional.ofNullable(employee);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Employee save(Employee employee) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, employee.getLastName());
            ps.setString(2, employee.getFirstName());
            ps.setString(3, employee.getExtension());
            ps.setString(4, employee.getEmail());
            ps.setString(5, employee.getOfficeCode());
            
            if (employee.getReportsTo() != null) {
                ps.setInt(6, employee.getReportsTo());
            } else {
                ps.setNull(6, java.sql.Types.INTEGER);
            }
            
            ps.setString(7, employee.getJobTitle());
            
            return ps;
        }, keyHolder);

        // Set the generated employee number
        Number generatedKey = keyHolder.getKey();
        if (generatedKey != null) {
            employee.setEmployeeNumber(generatedKey.intValue());
        }
        
        return employee;
    }

    public Employee update(Employee employee) {
        int rowsAffected = jdbcTemplate.update(UPDATE_SQL,
            employee.getLastName(),
            employee.getFirstName(),
            employee.getExtension(),
            employee.getEmail(),
            employee.getOfficeCode(),
            employee.getReportsTo(),
            employee.getJobTitle(),
            employee.getEmployeeNumber()
        );
        
        if (rowsAffected == 0) {
            throw new RuntimeException("Employee with ID " + employee.getEmployeeNumber() + " not found for update");
        }
        
        return employee;
    }

    public void deleteById(Integer employeeNumber) {
        int rowsAffected = jdbcTemplate.update(DELETE_SQL, employeeNumber);
        if (rowsAffected == 0) {
            throw new RuntimeException("Employee with ID " + employeeNumber + " not found for deletion");
        }
    }
}