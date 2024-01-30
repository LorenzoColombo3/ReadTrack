package com.example.readtrack.model;

import java.util.List;

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
        private BooksApiResponse booksResponse;
        private List<String> favUserBooks;
        public BooksResponseSuccess(BooksApiResponse booksResponse) {
            this.booksResponse = booksResponse;
        }
        public BooksResponseSuccess(List<String> favUserBooks){
            this.favUserBooks=favUserBooks;
        }
        public List<String> getFavData(){ return favUserBooks; }
        public BooksApiResponse getData() {
            return booksResponse;
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
