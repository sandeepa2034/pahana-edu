package com.icbt.pahanaedu.service;

import com.icbt.pahanaedu.model.Customer;
import com.icbt.pahanaedu.model.Bill;
import com.icbt.pahanaedu.repository.CustomerRepository;
import com.icbt.pahanaedu.repository.BillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private BillRepository billRepository;

    // Create a new customer
    public Customer createCustomer(Customer customer) {
        // Check if email or phone already exists
        if (customerRepository.existsByEmail(customer.getEmail())) {
            throw new RuntimeException("Customer with email " + customer.getEmail() + " already exists");
        }
        if (customerRepository.existsByPhoneNumber(customer.getPhoneNumber())) {
            throw new RuntimeException("Customer with phone number " + customer.getPhoneNumber() + " already exists");
        }

        customer.setRegistrationDate(LocalDateTime.now());
        return customerRepository.save(customer);
    }

    // Get all customers
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    // Get customers with pagination
    public Page<Customer> getAllCustomers(Pageable pageable) {
        return customerRepository.findAll(pageable);
    }

    // Get customer by ID
    public Optional<Customer> getCustomerById(String id) {
        return customerRepository.findById(id);
    }

    // Get customer by email
    public Optional<Customer> getCustomerByEmail(String email) {
        return customerRepository.findByEmail(email);
    }

    // Get customer by phone number
    public Optional<Customer> getCustomerByPhoneNumber(String phoneNumber) {
        return customerRepository.findByPhoneNumber(phoneNumber);
    }

    // Update customer
    public Customer updateCustomer(String id, Customer updatedCustomer) {
        Optional<Customer> existingCustomer = customerRepository.findById(id);
        if (existingCustomer.isPresent()) {
            Customer customer = existingCustomer.get();

            // Update fields
            customer.setFirstName(updatedCustomer.getFirstName());
            customer.setLastName(updatedCustomer.getLastName());
            customer.setEmail(updatedCustomer.getEmail());
            customer.setPhoneNumber(updatedCustomer.getPhoneNumber());
            customer.setAddress(updatedCustomer.getAddress());
            customer.setCity(updatedCustomer.getCity());
            customer.setPostalCode(updatedCustomer.getPostalCode());
            customer.setCountry(updatedCustomer.getCountry());
            customer.setPreferredCategories(updatedCustomer.getPreferredCategories());
            customer.setEmailNotifications(updatedCustomer.getEmailNotifications());
            customer.setSmsNotifications(updatedCustomer.getSmsNotifications());

            return customerRepository.save(customer);
        }
        throw new RuntimeException("Customer not found with id: " + id);
    }

    // Delete customer
    public void deleteCustomer(String id) {
        if (customerRepository.existsById(id)) {
            customerRepository.deleteById(id);
        } else {
            throw new RuntimeException("Customer not found with id: " + id);
        }
    }

    // Deactivate customer (soft delete)
    public Customer deactivateCustomer(String id) {
        Optional<Customer> customerOpt = customerRepository.findById(id);
        if (customerOpt.isPresent()) {
            Customer customer = customerOpt.get();
            customer.setActive(false);
            return customerRepository.save(customer);
        }
        throw new RuntimeException("Customer not found with id: " + id);
    }

    // Reactivate customer
    public Customer reactivateCustomer(String id) {
        Optional<Customer> customerOpt = customerRepository.findById(id);
        if (customerOpt.isPresent()) {
            Customer customer = customerOpt.get();
            customer.setActive(true);
            return customerRepository.save(customer);
        }
        throw new RuntimeException("Customer not found with id: " + id);
    }

    // Search customers by name
    public List<Customer> searchCustomersByName(String searchTerm) {
        return customerRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
                searchTerm, searchTerm);
    }

    // Get active customers only
    public List<Customer> getActiveCustomers() {
        return customerRepository.findByActiveTrue();
    }

    // Get customers by city
    public List<Customer> getCustomersByCity(String city) {
        return customerRepository.findByCity(city);
    }

    // Get customers by spending range
    public List<Customer> getCustomersBySpendingRange(Double minSpent, Double maxSpent) {
        return customerRepository.findByTotalSpentBetween(minSpent, maxSpent);
    }

    // Get recent customers (registered in last N days)
    public List<Customer> getRecentCustomers(int days) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(days);
        return customerRepository.findByRegistrationDateBetween(cutoffDate, LocalDateTime.now());
    }

    // Get customers who made purchases recently
    public List<Customer> getRecentlyActivePurchasers(int days) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(days);
        return customerRepository.findByLastPurchaseDateAfter(cutoffDate);
    }

    // Get inactive customers (no recent purchases)
    public List<Customer> getInactiveCustomers(int daysSinceLastPurchase) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysSinceLastPurchase);
        return customerRepository.findInactiveCustomers(cutoffDate);
    }

    // Get top spending customers
    public List<Customer> getTopSpendingCustomers(Double minimumSpent) {
        return customerRepository.findTopSpendingCustomers(minimumSpent);
    }

    // Get customers with multiple orders
    public List<Customer> getCustomersWithMultipleOrders(Integer minimumOrders) {
        return customerRepository.findCustomersWithMultipleOrders(minimumOrders);
    }

    // Update customer's purchase statistics
    public Customer updatePurchaseStats(String customerId, String billId, Double amount) {
        Optional<Customer> customerOpt = customerRepository.findById(customerId);
        if (customerOpt.isPresent()) {
            Customer customer = customerOpt.get();
            customer.addToOrderHistory(billId);
            customer.updateSpentAmount(amount);
            return customerRepository.save(customer);
        }
        throw new RuntimeException("Customer not found with id: " + customerId);
    }

    // Check if email is available
    public boolean isEmailAvailable(String email) {
        return !customerRepository.existsByEmail(email);
    }

    // Check if phone number is available
    public boolean isPhoneNumberAvailable(String phoneNumber) {
        return !customerRepository.existsByPhoneNumber(phoneNumber);
    }

    // Get customer statistics
    public CustomerStats getCustomerStatistics() {
        long totalCustomers = customerRepository.count();
        long activeCustomers = customerRepository.countByActiveTrue();

        CustomerStats stats = new CustomerStats();
        stats.setTotalCustomers(totalCustomers);
        stats.setActiveCustomers(activeCustomers);
        stats.setInactiveCustomers(totalCustomers - activeCustomers);

        return stats;
    }

    // Inner class for customer statistics
    public static class CustomerStats {
        private long totalCustomers;
        private long activeCustomers;
        private long inactiveCustomers;

        // Getters and setters
        public long getTotalCustomers() {
            return totalCustomers;
        }

        public void setTotalCustomers(long totalCustomers) {
            this.totalCustomers = totalCustomers;
        }

        public long getActiveCustomers() {
            return activeCustomers;
        }

        public void setActiveCustomers(long activeCustomers) {
            this.activeCustomers = activeCustomers;
        }

        public long getInactiveCustomers() {
            return inactiveCustomers;
        }

        public void setInactiveCustomers(long inactiveCustomers) {
            this.inactiveCustomers = inactiveCustomers;
        }
    }

    // Initialize sample customer data
    public void initializeSampleCustomers() {
        if (customerRepository.count() == 0) {
            List<Customer> sampleCustomers = List.of(
                    new Customer("John", "Doe", "john.doe@email.com", "+94771234567"),
                    new Customer("Jane", "Smith", "jane.smith@email.com", "+94772345678"),
                    new Customer("Michael", "Johnson", "michael.johnson@email.com", "+94773456789"),
                    new Customer("Sarah", "Williams", "sarah.williams@email.com", "+94774567890"),
                    new Customer("David", "Brown", "david.brown@email.com", "+94775678901"));

            // Set additional properties for sample customers
            for (int i = 0; i < sampleCustomers.size(); i++) {
                Customer customer = sampleCustomers.get(i);
                customer.setCity("Colombo");
                customer.setCountry("Sri Lanka");
                customer.setAddress("Sample Address " + (i + 1));
                customer.setPostalCode("1000" + i);
            }

            customerRepository.saveAll(sampleCustomers);
        }
    }
    
    // New methods for CustomerController support
    
    /**
     * Save customer (create or update)
     */
    public Customer saveCustomer(Customer customer) {
        if (customer.getId() == null) {
            customer.setRegistrationDate(LocalDateTime.now());
        }
        return customerRepository.save(customer);
    }
    
    /**
     * Search customers with pagination
     */
    public Page<Customer> searchCustomers(String searchTerm, Pageable pageable) {
        return customerRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                searchTerm, searchTerm, searchTerm, pageable);
    }
    
    /**
     * Get total customers count
     */
    public long getTotalCustomers() {
        return customerRepository.count();
    }
    
    /**
     * Find customer by phone (using existing phoneNumber field)
     */
    public Optional<Customer> findByPhone(String phone) {
        return customerRepository.findByPhoneNumber(phone);
    }
    
    /**
     * Find or create customer for guest checkout
     */
    public Customer findOrCreateCustomer(String fullName, String phone, String email, String address) {
        Optional<Customer> existingCustomer = findByPhone(phone);
        
        if (existingCustomer.isPresent()) {
            Customer customer = existingCustomer.get();
            // Update customer info if provided
            if (email != null && !email.trim().isEmpty()) {
                customer.setEmail(email.trim());
            }
            if (address != null && !address.trim().isEmpty()) {
                customer.setAddress(address.trim());
            }
            return customerRepository.save(customer);
        } else {
            // Create new customer
            Customer newCustomer = new Customer();
            
            // Parse full name into first and last name
            String[] nameParts = fullName.trim().split("\\s+", 2);
            newCustomer.setFirstName(nameParts[0]);
            if (nameParts.length > 1) {
                newCustomer.setLastName(nameParts[1]);
            } else {
                newCustomer.setLastName("");
            }
            
            newCustomer.setPhoneNumber(phone);
            newCustomer.setEmail(email);
            newCustomer.setAddress(address);
            newCustomer.setRegistrationDate(LocalDateTime.now());
            newCustomer.setActive(true);
            
            return customerRepository.save(newCustomer);
        }
    }
    
    /**
     * Save bill
     */
    public Bill saveBill(Bill bill) {
        return billRepository.save(bill);
    }
    
    /**
     * Get customer order history by phone
     */
    public List<Bill> getCustomerOrderHistory(String phone) {
        return billRepository.findByCustomerPhoneOrderByOrderDateDesc(phone);
    }
    
    /**
     * Get orders by customer name (simplified approach)
     */
    public List<Bill> getOrdersByCustomerName(String customerName) {
        return billRepository.findByCustomerNameContainingIgnoreCaseOrderByOrderDateDesc(customerName);
    }
}
