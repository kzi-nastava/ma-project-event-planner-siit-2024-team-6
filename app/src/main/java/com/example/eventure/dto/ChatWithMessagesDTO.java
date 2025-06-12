package com.example.eventure.dto;

import com.example.eventure.model.Chat;
import com.example.eventure.model.Message;

import java.util.List;

public class ChatWithMessagesDTO {
        private Chat chat;
        private List<Message> messages;

        public Chat getChat() { return chat; }
        public List<Message> getMessages() { return messages; }
}
