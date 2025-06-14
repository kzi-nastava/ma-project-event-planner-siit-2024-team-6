package com.example.eventure.model;

import android.os.Parcelable;

import java.io.Serializable;

public class PriceListItem {
    private int offerId;
    private String offerName;
    private double offerPrice;
    private double offerDiscountPrice;
    private boolean isService;

    public PriceListItem(int offerId, String offerName, double offerPrice, double offerDiscountPrice, boolean isService) {
        this.offerId = offerId;
        this.offerName = offerName;
        this.offerPrice = offerPrice;
        this.offerDiscountPrice = offerDiscountPrice;
        this.isService = isService;
    }

    public int getOfferId() { return offerId; }
    public void setOfferId(int offerId) { this.offerId = offerId; }

    public String getOfferName() { return offerName; }
    public void setOfferName(String offerName) { this.offerName = offerName; }

    public double getOfferPrice() { return offerPrice; }
    public void setOfferPrice(double offerPrice) { this.offerPrice = offerPrice; }

    public double getOfferDiscountPrice() { return offerDiscountPrice; }
    public void setOfferDiscountPrice(double offerDiscountPrice) { this.offerDiscountPrice = offerDiscountPrice; }

    public boolean isService() { return isService; }
    public void setService(boolean service) { isService = service; }

}
