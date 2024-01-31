package com.example.readtrack.repository.user;

import com.example.readtrack.model.Books;
import com.example.readtrack.model.User;

import java.util.HashMap;
import java.util.List;

public interface UserResponseCallback {
    void onSuccessFromAuthentication(User user);
    void onFailureFromAuthentication(String message);
    void onSuccessFromRemoteDatabase(User user);
    void onSuccessFromRemoteDatabase(HashMap<String,String> BooksList);

    void onSuccessFromRemoteBookReading(HashMap<String,String> BooksList);
    void onFailureFromRemoteDatabase(String message);
    void onSuccessLogout();
}
