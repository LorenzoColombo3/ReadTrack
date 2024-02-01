package com.example.readtrack.ui.welcome;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.readtrack.model.Result;
import com.example.readtrack.model.User;
import com.example.readtrack.repository.user.IUserRepository;
import com.example.readtrack.util.OnFavouriteCheckListener;

import java.util.Set;

public class UserViewModel extends ViewModel {
    private static final String TAG = UserViewModel.class.getSimpleName();

    private final IUserRepository userRepository;
    private MutableLiveData<Result> userMutableLiveData;
    private MutableLiveData<Result> userPreferencesMutableLiveData;
    private MutableLiveData<Result> favBooksLiveData;

    private MutableLiveData<Result> readingBooksLiveData;

    private MutableLiveData<Result> finishedBooksLiveData;

    private MutableLiveData<Result> startBooksLiveData;
    private MutableLiveData<Result> segnalibroLiveData;
    private boolean authenticationError;

    public UserViewModel(IUserRepository userRepository) {
        this.favBooksLiveData = new MutableLiveData<>();
        this.readingBooksLiveData = new MutableLiveData<>();
        this.finishedBooksLiveData = new MutableLiveData<>();
        this.startBooksLiveData = new MutableLiveData<>();
        this.segnalibroLiveData = new MutableLiveData<>();
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

    public MutableLiveData<Result> getSegnalibro(String idBook, String idToken){
        segnalibroLiveData=userRepository.getSegnalibro(idBook, idToken);
        return segnalibroLiveData;
    }

    public MutableLiveData<Result> getGoogleUserMutableLiveData(String token) {
        if (userMutableLiveData == null) {
            getUserData(token);
        }
        return userMutableLiveData;
    }

    public MutableLiveData<Result> getFavBooksMutableLiveData(String idToken) {
        favBooksLiveData = userRepository.getUserFavBooks(idToken);
        return favBooksLiveData;
    }

    public MutableLiveData<Result> getReadingBooksMutableLiveData(String idToken) {
        readingBooksLiveData = userRepository.getUserReadingBooks(idToken);
        return readingBooksLiveData;
    }

    public MutableLiveData<Result> getStartBooksMutableLiveData(String idToken) {
        startBooksLiveData = userRepository.getUserStartBooks(idToken);
        return startBooksLiveData;
    }

    public MutableLiveData<Result> getFinishedBooksMutableLiveData(String idToken) {
        finishedBooksLiveData = userRepository.getUserFinishedBooks(idToken);
        return finishedBooksLiveData;
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

    public void resetPassword(String email){
        userRepository.resetPassword(email);
    }

    public void updateReadingBooks(String idBook, int page, String linkImg, String idToken){
        userRepository.updateReadingBook(idBook,page,linkImg,idToken);
    }

}
