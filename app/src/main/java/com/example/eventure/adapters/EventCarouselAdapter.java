package com.example.eventure.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.eventure.R;
import com.example.eventure.dialogs.EventDetailsDialog;
import com.example.eventure.dto.EventDTO;
import com.example.eventure.model.Event;

import java.text.SimpleDateFormat;
import java.util.List;

public class EventCarouselAdapter extends RecyclerView.Adapter<EventCarouselAdapter.EventViewHolder> {

    private List<EventDTO> events;
    private FragmentActivity activity;


    public EventCarouselAdapter(List<EventDTO> events, FragmentActivity fragmentActivity) {
        if (events == null) {
            throw new IllegalArgumentException("Event list cannot be null");
        }
        this.events = events;
        this.activity = fragmentActivity;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.top_event_card, parent, false); // creating event card layout
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int eventIndex) {
        EventDTO event = events.get(eventIndex);
        holder.bind(event);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public class EventViewHolder extends RecyclerView.ViewHolder {

        private ImageView eventImage;
        private TextView eventTitle;
        private TextView eventDescription;
        private TextView viewButton;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);

            eventImage = itemView.findViewById(R.id.top_event_image);
            eventTitle = itemView.findViewById(R.id.top_event_title);
            eventDescription = itemView.findViewById(R.id.top_event_description);
            viewButton = itemView.findViewById(R.id.top_event_view_button);

        }

        public void bind(EventDTO event) {
            List<String> photos = event.getPhotos();

            if (photos != null && !photos.isEmpty() && photos.get(0) != null && !photos.get(0).isEmpty()) {
                String photo = photos.get(0);
                Glide.with(eventImage.getContext())
                        .load(photo)
                        .placeholder(R.drawable.event2)
                        .error(R.drawable.error_image)
                        .into(eventImage);
            } else {
                eventImage.setImageResource(R.drawable.event2); // или какая-то дефолтная
            }

            // event title
            eventTitle.setText(event.getName());
            eventDescription.setText(event.getDescription());
            viewButton.setOnClickListener(v -> {
                Event fullEvent = new Event(event);
                EventDetailsDialog dialog = EventDetailsDialog.newInstance(fullEvent);
                dialog.show(activity.getSupportFragmentManager(), "EventDetailsDialog");
        });
        }

    }
}

