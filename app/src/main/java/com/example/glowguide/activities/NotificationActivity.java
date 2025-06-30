package com.example.glowguide.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.glowguide.R;
import com.example.glowguide.adapters.ReminderNotificationAdapter;
import com.example.glowguide.models.Reminder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {

    private RecyclerView notificationRecyclerView;
    private ReminderNotificationAdapter adapter;
    private final List<Reminder> reminderList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        notificationRecyclerView = findViewById(R.id.notificationRecyclerView);
        notificationRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ReminderNotificationAdapter(reminderList);
        notificationRecyclerView.setAdapter(adapter);

        loadReminders();
    }

    private void loadReminders() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(user.getUid())
                .collection("reminders")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    reminderList.clear();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        Reminder reminder = doc.toObject(Reminder.class);
                        if (reminder != null) reminderList.add(reminder);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to fetch reminders", Toast.LENGTH_SHORT).show();
                });
    }
}
