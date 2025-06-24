package com.example.eventure.adapters;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.paging.PagedList;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.eventure.R;
import com.example.eventure.dialogs.EventDetailsDialog;
import com.example.eventure.dialogs.EventOrganizerDetailsDialog;
import com.example.eventure.model.Event;

import java.time.format.DateTimeFormatter;

public class OrganizerEventAdapter extends PagedListAdapter<Event, OrganizerEventAdapter.EventViewHolder> {

    public interface OnEditClickListener {
        void onEdit(Event event);
    }

    public interface OnDeleteClickListener {
        void onDelete(Event event);
    }

    private final OnEditClickListener onEditClickListener;
    private final OnDeleteClickListener onDeleteClickListener;
    private final FragmentActivity activity;

    public OrganizerEventAdapter(OnEditClickListener editListener, OnDeleteClickListener deleteListener, FragmentActivity activity) {
        super(DIFF_CALLBACK);
        this.onEditClickListener = editListener;
        this.onDeleteClickListener = deleteListener;
        this.activity = activity;
    }

    private static final DiffUtil.ItemCallback<Event> DIFF_CALLBACK = new DiffUtil.ItemCallback<Event>() {
        @Override
        public boolean areItemsTheSame(@NonNull Event oldItem, @NonNull Event newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull Event oldItem, @NonNull Event newItem) {
            return oldItem.equals(newItem);
        }
    };

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.organizer_event_card, parent, false);
        return new EventViewHolder(view);
    }
    public void submitList(@Nullable PagedList<Event> pagedList) {
        Log.d("ADAPTER", "submitList called. List size = " + (pagedList != null ? pagedList.size() : "null"));
        super.submitList(pagedList);
    }
    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = getItem(position);
        Log.d("ADAPTER", "onBindViewHolder called, position = " + position + ", event = " + (event != null ? event.getName() : "null"));

        if (event == null) {
            Log.w("Adapter", "Item at position " + position + " is null!");
            return;
        }

        Log.d("Adapter", "Binding event: " + event.getName());
        holder.eventTitle.setText(event.getName());
        holder.eventLocation.setText(event.getPlace());
        holder.eventDate.setText(event.getDate().format(DateTimeFormatter.BASIC_ISO_DATE));

        // Безопасная загрузка изображения
        if (event.getPhotos() != null && !event.getPhotos().isEmpty()) {
            String imageUrl = event.getPhotos().get(0);
            if (imageUrl != null && imageUrl.startsWith("http")) {
                Glide.with(holder.itemView.getContext())
                        .load(imageUrl)
                        .placeholder(R.drawable.placeholder_image)
                        .error(R.drawable.error_image)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .timeout(10000)
                        .into(holder.eventImage);
            } else {
                Log.w("Adapter", "Invalid image URL: " + imageUrl);
                holder.eventImage.setImageResource(R.drawable.placeholder_image);
            }
        } else {
            holder.eventImage.setImageResource(R.drawable.placeholder_image);
        }

        holder.editButton.setOnClickListener(v -> {
            if (onEditClickListener != null) onEditClickListener.onEdit(event);
        });

        holder.deleteButton.setOnClickListener(v -> {
            if (onDeleteClickListener != null) onDeleteClickListener.onDelete(event);
        });
        holder.itemView.setOnClickListener(v -> {
            EventOrganizerDetailsDialog dialog = EventOrganizerDetailsDialog.newInstance(event);
            dialog.show(activity.getSupportFragmentManager(), "EventOrganizerDetailsDialog");
        });

    }


    static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView eventTitle, eventLocation, eventDate;
        ImageView eventImage;
        Button editButton, deleteButton;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventTitle = itemView.findViewById(R.id.event_title);
            eventLocation = itemView.findViewById(R.id.event_location);
            eventDate = itemView.findViewById(R.id.event_date);
            eventImage = itemView.findViewById(R.id.event_image);
            editButton = itemView.findViewById(R.id.edit_event_button);
            deleteButton = itemView.findViewById(R.id.delete_event_button);
        }

    }

}
