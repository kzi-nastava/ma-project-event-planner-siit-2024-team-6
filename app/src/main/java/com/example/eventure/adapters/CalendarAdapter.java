package com.example.eventure.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventure.dto.CalendarItemDTO;

import java.util.List;
import java.util.function.Consumer;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.ViewHolder> {

    private final List<CalendarItemDTO> items;
    private final Consumer<CalendarItemDTO> onClick;

    public CalendarAdapter(List<CalendarItemDTO> items, Consumer<CalendarItemDTO> onClick) {
        this.items = items;
        this.onClick = onClick;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CalendarItemDTO item = items.get(position);
        holder.title.setText(item.getName() + " (" + item.getType() + ")");
        holder.itemView.setOnClickListener(v -> onClick.accept(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(android.R.id.text1);
        }
    }
}
