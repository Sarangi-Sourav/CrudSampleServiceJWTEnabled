package com.tester.classicmodel.repository;

import com.tester.classicmodel.model.ProductLine;
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
public class ProductLineRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ProductLineRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // SQL Queries
    private static final String FIND_ALL_SQL = 
        "SELECT productLine, textDescription, htmlDescription, image FROM productlines";

    private static final String FIND_BY_ID_SQL = FIND_ALL_SQL + " WHERE productLine = ?";

    private static final String INSERT_SQL = 
        "INSERT INTO productlines (productLine, textDescription, htmlDescription, image) VALUES (?, ?, ?, ?)";

    private static final String UPDATE_SQL = 
        "UPDATE productlines SET textDescription = ?, htmlDescription = ?, image = ? WHERE productLine = ?";

    private static final String DELETE_SQL = "DELETE FROM productlines WHERE productLine = ?";

    // RowMapper for ProductLine
    private static class ProductLineRowMapper implements RowMapper<ProductLine> {
        @Override
        public ProductLine mapRow(ResultSet rs, int rowNum) throws SQLException {
            ProductLine productLine = new ProductLine();
            productLine.setProductLine(rs.getString("productLine"));
            productLine.setTextDescription(rs.getString("textDescription"));
            productLine.setHtmlDescription(rs.getString("htmlDescription"));
            productLine.setImage(rs.getBytes("image"));
            return productLine;
        }
    }

    private final ProductLineRowMapper productLineRowMapper = new ProductLineRowMapper();

    // CRUD Operations
    public List<ProductLine> findAll() {
        return jdbcTemplate.query(FIND_ALL_SQL, productLineRowMapper);
    }

    public Optional<ProductLine> findById(String productLine) {
        try {
            ProductLine productLineEntity = jdbcTemplate.queryForObject(FIND_BY_ID_SQL, productLineRowMapper, productLine);
            return Optional.ofNullable(productLineEntity);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public ProductLine save(ProductLine productLine) {
        jdbcTemplate.update(INSERT_SQL,
            productLine.getProductLine(),
            productLine.getTextDescription(),
            productLine.getHtmlDescription(),
            productLine.getImage()
        );
        
        return productLine;
    }

    public ProductLine update(ProductLine productLine) {
        int rowsAffected = jdbcTemplate.update(UPDATE_SQL,
            productLine.getTextDescription(),
            productLine.getHtmlDescription(),
            productLine.getImage(),
            productLine.getProductLine()
        );
        
        if (rowsAffected == 0) {
            throw new RuntimeException("ProductLine with name " + productLine.getProductLine() + " not found for update");
        }
        
        return productLine;
    }

    public void deleteById(String productLine) {
        int rowsAffected = jdbcTemplate.update(DELETE_SQL, productLine);
        if (rowsAffected == 0) {
            throw new RuntimeException("ProductLine with name " + productLine + " not found for deletion");
        }
    }
}