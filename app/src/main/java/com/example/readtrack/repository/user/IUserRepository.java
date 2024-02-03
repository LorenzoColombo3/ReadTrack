package com.example.readtrack.repository.user;

import androidx.lifecycle.MutableLiveData;

import com.example.readtrack.model.Result;
import com.example.readtrack.model.User;

public interface IUserRepository {
    MutableLiveData<Result> getUser(String email, String password, boolean isUserRegistered);
    MutableLiveData<Result> getGoogleUser(String idToken);
    MutableLiveData<Result> logout();
    User getLoggedUser();
    void signUp(String email, String password);
    void signIn(String email, String password);
    void signInWithGoogle(String token);
    void resetPassword(String email);


}
