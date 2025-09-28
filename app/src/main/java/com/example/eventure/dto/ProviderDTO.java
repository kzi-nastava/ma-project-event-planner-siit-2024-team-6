package com.example.eventure.dto;

public class ProviderDTO extends UserDTO{
    private int id;
    private String companyEmail;
    private String companyName;
    private String companyAddress;
    private String description;
    private String[] companyPhotos;
    private String openingTime;
    private String closingTime;

    public String getClosingTime() {
        return closingTime;
    }
    public String getCompanyAddress() {
        return companyAddress;
    }
    public String getCompanyEmail() {
        return companyEmail;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getDescription() {
        return description;
    }

    public String getOpeningTime() {
        return openingTime;
    }

    public String[] getCompanyPhotos() {
        return companyPhotos;
    }


}
