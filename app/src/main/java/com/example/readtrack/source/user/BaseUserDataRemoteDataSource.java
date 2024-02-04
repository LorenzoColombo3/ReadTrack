package com.example.readtrack.source.user;

import android.graphics.Bitmap;

import com.example.readtrack.model.User;
import com.example.readtrack.repository.user.UserResponseCallback;

public abstract class BaseUserDataRemoteDataSource {
    protected UserResponseCallback userResponseCallback;

    public void setUserResponseCallback(UserResponseCallback userResponseCallback) {
        this.userResponseCallback = userResponseCallback;
    }
    public abstract void saveUserData(User user);
    public abstract void saveUserProfileImg(String idToken, Bitmap imgProfile);
    public abstract void getUserProfileImg(String idToken);


}
