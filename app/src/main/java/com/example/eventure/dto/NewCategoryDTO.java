package com.example.eventure.dto;

public class NewCategoryDTO {
    private String name;
    private String description;

    // Constructors
    public NewCategoryDTO(){}
    public NewCategoryDTO(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
