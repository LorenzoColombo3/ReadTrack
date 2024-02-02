package com.example.readtrack.ui;

import static com.example.readtrack.util.Constants.TOP_HEADLINES_PAGE_SIZE_VALUE;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
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
import com.example.readtrack.model.BooksApiResponse;
import com.example.readtrack.model.Result;
import com.example.readtrack.repository.books.BooksRepository;
import com.example.readtrack.util.ServiceLocator;
import com.google.android.material.search.SearchBar;
import com.google.android.material.search.SearchView;
import com.example.readtrack.adapter.BooksSearchRecyclerAdapter;
import com.journeyapps.barcodescanner.CaptureActivity;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.ArrayList;
import java.util.List;


public class SearchFragment extends Fragment  {

    private SearchView searchView;
    private SearchBar searchBar;
    private BooksSearchRecyclerAdapter booksSearchRecyclerViewAdapter;
    private String query;
    private List<Books> booksList;
    private BooksViewModel booksViewModel;
    private ImageButton isbnSearch;

    private int totalItemCount;
    private int lastVisibleItem;
    private int visibleItemCount;
    private final int threshold = 1;
    public SearchFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        booksList= new ArrayList<>();
        BooksRepository booksRepositoryWithLiveData =
                ServiceLocator.getInstance().getBookRepository(requireActivity().getApplication());
        booksViewModel = new ViewModelProvider(
                requireActivity(),
                new BooksViewModelFactory(booksRepositoryWithLiveData)).get(BooksViewModel.class);
        query="";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        searchView = view.findViewById(R.id.search_view);
        searchBar = view.findViewById(R.id.search_bar);
        isbnSearch = view.findViewById(R.id.isbn_search_button);
        isbnSearch.setOnClickListener(v -> {
            scanCode();
        });
        RecyclerView recyclerViewFavBooks = view.findViewById(R.id.search_results);
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        booksSearchRecyclerViewAdapter = new BooksSearchRecyclerAdapter(booksList, requireActivity().getApplication(),
                new BooksSearchRecyclerAdapter.OnItemClickListener() {
                    @Override
                    public void onBooksItemClick(Books book) {
                        ((MainActivity) requireActivity()).hideBottomNavigation();
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("bookArgument", book);
                        Navigation.findNavController(view).navigate(R.id.action_search_fragment_to_bookFragment, bundle);
                    }
                });
        searchView
                .getEditText()
                .setOnEditorActionListener(
                        (v, actionId, event) -> {
                            searchBar.setText(searchView.getText());
                            searchView.hide();
                            query="";
                            booksViewModel.reset();
                            query=searchBar.getText().toString();
                            searchBar.setText("");
                            booksViewModel.getBooks(query).observe(getViewLifecycleOwner(), result -> {
                                if (result.isSuccess()) {
                                    BooksApiResponse booksApiResponse=((Result.BooksResponseSuccess) result).getData();
                                    List<Books> booksSearched = booksApiResponse.getItems();
                                    if (!booksViewModel.isLoading()) {
                                        booksList.clear();
                                        booksViewModel.setTotalResults(booksApiResponse.getTotalItems());
                                        this.booksList.addAll(booksSearched);
                                        recyclerViewFavBooks.setLayoutManager(layoutManager);
                                        recyclerViewFavBooks.setAdapter(booksSearchRecyclerViewAdapter);
                                    } else {
                                        booksViewModel.setLoading(false);
                                        booksViewModel.setCurrentResults(booksList.size());
                                        int initialSize = booksList.size();
                                        for (int i = 0; i < booksList.size(); i++) {
                                            if (booksList.get(i) == null) {
                                                booksList.remove(booksList.get(i));
                                            }
                                        }
                                        for (int i = 0; i < booksSearched.size(); i++) {
                                            booksList.add(booksSearched.get(i));
                                        }
                                        booksSearchRecyclerViewAdapter.notifyItemRangeInserted(initialSize, booksList.size());
                                    }
                                } else {
                                    Log.d("search result", "Nessun risultato trovato");
                                }
                            });
                            return false;
                        });
        recyclerViewFavBooks.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                boolean isConnected = isConnected();
                if (isConnected && totalItemCount != booksViewModel.getTotalResults()) {

                    totalItemCount = layoutManager.getItemCount();
                    lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                    visibleItemCount = layoutManager.getChildCount();

                    // Condition to enable the loading of other news while the user is scrolling the list
                    if (totalItemCount == visibleItemCount ||
                            (totalItemCount <= (lastVisibleItem + threshold) &&
                                    dy > 0 &&
                                    !booksViewModel.isLoading()
                            ) &&
                                    booksViewModel.getBooksResponseLiveData().getValue() != null &&
                                    booksViewModel.getCurrentResults() != booksViewModel.getTotalResults()
                    ) {
                        MutableLiveData<Result> booksListMutableLiveData = booksViewModel.getBooksResponseLiveData();
                        if (booksListMutableLiveData.getValue() != null &&
                                booksListMutableLiveData.getValue().isSuccess()) {

                            booksViewModel.setLoading(true);
                            booksList.add(null);
                            booksSearchRecyclerViewAdapter.notifyItemRangeInserted(booksList.size(), booksList.size() + 1);
                            int page = booksViewModel.getPage() + TOP_HEADLINES_PAGE_SIZE_VALUE;
                            booksViewModel.setPage(page);
                            booksViewModel.getBooks(query);
                        }
                    }
                }
            }
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
            query="isbn:"+result.getContents();
            booksViewModel.reset();
            booksViewModel.getBooks(query).observe(getViewLifecycleOwner(), res -> {
                if (res.isSuccess()) {
                    String id=((Result.BooksResponseSuccess) res).getData().getItems().get(0).getId();
                    booksViewModel.getBooksById(id).observe(getViewLifecycleOwner(), book -> {
                        if (book.isSuccess()) {
                            ((MainActivity) requireActivity()).hideBottomNavigation();
                            Log.d("search result", ((Result.BooksResponseSuccess) book).getData().getItems().get(0).getVolumeInfo().getTitle());
                            Bundle bundle = new Bundle();
                            bundle.putParcelable("bookArgument", ((Result.BooksResponseSuccess) book).getData().getItems().get(0));
                            Navigation.findNavController(getView()).navigate(R.id.action_search_fragment_to_bookFragment, bundle);
                        } else {
                        }
                    });
                } else {

                }
            });
        }
    });


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private boolean isConnected() {
        ConnectivityManager cm =
                (ConnectivityManager)requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        booksViewModel.setLoading(false);
    }

    @Override
    public void onPause(){
        super.onPause();
    }
    @Override
    public void onResume(){
        super.onResume();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


}
