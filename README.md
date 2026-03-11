# Spring Boot Online Shop

A full-stack Online Shop backend built with Spring Boot. 
The application provides a RESTful API for managing products, orders
and users with JWT-based authentication and role-based authorization. 
Data is persisted using a MySQL database.

## Features

### JWT Authentication

- Secure login and registration
- Token-based authentication for API requests

### Role-Based Authorization

- USER role – browse products and place orders
- ADMIN role – manage products, users, and orders

### Product Management

- **Role Management**: User and Admin roles with different access levels.
- Create, update, delete products. (Admin)
- View product catalog, manage their cart, and place orders. (User)

### MySQL Database Integration

- MySQL relational database
- JPA/Hibernate ORM
- Automatic schema generation
- Entity relationships (One‑To‑Many, Many‑To‑Many, etc.)

### RESTful API

- Clean and structured endpoints

### Architecture

- Layered architecture (Controller → Service → Repository)
- DTO mapping for clean API responses
- Global exception handling
- Validation with Jakarta Validation

### Tech Stack

- Backend: Spring Boot
- Security: Spring Security + JWT
- Database: MySQL
- ORM: Spring Data JPA / Hibernate
- Build Tool: Maven
- Language: Java
- Other: Lombok

## Project Structure

src/main/java/app
├── admin
│   ├── model                 # Admin-specific entities
│   ├── repository            # Admin data access
│   └── service               # Admin business logic
│
├── basket
│   ├── model                 # Basket/cart entities
│   ├── repository            # Basket persistence
│   └── service               # Basket operations
│
├── config
│   ├── KafkaConfig           # Kafka producer/consumer config (if used)
│   ├── SecurityConfig        # Spring Security + JWT setup
│   └── SwaggerConfig         # OpenAPI/Swagger documentation
│
├── email
│   ├── client                # Email client
│   └── service               # Email sending logic
│
├── event                     # Domain events, listeners, publishers
│
├── exception                 # Custom exceptions & handlers
│
├── product
│   ├── model                 # Product entities
│   ├── repository            # Product persistence
│   └── service               # Product business logic
│
├── security
│   ├── AuthenticationMetadata
│   ├── JwtFilter             # JWT validation filter
│   └── JWTService            # Token generation & validation
│
├── user
│   ├── model                 # User entity + roles
│   ├── repository            # User persistence
│   └── service               # User management logic
│
└── web
    ├── dto                   # Request/response DTOs
    ├── mapper                # DTO ↔ Entity mappers
    ├── AdminController       # REST controllers (admin endpoints)
    ├── AuthController        # REST controllers (authentication endpoints)
    ├── BasketController      # REST controllers (basket/cart endpoints)
    ├── ProductController     # REST controllers (products endpoints)
    ├── ProfileEditController # REST controllers (change user profile endpoints)

## Prerequisites

- Java 17+
- Maven 3.4+
- MySQL 8.0+
- Your preferred IDE (IntelliJ, Eclipse, etc.)


