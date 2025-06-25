package com.example.eventure.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.eventure.R;
import com.example.eventure.model.EventType;

public class EventTypeDetailsDialog extends DialogFragment {

    private EventType eventType;

    public static EventTypeDetailsDialog newInstance(EventType eventType) {
        EventTypeDetailsDialog dialog = new EventTypeDetailsDialog();
        Bundle args = new Bundle();
        args.putParcelable("eventType", eventType);
        dialog.setArguments(args);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_event_type_card, container, false);

        if (getArguments() != null) {
            eventType = getArguments().getParcelable("eventType");
        }

        if (eventType == null) {
            dismiss();
            return view;
        }

        setupUI(view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.95);
            getDialog().getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.white);
        }
    }

    private void setupUI(View view) {
        ImageButton btnExit = view.findViewById(R.id.btn_exit);
        btnExit.setOnClickListener(v -> dismiss());

        TextView nameText = view.findViewById(R.id.event_type_title);
        TextView descriptionText = view.findViewById(R.id.event_type_description);

        nameText.setText(eventType.getName());
        descriptionText.setText(eventType.getDescription());
    }
}
