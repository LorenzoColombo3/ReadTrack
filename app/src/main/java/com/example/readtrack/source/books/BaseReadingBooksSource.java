package com.example.readtrack.source.books;

import com.example.readtrack.repository.books.BooksResponseCallback;

public abstract class BaseReadingBooksSource {

    protected BooksResponseCallback booksResponseCallback;

    public void setBooksCallback(BooksResponseCallback booksResponseCallback) {
        this.booksResponseCallback = booksResponseCallback;
    }
    public abstract void getUserReadingBooksTumbnail(String idToken);
    public abstract void removeReadingBook(String idBook, String idToken);
    public abstract void updateReadingBook(String idBook, int page, String imageLink, String title, int numPages, String idToken);
    public abstract void getSegnalibro(String idBook, String idToken);
    public abstract void getNumPages(String idBook, String idToken);
    public abstract void getTitle(String idBook, String idToken);
    public abstract void getUserReadingBooks(String idToken);
}
