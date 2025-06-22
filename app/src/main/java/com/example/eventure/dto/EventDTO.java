package com.example.eventure.dto;

import com.example.eventure.model.Event;
import com.example.eventure.model.Status;

import java.time.LocalDateTime;
import java.util.List;


public class EventDTO {
    private Integer id;
    private String name;
    private String description;
    private Integer maxParticipants;
    private Integer participants;
    private Boolean isPublic;
    private String place;
    private LocalDateTime date;
    private EventTypeDTO eventType;
    private List<String> photos;
    private Boolean isDeleted;
    private Double rating;

    public EventDTO(Integer id, String name, String description, Integer maxParticipants, Integer participants, Boolean isPublic, String place, LocalDateTime date, EventTypeDTO eventType, List<String> photos, Boolean isDeleted, Double rating) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.maxParticipants = maxParticipants;
        this.participants = participants;
        this.isPublic = isPublic;
        this.place = place;
        this.date = date;
        this.eventType = eventType;
        this.photos = photos;
        this.isDeleted = isDeleted;
        this.rating = rating;
    }

    public EventDTO() {
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

    public EventTypeDTO getEventType() {
        return eventType;
    }

    public List<String> getPhotos() {
        return photos;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public Double getRating() {
        return rating;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setMaxParticipants(Integer maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public void setParticipants(Integer participants) {
        this.participants = participants;
    }

    public void setPublic(Boolean aPublic) {
        isPublic = aPublic;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public void setEventType(EventTypeDTO eventType) {
        this.eventType = eventType;
    }

    public void setPhotos(List<String> photos) {
        this.photos = photos;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }
}
