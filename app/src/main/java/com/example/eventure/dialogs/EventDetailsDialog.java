package com.example.eventure.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.eventure.R;
import com.example.eventure.adapters.ImageCarouselAdapter;
import com.example.eventure.clients.ClientUtils;
import com.example.eventure.model.Event;
import com.google.android.material.snackbar.Snackbar;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.white);
        }
    }

    private void setupUI(View view) {
        ImageButton btnExit = view.findViewById(R.id.btn_exit);
        btnExit.setOnClickListener(v -> dismiss());

        btnFavorite = view.findViewById(R.id.btn_favorite);
        isFavorited = false;

        if (Boolean.TRUE.equals(event.getPublic())) {
            ClientUtils.eventService.isEventFavorited(event.getId()).enqueue(new Callback<Boolean>() {
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                    if (response.isSuccessful() && Boolean.TRUE.equals(response.body())) {
                        btnFavorite.setImageResource(R.drawable.heart_filled_icon);
                        isFavorited = true;
                    }
                }

                @Override
                public void onFailure(Call<Boolean> call, Throwable t) { }
            });
        }

        btnFavorite.setOnClickListener(v -> {
            isFavorited = !isFavorited;
            btnFavorite.setImageResource(isFavorited ? R.drawable.heart_filled_icon : R.drawable.heart_icon);

            Call<Void> call = isFavorited
                    ? ClientUtils.eventService.addEventToFavorites(event.getId())
                    : ClientUtils.eventService.removeEventFromFavorites(event.getId());

            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) { }
                @Override
                public void onFailure(Call<Void> call, Throwable t) { }
            });
        });

        ImageButton btnContact = view.findViewById(R.id.provider_icon);
        btnContact.setOnClickListener(v ->
                Snackbar.make(view, "Contact organizer clicked", Snackbar.LENGTH_SHORT).show());

        Button btnJoin = view.findViewById(R.id.btn_join);
        btnJoin.setOnClickListener(v -> {
            ClientUtils.eventService.participate(event.getId()).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    Snackbar.make(view, "You joined the event!", Snackbar.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Snackbar.make(view, "Join failed!", Snackbar.LENGTH_SHORT).show();
                }
            });
        });

        Button btnInfo = view.findViewById(R.id.btn_download_info);
        btnInfo.setOnClickListener(v -> {
            ClientUtils.eventService.getInfoPdf(event.getId()).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Snackbar.make(view, "PDF download (implement save)", Snackbar.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Snackbar.make(view, "PDF failed", Snackbar.LENGTH_SHORT).show();
                }
            });
        });

        Button btnAgenda = view.findViewById(R.id.btn_download_agenda);
        btnAgenda.setOnClickListener(v ->
                Snackbar.make(view, "TODO: Download agenda PDF", Snackbar.LENGTH_SHORT).show());
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
