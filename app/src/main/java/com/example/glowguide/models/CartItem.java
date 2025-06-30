package com.example.glowguide.models;

import java.io.Serializable;

public class CartItem implements Serializable {
    private Product product;
    private int quantity;
    private boolean isSelected;

    public CartItem(Product product) {
        this.product = product;
        this.quantity = 1;
        this.isSelected = true;
    }

    public Product getProduct() { return product; }
    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = Math.max(quantity, 1);
    }

    public void increaseQuantity() {
        this.quantity++;
    }

    public void decreaseQuantity() {
        if (this.quantity > 1) {
            this.quantity--;
        }
    }
    public boolean isSelected() { return isSelected; }
    public void setSelected(boolean selected) { isSelected = selected; }
    public double getTotalPrice() {
        return product.getPrice() * quantity;
    }
}