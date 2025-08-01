package com.icbt.pahanaedu.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "bills")
public class Bill {

    @Id
    private String id;
    
    // Bill number for display and reference
    private String billNumber;

    // Customer information
    @DBRef
    private Customer customer;
    private String customerName;
    private String customerEmail;
    private String customerPhone;

    // Order details
    @NotNull(message = "Order items cannot be null")
    private List<OrderItem> items;

    // Financial information
    @NotNull(message = "Subtotal is required")
    @Positive(message = "Subtotal must be positive")
    private Double subtotal;

    private Double taxRate = 0.0; // Tax rate as percentage (e.g., 0.15 for 15%)
    private Double taxAmount = 0.0;
    private Double discountAmount = 0.0;
    private String discountCode;

    @NotNull(message = "Total amount is required")
    @Positive(message = "Total amount must be positive")
    private Double totalAmount;

    // Payment information
    private String paymentMethod; // CASH, CARD, BANK_TRANSFER, DIGITAL_WALLET
    private String paymentStatus = "PENDING"; // PENDING, PAID, FAILED, REFUNDED
    private String transactionId;
    private LocalDateTime paymentDate;

    // Order tracking
    private String orderStatus = "PROCESSING"; // PROCESSING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED
    private LocalDateTime orderDate;
    private LocalDateTime deliveryDate;

    // Additional information
    private String notes;
    private String deliveryAddress;
    private Boolean isDelivery = false;
    private Double deliveryCharge = 0.0;

    // Audit fields
    private String createdBy;
    private LocalDateTime createdDate;
    private String lastModifiedBy;
    private LocalDateTime lastModifiedDate;

    // Inner class for order items
    public static class OrderItem {
        @DBRef
        private Item item;
        private String itemId;
        private String itemTitle;
        private String itemAuthor;
        private String itemCategory;
        private Double itemPrice;
        private Integer quantity;
        private Double lineTotal;

        // Default constructor
        public OrderItem() {
        }

        // Constructor
        public OrderItem(Item item, Integer quantity) {
            this.item = item;
            this.itemId = item.getId();
            this.itemTitle = item.getTitle();
            this.itemAuthor = item.getAuthor();
            this.itemCategory = item.getCategory();
            this.itemPrice = item.getPrice();
            this.quantity = quantity;
            this.lineTotal = item.getPrice() * quantity;
        }

        // Getters and Setters
        public Item getItem() {
            return item;
        }

        public void setItem(Item item) {
            this.item = item;
        }

        public String getItemId() {
            return itemId;
        }

        public void setItemId(String itemId) {
            this.itemId = itemId;
        }

        public String getItemTitle() {
            return itemTitle;
        }

        public void setItemTitle(String itemTitle) {
            this.itemTitle = itemTitle;
        }

        public String getItemAuthor() {
            return itemAuthor;
        }

        public void setItemAuthor(String itemAuthor) {
            this.itemAuthor = itemAuthor;
        }

        public String getItemCategory() {
            return itemCategory;
        }

        public void setItemCategory(String itemCategory) {
            this.itemCategory = itemCategory;
        }

        public Double getItemPrice() {
            return itemPrice;
        }

        public void setItemPrice(Double itemPrice) {
            this.itemPrice = itemPrice;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
            this.lineTotal = this.itemPrice * quantity;
        }

        public Double getLineTotal() {
            return lineTotal;
        }

        public void setLineTotal(Double lineTotal) {
            this.lineTotal = lineTotal;
        }
    }

    // Default constructor
    public Bill() {
        this.orderDate = LocalDateTime.now();
        this.createdDate = LocalDateTime.now();
    }

    // Constructor with customer
    public Bill(Customer customer) {
        this();
        this.customer = customer;
        this.customerName = customer.getFullName();
        this.customerEmail = customer.getEmail();
        this.customerPhone = customer.getPhoneNumber();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
        if (customer != null) {
            this.customerName = customer.getFullName();
            this.customerEmail = customer.getEmail();
            this.customerPhone = customer.getPhoneNumber();
        }
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
        calculateTotals();
    }

    public Double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }

    public Double getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(Double taxRate) {
        this.taxRate = taxRate;
        calculateTotals();
    }

    public Double getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(Double taxAmount) {
        this.taxAmount = taxAmount;
    }

    public Double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(Double discountAmount) {
        this.discountAmount = discountAmount;
        calculateTotals();
    }

    public String getDiscountCode() {
        return discountCode;
    }

    public void setDiscountCode(String discountCode) {
        this.discountCode = discountCode;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public LocalDateTime getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(LocalDateTime deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public Boolean getIsDelivery() {
        return isDelivery;
    }

    public void setIsDelivery(Boolean isDelivery) {
        this.isDelivery = isDelivery;
    }

    public Double getDeliveryCharge() {
        return deliveryCharge;
    }

    public void setDeliveryCharge(Double deliveryCharge) {
        this.deliveryCharge = deliveryCharge;
        calculateTotals();
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public LocalDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    // Helper methods
    public void calculateTotals() {
        if (items != null && !items.isEmpty()) {
            this.subtotal = items.stream()
                    .mapToDouble(OrderItem::getLineTotal)
                    .sum();
        } else {
            this.subtotal = 0.0;
        }

        this.taxAmount = this.subtotal * (this.taxRate / 100);

        this.totalAmount = this.subtotal + this.taxAmount +
                (this.deliveryCharge != null ? this.deliveryCharge : 0.0) -
                (this.discountAmount != null ? this.discountAmount : 0.0);
    }

    public void addItem(Item item, Integer quantity) {
        if (this.items == null) {
            this.items = new java.util.ArrayList<>();
        }
        this.items.add(new OrderItem(item, quantity));
        calculateTotals();
    }

    public Integer getTotalItems() {
        if (items == null)
            return 0;
        return items.stream().mapToInt(OrderItem::getQuantity).sum();
    }

    public void markAsPaid(String paymentMethod, String transactionId) {
        this.paymentStatus = "PAID";
        this.paymentMethod = paymentMethod;
        this.transactionId = transactionId;
        this.paymentDate = LocalDateTime.now();
        this.lastModifiedDate = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Bill{" +
                "id='" + id + '\'' +
                ", customerName='" + customerName + '\'' +
                ", totalAmount=" + totalAmount +
                ", paymentStatus='" + paymentStatus + '\'' +
                ", orderStatus='" + orderStatus + '\'' +
                ", orderDate=" + orderDate +
                '}';
    }
    
    // Bill number methods
    public String getBillNumber() {
        return billNumber;
    }

    public void setBillNumber(String billNumber) {
        this.billNumber = billNumber;
    }
    
    /**
     * Generate a unique bill number based on current date and time
     */
    public void generateBillNumber() {
        if (this.billNumber == null || this.billNumber.isEmpty()) {
            LocalDateTime now = LocalDateTime.now();
            this.billNumber = String.format("PEB-%04d%02d%02d-%02d%02d%02d",
                    now.getYear(), now.getMonthValue(), now.getDayOfMonth(),
                    now.getHour(), now.getMinute(), now.getSecond());
        }
    }
}
