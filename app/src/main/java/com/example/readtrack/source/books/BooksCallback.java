package com.example.readtrack.source.books;

import com.example.readtrack.model.Books;
import com.example.readtrack.model.BooksApiResponse;

import java.util.List;

public interface BooksCallback {
    void onSuccessFromRemote(BooksApiResponse booksApiResponse);

    void onSuccessFromRemoteId(BooksApiResponse booksApiResponse);
    void onFailureFromRemote(Exception exception);
    void onBooksFavoriteStatusChanged(List<Books> books);
    void onDeleteFavoriteBooksSuccess(List<Books> favoriteBooks);
}
