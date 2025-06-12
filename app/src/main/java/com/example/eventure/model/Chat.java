package com.example.eventure.model;

public class Chat {
    private int id;
    private String name;
    private String photoUrl;

    public Chat(int id, String name, String lastName, String photoUrl) {
        this.id = id;
        this.name = name + " " + lastName;
        this.photoUrl = photoUrl;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getPhotoUrl() { return photoUrl; }
}
