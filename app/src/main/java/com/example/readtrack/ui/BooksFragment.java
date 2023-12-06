package com.example.readtrack.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.readtrack.R;
import com.example.readtrack.adapter.BooksRecyclerViewAdapter;
import com.example.readtrack.model.Book;
import com.example.readtrack.repository.BooksRepository;
import com.example.readtrack.repository.IBookRepository;
import com.example.readtrack.util.ResponseCallback;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class BooksFragment extends Fragment implements ResponseCallback{

    private List<Book> booksList;
    private IBookRepository iBookRepository;
    private BooksRecyclerViewAdapter booksRecyclerViewAdapter;

    public BooksFragment() {}

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        iBookRepository=new BooksRepository(requireActivity().getApplication(), this);
        booksList=new ArrayList<>();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_books, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //TODO inserire l'immagine nel imageView della copertina
    }

    @Override
    public void onSuccess(List<Book> newsList, long lastUpdate) {
        if (booksList != null) {
            this.booksList.clear();
            this.booksList.addAll(booksList);
        }

        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                booksRecyclerViewAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onFailure(String errorMessage) {
        Snackbar.make(requireActivity().findViewById(android.R.id.content),
                errorMessage, Snackbar.LENGTH_LONG).show();
    }

}