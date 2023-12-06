package com.example.readtrack.repository;


public interface IBookRepository {
    void fetchBooks(String author, String title, int isbn);
    void getFavouriteBooks();
    void deleteFavouriteBooks();
}
