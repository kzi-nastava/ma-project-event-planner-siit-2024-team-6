package com.example.eventure.dto;

public class RegistrationRequestDTO {
    private String email;
    private String password;
    private String photoUrl;
    private String name;
    private String lastname;
    private String address;
    private String phoneNumber;
    private String companyEmail;
    private String companyName;
    private String companyAddress;
    private String description;
    private String companyPhoto;
    private String openingTime;
    private String closingTime;

    private String role;

    public RegistrationRequestDTO() {
    }

    public RegistrationRequestDTO(String email, String password, String photoUrl, String name, String lastname, String address, String phoneNumber, String companyEmail, String companyName, String companyAddress, String description, String companyPhoto, String openingTime, String closingTime, String role) {
        this.email = email;
        this.password = password;
        this.photoUrl = photoUrl;
        this.name = name;
        this.lastname = lastname;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.companyEmail = companyEmail;
        this.companyName = companyName;
        this.companyAddress = companyAddress;
        this.description = description;
        this.companyPhoto = companyPhoto;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCompanyEmail() {
        return companyEmail;
    }

    public void setCompanyEmail(String companyEmail) {
        this.companyEmail = companyEmail;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCompanyPhoto() {
        return companyPhoto;
    }

    public void setCompanyPhoto(String companyPhoto) {
        this.companyPhoto = companyPhoto;
    }

    public String getOpeningTime() {
        return openingTime;
    }

    public void setOpeningTime(String openingTime) {
        this.openingTime = openingTime;
    }

    public String getClosingTime() {
        return closingTime;
    }

    public void setClosingTime(String closingTime) {
        this.closingTime = closingTime;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
