package com.example.readtrack.source.books;

import com.example.readtrack.repository.books.BooksResponseCallback;
import com.example.readtrack.util.OnCheckListener;

public abstract class BaseReadingBooksSource {
    protected BooksResponseCallback booksResponseCallback;
    public void setBooksCallback(BooksResponseCallback booksResponseCallback) {
        this.booksResponseCallback = booksResponseCallback;
    }
    public abstract void removeReadingBook(String idBook, String idToken);
    public abstract void updateReadingBook(String idBook, int page, String imageLink, String title, int numPages, String idToken);
    public abstract void getSegnalibro(String idBook, String idToken);
    public abstract void getUserReadingBooks(String idToken);
    public abstract void isReadingBook(String idBook, String idToken, OnCheckListener listener);
}
