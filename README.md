# Pahana Edu Billing System

![Java](https://img.shields.io/badge/Java-23-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-green)
![MongoDB](https://img.shields.io/badge/MongoDB-Atlas-success)
![Maven](https://img.shields.io/badge/Maven-Build-blue)
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-Template-green)
![Bootstrap](https://img.shields.io/badge/Bootstrap-5-purple)
![License](https://img.shields.io/badge/License-MIT-blue)

A comprehensive web-based billing management system for Pahana Edu Bookshop in Colombo City. Built with Spring Boot, MongoDB Atlas, and Thymeleaf to manage customer accounts and billing information efficiently. The application provides a modern, responsive interface for both customers and administrators.

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
- **Language**: Java 23 (using latest features)
- **Database**: MongoDB Atlas (Cloud-hosted NoSQL database)
- **Security**: Spring Security with BCrypt password encoding
- **Template Engine**: Thymeleaf with Spring integration
- **Frontend**:
  - HTML5, CSS3
  - Bootstrap 5 for responsive design
  - Font Awesome 6 for icons
  - JavaScript for client-side interactions
- **Build Tool**: Maven with dependency management
- **Testing**: 
  - JUnit 5 for unit tests
  - Spring Boot Test for integration testing
  - Testcontainers for MongoDB testing
- **Data Validation**: Jakarta Bean Validation API
- **Development Tools**: Spring Boot DevTools (live reload)

## 📁 Project Structure

```
pahana-edu/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/icbt/pahanaedu/
│   │   │       ├── PahanaEduApplication.java
│   │   │       ├── controller/
│   │   │       │   ├── HomeController.java         # Handles main page routing
│   │   │       │   ├── AuthController.java         # Login/register functionality
│   │   │       │   └── api/                        # REST API controllers
│   │   │       │       ├── ApiController.java
│   │   │       │       ├── CustomerController.java
│   │   │       │       └── ItemController.java
│   │   │       ├── model/                          # MongoDB document models
│   │   │       │   ├── Bill.java
│   │   │       │   ├── Customer.java
│   │   │       │   ├── Item.java                   # Book/product model
│   │   │       │   └── User.java
│   │   │       ├── repository/                     # MongoDB repositories
│   │   │       │   ├── BillRepository.java
│   │   │       │   ├── CustomerRepository.java
│   │   │       │   ├── ItemRepository.java
│   │   │       │   └── UserRepository.java
│   │   │       ├── service/                        # Business logic
│   │   │       │   ├── CustomerService.java
│   │   │       │   ├── ItemService.java
│   │   │       │   └── UserService.java
│   │   │       └── config/                         # Configuration classes
│   │   │           ├── AppConfig.java              # Application configuration
│   │   │           └── SecurityConfig.java         # Spring Security configuration
│   │   └── resources/
│   │       ├── templates/                          # Thymeleaf templates
│   │       │   ├── index.html                      # Landing page
│   │       │   ├── shop.html                       # Book catalog page
│   │       │   ├── login.html                      # User login page
│   │       │   ├── register.html                   # User registration page
│   │       │   └── help.html                       # Help documentation
│   │       ├── static/                             # Static resources
│   │       │   ├── css/
│   │       │   └── js/
│   │       ├── application.properties              # Main configuration
│   │       └── application-dev.properties          # Development configuration
│   └── test/
│       ├── java/
│       │   └── com/icbt/pahanaedu/
│       │       └── PahanaEduApplicationTests.java
│       └── resources/
│           └── application-test.properties         # Test configuration
├── mongodb/
│   └── sample-data/                                # MongoDB sample data
├── docs/                                           # Documentation
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

2. **MongoDB Atlas Configuration**
   
   The application is already configured to use MongoDB Atlas. The connection string is set in `application.properties`:
   ```properties
   spring.data.mongodb.uri=mongodb+srv://sandeepa:b93hiP5KQ8Q81lU0@pahanacluster.wspwo6f.mongodb.net/pahana_edu_db
   ```
   
   If you want to use your own MongoDB instance:
   
   **Option A: Your own MongoDB Atlas cluster**
   - Create a free account at [MongoDB Atlas](https://www.mongodb.com/atlas)
   - Create a new cluster
   - Get your connection string
   - Update `application.properties` with your connection string
   
   **Option B: Local MongoDB Installation**
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
   - Update `application.properties`:
     ```properties
     spring.data.mongodb.uri=mongodb://localhost:27017/pahana_edu_db
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
   # Run with default profile
   mvn spring-boot:run
   
   # Run with dev profile (for development)
   mvn spring-boot:run "-Dspring-boot.run.profiles=dev"
   ```

6. **Access the Application**
   - Open your browser and navigate to: `http://localhost:8080`
   - Explore the bookshop as a guest user
   - Browse the catalog at: `http://localhost:8080/shop`
   - View help documentation at: `http://localhost:8080/help`
   - Login with default admin credentials: `admin` / `admin123`

### Database Setup

The application uses MongoDB and will automatically create the required collections on startup. Sample data is loaded automatically through the `ItemService.initializeSampleData()` method when the application starts.

#### Current MongoDB Configuration

The application is configured to use MongoDB Atlas:
```properties
spring.data.mongodb.uri=mongodb+srv://sandeepa:b93hiP5KQ8Q81lU0@pahanacluster.wspwo6f.mongodb.net/pahana_edu_db
spring.data.mongodb.auto-index-creation=true
```

#### MongoDB Collections

The application uses the following collections:
- `users` - User accounts for authentication
- `items` - Books and other products available in the shop
- `customers` - Customer information and order history
- `bills` - Order and billing information

#### Data Models

1. **User Model**
   - Core fields: username, password (BCrypt encoded), roles
   - Used for authentication and authorization

2. **Item (Book) Model**
   - Core fields: title, author, price, stock, description, category
   - Additional fields: ISBN, imageUrl, publisher, publishYear, available

3. **Customer Model**
   - Core fields: name, phone (primary key), email, address
   - Used for customer management

4. **Bill Model**
   - Core fields: customer, items, total, date
   - Used for order processing and reporting

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

## 🎨 Design System & Color Palette

The application uses a professional academic color palette designed to create a scholarly and trustworthy appearance suitable for an educational bookshop.

### Academic Color Palette

| Color Name | Hex Code | RGB | Usage |
|------------|----------|-----|-------|
| **Oxford Blue** | `#002147` | `rgb(0, 33, 71)` | Primary navigation, headers, main CTAs |
| **Ivory Cream** | `#F8F5EC` | `rgb(248, 245, 236)` | Background, card backgrounds |
| **Dusty Rose** | `#C4A69F` | `rgb(196, 166, 159)` | Secondary buttons, accents |
| **Muted Sage** | `#A8B5A2` | `rgb(168, 181, 162)` | Success states, nature elements |
| **Antique Gold** | `#C6A664` | `rgb(198, 166, 100)` | Highlights, borders, active states |
| **Charcoal Gray** | `#3E3E3E` | `rgb(62, 62, 62)` | Text, secondary content |
| **Maroon Red** | `#6C2C2F` | `rgb(108, 44, 47)` | Error states, warnings |

### Extended Palette (Gradients & States)

| Color Name | Hex Code | Usage |
|------------|----------|-------|
| **Oxford Blue Light** | `#1a3659` | Gradient variations, hover states |
| **Oxford Blue Dark** | `#001122` | Deep shadows, intense elements |
| **Ivory Cream Dark** | `#F0ECE1` | Subtle borders, form elements |
| **Dusty Rose Light** | `#D4B6AF` | Light accent variations |
| **Sage Dark** | `#98A592` | Darker sage variations |
| **Gold Light** | `#D6B674` | Light gold accents |

### CSS Custom Properties

The color palette is implemented using CSS custom properties for easy maintenance and consistency:

```css
:root {
    /* Primary Palette */
    --oxford-blue: #002147;
    --ivory-cream: #F8F5EC;
    --dusty-rose: #C4A69F;
    --muted-sage: #A8B5A2;
    --antique-gold: #C6A664;
    --charcoal-gray: #3E3E3E;
    --maroon-red: #6C2C2F;
    
    /* Extended Palette */
    --oxford-blue-light: #1a3659;
    --oxford-blue-dark: #001122;
    --ivory-cream-dark: #F0ECE1;
    --dusty-rose-light: #D4B6AF;
    --sage-dark: #98A592;
    --gold-light: #D6B674;
}
```

### Design Principles

1. **Academic Elegance**: The color palette evokes trust, knowledge, and professionalism
2. **Accessibility**: All color combinations meet WCAG 2.1 AA contrast requirements
3. **Consistency**: Colors are used systematically across all pages and components
4. **Responsive Design**: The design adapts beautifully across all device sizes
5. **Modern UI/UX**: Clean typography, generous whitespace, and intuitive navigation

### Implementation

- **Navigation**: Oxford Blue gradient backgrounds with Antique Gold accents
- **Cards & Components**: Ivory Cream backgrounds with subtle shadows
- **Buttons**: Gradient combinations using primary and secondary colors
- **Typography**: Charcoal Gray for body text, Oxford Blue for headings
- **Interactive Elements**: Smooth transitions and hover effects using palette variations

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

## 📋 Project Status

- [x] Project structure setup with Spring Boot 3.2.5 and Java 23
- [x] MongoDB Atlas connection configured and working
- [x] User Authentication framework with Spring Security implemented
- [x] Basic Customer and Item models created with validation
- [x] Landing page and shop page with dynamic product display
- [x] Book/Item Management system with categories and filtering
- [x] Responsive UI using Bootstrap 5 and Font Awesome
- [x] Shopping cart functionality (client-side)
- [ ] Bill generation and checkout process
- [ ] PDF generation for customer bills
- [ ] Admin dashboard for inventory management
- [ ] Customer account management enhancement
- [ ] User profile management
- [ ] Complete comprehensive test suite
- [ ] Set up CI/CD pipeline
- [ ] Deploy to cloud platform

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 📞 Support

For support and questions:
- Create an issue in the GitHub repository
- Contact the development team

## 📸 Current Implementation

The application currently has the following pages and functionality implemented:

1. **Landing Page (`/`)** - Displays featured books and category navigation
2. **Shop Page (`/shop`)** - Catalog of all books with filtering by category and search
3. **Login Page (`/login`)** - User authentication form
4. **Registration Page (`/register`)** - New user registration
5. **Help Page (`/help`)** - Documentation and support information

The application currently supports:
- Dynamic display of books from MongoDB
- Responsive design using Bootstrap 5
- Client-side shopping cart functionality
- Book filtering by category and search
- Basic authentication framework

## 🚀 Next Steps

1. **Admin Dashboard** - Create protected admin area for inventory management
2. **Checkout Process** - Implement bill generation and payment flow
3. **User Profiles** - Enhance user account management
4. **PDF Bills** - Add bill generation in PDF format
5. **Testing** - Complete comprehensive test coverage
6. **Cloud Deployment** - Deploy to a cloud platform for production use

---

**Note**: This project is a web-based billing system for Pahana Edu Bookshop in Colombo City, developed as part of the ICBT CIS6003 module coursework to replace manual customer account management with an efficient computerized system. The current implementation provides a working bookshop frontend with dynamic data from MongoDB Atlas.