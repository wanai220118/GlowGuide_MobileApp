package com.example.glowguide.models;

import java.io.Serializable;

public class OrderItem implements Serializable {
    private String name;
    private int quantity;
    private double price;

    public OrderItem() {}

    public OrderItem(String name, int quantity, double price) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }

    public String getName() { return name; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }
}
