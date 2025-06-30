package com.example.glowguide.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.glowguide.R;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText etResetEmail;
    private Button btnResetPassword;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        etResetEmail = findViewById(R.id.etResetEmail);
        btnResetPassword = findViewById(R.id.btnResetPassword);
        firebaseAuth = FirebaseAuth.getInstance();

        btnResetPassword.setOnClickListener(view -> {
            String email = etResetEmail.getText().toString().trim();

            if (email.isEmpty()) {
                etResetEmail.setError("Email is required");
                etResetEmail.requestFocus();
                return;
            }

            firebaseAuth.sendPasswordResetEmail(email)
                    .addOnSuccessListener(aVoid -> Toast.makeText(ForgotPasswordActivity.this, "Reset link sent to your email.", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(ForgotPasswordActivity.this, "Failed to send reset email: " + e.getMessage(), Toast.LENGTH_LONG).show());
        });

        findViewById(R.id.backToLoginButton).setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        String passedEmail = getIntent().getStringExtra("email");
        if (passedEmail != null) {
            etResetEmail.setText(passedEmail);
        }

    }
}
