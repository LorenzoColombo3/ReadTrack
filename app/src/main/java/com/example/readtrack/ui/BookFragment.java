package com.example.readtrack.ui;

import static com.example.readtrack.util.Constants.ENCRYPTED_SHARED_PREFERENCES_FILE_NAME;
import static com.example.readtrack.util.Constants.ID_TOKEN;
import static com.example.readtrack.util.Constants.TOP_HEADLINES_PAGE_SIZE_VALUE;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.readtrack.R;
import com.example.readtrack.adapter.BooksRecyclerViewAdapter;
import com.example.readtrack.model.Books;
import com.example.readtrack.model.BooksApiResponse;
import com.example.readtrack.model.Result;
import com.example.readtrack.repository.books.BooksRepositoryWithLiveData;
import com.example.readtrack.repository.user.IUserRepository;
import com.example.readtrack.ui.welcome.UserViewModel;
import com.example.readtrack.ui.welcome.UserViewModelFactory;
import com.example.readtrack.util.DataEncryptionUtil;
import com.example.readtrack.util.OnFavouriteCheckListener;
import com.example.readtrack.util.ServiceLocator;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

public class BookFragment extends Fragment  {
    String idToken;
    private UserViewModel userViewModel;
    DataEncryptionUtil dataEncryptionUtil;
    private List<Books> otherBooks;
    private BooksRecyclerViewAdapter booksRecyclerViewAdapter;
    private RecyclerView recyclerViewOthBooks;
    private TextView title;
    private ImageView cover;
    private TextView author;
    private TextView year;
    private TextView genre;
    private TextView publisher;
    private TextView isbn;
    private TextView numPages;
    private TextView description;
    private TextView titleOthBooks;
    private TextView readMoreButton;
    private TextView readLessButton;
    private BooksViewModel booksViewModel;
    private Button favouriteButton;
    private int totalItemCount;
    private int lastVisibleItem;
    private int visibleItemCount;
    private final int threshold = 1;
    private String query;
    public BookFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BooksRepositoryWithLiveData booksRepositoryWithLiveData =
                ServiceLocator.getInstance().getBookRepository(requireActivity().getApplication());

        booksViewModel = new ViewModelProvider(
                requireActivity(),
                new BooksViewModelFactory(booksRepositoryWithLiveData)).get(BooksViewModel.class);
        otherBooks=new ArrayList<>();
        query="";
        dataEncryptionUtil = new DataEncryptionUtil(requireActivity().getApplication());
        IUserRepository userRepository = ServiceLocator.getInstance().
                getUserRepository(getActivity().getApplication());
        userViewModel = new ViewModelProvider(
                this, new UserViewModelFactory(userRepository)).get(UserViewModel.class);
        try {
            idToken = dataEncryptionUtil.readSecretDataWithEncryptedSharedPreferences(
                    ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, ID_TOKEN);
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book, container, false);
        title=view.findViewById(R.id.title);
        cover=view.findViewById(R.id.cover);
        author=view.findViewById(R.id.author);
        year=view.findViewById(R.id.year);
        genre=view.findViewById(R.id.genre);
        publisher=view.findViewById(R.id.publisher);
        isbn=view.findViewById(R.id.isbn);
        numPages=view.findViewById(R.id.num_pages);
        description=view.findViewById(R.id.description);
        recyclerViewOthBooks=view.findViewById(R.id.recyclerview_other_books);
        titleOthBooks=view.findViewById(R.id.title_oth_books);
        readMoreButton=view.findViewById(R.id.read_more_button);
        readLessButton=view.findViewById(R.id.read_less_button);
        favouriteButton=view.findViewById(R.id.add_favourite);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        booksViewModel.reset();
        Books book;
        Bundle args = getArguments();
        if (args != null) {
            if (args.containsKey("bookArgument") && args.get("bookArgument") instanceof Books) {
                book = (Books) args.get("bookArgument");
                userViewModel.isFavouriteBook(book.getId(), idToken, new OnFavouriteCheckListener() {
                    @Override
                    public void onFavouriteCheckResult(boolean isFavourite) {
                        if (isFavourite) {
                            favouriteButton.setText("Rimuovi dai preferiti");
                        } else {
                            favouriteButton.setText("Aggiungi ai preferiti");
                        }
                    }
                });
                if(book.getVolumeInfo().getAuthors()!=null) {
                    query="autor:"+ book.getVolumeInfo().getAuthors().get(0);
                }else {
                    this.titleOthBooks.setText("Altri libri di Sconosciuto");
                }
            } else {
                book = null;
            }
            setBook(book);
        } else {
            book = null;
        }

        String finalIdToken = idToken;
        favouriteButton.setOnClickListener(v->{
            userViewModel.isFavouriteBook(book.getId(), finalIdToken, isFavourite -> {
                if (isFavourite) {
                    favouriteButton.setText("Aggiungi ai preferiti");
                    userViewModel.removeFavouriteBook(book.getId(),finalIdToken);
                } else {
                    String imageLink= "https" + book.getVolumeInfo().getImageLinks().getThumbnail().substring(4);
                    favouriteButton.setText("Rimuovi dai preferiti");
                    userViewModel.addFavouriteBook(book.getId(), imageLink, finalIdToken);
                }
            });
        });


        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        booksRecyclerViewAdapter = new BooksRecyclerViewAdapter(otherBooks,
                new BooksRecyclerViewAdapter.OnItemClickListener() {
                    @Override
                    public void onBooksItemClick(Books books) {
                        String id=books.getId();
                        booksViewModel.getBooksById(id).observe(getViewLifecycleOwner(), res -> {
                            if (res.isSuccess()) {
                                Bundle bundle = new Bundle();
                                bundle.putParcelable("bookArgument", ((Result.BooksResponseSuccess) res).getData().getItems().get(0));
                                Navigation.findNavController(view).navigate(R.id.action_bookFragment_self, bundle);
                            } else {
                                // Gestisci il caso in cui non ci sono risultati
                                Log.d("search", "Nessun risultato trovato");
                            }
                        });
                    }
                });
        recyclerViewOthBooks.setLayoutManager(layoutManager);
        recyclerViewOthBooks.setAdapter(booksRecyclerViewAdapter);
        booksViewModel.getBooks(query).observe(getViewLifecycleOwner(), result -> {
            if (result.isSuccess()) {
                BooksApiResponse booksApiResponse=((Result.BooksResponseSuccess) result).getData();
                List<Books> booksSearched = booksApiResponse.getItems();
                if (!booksViewModel.isLoading()) {
                    otherBooks.clear();
                    booksViewModel.setTotalResults(booksApiResponse.getTotalItems());
                    this.otherBooks.addAll(booksSearched);
                    recyclerViewOthBooks.setLayoutManager(layoutManager);
                    recyclerViewOthBooks.setAdapter(booksRecyclerViewAdapter);
                } else {
                    booksViewModel.setLoading(false);
                    booksViewModel.setCurrentResults(otherBooks.size());
                    int initialSize = otherBooks.size();
                    for (int i = 0; i < otherBooks.size(); i++) {
                        if (otherBooks.get(i) == null) {
                            otherBooks.remove(otherBooks.get(i));
                        }
                    }
                    for (int i = 0; i < booksSearched.size(); i++) {
                        otherBooks.add(booksSearched.get(i));
                    }
                    booksRecyclerViewAdapter.notifyItemRangeInserted(initialSize, otherBooks.size());
                }
            } else {
                // Gestisci il caso in cui non ci sono risultati
                Log.d("search result", "Nessun risultato trovato");
            }
        });
        recyclerViewOthBooks.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                                    dx > 0 &&
                                    !booksViewModel.isLoading()
                            ) &&
                                    booksViewModel.getBooksResponseLiveData().getValue() != null &&
                                    booksViewModel.getCurrentResults() != booksViewModel.getTotalResults()
                    ) {
                        MutableLiveData<Result> booksListMutableLiveData = booksViewModel.getBooksResponseLiveData();
                        if (booksListMutableLiveData.getValue() != null &&
                                booksListMutableLiveData.getValue().isSuccess()) {
                            booksViewModel.setLoading(true);
                            otherBooks.add(null);
                            booksRecyclerViewAdapter.notifyItemRangeInserted(otherBooks.size(), otherBooks.size() + 1);
                            int page = booksViewModel.getPage() + TOP_HEADLINES_PAGE_SIZE_VALUE;
                            booksViewModel.setPage(page);
                            booksViewModel.getBooks(query);
                        }
                    }
                }
            }
        });
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

    private void setBook(Books book){
        this.title.setText(book.getVolumeInfo().getTitle());
        if(book.getVolumeInfo().getImageLinks()!=null) {
            Picasso.get()
                    .load("https" + book.getVolumeInfo().getImageLinks().getThumbnail().substring(4))
                    .into(this.cover);
        }
        if(book.getVolumeInfo().getAuthors()!=null){
            this.author.setText(book.getVolumeInfo().getAuthors().get(0));
        }else{
            this.author.setText("Sconosciuto");
        }
        this.year.setText(book.getVolumeInfo().getPublishedDate().substring(0,4));
        if(book.getVolumeInfo().getCategories()!=null) {
            this.genre.setText(book.getVolumeInfo().getCategories().get(0));
        }else{
            this.genre.setText("Sconosciuto");
        }
        this.publisher.setText(book.getVolumeInfo().getPublisher());
        if(book.getVolumeInfo().getIndustryIdentifiers()!=null) {
            if (book.getVolumeInfo().getIndustryIdentifiers().size() == 2) {
                this.isbn.setText(book.getVolumeInfo().getIndustryIdentifiers().get(1).getIdentifier());
            } else {
                this.isbn.setText(book.getVolumeInfo().getIndustryIdentifiers().get(0).getIdentifier());
            }
        }
        this.numPages.setText("/"+String.valueOf(book.getVolumeInfo().getPageCount()));
        this.description.setText(book.getVolumeInfo().getDescription());// Aggiungi un listener per ascoltare i cambiamenti nel layout del TextView
        description.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                description.getViewTreeObserver().removeOnPreDrawListener(this);
                int lineCount = description.getLineCount();
                checkReadMoreButtonVisibility();
                return true;
            }
        });
        readMoreButton.setOnClickListener(view -> {
            description.setMaxLines(Integer.MAX_VALUE);
            description.setEllipsize(null);
            readMoreButton.setVisibility(View.INVISIBLE);
            readLessButton.setVisibility(View.VISIBLE);
        });
        readLessButton.setOnClickListener(view -> {
            description.setMaxLines(10);
            description.setEllipsize(null);
            readMoreButton.setVisibility(View.VISIBLE);
            readLessButton.setVisibility(View.INVISIBLE);
        });
    }
    private void checkReadMoreButtonVisibility() {
        // Verifica se il testo è troncato
        if (description.getLineCount() > description.getMaxLines()) {
            readMoreButton.setVisibility(View.VISIBLE);
        } else {
            readMoreButton.setVisibility(View.INVISIBLE);
        }
    }
}
