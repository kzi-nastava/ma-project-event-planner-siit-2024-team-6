package com.example.eventure.dto;

import com.example.eventure.model.BudgetItem;

public class NewBudgetItemDTO {
    int maxPrice;
    int currPrice;
    String category;

    public NewBudgetItemDTO(String c, int max, int curr){
        this.category = c;
        this.currPrice = curr;
        this.maxPrice = max;
    }

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
