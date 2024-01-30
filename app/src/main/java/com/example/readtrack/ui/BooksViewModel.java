package com.example.readtrack.ui;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.readtrack.model.Result;
import com.example.readtrack.repository.books.BooksRepositoryWithLiveData;

public class BooksViewModel extends ViewModel {
    private static final String TAG = BooksViewModel.class.getSimpleName();

    private final BooksRepositoryWithLiveData booksRepositoryWithLiveData;

    private MutableLiveData<Result> booksListLiveData;
    private MutableLiveData<Result> favoriteBookListLiveData;

    private int page;
    private int currentResults;
    private int totalResults;
    private boolean isLoading;

    public BooksViewModel(BooksRepositoryWithLiveData booksRepositoryWithLiveData) {
        this.booksRepositoryWithLiveData = booksRepositoryWithLiveData;
    }

    public void reset(){
        booksListLiveData = new MutableLiveData<Result>();
        favoriteBookListLiveData = new MutableLiveData<Result>();
        this.page=0;
        this.totalResults=0;
        booksRepositoryWithLiveData.reset();
    }

    public MutableLiveData<Result> getBooks(String query) {
        Log.d("start normal", "");
        booksListLiveData= booksRepositoryWithLiveData.searchBooks(query, page);
        return booksListLiveData;
    }

    public MutableLiveData<Result> getBooksById(String id) {
        favoriteBookListLiveData= booksRepositoryWithLiveData.searchBooksById(id);
        Log.d("start id", "");
        return favoriteBookListLiveData;
    }

    public MutableLiveData<Result> getBooksResponseLiveData() {
        return booksListLiveData;
    }
    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    public int getCurrentResults() {
        return currentResults;
    }

    public void setCurrentResults(int currentResults) {
        this.currentResults = currentResults;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }
}
