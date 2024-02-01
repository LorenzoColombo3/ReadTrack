package com.example.readtrack.source.books;

import com.example.readtrack.repository.books.BooksResponseCallback;

public abstract class BaseBooksSource {

    protected BooksResponseCallback booksResponseCallback;

    public void setBooksCallback(BooksResponseCallback booksResponseCallback) {
        this.booksResponseCallback = booksResponseCallback;
    }

    public abstract void searchBooks(String query, int page);

    public abstract void searchBooksById(String id);
}
