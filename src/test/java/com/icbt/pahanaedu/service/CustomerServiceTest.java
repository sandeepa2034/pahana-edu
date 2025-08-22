package com.icbt.pahanaedu.service;

import com.icbt.pahanaedu.model.Customer;
import com.icbt.pahanaedu.model.Bill;
import com.icbt.pahanaedu.repository.CustomerRepository;
import com.icbt.pahanaedu.repository.BillRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CustomerService
 */
@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private BillRepository billRepository;

    @InjectMocks
    private CustomerService customerService;

    private Customer testCustomer;
    private Bill testBill;

    @BeforeEach
    void setUp() {
        testCustomer = new Customer("John", "Doe", "john.doe@example.com", "1234567890");
        testCustomer.setId("test-id-123");
        testCustomer.setAddress("123 Test Street");
        testCustomer.setCity("Colombo");

        testBill = new Bill();
        testBill.setId("bill-123");
        testBill.setCustomerPhone("1234567890");
        testBill.setCustomerName("John Doe");
        testBill.setTotalAmount(150.0);
    }

    @Test
    void testGetAllCustomers() {
        // Arrange
        List<Customer> customers = Arrays.asList(testCustomer);
        when(customerRepository.findAll()).thenReturn(customers);

        // Act
        List<Customer> result = customerService.getAllCustomers();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testCustomer, result.get(0));
        verify(customerRepository, times(1)).findAll();
    }

    @Test
    void testGetAllCustomersWithPagination() {
        // Arrange
        List<Customer> customers = Arrays.asList(testCustomer);
        Page<Customer> customerPage = new PageImpl<>(customers);
        Pageable pageable = PageRequest.of(0, 10);
        when(customerRepository.findAll(pageable)).thenReturn(customerPage);

        // Act
        Page<Customer> result = customerService.getAllCustomers(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testCustomer, result.getContent().get(0));
        verify(customerRepository, times(1)).findAll(pageable);
    }

    @Test
    void testGetCustomerById() {
        // Arrange
        when(customerRepository.findById("test-id-123")).thenReturn(Optional.of(testCustomer));

        // Act
        Optional<Customer> result = customerService.getCustomerById("test-id-123");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testCustomer, result.get());
        verify(customerRepository, times(1)).findById("test-id-123");
    }

    @Test
    void testGetCustomerByIdNotFound() {
        // Arrange
        when(customerRepository.findById("non-existent")).thenReturn(Optional.empty());

        // Act
        Optional<Customer> result = customerService.getCustomerById("non-existent");

        // Assert
        assertFalse(result.isPresent());
        verify(customerRepository, times(1)).findById("non-existent");
    }

    @Test
    void testGetCustomerByPhoneNumber() {
        // Arrange
        when(customerRepository.findByPhoneNumber("1234567890")).thenReturn(Optional.of(testCustomer));

        // Act
        Optional<Customer> result = customerService.getCustomerByPhoneNumber("1234567890");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testCustomer, result.get());
        verify(customerRepository, times(1)).findByPhoneNumber("1234567890");
    }

    @Test
    void testSaveCustomer() {
        // Arrange
        when(customerRepository.save(testCustomer)).thenReturn(testCustomer);

        // Act
        Customer result = customerService.saveCustomer(testCustomer);

        // Assert
        assertNotNull(result);
        assertEquals(testCustomer, result);
        verify(customerRepository, times(1)).save(testCustomer);
    }

    @Test
    void testDeleteCustomer() {
        // Arrange
        when(customerRepository.existsById("test-id-123")).thenReturn(true);
        doNothing().when(customerRepository).deleteById("test-id-123");

        // Act
        customerService.deleteCustomer("test-id-123");

        // Assert
        verify(customerRepository, times(1)).existsById("test-id-123");
        verify(customerRepository, times(1)).deleteById("test-id-123");
    }

    @Test
    void testSearchCustomersByName() {
        // Arrange
        List<Customer> customers = Arrays.asList(testCustomer);
        when(customerRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase("John", "John"))
                .thenReturn(customers);

        // Act
        List<Customer> result = customerService.searchCustomersByName("John");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testCustomer, result.get(0));
        verify(customerRepository, times(1))
                .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase("John", "John");
    }

    @Test
    void testFindOrCreateCustomerExisting() {
        // Arrange
        when(customerRepository.findByPhoneNumber("1234567890")).thenReturn(Optional.of(testCustomer));
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);

        // Act
        Customer result = customerService.findOrCreateCustomer("John Doe", "1234567890", 
                "john.doe@example.com", "123 Test Street");

        // Assert
        assertNotNull(result);
        assertEquals(testCustomer, result);
        verify(customerRepository, times(1)).findByPhoneNumber("1234567890");
        verify(customerRepository, times(1)).save(any(Customer.class)); // Updated customer is saved
    }

    @Test
    void testFindOrCreateCustomerNew() {
        // Arrange
        when(customerRepository.findByPhoneNumber("9876543210")).thenReturn(Optional.empty());
        Customer newCustomer = new Customer("Jane", "Smith", "jane.smith@example.com", "9876543210");
        when(customerRepository.save(any(Customer.class))).thenReturn(newCustomer);

        // Act
        Customer result = customerService.findOrCreateCustomer("Jane Smith", "9876543210", 
                "jane.smith@example.com", "456 New Street");

        // Assert
        assertNotNull(result);
        assertEquals(newCustomer, result);
        verify(customerRepository, times(1)).findByPhoneNumber("9876543210");
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void testSaveBill() {
        // Arrange
        when(billRepository.save(testBill)).thenReturn(testBill);

        // Act
        Bill result = customerService.saveBill(testBill);

        // Assert
        assertNotNull(result);
        assertEquals(testBill, result);
        verify(billRepository, times(1)).save(testBill);
    }

    @Test
    void testGetCustomerOrderHistory() {
        // Arrange
        List<Bill> bills = Arrays.asList(testBill);
        when(billRepository.findByCustomerPhoneOrderByOrderDateDesc("1234567890")).thenReturn(bills);

        // Act
        List<Bill> result = customerService.getCustomerOrderHistory("1234567890");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testBill, result.get(0));
        verify(billRepository, times(1)).findByCustomerPhoneOrderByOrderDateDesc("1234567890");
    }

    @Test
    void testGetOrdersByCustomerName() {
        // Arrange
        List<Bill> bills = Arrays.asList(testBill);
        when(billRepository.findByCustomerNameContainingIgnoreCaseOrderByOrderDateDesc("John Doe")).thenReturn(bills);

        // Act
        List<Bill> result = customerService.getOrdersByCustomerName("John Doe");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testBill, result.get(0));
        verify(billRepository, times(1)).findByCustomerNameContainingIgnoreCaseOrderByOrderDateDesc("John Doe");
    }

    @Test
    void testIsPhoneNumberAvailable() {
        // Arrange
        when(customerRepository.existsByPhoneNumber("1234567890")).thenReturn(false);

        // Act
        boolean result = customerService.isPhoneNumberAvailable("1234567890");

        // Assert
        assertTrue(result);
        verify(customerRepository, times(1)).existsByPhoneNumber("1234567890");
    }

    @Test
    void testPhoneNotAvailable() {
        // Arrange
        when(customerRepository.existsByPhoneNumber("9999999999")).thenReturn(true);

        // Act
        boolean result = customerService.isPhoneNumberAvailable("9999999999");

        // Assert
        assertFalse(result);
        verify(customerRepository, times(1)).existsByPhoneNumber("9999999999");
    }
}
