package com.example.eventure.dto;

import com.example.eventure.model.EventType;

public class EventTypeDTO {
    private String name;
    private String description;
    private boolean isDeleted;

    public EventTypeDTO(){}
    public EventTypeDTO(EventType e){
        this.description = e.getDescription();
        this.name = e.getName();
        this.isDeleted = e.getIsDeleted();
    }
}
