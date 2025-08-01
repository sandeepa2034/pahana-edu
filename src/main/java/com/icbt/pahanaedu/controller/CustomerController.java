package com.icbt.pahanaedu.controller;

import com.icbt.pahanaedu.model.Customer;
import com.icbt.pahanaedu.model.Bill;
import com.icbt.pahanaedu.model.Item;
import com.icbt.pahanaedu.model.User;
import com.icbt.pahanaedu.repository.BillRepository;
import com.icbt.pahanaedu.service.CustomerService;
import com.icbt.pahanaedu.service.ItemService;
import com.icbt.pahanaedu.service.UserService;
import com.icbt.pahanaedu.service.PdfBillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @Autowired
    private BillRepository billRepository;
    
    @Autowired
    private PdfBillService pdfBillService;

    /**
     * List all customers (Admin only)
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String listCustomers(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "search", required = false) String search,
            Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("fullName"));
        Page<Customer> customers;

        if (search != null && !search.trim().isEmpty()) {
            customers = customerService.searchCustomers(search.trim(), pageable);
            model.addAttribute("search", search);
        } else {
            customers = customerService.getAllCustomers(pageable);
        }

        model.addAttribute("customers", customers);
        model.addAttribute("totalCustomers", customerService.getTotalCustomers());
        return "admin/customers";
    }

    /**
     * Show customer details (Admin only)
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String viewCustomer(@PathVariable String id, Model model) {
        Optional<Customer> customer = customerService.getCustomerById(id);
        if (customer.isEmpty()) {
            return "redirect:/customers?error=Customer not found";
        }

        List<Bill> customerOrders = customerService.getCustomerOrderHistory(customer.get().getPhoneNumber());
        
        model.addAttribute("customer", customer.get());
        model.addAttribute("orders", customerOrders);
        return "admin/customer-details";
    }

    /**
     * Show add customer form (Admin only)
     */
    @GetMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public String showAddForm(Model model) {
        model.addAttribute("customer", new Customer());
        return "admin/customer-form";
    }

    /**
     * Show edit customer form (Admin only)
     */
    @GetMapping("/{id}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public String showEditForm(@PathVariable String id, Model model) {
        Optional<Customer> customer = customerService.getCustomerById(id);
        if (customer.isEmpty()) {
            return "redirect:/customers?error=Customer not found";
        }
        
        model.addAttribute("customer", customer.get());
        return "admin/customer-form";
    }

    /**
     * Save customer (Admin only)
     */
    @PostMapping("/save")
    @PreAuthorize("hasRole('ADMIN')")
    public String saveCustomer(@Valid @ModelAttribute Customer customer,
                              BindingResult result,
                              RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            return "admin/customer-form";
        }

        try {
            Customer savedCustomer = customerService.saveCustomer(customer);
            redirectAttributes.addFlashAttribute("success", 
                "Customer " + savedCustomer.getFullName() + " saved successfully!");
            return "redirect:/customers";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error saving customer: " + e.getMessage());
            return "redirect:/customers/add";
        }
    }

    /**
     * Delete customer (Admin only)
     */
    @PostMapping("/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteCustomer(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            customerService.deleteCustomer(id);
            redirectAttributes.addFlashAttribute("success", "Customer deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting customer: " + e.getMessage());
        }
        return "redirect:/customers";
    }

    /**
     * Guest checkout API endpoint
     */
    @PostMapping("/api/guest-checkout")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> guestCheckout(@RequestBody Map<String, Object> orderData) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Extract customer information
            @SuppressWarnings("unchecked")
            Map<String, String> customerData = (Map<String, String>) orderData.get("customer");
            String fullName = customerData.get("fullName");
            String phone = customerData.get("phone");
            String email = customerData.get("email");
            String address = customerData.get("address");

            // Extract cart items
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> cartItems = (List<Map<String, Object>>) orderData.get("items");

            // Find or create customer
            Customer customer = customerService.findOrCreateCustomer(fullName, phone, email, address);

            // Create bill
            Bill bill = new Bill();
            bill.setCustomer(customer);
            bill.setCustomerPhone(phone);
            bill.setCustomerName(fullName);
            bill.setOrderDate(LocalDateTime.now());
            bill.setOrderStatus("COMPLETED");

            double totalAmount = 0.0;
            for (Map<String, Object> cartItem : cartItems) {
                String itemId = (String) cartItem.get("itemId");
                Integer quantity = (Integer) cartItem.get("quantity");
                
                Optional<Item> item = itemService.getItemById(itemId);
                if (item.isPresent()) {
                    Item currentItem = item.get();
                    bill.addItem(currentItem, quantity);
                    totalAmount += currentItem.getPrice() * quantity;
                }
            }

            bill.setTotalAmount(totalAmount);

            // Save the bill
            Bill savedBill = customerService.saveBill(bill);

            response.put("success", true);
            response.put("message", "Order placed successfully!");
            response.put("orderId", savedBill.getId());
            response.put("totalAmount", totalAmount);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error processing order: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * API endpoint to check if phone number exists
     */
    @GetMapping("/api/check-phone")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> checkPhoneExists(@RequestParam String phone) {
        Map<String, Object> response = new HashMap<>();
        Optional<Customer> customer = customerService.findByPhone(phone);
        
        response.put("exists", customer.isPresent());
        if (customer.isPresent()) {
            response.put("customer", Map.of(
                "fullName", customer.get().getFullName(),
                "email", customer.get().getEmail(),
                "address", customer.get().getAddress()
            ));
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * Process checkout and create order
     */
    @PostMapping("/api/checkout")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> processCheckout(@RequestBody Map<String, Object> orderData) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Extract customer information
            String fullName = (String) orderData.get("fullName");
            String phone = (String) orderData.get("phone");
            String email = (String) orderData.get("email");
            String address = (String) orderData.get("address");
            
            // Find or create customer
            Customer customer = customerService.findOrCreateCustomer(fullName, phone, email, address);
            
            // Extract cart items
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> cartItems = (List<Map<String, Object>>) orderData.get("items");
            
            if (cartItems == null || cartItems.isEmpty()) {
                response.put("success", false);
                response.put("message", "Cart is empty");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Create bill
            Bill bill = new Bill();
            bill.setCustomer(customer);
            bill.setCustomerName(customer.getFullName());
            bill.setCustomerPhone(customer.getPhoneNumber());
            bill.setCustomerEmail(customer.getEmail());
            bill.setOrderDate(LocalDateTime.now());
            bill.setOrderStatus("CONFIRMED");
            bill.setPaymentStatus("PENDING");
            bill.generateBillNumber(); // Generate unique bill number
            
            double totalAmount = 0.0;
            List<Bill.OrderItem> billItems = new java.util.ArrayList<>();
            
            for (Map<String, Object> cartItem : cartItems) {
                String itemId = (String) cartItem.get("id");
                String title = (String) cartItem.get("title");
                String author = (String) cartItem.get("author");
                Double price = ((Number) cartItem.get("price")).doubleValue();
                Integer quantity = ((Number) cartItem.get("quantity")).intValue();
                
                Bill.OrderItem billItem = new Bill.OrderItem();
                billItem.setItemId(itemId);
                billItem.setItemTitle(title);
                billItem.setItemAuthor(author);
                billItem.setItemPrice(price);
                billItem.setQuantity(quantity);
                billItem.setLineTotal(price * quantity);
                
                billItems.add(billItem);
                totalAmount += billItem.getLineTotal();
            }
            
            bill.setItems(billItems);
            bill.setSubtotal(totalAmount);
            bill.setTotalAmount(totalAmount);
            bill.calculateTotals(); // Ensure all totals are calculated
            
            // Save the bill
            Bill savedBill = billRepository.save(bill);
            
            // Update customer purchase statistics
            customerService.updatePurchaseStats(customer.getId(), savedBill.getId(), totalAmount);
            
            response.put("success", true);
            response.put("message", "Order placed successfully!");
            response.put("billId", savedBill.getId());
            response.put("billNumber", savedBill.getBillNumber());
            response.put("totalAmount", totalAmount);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error processing order: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Download bill as PDF
     */
    @GetMapping("/api/bill/{billId}/pdf")
    @ResponseBody
    public ResponseEntity<byte[]> downloadBillPdf(@PathVariable String billId) {
        try {
            // Find the bill
            Optional<Bill> billOpt = billRepository.findById(billId);
            if (billOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            Bill bill = billOpt.get();
            
            // Generate PDF
            byte[] pdfData = pdfBillService.generateBillPdf(bill);
            
            // Set headers for PDF download
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "bill-" + bill.getBillNumber() + ".pdf");
            headers.setContentLength(pdfData.length);
            
            return new ResponseEntity<>(pdfData, headers, HttpStatus.OK);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
