package com.example.eventure.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventure.R;
import com.example.eventure.activities.ChatActivity;
import com.example.eventure.adapters.ChatAdapter;
import com.example.eventure.clients.ClientUtils;
import com.example.eventure.model.Chat;
import com.google.android.material.snackbar.Snackbar;

import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatsFragment extends Fragment {
    private Context context;
    private RecyclerView recyclerView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messages_center, container, false);
        context = getActivity();

        recyclerView = view.findViewById(R.id.recycler_messages);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        loadChats(view);
        return view;
    }

    private void loadChats(View view){
        Call<List<Chat>> call = ClientUtils.chatService.getAllChats();

        call.enqueue(new Callback<List<Chat>>() {
            @Override
            public void onResponse(Call<List<Chat>> call, Response<List<Chat>> response) {
                if (response.isSuccessful()) {
                    List<Chat> chatList = response.body();
                    ChatAdapter adapter = new ChatAdapter(context, chatList, chat -> {
                        ((ChatActivity) getActivity()).openChat(chat.getId(), chat.getName(), chat.getPhotoUrl());
                    });
                    recyclerView.setAdapter(adapter);
                } else {
                    Snackbar.make(view, "Server error", Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Chat>> call, Throwable t) {
                Snackbar.make(view, "Failed to load chats", Snackbar.LENGTH_SHORT).show();
            }
        });
    }
}
