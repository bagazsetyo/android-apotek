package com.example.belajar_android_sturio;

public class CheckoutItem {
    private long id;
    private String name;
    private int quantity;

    public CheckoutItem(long id, String name, int quantity) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
    }

    // Getters
    public long getId() { return id; }
    public String getName() { return name; }
    public int getQuantity() { return quantity; }
}