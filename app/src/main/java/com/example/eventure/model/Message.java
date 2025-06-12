package com.example.eventure.model;

public class Message {
    private Integer id;
    private String text;
    private boolean isFromUser;

    public Message(){}
    public Message(int id, String text, boolean isFromUser){
        this.id = id;
        this.text = text;
        this.isFromUser = isFromUser;
    }

    // Getters and setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public boolean isFromUser() { return isFromUser; }
    public void setFromUser(boolean fromUser) { isFromUser = fromUser; }
}

