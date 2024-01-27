package com.example.readtrack.model;

import android.util.Log;

import java.util.List;

//TODO deve estentedere bookResponse
public class BooksApiResponse{
    private String kind;
    private int totalItems;
    private List<Books> items;

    public BooksApiResponse(int totalItems, String kind, List<Books> items){
        this.kind=kind;
        this.totalItems=totalItems;
        this.items=items;
    }
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
