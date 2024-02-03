package com.example.readtrack.source.books;

import com.example.readtrack.repository.books.BooksResponseCallback;
import com.example.readtrack.util.OnCheckListener;

public abstract class BaseSavedBooksSource {

    protected BooksResponseCallback booksResponseCallback;

    public void setBooksCallback(BooksResponseCallback booksResponseCallback) {
        this.booksResponseCallback = booksResponseCallback;
    }
    public abstract void getUserSavedBooks(String idToken);

    public abstract void isSavedBook(String idBook, String idToken, OnCheckListener listener);

    public abstract void removeSavedBook(String idBook, String idToken);
    public abstract void addSavedBook(String idBook, String imageLink, String idToken);
}
