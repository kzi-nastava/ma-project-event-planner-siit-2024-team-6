package com.example.eventure.model;

import java.util.Date;

public class Event {

    private int photoID;  // Changed from String to int
    private String title;
    private String location;
    private Date date;
    private String time;
    private float rating;
    private String description;  // Added description attribute

    public Event() {}

    // Updated constructor to use int for photoID
    public Event(int photoID, String title, String location, Date date, String time, float rating, String description) {
        this.photoID = photoID;
        this.title = title;
        this.location = location;
        this.date = date;
        this.time = time;
        this.rating = rating;
        this.description = description;  // Initialize the description
    }

    // Getter and setter for photoID
    public int getPhotoID() {
        return photoID;
    }

    public void setPhotoID(int photoID) {
        this.photoID = photoID;
    }

    // Getter and setter for title
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    // Getter and setter for location
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    // Getter and setter for date
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    // Getter and setter for time
    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    // Getter and setter for rating
    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    // Getter and setter for description
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Event{" +
                "photoID=" + photoID +  // Updated to show photoID
                ", title='" + title + '\'' +
                ", location='" + location + '\'' +
                ", date=" + date +
                ", time='" + time + '\'' +
                ", rating=" + rating +
                ", description='" + description + '\'' +  // Include description in toString
                '}';
    }
}
