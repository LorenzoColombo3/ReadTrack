package com.example.readtrack.source.books;

import com.example.readtrack.model.Book;
import com.example.readtrack.model.BooksResponse;
import com.example.readtrack.repository.books.BooksResponseCallback;

public abstract class BaseBooksLocalDataSource {
    protected BooksResponseCallback booksCallback;

    public void setBooksCallback(BooksResponseCallback booksCallback) {
        this.booksCallback = booksCallback;
    }

    public abstract void getBooks();
    public abstract void delete(Book book);
    public abstract void insertBook(BooksResponse booksResponse);

}
