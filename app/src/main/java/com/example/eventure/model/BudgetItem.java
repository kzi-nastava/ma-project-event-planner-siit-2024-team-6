package com.example.eventure.model;

import com.example.eventure.dto.BudgetItemDTO;

public class BudgetItem {
    private int id;
    private String category;
    private int maxPrice;
    private int currPrice;

    public BudgetItem(){}

    public BudgetItem(BudgetItemDTO dto){
        this.id = dto.getId();
        this.category = dto.getCategory();
        this.currPrice = dto.getCurrPrice();
        this.maxPrice = dto.getMaxPrice();
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
