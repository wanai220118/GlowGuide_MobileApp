package com.example.glowguide.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.glowguide.R;
import com.example.glowguide.activities.AddConsultationActivity;
import com.example.glowguide.models.Consultation;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ConsultationAdapter extends RecyclerView.Adapter<ConsultationAdapter.ViewHolder> {

    private final List<Consultation> consultations;
    private final Context context;

    public ConsultationAdapter(Context context, List<Consultation> consultations) {
        this.context = context;
        this.consultations = consultations;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvType, tvDate, tvStatus;
        Button btnCancel, btnReschedule;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvType = itemView.findViewById(R.id.tvConsultType);
            tvDate = itemView.findViewById(R.id.tvConsultDate);
            tvStatus = itemView.findViewById(R.id.tvConsultStatus);
            btnCancel = itemView.findViewById(R.id.btnCancel);
            btnReschedule = itemView.findViewById(R.id.btnReschedule);
        }
    }

    @NonNull
    @Override
    public ConsultationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_consultation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConsultationAdapter.ViewHolder holder, int position) {
        Consultation consultation = consultations.get(position);

        holder.tvType.setText(consultation.getType());

        if (consultation.getDate() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());
            holder.tvDate.setText(sdf.format(consultation.getDate()));
        } else {
            holder.tvDate.setText("-");
        }

        String status = consultation.getStatus();
        holder.tvStatus.setText(status);

        if ("CANCELLED".equalsIgnoreCase(status)) {
            // Hide both buttons for cancelled consultations
            holder.btnCancel.setVisibility(View.GONE);
            holder.btnReschedule.setVisibility(View.GONE);
        } else {
            // Show buttons if not cancelled
            holder.btnCancel.setVisibility(View.VISIBLE);
            holder.btnReschedule.setVisibility(View.VISIBLE);
        }

        holder.btnCancel.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Cancel Consultation")
                    .setMessage("Are you sure you want to cancel this consultation?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        FirebaseFirestore.getInstance()
                                .collection("consultations")
                                .document(consultation.getUserId())
                                .collection("bookings")
                                .document(consultation.getId())
                                .update("status", "CANCELLED")
                                .addOnSuccessListener(unused -> {
                                    Toast.makeText(context, "Consultation cancelled", Toast.LENGTH_SHORT).show();

                                    consultation.setStatus("CANCELLED");
                                    notifyItemChanged(position);
                                    // Remove if you're in a filtered view like UPCOMING
                                    consultations.remove(position);
                                    notifyItemRemoved(position);
                                    notifyItemRangeChanged(position, consultations.size());
                                });
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        holder.btnReschedule.setOnClickListener(v -> {
            Intent intent = new Intent(context, AddConsultationActivity.class);
            intent.putExtra("reschedule", true);
            intent.putExtra("consultationId", consultation.getId());
            intent.putExtra("userId", consultation.getUserId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return consultations.size();
    }
}