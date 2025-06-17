package com.example.eventure.dialogs;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.eventure.R;
import com.example.eventure.adapters.ImageCarouselAdapter;
import com.example.eventure.model.Event;
import com.google.android.material.snackbar.Snackbar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EventDetailsDialog#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventDetailsDialog extends DialogFragment {
    private Event event;
    private boolean isFavorited;
    private ImageButton btnFavorite;

    public static EventDetailsDialog newInstance(Event event) {
        EventDetailsDialog fragment = new EventDetailsDialog();
        Bundle args = new Bundle();
        args.putParcelable("event", event);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_details_dialog, container, false);

        if (getArguments() != null) {
            event = getArguments().getParcelable("event");
        }

        if (event == null) {
            dismiss();
            return view;
        }

        setupUI(view);
        populateUI(view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.95);
            getDialog().getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    private void setupUI(View view) {
        ImageButton btnExit = view.findViewById(R.id.btn_exit);
        btnExit.setOnClickListener(v -> dismiss());

        btnFavorite = view.findViewById(R.id.btn_favorite);
        isFavorited = false;

        // TODO: Реализовать проверку на избранное (если есть метод на бэке)
        btnFavorite.setOnClickListener(v -> {
            isFavorited = !isFavorited;
            btnFavorite.setImageResource(isFavorited ? R.drawable.heart_filled_icon : R.drawable.heart_icon);
            // TODO: Вызов методов addToFavorites / removeFromFavorites
        });

        ImageButton btnContact = view.findViewById(R.id.provider_icon);
        btnContact.setOnClickListener(v -> {
            // TODO: реализовать диалог контакта с организатором
            Snackbar.make(view, "Contact organizer clicked", Snackbar.LENGTH_SHORT).show();
        });
    }

    private void populateUI(View view) {
        ((TextView) view.findViewById(R.id.event_title)).setText(event.getName());
        ((TextView) view.findViewById(R.id.event_description)).setText(event.getDescription());
        ((TextView) view.findViewById(R.id.event_place)).setText(event.getPlace());
        ((TextView) view.findViewById(R.id.event_date)).setText(event.getDate().toString());

        ViewPager2 imageCarousel = view.findViewById(R.id.event_image_carousel);
        if (event.getPhotos() != null && !event.getPhotos().isEmpty()) {
            ImageCarouselAdapter adapter = new ImageCarouselAdapter(getContext(), event.getPhotos());
            imageCarousel.setAdapter(adapter);
        }
    }
}