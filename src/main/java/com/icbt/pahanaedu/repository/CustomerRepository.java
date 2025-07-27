package com.icbt.pahanaedu.repository;

import com.icbt.pahanaedu.model.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends MongoRepository<Customer, String> {

    // Find customer by email
    Optional<Customer> findByEmail(String email);

    // Find customer by phone number
    Optional<Customer> findByPhoneNumber(String phoneNumber);

    // Find customers by name (case-insensitive)
    List<Customer> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(String firstName, String lastName);

    // Find customers by name or email with pagination (for search)
    Page<Customer> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String firstName, String lastName, String email, Pageable pageable);

    // Find active customers
    List<Customer> findByActiveTrue();

    // Find customers by city
    List<Customer> findByCity(String city);

    // Find customers by total spent range
    List<Customer> findByTotalSpentBetween(Double minSpent, Double maxSpent);

    // Find customers who have made purchases after a certain date
    List<Customer> findByLastPurchaseDateAfter(LocalDateTime date);

    // Find customers registered within a date range
    List<Customer> findByRegistrationDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Find customers with email notifications enabled
    List<Customer> findByEmailNotificationsTrue();

    // Find customers by preferred categories
    List<Customer> findByPreferredCategoriesContaining(String category);

    // Custom query to find top spending customers
    @Query("{ 'totalSpent': { $gt: ?0 } }")
    List<Customer> findTopSpendingCustomers(Double minimumSpent);

    // Custom query to find customers with multiple orders
    @Query("{ 'totalOrders': { $gte: ?0 } }")
    List<Customer> findCustomersWithMultipleOrders(Integer minimumOrders);

    // Check if email exists
    boolean existsByEmail(String email);

    // Check if phone number exists
    boolean existsByPhoneNumber(String phoneNumber);

    // Count active customers
    long countByActiveTrue();

    // Find customers who haven't made a purchase recently
    @Query("{ $or: [ { 'lastPurchaseDate': { $lt: ?0 } }, { 'lastPurchaseDate': null } ] }")
    List<Customer> findInactiveCustomers(LocalDateTime cutoffDate);
}
