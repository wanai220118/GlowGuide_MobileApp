package com.example.glowguide.activities;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.glowguide.R;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import com.example.glowguide.ReminderReceiver;
import com.example.glowguide.models.Reminder;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AddReminderActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private TextView timeText, repeatText, categoryText, noteText;
    private Button setReminderBtn;
    private int hour, minute;

    private final String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
    private boolean[] selectedDays = new boolean[days.length];

    private final String[] categories = {"All", "Facial Wash", "Toner", "Serum", "Moisturizer", "Sunscreen"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reminder);

        timeText = findViewById(R.id.time_text);
        repeatText = findViewById(R.id.repeat_text);
        categoryText = findViewById(R.id.category_text);
        noteText = findViewById(R.id.note_text);
        setReminderBtn = findViewById(R.id.set_reminder_btn);

        // Set current time as default
        Calendar calendar = Calendar.getInstance();
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);
        updateDisplayedTime(hour, minute);

        timeText.setOnClickListener(v -> showTimePicker());
        repeatText.setOnClickListener(v -> showRepeatDialog());
        categoryText.setOnClickListener(v -> showCategoryDialog());
        noteText.setOnClickListener(v -> showNoteDialog());

        setReminderBtn.setOnClickListener(v -> {
            final String time = timeText.getText().toString().trim();
            final String repeat = repeatText.getText().toString().trim();
            final String category = categoryText.getText().toString().trim();
            String rawNote = noteText.getText().toString().trim();
            final String note = rawNote.isEmpty() ? "No note" : rawNote;

            if (repeat.isEmpty()) {
                Toast.makeText(this, "Please select repeat days", Toast.LENGTH_SHORT).show();
                return;
            }
            if (category.isEmpty()) {
                Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show();
                return;
            }

            Reminder reminder = new Reminder(time, repeat, category, note);

            // Firestore save
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            if (user == null) {
                Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
                return;
            }

            String uid = user.getUid();

            FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(uid)
                    .collection("reminders")
                    .add(reminder)

                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "Reminder saved!", Toast.LENGTH_SHORT).show();

                        Calendar alarmCalendar = Calendar.getInstance();
                        alarmCalendar.set(Calendar.HOUR_OF_DAY, hour);
                        alarmCalendar.set(Calendar.MINUTE, minute);
                        alarmCalendar.set(Calendar.SECOND, 0);

                        alarmCalendar.add(Calendar.MINUTE, -30);

                        Intent intent = new Intent(this, ReminderReceiver.class);
                        intent.putExtra("reminder_note", note);

                        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                                this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                        );

                        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmCalendar.getTimeInMillis(), pendingIntent);

                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to save reminder", Toast.LENGTH_SHORT).show();
                    });
        });

    }

    private void updateDisplayedTime(int hourOfDay, int minuteOfDay) {
        String amPm = (hourOfDay < 12) ? "AM" : "PM";
        int displayHour = (hourOfDay % 12 == 0) ? 12 : hourOfDay % 12;
        timeText.setText(String.format(Locale.getDefault(), "%02d:%02d %s", displayHour, minuteOfDay, amPm));
    }

    private void showTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minuteOfDay) -> {
            hour = hourOfDay;
            minute = minuteOfDay;
            updateDisplayedTime(hourOfDay, minuteOfDay);
        }, hour, minute, false);
        timePickerDialog.show();
    }

    private void showRepeatDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Days");
        builder.setMultiChoiceItems(days, selectedDays, (dialog, which, isChecked) -> selectedDays[which] = isChecked);

        builder.setPositiveButton("OK", (dialog, which) -> {
            List<String> selected = new ArrayList<>();
            for (int i = 0; i < days.length; i++) {
                if (selectedDays[i]) selected.add(days[i]);
            }
            repeatText.setText(String.join(", ", selected));
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void showCategoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Category");
        builder.setItems(categories, (dialog, which) -> categoryText.setText(categories[which]));
        builder.show();
    }

    private void showNoteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Note");

        final EditText input = new EditText(this);
        input.setText(noteText.getText());
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> noteText.setText(input.getText().toString()));
        builder.setNegativeButton("Cancel", null);

        builder.show();
    }
}
