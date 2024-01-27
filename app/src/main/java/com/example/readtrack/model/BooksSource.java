package com.example.readtrack.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

public class BooksSource implements Parcelable {
    private String name;

    public BooksSource(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BooksSource that = (BooksSource) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "BooksSource{" +
                "name='" + name + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
    }

    public void readFromParcel(Parcel source) {
        this.name = source.readString();
    }

    protected BooksSource(Parcel in) {
        this.name = in.readString();
    }

    public static final Parcelable.Creator<BooksSource> CREATOR = new Parcelable.Creator<BooksSource>() {
        @Override
        public BooksSource createFromParcel(Parcel source) {
            return new BooksSource(source);
        }

        @Override
        public BooksSource[] newArray(int size) {
            return new BooksSource[size];
        }
    };
}
