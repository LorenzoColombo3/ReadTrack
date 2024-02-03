package com.example.readtrack.source.books;

import com.example.readtrack.repository.books.BooksResponseCallback;
import com.example.readtrack.util.OnCheckListener;


public abstract class BaseFavoriteBooksSource {

    protected BooksResponseCallback booksResponseCallback;

    public void setBooksCallback(BooksResponseCallback booksResponseCallback) {
        this.booksResponseCallback = booksResponseCallback;
    }
    public abstract void getUserFavBooks(String idToken);

    public abstract void isFavouriteBook(String idBook, String idToken, OnCheckListener listener);

    public abstract void removeFavouriteBook(String idBook, String idToken);
    public abstract void addFavouriteBook(String idBook, String imageLink, String idToken);
}
