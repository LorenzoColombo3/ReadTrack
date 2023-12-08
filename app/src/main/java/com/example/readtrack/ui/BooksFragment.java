package com.example.readtrack.ui;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.readtrack.R;
import com.example.readtrack.adapter.BooksRecyclerViewAdapter;
import com.example.readtrack.model.Book;
import com.example.readtrack.model.BookViewModel;
import com.example.readtrack.util.JSONparser;
import com.example.readtrack.util.ResponseCallback;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BooksFragment extends Fragment implements ResponseCallback{

    private List<Book> booksList;
    private BooksRecyclerViewAdapter booksRecyclerViewAdapter;

    public BooksFragment() {}

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        Log.d("aaa","aaa");
        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menu.clear();
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                return false;
            }
        });

        RecyclerView recyclerViewFavBooks = view.findViewById(R.id.recyclerview_fav_books);
        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(requireContext(),
                        LinearLayoutManager.HORIZONTAL, false);

        booksList= getBooksListWithWithGSon();

        Log.d("bookList",booksList.toString());

        booksRecyclerViewAdapter = new BooksRecyclerViewAdapter(booksList,
                new BooksRecyclerViewAdapter.OnItemClickListener() {
                    @Override
                    public void onBooksItemClick(Book book) {
                        Snackbar.make(view, book.getVolumeInfo().getTitle(), Snackbar.LENGTH_SHORT).show();
                    }
                 });
        recyclerViewFavBooks.setLayoutManager(layoutManager);
        recyclerViewFavBooks.setAdapter(booksRecyclerViewAdapter);

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

    private List<Book> getBooksListWithWithGSon() {
        JSONparser jsonParserUtil = new JSONparser(requireActivity().getApplication());
        return jsonParserUtil.parseJSONFileWithGSon("Remote", "https://www.googleapis.com/books/v1/volumes?q=inauthor:Agatha%20Christie&startIndex=0&maxResults=40").getItems();
    }

}