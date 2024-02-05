package com.example.readtrack.repository.books;

import com.example.readtrack.model.Book;
import com.example.readtrack.model.BooksApiResponse;
import com.example.readtrack.model.BooksResponse;

import java.util.List;

public interface BooksResponseCallback {
    void onSuccessFromRemote(BooksApiResponse booksApiResponse);

    void onSuccessFromRemoteId(BooksApiResponse booksApiResponse);
    void onFailureFromRemote(Exception exception);
    void onSuccessFromRemoteDatabase(List<Book> bookList, String path);
    void onSuccessFromRemoteMarkReading(List<Book> bookList);

    void onSuccessFromLocal(BooksResponse booksResponse);


    void onSuccessFromRemoteWriting(List<Book> bookList);

    void onSuccessFromDeletion(Book book);
}
