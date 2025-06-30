package com.example.glowguide.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.glowguide.R;
import com.example.glowguide.activities.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;

public class ProfileFragment extends Fragment {

    private TextView tvUsername, tvEmail, tvTotalConsultations, tvPhone, tvLastLogin;
    private Button btnLogout;
    private FirebaseAuth auth;
    private FirebaseUser user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        tvUsername = view.findViewById(R.id.tvUsername);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvTotalConsultations = view.findViewById(R.id.tvTotalConsultations);
        tvPhone = view.findViewById(R.id.tvPhone);
        tvLastLogin = view.findViewById(R.id.tvLastLogin);
        btnLogout = view.findViewById(R.id.btnLogout);

        ImageView editPhoto = view.findViewById(R.id.editPhotoIcon);
        editPhoto.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), EditProfileActivity.class));
        });

        if (user != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // Load user profile details
            db.collection("users").document(user.getUid())
                    .get()
                    .addOnSuccessListener(snapshot -> {
                        if (snapshot.exists()) {
                            tvUsername.setText(snapshot.getString("fullName"));
                            tvEmail.setText(user.getEmail());

                            String phone = snapshot.getString("phone");
                            tvPhone.setText("Phone: " + (phone != null ? phone : "-"));

                            String lastLogin = DateFormat.getDateTimeInstance().format(user.getMetadata().getLastSignInTimestamp());
                            tvLastLogin.setText("Last login: " + lastLogin);
                        }
                    });

            // Count consultations
            db.collection("consultations").document(user.getUid())
                    .collection("bookings")
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        int count = querySnapshot.size();
                        tvTotalConsultations.setText("Total Consultations: " + count);
                    })
                    .addOnFailureListener(e -> {
                        tvTotalConsultations.setText("Failed to load consultations");
                    });
        }

        // Navigation buttons
        view.findViewById(R.id.btnEditProfile).setOnClickListener(v -> startActivity(new Intent(getContext(), EditProfileActivity.class)));
        view.findViewById(R.id.btnRoutine).setOnClickListener(v -> startActivity(new Intent(getContext(), RoutineActivity.class)));
        view.findViewById(R.id.btnSkinCondition).setOnClickListener(v -> startActivity(new Intent(getContext(), SkinConditionActivity.class)));
        view.findViewById(R.id.btnAbout).setOnClickListener(v -> startActivity(new Intent(getContext(), AboutActivity.class)));
        view.findViewById(R.id.btnFAQs).setOnClickListener(v -> startActivity(new Intent(getContext(), FaqsActivity.class)));
        view.findViewById(R.id.btnPrivacyPolicy).setOnClickListener(v -> startActivity(new Intent(getContext(), PrivacyPolicyActivity.class)));
        view.findViewById(R.id.btnConsultation).setOnClickListener(v -> startActivity(new Intent(getContext(), AddConsultationActivity.class)));

        // Logout
        btnLogout.setOnClickListener(v -> {
            auth.signOut();
            requireActivity().getSharedPreferences("GlowGuidePrefs", getContext().MODE_PRIVATE)
                    .edit()
                    .putBoolean("isLoggedIn", false)
                    .apply();

            Toast.makeText(getContext(), "Logged out successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getContext(), OnBoardingActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        // Load profile image
        loadUserProfileImage(view);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getView() != null) {
            loadUserProfileImage(getView());
        }
    }

    private void loadUserProfileImage(View view) {
        ImageView profileImage = view.findViewById(R.id.profileImage);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(user.getUid())
                    .get()
                    .addOnSuccessListener(snapshot -> {
                        if (snapshot.exists()) {
                            String imageUrl = snapshot.getString("profileImageUrl");
                            if (imageUrl != null && !imageUrl.isEmpty()) {
                                Glide.with(this).load(imageUrl).into(profileImage);
                            }
                        }
                    });
        }
    }
}