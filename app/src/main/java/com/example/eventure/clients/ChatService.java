package com.example.eventure.clients;

import com.example.eventure.model.Chat;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ChatService {
    @GET("api/chats")  // replace with full path if necessary
    Call<List<Chat>> getAllChats();
}
