package com.example.eventure.dialogs;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
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
import com.example.eventure.activities.ChatActivity;
import com.example.eventure.activities.HomeActivity;
import com.example.eventure.adapters.ImageCarouselAdapter;
import com.example.eventure.clients.ClientUtils;
import com.example.eventure.dto.UserDTO;
import com.example.eventure.model.Event;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
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
        btnContact.setOnClickListener(v -> {
            if (!ClientUtils.getAuthService().isLoggedIn()) {
                Snackbar.make(view, "You must be logged in to contact the organizer.", Snackbar.LENGTH_SHORT).show();
                return;
            }

            Call<UserDTO> call = ClientUtils.eventService.getEventOrganizer(event.getId());
            call.enqueue(new Callback<UserDTO>() {
                @Override
                public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        UserDTO organizer = response.body();
                        int organizerId = organizer.getId();
                        String userImage = organizer.getPhotoUrl();
                        String userName = organizer.getName() + " " + organizer.getLastname();

                        // Now call findChat inside this callback:
                        ClientUtils.chatService.findChat(organizerId).enqueue(new Callback<Integer>() {
                            @Override
                            public void onResponse(Call<Integer> call, Response<Integer> response) {
                                if (response.isSuccessful()) {
                                    int chatId = response.body();
                                    Intent intent = new Intent(getContext(), ChatActivity.class);
                                    intent.putExtra("chatId", chatId);
                                    intent.putExtra("userName", userName);
                                    intent.putExtra("userImage", userImage);
                                    startActivity(intent);
                                } else {
                                    Snackbar.make(view, "Cannot access a chat with yourself.", Snackbar.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Integer> call, Throwable t) {
                                Snackbar.make(view, "Error: "+t.getMessage(), Snackbar.LENGTH_SHORT).show();
                                t.printStackTrace();
                            }
                        });

                    } else {
                        Snackbar.make(view, "Cannot access organizers info. Try again later.", Snackbar.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<UserDTO> call, Throwable t) {
                    Snackbar.make(view, "Error: "+t.getMessage(), Snackbar.LENGTH_SHORT).show();
                    t.printStackTrace();
                }
            });
        });


        Button btnJoin = view.findViewById(R.id.btn_join);
        btnJoin.setOnClickListener(v -> {
            ClientUtils.eventService.isParticipating(event.getId()).enqueue(new Callback<Boolean>() {
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        updateJoinButton(view, btnJoin, response.body());
                    }
                }

                @Override
                public void onFailure(Call<Boolean> call, Throwable t) {
                    Snackbar.make(view, "Failed to check participation", Snackbar.LENGTH_SHORT).show();
                }
            });
        });

        Button btnInfo = view.findViewById(R.id.btn_download_info);
        btnInfo.setOnClickListener(v -> {
            ClientUtils.eventService.getInfoPdf(event.getId()).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        boolean success = savePdfToDownloads(response.body(), "event_info_" + event.getId() + ".pdf");
                        String message = success ? "PDF saved to Downloads" : "Failed to save PDF";
                        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
                    } else {
                        Snackbar.make(view, "PDF download failed", Snackbar.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Snackbar.make(view, "Network error while downloading PDF", Snackbar.LENGTH_SHORT).show();
                }
            });
        });


        Button btnAgenda = view.findViewById(R.id.btn_download_agenda);
        btnAgenda.setOnClickListener(v -> {
            ClientUtils.organizerService.getAgendaPdf(event.getId()).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        boolean success = savePdfToDownloads(response.body(), "event_agenda_" + event.getId() + ".pdf");
                        String message = success ? "Agenda PDF saved to Downloads" : "Failed to save Agenda PDF";
                        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
                    } else {
                        Snackbar.make(view, "Agenda download failed", Snackbar.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Snackbar.make(view, "Network error while downloading agenda", Snackbar.LENGTH_SHORT).show();
                }
            });
        });

    }
    private boolean savePdfToDownloads(ResponseBody body, String fileName) {
        try {
            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if (!downloadsDir.exists()) {
                downloadsDir.mkdirs();
            }
            File pdfFile = new File(downloadsDir, fileName);

            InputStream inputStream = body.byteStream();
            OutputStream outputStream = new FileOutputStream(pdfFile);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.flush();
            outputStream.close();
            inputStream.close();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void updateJoinButton(View view, Button btnJoin, boolean isJoined) {
        if (isJoined) {
            btnJoin.setText("Cancel Participation");
            btnJoin.setOnClickListener(v -> {
                ClientUtils.eventService.removeParticipation(event.getId()).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        Snackbar.make(view, "You left the event.", Snackbar.LENGTH_SHORT).show();
                        btnJoin.setText("Join");
                        updateJoinButton(view, btnJoin, false);
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Snackbar.make(view, "Cancel failed!", Snackbar.LENGTH_SHORT).show();
                    }
                });
            });
        } else {
            btnJoin.setText("Join");
            btnJoin.setOnClickListener(v -> {
                ClientUtils.eventService.participate(event.getId()).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        Snackbar.make(view, "You joined the event!", Snackbar.LENGTH_SHORT).show();
                        btnJoin.setText("Cancel Participation");
                        updateJoinButton(view, btnJoin, true);
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Snackbar.make(view, "Join failed!", Snackbar.LENGTH_SHORT).show();
                    }
                });
            });
        }
    }

    private void populateUI(View view) {
        ((TextView) view.findViewById(R.id.event_title)).setText(event.getName());
        ((TextView) view.findViewById(R.id.event_description)).setText(event.getDescription());
        ((TextView) view.findViewById(R.id.event_place)).setText(event.getPlace());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy 'u' HH:mm", Locale.getDefault());
        String formattedDate = event.getDate().format(formatter);
        ((TextView) view.findViewById(R.id.event_date)).setText(formattedDate);

        ViewPager2 imageCarousel = view.findViewById(R.id.event_image_carousel);
        if (event.getPhotos() != null && !event.getPhotos().isEmpty()) {
            ImageCarouselAdapter adapter = new ImageCarouselAdapter(getContext(), event.getPhotos());
            imageCarousel.setAdapter(adapter);
        }
    }
}
