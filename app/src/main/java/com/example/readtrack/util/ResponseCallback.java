package com.example.readtrack.util;

import com.example.readtrack.model.Book;

import java.util.List;

public interface ResponseCallback {
    void onSuccess(List<Book> bookList, long lastUpdate);
    void onFailure(String errorMessage);
    //void onNewsFavoriteStatusChanged(Book news);
}
