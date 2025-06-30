package com.example.glowguide.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.glowguide.R;
import com.example.glowguide.models.Reminder;

import java.util.List;

public class ReminderNotificationAdapter extends RecyclerView.Adapter<ReminderNotificationAdapter.ViewHolder> {

    private final List<Reminder> reminders;

    public ReminderNotificationAdapter(List<Reminder> reminders) {
        this.reminders = reminders;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reminder, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Reminder reminder = reminders.get(position);
        holder.time.setText(reminder.time);
        holder.repeat.setText(reminder.repeat);
        holder.category.setText(reminder.category);
        holder.note.setText(reminder.note);
    }

    @Override
    public int getItemCount() {
        return reminders.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView time, repeat, category, note;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.reminder_time);
            repeat = itemView.findViewById(R.id.reminder_repeat);
            category = itemView.findViewById(R.id.reminder_category);
            note = itemView.findViewById(R.id.reminder_note);
        }
    }
}
