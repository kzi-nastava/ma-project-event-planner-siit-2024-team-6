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
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }
    public String getDescription(){
        return  description;
    }
    public void setDescription(String description){
        this.description = description;
    }
    public boolean getIsDeleted(){
        return isDeleted;
    }
    public void setIsDeleted(boolean value){
        isDeleted = value;
    }
}
