package com.example.eventure.dto;

import com.example.eventure.model.Notification;
import com.example.eventure.model.NotificationType;


import java.time.LocalDateTime;

public class NotificationDTO {
    private Integer id;
    private String text;
    private Integer receiverId;
    private LocalDateTime timestamp;
    private NotificationType type;

    public NotificationDTO() {}

    public NotificationDTO(Notification notification) {
        this.id = notification.getId();
        this.text = notification.getText();
        this.receiverId = notification.getReceiver().getId();
        this.timestamp = notification.getTimestamp();
        this.type = notification.getType();
    }
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Integer receiverId) {
        this.receiverId = receiverId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }
}
