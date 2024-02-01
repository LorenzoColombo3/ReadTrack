package com.example.readtrack.repository.books;

import com.example.readtrack.model.Books;
import com.example.readtrack.model.BooksApiResponse;

import java.util.List;

public interface BooksResponseCallback {
    void onSuccessFromRemote(BooksApiResponse booksApiResponse);

    void onSuccessFromRemoteId(BooksApiResponse booksApiResponse);
    void onFailureFromRemote(Exception exception);
    void onBooksFavoriteStatusChanged(List<Books> books);
    void onDeleteFavoriteBooksSuccess(List<Books> favoriteBooks);
}
