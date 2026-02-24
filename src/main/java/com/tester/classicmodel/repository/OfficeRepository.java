package com.tester.classicmodel.repository;

import com.tester.classicmodel.model.Office;
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
public class OfficeRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public OfficeRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // SQL Queries
    private static final String FIND_ALL_SQL = 
        "SELECT officeCode, city, phone, addressLine1, addressLine2, " +
        "state, country, postalCode, territory FROM offices";

    private static final String FIND_BY_ID_SQL = FIND_ALL_SQL + " WHERE officeCode = ?";

    private static final String INSERT_SQL = 
        "INSERT INTO offices (officeCode, city, phone, addressLine1, addressLine2, " +
        "state, country, postalCode, territory) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String UPDATE_SQL = 
        "UPDATE offices SET city = ?, phone = ?, addressLine1 = ?, addressLine2 = ?, " +
        "state = ?, country = ?, postalCode = ?, territory = ? WHERE officeCode = ?";

    private static final String DELETE_SQL = "DELETE FROM offices WHERE officeCode = ?";

    // RowMapper for Office
    private static class OfficeRowMapper implements RowMapper<Office> {
        @Override
        public Office mapRow(ResultSet rs, int rowNum) throws SQLException {
            Office office = new Office();
            office.setOfficeCode(rs.getString("officeCode"));
            office.setCity(rs.getString("city"));
            office.setPhone(rs.getString("phone"));
            office.setAddressLine1(rs.getString("addressLine1"));
            office.setAddressLine2(rs.getString("addressLine2"));
            office.setState(rs.getString("state"));
            office.setCountry(rs.getString("country"));
            office.setPostalCode(rs.getString("postalCode"));
            office.setTerritory(rs.getString("territory"));
            return office;
        }
    }

    private final OfficeRowMapper officeRowMapper = new OfficeRowMapper();

    // CRUD Operations
    public List<Office> findAll() {
        return jdbcTemplate.query(FIND_ALL_SQL, officeRowMapper);
    }

    public Optional<Office> findById(String officeCode) {
        try {
            Office office = jdbcTemplate.queryForObject(FIND_BY_ID_SQL, officeRowMapper, officeCode);
            return Optional.ofNullable(office);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Office save(Office office) {
        jdbcTemplate.update(INSERT_SQL,
            office.getOfficeCode(),
            office.getCity(),
            office.getPhone(),
            office.getAddressLine1(),
            office.getAddressLine2(),
            office.getState(),
            office.getCountry(),
            office.getPostalCode(),
            office.getTerritory()
        );
        
        return office;
    }

    public Office update(Office office) {
        int rowsAffected = jdbcTemplate.update(UPDATE_SQL,
            office.getCity(),
            office.getPhone(),
            office.getAddressLine1(),
            office.getAddressLine2(),
            office.getState(),
            office.getCountry(),
            office.getPostalCode(),
            office.getTerritory(),
            office.getOfficeCode()
        );
        
        if (rowsAffected == 0) {
            throw new RuntimeException("Office with code " + office.getOfficeCode() + " not found for update");
        }
        
        return office;
    }

    public void deleteById(String officeCode) {
        int rowsAffected = jdbcTemplate.update(DELETE_SQL, officeCode);
        if (rowsAffected == 0) {
            throw new RuntimeException("Office with code " + officeCode + " not found for deletion");
        }
    }
}