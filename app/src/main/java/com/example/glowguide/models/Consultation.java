package com.example.glowguide.models;

import java.io.Serializable;
import java.util.Date;

public class Consultation implements Serializable {
    private String id;
    private String userId;
    private String type;
    private String status;
    private Date date;

    public Consultation() {
        // Firestore requires no-arg constructor
    }

    public Consultation(String userId, String type, String status, Date date) {
        this.userId = userId;
        this.type = type;
        this.status = status;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public String getType() {
        return type;
    }

    public String getStatus() {
        return status;
    }

    public Date getDate() {
        return date;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
