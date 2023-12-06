package com.example.readtrack.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Book implements Parcelable {
    private String title;
    private String author;
    private String smallThumbnailURL;
    private String thumbnailURL;

    // Costruttore vuoto necessario per la deserializzazione
    public Book() {}

    // Getter e setter per i campi
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getSmallThumbnailURL() {
        return smallThumbnailURL;
    }

    public void setSmallThumbnailURL(String smallThumbnailURL) {
        this.smallThumbnailURL = smallThumbnailURL;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public void setThumbnailURL(String thumbnailURL) {
        this.thumbnailURL = thumbnailURL;
    }

    @Override
    public String toString() {
        return "Book{" +
                "title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", smallThumbnailURL='" + smallThumbnailURL + '\'' +
                ", thumbnailURL='" + thumbnailURL + '\'' +
                '}';
    }

    @Override
    public int describeContents() {return 0;}

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(this.author);
        dest.writeString(this.title);
        dest.writeString(this.smallThumbnailURL);
        dest.writeString(this.thumbnailURL);
    }
    public void readFromParcel(Parcel source) {
        this.author=source.readString();
        this.title=source.readString();
        this.smallThumbnailURL=source.readString();
        this.thumbnailURL=source.readString();
    }
    protected Book(Parcel in){
        this.author=in.readString();
        this.title=in.readString();
        this.smallThumbnailURL=in.readString();
        this.thumbnailURL=in.readString();
    }
    public static final Parcelable.Creator<Book> CREATOR = new Parcelable.Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel source) {
            return new Book(source);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };
}
