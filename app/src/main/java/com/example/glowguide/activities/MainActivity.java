package com.example.glowguide.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import com.example.glowguide.R;
import com.example.glowguide.databinding.ActivityMainBinding;
import com.example.glowguide.fragments.CartFragment;
import com.example.glowguide.fragments.HomeFragment;
import com.example.glowguide.fragments.ProfileFragment;
import com.example.glowguide.fragments.ShopFragment;
import com.example.glowguide.fragments.TrackFragment;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "reminder_channel";
            String channelName = "Routine Reminders";
            String channelDesc = "Notifications for skincare routine reminders";

            Uri soundUri = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.alarm2);
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();

            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(channelDesc);
            channel.setSound(soundUri, audioAttributes); // 🔊 Set custom sound here

            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                        1001
                );
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "reminder_channel",
                    "Reminder Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Notifies user 30 minutes before reminder");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        super.onCreate(savedInstanceState);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.home_container, new ShopFragment())
                .commit();

        // Setup view binding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Default fragment
        replaceFragment(new HomeFragment());


        // Navigation without switch-case
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.home) {
                replaceFragment(new HomeFragment());
            } else if (itemId == R.id.shop) {
                replaceFragment(new ShopFragment());
            } else if (itemId == R.id.add) {
                showBottomSheet();
            } else if (itemId == R.id.track) {
                replaceFragment(new TrackFragment());
            } else if (itemId == R.id.profile) {
                replaceFragment(new ProfileFragment());
            }

            return true;
        });

        // Check for intent flag to open Cart
        if (getIntent().getBooleanExtra("open_cart", false)) {
            replaceFragment(new CartFragment());
        } else if (getIntent().getBooleanExtra("open_home", false)) {
            replaceFragment(new HomeFragment());
        } else {
            replaceFragment(new HomeFragment()); // default
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }
    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.home_container, fragment)
                .commit();
    }

    private void showBottomSheet() {
        // Create a BottomSheetDialog
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);

        // Inflate the custom bottom sheet layout
        View bottomSheetView = LayoutInflater.from(this).inflate(R.layout.bottomsheetlayout, null);

        // Set the view of the dialog
        bottomSheetDialog.setContentView(bottomSheetView);

        // Handle the cancel button click inside the bottom sheet
        ImageView cancelButton = bottomSheetView.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(v -> bottomSheetDialog.dismiss());  // Dismiss the bottom sheet

        // Add Routine button logic
        View addRoutineButton = bottomSheetView.findViewById(R.id.addRoutineButton);
        addRoutineButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddReminderActivity.class);
            startActivity(intent);
            bottomSheetDialog.dismiss(); // optional: close the bottom sheet
        });

        // Add Consultation button logic
        View addConsultationButton = bottomSheetView.findViewById(R.id.addConsultationButton);
        addConsultationButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddConsultationActivity.class);
            startActivity(intent);
            bottomSheetDialog.dismiss(); // optional: close the bottom sheet
        });

        // Show the bottom sheet dialog
        bottomSheetDialog.show();
    }
}
