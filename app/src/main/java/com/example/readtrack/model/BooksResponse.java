package com.example.readtrack.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BooksResponse implements Parcelable{
    @SerializedName("articles")
    private List<Book> bookList;

    public BooksResponse() {}

    public BooksResponse(List<Book> bookList) {
       this.bookList = bookList;
    }

    public List<Book> getItems() {
        return bookList;
    }

    public void setItems(List<Book> bookList) {
         this.bookList = bookList;
    }

    @Override
    public String toString() {
        return "BooksResponse{" +
                "booksResponse=" + bookList +
                '}';
    }

    public static final Creator<BooksResponse> CREATOR = new Creator<BooksResponse>() {
        @Override
        public BooksResponse createFromParcel(Parcel in) {
            return new BooksResponse(in);
        }

        @Override
        public BooksResponse[] newArray(int size) {
            return new BooksResponse[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.bookList);
    }

    public void readFromParcel(Parcel source) {
        this.bookList = source.createTypedArrayList(Book.CREATOR);
    }

    protected BooksResponse(Parcel in) {
         this.bookList = in.createTypedArrayList(Book.CREATOR);
    }
}
