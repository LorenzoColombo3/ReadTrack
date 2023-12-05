package com.example.readtrack.model;

public class Book {
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
}
