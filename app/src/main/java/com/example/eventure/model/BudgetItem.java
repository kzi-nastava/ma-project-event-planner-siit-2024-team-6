package com.example.eventure.model;

public class BudgetItem {
    private String category;
    private int maxPrice;
    private int currPrice;

    public BudgetItem(int max, int curr, String c){
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
