package com.example.glowguide.activities;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.glowguide.R;
import com.example.glowguide.adapters.ReminderAdapter;
import com.example.glowguide.models.Reminder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RoutineActivity extends AppCompatActivity {

    private RecyclerView reminderRecyclerView;
    private ReminderAdapter adapter;
    private List<Reminder> reminderList = new ArrayList<>();

    private final Map<String, List<Reminder>> groupedReminders = new LinkedHashMap<>();
    private final List<Object> displayList = new ArrayList<>(); // Contains both String (day headers) & Reminder


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routine);

        adapter = new ReminderAdapter();
        adapter.setOnReminderLongClickListener(reminder -> {
            Intent intent = new Intent(RoutineActivity.this, AddReminderActivity.class);
            intent.putExtra("reminder_data", reminder);
            startActivity(intent);
            Toast.makeText(this, "Long pressed: " + reminder.note, Toast.LENGTH_SHORT).show();
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        reminderRecyclerView = findViewById(R.id.reminderRecyclerView);
        reminderRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ReminderAdapter();
        reminderRecyclerView.setAdapter(adapter);

        loadReminders();

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                final int pos = position;
                Object item = displayList.get(pos);

                if (!(item instanceof Reminder)) {
                    adapter.notifyItemChanged(pos);
                    return;
                }

                Reminder reminderToDelete = (Reminder) item;

                new AlertDialog.Builder(RoutineActivity.this)
                        .setTitle("Confirm Deletion")
                        .setMessage("Are you sure you want to delete this reminder?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if (user != null) {
                                FirebaseFirestore.getInstance()
                                        .collection("users")
                                        .document(user.getUid())
                                        .collection("reminders")
                                        .whereEqualTo("time", reminderToDelete.time)
                                        .whereEqualTo("category", reminderToDelete.category)
                                        .whereEqualTo("note", reminderToDelete.note)
                                        .whereEqualTo("repeat", reminderToDelete.repeat)
                                        .get()
                                        .addOnSuccessListener(querySnapshot -> {
                                            for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                                                doc.getReference().delete();
                                            }

                                            loadReminders(); // reload fresh reminders from Firestore
                                            Toast.makeText(RoutineActivity.this, "Reminder deleted", Toast.LENGTH_SHORT).show();

                                            TextView emptyState = findViewById(R.id.emptyStateText);
                                            emptyState.setVisibility(displayList.isEmpty() ? View.VISIBLE : View.GONE);

                                            Toast.makeText(RoutineActivity.this, "Reminder deleted", Toast.LENGTH_SHORT).show();
                                        });
                            }
                        })
                        .setNegativeButton("Cancel", (dialog, which) -> adapter.notifyItemChanged(pos))
                        .setCancelable(false)
                        .show();
            }
        });

        itemTouchHelper.attachToRecyclerView(reminderRecyclerView);
    }

    private void loadReminders() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        String uid = user.getUid();
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .collection("reminders")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    groupedReminders.clear();
                    displayList.clear();

                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        Reminder reminder = doc.toObject(Reminder.class);
                        if (reminder != null && reminder.repeat != null) {
                            String[] days = reminder.repeat.split(",\\s*");
                            for (String day : days) {
                                if (!groupedReminders.containsKey(day)) {
                                    groupedReminders.put(day, new ArrayList<>());
                                }
                                groupedReminders.get(day).add(reminder);
                            }
                        }
                    }

                    // Sort by day order: Mon, Tue, ..., Sun
                    String[] weekOrder = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
                    for (String day : weekOrder) {
                        if (groupedReminders.containsKey(day)) {
                            displayList.add(day); // Header
                            displayList.addAll(groupedReminders.get(day));
                        }
                    }

                    adapter.setData(displayList);


                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        Reminder reminder = doc.toObject(Reminder.class);
                        reminderList.add(reminder);
                    }
                    Collections.sort(reminderList, Comparator.comparing(r -> r.time));

                    adapter.notifyDataSetChanged();
                    TextView emptyState = findViewById(R.id.emptyStateText);
                    emptyState.setVisibility(reminderList.isEmpty() ? View.VISIBLE : View.GONE);

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load reminders", Toast.LENGTH_SHORT).show();
                });
    }

    private void showEditReminderDialog(Reminder reminder) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_reminder, null);

        TextView editTime = dialogView.findViewById(R.id.edit_time);
        TextView editRepeat = dialogView.findViewById(R.id.edit_repeat);
        EditText editNote = dialogView.findViewById(R.id.edit_note);
        Spinner editCategorySpinner = dialogView.findViewById(R.id.edit_category_spinner);
        String[] categories = {"All", "Facial Wash", "Toner", "Serum", "Moisturizer", "Sunscreen"};
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editCategorySpinner.setAdapter(categoryAdapter);

        int selectedIndex = 0;
        for (int i = 0; i < categories.length; i++) {
            if (categories[i].equalsIgnoreCase(reminder.category)) {
                selectedIndex = i;
                break;
            }
        }
        editCategorySpinner.setSelection(selectedIndex);


        editTime.setText(reminder.time);
        editRepeat.setText(reminder.repeat);
        editNote.setText(reminder.note);

        final int[] hour = {0};
        final int[] minute = {0};
        final boolean[] selectedDays = new boolean[7];
        final String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};

        // Time picker
        editTime.setOnClickListener(v -> {
            Calendar now = Calendar.getInstance();
            TimePickerDialog dialog = new TimePickerDialog(this, (view, h, m) -> {
                hour[0] = h;
                minute[0] = m;
                String amPm = h < 12 ? "AM" : "PM";
                int displayHour = (h % 12 == 0) ? 12 : h % 12;
                editTime.setText(String.format(Locale.getDefault(), "%02d:%02d %s", displayHour, m, amPm));
            }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), false);
            dialog.show();
        });

        // Repeat days
        editRepeat.setOnClickListener(v -> {
            boolean[] checked = new boolean[days.length];
            String[] current = reminder.repeat.split(",\\s*");
            for (int i = 0; i < days.length; i++) {
                for (String day : current) {
                    if (days[i].equals(day)) checked[i] = true;
                }
            }
            new AlertDialog.Builder(this)
                    .setTitle("Select Days")
                    .setMultiChoiceItems(days, checked, (dialog, which, isChecked) -> checked[which] = isChecked)
                    .setPositiveButton("OK", (dialog, which) -> {
                        List<String> selected = new ArrayList<>();
                        for (int i = 0; i < days.length; i++) {
                            if (checked[i]) selected.add(days[i]);
                        }
                        editRepeat.setText(String.join(", ", selected));
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        new AlertDialog.Builder(this)
                .setTitle("Edit Reminder")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    String newTime = editTime.getText().toString().trim();
                    String newRepeat = editRepeat.getText().toString().trim();
                    String newNote = editNote.getText().toString().trim();
                    String newCategory = editCategorySpinner.getSelectedItem().toString();

                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        String uid = user.getUid();
                        FirebaseFirestore.getInstance()
                                .collection("users")
                                .document(uid)
                                .collection("reminders")
                                .whereEqualTo("time", reminder.time)
                                .whereEqualTo("repeat", reminder.repeat)
                                .whereEqualTo("note", reminder.note)
                                .whereEqualTo("category", reminder.category)
                                .get()
                                .addOnSuccessListener(querySnapshot -> {
                                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                                        doc.getReference().update(
                                                "time", newTime,
                                                "repeat", newRepeat,
                                                "note", newNote,
                                                "category", newCategory
                                        );
                                    }

                                    reminder.time = newTime;
                                    reminder.repeat = newRepeat;
                                    reminder.note = newNote;
                                    reminder.category = newCategory;
                                    adapter.notifyDataSetChanged();

                                    Toast.makeText(this, "Reminder updated", Toast.LENGTH_SHORT).show();
                                });
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}

