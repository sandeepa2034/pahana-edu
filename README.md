# Pahana Edu Billing System

![Java](https://img.shields.io/badge/Java-23-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-green)
![MongoDB](https://img.shields.io/badge/MongoDB-Database-green)
![Maven](https://img.shields.io/badge/Maven-Build-blue)

A comprehensive web-based billing management system for Pahana Edu Bookshop in Colombo City. Built with Spring Boot, MongoDB, and Thymeleaf to manage customer accounts and billing information efficiently.

## 🛠️ Quick Setup

### Prerequisites

- Java 23 or higher
- Maven 3.8+
- MongoDB 4.4+ (local or Atlas)
- Git

### Installation & Run

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd pahana-edu
   ```

2. **Configure MongoDB**
   - Local: Start MongoDB service
   - Atlas: Update `application.properties` with your connection string

3. **Build and Run**
   ```bash
   mvn clean install
   mvn spring-boot:run "-Dspring-boot.run.profiles=dev"
   ```

4. **Access the Application**
   - Open your browser: [http://localhost:8080](http://localhost:8080)
   - Default admin: `admin` / `admin123`

---

**What is this?**

This is a web-based billing system for Pahana Edu Bookshop in Colombo City, developed for the ICBT CIS6003 module. It replaces manual customer account management with an efficient computerized solution using Spring Boot, MongoDB, and Thymeleaf.