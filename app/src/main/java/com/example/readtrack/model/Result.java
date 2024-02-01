package com.example.readtrack.model;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class Result {
    private Result() {}

    public boolean isSuccess() {
        if (this instanceof BooksResponseSuccess || this instanceof UserResponseSuccess || this instanceof BooksReadingResponseSuccess) {
            Log.d("successo","");
            return true;
        } else {
            return false;
        }
    }

    public static final class BooksReadingResponseSuccess extends Result {
        private BooksApiResponse booksResponse;
        private ArrayList<Books> userBooks;

        public BooksReadingResponseSuccess(BooksApiResponse booksResponse) {
            this.booksResponse = booksResponse;
        }
        public BooksReadingResponseSuccess(ArrayList<Books> favUserBooks){
            Log.d("daddaaaa", String.valueOf(favUserBooks.size()));
            this.userBooks=favUserBooks;
            Log.d("daddaaaaa",String.valueOf(userBooks.size()));
        }

        public ArrayList<Books> getBooksData(){
            Log.d("title", userBooks.get(0).getVolumeInfo().getTitle());
            return userBooks; }
        public BooksApiResponse getData() {
            return booksResponse;
        }

    }


    public static final class BooksResponseSuccess extends Result {
        private BooksApiResponse booksResponse;
        private HashMap<String,String> userBooks;

        public BooksResponseSuccess(BooksApiResponse booksResponse) {
            this.booksResponse = booksResponse;
        }
        public BooksResponseSuccess(HashMap<String,String> favUserBooks){
            this.userBooks=favUserBooks;
        }

        public HashMap<String, String> getBooksData(){ return userBooks; }
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
