package com.example.eventure.dto;

import java.time.LocalDateTime;

public class NewReservationDTO {
    private Integer serviceId;
    private Integer eventId;
    private String startTime;
    private String endTime;

    // No-arg constructor
    public NewReservationDTO() {
    }

    // All-args constructor
    public NewReservationDTO(Integer serviceId, Integer eventId, String startTime, String endTime) {
        this.serviceId = serviceId;
        this.eventId = eventId;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Getters and setters
    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public String  getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
