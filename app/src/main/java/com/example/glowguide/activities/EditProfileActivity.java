package com.example.glowguide.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.glowguide.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;

public class EditProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView profileImageView;
    private EditText etFullName, etPhone, etAddress, etEmail;
    private Spinner spinnerGender;
    private TextView tvBirthDate;

    private FirebaseUser user;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Toolbar setup
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        // View bindings
        profileImageView = findViewById(R.id.profileImageView);
        Button btnUploadImage = findViewById(R.id.btnUploadImage);
        Button btnSaveProfile = findViewById(R.id.btnSaveProfile);
        etFullName = findViewById(R.id.etFullName);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);
        etEmail = findViewById(R.id.etEmail);
        spinnerGender = findViewById(R.id.spinnerGender);
        tvBirthDate = findViewById(R.id.tvBirthDate);

        tvBirthDate.setOnClickListener(v -> showDatePicker());
        btnUploadImage.setOnClickListener(v -> openGallery());

        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        if (user != null) {
            etEmail.setText(user.getEmail());

            db.collection("users").document(user.getUid()).get()
                    .addOnSuccessListener(snapshot -> {
                        if (snapshot.exists()) {
                            etFullName.setText(snapshot.getString("fullName"));
                            etPhone.setText(snapshot.getString("phone"));
                            etAddress.setText(snapshot.getString("address"));
                            tvBirthDate.setText(snapshot.getString("birthDate"));

                            String gender = snapshot.getString("gender");
                            if (gender != null) {
                                String[] genderOptions = getResources().getStringArray(R.array.gender_array);
                                for (int i = 0; i < genderOptions.length; i++) {
                                    if (genderOptions[i].equalsIgnoreCase(gender)) {
                                        spinnerGender.setSelection(i);
                                        break;
                                    }
                                }
                            }
                        }
                    });
        }

        btnSaveProfile.setOnClickListener(v -> {
            String name = etFullName.getText().toString();
            String phone = etPhone.getText().toString();
            String address = etAddress.getText().toString();
            String birthDate = tvBirthDate.getText().toString();
            String gender = spinnerGender.getSelectedItem().toString();

            HashMap<String, Object> data = new HashMap<>();
            data.put("fullName", name);
            data.put("phone", phone);
            data.put("address", address);
            data.put("birthDate", birthDate);
            data.put("gender", gender);

            db.collection("users").document(user.getUid()).update(data)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show());
        });
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String date = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    tvBirthDate.setText(date);
                }, year, month, day);

        datePickerDialog.show();
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                profileImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
