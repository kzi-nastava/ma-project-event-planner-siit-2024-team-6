package com.example.eventure.dto;

import java.time.LocalDateTime;

public class ReservationDTO {
    private Integer id;
    private Integer serviceId;
    private LocalDateTime start;
    private LocalDateTime end;

    public ReservationDTO() {
    }

    public ReservationDTO(Integer id, Integer serviceId, LocalDateTime start, LocalDateTime end) {
        this.id = id;
        this.serviceId = serviceId;
        this.start = start;
        this.end = end;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }
}
