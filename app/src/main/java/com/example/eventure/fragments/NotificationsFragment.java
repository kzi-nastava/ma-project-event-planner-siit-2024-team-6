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

    private int receiverId;
    private int currentPage = 0;
    private final int pageSize = ClientUtils.PAGE_SIZE;
    private boolean isLastPage = false;
    private boolean isLoading = false;

    private StompClient stompClient;



    public NotificationsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notifications, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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

        loadNotifications(0, pageSize);  // initial load
        connectWebSocket(receiverId);

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


    @SuppressLint("CheckResult")
    private void connectWebSocket(int userId) {
        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, "ws://10.0.2.2:8080/socket");

        stompClient.lifecycle().subscribe(lifecycleEvent -> {
            switch (lifecycleEvent.getType()) {
                case OPENED:
                    Log.d("STOMP", "Notification socket connected");

                    stompClient.topic("/socket-publisher/notifications/" + userId)
                            .subscribe(topicMessage -> {
                                String json = topicMessage.getPayload();
                                Log.d("STOMP", "Received JSON: " + json);
                                NotificationDTO newNotification = new Gson().fromJson(json, NotificationDTO.class);
                                requireActivity().runOnUiThread(() -> {
                                    adapter.addNotificationAtTop(newNotification);
                                    recyclerView.scrollToPosition(0);
                                });

                            }, throwable -> Log.e("STOMP", "Notification subscribe error", throwable));
                    break;

                case ERROR:
                    Log.e("STOMP", "Notification socket error", lifecycleEvent.getException());
                    break;

                case CLOSED:
                    Log.d("STOMP", "Notification socket closed");
                    break;
            }
        });

        stompClient.connect();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Hide bottom nav when this fragment is visible
        if (getActivity() != null) {
            View bottomNav = getActivity().findViewById(R.id.bottom_navigation);
            if (bottomNav != null) {
                bottomNav.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        // Show bottom nav when leaving this fragment
        if (getActivity() != null) {
            View bottomNav = getActivity().findViewById(R.id.bottom_navigation);
            if (bottomNav != null) {
                bottomNav.setVisibility(View.VISIBLE);
            }
        }
    }
    @Override
    public void onDestroyView() {
        if (stompClient != null) stompClient.disconnect();
        super.onDestroyView();
    }


}
