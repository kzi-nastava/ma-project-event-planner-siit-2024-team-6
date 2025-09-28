package com.example.eventure.dto;

import com.example.eventure.model.Status;

public class CategorySuggestionDTO {
    private String suggestion;
    private Status status;

    public String getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status s) {
        status = s;
    }
}
