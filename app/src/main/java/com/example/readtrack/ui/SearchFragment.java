package com.example.readtrack.ui;

import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.readtrack.R;
import com.example.readtrack.model.Books;
import com.example.readtrack.util.ResponseCallback;
import com.google.android.material.search.SearchBar;
import com.google.android.material.search.SearchView;
import com.example.readtrack.repository.BookRepository;
import com.example.readtrack.adapter.BooksSearchRecyclerAdapter;
import com.journeyapps.barcodescanner.CaptureActivity;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.List;


public class SearchFragment extends Fragment implements ResponseCallback {

    private SearchView searchView;
    private SearchBar searchBar;
    private BooksSearchRecyclerAdapter booksSearchRecyclerViewAdapter;
    private BookRepository bookRepository;
    private BookRepository bookRepositoryIsbn;
    private ImageButton isbnSearch;
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
        isbnSearch = view.findViewById(R.id.isbn_search_button);
        bookRepository = new BookRepository(requireActivity().getApplication(), this);
        isbnSearch.setOnClickListener(v -> {
            scanCode();
        });
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
                            bookRepository.searchBooks(searchBar.getText().toString());
                            searchBar.setText("");
                            // Osserva i risultati della ricerca
                            bookRepository.getSearchResults().observe(getViewLifecycleOwner(), books -> {
                                if (books != null && !books.isEmpty()) {
                                    booksSearchRecyclerViewAdapter = new BooksSearchRecyclerAdapter(books,
                                            requireActivity().getApplication(),
                                            new BooksSearchRecyclerAdapter.OnItemClickListener() {
                                                @Override
                                                public void onBooksItemClick(Books book) {
                                                    ((MainActivity) requireActivity()).hideBottomNavigation();
                                                    Bundle bundle = new Bundle();
                                                    bundle.putParcelable("bookArgument", book);
                                                    Navigation.findNavController(view).navigate(R.id.action_search_fragment_to_bookFragment, bundle);
                                                    //bookRepository.saveDataInDatabase(book);
                                                }
                                            });
                                    recyclerViewFavBooks.setLayoutManager(layoutManager);
                                    recyclerViewFavBooks.setAdapter(booksSearchRecyclerViewAdapter);
                                } else {
                                    // Gestisci il caso in cui non ci sono risultati
                                    Log.d("search result", "Nessun risultato trovato");
                                }
                            });
                            return false;
                        });
        return view;
    }


    private void scanCode() {
        ScanOptions options= new ScanOptions();
        options.setPrompt("scannerizza codice");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureActivity.class);
        barLauncher.launch(options);
    }

    ActivityResultLauncher<ScanOptions> barLauncher= registerForActivityResult(new ScanContract(), result ->{
        if(result.getContents()!=null){
            Log.d("isbn:",result.getContents());
            bookRepositoryIsbn = new BookRepository(requireActivity().getApplication(), this);
            bookRepository.searchBooks("isbn:"+result.getContents());
            bookRepository.getSearchResults().observe(getViewLifecycleOwner(), res -> {
                if (res != null && !res.isEmpty()) {
                    String id=res.get(0).getId();
                    bookRepositoryIsbn.searchBooksById(id);
                    Log.d("id",id);
                    bookRepositoryIsbn.getSearchResults().observe(getViewLifecycleOwner(), books -> {
                        if (books != null && !books.isEmpty()) {
                            Bundle bundle = new Bundle();
                            bundle.putParcelable("bookArgument", books.get(0));
                            Navigation.findNavController(getView()).navigate(R.id.action_search_fragment_to_bookFragment, bundle);
                        } else {
                            // Gestisci il caso in cui non ci sono risultati
                            Log.d("search result", "Nessun risultato trovato");
                        }
                    });
                } else {
                    // Gestisci il caso in cui non ci sono risultati
                    Log.d("search result", "Nessun risultato trovato");
                }
            });
        }
    });



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onSuccess(List<Books> newsList, long lastUpdate) {

    }

    @Override
    public void onFailure(String errorMessage) {

    }


}
