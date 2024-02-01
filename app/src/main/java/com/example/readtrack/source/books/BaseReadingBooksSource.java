package com.example.readtrack.source.books;

import com.example.readtrack.model.User;
import com.example.readtrack.repository.books.BooksResponseCallback;
import com.example.readtrack.util.OnFavouriteCheckListener;

public abstract class BaseReadingBooksSource {

    protected BooksResponseCallback booksResponseCallback;

    public void setBooksCallback(BooksResponseCallback booksResponseCallback) {
        this.booksResponseCallback = booksResponseCallback;
    }
    public abstract void getUserReadingBooks(String idToken);
    public abstract void removeReadingBook(String idBook, String idToken);
    public abstract void updateReadingBook(String idBook, int page, String imageLink, String idToken);
    public abstract void getSegnalibro(String idBook, String idToken);
}
