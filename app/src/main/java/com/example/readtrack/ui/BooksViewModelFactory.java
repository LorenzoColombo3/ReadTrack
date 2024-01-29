package com.example.readtrack.ui;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.readtrack.repository.books.BooksRepositoryWithLiveData;

public class BooksViewModelFactory implements ViewModelProvider.Factory {

    private final BooksRepositoryWithLiveData booksRepositoryWithLiveData;

    public BooksViewModelFactory(BooksRepositoryWithLiveData booksRepositoryWithLiveData) {
        this.booksRepositoryWithLiveData = booksRepositoryWithLiveData;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new BooksViewModel(booksRepositoryWithLiveData);
    }
}
