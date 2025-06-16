package com.example.eventure.dto;

public class NewPriceListItemDTO {
    Double price;
    Double salePrice;
    public NewPriceListItemDTO(double price, double salePrice){
        this.salePrice = salePrice;
        this.price = price;
    }
}
