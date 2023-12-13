package com.example.readtrack.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.readtrack.R;
import com.example.readtrack.adapter.BooksRecyclerViewAdapter;
import com.example.readtrack.model.Book;
import com.google.android.material.search.SearchBar;
import com.google.android.material.search.SearchView;
import com.example.readtrack.model.BookViewModel;
import com.google.android.material.snackbar.Snackbar;


public class SearchFragment extends Fragment {

    SearchView searchView;
    SearchBar searchBar;
    private BooksRecyclerViewAdapter booksRecyclerViewAdapter;

    public SearchFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        searchView = view.findViewById(R.id.search_view);
        searchBar = view.findViewById(R.id.search_bar);
        searchView
                .getEditText()
                .setOnEditorActionListener(
                        (v, actionId, event) -> {
                            searchBar.setText(searchView.getText());
                            searchView.hide();

                            RecyclerView recyclerViewFavBooks = view.findViewById(R.id.search_results);
                            RecyclerView.LayoutManager layoutManager =
                                    new LinearLayoutManager(requireContext(),
                                            LinearLayoutManager.VERTICAL, false);
                            Log.d("inserimentoBar",searchBar.getText().toString());
                            BookViewModel bookViewModel=new BookViewModel();
                            bookViewModel.searchBooks(searchBar.getText().toString(), "inhautor");
                            // Osserva i risultati della ricerca
                            bookViewModel.getSearchResults().observe(getViewLifecycleOwner(), books -> {
                                if (books != null && !books.isEmpty()) {
                                    Log.d("search result", books.get(0).getVolumeInfo().getTitle());
                                    Log.d("search result", String.valueOf(books.size()));
                                    booksRecyclerViewAdapter = new BooksRecyclerViewAdapter(books,
                                            new BooksRecyclerViewAdapter.OnItemClickListener() {
                                                @Override
                                                public void onBooksItemClick(Book book) {
                                                    Snackbar.make(view, book.getVolumeInfo().getTitle(), Snackbar.LENGTH_SHORT).show();
                                                }
                                            });
                                    recyclerViewFavBooks.setLayoutManager(layoutManager);
                                    recyclerViewFavBooks.setAdapter(booksRecyclerViewAdapter);
                                } else {
                                    // Gestisci il caso in cui non ci sono risultati
                                    Log.d("search result", "Nessun risultato trovato");
                                }
                            });
                            return false;
                        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
}

/*
TODO da implementare con la barra di ricerca
BookViewModel bookViewModel=new BookViewModel();
bookViewModel.searchBooks("Il mistero del cadavere scomparso", "title");
// Osserva i risultati della ricerca
bookViewModel.getSearchResults().observe(this, books -> {
    if (books != null && !books.isEmpty()) {
        // I risultati della ricerca sono disponibili, accedi ai dati
        Log.d("search result", books.get(0).getVolumeInfo().getTitle());
    } else {
        // Gestisci il caso in cui non ci sono risultati
        Log.d("search result", "Nessun risultato trovato");
    }
});
 */