package com.example.readtrack.repository.books;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.readtrack.model.Books;
import com.example.readtrack.model.BooksApiResponse;
import com.example.readtrack.model.Result;
import com.example.readtrack.source.books.BaseBooksSource;
import com.example.readtrack.source.books.BooksCallback;

import java.util.List;

public class BooksRepositoryWithLiveData implements BooksCallback {

    private static final String TAG = BooksRepositoryWithLiveData.class.getSimpleName();

    private  MutableLiveData<Result> booksLiveData;
    private  MutableLiveData<Result> favoriteBooksLiveData;
    private final BaseBooksSource booksDataSource;

    public BooksRepositoryWithLiveData(BaseBooksSource booksDataSource) {

        booksLiveData = new MutableLiveData<>();
        favoriteBooksLiveData = new MutableLiveData<>();
        this.booksDataSource = booksDataSource;
        this.booksDataSource.setBooksCallback(this);
    }


    public MutableLiveData<Result> searchBooks(String query, int page) {
        Log.d("searching books", "");
        booksDataSource.searchBooks(query, page);
        return booksLiveData;
    }
    public void reset(){
        booksLiveData = new MutableLiveData<Result>();
        favoriteBooksLiveData = new MutableLiveData<Result>();
    }

    public MutableLiveData<Result> searchBooksById(String id) {
        Log.d("searching id", "");
        booksDataSource.searchBooksById(id);
        return favoriteBooksLiveData;
    }

    @Override
    public void onSuccessFromRemote(BooksApiResponse booksApiResponse) {
        Result.BooksResponseSuccess result = new Result.BooksResponseSuccess(booksApiResponse);
        Log.d("return books", booksApiResponse.getItems().get(0).getVolumeInfo().getTitle());
        booksLiveData.postValue(result);
    }

    @Override
    public void onSuccessFromRemoteId(BooksApiResponse booksApiResponse) {
        Result.BooksResponseSuccess result = new Result.BooksResponseSuccess(booksApiResponse);
        Log.d("return id", booksApiResponse.getItems().get(0).getVolumeInfo().getTitle());
        favoriteBooksLiveData.setValue(result);
    }


    @Override
    public void onFailureFromRemote(Exception exception) {
        Result.Error result = new Result.Error(exception.getMessage());
        booksLiveData.postValue(result);
    }

    @Override
    public void onBooksFavoriteStatusChanged(List<Books> books) {

    }

    @Override
    public void onDeleteFavoriteBooksSuccess(List<Books> favoriteBooks) {

    }
}
