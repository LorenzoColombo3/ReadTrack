package com.example.readtrack.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BooksResponse implements Parcelable{
    @SerializedName("articles")
    private List<Books> booksList;

    public BooksResponse() {}

    public BooksResponse(List<Books> booksList) {
       this.booksList = booksList;
    }

    public List<Books> getItems() {
        return booksList;
    }

    public void setItems(List<Books> booksList) {
         this.booksList = booksList;
    }

    @Override
    public String toString() {
        return "BooksResponse{" +
                "booksResponse=" + booksList +
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
        dest.writeTypedList(this.booksList);
    }

    public void readFromParcel(Parcel source) {
        this.booksList = source.createTypedArrayList(Books.CREATOR);
    }

    protected BooksResponse(Parcel in) {
         this.booksList = in.createTypedArrayList(Books.CREATOR);
    }
}
