package com.example.eventure.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventure.R;
import com.example.eventure.dto.ActivityDTO;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Consumer;

public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ViewHolder> {

    private final List<ActivityDTO> activities;
    private final Consumer<Integer> onDeleteClick;

    public ActivityAdapter(List<ActivityDTO> activities, Consumer<Integer> onDeleteClick) {
        this.activities = activities;
        this.onDeleteClick = onDeleteClick;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, desc, loc, time;
        Button delete;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.item_name);
            desc = itemView.findViewById(R.id.item_description);
            loc = itemView.findViewById(R.id.item_location);
            time = itemView.findViewById(R.id.item_time);
            delete = itemView.findViewById(R.id.btn_delete);
        }
    }

    @NonNull
    @Override
    public ActivityAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_activity, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ActivityDTO act = activities.get(position);
        holder.name.setText(act.getName());
        holder.desc.setText(act.getDescription());
        holder.loc.setText(act.getLocation());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        String timeFormatted = act.getStartTime().format(formatter) + " - " + act.getEndTime().format(formatter);
        holder.time.setText(timeFormatted);

        holder.delete.setOnClickListener(v -> onDeleteClick.accept(act.getId()));
    }


    @Override
    public int getItemCount() {
        return activities.size();
    }
}
