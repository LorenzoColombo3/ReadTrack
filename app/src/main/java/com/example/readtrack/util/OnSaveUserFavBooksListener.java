package com.example.readtrack.util;

public interface OnSaveUserFavBooksListener {
    void onSaveSuccess(boolean isAdded);
    void onSaveFailure(String errorMessage);
}
