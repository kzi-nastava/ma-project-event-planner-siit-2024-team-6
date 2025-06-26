package com.example.eventure.adapters;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.paging.PagedList;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventure.R;
import com.example.eventure.dialogs.EventTypeDetailsDialog;
import com.example.eventure.model.EventType;

public class AdminEventTypeAdapter extends PagedListAdapter<EventType, AdminEventTypeAdapter.EventTypeViewHolder> {

    public interface OnEditClickListener {
        void onEdit(EventType eventType);
    }

    public interface OnDeleteClickListener {
        void onDelete(EventType eventType);
    }

    private final OnEditClickListener onEditClickListener;
    private final OnDeleteClickListener onDeleteClickListener;
    private final FragmentActivity activity;

    public AdminEventTypeAdapter(OnEditClickListener editListener, OnDeleteClickListener deleteListener, FragmentActivity activity) {
        super(DIFF_CALLBACK);
        this.onEditClickListener = editListener;
        this.onDeleteClickListener = deleteListener;
        this.activity = activity;
    }

    private static final DiffUtil.ItemCallback<EventType> DIFF_CALLBACK = new DiffUtil.ItemCallback<EventType>() {
        @Override
        public boolean areItemsTheSame(@NonNull EventType oldItem, @NonNull EventType newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull EventType oldItem, @NonNull EventType newItem) {
            return oldItem.equals(newItem);
        }
    };

    @NonNull
    @Override
    public EventTypeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_event_type_card, parent, false);
        return new EventTypeViewHolder(view);
    }

    public void submitList(@Nullable PagedList<EventType> pagedList) {
        Log.d("ADAPTER", "submitList called. List size = " + (pagedList != null ? pagedList.size() : "null"));
        super.submitList(pagedList);
    }

    @Override
    public void onBindViewHolder(@NonNull EventTypeViewHolder holder, int position) {
        EventType eventType = getItem(position);
        if (eventType == null) {
            Log.w("AdminEventTypeAdapter", "Item at position " + position + " is null!");
            return;
        }

        holder.title.setText(eventType.getName());
        holder.description.setText(eventType.getDescription());

        holder.editButton.setOnClickListener(v -> {
            if (onEditClickListener != null) onEditClickListener.onEdit(eventType);
        });

        holder.deleteButton.setOnClickListener(v -> {
            if (onDeleteClickListener != null) onDeleteClickListener.onDelete(eventType);
        });

        holder.itemView.setOnClickListener(v -> {
            EventTypeDetailsDialog dialog = EventTypeDetailsDialog.newInstance(eventType);
            dialog.show(activity.getSupportFragmentManager(), "EventTypeDetailsDialog");
        });
    }

    static class EventTypeViewHolder extends RecyclerView.ViewHolder {
        TextView title, description;
        Button editButton, deleteButton;

        public EventTypeViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.event_type_title);
            description = itemView.findViewById(R.id.event_type_description);
            editButton = itemView.findViewById(R.id.edit_event_type_button);
            deleteButton = itemView.findViewById(R.id.delete_event_type_button);
        }
    }
}
