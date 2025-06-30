package com.example.eventure.dto;

import java.time.LocalDateTime;

public class CalendarItemDTO {
    private int id;
    private String name;
    private String type;
    private LocalDateTime date;

    public CalendarItemDTO(int id, String name, String type, LocalDateTime date) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}
