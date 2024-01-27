package com.example.readtrack.source;

public abstract class BaseBooksSource {

    protected BooksCallback booksCallback;

    public void setBooksCallback(BooksCallback booksCallback) {
        this.booksCallback = booksCallback;
    }

    public abstract void searchBooks(String query, int page);

    public abstract void searchBooksById(String id);
}
