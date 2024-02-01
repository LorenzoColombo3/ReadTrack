package com.example.readtrack.source.books;

import com.example.readtrack.repository.books.BooksResponseCallback;

public abstract class BaseFinishedBooksSource {

    protected BooksResponseCallback booksResponseCallback;

    public void setBooksCallback(BooksResponseCallback booksResponseCallback) {
        this.booksResponseCallback = booksResponseCallback;
    }
}
