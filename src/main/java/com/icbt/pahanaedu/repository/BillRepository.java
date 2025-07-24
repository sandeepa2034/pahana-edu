package com.icbt.pahanaedu.repository;

import com.icbt.pahanaedu.model.Bill;
import com.icbt.pahanaedu.model.Customer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BillRepository extends MongoRepository<Bill, String> {

    // Find bills by customer
    List<Bill> findByCustomer(Customer customer);

    // Find bills by customer ID
    List<Bill> findByCustomer_Id(String customerId);

    // Find bills by payment status
    List<Bill> findByPaymentStatus(String paymentStatus);

    // Find bills by order status
    List<Bill> findByOrderStatus(String orderStatus);

    // Find bills by payment method
    List<Bill> findByPaymentMethod(String paymentMethod);

    // Find bills within a date range
    List<Bill> findByOrderDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Find bills by customer email
    List<Bill> findByCustomerEmail(String customerEmail);

    // Find bills by customer phone
    List<Bill> findByCustomerPhone(String customerPhone);

    // Find bills with total amount greater than specified value
    List<Bill> findByTotalAmountGreaterThan(Double amount);

    // Find bills with total amount between range
    List<Bill> findByTotalAmountBetween(Double minAmount, Double maxAmount);

    // Find pending bills
    List<Bill> findByPaymentStatus(String paymentStatus, LocalDateTime orderDate);

    // Find bills by transaction ID
    Bill findByTransactionId(String transactionId);

    // Find recent bills (last 30 days)
    @Query("{ 'orderDate': { $gte: ?0 } }")
    List<Bill> findRecentBills(LocalDateTime thirtyDaysAgo);

    // Find bills by customer name (case-insensitive)
    List<Bill> findByCustomerNameContainingIgnoreCase(String customerName);

    // Find delivery orders
    List<Bill> findByIsDeliveryTrue();

    // Find bills with discount applied
    List<Bill> findByDiscountAmountGreaterThan(Double discountAmount);

    // Custom query to find bills by item category
    @Query("{ 'items.itemCategory': ?0 }")
    List<Bill> findBillsByItemCategory(String category);

    // Custom query to get sales summary for a date range
    @Query(value = "{ 'orderDate': { $gte: ?0, $lte: ?1 }, 'paymentStatus': 'PAID' }", fields = "{ 'totalAmount': 1, 'orderDate': 1, 'customerName': 1 }")
    List<Bill> findSalesReportByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    // Count bills by payment status
    long countByPaymentStatus(String paymentStatus);

    // Count bills by order status
    long countByOrderStatus(String orderStatus);

    // Calculate total revenue for paid bills
    @Query(value = "{ 'paymentStatus': 'PAID' }", fields = "{ 'totalAmount': 1 }")
    List<Bill> findPaidBillsForRevenue();

    // Find overdue bills (pending payment for more than specified days)
    @Query("{ 'paymentStatus': 'PENDING', 'orderDate': { $lt: ?0 } }")
    List<Bill> findOverdueBills(LocalDateTime cutoffDate);

    // Find bills by multiple payment statuses
    List<Bill> findByPaymentStatusIn(List<String> paymentStatuses);

    // Find top customers by total purchase amount
    @Query(value = "{ 'paymentStatus': 'PAID' }", sort = "{ 'totalAmount': -1 }")
    List<Bill> findTopBillsByAmount();
}
