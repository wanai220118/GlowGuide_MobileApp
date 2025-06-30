package com.example.glowguide.models;

import java.io.Serializable;

public class Reminder implements Serializable {
    public String time, repeat, category, note;

    public Reminder() {} // Required by Firestore

    public Reminder(String time, String repeat, String category, String note) {
        this.time = time;
        this.repeat = repeat;
        this.category = category;
        this.note = note;
    }
}
