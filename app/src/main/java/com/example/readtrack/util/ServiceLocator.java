package com.example.readtrack.util;

import android.app.Application;

import com.example.readtrack.database.BookRoomDatabase;
import com.example.readtrack.service.BookApiService;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceLocator {
    private static volatile ServiceLocator INSTANCE = null;

    private ServiceLocator() {}

    public static ServiceLocator getInstance() {
        if (INSTANCE == null) {
            synchronized(ServiceLocator.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ServiceLocator();
                }
            }
        }
        return INSTANCE;
    }


    public BookApiService getBookApiService() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.BOOKS_API_BASE_URL).
                addConverterFactory(GsonConverterFactory.create()).build();
        return retrofit.create(BookApiService.class);
    }

    public BookRoomDatabase getNewsDao(Application application) {
        return BookRoomDatabase.getDatabase(application);
    }
}
