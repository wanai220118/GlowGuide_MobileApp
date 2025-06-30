package com.example.glowguide.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.glowguide.R;

public class SkinConditionActivity extends AppCompatActivity {

    private TextView tvSkinType, tvTreatments, tvProducts;
    private String previousSkinType = "None";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skin_condition);

        // Toolbar setup
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        // Views
        tvSkinType = findViewById(R.id.tvSkinType);
        tvTreatments = findViewById(R.id.tvTreatments);
        tvProducts = findViewById(R.id.tvProducts);

        // Get current skin type at launch to compare later
        SharedPreferences prefs = getSharedPreferences("GlowGuidePrefs", MODE_PRIVATE);
        previousSkinType = prefs.getString("skinType", "None");

        // Retake Quiz button
        Button btnRetakeQuiz = findViewById(R.id.btnRetakeQuiz);
        btnRetakeQuiz.setOnClickListener(v -> {
            Intent intent = new Intent(SkinConditionActivity.this, QuizActivity.class); // Change to your quiz activity
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences prefs = getSharedPreferences("GlowGuidePrefs", MODE_PRIVATE);
        String skinType = prefs.getString("skinType", "None");

        tvSkinType.setText("Skin Type: " + skinType);

        if (!skinType.equalsIgnoreCase(previousSkinType)) {
            Toast.makeText(this, "Skin Type updated to: " + skinType, Toast.LENGTH_SHORT).show();
            previousSkinType = skinType;
        }

        switch (skinType.toLowerCase()) {
            case "dry":
                tvTreatments.setText("HydraGlow Facial, Deep Moisture Infusion");
                tvProducts.setText("Hydrating Serum, Moisture Barrier Cream");
                break;
            case "oily":
                tvTreatments.setText("Balance & Clarify Facial, Oxygen Revive Facial");
                tvProducts.setText("Oil-Free Cleanser, Mattifying Gel");
                break;
            case "sensitive":
                tvTreatments.setText("Calm & Soothe Facial, Gentle Barrier Repair");
                tvProducts.setText("Soothing Toner, Fragrance-Free Moisturizer");
                break;
            case "combination":
                tvTreatments.setText("Dual-Zone Skin Therapy, Glow Up Consultation");
                tvProducts.setText("Balancing Lotion, Lightweight Moisturizer");
                break;
            case "normal":
                tvTreatments.setText("Radiance Boost Therapy, Age Renew Consultation");
                tvProducts.setText("Daily Cleanser, Normal Skin Moisturizer");
                break;
            default:
                tvTreatments.setText("—");
                tvProducts.setText("—");
        }
    }
}
