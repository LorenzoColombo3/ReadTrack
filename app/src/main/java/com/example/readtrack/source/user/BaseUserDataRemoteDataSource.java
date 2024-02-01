package com.example.readtrack.source.user;

import com.example.readtrack.model.User;
import com.example.readtrack.repository.user.UserResponseCallback;
import com.example.readtrack.util.OnFavouriteCheckListener;

public abstract class BaseUserDataRemoteDataSource {
    protected UserResponseCallback userResponseCallback;

    public void setUserResponseCallback(UserResponseCallback userResponseCallback) {
        this.userResponseCallback = userResponseCallback;
    }
    public abstract void saveUserData(User user);


}
