package com.example.readtrack.repository.user;

import static com.example.readtrack.util.Constants.FAVOURITES_BOOKS;
import static com.example.readtrack.util.Constants.READING_BOOKS;
import static com.example.readtrack.util.Constants.RED_BOOKS;
import static com.example.readtrack.util.Constants.WANT_TO_READ;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.readtrack.model.Result;
import com.example.readtrack.model.User;
import com.example.readtrack.source.user.BaseUserAuthenticationRemoteDataSource;
import com.example.readtrack.source.user.BaseUserDataRemoteDataSource;
import com.example.readtrack.util.OnFavouriteCheckListener;

import java.util.HashMap;
import java.util.Set;

public class UserRepository implements IUserRepository, UserResponseCallback {

    private static final String TAG = UserRepository.class.getSimpleName();

    private final BaseUserAuthenticationRemoteDataSource userAuthRemoteDataSource;
    private final BaseUserDataRemoteDataSource userDataRemoteDataSource;
    private final MutableLiveData<Result> userMutableLiveData;
    private MutableLiveData<Result> favBooksListLiveData;
    private MutableLiveData<Result> readingBooksLiveData;

    private MutableLiveData<Result> finishedBooksLiveData;

    private MutableLiveData<Result> startBooksLiveData;
    private MutableLiveData<Result> segnalibroLiveData;

    public UserRepository(BaseUserAuthenticationRemoteDataSource userRemoteDataSource,
                          BaseUserDataRemoteDataSource userDataRemoteDataSource) {
        this.userAuthRemoteDataSource = userRemoteDataSource;
        this.userDataRemoteDataSource = userDataRemoteDataSource;
        this.userMutableLiveData = new MutableLiveData<>();
        this.favBooksListLiveData =new MutableLiveData<>();
        this.readingBooksLiveData = new MutableLiveData<>();
        this.finishedBooksLiveData = new MutableLiveData<>();
        this.startBooksLiveData = new MutableLiveData<>();
        this.segnalibroLiveData = new MutableLiveData<>();
        this.userAuthRemoteDataSource.setUserResponseCallback(this);
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
    public MutableLiveData<Result> getUserFavBooks(String idToken){
        userDataRemoteDataSource.getUserFavBooks(idToken);
        return favBooksListLiveData;
    }

    @Override
    public MutableLiveData<Result> getUserReadingBooks(String idToken){
        userDataRemoteDataSource.getUserReadingBooks(idToken);
        return readingBooksLiveData;
    }

    @Override
    public MutableLiveData<Result> getUserFinishedBooks(String idToken){
        userDataRemoteDataSource.getUserFinishedBooks(idToken);
        return finishedBooksLiveData;
    }

    @Override
    public MutableLiveData<Result> getUserStartBooks(String idToken){
        userDataRemoteDataSource.getUserStartBooks(idToken);
        return startBooksLiveData;
    }
    @Override
    public MutableLiveData<Result> getSegnalibro(String idBook, String idToken){
        userDataRemoteDataSource.getSegnalibro(idBook, idToken);
        return segnalibroLiveData;
    }

    @Override
    public MutableLiveData<Result> getUserPreferences(String idToken) {
        return null;
    }

    @Override
    public User getLoggedUser() {
        return userAuthRemoteDataSource.getLoggedUser();
    }

    @Override
    public MutableLiveData<Result> logout() {
        userAuthRemoteDataSource.logout();
        return userMutableLiveData;
    }

    @Override
    public void signUp(String email, String password) {
        userAuthRemoteDataSource.signUp(email, password);
    }

    @Override
    public void signIn(String email, String password) {
        userAuthRemoteDataSource.signIn(email, password);
    }

    @Override
    public void signInWithGoogle(String token) {
        userAuthRemoteDataSource.signInWithGoogle(token);
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
    public void onSuccessFromRemoteDatabase(HashMap<String,String> booksList, String path) {
        Result.BooksResponseSuccess result = new Result.BooksResponseSuccess(booksList);
        switch (path) {
            case FAVOURITES_BOOKS:
                favBooksListLiveData.postValue(result);
                break;
            case READING_BOOKS:
                readingBooksLiveData.postValue(result);
                break;

            case RED_BOOKS:
                finishedBooksLiveData.postValue(result);
                break;

            case WANT_TO_READ:
                startBooksLiveData.postValue(result);
                break;
        }
    }

    @Override
    public void onSuccessFromRemoteBookReading(HashMap<String, String> booksList) {
        Result.BooksResponseSuccess result = new Result.BooksResponseSuccess(booksList);
        segnalibroLiveData.postValue(result);
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
        userAuthRemoteDataSource.resetPassword(email);
    }
    @Override
    public void updateReadingBook(String idBook, int page, String linkImg, String idToken){
        userDataRemoteDataSource.updateReadingBook(idBook,page,linkImg,idToken);
    }

}






