package com.tester.classicmodel.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/health")
    public Map<String, String> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "OK");
        response.put("message", "Application is running");
        return response;
    }

    @GetMapping("/db-direct")
    public Map<String, Object> testDirectConnection() {
        Map<String, Object> response = new HashMap<>();
        
        String url = "jdbc:mysql://localhost:3306/classicmodels?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
        String username = "root";
        String password = "Sololeveling@123";
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url, username, password);
            
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) as count FROM customers");
            
            int count = 0;
            if (resultSet.next()) {
                count = resultSet.getInt("count");
            }
            
            resultSet.close();
            statement.close();
            connection.close();
            
            response.put("status", "OK");
            response.put("message", "Direct database connection successful");
            response.put("customerCount", count);
            
        } catch (Exception e) {
            response.put("status", "ERROR");
            response.put("message", "Direct database connection failed: " + e.getMessage());
            response.put("error", e.getClass().getSimpleName());
        }
        
        return response;
    }

    @GetMapping("/csrf_token")
    public CsrfToken getCsrfToken (HttpServletRequest request){
        return (CsrfToken) request.getAttribute("_csrf");
    }
}