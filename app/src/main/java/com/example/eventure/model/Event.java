package com.example.eventure.model;

import android.app.Activity;

import com.example.eventure.dto.EventDTO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Event {

    private Integer id;
    private String name;
    private String description;
    private Integer maxParticipants;
    private Integer participants;
    private Boolean isPublic;
    private String place;
    private LocalDateTime date;
    private Double rating;
    private List<String> photos;
    private EventType eventType;
    private List<Activity> eventActivities;
    //private Budget budget;
    //private List<Product> products;
    private Boolean isDeleted;

    public Event() {}

    public Event(
            Integer id,
            String name,
            String description,
            Integer maxParticipants,
            Integer participants,
            Boolean isPublic,
            String place,
            LocalDateTime date,
            EventType eventType,
            List<Activity> activities
            //,Budget budget,
            //List<Product> products
            ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.maxParticipants = maxParticipants;
        this.participants = participants;
        this.isPublic = isPublic;
        this.place = place;
        this.date = date;
        this.eventType = eventType;
        this.eventActivities = activities;
        //this.budget = budget;
        //this.products = products;
        this.rating = 0.0;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Integer getMaxParticipants() {
        return maxParticipants;
    }

    public Integer getParticipants() {
        return participants;
    }

    public Boolean getPublic() {
        return isPublic;
    }

    public String getPlace() {
        return place;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public Double getRating() {
        return rating;
    }

    public List<String> getPhotos() {
        return photos;
    }

    public EventType getEventType() {
        return eventType;
    }

    public List<Activity> getEventActivities() {
        return eventActivities;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }
}
