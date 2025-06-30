package com.example.glowguide.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.glowguide.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;


public class AddConsultationActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    private final String[] consultationTypes = {
            "Acne care consultation",
            "Radiant skin consultation",
            "Age renew consultation",
            "Lift & Glow consultation",
            "Glow Up consultation",
            "HydraGlow Facial",
            "Radiance Boost Therapy",
            "Deep Moisture Infusion",
            "Oxygen Revive Facial",
            "Balance & Clarify Facial",
            "Dual-Zone Skin Therapy",
            "Calm & Soothe Facial",
            "Gentle Barrier Repair",
            "Clarifying Acne Facial",
            "Blemish Control Peel",
            "Skin Detox Cleanse",
            "GlowGuide Signature Facial"
    };

    private TextView txtSelectedDate;
    private ImageButton btnPrevDate;
    private RadioGroup radioGroupTimeSlots;
    private Button btnBook;
    interface OnBookedSlotsLoaded {
        void onLoaded(Set<String> bookedTimes);
    }

    private Calendar calendar;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault());

    private final String[] timeSlots = {"08:00 AM", "10:00 AM", "02:00 PM"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        boolean isReschedule = intent.getBooleanExtra("reschedule", false);
        String consultationId = intent.getStringExtra("consultationId");
        String intentUserId = intent.getStringExtra("userId");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_consultation);

        Spinner spinnerType = findViewById(R.id.spinnerConsultationType);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, consultationTypes);
        spinnerType.setAdapter(adapter);

        txtSelectedDate = findViewById(R.id.txtSelectedDate);
        btnPrevDate = findViewById(R.id.btnPrevDate);
        ImageButton btnNextDate = findViewById(R.id.btnNextDate);
        ImageButton btnPickDate = findViewById(R.id.btnPickDate);
        radioGroupTimeSlots = new RadioGroup(this);
        btnBook = findViewById(R.id.btnBook);

        LinearLayout layoutTimeSlots = findViewById(R.id.layoutTimeSlots);
        calendar = Calendar.getInstance();

        btnPrevDate.setOnClickListener(v -> {
            do {
                calendar.add(Calendar.DAY_OF_MONTH, -1);
            } while (isWeekend(calendar));
            updateDateText();
            disablePrevIfToday();
        });

        btnNextDate.setOnClickListener(v -> {
            do {
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            } while (isWeekend(calendar));
            updateDateText();
            disablePrevIfToday();
        });

        btnPickDate.setOnClickListener(v -> {
            DatePickerDialog dialog = new DatePickerDialog(
                    this,
                    (view, year, month, dayOfMonth) -> {
                        Calendar pickedDate = Calendar.getInstance();
                        pickedDate.set(year, month, dayOfMonth);

                        if (isWeekend(pickedDate)) {
                            Toast.makeText(this, "Weekends are not available for booking.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        calendar.set(year, month, dayOfMonth);
                        updateDateText();
                        disablePrevIfToday();
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );

            dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000); // disallow past dates
            dialog.show();
        });

        btnBook.setOnClickListener(v -> {
            int selectedId = radioGroupTimeSlots.getCheckedRadioButtonId();
            if (selectedId == -1) {
                Toast.makeText(this, "Please select a time slot", Toast.LENGTH_SHORT).show();
                return;
            }

            RadioButton selectedSlot = findViewById(selectedId);
            String selectedTime = selectedSlot.getText().toString();
            String selectedDate = txtSelectedDate.getText().toString();

            new AlertDialog.Builder(this)
                    .setTitle("Confirm Booking")
                    .setMessage("Book a consultation on\n" + selectedDate + " at " + selectedTime + "?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        if (currentUser == null) {
                            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String selectedType = spinnerType.getSelectedItem() != null
                                ? spinnerType.getSelectedItem().toString()
                                : "";

                        if (selectedType.isEmpty()) {
                            Toast.makeText(this, "Please select a consultation type", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String userId = currentUser.getUid();

                        db.collection("consultations")
                                .document(userId)
                                .collection("bookings")
                                .whereEqualTo("date", selectedDate)
                                .whereEqualTo("time", selectedTime)
                                .get()
                                .addOnSuccessListener(querySnapshot -> {
                                    if (!querySnapshot.isEmpty() && !isReschedule) {
                                        // Slot already booked
                                        if (!isFinishing() && !isDestroyed()) {
                                            new AlertDialog.Builder(this)
                                                    .setTitle("Time Slot Unavailable")
                                                    .setMessage("The time slot you selected is already booked.\nPlease choose another time or date.")
                                                    .setPositiveButton("OK", null)
                                                    .show();
                                            radioGroupTimeSlots.clearCheck();
                                        }
                                    } else {
                                        // Slot is available — save booking
                                        Map<String, Object> booking = new HashMap<>();
                                        try {
                                            SimpleDateFormat fullFormat = new SimpleDateFormat("EEE, dd MMM yyyy hh:mm a", Locale.getDefault());
                                            Date fullDateTime = fullFormat.parse(selectedDate + " " + selectedTime);
                                            booking.put("date", fullDateTime); // ✅ correct date + time
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }

                                        booking.put("time", selectedTime);
                                        booking.put("type", selectedType);
                                        booking.put("timestamp", System.currentTimeMillis());
                                        booking.put("userId", userId);
                                        booking.put("status", "UPCOMING");

                                        if (isReschedule && consultationId != null) {
                                            db.collection("consultations")
                                                    .document(userId)
                                                    .collection("bookings")
                                                    .document(consultationId)
                                                    .set(booking)
                                                    .addOnSuccessListener(documentReference -> showSuccessDialog())
                                                    .addOnFailureListener(e -> {
                                                        Toast.makeText(this, "Failed to reschedule: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                                    });
                                        } else {
                                            db.collection("consultations")
                                                    .document(userId)
                                                    .collection("bookings")
                                                    .add(booking)
                                                    .addOnSuccessListener(documentReference -> showSuccessDialog())
                                                    .addOnFailureListener(e -> {
                                                        Toast.makeText(this, "Failed to save booking: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                                    });
                                        }
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Failed to check bookings: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                });
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        disablePrevIfToday();

        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        updateDateText();
    }

    private void updateDateText() {
        String selectedDate = dateFormat.format(calendar.getTime());
        txtSelectedDate.setText(selectedDate);

        fetchBookedTimeSlots(selectedDate, bookedTimes -> {
            LinearLayout layoutTimeSlots = findViewById(R.id.layoutTimeSlots);
            displayTimeSlots(layoutTimeSlots, bookedTimes);
        });
    }


    private void disablePrevIfToday() {
        Calendar today = Calendar.getInstance();
        boolean isToday = calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR)
                && calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR);
        btnPrevDate.setEnabled(!isToday);
    }

    private void displayTimeSlots(LinearLayout layout, Set<String> bookedTimes){
        layout.removeAllViews();

        TextView label = new TextView(this);
        label.setText("Please choose an available time slot:");
        label.setTypeface(null, Typeface.BOLD);
        layout.addView(label);

        radioGroupTimeSlots.setOrientation(RadioGroup.VERTICAL);
        radioGroupTimeSlots.removeAllViews();

        for (String time : timeSlots) {
            RadioButton rb = new RadioButton(this);
            rb.setText(time);
            rb.setTextSize(16);

            if (bookedTimes.contains(time)) {
                rb.setEnabled(false);
                rb.setTextColor(getResources().getColor(android.R.color.darker_gray));
            }

            radioGroupTimeSlots.addView(rb);
        }

        layout.addView(radioGroupTimeSlots);
    }

        private void showSuccessDialog() {
        if (isFinishing() || isDestroyed()) return;

        new AlertDialog.Builder(this)
                .setTitle("Booking Confirmed")
                .setMessage("Your consultation has been successfully booked.")
                .setPositiveButton("OK", (dialog, which) -> {
                    startActivity(new Intent(this, MainActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                    finish();
                })
                .setCancelable(false)
                .show();
    }

    private void fetchBookedTimeSlots(String selectedDate, OnBookedSlotsLoaded listener) {
        if (currentUser == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            listener.onLoaded(new HashSet<>());
            return;
        }

        String userId = currentUser.getUid();

        db.collection("consultations")
                .document(userId)
                .collection("bookings")
                .whereEqualTo("date", selectedDate)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    Set<String> bookedTimes = new HashSet<>();
                    for (var doc : querySnapshot.getDocuments()) {
                        bookedTimes.add(doc.getString("time"));
                    }
                    listener.onLoaded(bookedTimes);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load booked slots", Toast.LENGTH_SHORT).show();
                    listener.onLoaded(new HashSet<>());
                });
    }

    private boolean isWeekend(Calendar date) {
        int dayOfWeek = date.get(Calendar.DAY_OF_WEEK);
        return dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY;
    }
}