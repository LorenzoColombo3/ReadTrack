package com.example.readtrack.ui.welcome;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.readtrack.model.Books;
import com.example.readtrack.model.Result;
import com.example.readtrack.model.User;
import com.example.readtrack.repository.user.IUserRepository;
import com.example.readtrack.util.OnFavouriteCheckListener;

import java.util.List;
import java.util.Set;

public class UserViewModel extends ViewModel {
    private static final String TAG = UserViewModel.class.getSimpleName();

    private final IUserRepository userRepository;
    private MutableLiveData<Result> userMutableLiveData;
    private MutableLiveData<Result> userPreferencesMutableLiveData;
    private MutableLiveData<Result> favoriteBooksLiveData;
    private boolean authenticationError;

    public UserViewModel(IUserRepository userRepository) {
        this.favoriteBooksLiveData= new MutableLiveData<>();
        this.userRepository = userRepository;
        authenticationError = false;
    }

    public MutableLiveData<Result> getUserMutableLiveData(
            String email, String password, boolean isUserRegistered) {
        if (userMutableLiveData == null) {
            Log.d("mutableNull","");
            getUserData(email, password, isUserRegistered);
        }
        return userMutableLiveData;
    }

    public MutableLiveData<Result> getGoogleUserMutableLiveData(String token) {
        if (userMutableLiveData == null) {
            getUserData(token);
        }
        return userMutableLiveData;
    }

    public MutableLiveData<Result> getUserFavoriteBooksMutableLiveData(String idToken) {
        favoriteBooksLiveData = userRepository.getUserFavoriteBooks(idToken);
        return favoriteBooksLiveData;
    }

    public void saveUserPreferences(String favoriteCountry, Set<String> favoriteTopics, String idToken) {
        if (idToken != null) {
            userRepository.saveUserPreferences(favoriteCountry, favoriteTopics, idToken);
        }
    }

    public MutableLiveData<Result> getUserPreferences(String idToken) {
        if (idToken != null) {
            userPreferencesMutableLiveData = userRepository.getUserPreferences(idToken);
        }
        return userPreferencesMutableLiveData;
    }

    public User getLoggedUser() {
        return userRepository.getLoggedUser();
    }

    public MutableLiveData<Result> logout() {
        if (userMutableLiveData == null) {
            userMutableLiveData = userRepository.logout();
        } else {
            userRepository.logout();
        }

        return userMutableLiveData;
    }

    public void getUser(String email, String password, boolean isUserRegistered) {
        userRepository.getUser(email, password, isUserRegistered);
    }

    public boolean isAuthenticationError() {
        return authenticationError;
    }

    public void setAuthenticationError(boolean authenticationError) {
        this.authenticationError = authenticationError;
    }

    private void getUserData(String email, String password, boolean isUserRegistered) {
        userMutableLiveData = userRepository.getUser(email, password, isUserRegistered);
    }

    private void getUserData(String token) {
        userMutableLiveData = userRepository.getGoogleUser(token);
    }

    public void isFavouriteBook(String idBook, String idToken, OnFavouriteCheckListener listener) {
        userRepository.isFavouriteBook(idBook,idToken,listener);
    }

    public void removeFavouriteBook(String idBook, String idToken){
        userRepository.removeFavouriteBook(idBook,idToken);
    }
    public void addFavouriteBook(String idBook, String imageLink, String idToken){
        userRepository.addFavouriteBook(idBook, imageLink, idToken);
    }

}
