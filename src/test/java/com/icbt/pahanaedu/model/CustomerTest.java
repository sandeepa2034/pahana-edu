package com.icbt.pahanaedu.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Customer model class
 */
class CustomerTest {

    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = new Customer("John", "Doe", "john.doe@example.com", "1234567890");
    }

    @Test
    void testCustomerCreation() {
        assertNotNull(customer);
        assertEquals("John", customer.getFirstName());
        assertEquals("Doe", customer.getLastName());
        assertEquals("john.doe@example.com", customer.getEmail());
        assertEquals("1234567890", customer.getPhoneNumber());
        assertNotNull(customer.getRegistrationDate());
        assertEquals("Sri Lanka", customer.getCountry());
        assertTrue(customer.getActive());
        assertEquals(0.0, customer.getTotalSpent());
        assertEquals(0, customer.getTotalOrders());
    }

    @Test
    void testDefaultConstructor() {
        Customer emptyCustomer = new Customer();
        assertNotNull(emptyCustomer);
        assertNull(emptyCustomer.getFirstName());
        assertNull(emptyCustomer.getLastName());
        assertNull(emptyCustomer.getEmail());
        assertNotNull(emptyCustomer.getRegistrationDate());
    }

    @Test
    void testSettersAndGetters() {
        customer.setFirstName("Jane");
        customer.setLastName("Smith");
        customer.setEmail("jane.smith@example.com");
        customer.setPhoneNumber("9876543210");
        customer.setAddress("456 Oak Ave");
        customer.setCity("Colombo");

        assertEquals("Jane", customer.getFirstName());
        assertEquals("Smith", customer.getLastName());
        assertEquals("jane.smith@example.com", customer.getEmail());
        assertEquals("9876543210", customer.getPhoneNumber());
        assertEquals("456 Oak Ave", customer.getAddress());
        assertEquals("Colombo", customer.getCity());
    }

    @Test
    void testFullName() {
        assertEquals("John Doe", customer.getFullName());
    }

    @Test
    void testUpdateSpentAmount() {
        customer.updateSpentAmount(25.99);
        assertEquals(25.99, customer.getTotalSpent());
        assertNotNull(customer.getLastPurchaseDate());
        
        customer.updateSpentAmount(15.50);
        assertEquals(41.49, customer.getTotalSpent(), 0.01);
    }

    @Test
    void testAddToOrderHistory() {
        customer.addToOrderHistory("ORDER-001");
        customer.addToOrderHistory("ORDER-002");
        
        assertEquals(2, customer.getOrderHistory().size());
        assertEquals(2, customer.getTotalOrders());
        assertTrue(customer.getOrderHistory().contains("ORDER-001"));
        assertTrue(customer.getOrderHistory().contains("ORDER-002"));
    }

    @Test
    void testToString() {
        String toString = customer.toString();
        assertTrue(toString.contains("John"));
        assertTrue(toString.contains("Doe"));
        assertTrue(toString.contains("john.doe@example.com"));
    }
}
