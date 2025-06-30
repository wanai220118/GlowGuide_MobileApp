package com.example.glowguide.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class LauncherActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences("GlowGuidePrefs", MODE_PRIVATE);
        boolean hasOnboarded = prefs.getBoolean("hasOnboarded", false);
        boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);

        // Uncomment this block below ONLY for testing to force login screen
        /*
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("isLoggedIn", false);
        editor.apply();
        isLoggedIn = false;
        */

        if (!hasOnboarded) {
            startActivity(new Intent(this, OnBoardingActivity.class));
        } else if (!isLoggedIn) {
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            startActivity(new Intent(this, MainActivity.class));
        }

        finish(); // Close this launcher activity
    }
}