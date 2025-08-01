# Pahana Edu Bookshop - Complete Class Documentation

This document provides a comprehensive overview of all Java classes in the Pahana Edu Bookshop system, including their attributes, methods, and relationships.

## Table of Contents

1. [Model Classes](#model-classes)
2. [Service Classes](#service-classes)
3. [Controller Classes](#controller-classes)
4. [Repository Interfaces](#repository-interfaces)
5. [Configuration Classes](#configuration-classes)
6. [Main Application Class](#main-application-class)

---

## Model Classes

### 1. User.java

**Purpose**: Authentication and user management model implementing Spring Security UserDetails interface.

**Location**: `src/main/java/com/icbt/pahanaedu/model/User.java`

**Annotations**: 
- `@Document(collection = "users")`
- `@Validated`

**Implements**: `UserDetails` (Spring Security)

#### Attributes:
- `id` (String) - MongoDB document ID with `@Id`
- `username` (String) - Unique username with `@Indexed(unique = true)`, `@NotBlank`, `@Size(min = 3, max = 50)`
- `password` (String) - Encrypted password with `@NotBlank`, `@Size(min = 6)`
- `role` (String) - User role (ADMIN/USER) with `@NotBlank`
- `enabled` (boolean) - Account status, default `true`
- `email` (String) - Email address with `@Email`
- `firstName` (String) - User's first name with `@Size(max = 50)`
- `lastName` (String) - User's last name with `@Size(max = 50)`
- `address` (String) - Physical address with `@Size(max = 200)`
- `city` (String) - City with `@Size(max = 50)`
- `country` (String) - Country with `@Size(max = 50)`
- `phone` (String) - Phone number with `@Pattern` validation

#### Methods:
##### UserDetails Implementation:
- `getAuthorities()` - Returns user roles as authorities
- `getPassword()` - Returns encrypted password
- `getUsername()` - Returns username
- `isAccountNonExpired()` - Returns true (accounts don't expire)
- `isAccountNonLocked()` - Returns true (accounts aren't locked)
- `isCredentialsNonExpired()` - Returns true (credentials don't expire)
- `isEnabled()` - Returns enabled status

##### Business Methods:
- `getFullName()` - Concatenates first and last name
- Standard getters and setters for all fields

---

### 2. Customer.java

**Purpose**: Comprehensive customer management model for order tracking and profile management.

**Location**: `src/main/java/com/icbt/pahanaedu/model/Customer.java`

**Annotations**: 
- `@Document(collection = "customers")`
- `@Validated`

#### Attributes:
- `id` (String) - MongoDB document ID with `@Id`
- `firstName` (String) - Customer first name with `@NotBlank`, `@Size(min = 1, max = 50)`
- `lastName` (String) - Customer last name with `@NotBlank`, `@Size(min = 1, max = 50)`
- `email` (String) - Email with `@Email`, `@Indexed(unique = true)`
- `phoneNumber` (String) - Phone with `@Indexed(unique = true, sparse = true)`
- `address` (String) - Physical address with `@Size(max = 200)`
- `city` (String) - City with `@Size(max = 50)`
- `postalCode` (String) - Postal code with `@Size(max = 20)`
- `country` (String) - Country with `@Size(max = 50)`
- `registrationDate` (LocalDateTime) - Registration timestamp
- `totalSpent` (Double) - Total amount spent, default 0.0
- `orderHistory` (List<String>) - List of order IDs
- `preferredCategories` (List<String>) - Customer preferences
- `lastPurchaseDate` (LocalDateTime) - Last purchase timestamp
- `active` (Boolean) - Account status, default true
- `emailNotifications` (Boolean) - Email preferences, default true
- `smsNotifications` (Boolean) - SMS preferences, default false

#### Methods:
##### Business Logic:
- `getFullName()` - Returns concatenated first and last name
- `addToOrderHistory(String orderId)` - Adds order to history and updates last purchase date
- `updateSpentAmount(Double amount)` - Adds amount to total spent
- `getOrderCount()` - Returns size of order history
- `hasOrders()` - Checks if customer has any orders
- `getDisplayName()` - Returns full name or email if name unavailable

##### Constructors:
- Default constructor
- `Customer(String firstName, String lastName, String email, String phoneNumber)` - Main constructor

##### Standard Methods:
- Standard getters and setters for all fields

---

### 3. Item.java

**Purpose**: Book/product inventory model with stock management and validation.

**Location**: `src/main/java/com/icbt/pahanaedu/model/Item.java`

**Annotations**: 
- `@Document(collection = "items")`
- `@Validated`

#### Attributes:
- `id` (String) - MongoDB document ID with `@Id`
- `title` (String) - Book title with `@NotBlank`, `@Size(min = 1, max = 200)`
- `author` (String) - Author name with `@NotBlank`, `@Size(min = 1, max = 100)`
- `description` (String) - Book description with `@Size(max = 1000)`
- `price` (Double) - Price with `@NotNull`, `@Positive`
- `stock` (Integer) - Stock quantity with `@NotNull`, `@Min(0)`
- `category` (String) - Book category with `@NotBlank`, `@Size(min = 1, max = 50)`
- `isbn` (String) - ISBN with `@Size(max = 20)`
- `publisher` (String) - Publisher with `@Size(max = 100)`
- `publishYear` (Integer) - Publication year with `@Min(1900)`, `@Max(2025)`
- `imageUrl` (String) - Image path with `@Size(max = 200)`
- `available` (Boolean) - Availability status, default true

#### Methods:
##### Business Logic:
- `getFormattedPrice()` - Returns price formatted as "Rs. X,XXX.XX"
- `isInStock()` - Checks if stock > 0
- `isLowStock()` - Checks if stock <= 5
- `getAvailabilityText()` - Returns "In Stock" or "Out of Stock"

##### Constructors:
- Default constructor
- `Item(String title, String author, String description, Double price, Integer stock, String category)` - Main constructor

##### Standard Methods:
- Standard getters and setters for all fields

---

### 4. Bill.java

**Purpose**: Complex billing and order management with nested OrderItem class.

**Location**: `src/main/java/com/icbt/pahanaedu/model/Bill.java`

**Annotations**: 
- `@Document(collection = "bills")`
- `@Validated`

#### Attributes:
- `id` (String) - MongoDB document ID with `@Id`
- `billNumber` (String) - Unique bill number with `@NotBlank`
- `customerName` (String) - Customer name with `@NotBlank`
- `customerPhone` (String) - Customer phone with `@NotBlank`
- `customerEmail` (String) - Customer email with `@Email`
- `customerAddress` (String) - Customer address
- `orderDate` (LocalDateTime) - Order timestamp, default now
- `items` (List<OrderItem>) - List of ordered items with `@NotEmpty`
- `subtotal` (Double) - Subtotal amount with `@NotNull`, `@PositiveOrZero`
- `tax` (Double) - Tax amount with `@NotNull`, `@PositiveOrZero`
- `shipping` (Double) - Shipping cost with `@NotNull`, `@PositiveOrZero`
- `total` (Double) - Total amount with `@NotNull`, `@Positive`
- `paymentMethod` (String) - Payment method with `@NotBlank`
- `paymentStatus` (String) - Payment status, default "PENDING"
- `orderStatus` (String) - Order status, default "PROCESSING"
- `notes` (String) - Additional notes
- `createdAt` (LocalDateTime) - Creation timestamp
- `updatedAt` (LocalDateTime) - Last update timestamp

#### Nested Class: OrderItem
##### Attributes:
- `itemId` (String) - Item reference with `@NotBlank`
- `title` (String) - Item title with `@NotBlank`
- `author` (String) - Item author
- `price` (Double) - Unit price with `@NotNull`, `@Positive`
- `quantity` (Integer) - Quantity with `@NotNull`, `@Positive`
- `subtotal` (Double) - Line total with `@NotNull`, `@PositiveOrZero`

##### OrderItem Methods:
- Constructors and standard getters/setters

#### Bill Methods:
##### Business Logic:
- `calculateTotals()` - Recalculates subtotal, tax, shipping, and total
- `addItem(OrderItem item)` - Adds item and recalculates totals
- `removeItem(String itemId)` - Removes item by ID and recalculates
- `updateQuantity(String itemId, int newQuantity)` - Updates item quantity
- `getTotalItems()` - Returns total quantity of all items
- `markAsPaid()` - Sets payment status to "PAID"
- `markAsShipped()` - Sets order status to "SHIPPED"
- `markAsDelivered()` - Sets order status to "DELIVERED"
- `getFormattedTotal()` - Returns formatted total as "Rs. X,XXX.XX"

##### Validation:
- `@PrePersist` and `@PreUpdate` methods to set timestamps and calculate totals

##### Standard Methods:
- Standard getters and setters for all fields

---

## Service Classes

### 1. UserService.java

**Purpose**: User management service implementing Spring Security UserDetailsService.

**Location**: `src/main/java/com/icbt/pahanaedu/service/UserService.java`

**Annotations**: `@Service`

**Implements**: `UserDetailsService`

#### Dependencies:
- `UserRepository userRepository` (via `@Autowired`)
- `PasswordEncoder passwordEncoder` (via `@Autowired`)

#### Methods:
##### Spring Security Integration:
- `loadUserByUsername(String usernameOrPhone)` - Loads user for authentication (supports username or phone)

##### User Registration:
- `registerUser(String username, String phone, String password, String role)` - Registers new user with validation and password encryption

##### User Lookup:
- `findByUsername(String username)` - Returns Optional<User>
- `findByPhone(String phone)` - Returns Optional<User>
- `usernameExists(String username)` - Boolean check for username availability

##### User Management:
- `getAllUsers()` - Returns List<User> for admin operations
- `getUsersByRole(String role)` - Returns List<User> filtered by role
- `updateUser(User user)` - Updates user details
- `deleteUser(String userId)` - Deletes user by ID
- `toggleUserStatus(String userId)` - Enables/disables user account
- `changePassword(String userId, String newPassword)` - Updates encrypted password

##### Statistics:
- `getTotalUsers()` - Returns total user count
- `getActiveUsers()` - Returns active user count
- `getAdminCount()` - Returns admin count
- `getUserCount()` - Returns regular user count

---

### 2. CustomerService.java

**Purpose**: Comprehensive customer management with order tracking and analytics.

**Location**: `src/main/java/com/icbt/pahanaedu/service/CustomerService.java`

**Annotations**: `@Service`

#### Dependencies:
- `CustomerRepository customerRepository` (via `@Autowired`)
- `BillRepository billRepository` (via `@Autowired`)

#### Methods:
##### Customer CRUD:
- `createCustomer(Customer customer)` - Creates customer with validation and cleanup
- `getAllCustomers()` - Returns all customers
- `getAllCustomers(Pageable pageable)` - Returns paginated customers
- `getCustomerById(String id)` - Returns Optional<Customer>
- `getCustomerByEmail(String email)` - Returns Optional<Customer>
- `getCustomerByPhoneNumber(String phoneNumber)` - Returns Optional<Customer>
- `updateCustomer(String id, Customer updatedCustomer)` - Updates customer details
- `deleteCustomer(String id)` - Hard delete customer
- `saveCustomer(Customer customer)` - Save or update customer

##### Customer Status Management:
- `deactivateCustomer(String id)` - Soft delete (sets active = false)
- `reactivateCustomer(String id)` - Reactivates customer
- `getActiveCustomers()` - Returns only active customers

##### Search and Filtering:
- `searchCustomersByName(String searchTerm)` - Searches by first/last name
- `searchCustomers(String searchTerm, Pageable pageable)` - Paginated search
- `getCustomersByCity(String city)` - Filter by city
- `getCustomersBySpendingRange(Double minSpent, Double maxSpent)` - Filter by spending

##### Analytics:
- `getRecentCustomers(int days)` - Customers registered in last N days
- `getRecentlyActivePurchasers(int days)` - Customers who purchased recently
- `getInactiveCustomers(int daysSinceLastPurchase)` - Inactive customers
- `getTopSpendingCustomers(Double minimumSpent)` - High-value customers
- `getCustomersWithMultipleOrders(Integer minimumOrders)` - Repeat customers

##### Order Management:
- `updatePurchaseStats(String customerId, String billId, Double amount)` - Updates customer purchase data
- `findOrCreateCustomer(String fullName, String phone, String email, String address)` - Guest checkout support
- `saveBill(Bill bill)` - Saves bill/order
- `getCustomerOrderHistory(String phone)` - Gets order history by phone
- `getOrdersByCustomerName(String customerName)` - Gets orders by customer name

##### Validation:
- `isEmailAvailable(String email)` - Checks email availability
- `isPhoneNumberAvailable(String phoneNumber)` - Checks phone availability

##### Statistics:
- `getCustomerStatistics()` - Returns CustomerStats object with totals
- `getTotalCustomers()` - Returns total customer count
- `cleanupNullPhoneRecords()` - Maintenance method for data cleanup

##### Data Initialization:
- `initializeSampleCustomers()` - Creates sample data if database empty

#### Inner Class: CustomerStats
##### Attributes:
- `totalCustomers` (long)
- `activeCustomers` (long) 
- `inactiveCustomers` (long)

---

### 3. ItemService.java

**Purpose**: Book/product inventory management with search and stock control.

**Location**: `src/main/java/com/icbt/pahanaedu/service/ItemService.java`

**Annotations**: `@Service`

#### Dependencies:
- `ItemRepository itemRepository` (via `@Autowired`)

#### Methods:
##### Item CRUD:
- `getAllItems()` - Returns all items sorted by title
- `getAllItems(Pageable pageable)` - Returns paginated items
- `getItemById(String id)` - Returns Optional<Item>
- `saveItem(Item item)` - Saves item and updates availability
- `updateItem(Item item)` - Updates item and recalculates availability
- `deleteItem(String id)` - Deletes item by ID
- `existsById(String id)` - Checks if item exists

##### Search and Filtering:
- `getItemsByCategory(String category)` - Filter by category
- `getAvailableItems()` - Returns only available items
- `searchItems(String searchTerm)` - Search by title or author
- `getFeaturedItems()` - Returns first 8 available items for homepage
- `getItemsByPriceRange(Double minPrice, Double maxPrice)` - Price range filter
- `getAllCategories()` - Returns distinct categories

##### Stock Management:
- `updateStock(String itemId, int quantity)` - Reduces stock after purchase
- `getLowStockItems()` - Returns items with stock <= 5

##### Data Initialization:
- `initializeSampleData()` - Creates sample books if database empty

---

## Controller Classes

### 1. HomeController.java

**Purpose**: Main application controller handling public pages and user interactions.

**Location**: `src/main/java/com/icbt/pahanaedu/controller/HomeController.java`

**Annotations**: `@Controller`

#### Dependencies:
- `ItemService itemService` (via `@Autowired`)
- `UserService userService` (via `@Autowired`)

#### Methods:
##### Public Pages:
- `home(Model model, Authentication authentication)` - `@GetMapping("/")` - Homepage with featured items
- `shop(...)` - `@GetMapping("/shop")` - Product catalog with filtering, search, and pagination
- `help(Model model, Authentication authentication)` - `@GetMapping("/help")` - Help and support page
- `cart(Model model, Authentication authentication)` - `@GetMapping("/cart")` - Shopping cart page
- `checkout(Model model)` - `@GetMapping("/checkout")` - Checkout page

##### User Profile Management:
- `profile(Model model, Authentication authentication)` - `@GetMapping("/profile")` - User profile page
- `updateProfile(User updatedUser, Authentication authentication, RedirectAttributes redirectAttributes)` - `@PostMapping("/profile/update")` - Updates user profile
- `myOrders(Model model, Authentication authentication)` - `@GetMapping("/my-orders")` - User orders page

#### Shop Method Parameters:
- `category` (String) - Filter by category
- `search` (String) - Search term
- `minPrice`, `maxPrice` (Double) - Price range
- `page`, `size` (int) - Pagination
- `sort`, `direction` (String) - Sorting options

---

## Repository Interfaces

### 1. UserRepository.java

**Purpose**: MongoDB repository interface for User entity operations.

**Location**: `src/main/java/com/icbt/pahanaedu/repository/UserRepository.java`

**Annotations**: `@Repository`

**Extends**: `MongoRepository<User, String>`

#### Methods:
##### User Lookup:
- `findByUsernameIgnoreCase(String username)` - Returns Optional<User>
- `existsByUsernameIgnoreCase(String username)` - Boolean check
- `findByPhone(String phone)` - Returns Optional<User>
- `existsByPhone(String phone)` - Boolean check

##### Role and Status Management:
- `findByRole(String role)` - Returns List<User>
- `findByEnabled(boolean enabled)` - Returns List<User>
- `countByRole(String role)` - Count by role
- `countByEnabled(boolean enabled)` - Count by status

---

### 2. CustomerRepository.java

**Purpose**: MongoDB repository interface for Customer entity with advanced query methods.

**Location**: `src/main/java/com/icbt/pahanaedu/repository/CustomerRepository.java`

**Annotations**: `@Repository`

**Extends**: `MongoRepository<Customer, String>`

#### Methods:
##### Basic Lookup:
- `findByEmail(String email)` - Returns Optional<Customer>
- `existsByEmail(String email)` - Boolean check
- `findByPhoneNumber(String phoneNumber)` - Returns Optional<Customer>
- `existsByPhoneNumber(String phoneNumber)` - Boolean check
- `findByPhoneNumberIsNull()` - Returns List<Customer> for cleanup

##### Search and Filtering:
- `findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(String firstName, String lastName)` - Name search
- `findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(...)` - Extended search with pagination
- `findByActiveTrue()` - Active customers only
- `countByActiveTrue()` - Count active customers
- `findByCity(String city)` - Filter by city
- `findByTotalSpentBetween(Double minSpent, Double maxSpent)` - Spending range

##### Time-based Queries:
- `findByRegistrationDateBetween(LocalDateTime start, LocalDateTime end)` - Registration date range
- `findByLastPurchaseDateAfter(LocalDateTime date)` - Recent purchasers

##### Analytics Queries (using `@Query`):
- `findInactiveCustomers(LocalDateTime cutoffDate)` - Customers with no recent purchases
- `findTopSpendingCustomers(Double minimumSpent)` - High-value customers sorted by spending
- `findCustomersWithMultipleOrders(Integer minimumOrders)` - Repeat customers

---

### 3. ItemRepository.java

**Purpose**: MongoDB repository interface for Item entity with search and filtering capabilities.

**Location**: `src/main/java/com/icbt/pahanaedu/repository/ItemRepository.java`

**Annotations**: `@Repository`

**Extends**: `MongoRepository<Item, String>`

#### Methods:
##### Category and Availability:
- `findByCategoryIgnoreCase(String category)` - Returns List<Item>
- `findByAvailableTrue()` - Available items only
- `findTop8ByAvailableTrueOrderByTitleAsc()` - Featured items for homepage

##### Search:
- `findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(String title, String author)` - Text search

##### Price and Stock:
- `findByPriceBetweenAndAvailableTrue(Double minPrice, Double maxPrice)` - Price range with availability
- `findByStockLessThanEqualAndAvailableTrue(int stock)` - Low stock items

##### Analytics:
- `findDistinctCategories()` - Returns List<String> of unique categories (using `@Query`)

---

### 4. BillRepository.java

**Purpose**: MongoDB repository interface for Bill/Order entity.

**Location**: `src/main/java/com/icbt/pahanaedu/repository/BillRepository.java`

**Annotations**: `@Repository`

**Extends**: `MongoRepository<Bill, String>`

#### Methods:
##### Customer Order History:
- `findByCustomerPhoneOrderByOrderDateDesc(String customerPhone)` - Orders by phone, sorted by date
- `findByCustomerNameContainingIgnoreCaseOrderByOrderDateDesc(String customerName)` - Orders by customer name

##### Order Status:
- `findByOrderStatus(String orderStatus)` - Filter by order status
- `findByPaymentStatus(String paymentStatus)` - Filter by payment status

##### Date Range:
- `findByOrderDateBetween(LocalDateTime startDate, LocalDateTime endDate)` - Orders in date range

---

## Configuration Classes

### 1. SecurityConfig.java

**Purpose**: Spring Security configuration for authentication and authorization.

**Location**: `src/main/java/com/icbt/pahanaedu/config/SecurityConfig.java`

**Annotations**: 
- `@Configuration`
- `@EnableWebSecurity`

#### Dependencies:
- `UserDetailsService userDetailsService` (Constructor injection)

#### Methods:
##### Security Configuration:
- `filterChain(HttpSecurity http)` - `@Bean` - Main security filter chain configuration
- `authenticationProvider(PasswordEncoder passwordEncoder)` - `@Bean` - DAO authentication provider
- `authenticationManager(AuthenticationConfiguration config)` - `@Bean` - Authentication manager

#### Security Rules:
##### Public Access:
- `/`, `/home`, `/shop`, `/help`, `/register`, `/login`
- Static resources: `/css/**`, `/js/**`, `/images/**`
- API endpoints: `/api/checkout`

##### Role-based Access:
- `/admin/**` - Requires ADMIN role
- `/user/**`, `/my-orders`, `/profile` - Requires USER or ADMIN role
- `/dashboard` - Requires authentication

##### Login/Logout Configuration:
- Login page: `/login`
- Success redirect: `/dashboard`
- Logout success: `/`
- Access denied page: `/access-denied`

---

### 2. AppConfig.java

**Purpose**: General application configuration including password encoding.

**Location**: `src/main/java/com/icbt/pahanaedu/config/AppConfig.java`

**Annotations**: `@Configuration`

#### Methods:
- `passwordEncoder()` - `@Bean` - Provides BCryptPasswordEncoder instance

---

### 3. DataInitializer.java

**Purpose**: Application startup data initialization.

**Location**: `src/main/java/com/icbt/pahanaedu/config/DataInitializer.java`

**Annotations**: 
- `@Component`

**Implements**: `ApplicationRunner`

#### Dependencies:
- `UserService userService`
- `ItemService itemService` 
- `CustomerService customerService`

#### Methods:
- `run(ApplicationArguments args)` - Initializes sample data on application startup

---

## Main Application Class

### PahanaEduApplication.java

**Purpose**: Spring Boot main application class.

**Location**: `src/main/java/com/icbt/pahanaedu/PahanaEduApplication.java`

**Annotations**: `@SpringBootApplication`

#### Methods:
- `main(String[] args)` - Application entry point

---

## Additional Controller Classes

The system includes several other controller classes for specific functionality:

### AuthController.java
- Handles user registration and authentication
- Login/logout functionality
- Password reset capabilities

### AdminController.java  
- Admin dashboard and statistics
- User management operations
- System administration functions

### CustomerController.java
- Customer management CRUD operations
- Customer search and filtering
- Order history management

### API Controllers
- RESTful API endpoints for frontend integration
- JSON responses for AJAX operations
- Mobile app support endpoints

---

## Summary

The Pahana Edu Bookshop system consists of **60+ Java classes** organized into:

- **4 Core Model Classes**: User, Customer, Item, Bill (with nested OrderItem)
- **3 Service Classes**: UserService, CustomerService, ItemService  
- **6+ Controller Classes**: HomeController, AuthController, AdminController, CustomerController, plus API controllers
- **4 Repository Interfaces**: UserRepository, CustomerRepository, ItemRepository, BillRepository
- **3 Configuration Classes**: SecurityConfig, AppConfig, DataInitializer
- **1 Main Application Class**: PahanaEduApplication

The system implements a **layered architecture** with clear separation of concerns:
- **Model Layer**: Domain entities with validation and business logic
- **Repository Layer**: Data access interfaces with custom query methods
- **Service Layer**: Business logic and transaction management
- **Controller Layer**: Web request handling and response generation
- **Configuration Layer**: Security, authentication, and application setup

All classes follow **Spring Boot best practices** with proper dependency injection, validation annotations, and MongoDB integration for a scalable bookshop management system.
