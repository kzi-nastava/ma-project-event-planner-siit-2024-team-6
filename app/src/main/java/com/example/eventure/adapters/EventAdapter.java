package com.example.eventure.adapters;
import android.util.Log;
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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {
    private List<EventDTO> events;

    public EventAdapter(List<EventDTO> eventList) {
        this.events = eventList;
    }
    public EventAdapter() {
        this.events = new ArrayList<>();
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_card, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int eventIndex) {
        EventDTO event = events.get(eventIndex);
        holder.bind(event);
    }

    @Override
    public int getItemCount() {
        //Log.d("EventsTag", String.valueOf(events.size()));
        return events.size();
    }
    public void addEvents(List<EventDTO> newEvents) {
        int previousSize = events.size();
        events.addAll(newEvents);
        notifyItemRangeInserted(previousSize, newEvents.size());
    }
    public void clearEvents() {
        this.events.clear();
        notifyDataSetChanged();
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

        public void bind(EventDTO event) {

            //String photo = event.getPhotos().get(0);

            List<String> photos = event.getPhotos();
            String photo = null;
            if (photos != null && !photos.isEmpty()) {
                photo = photos.get(0);
            }

            Glide.with(eventImage.getContext())
                    .load(photo)
                    .placeholder(R.drawable.event2)
                    .error(R.drawable.error_image)
                    .into(eventImage);

            //event title
            eventTitle.setText(event.getName());
            //event location
            eventLocation.setText(event.getPlace());
            //event date and time
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy 'at' HH:mm");
            String formattedDate = event.getDate().format(formatter);
            eventDate.setText(formattedDate);
            itemView.setOnClickListener(v -> {
                Event fullEvent = new Event(event); // если нужен полный объект, иначе можно передавать напрямую eventDTO
                EventDetailsDialog dialog = EventDetailsDialog.newInstance(fullEvent);
                dialog.show(((FragmentActivity)v.getContext()).getSupportFragmentManager(), "EventDetailsDialog");
            });
        }
    }
}
