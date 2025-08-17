package com.icbt.pahanaedu.controller;

import com.icbt.pahanaedu.model.Bill;
import com.icbt.pahanaedu.model.User;
import com.icbt.pahanaedu.model.Item;
import com.icbt.pahanaedu.model.Customer;
import com.icbt.pahanaedu.service.UserService;
import com.icbt.pahanaedu.service.CustomerService;
import com.icbt.pahanaedu.repository.ItemRepository;
import com.icbt.pahanaedu.repository.BillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.time.LocalDateTime;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BillRepository billRepository;

    /**
     * Admin Dashboard
     */
    @GetMapping("/dashboard")
    public String adminDashboard(Authentication authentication, Model model) {
        String username = authentication.getName();
        User user = userService.findByUsername(username).orElse(null);
        
        if (user == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("user", user);
        model.addAttribute("username", username);
        
        // Add dashboard statistics
        Map<String, Object> stats = getDashboardStatistics();
        model.addAttribute("stats", stats);
        
        return "admin/dashboard";
    }

    /**
     * Admin Customers Page
     */
    @GetMapping("/customers")
    public String adminCustomers(
            @RequestParam(value = "search", required = false) String search,
            Authentication authentication,
            Model model) {
        
        String username = authentication.getName();
        User user = userService.findByUsername(username).orElse(null);
        
        if (user == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("user", user);
        model.addAttribute("username", username);
        
        // Add customer data based on search
        if (search != null && !search.trim().isEmpty()) {
            model.addAttribute("customers", customerService.searchCustomersByName(search));
            model.addAttribute("search", search);
        } else {
            model.addAttribute("customers", customerService.getAllCustomers());
        }
        
        return "admin/customers";
    }

    /**
     * Admin Orders Page
     */
    @GetMapping("/orders")
    public String adminOrders(Authentication authentication, Model model) {
        String username = authentication.getName();
        User user = userService.findByUsername(username).orElse(null);
        
        if (user == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("user", user);
        model.addAttribute("username", username);
        
        // Add orders data
        List<Bill> orders = billRepository.findAll();
        System.out.println("DEBUG: Found " + orders.size() + " orders in database");
        for (Bill order : orders) {
            System.out.println("DEBUG: Order ID: " + order.getId() + ", Customer: " + order.getCustomerName() + ", Total: " + order.getTotalAmount());
        }
        model.addAttribute("orders", orders);
        
        return "admin/orders";
    }

    /**
     * Admin Items/Books Page
     */
    @GetMapping("/items")
    public String adminItems(Authentication authentication, Model model) {
        String username = authentication.getName();
        User user = userService.findByUsername(username).orElse(null);
        
        if (user == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("user", user);
        model.addAttribute("username", username);
        
        // Add items data
        model.addAttribute("books", itemRepository.findAll());
        
        return "admin/items";
    }

    /**
     * Get dashboard statistics
     */
    private Map<String, Object> getDashboardStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            stats.put("totalUsers", userService.getTotalUsers());
            stats.put("activeUsers", userService.getActiveUsers());
            stats.put("adminCount", userService.getAdminCount());
            stats.put("userCount", userService.getUserCount());
            stats.put("totalCustomers", customerService.getAllCustomers().size());
            stats.put("totalOrders", billRepository.count());
            stats.put("totalItems", itemRepository.count());
            stats.put("pendingOrders", billRepository.countByOrderStatus("PENDING"));
            stats.put("paidOrders", billRepository.countByPaymentStatus("PAID"));
        } catch (Exception e) {
            // Handle any errors gracefully
            stats.put("totalUsers", 0L);
            stats.put("activeUsers", 0L);
            stats.put("adminCount", 0L);
            stats.put("userCount", 0L);
            stats.put("totalCustomers", 0L);
            stats.put("totalOrders", 0L);
            stats.put("totalItems", 0L);
            stats.put("pendingOrders", 0L);
            stats.put("paidOrders", 0L);
        }
        
        return stats;
    }

    // ========================================
    // ITEM/BOOK CRUD OPERATIONS
    // ========================================

    /**
     * Create new item/book
     */
    @PostMapping("/items/create")
    public String createItem(@Valid @ModelAttribute Item item,
                           BindingResult result,
                           @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                           Model model) {
        
        if (result.hasErrors()) {
            model.addAttribute("error", "Please fix the validation errors");
            return "redirect:/admin/items?error=validation";
        }

        try {
            // Handle image upload
            if (imageFile != null && !imageFile.isEmpty()) {
                String imageUrl = saveImage(imageFile);
                item.setImageUrl(imageUrl);
            }

            itemRepository.save(item);
            return "redirect:/admin/items?success=created";
        } catch (Exception e) {
            model.addAttribute("error", "Error creating item: " + e.getMessage());
            return "redirect:/admin/items?error=create";
        }
    }

    /**
     * Get item for editing (JSON response)
     */
    @GetMapping("/items/{id}/edit")
    @ResponseBody
    public ResponseEntity<Item> getItemForEdit(@PathVariable String id) {
        Optional<Item> item = itemRepository.findById(id);
        if (item.isPresent()) {
            return ResponseEntity.ok(item.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Update existing item/book
     */
    @PostMapping("/items/{id}/update")
    public String updateItem(@PathVariable String id,
                           @Valid @ModelAttribute Item updatedItem,
                           BindingResult result,
                           @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                           Model model) {
        
        if (result.hasErrors()) {
            return "redirect:/admin/items?error=validation";
        }

        try {
            Optional<Item> existingItemOpt = itemRepository.findById(id);
            if (!existingItemOpt.isPresent()) {
                return "redirect:/admin/items?error=notfound";
            }

            Item existingItem = existingItemOpt.get();
            
            // Update fields
            existingItem.setTitle(updatedItem.getTitle());
            existingItem.setAuthor(updatedItem.getAuthor());
            existingItem.setDescription(updatedItem.getDescription());
            existingItem.setPrice(updatedItem.getPrice());
            existingItem.setCategory(updatedItem.getCategory());

            // Handle image upload
            if (imageFile != null && !imageFile.isEmpty()) {
                String imageUrl = saveImage(imageFile);
                existingItem.setImageUrl(imageUrl);
            }

            itemRepository.save(existingItem);
            return "redirect:/admin/items?success=updated";
        } catch (Exception e) {
            return "redirect:/admin/items?error=update";
        }
    }

    /**
     * Delete item/book
     */
    @DeleteMapping("/items/{id}/delete")
    @ResponseBody
    public ResponseEntity<Map<String, String>> deleteItem(@PathVariable String id) {
        Map<String, String> response = new HashMap<>();
        
        try {
            Optional<Item> item = itemRepository.findById(id);
            if (!item.isPresent()) {
                response.put("error", "Item not found");
                return ResponseEntity.notFound().build();
            }

            itemRepository.deleteById(id);
            response.put("success", "Item deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "Error deleting item: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * View item details
     */
    @GetMapping("/items/{id}/view")
    public String viewItem(@PathVariable String id, Model model) {
        Optional<Item> item = itemRepository.findById(id);
        if (item.isPresent()) {
            model.addAttribute("item", item.get());
            return "admin/item-view";
        } else {
            return "redirect:/admin/items?error=notfound";
        }
    }

    // ========================================
    // CUSTOMER CRUD OPERATIONS
    // ========================================

    /**
     * Create new customer or admin user
     */
    @PostMapping("/customers/create")
    public String createCustomer(@Valid @ModelAttribute Customer customer,
                               BindingResult result,
                               @RequestParam("userRole") String userRole,
                               Model model) {
        
        if (result.hasErrors()) {
            return "redirect:/admin/customers?error=validation";
        }

        try {
            if ("ADMIN".equals(userRole)) {
                // Create admin user
                String username = customer.getEmail(); // Use email as username
                String phone = customer.getPhoneNumber();
                String defaultPassword = "admin123"; // You might want to generate this
                String role = "ADMIN";
                
                User savedUser = userService.registerUser(username, phone, defaultPassword, role);
                return "redirect:/admin/customers?success=admin_created";
                
            } else {
                // Create customer record
                customer.setRegistrationDate(LocalDateTime.now());
                Customer savedCustomer = customerService.saveCustomer(customer);
                return "redirect:/admin/customers?success=customer_created";
            }
        } catch (Exception e) {
            return "redirect:/admin/customers?error=create";
        }
    }

    /**
     * Get customer for editing (JSON response)
     */
    @GetMapping("/customers/{id}/edit")
    @ResponseBody
    public ResponseEntity<Customer> getCustomerForEdit(@PathVariable String id) {
        Optional<Customer> customer = customerService.getCustomerById(id);
        if (customer.isPresent()) {
            return ResponseEntity.ok(customer.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Update existing customer
     */
    @PostMapping("/customers/{id}/update")
    public String updateCustomer(@PathVariable String id,
                               @Valid @ModelAttribute Customer customer,
                               BindingResult result,
                               Model model) {
        
        if (result.hasErrors()) {
            return "redirect:/admin/customers?error=validation";
        }

        try {
            customer.setId(id);
            customerService.saveCustomer(customer);
            return "redirect:/admin/customers?success=updated";
        } catch (Exception e) {
            return "redirect:/admin/customers?error=update";
        }
    }

    /**
     * Delete customer
     */
    @DeleteMapping("/customers/{id}/delete")
    @ResponseBody
    public ResponseEntity<Map<String, String>> deleteCustomer(@PathVariable String id) {
        Map<String, String> response = new HashMap<>();
        
        try {
            customerService.deleteCustomer(id);
            response.put("success", "Customer deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "Error deleting customer: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Helper method to save uploaded images
     */
    private String saveImage(MultipartFile file) throws IOException {
        // Create directory if it doesn't exist
        String uploadDir = "src/main/resources/static/images/books/";
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String filename = System.currentTimeMillis() + extension;

        // Save file
        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Return relative URL
        return "/images/books/" + filename;
    }
}
