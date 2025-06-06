package com.example.eventure.model;

public class CategorySuggestion {
    Integer id;
    String name;
    String description;
    String offerName;
    String offerDescription;

    public CategorySuggestion(){}
    public CategorySuggestion(int id, String name, String description, String offerName, String offerDescription){
        this.id = id;
        this.name = name;
        this.description = description;
        this.offerDescription = offerDescription;
        this.offerName = offerName;
    }

    public int getId(){
        return this.id;
    }

    public String getName(){
        return this.name;
    }
    public String getDescription(){
        return this.description;
    }

    public String getOfferName(){
        return this.offerName;
    }

    public String getOfferDescription(){
        return this.offerDescription;
    }
}
