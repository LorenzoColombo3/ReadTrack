package com.example.readtrack.model;

import java.util.List;

public class BooksApiResponse{
    private String kind;
    private int totalItems;
    private List<Books> items;


    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    public List<Books> getItems() {
        return items;
    }

    public void setItems(List<Books> items) {
        this.items = items;
    }

}
