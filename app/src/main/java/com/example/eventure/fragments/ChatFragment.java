package com.example.eventure.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.eventure.R;
import com.example.eventure.adapters.MessageAdapter;
import com.example.eventure.clients.ClientUtils;
import com.example.eventure.dto.ChatWithMessagesDTO;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatFragment extends Fragment {
    private static final String ARG_CHAT_ID = "id";
    private static final String ARG_NAME = "name";
    private static final String ARG_IMAGE = "image";

    public static ChatFragment newInstance(int id, String name, String image) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_CHAT_ID, id);
        args.putString(ARG_NAME, name);
        args.putString(ARG_IMAGE, image);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        TextView nameView = view.findViewById(R.id.chat_name);
        ImageView profileImage = view.findViewById(R.id.chat_image);
        RecyclerView chatRecycler = view.findViewById(R.id.chat_recycler);
        chatRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        int chatId = getArguments().getInt(ARG_CHAT_ID);

        ClientUtils.chatService.getChat(chatId).enqueue(new Callback<ChatWithMessagesDTO>() {
            @Override
            public void onResponse(Call<ChatWithMessagesDTO> call, Response<ChatWithMessagesDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ChatWithMessagesDTO data = response.body();

                    nameView.setText(data.getChat().getName());

                    Glide.with(ChatFragment.this)
                            .load(data.getChat().getPhotoUrl())
                            .placeholder(R.drawable.placeholder_image)
                            .error(R.drawable.error_image)
                            .into(profileImage);

                    chatRecycler.setAdapter(new MessageAdapter(data.getMessages()));
                } else {
                    Toast.makeText(getContext(), "Failed to load chat", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ChatWithMessagesDTO> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        return view;
    }
}
