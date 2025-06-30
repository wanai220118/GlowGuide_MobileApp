package com.example.glowguide.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.glowguide.R;
import com.example.glowguide.adapters.ConsultationAdapter;
import com.example.glowguide.models.Consultation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ConsultationStatusFragment extends Fragment {

    private static final String ARG_STATUS = "status";
    private String status;

    private RecyclerView recyclerView;
    private ConsultationAdapter adapter;
    private List<Consultation> consultationList = new ArrayList<>();

    public ConsultationStatusFragment() {}

    public static ConsultationStatusFragment newInstance(String status) {
        ConsultationStatusFragment fragment = new ConsultationStatusFragment();
        Bundle args = new Bundle();
        args.putString(ARG_STATUS, status);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        status = getArguments() != null ? getArguments().getString(ARG_STATUS) : "UPCOMING";
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_consultation_status, container, false);

        recyclerView = view.findViewById(R.id.recyclerConsultation);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new ConsultationAdapter(getContext(), consultationList);
        recyclerView.setAdapter(adapter);

        loadConsultations();

        return view;
    }

    private void loadConsultations() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collectionGroup("bookings")
                .whereEqualTo("userId", user.getUid())
                .whereEqualTo("status", status)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    consultationList.clear();
                    for (DocumentSnapshot doc : querySnapshot) {
                        Log.d("CONSULT_CHECK", "Doc data: " + doc.getData());

                        try {
                            Consultation consultation = new Consultation();
                            consultation.setId(doc.getId());
                            consultation.setUserId(doc.getString("userId"));
                            consultation.setType(doc.getString("type"));
                            consultation.setStatus(doc.getString("status"));

                            // Handle the 'date' field: can be stored as Timestamp or String
                            Object rawDate = doc.get("date");
                            if (rawDate instanceof com.google.firebase.Timestamp) {
                                consultation.setDate(((com.google.firebase.Timestamp) rawDate).toDate());
                            } else if (rawDate instanceof String) {
                                try {
                                    SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault());
                                    consultation.setDate(sdf.parse((String) rawDate));
                                } catch (Exception e) {
                                    Log.w("CONSULT_PARSE", "Failed to parse date string", e);
                                }
                            }

                            consultationList.add(consultation);
                        } catch (Exception e) {
                            Log.e("CONSULT_CONVERT", "Failed to convert document: " + e.getMessage());
                        }
                    }

                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e("CONSULT_TRACK", "Error fetching consultations", e);
                    Toast.makeText(getContext(), "Failed to load consultations.", Toast.LENGTH_SHORT).show();
                });
    }

}