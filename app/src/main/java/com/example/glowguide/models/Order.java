package com.example.glowguide.models;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Order implements Serializable {
    private String id;
    private List<OrderItem> items;
    private String status;
    private String userId;
    private double total;
    private String paymentMethod;
    private String cancelReason;
    private Date orderDate;

    public Order() {
        // Required for Firestore deserialization
    }

    public Order(List<OrderItem> items, String status, String userId, double total, String paymentMethod, Date orderDate) {
        this.items = items;
        this.status = status;
        this.userId = userId;
        this.total = total;
        this.paymentMethod = paymentMethod;
        this.orderDate = orderDate;
    }

    // Getters & setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getCancelReason() { return cancelReason; }
    public void setCancelReason(String cancelReason) { this.cancelReason = cancelReason; }

    public Date getOrderDate() { return orderDate; }
    public void setOrderDate(Date orderDate) { this.orderDate = orderDate; }
}