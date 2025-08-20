# Pahana Edu Bookshop

![Java](https://img.shields.io/badge/Java-23-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-green)
![MongoDB](https://img.shields.io/badge/MongoDB-Atlas-success)

A web-based billing and inventory management system for Pahana Edu Bookshop in Colombo, Sri Lanka.

## Features

### Customer Features
- Browse and search books catalog
- Shopping cart management
- Secure checkout (Card payment and Cash on Delivery)
- Order history and tracking
- User profile management

### Admin Features
- Customer management
- Inventory management
- Order processing
- Dashboard with analytics

### Technical Features
- Spring Security authentication
- MongoDB Atlas database
- Responsive Bootstrap UI
- RESTful APIs

## Technology Stack

- **Java 23** - Programming language
- **Spring Boot 3.2.5** - Application framework
- **Spring Security** - Authentication and authorization
- **MongoDB Atlas** - Cloud database
- **Thymeleaf** - Template engine
- **Bootstrap 5** - Frontend framework
- **Maven** - Build tool

## Quick Start

### Prerequisites
- Java 23+
- Maven 3.8+
- MongoDB Atlas account

### Installation
1. Clone the repository
   ```bash
   git clone https://github.com/sandeepa2034/pahana-edu.git
   cd pahana-edu
   ```

2. Configure database in `application.properties`
   ```properties
   spring.data.mongodb.uri=your-mongodb-uri
   ```

3. Run the application
   ```bash
   mvn spring-boot:run
   ```

4. Access the application at `http://localhost:8080`

## Default Credentials
- **Admin**: admin / admin123

## Testing

Run tests with:
```bash
mvn test
```

## License

MIT License

---

**© 2025 Pahana Edu Bookshop - ICBT Campus Project**