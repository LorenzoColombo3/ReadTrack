package com.example.readtrack.model;

public abstract class Result {
    private Result() {}

    public boolean isSuccess() {
        return this instanceof Success;
    }

    public static final class Success extends Result {
        private BooksApiResponse booksResponse;
        public Success( BooksApiResponse booksResponse) {
            this.booksResponse = booksResponse;
        }
        public BooksApiResponse getData() {
            return booksResponse;
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
