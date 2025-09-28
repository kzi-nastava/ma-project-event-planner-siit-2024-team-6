package com.example.eventure.fragments;

import static ua.naiksoftware.stomp.dto.LifecycleEvent.Type.CLOSED;
import static ua.naiksoftware.stomp.dto.LifecycleEvent.Type.ERROR;
import static ua.naiksoftware.stomp.dto.LifecycleEvent.Type.OPENED;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventure.R;
import com.example.eventure.adapters.NotificationAdapter;
import com.example.eventure.clients.ClientUtils;
import com.example.eventure.clients.NotificationService;
import com.example.eventure.clients.NotificationSocketManager;
import com.example.eventure.dto.NotificationDTO;
import com.example.eventure.model.PagedResponse;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;

public class NotificationsFragment extends Fragment {

    private RecyclerView recyclerView;
    private NotificationAdapter adapter;
    private List<NotificationDTO> notificationsList;
    private Button btnLoadMore;
    private Switch switchMuteNotifications;


    private Integer receiverId;
    private boolean isMuted = false;
    private int currentPage = 0;
    private final int pageSize = ClientUtils.PAGE_SIZE;
    private boolean isLastPage = false;
    private boolean isLoading = false;



    public NotificationsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notifications, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //navigation restriction (only for logged in users)
        if (!ClientUtils.getAuthService().isLoggedIn()) {
            TextView error = view.findViewById(R.id.notificationsError);
            error.setVisibility(View.VISIBLE);
            TextView commentsTv = view.findViewById(R.id.tvNotificationsLabel);
            commentsTv.setVisibility(View.GONE);
            switchMuteNotifications = view.findViewById(R.id.switchMuteNotifications);
            switchMuteNotifications.setVisibility(View.GONE);
            return;
        }
        //muted
        switchMuteNotifications = view.findViewById(R.id.switchMuteNotifications);
        isMuted = ClientUtils.getAuthService().isMuted();
        switchMuteNotifications.setChecked(isMuted);
        switchMuteNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            toggleMuteNotifications(isChecked);
        });

        recyclerView = view.findViewById(R.id.recycler_view_notifications);
        btnLoadMore = view.findViewById(R.id.loadMoreNotifications);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        receiverId = ClientUtils.getAuthService().getUserId();
        notificationsList = new ArrayList<>();
        adapter = new NotificationAdapter(notificationsList);
        recyclerView.setAdapter(adapter);

        btnLoadMore.setOnClickListener(v -> {
            if (!isLoading && !isLastPage) {
                loadNotifications(currentPage + 1, pageSize);
            }
        });

        loadNotifications(0, pageSize);

    }

    private void loadNotifications(int page, int size) {
        isLoading = true;
        Call<PagedResponse<NotificationDTO>> call = ClientUtils.notificationService.getByReceiver(
                receiverId,
                page,
                size,
                "timestamp",
                "desc"
        );

        call.enqueue(new Callback<PagedResponse<NotificationDTO>>() {
            @Override
            public void onResponse(Call<PagedResponse<NotificationDTO>> call, Response<PagedResponse<NotificationDTO>> response) {
                isLoading = false;
                if (response.code() == 204) {
                    Toast.makeText(getContext(), "No notifications available", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (response.isSuccessful() && response.body() != null) {
                    List<NotificationDTO> newNotifications = response.body().getContent();

                    if (page == 0) {
                        adapter.setNotifications(newNotifications);
                    } else {
                        adapter.addNotifications(newNotifications);
                    }

                    currentPage = page;

                    if (adapter.getItemCount() >= response.body().getTotalElements()) {
                        isLastPage = true;
                    }

                    btnLoadMore.setVisibility(isLastPage ? View.GONE : View.VISIBLE);
                } else {
                    Log.e("NTag", "Step R4: Response error - Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<PagedResponse<NotificationDTO>> call, Throwable t) {
                isLoading = false;
                Log.e("NTag", "Step R5: onFailure - " + t.getMessage(), t);
            }
        });


        Log.d("NTag", "Step 11: Enqueue called");
    }

    private void toggleMuteNotifications(boolean isMuted) {
        ClientUtils.getAuthService().saveMuted(isMuted);

        Call<Void> call = ClientUtils.notificationService.toggleMute(ClientUtils.getAuthService().getUserId(), isMuted);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), isMuted ? "Notifications muted" : "Notifications unmuted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Failed to update mute setting", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();

        if (getActivity() != null) {
            View bottomNav = getActivity().findViewById(R.id.bottom_navigation);
            if (bottomNav != null) {
                bottomNav.setVisibility(View.GONE);
            }
        }

        // Register for real-time notification updates
        NotificationSocketManager.getInstance().setNotificationListener(notification -> {
            requireActivity().runOnUiThread(() -> {
                adapter.addNotificationAtTop(notification);
                recyclerView.scrollToPosition(0);
            });

        });
    }

    @Override
    public void onPause() {
        super.onPause();
        NotificationSocketManager.getInstance().removeNotificationListener();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (getActivity() != null) {
            View bottomNav = getActivity().findViewById(R.id.bottom_navigation);
            if (bottomNav != null) {
                bottomNav.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


}
