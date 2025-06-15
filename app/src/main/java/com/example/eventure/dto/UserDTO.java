package com.example.eventure.dto;

import com.example.eventure.model.User;

import java.time.LocalDateTime;
import java.util.List;

public class UserDTO {
    private Integer id;
    private String email;
    private String name;
    private String lastname;
    private String address;
    private String userType;
    private String phoneNumber;
    private String photoUrl;
    private boolean isActive;

    private LocalDateTime suspendedSince;
    private List<OfferDTO> favouriteOffers;
    private List<EventDTO> favouriteEvents;
    private List<EventDTO> attends;
    private List<NotificationDTO> notifications;

    private String companyEmail;
    private String companyAddress;
    private String description;
    private String openingTime;
    private String closingTime;
    private String[] companyPhotos;


    public UserDTO() {}
    public UserDTO(User user) {
        if (user != null) {
            this.userType = user.getUserType();
            this.id = user.getId();
            this.email = user.getEmail();
            this.name = user.getName();
            this.lastname = user.getLastname();
            this.address = user.getAddress();
            this.phoneNumber = user.getPhoneNumber();
            this.photoUrl = user.getPhotoUrl();
            this.isActive = user.getIsActive();
            this.suspendedSince = user.getSuspendedSince();
        }
    }

    public User toUser() {
        User user = new User();
        user.setEmail(this.email);
        user.setName(this.name);
        user.setLastname(this.lastname);
        user.setAddress(this.address);
        user.setPhoneNumber(this.phoneNumber);
        user.setPhotoUrl(this.photoUrl);
        user.setIsActive(this.isActive);
        user.setSuspendedSince(this.suspendedSince);
        return user;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public LocalDateTime getSuspendedSince() {
        return suspendedSince;
    }

    public void setSuspendedSince(LocalDateTime suspendedSince) {
        this.suspendedSince = suspendedSince;
    }

    public List<OfferDTO> getFavouriteOffers() {
        return favouriteOffers;
    }

    public void setFavouriteOffers(List<OfferDTO> favouriteOffers) {
        this.favouriteOffers = favouriteOffers;
    }

    public List<EventDTO> getFavouriteEvents() {
        return favouriteEvents;
    }

    public void setFavouriteEvents(List<EventDTO> favouriteEvents) {
        this.favouriteEvents = favouriteEvents;
    }

    public List<EventDTO> getAttends() {
        return attends;
    }

    public void setAttends(List<EventDTO> attends) {
        this.attends = attends;
    }

    public List<NotificationDTO> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<NotificationDTO> notifications) {
        this.notifications = notifications;
    }

    public String getCompanyEmail() {
        return companyEmail;
    }

    public void setCompanyEmail(String companyEmail) {
        this.companyEmail = companyEmail;
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

    public String[] getCompanyPhotos() {
        return companyPhotos;
    }

    public void setCompanyPhotos(String[] companyPhotos) {
        this.companyPhotos = companyPhotos;
    }
}
