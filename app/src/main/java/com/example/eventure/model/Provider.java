package com.example.eventure.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.eventure.dto.ProviderDTO;
import com.example.eventure.dto.UserDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Arrays;

public class Provider extends User implements Parcelable, Serializable{
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("companyEmail")
    @Expose
    private String companyEmail;

    @SerializedName("companyName")
    @Expose
    private String companyName;

    @SerializedName("companyAddress")
    @Expose
    private String companyAddress;

    @SerializedName("companyPhotos")
    @Expose
    private String[] companyPhotos;

    @SerializedName("openingTime")
    @Expose
    private String openingTime;

    @SerializedName("closingTime")
    @Expose
    private String closingTime;

    // Default constructor
    public Provider() {
    }

    public Provider(ProviderDTO dto){
        this.id = dto.getId();
        this.description = dto.getDescription();
        this.companyEmail = dto.getCompanyEmail();
        this.companyName = dto.getCompanyName();
        this.companyAddress = dto.getCompanyAddress();
        this.companyPhotos = dto.getCompanyPhotos();
        this.openingTime = dto.getOpeningTime();
        this.closingTime = dto.getClosingTime();
    }

    public Provider(UserDTO dto){
        this.id = dto.getId();
        this.description = dto.getDescription();
        this.companyEmail = dto.getCompanyEmail();
        this.companyName = dto.getCompanyName();
        this.companyAddress = dto.getCompanyAddress();
        this.companyPhotos = dto.getCompanyPhotos();
        this.openingTime = dto.getOpeningTime();
        this.closingTime = dto.getClosingTime();
    }

    // Full constructor
    public Provider(int id, String description, String companyEmail, String companyName,
                    String companyAddress, String[] companyPhotos,
                    String openingTime, String closingTime) {
        this.id = id;
        this.description = description;
        this.companyEmail = companyEmail;
        this.companyName = companyName;
        this.companyAddress = companyAddress;
        this.companyPhotos = companyPhotos;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String[] getCompanyPhotos() {
        return companyPhotos;
    }

    public void setCompanyPhotos(String[] companyPhotos) {
        this.companyPhotos = companyPhotos;
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

    // Parcelable implementation
    protected Provider(Parcel in) {
        id = in.readInt();
        description = in.readString();
        companyEmail = in.readString();
        companyName = in.readString();
        companyAddress = in.readString();
        companyPhotos = in.createStringArray();
        openingTime = in.readString();
        closingTime = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(description);
        dest.writeString(companyEmail);
        dest.writeString(companyName);
        dest.writeString(companyAddress);
        dest.writeStringArray(companyPhotos);
        dest.writeString(openingTime);
        dest.writeString(closingTime);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Provider> CREATOR = new Creator<Provider>() {
        @Override
        public Provider createFromParcel(Parcel in) {
            return new Provider(in);
        }

        @Override
        public Provider[] newArray(int size) {
            return new Provider[size];
        }
    };

    @Override
    public String toString() {
        return "Provider{" +
                "description='" + description + '\'' +
                ", companyEmail='" + companyEmail + '\'' +
                ", companyName='" + companyName + '\'' +
                ", companyAddress='" + companyAddress + '\'' +
                ", companyPhotos=" + Arrays.toString(companyPhotos) +
                ", openingTime='" + openingTime + '\'' +
                ", closingTime='" + closingTime + '\'' +
                '}';
    }
}
