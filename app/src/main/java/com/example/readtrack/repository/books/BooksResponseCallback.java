package com.example.readtrack.repository.books;

import android.util.Log;

import com.example.readtrack.model.Books;
import com.example.readtrack.model.BooksApiResponse;
import com.example.readtrack.model.BooksResponse;
import com.example.readtrack.model.Result;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface BooksResponseCallback {
    void onSuccessFromRemote(BooksApiResponse booksApiResponse);

    void onSuccessFromRemoteId(BooksApiResponse booksApiResponse);
    void onFailureFromRemote(Exception exception);
    void onSuccessFromRemoteDatabase(List<Books> booksList, String path);
    void onSuccessFromRemoteMarkReading(List<Books> booksList);

    void onSuccessFromLocal(BooksResponse booksResponse);

    void onBookUpdated();

    void onFailureFromLocal(Exception exception);

    void onSuccessFromRemoteWriting(List<Books> booksList);

    void onSuccessFromDeletion(Books books);
}
