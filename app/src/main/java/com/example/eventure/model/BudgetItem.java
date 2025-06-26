package com.example.eventure.model;

public class BudgetItem {
    private String catgeory;
    private int maxPrice;
    private int currPrice;

    public BudgetItem(int max, int curr, String c){
        this.catgeory = c;
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

    public String getCatgeory() {
        return catgeory;
    }

    public void setCatgeory(String catgeory) {
        this.catgeory = catgeory;
    }
}
