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
}
