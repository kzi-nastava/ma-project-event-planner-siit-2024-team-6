package com.example.eventure.dto;

import com.example.eventure.model.Category;
import com.example.eventure.model.EventType;

import java.util.List;

public class EventTypeDTO {
    private Integer id;
    private String name;
    private String description;
    private boolean isDeleted;
    private List<Category> categories;

    public EventTypeDTO(){}
    public EventTypeDTO(EventType e){
        this.description = e.getDescription();
        this.name = e.getName();
        this.isDeleted = e.getIsDeleted();
        this.id = e.getId();
        this.categories = e.getCategories();
    }

    public EventTypeDTO(String name, String description, boolean isDeleted, List<Category> categories) {
        this.name = name;
        this.description = description;
        this.isDeleted = isDeleted;
        this.categories = categories;
    }

    public EventTypeDTO(Integer id, String name, String description, boolean isDeleted, List<Category> categories) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.isDeleted = isDeleted;
        this.categories = categories;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
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
    @Override
    public String toString() {
        return name; // или getName() если поле приватное
    }

}
