package com.example.eventure.fragments;

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

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    }

    private void loadNotifications(int page, int size) {
        isLoading = true;
        Log.d("NTag","USAO");
        Call<PagedResponse<NotificationDTO>> call = ClientUtils.notificationService.getByReceiver(
                receiverId,
                page,
                size,
                "timestamp",
                "desc"
        );

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<PagedResponse<NotificationDTO>> call, Response<PagedResponse<NotificationDTO>> response) {
                isLoading = false;

                if (response.isSuccessful() && response.body() != null) {
                    List<NotificationDTO> newNotifications = response.body().getContent();
                    Log.d("NTag","Stigle");
                    Log.d("NTag",String.valueOf(newNotifications.size()));

                    if (page == 0) {
                        adapter.setNotifications(newNotifications);
                    }else{
                        adapter.addNotifications(newNotifications);
                    }
                    currentPage = page;

                    if (adapter.getItemCount() >= response.body().getTotalElements()) {
                        isLastPage = true;
                    }

                    btnLoadMore.setVisibility(isLastPage ? View.GONE : View.VISIBLE);
                } else {
                    Toast.makeText(getContext(), "Failed to load notifications", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<PagedResponse<NotificationDTO>> call, Throwable t) {
                isLoading = false;
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("NotificationsFragment", "Error loading notifications", t);
            }
        });
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

}
