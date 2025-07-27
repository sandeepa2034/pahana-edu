package com.icbt.pahanaedu.controller;

import com.icbt.pahanaedu.model.User;
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

import java.util.HashMap;
import java.util.Map;

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
        model.addAttribute("orders", billRepository.findAll());
        
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
        model.addAttribute("items", itemRepository.findAll());
        
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
}
