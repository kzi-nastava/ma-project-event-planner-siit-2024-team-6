package com.example.eventure.adapters;
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

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {
    private List<Event> events;

    public EventAdapter(List<Event> eventList) {
        this.events = eventList;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_card, parent, false);
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

    static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView eventTitle, eventLocation, eventDate;
        ImageView eventImage;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventTitle = itemView.findViewById(R.id.event_title);
            eventLocation = itemView.findViewById(R.id.event_location);
            eventDate = itemView.findViewById(R.id.event_date);
            eventImage = itemView.findViewById(R.id.event_image);
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
            eventLocation.setText(event.getLocation());
            SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy 'at' HH:mm");
            String formattedDate = dateFormatter.format(event.getDate());
            eventDate.setText(formattedDate);

        }
    }
}
