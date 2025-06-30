package com.example.glowguide.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.glowguide.R;
import com.example.glowguide.models.Reminder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ReminderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private List<Object> dataList = new ArrayList<>();
    private final Set<String> collapsedHeaders = new HashSet<>();

    public interface OnReminderLongClickListener {
        void onReminderLongClick(Reminder reminder);
    }

    private OnReminderLongClickListener longClickListener;

    public void setOnReminderLongClickListener(OnReminderLongClickListener listener) {
        this.longClickListener = listener;
    }

    public void setData(List<Object> fullList) {
        dataList.clear();
        String currentDay = null;

        for (Object item : fullList) {
            if (item instanceof String) {
                currentDay = (String) item;
                dataList.add(currentDay);
            } else if (item instanceof Reminder && currentDay != null) {
                if (!collapsedHeaders.contains(currentDay)) {
                    dataList.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return (dataList.get(position) instanceof String) ? TYPE_HEADER : TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reminder, parent, false);
            return new ItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {
            String day = (String) dataList.get(position);
            HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
            headerHolder.headerText.setText(day);

            holder.itemView.setOnClickListener(v -> {
                if (collapsedHeaders.contains(day)) {
                    collapsedHeaders.remove(day);
                } else {
                    collapsedHeaders.add(day);
                }
                notifyDataSetChanged();
            });

        } else if (holder instanceof ItemViewHolder) {
            Reminder reminder = (Reminder) dataList.get(position);
            ItemViewHolder itemHolder = (ItemViewHolder) holder;

            itemHolder.timeText.setText(reminder.time);
            itemHolder.repeatText.setText(reminder.repeat);
            itemHolder.categoryText.setText(reminder.category);
            itemHolder.noteText.setText(reminder.note);

            holder.itemView.setOnLongClickListener(v -> {
                if (longClickListener != null) {
                    longClickListener.onReminderLongClick(reminder);
                }
                return true;
            });
        }
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView headerText;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            headerText = itemView.findViewById(R.id.day_header);
        }
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView timeText, repeatText, categoryText, noteText;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            timeText = itemView.findViewById(R.id.reminder_time);
            repeatText = itemView.findViewById(R.id.reminder_repeat);
            categoryText = itemView.findViewById(R.id.reminder_category);
            noteText = itemView.findViewById(R.id.reminder_note);
        }
    }
}