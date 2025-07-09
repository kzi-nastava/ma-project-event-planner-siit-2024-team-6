package com.example.eventure.dto;

import com.example.eventure.model.BudgetItem;

public class BudgetItemDTO {
    int id;
    int maxPrice;
    int currPrice;
    String category;

    public BudgetItemDTO(BudgetItem bi){
        this.id = bi.getId();
        this.category = bi.getCategory();
        this.currPrice = bi.getCurrPrice();
        this.maxPrice = bi.getMaxPrice();
    }

    public int getId(){return id;}

    public int getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(int maxPrice) {
        this.maxPrice = maxPrice;
    }

    public int getCurrPrice() {
        return currPrice;
    }

    public void setCurrPrice(int currPrice) {
        this.currPrice = currPrice;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
