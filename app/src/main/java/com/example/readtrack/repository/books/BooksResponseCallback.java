package com.example.readtrack.repository.books;

import com.example.readtrack.model.Books;
import com.example.readtrack.model.BooksApiResponse;

import java.util.HashMap;
import java.util.List;

public interface BooksResponseCallback {
    void onSuccessFromRemote(BooksApiResponse booksApiResponse);

    void onSuccessFromRemoteId(BooksApiResponse booksApiResponse);
    void onFailureFromRemote(Exception exception);
    void onSuccessFromRemoteDatabase(HashMap<String,String> BooksList, String path);
    void onSuccessFromRemoteMarkReading(HashMap<String,String> BooksList);
}
