package com.example.eventure.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventure.R;
import com.example.eventure.model.Event;

import java.text.SimpleDateFormat;
import java.util.List;

public class EventCarouselAdapter extends RecyclerView.Adapter<EventCarouselAdapter.EventViewHolder> {

    private List<Event> events;

    public EventCarouselAdapter(List<Event> events) {
        if (events == null) {
            throw new IllegalArgumentException("Event list cannot be null");
        }
        this.events = events;
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
        Event event = events.get(eventIndex);
        holder.bind(event);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {

        private ImageView eventImage;
        private TextView eventTitle;
        private TextView eventDescription;
        public EventViewHolder(@NonNull View itemView) {
            super(itemView);

            eventImage = itemView.findViewById(R.id.top_event_image);
            eventTitle = itemView.findViewById(R.id.top_event_title);
            eventDescription = itemView.findViewById(R.id.top_event_description);
        }

        public void bind(Event event) {
//            // Loading image
//            Glide.with(itemView.getContext())
//                    .load(event.getPhotoURL())
//                    .into(eventImage);

            eventImage.setImageResource(event.getPhotoID());
            //event title
            eventTitle.setText(event.getTitle());

            //event location
            eventDescription.setText(event.getDescription());

        }
    }
}

