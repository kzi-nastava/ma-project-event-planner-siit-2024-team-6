package com.example.eventure.dto;

import com.example.eventure.model.Status;

import java.util.List;

public class OfferDTO {
    private Status status;
    private String name;
    private String description;
    private Double price;
    private Double sale;
    private List<String> photos;
    private Boolean isVisible;
    private Boolean isAvailable;
    private Boolean isDeleted;
    private String category;
    private List<EventTypeDTO> eventTypes;
    private String type; // product or service

    // Service fields
    private String specifics;
    private int minDuration;
    private int maxDuration;
    private int preciseDuration;
    private int latestReservation;
    private int latestCancelation;
    private boolean isReservationAutoApproved;

    // Getters and Setters
    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getSale() {
        return sale;
    }

    public void setSale(Double sale) {
        this.sale = sale;
    }

    public List<String> getPhotos() {
        return photos;
    }

    public void setPhotos(List<String> photos) {
        this.photos = photos;
    }

    public Boolean getIsVisible() {
        return isVisible;
    }

    public void setIsVisible(Boolean isVisible) {
        this.isVisible = isVisible;
    }

    public Boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<EventTypeDTO> getEventTypes() {
        return eventTypes;
    }

    public void setEventTypes(List<EventTypeDTO> eventTypes) {
        this.eventTypes = eventTypes;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSpecifics() {
        return specifics;
    }

    public void setSpecifics(String specifics) {
        this.specifics = specifics;
    }

    public int getMinDuration() {
        return minDuration;
    }

    public void setMinDuration(int minDuration) {
        this.minDuration = minDuration;
    }

    public int getMaxDuration() {
        return maxDuration;
    }

    public void setMaxDuration(int maxDuration) {
        this.maxDuration = maxDuration;
    }

    public int getPreciseDuration() {
        return preciseDuration;
    }

    public void setPreciseDuration(int preciseDuration) {
        this.preciseDuration = preciseDuration;
    }

    public int getLatestReservation() {
        return latestReservation;
    }

    public void setLatestReservation(int latestReservation) {
        this.latestReservation = latestReservation;
    }

    public int getLatestCancelation() {
        return latestCancelation;
    }

    public void setLatestCancelation(int latestCancelation) {
        this.latestCancelation = latestCancelation;
    }

    public boolean isReservationAutoApproved() {
        return isReservationAutoApproved;
    }

    public void setReservationAutoApproved(boolean reservationAutoApproved) {
        isReservationAutoApproved = reservationAutoApproved;
    }
}
