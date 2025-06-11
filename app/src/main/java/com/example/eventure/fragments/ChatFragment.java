package com.example.eventure.fragments;

import static ua.naiksoftware.stomp.dto.LifecycleEvent.Type.OPENED;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.eventure.R;
import com.example.eventure.adapters.MessageAdapter;
import com.example.eventure.clients.AuthService;
import com.example.eventure.clients.ClientUtils;
import com.example.eventure.dto.ChatWithMessagesDTO;
import com.example.eventure.dto.NewMessageDTO;
import com.example.eventure.model.Message;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;

public class ChatFragment extends Fragment {
    private static final String ARG_CHAT_ID = "id";
    private static final String ARG_NAME = "name";
    private static final String ARG_IMAGE = "image";

    private MessageAdapter messageAdapter;
    private RecyclerView chatRecycler;
    private int chatId;
    private StompClient stompClient;

    public static ChatFragment newInstance(int id, String name, String image) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putInt("id", id);
        args.putString("name", name);
        args.putString("image", image);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        TextView nameView = view.findViewById(R.id.chat_name);
        ImageView profileImage = view.findViewById(R.id.chat_image);
        chatRecycler = view.findViewById(R.id.chat_recycler);
        chatRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        ImageView backButton = view.findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> {
            if (getActivity() != null) getActivity().onBackPressed();
        });

        EditText messageInput = view.findViewById(R.id.input_message);
        Button sendButton = view.findViewById(R.id.button_send);

        chatId = getArguments().getInt(ARG_CHAT_ID);

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

                    messageAdapter = new MessageAdapter(data.getMessages());
                    chatRecycler.setAdapter(messageAdapter);
                    scrollToBottom();
                } else {
                    Snackbar.make(view, "Failed to load chat!", Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ChatWithMessagesDTO> call, Throwable t) {
                Snackbar.make(view, "Error: " + t.getMessage(), Snackbar.LENGTH_SHORT).show();
            }
        });

        sendButton.setOnClickListener(v -> {
            String text = messageInput.getText().toString().trim();
            if (!text.isEmpty()) {
                sendMessage(chatId, text);
                messageInput.setText("");
            } else {
                Snackbar.make(view, "Please enter a message", Snackbar.LENGTH_SHORT).show();
            }
        });
        AuthService authService = new AuthService(getContext());
        connectWebSocket(authService.getUserId());
        return view;
    }

    @SuppressLint("CheckResult")
    private void connectWebSocket(int userId) {
        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, "ws://10.0.2.2:8080/socket");


        stompClient.lifecycle().subscribe(lifecycleEvent -> {
            switch (lifecycleEvent.getType()) {
                case OPENED:
                    Log.d("STOMP", "Connection opened");

                    stompClient.topic("/socket-publisher/messages/" + userId)
                            .subscribe(topicMessage -> {
                                String json = topicMessage.getPayload();
                                Message newMessage = new Gson().fromJson(json, Message.class);
                                getActivity().runOnUiThread(() -> {
                                    messageAdapter.addMessage(newMessage);
                                    chatRecycler.scrollToPosition(messageAdapter.getItemCount() - 1);
                                });
                            }, throwable -> {
                                Log.e("STOMP", "Subscribe error", throwable);
                            });
                    break;

                case ERROR:
                    Log.e("STOMP", "Error", lifecycleEvent.getException());
                    break;

                case CLOSED:
                    Log.d("STOMP", "Connection closed");
                    break;
            }
        });

        stompClient.connect();

    }

    @Override
    public void onDestroy() {
        stompClient.disconnect();
        super.onDestroy();
    }

    private void sendMessage(int chatId, String text) {
        NewMessageDTO dto = new NewMessageDTO(text);
        Call<Message> call = ClientUtils.chatService.sendMessage(chatId, dto);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Message sentMessage = response.body();
                    if (messageAdapter != null) {
                        messageAdapter.addMessage(sentMessage);
                        scrollToBottom();
                    }
                } else {
                    Snackbar.make(requireView(), "Failed to send message.", Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {
                Snackbar.make(requireView(), "Network error: " + t.getMessage(), Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void scrollToBottom() {
        if (messageAdapter != null && messageAdapter.getItemCount() > 0) {
            chatRecycler.post(() -> {
                chatRecycler.scrollToPosition(messageAdapter.getItemCount() - 1);
            });
        }
    }
}
