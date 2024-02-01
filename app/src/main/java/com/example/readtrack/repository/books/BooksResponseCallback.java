package com.example.readtrack.repository.books;

import android.util.Log;

import com.example.readtrack.model.Books;
import com.example.readtrack.model.BooksApiResponse;
import com.example.readtrack.model.Result;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface BooksResponseCallback {
    void onSuccessFromRemote(BooksApiResponse booksApiResponse);

    void onSuccessFromRemoteId(BooksApiResponse booksApiResponse);
    void onFailureFromRemote(Exception exception);
    void onSuccessFromRemoteDatabase(HashMap<String,String> BooksList, String path);
    void onSuccessFromRemoteMarkReading(HashMap<String,String> BooksList);
    void onSuccessFromRemoteTitleReading(HashMap<String, String> booksList);
    void onSuccessFromRemoteNumPagesReading(HashMap<String, String> booksList);
    void onSuccessFromRemoteReadingBooks(ArrayList<Books> readingBooks);
}
