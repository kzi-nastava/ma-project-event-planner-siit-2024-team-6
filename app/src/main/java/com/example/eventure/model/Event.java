package com.example.eventure.model;

import android.app.Activity;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.example.eventure.dto.EventDTO;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Event implements Parcelable, Serializable {

    private Integer id;
    private String name;
    private String description;
    private Integer maxParticipants;
    private Integer participants;
    private Boolean isPublic;
    private String place;
    private LocalDateTime date;
    private Double rating;
    private List<String> photos;
    private EventType eventType;
    private List<Activity> eventActivities;
    //private Budget budget;
    //private List<Product> products;
    private Boolean isDeleted;

    public Event() {}

    public Event(
            Integer id,
            String name,
            String description,
            Integer maxParticipants,
            Integer participants,
            Boolean isPublic,
            String place,
            LocalDateTime date,
            EventType eventType,
            List<Activity> activities
            //,Budget budget,
            //List<Product> products
            ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.maxParticipants = maxParticipants;
        this.participants = participants;
        this.isPublic = isPublic;
        this.place = place;
        this.date = date;
        this.eventType = eventType;
        this.eventActivities = activities;
        //this.budget = budget;
        //this.products = products;
        this.rating = 0.0;
    }

    public Event(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        name = in.readString();
        description = in.readString();
        if (in.readByte() == 0) {
            maxParticipants = null;
        } else {
            maxParticipants = in.readInt();
        }
        if (in.readByte() == 0) {
            participants = null;
        } else {
            participants = in.readInt();
        }
        byte tmpIsPublic = in.readByte();
        isPublic = tmpIsPublic == 0 ? null : tmpIsPublic == 1;
        place = in.readString();
        if (in.readByte() == 0) {
            rating = null;
        } else {
            rating = in.readDouble();
        }
        photos = in.createStringArrayList();
        eventType = in.readParcelable(EventType.class.getClassLoader());
        byte tmpIsDeleted = in.readByte();
        isDeleted = tmpIsDeleted == 0 ? null : tmpIsDeleted == 1;
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    public Event(EventDTO dto) {
        this.id = dto.getId();
        this.name = dto.getName();
        this.description = dto.getDescription();
        this.maxParticipants = dto.getMaxParticipants();
        this.participants = dto.getParticipants();
        this.isPublic = dto.getPublic();
        this.place = dto.getPlace();
        this.date = dto.getDate();
        this.photos = dto.getPhotos();
        this.isDeleted = dto.getDeleted();
        this.rating = dto.getRating();

        if (dto.getEventType() != null) {
            this.eventType = new EventType(dto.getEventType());
        }
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Integer getMaxParticipants() {
        return maxParticipants;
    }

    public Integer getParticipants() {
        return participants;
    }

    public Boolean getPublic() {
        return isPublic;
    }

    public String getPlace() {
        return place;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public Double getRating() {
        return rating;
    }

    public List<String> getPhotos() {
        return photos;
    }

    public EventType getEventType() {
        return eventType;
    }

    public List<Activity> getEventActivities() {
        return eventActivities;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Event event = (Event) obj;

        return name != null && name.equals(event.name);
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(id);
        }
        dest.writeString(name);
        dest.writeString(description);
        if (maxParticipants == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(maxParticipants);
        }
        if (participants == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(participants);
        }
        dest.writeByte((byte) (isPublic == null ? 0 : isPublic ? 1 : 2));
        dest.writeString(place);
        if (rating == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(rating);
        }
        dest.writeStringList(photos);
        dest.writeParcelable(eventType, flags);
        dest.writeByte((byte) (isDeleted == null ? 0 : isDeleted ? 1 : 2));
    }
}
