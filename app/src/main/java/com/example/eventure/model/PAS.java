package com.example.eventure.model;

public class PAS {

    private int photoID;
    private String title;
    private String description;
    private int price;
    private int sale; // New sale field

    // Default constructor
    public PAS() {}

    // Parameterized constructor
    public PAS(int photoID, String title, String description, int price, int sale) {
        this.photoID = photoID;
        this.title = title;
        this.description = description;
        this.price = price;
        this.sale = sale; // Initialize sale
    }

    // Getter and setter for photoID
    public int getPhotoID() {
        return photoID;
    }

    public void setPhotoID(int photoID) {
        this.photoID = photoID;
    }

    // Getter and setter for title
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    // Getter and setter for description
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Getter and setter for price
    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    // Getter and setter for sale
    public int getSale() {
        return sale;
    }

    public void setSale(int sale) {
        this.sale = sale;
    }

    @Override
    public String toString() {
        return "PAS{" +
                "photoID=" + photoID +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", sale=" + sale +
                '}';
    }
}
