package com.example.eventure.clients;

import com.example.eventure.dto.ChatWithMessagesDTO;
import com.example.eventure.model.Chat;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ChatService {
    @GET("chats/")  // replace with full path if necessary
    Call<List<Chat>> getAllChats();
    @GET("chats/{chatId}")
    Call<ChatWithMessagesDTO> getChat(@Path("chatId") int chatId);
}
