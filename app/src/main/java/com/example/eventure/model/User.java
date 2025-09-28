package com.example.eventure.model;

import com.example.eventure.dto.UserDTO;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class User{

    private int id;

    private String email;

    private String password;

    private String photoUrl;

    private Boolean isActive = true; // Значение по умолчанию для новых пользователей

    private LocalDateTime suspendedSince = LocalDateTime.now();

    private String name;

    private String lastname;

    private String address;

    private String phoneNumber;

    private Timestamp lastPasswordResetDate;

    private String userType;

    private List<Offer> favouriteOffers = new ArrayList<>();

    private List<Event> favouriteEvents = new ArrayList<>();

    private List<Event> attends = new ArrayList<>();

    private List<Notification> notifications = new ArrayList<>();

    public User(UserDTO user) {
        
    }
    public User() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean active) {
        isActive = active;
    }


    public LocalDateTime getSuspendedSince() {
        return suspendedSince;
    }

    public void setSuspendedSince(LocalDateTime suspendedSince) {
        this.suspendedSince = suspendedSince;
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

    public Timestamp getLastPasswordResetDate() {
        return lastPasswordResetDate;
    }

    public void setLastPasswordResetDate(Timestamp lastPasswordResetDate) {
        this.lastPasswordResetDate = lastPasswordResetDate;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public List<Offer> getFavouriteOffers() {
        return favouriteOffers;
    }

    public void setFavouriteOffers(List<Offer> favouriteOffers) {
        this.favouriteOffers = favouriteOffers;
    }

    public List<Event> getFavouriteEvents() {
        return favouriteEvents;
    }

    public void setFavouriteEvents(List<Event> favouriteEvents) {
        this.favouriteEvents = favouriteEvents;
    }

    public List<Event> getAttends() {
        return attends;
    }

    public void setAttends(List<Event> attends) {
        this.attends = attends;
    }

    public List<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }
}
