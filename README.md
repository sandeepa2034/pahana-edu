# Pahana Edu Billing System

![Java](https://img.shields.io/badge/Java-23-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-green)
![MongoDB](https://img.shields.io/badge/MongoDB-Database-green)
![Maven](https://img.shields.io/badge/Maven-Build-blue)
![License](https://img.shields.io/badge/License-MIT-blue)

A comprehensive web-based billing management system for Pahana Edu Bookshop in Colombo City. Built with Spring Boot, MongoDB, and Thymeleaf to manage customer accounts and billing information efficiently.

## 🚀 Features

### Core Functional Tasks
✅ **User Authentication**
- Spring Security with encrypted passwords (BCrypt)
- Login form with input validation
- Logout and session timeout handling

✅ **Customer Account Management**
- Add, edit, and view customer accounts
- Search customer by account number or name
- Phone number validation and management
- Customer billing history tracking

✅ **Book/Item Management**
- CRUD operations for books and items (title, author, price, stock)
- Display book catalog with sorting/search
- Inventory management for bookshop stock

✅ **Bill Calculation**
- Input: quantity purchased → Output: total amount
- Auto-fetch book/item price for calculation
- Display itemized breakdown with total
- Generate printable/PDF bills for customers

✅ **Database Integration (Spring Data MongoDB)**
- Use MongoDB for data persistence
- Entities: Customer, Book/Item, Bill, User
- Customer account details with phone number as primary key
- Relationships and field validation via annotations

✅ **User Interface**
- HTML + Thymeleaf with Bootstrap styling
- Customer-facing: Browse books, view cart, purchase
- Admin dashboard: Customers, Books, Billing, Reports, Help
- Error/success alerts using th:if

✅ **Help Section**
- Instructions for browsing books, account creation, and billing
- Customer support information
- Displayed as a static HTML or Thymeleaf page

### Testing (20 Marks Scope)
✅ **Unit Testing**
- Use JUnit and Spring Boot Test
- Test classes for: Authentication, Customer CRUD, Book management, Billing logic
- Use mock data where needed

✅ **Test Plan & Coverage**
- Document test strategy and tools
- Include edge-case scenarios
- Store sample screenshots of test runs in /test/docs

✅ **CI/CD Pipeline**
- Use GitHub Actions to run tests on every push or pull request
- Fail builds if any test fails

## 🛠️ Technology Stack

- **Backend Framework**: Spring Boot 3.2.5
- **Language**: Java 23
- **Database**: MongoDB
- **Security**: Spring Security with BCrypt
- **Template Engine**: Thymeleaf
- **Frontend**: HTML5, CSS3, Bootstrap 5, Font Awesome
- **Build Tool**: Maven
- **Testing**: JUnit 5, Spring Boot Test, Testcontainers

## 📁 Project Structure

```
pahana-edu/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/icbt/pahanaedu/
│   │   │       ├── PahanaEduApplication.java
│   │   │       ├── controller/
│   │   │       ├── model/
│   │   │       ├── repository/
│   │   │       ├── service/
│   │   │       └── config/
│   │   └── resources/
│   │       ├── templates/
│   │       ├── static/
│   │       └── application.properties
│   └── test/
│       ├── java/
│       └── resources/
├── docs/
├── sql/
├── pom.xml
├── .gitignore
└── README.md
```

## 🚀 Getting Started

### Prerequisites

- Java 23 or higher
- Maven 3.8+
- MongoDB 4.4+ (local installation or MongoDB Atlas)
- Git

### Installation & Setup

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd pahana-edu
   ```

2. **Configure MongoDB**
   
   **Option A: Local MongoDB Installation**
   - Download and install MongoDB from [https://www.mongodb.com/try/download/community](https://www.mongodb.com/try/download/community)
   - Start MongoDB service:
     ```bash
     # On Windows
     net start MongoDB
     
     # On macOS (with Homebrew)
     brew services start mongodb/brew/mongodb-community
     
     # On Linux
     sudo systemctl start mongod
     ```
   
   **Option B: MongoDB Atlas (Cloud)**
   - Create a free account at [MongoDB Atlas](https://www.mongodb.com/atlas)
   - Create a new cluster
   - Get your connection string
   - Update `application.properties`:
     ```properties
     spring.data.mongodb.uri=mongodb+srv://username:password@cluster.mongodb.net/pahana_edu_db
     ```

3. **Install Dependencies and Build**
   ```bash
   mvn clean install
   ```

4. **Run Tests**
   ```bash
   mvn test
   ```

5. **Run the Application**
   ```bash
   mvn spring-boot:run "-Dspring-boot.run.profiles=dev"
   ```

6. **Access the Application**
   - Open your browser and navigate to: `http://localhost:8080`
   - Browse books as a customer or login as admin
   - Default admin credentials: `admin` / `admin123`

### Database Setup

The application uses MongoDB and will automatically create the required collections on startup. No manual database setup is required.

#### MongoDB Configuration Options

**Local MongoDB (Default):**
```properties
spring.data.mongodb.host=localhost
spring.data.mongodb.port=27017
spring.data.mongodb.database=pahana_edu_db
```

**MongoDB Atlas:**
```properties
spring.data.mongodb.uri=mongodb+srv://<username>:<password>@<cluster>.mongodb.net/pahana_edu_db
```

**Docker MongoDB:**
```bash
docker run -d -p 27017:27017 --name mongodb mongo:latest
```

## 🧪 Testing

Run all tests:
```bash
mvn test
```

Run tests with coverage:
```bash
mvn test jacoco:report
```

Run integration tests:
```bash
mvn verify
```

## 🔧 Development Setup

### IDE Configuration

**IntelliJ IDEA:**
1. Import as Maven project
2. Set Project SDK to Java 23
3. Enable annotation processing
4. Install Spring Boot plugin

**VS Code:**
1. Install Extension Pack for Java
2. Install Spring Boot Extension Pack
3. Open project folder

### Environment Variables

You can override default configuration using environment variables:

```bash
export MONGODB_URI=mongodb://localhost:27017/pahana_edu_db
export SERVER_PORT=8080
export SPRING_PROFILES_ACTIVE=dev
```

## 📝 Development Guidelines

1. Follow Java naming conventions
2. Write unit tests for all service methods
3. Use proper validation annotations
4. Implement proper error handling
5. Add meaningful commit messages
6. Update README for any configuration changes

## 🏗️ Build and Deployment

### Local Build
```bash
mvn clean package
java -jar target/pahana-edu-0.0.1-SNAPSHOT.jar
```

### Production Build
```bash
mvn clean package -Pprod
```

### Docker Deployment
```dockerfile
FROM openjdk:23-jdk-slim
COPY target/pahana-edu-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📋 TODO

- [ ] Implement User Authentication with Spring Security
- [ ] Create Customer Account Management with phone number as primary key
- [ ] Develop Book/Item Management system
- [ ] Build Bill Calculation functionality for bookshop purchases
- [ ] Add PDF generation for customer bills
- [ ] Implement book search and filtering
- [ ] Add customer and inventory validation
- [ ] Create comprehensive test suite
- [ ] Set up CI/CD pipeline
- [ ] Deploy to cloud platform

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 📞 Support

For support and questions:
- Create an issue in the GitHub repository
- Contact the development team

## 📸 Screenshots

*Screenshots will be added once the UI is implemented*

---

**Note**: This project is a web-based billing system for Pahana Edu Bookshop in Colombo City, developed as part of the ICBT CIS6003 module coursework to replace manual customer account management with an efficient computerized system.