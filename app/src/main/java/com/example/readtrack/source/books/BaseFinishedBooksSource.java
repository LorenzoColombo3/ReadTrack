package com.example.readtrack.source.books;

import com.example.readtrack.repository.books.BooksResponseCallback;
import com.example.readtrack.util.OnFavouriteCheckListener;

public abstract class BaseFinishedBooksSource {

    protected BooksResponseCallback booksResponseCallback;

    public void setBooksCallback(BooksResponseCallback booksResponseCallback) {
        this.booksResponseCallback = booksResponseCallback;
    }

    public abstract void removeUserFinishedBook(String idBook, String idToken);

    public abstract void addUserFinishedBook(String idBook,  String imageLink, String idToken);

    public abstract void getUserFinishedBooks(String idToken);
    public abstract void isFinishedBook(String idBook, String idToken, OnFavouriteCheckListener listener);


}
