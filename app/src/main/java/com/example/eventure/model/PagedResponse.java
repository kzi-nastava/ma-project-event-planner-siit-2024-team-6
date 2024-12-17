package com.example.eventure.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PagedResponse<T> {

    @SerializedName("content")
    @Expose
    private List<T> content;

    @SerializedName("totalPages")
    @Expose
    private int totalPages;

    @SerializedName("totalElements")
    @Expose
    private int totalElements;

    @SerializedName("last")
    @Expose
    private boolean last;

    @SerializedName("first")
    @Expose
    private boolean first;

    @SerializedName("size")
    @Expose
    private int size;

    @SerializedName("number")
    @Expose
    private int number;

    @SerializedName("numberOfElements")
    @Expose
    private int numberOfElements;

    // Getters and Setters

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(int totalElements) {
        this.totalElements = totalElements;
    }

    public boolean isLast() {
        return last;
    }

    public void setLast(boolean last) {
        this.last = last;
    }

    public boolean isFirst() {
        return first;
    }

    public void setFirst(boolean first) {
        this.first = first;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getNumberOfElements() {
        return numberOfElements;
    }

    public void setNumberOfElements(int numberOfElements) {
        this.numberOfElements = numberOfElements;
    }
}
