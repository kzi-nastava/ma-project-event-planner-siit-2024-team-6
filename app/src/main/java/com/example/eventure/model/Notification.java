package com.example.eventure.model;

import com.example.eventure.dto.NotificationDTO;

import java.time.LocalDateTime;

public class Notification {

    private Integer id;
    private String text;

    private User receiver;
    private LocalDateTime timestamp;

    private NotificationType type;

    public Notification() {}
//    public Notification(NotificationDTO dto){
//        this.text = dto.getText();
//        this.timestamp = dto.getTimestamp();
//    }


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

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
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
