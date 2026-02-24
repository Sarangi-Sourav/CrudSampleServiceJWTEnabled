package com.tester.classicmodel.repository;

import com.tester.classicmodel.model.Product;
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
public class ProductRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ProductRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // SQL Queries
    private static final String FIND_ALL_SQL = 
        "SELECT productCode, productName, productLine, productScale, productVendor, " +
        "productDescription, quantityInStock, buyPrice, MSRP FROM products";

    private static final String FIND_BY_ID_SQL = FIND_ALL_SQL + " WHERE productCode = ?";

    private static final String INSERT_SQL = 
        "INSERT INTO products (productCode, productName, productLine, productScale, productVendor, " +
        "productDescription, quantityInStock, buyPrice, MSRP) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String UPDATE_SQL = 
        "UPDATE products SET productName = ?, productLine = ?, productScale = ?, productVendor = ?, " +
        "productDescription = ?, quantityInStock = ?, buyPrice = ?, MSRP = ? WHERE productCode = ?";

    private static final String DELETE_SQL = "DELETE FROM products WHERE productCode = ?";

    // RowMapper for Product
    private static class ProductRowMapper implements RowMapper<Product> {
        @Override
        public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
            Product product = new Product();
            product.setProductCode(rs.getString("productCode"));
            product.setProductName(rs.getString("productName"));
            product.setProductLine(rs.getString("productLine"));
            product.setProductScale(rs.getString("productScale"));
            product.setProductVendor(rs.getString("productVendor"));
            product.setProductDescription(rs.getString("productDescription"));
            product.setQuantityInStock(rs.getShort("quantityInStock"));
            product.setBuyPrice(rs.getBigDecimal("buyPrice"));
            product.setMsrp(rs.getBigDecimal("MSRP"));
            return product;
        }
    }

    private final ProductRowMapper productRowMapper = new ProductRowMapper();

    // CRUD Operations
    public List<Product> findAll() {
        return jdbcTemplate.query(FIND_ALL_SQL, productRowMapper);
    }

    public Optional<Product> findById(String productCode) {
        try {
            Product product = jdbcTemplate.queryForObject(FIND_BY_ID_SQL, productRowMapper, productCode);
            return Optional.ofNullable(product);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Product save(Product product) {
        jdbcTemplate.update(INSERT_SQL,
            product.getProductCode(),
            product.getProductName(),
            product.getProductLine(),
            product.getProductScale(),
            product.getProductVendor(),
            product.getProductDescription(),
            product.getQuantityInStock(),
            product.getBuyPrice(),
            product.getMsrp()
        );
        
        return product;
    }

    public Product update(Product product) {
        int rowsAffected = jdbcTemplate.update(UPDATE_SQL,
            product.getProductName(),
            product.getProductLine(),
            product.getProductScale(),
            product.getProductVendor(),
            product.getProductDescription(),
            product.getQuantityInStock(),
            product.getBuyPrice(),
            product.getMsrp(),
            product.getProductCode()
        );
        
        if (rowsAffected == 0) {
            throw new RuntimeException("Product with code " + product.getProductCode() + " not found for update");
        }
        
        return product;
    }

    public void deleteById(String productCode) {
        int rowsAffected = jdbcTemplate.update(DELETE_SQL, productCode);
        if (rowsAffected == 0) {
            throw new RuntimeException("Product with code " + productCode + " not found for deletion");
        }
    }
}