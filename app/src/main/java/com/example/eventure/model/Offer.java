package com.example.eventure.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.eventure.dto.EventTypeDTO;
import com.example.eventure.dto.OfferDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Offer implements Parcelable, Serializable {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("status")
    @Expose
    private Status status;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("price")
    @Expose
    private Double price;
    @SerializedName("sale")
    @Expose
    private Double sale;
    @SerializedName("photos")
    @Expose
    private List<String> photos;
    @SerializedName("isVisible")
    @Expose
    private Boolean isVisible;
    @SerializedName("isAvailable")
    @Expose
    private Boolean isAvailable;
    @SerializedName("isDeleted")
    @Expose
    private Boolean isDeleted;
    @SerializedName("category")
    @Expose
    private String category;
    @SerializedName("eventTypes")
    @Expose
    private List<EventType> eventTypes;
    @SerializedName("type")
    @Expose
    private String type; // product or service


    // Service fields
    @SerializedName("specifics")
    @Expose
    private String specifics;
    @SerializedName("minDuration")
    @Expose
    private int minDuration;
    @SerializedName("maxDuration")
    @Expose
    private int maxDuration;
    @SerializedName("preciseDuration")
    @Expose
    private int preciseDuration;
    @SerializedName("latestReservation")
    @Expose
    private int latestReservation;
    @SerializedName("latestCancelation")
    @Expose
    private int latestCancellation;
    @SerializedName("isReservationAutoApproved")
    @Expose
    private boolean isReservationAutoApproved;

    public Offer() {
    }

    public Offer(int id, String name, Status status, String description, double price, double sale, List<String> photos, boolean isAvailable, boolean isVisible, boolean isDeleted, List<EventType> eventTypes, String category, String type, String specifics, int minDuration, int maxDuration, int preciseDuration, int latestCancellation, int latestReservation, boolean isReservationAutoApproved) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.description = description;
        this.photos = photos;
        this.price = price;
        this.sale = sale;
        this.isAvailable = isAvailable;
        this.isDeleted = isDeleted;
        this.isVisible = isVisible;
        this.eventTypes = eventTypes;
        this.category = category;
        this.type = type;
        this.specifics = specifics;
        this.maxDuration = maxDuration;
        this.minDuration = minDuration;
        this.preciseDuration = preciseDuration;
        this.latestCancellation = latestCancellation;
        this.latestReservation = latestReservation;
        this.isReservationAutoApproved = isReservationAutoApproved;
    }

    public Offer(OfferDTO offerDTO){
        this.id = offerDTO.getId();
        this.name = offerDTO.getName();
        this.status = offerDTO.getStatus();
        this.description = offerDTO.getDescription();
        this.photos = offerDTO.getPhotos();
        this.price = offerDTO.getPrice();
        this.sale = offerDTO.getSale();
        this.isAvailable = offerDTO.getIsAvailable();
        this.isDeleted = offerDTO.getIsDeleted();
        this.isVisible = offerDTO.getIsVisible();
        this.eventTypes = new ArrayList<>();
        for (EventTypeDTO et: offerDTO.getEventTypes()){
            this.eventTypes.add(new EventType(et));
        }
        this.category = offerDTO.getCategory();
        this.type = offerDTO.getType();
        this.specifics = offerDTO.getSpecifics();
        this.maxDuration = offerDTO.getMaxDuration();
        this.minDuration = offerDTO.getMinDuration();
        this.preciseDuration = offerDTO.getPreciseDuration();
        this.latestCancellation = offerDTO.getLatestCancelation();
        this.latestReservation = offerDTO.getLatestReservation();
        this.isReservationAutoApproved = offerDTO.isReservationAutoApproved();
    }

    public Offer(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        status = in.readByte() == 0 ? null : Status.valueOf(in.readString());
        name = in.readString();
        description = in.readString();
        if (in.readByte() == 0) {
            price = null;
        } else {
            price = in.readDouble();
        }
        if (in.readByte() == 0) {
            sale = null;
        } else {
            sale = in.readDouble();
        }
        photos = in.createStringArrayList();
        byte tmpIsVisible = in.readByte();
        isVisible = tmpIsVisible == 0 ? null : tmpIsVisible == 1;
        byte tmpIsAvailable = in.readByte();
        isAvailable = tmpIsAvailable == 0 ? null : tmpIsAvailable == 1;
        byte tmpIsDeleted = in.readByte();
        isDeleted = tmpIsDeleted == 0 ? null : tmpIsDeleted == 1;
        category = in.readString();
        eventTypes = in.createTypedArrayList(EventType.CREATOR);
        type = in.readString();
        specifics = in.readString();
        minDuration = in.readInt();
        maxDuration = in.readInt();
        preciseDuration = in.readInt();
        latestReservation = in.readInt();
        latestCancellation = in.readInt();
        isReservationAutoApproved = in.readByte() != 0;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getSale() {
        return sale;
    }

    public void setSale(Double sale) {
        this.sale = sale;
    }

    public List<String> getPhotos() {
        return photos;
    }

    public void setPhotos(List<String> photos) {
        this.photos = photos;
    }

    public Boolean getIsVisible() {
        return isVisible;
    }

    public void setIsVisible(Boolean isVisible) {
        this.isVisible = isVisible;
    }

    public Boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<EventType> getEventTypes() {
        return eventTypes;
    }

    public void setEventTypes(List<EventType> eventTypes) {
        this.eventTypes = eventTypes;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSpecifics() {
        return specifics;
    }

    public void setSpecifics(String specifics) {
        this.specifics = specifics;
    }

    public int getMinDuration() {
        return minDuration;
    }

    public void setMinDuration(int minDuration) {
        this.minDuration = minDuration;
    }

    public int getMaxDuration() {
        return maxDuration;
    }

    public void setMaxDuration(int maxDuration) {
        this.maxDuration = maxDuration;
    }

    public int getPreciseDuration() {
        return preciseDuration;
    }

    public void setPreciseDuration(int preciseDuration) {
        this.preciseDuration = preciseDuration;
    }

    public int getLatestReservation() {
        return latestReservation;
    }

    public void setLatestReservation(int latestReservation) {
        this.latestReservation = latestReservation;
    }

    public int getLatestCancellation() {
        return latestCancellation;
    }

    public void setLatestCancellation(int latestCancelation) {
        this.latestCancellation = latestCancelation;
    }

    public boolean isReservationAutoApproved() {
        return isReservationAutoApproved;
    }

    public void setReservationAutoApproved(boolean reservationAutoApproved) {
        isReservationAutoApproved = reservationAutoApproved;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        // Write id
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(id);
        }

        // Write status
        if (status == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeString(status.name());
        }

        // Write name
        dest.writeString(name);

        // Write description
        dest.writeString(description);

        // Write price
        if (price == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(price);
        }

        // Write sale
        if (sale == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(sale);
        }

        // Write photos
        dest.writeStringList(photos);

        // Write isVisible
        dest.writeByte((byte) (isVisible == null ? 0 : isVisible ? 1 : 2));

        // Write isAvailable
        dest.writeByte((byte) (isAvailable == null ? 0 : isAvailable ? 1 : 2));

        // Write isDeleted
        dest.writeByte((byte) (isDeleted == null ? 0 : isDeleted ? 1 : 2));

        // Write category
        dest.writeString(category);

        // Write eventTypes
        dest.writeTypedList(eventTypes);

        // Write type
        dest.writeString(type);

        // Write specifics
        dest.writeString(specifics);

        // Write durations
        dest.writeInt(minDuration);
        dest.writeInt(maxDuration);
        dest.writeInt(preciseDuration);

        // Write latestReservation
        dest.writeInt(latestReservation);

        // Write latestCancelation
        dest.writeInt(latestCancellation);

        // Write isReservationAutoApproved
        dest.writeByte((byte) (isReservationAutoApproved ? 1 : 0));
    }

    public static final Creator<Offer> CREATOR = new Creator<Offer>() {
        @Override
        public Offer createFromParcel(Parcel in) {
            return new Offer(in);
        }

        @Override
        public Offer[] newArray(int size) {
            return new Offer[size];
        }
    };
}