package com.example.readtrack.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class BooksApiResponse implements Parcelable {
    private String status;
    private int totalResults;
    private List<Book> books;

    public BooksApiResponse(){}

    public BooksApiResponse(String status, int totalResults, List<Book> books){
        this.status=status;
        this.totalResults=totalResults;
        this.books=books;
    }

    @Override
    public String toString() {
        return "BooksApiResponse{" +
                "status='" + status + '\'' +
                ", totalResults=" + totalResults +
                ", books=" + books +
                '}';
    }

    public String getStatus() {return status;}

    public void setStatus(String status) {this.status = status;}

    public int getTotalResults() {return totalResults;}

    public void setTotalResults(int totalResults) {this.totalResults = totalResults;}

    public List<Book> getBooks() {return books;}

    public void setBooks(List<Book> books) {this.books = books;}

    @Override
    public int describeContents() {return 0;}

    protected BooksApiResponse(Parcel in) {
        this.status = in.readString();
        this.totalResults = in.readInt();
        this.books = in.createTypedArrayList(Book.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.status);
        dest.writeInt(this.totalResults);
        dest.writeTypedList(this.books);
    }

    public void readFromParcel(Parcel source) {
        this.status = source.readString();
        this.totalResults = source.readInt();
        this.books = source.createTypedArrayList(Book.CREATOR);
    }

    public static final Creator<BooksApiResponse> CREATOR = new Creator<BooksApiResponse>() {
        @Override
        public BooksApiResponse createFromParcel(Parcel in) {
            return new BooksApiResponse(in);
        }
        @Override
        public BooksApiResponse[] newArray(int size) {return new BooksApiResponse[size];}
    };
}
