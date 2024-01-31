package com.example.readtrack.repository.user;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.readtrack.model.Books;
import com.example.readtrack.model.BooksApiResponse;
import com.example.readtrack.model.Result;
import com.example.readtrack.model.User;
import com.example.readtrack.source.books.BooksCallback;
import com.example.readtrack.source.user.BaseUserAuthenticationRemoteDataSource;
import com.example.readtrack.source.user.BaseUserDataRemoteDataSource;
import com.example.readtrack.util.OnFavouriteCheckListener;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class UserRepository implements IUserRepository, UserResponseCallback, BooksCallback {

    private static final String TAG = UserRepository.class.getSimpleName();

    private final BaseUserAuthenticationRemoteDataSource userRemoteDataSource;
    private final BaseUserDataRemoteDataSource userDataRemoteDataSource;
    private final MutableLiveData<Result> userMutableLiveData;
    private final MutableLiveData<Result> userFavoriteNewsMutableLiveData;
    private final MutableLiveData<Result> userPreferencesMutableLiveData;
    private MutableLiveData<Result> favoriteBooksLiveData;

    public UserRepository(BaseUserAuthenticationRemoteDataSource userRemoteDataSource,
                          BaseUserDataRemoteDataSource userDataRemoteDataSource) {
        this.userRemoteDataSource = userRemoteDataSource;
        this.userDataRemoteDataSource = userDataRemoteDataSource;
        this.userMutableLiveData = new MutableLiveData<>();
        this.favoriteBooksLiveData =new MutableLiveData<>();
        this.userPreferencesMutableLiveData = new MutableLiveData<>();
        this.userFavoriteNewsMutableLiveData = new MutableLiveData<>();
        this.userRemoteDataSource.setUserResponseCallback(this);
        this.userDataRemoteDataSource.setUserResponseCallback(this);
    }


    @Override
    public MutableLiveData<Result> getUser(String email, String password, boolean isUserRegistered) {
        if (isUserRegistered) {
            signIn(email, password);
        } else {
            Log.d("sugnUp","false");
            signUp(email, password);
        }
        return userMutableLiveData;
    }

    @Override
    public MutableLiveData<Result> getGoogleUser(String idToken) {
        signInWithGoogle(idToken);
        return userMutableLiveData;
    }
    @Override
    public MutableLiveData<Result> getUserFavoriteBooks(String idToken){
        userDataRemoteDataSource.getUserFavoriteBooks(idToken);
        return favoriteBooksLiveData;
    }

    @Override
    public MutableLiveData<Result> getUserPreferences(String idToken) {
        return null;
    }

    @Override
    public User getLoggedUser() {
        return userRemoteDataSource.getLoggedUser();
    }

    @Override
    public MutableLiveData<Result> logout() {
        userRemoteDataSource.logout();
        return userMutableLiveData;
    }

    @Override
    public void signUp(String email, String password) {
        userRemoteDataSource.signUp(email, password);
    }

    @Override
    public void signIn(String email, String password) {
        userRemoteDataSource.signIn(email, password);
    }

    @Override
    public void signInWithGoogle(String token) {
        userRemoteDataSource.signInWithGoogle(token);
    }

    @Override
    public void saveUserPreferences(String favoriteCountry, Set<String> favoriteTopics, String idToken) {

    }

    @Override
    public void onSuccessFromAuthentication(User user) {
        if (user != null) {
            userDataRemoteDataSource.saveUserData(user);
        }
    }

    @Override
    public void onFailureFromAuthentication(String message) {
        Result.Error result = new Result.Error(message);
        userMutableLiveData.postValue(result);
    }

    @Override
    public void onSuccessFromRemoteDatabase(User user) {
        Log.d("qui non dovrebbe entrare","user rep");
        Result.UserResponseSuccess result = new Result.UserResponseSuccess(user);
        userMutableLiveData.postValue(result);
    }

    @Override
    public void onSuccessFromRemoteDatabase(HashMap<String,String> booksList) {
        Result.BooksResponseSuccess result = new Result.BooksResponseSuccess(booksList);
        favoriteBooksLiveData.postValue(result);
        Log.d("result", String.valueOf(booksList.size()));
    }

    @Override
    public void onSuccessFromGettingUserPreferences() {
        userPreferencesMutableLiveData.postValue(new Result.UserResponseSuccess(null));
    }

    @Override
    public void onFailureFromRemoteDatabase(String message) {
        Result.Error result = new Result.Error(message);
        userMutableLiveData.postValue(result);
    }

    @Override
    public void onSuccessLogout() {
    }

    @Override
    public void onSuccessFromRemote(BooksApiResponse booksApiResponse) {

    }

    @Override
    public void onSuccessFromRemoteId(BooksApiResponse booksApiResponse) {

    }

    @Override
    public void onFailureFromRemote(Exception exception) {

    }

    @Override
    public void onBooksFavoriteStatusChanged(List<Books> books) {

    }

    @Override
    public void onDeleteFavoriteBooksSuccess(List<Books> favoriteBooks) {

    }
    @Override
    public void isFavouriteBook(String idBook, String idToken, OnFavouriteCheckListener listener){
        userDataRemoteDataSource.isFavouriteBook(idBook,idToken,listener);
    }

    @Override
    public void removeFavouriteBook(String idBook, String idToken) {
        userDataRemoteDataSource.removeFavouriteBook(idBook,idToken);
    }

    @Override
    public void addFavouriteBook(String idBook, String imageLink, String idToken) {
        userDataRemoteDataSource.addFavouriteBook(idBook, imageLink, idToken);
    }
    @Override
    public void resetPassword(String email){
        userRemoteDataSource.resetPassword(email);
    }

}






