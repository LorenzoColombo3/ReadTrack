package com.example.readtrack.repository;

import static com.example.readtrack.util.Constants.NEWS_API_TEST_JSON_FILE;

import android.app.Application;

import com.example.readtrack.model.Book;
import com.example.readtrack.model.BooksApiResponse;
import com.example.readtrack.util.ResponseCallback;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class BooksRepository implements IBookRepository{
    private final Application application;
    private final ResponseCallback responseCallback;

    public BooksRepository(Application application, ResponseCallback responseCallback) {
        this.application = application;
        this.responseCallback = responseCallback;
    }

    @Override
    public void fetchBooks(String author, String title, int isbn) {
        BooksApiResponse booksApiResponse=null;
        try {
            InputStream inputStream = application.getApplicationContext().getAssets().open(NEWS_API_TEST_JSON_FILE);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            booksApiResponse= new Gson().fromJson(bufferedReader, BooksApiResponse.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void getFavouriteBooks() {

    }

    @Override
    public void deleteFavouriteBooks() {

    }
}
