package com.example.readtrack.repository.user;

import androidx.lifecycle.MutableLiveData;

import com.example.readtrack.model.Result;
import com.example.readtrack.model.User;
import com.example.readtrack.util.OnFavouriteCheckListener;

import java.util.HashMap;
import java.util.Set;

public interface IUserRepository {
    MutableLiveData<Result> getUser(String email, String password, boolean isUserRegistered);
    MutableLiveData<Result> getGoogleUser(String idToken);
    MutableLiveData<Result> getUserFavBooks(String idToken);

    MutableLiveData<Result> getSegnalibro(String idBook, String idToken);
    MutableLiveData<Result> getUserPreferences(String idToken);
    MutableLiveData<Result> logout();
    User getLoggedUser();
    void signUp(String email, String password);
    void signIn(String email, String password);
    void signInWithGoogle(String token);
    void saveUserPreferences(String favoriteCountry, Set<String> favoriteTopics, String idToken);
    void isFavouriteBook(String idBook, String idToken, OnFavouriteCheckListener listener);
    void removeFavouriteBook(String idBook, String idToken);
    void addFavouriteBook(String idBook, String imageLink, String idToken);
    void resetPassword(String email);
    void updateReadingBook(String idBook, int page, String linkImg, String idToken);
    void onSuccessFromRemoteDatabase(HashMap<String,String> booksList);

    void onSuccessFromRemoteBookReading(HashMap<String,String> booksList);


}
