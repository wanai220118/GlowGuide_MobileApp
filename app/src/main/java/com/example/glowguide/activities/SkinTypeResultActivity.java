package com.example.glowguide.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.glowguide.R;

public class SkinTypeResultActivity extends AppCompatActivity {
    private int[] answers;  // Array to store the answers passed from previous activities

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve the answers array passed from the previous activity
        answers = getIntent().getIntArrayExtra("answers");

        // Calculate the skin type based on the answers array
        String skinType = calculateSkinType();

        // Save to SharedPreferences
        SharedPreferences prefs = getSharedPreferences("GlowGuidePrefs", MODE_PRIVATE);
        prefs.edit().putString("skinType", skinType.replace(" Skin", "").toLowerCase()).apply();

        Toast.makeText(this, "Skin type saved: " + skinType, Toast.LENGTH_SHORT).show();

        // Get the treatment suggestions
        String treatmentMessage = getTreatmentForSkinType(skinType);

        // Create an AlertDialog to display the result
        new AlertDialog.Builder(this)
                .setTitle("Your Skin Type: " + skinType)
                .setMessage("We recommend the following treatments:\n\n" + treatmentMessage)
                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Navigate to the homepage (e.g., MainActivity)
                        Intent intent = new Intent(SkinTypeResultActivity.this, MainActivity.class);  // Change MainActivity to your desired homepage
                        startActivity(intent);
                        finish();  // Close the current activity
                    }
                })
                .setCancelable(false) // Optional: prevent the dialog from being dismissed by tapping outside
                .show();
    }

    private String calculateSkinType() {
        int normal = 0, dry = 0, oily = 0, combination = 0, sensitive = 0;

        // Logic to calculate skin type based on answers array
        for (int i = 0; i < answers.length; i++) {
            switch (answers[i]) {
                case 1: // Normal skin
                    normal++; break;
                case 2: // Dry skin
                    dry++; break;
                case 3: // Oily skin
                    oily++; break;
                case 4: // Combination skin
                    combination++; break;
                case 5: // Sensitive skin
                    sensitive++; break;
            }
        }

        // Determine skin type based on the most frequent answer
        if (normal > Math.max(Math.max(dry, oily), Math.max(combination, sensitive))) {
            return "Normal Skin";
        } else if (dry > Math.max(Math.max(normal, oily), Math.max(combination, sensitive))) {
            return "Dry Skin";
        } else if (oily > Math.max(Math.max(normal, dry), Math.max(combination, sensitive))) {
            return "Oily Skin";
        } else if (combination > Math.max(Math.max(normal, dry), Math.max(oily, sensitive))) {
            return "Combination Skin";
        } else {
            return "Sensitive Skin";
        }
    }

    private String getTreatmentForSkinType(String skinType) {
        switch (skinType) {
            case "Normal Skin":
                return "GlowGuide Signature Facial, Radiance Boost Therapy";
            case "Dry Skin":
                return "Deep Moisture Infusion, Oxygen Revive Facial";
            case "Oily Skin":
                return "Balance & Clarify Facial, Clarifying Acne Facial";
            case "Combination Skin":
                return "Dual-Zone Skin Therapy, HydraGlow Facial";
            case "Sensitive Skin":
                return "Calm & Soothe Facial, Gentle Barrier Repair";
            default:
                return "No treatments available.";
        }
    }
}