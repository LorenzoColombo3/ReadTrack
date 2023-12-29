package com.example.readtrack.util;

import com.example.readtrack.model.Books;

import java.util.List;

public interface ResponseCallback {
    void onSuccess(List<Books> bookList, long lastUpdate);
    void onFailure(String errorMessage);
    //void onNewsFavoriteStatusChanged(Books news);
}
