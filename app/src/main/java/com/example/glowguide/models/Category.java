package com.example.glowguide.models;

public class Category {
    private int id;
    private String name;
    private String description;
    private int imageRes;

    public Category(int id, String name, String description, int imageRes) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.imageRes = imageRes;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getImageRes() {
        return imageRes;
    }
}
