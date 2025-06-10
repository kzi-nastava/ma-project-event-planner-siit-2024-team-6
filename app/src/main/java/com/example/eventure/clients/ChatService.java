package com.example.eventure.clients;

import com.example.eventure.dto.ChatWithMessagesDTO;
import com.example.eventure.dto.NewMessageDTO;
import com.example.eventure.model.Chat;
import com.example.eventure.model.Message;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ChatService {
    @GET("chats/")  // replace with full path if necessary
    Call<List<Chat>> getAllChats();
    @GET("chats/{chatId}")
    Call<ChatWithMessagesDTO> getChat(@Path("chatId") int chatId);
    @POST("chats/{chatId}/send")
    Call<Message> sendMessage(
            @Path("chatId") int chatId,
            @Body NewMessageDTO message // pass "Bearer <token>"
    );
}
