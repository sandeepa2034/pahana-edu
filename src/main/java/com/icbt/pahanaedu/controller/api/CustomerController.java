package com.icbt.pahanaedu.controller.api;

import com.icbt.pahanaedu.model.Bill;
import com.icbt.pahanaedu.model.Customer;
import com.icbt.pahanaedu.model.Item;
import com.icbt.pahanaedu.service.CustomerService;
import com.icbt.pahanaedu.service.ItemService;
import com.icbt.pahanaedu.service.PdfBillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/customers/api")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private PdfBillService pdfBillService;

    /**
     * Process checkout and create order
     */
    @PostMapping("/checkout")
    public ResponseEntity<Map<String, Object>> processCheckout(@RequestBody Map<String, Object> orderData) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Extract customer information
            String fullName = (String) orderData.get("fullName");
            String phone = (String) orderData.get("phone");
            String email = (String) orderData.get("email");
            String address = (String) orderData.get("address");
            String paymentMethod = (String) orderData.get("paymentMethod");

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
            bill.setCustomerEmail(email);
            bill.setDeliveryAddress(address);
            bill.setOrderDate(LocalDateTime.now());
            bill.setPaymentMethod(paymentMethod);
            
            // Set payment status based on payment method
            if ("cash".equalsIgnoreCase(paymentMethod)) {
                bill.setPaymentStatus("PENDING");
                bill.setOrderStatus("PENDING");
            } else if ("card".equalsIgnoreCase(paymentMethod)) {
                bill.setPaymentStatus("PAID");
                bill.setOrderStatus("CONFIRMED");
                bill.setPaymentDate(LocalDateTime.now());
            }

            // Generate bill number
            bill.generateBillNumber();

            // Process cart items
            List<Bill.OrderItem> orderItems = new ArrayList<>();
            double totalAmount = 0.0;
            
            for (Map<String, Object> cartItem : cartItems) {
                String itemId = (String) cartItem.get("id");
                Double price = Double.parseDouble(cartItem.get("price").toString());
                
                Optional<Item> itemOpt = itemService.getItemById(itemId);
                if (itemOpt.isPresent()) {
                    Item item = itemOpt.get();
                    Bill.OrderItem orderItem = new Bill.OrderItem(item);
                    orderItems.add(orderItem);
                    totalAmount += price;
                }
            }

            bill.setItems(orderItems);
            bill.setSubtotal(totalAmount);
            bill.setTotalAmount(totalAmount);
            
            // Save bill
            Bill savedBill = customerService.saveBill(bill);

            // Prepare response
            response.put("success", true);
            response.put("message", "Order placed successfully!");
            response.put("billId", savedBill.getId());
            response.put("billNumber", savedBill.getBillNumber());
            response.put("totalAmount", savedBill.getTotalAmount());
            response.put("paymentStatus", savedBill.getPaymentStatus());
            response.put("orderStatus", savedBill.getOrderStatus());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error processing order: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Get order by bill ID for PDF generation
     */
    @GetMapping("/bill/{billId}")
    public ResponseEntity<Bill> getBill(@PathVariable String billId) {
        try {
            Bill bill = customerService.getBillById(billId);
            if (bill != null) {
                return ResponseEntity.ok(bill);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Download PDF invoice for a bill
     */
    @GetMapping("/bill/{billId}/pdf")
    public ResponseEntity<byte[]> downloadBillPdf(@PathVariable String billId) {
        try {
            Bill bill = customerService.getBillById(billId);
            if (bill == null) {
                return ResponseEntity.notFound().build();
            }

            // Generate PDF
            byte[] pdfBytes = pdfBillService.generateBillPdf(bill);

            // Set headers for PDF download
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "bill-" + bill.getBillNumber() + ".pdf");
            headers.setContentLength(pdfBytes.length);

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
