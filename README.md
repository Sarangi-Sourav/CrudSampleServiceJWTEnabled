# ClassicModel Tester

This repository contains a simple CRUD application built with Spring Boot that interacts with the ClassicModels database.

## Purpose

The primary goal of this project is to serve as a codebase for the **Code_Analyzer** service. The Code_Analyzer analyzes this repository to create a knowledge graph, which in turn empowers an LLM (Large Language Model) to answer questions about the code structure, dependencies, and functionality.

## Tech Stack

*   **Java 21**
*   **Spring Boot 3.x**
*   **Spring JDBC (JdbcTemplate)** for database interactions
*   **MySQL** as the database
*   **Spring Security** for authentication and authorization
*   **Docker** for containerization

## Key Features

*   **CRUD Operations:** Basic Create, Read, Update, and Delete operations for standard ClassicModels entities (Orders, Payments, etc.).
*   **JdbcTemplate:** Direct SQL query execution for transparency and ease of analysis by the Code_Analyzer.
*   **Authentication:** Secure login and sign-up endpoints (`/api/auth/login`, `/api/auth/signin`).
*   **Centralized Error Handling:** Uses `@ControllerAdvice` and `@ExceptionHandler` for consistent error responses.

## Getting Started

1.  **Clone the repository:**
    ```bash
    git clone <repository-url>
    ```
2.  **Configure Database:**
    Ensure your MySQL database is running and the credentials in `src/main/resources/application.properties` match your setup.
3.  **Run the Application:**
    ```bash
    ./mvnw spring-boot:run
    ```
4.  **Docker:**
    You can also run the application using Docker:
    ```bash
    docker build -t classicmodel-app .
    docker run -p 8080:8080 classicmodel-app
    ```

## Project Structure

The project follows a standard Spring Boot layered architecture:

*   `controller`: REST controllers handling HTTP requests.
*   `service`: Business logic layer.
*   `repository`: Data access layer using `JdbcTemplate`.
*   `model`: POJOs representing database entities.
*   `dto`: Data Transfer Objects for API communication.
*   `exception`: Custom exceptions and global error handling.

## Note

This project is designed to be analyzed. The use of `JdbcTemplate` instead of a full ORM like Hibernate/JPA for some parts is intentional to provide clear SQL patterns for the analyzer.
