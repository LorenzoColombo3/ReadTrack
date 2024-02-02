package com.example.readtrack.model;

import java.util.HashMap;

public abstract class Result {
    private Result() {}

    public boolean isSuccess() {
        if (this instanceof BooksResponseSuccess || this instanceof UserResponseSuccess) {
            return true;
        } else {
            return false;
        }
    }

    public static final class BooksResponseSuccess extends Result {
        private BooksApiResponse booksApiResponse;
        private BooksResponse booksResponse;

        public BooksResponseSuccess(BooksApiResponse booksApiResponse) {
            this.booksApiResponse = booksApiResponse;
        }
        public BooksResponseSuccess(BooksResponse booksResponse){
            this.booksResponse=booksResponse;
        }

        public BooksResponse getDataBooks(){ return booksResponse; }
        public BooksApiResponse getData() {
            return booksApiResponse;
        }

    }

    public static final class UserResponseSuccess extends Result {
        private final User user;
        public UserResponseSuccess(User user) {
            this.user = user;
        }
        public User getData() {
            return user;
        }
    }
    public static final class Error extends Result {
        private final String message;
        public Error(String message) {
            this.message = message;
        }
        public String getMessage() {
            return message;
        }
    }
}
