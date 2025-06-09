package com.example.eventure.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventure.R;
import com.example.eventure.dto.NotificationDTO;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private List<NotificationDTO> notificationList;

    public NotificationAdapter(List<NotificationDTO> notificationList) {
        Log.d("NTag", "Adapter constructor called");
        this.notificationList = notificationList;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_card, parent, false);
        return new NotificationViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        NotificationDTO notification = notificationList.get(position);

        // Format timestamp to a readable string
        String formattedTimestamp = formatTimestamp(String.valueOf(notification.getTimestamp()));
        holder.timestamp.setText(formattedTimestamp);

        // Set the notification text
        holder.text.setText(notification.getText());
    }

    // Optionally format your timestamp string (adjust parsing depending on your timestamp format)
    private String formatTimestamp(String timestamp) {
        try {
            // Example: if timestamp is ISO 8601 string like "2024-06-09T12:34:56Z"
            SimpleDateFormat sdfInput = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
            Date date = sdfInput.parse(timestamp);

            // Format to something readable like "Jun 9, 12:34 PM"
            SimpleDateFormat sdfOutput = new SimpleDateFormat("MMM d, h:mm a", Locale.getDefault());
            return sdfOutput.format(date);
        } catch (ParseException | NullPointerException e) {
            e.printStackTrace();
            return timestamp; // fallback to raw string if parsing fails
        }
    }

    public void setNotifications(List<NotificationDTO> newNotifications) {
        Log.d("NTag", "setNotifications called");
        notificationList = newNotifications;
        notifyDataSetChanged();
    }

    public void addNotifications(List<NotificationDTO> newNotifications) {
        Log.d("NTag", "addNotifications called");
        int startPos = notificationList.size();
        notificationList.addAll(newNotifications);
        notifyItemRangeInserted(startPos, newNotifications.size());
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView timestamp, text;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            timestamp = itemView.findViewById(R.id.tvNotificationTimestamp);
            text = itemView.findViewById(R.id.tvNotificationText);
        }
    }
}
