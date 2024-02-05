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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.readtrack.R;
import com.example.readtrack.adapter.BooksRecyclerViewAdapter;
import com.example.readtrack.databinding.FragmentBookBinding;
import com.example.readtrack.model.Books;
import com.example.readtrack.model.BooksApiResponse;
import com.example.readtrack.model.Result;
import com.example.readtrack.repository.books.BooksRepository;
import com.example.readtrack.repository.user.IUserRepository;
import com.example.readtrack.ui.welcome.UserViewModel;
import com.example.readtrack.ui.welcome.UserViewModelFactory;
import com.example.readtrack.util.DataEncryptionUtil;
import com.example.readtrack.util.OnCheckListener;
import com.example.readtrack.util.ServiceLocator;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

public class BookFragment extends Fragment implements ModalBottomSheet.BottomSheetListener {
    private FragmentBookBinding binding;
    private String idToken;
    private UserViewModel userViewModel;
    private DataEncryptionUtil dataEncryptionUtil;
    private List<Books> otherBooks;
    private BooksRecyclerViewAdapter booksRecyclerViewAdapter;
    private BooksViewModel booksViewModel;
    private ModalBottomSheet modalBottomSheet;
    private int totalItemCount;
    private int lastVisibleItem;
    private int visibleItemCount;
    private final int threshold = 1;
    private  Books book;
    private String query;
    public BookFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BooksRepository booksRepositoryWithLiveData =
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
        binding = FragmentBookBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        booksViewModel.reset();
        Bundle args = getArguments();
        if (args != null) {
            if (args.containsKey("bookArgument") && args.get("bookArgument") instanceof Books) {
                book = (Books) args.get("bookArgument");
                booksViewModel.isFavouriteBook(book.getId(), idToken, new OnCheckListener() {
                    @Override
                    public void onCheckResult(boolean isFavourite) {
                        if (isFavourite) {
                            binding.addFavourite.setImageDrawable(AppCompatResources.getDrawable(getActivity(),
                                    R.drawable.ic_baseline_favorite_24));
                            binding.addFavourite.setColorFilter(ContextCompat.getColor(getActivity(), R.color.red_500));
                        } else {
                            binding.addFavourite.setImageDrawable( AppCompatResources.getDrawable(getActivity(),
                                    R.drawable.ic_baseline_favorite_border_24));
                            binding.addFavourite.setColorFilter(ContextCompat.getColor(getActivity(), R.color.black));
                        }
                    }
                });
                if(book.getVolumeInfo().getAuthors()!=null) {
                    query="autor:"+ book.getVolumeInfo().getAuthors().get(0);
                }else {
                    binding.titleOthBooks.setText("Altri libri di Sconosciuto");
                }
            } else {
                book = null;
            }
            setBook();
        } else {
            book = null;
        }
        booksViewModel.isReadingBook(book.getId(), idToken, isReading ->{
            if(isReading){
                booksViewModel.removeSavedBook(book.getId(), idToken);
                binding.wantToRead.setText("Stai leggendo");
                binding.wantToRead.setClickable(false);
            }
        });
        booksViewModel.isSavedBook(book.getId(), idToken, isSaved -> {
            if(isSaved){
                binding.wantToRead.setText("Salvato");
            }
        });
        binding.wantToRead.setOnClickListener(v->{
            booksViewModel.isSavedBook(book.getId(), idToken, isSaved -> {
                if(isSaved){
                    binding.wantToRead.setText("Salva per dopo");
                    booksViewModel.removeSavedBook(book.getId(), idToken);
                }else{
                    String imageLink="";
                    if(book.getVolumeInfo().getImageLinks()!=null)
                        imageLink= "https" + book.getVolumeInfo().getImageLinks().getThumbnail().substring(4);
                    binding.wantToRead.setText("Salvato");
                    booksViewModel.addSavedBook(book.getId(), imageLink, book.getVolumeInfo().getTitle(), idToken);
                    booksViewModel.removeFinishedBook(book.getId(), idToken);
                    book.setBookMarker(0);
                    aggiornaSegnalibro();
                }
            });
        });
        binding.addFavourite.setOnClickListener(v->{
            booksViewModel.isFavouriteBook(book.getId(), idToken, isFavourite -> {
                if (isFavourite) {
                    binding.addFavourite.setImageDrawable( AppCompatResources.getDrawable(getActivity(),
                            R.drawable.ic_baseline_favorite_border_24));
                    binding.addFavourite.setColorFilter(ContextCompat.getColor(getActivity(), R.color.black));
                    booksViewModel.removeFavouriteBook(book.getId(),idToken);
                } else {
                    String imageLink="";
                    if(book.getVolumeInfo().getImageLinks()!=null)
                         imageLink= "https" + book.getVolumeInfo().getImageLinks().getThumbnail().substring(4);
                    binding.addFavourite.setImageDrawable(AppCompatResources.getDrawable(getActivity(),
                            R.drawable.ic_baseline_favorite_24));
                    binding.addFavourite.setColorFilter(ContextCompat.getColor(getActivity(), R.color.red_500));
                    booksViewModel.addFavouriteBook(book.getId(), imageLink, idToken);
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
        binding.recyclerviewOtherBooks.setLayoutManager(layoutManager);
        binding.recyclerviewOtherBooks.setAdapter(booksRecyclerViewAdapter);
        booksViewModel.getBooks(query).observe(getViewLifecycleOwner(), result -> {
            if (result.isSuccess()) {
                BooksApiResponse booksApiResponse=((Result.BooksResponseSuccess) result).getData();
                List<Books> booksSearched = booksApiResponse.getItems();
                if (!booksViewModel.isLoading()) {
                    otherBooks.clear();
                    booksViewModel.setTotalResults(booksApiResponse.getTotalItems());
                    this.otherBooks.addAll(booksSearched);
                    binding.recyclerviewOtherBooks.setLayoutManager(layoutManager);
                    binding.recyclerviewOtherBooks.setAdapter(booksRecyclerViewAdapter);
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
                Log.d("search result", "Nessun risultato trovato");
            }
        });
        binding.recyclerviewOtherBooks.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
        binding.addReading.setOnClickListener(v -> {
            mostraModalBottomSheet(book);
        });
    }
    private void mostraModalBottomSheet(Books book) {
        modalBottomSheet = new ModalBottomSheet(book, String.valueOf(book.getBookMarker()));
        modalBottomSheet.setBottomSheetListener(this);
        modalBottomSheet.show(getActivity().getSupportFragmentManager(), ModalBottomSheet.TAG);

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

    private void setBook(){
        String titleString=book.getVolumeInfo().getTitle();
        if(titleString.length()>30) {
            binding.title.setText(book.getVolumeInfo().getTitle().substring(0, 30) + "...");
        }else{
            binding.title.setText(book.getVolumeInfo().getTitle());
        }

        if(book.getVolumeInfo().getImageLinks()!=null) {
            Picasso.get()
                    .load("https" + book.getVolumeInfo().getImageLinks().getThumbnail().substring(4))
                    .into(binding.cover);
        }
        if(book.getVolumeInfo().getAuthors()!=null){
            binding.author.setText(book.getVolumeInfo().getAuthors().get(0));
        }else{
            binding.author.setText("Sconosciuto");
        }
        binding.year.setText(book.getVolumeInfo().getPublishedDate().substring(0,4));
        if(book.getVolumeInfo().getCategories()!=null) {
            binding.genre.setText(book.getVolumeInfo().getCategories().get(0));
        }else{
            binding.genre.setText("Sconosciuto");
        }
        binding.publisher.setText(book.getVolumeInfo().getPublisher());
        if(book.getVolumeInfo().getIndustryIdentifiers()!=null) {
            if (book.getVolumeInfo().getIndustryIdentifiers().size() == 2) {
                binding.isbn.setText(book.getVolumeInfo().getIndustryIdentifiers().get(1).getIdentifier());
            } else {
                binding.isbn.setText(book.getVolumeInfo().getIndustryIdentifiers().get(0).getIdentifier());
            }
        }
        binding.numPages.setText(0+"/"+String.valueOf(book.getVolumeInfo().getPageCount()));
        booksViewModel.isFinishedBook(book.getId(), idToken, isFinished ->{
            if(isFinished){
                int numeroTotalePagine = book.getVolumeInfo().getPageCount(); // Numero totale di pagine del libro
                float percentualeCompletamento = (float) numeroTotalePagine / numeroTotalePagine * 100;
                binding.linearProgressIndicator.setProgress((int) percentualeCompletamento);
                binding.numPages.setText(String.valueOf(numeroTotalePagine).trim() + "/" + String.valueOf(numeroTotalePagine));
            }else{
                booksViewModel.getMarkerLiveData(book.getId(), idToken).observe(getViewLifecycleOwner(), result -> {
                    if (result.isSuccess()) {
                        book.setBookMarker(((Result.BooksResponseSuccess) result).getDataBooks().getItems().get(0).getBookMarker());
                        if(book.getBookMarker()>=0) {
                            int numeroTotalePagine = book.getVolumeInfo().getPageCount(); // Numero totale di pagine del libro
                            int paginaDelSegnalibro = book.getBookMarker(); // Pagina del segnalibro
                            float percentualeCompletamento = (float) paginaDelSegnalibro / numeroTotalePagine * 100;
                            binding.linearProgressIndicator.setProgress((int) percentualeCompletamento);
                            binding.numPages.setText(String.valueOf(book.getBookMarker()).trim() + "/" + String.valueOf(numeroTotalePagine));
                        }
                    } else {
                        binding.numPages.setText(0+"/"+String.valueOf(book.getVolumeInfo().getPageCount()));
                    }
                });
            }
        });
        binding.description.setText(book.getVolumeInfo().getDescription());// Aggiungi un listener per ascoltare i cambiamenti nel layout del TextView
        binding.description.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                binding.description.getViewTreeObserver().removeOnPreDrawListener(this);
                int lineCount = binding.description.getLineCount();
                checkReadMoreButtonVisibility();
                return true;
            }
        });
        binding.readMoreButton.setOnClickListener(view -> {
            binding.description.setMaxLines(Integer.MAX_VALUE);
            binding.description.setEllipsize(null);
            binding.readMoreButton.setVisibility(View.INVISIBLE);
            binding.readLessButton.setVisibility(View.VISIBLE);
        });
        binding.readLessButton.setOnClickListener(view -> {
            binding.description.setMaxLines(10);
            binding.description.setEllipsize(null);
            binding.readMoreButton.setVisibility(View.VISIBLE);
            binding.readLessButton.setVisibility(View.INVISIBLE);
        });
    }
    private void checkReadMoreButtonVisibility() {
        // Verifica se il testo è troncato
        if (binding.description.getLineCount() > binding.description.getMaxLines()) {
            binding.readMoreButton.setVisibility(View.VISIBLE);
        } else {
            binding.readMoreButton.setVisibility(View.INVISIBLE);
        }
    }

    public void aggiornaSegnalibro(){
        if(book.getBookMarker()>=0) {
            int numeroTotalePagine = book.getVolumeInfo().getPageCount(); // Numero totale di pagine del libro
            int paginaDelSegnalibro = book.getBookMarker(); // Pagina del segnalibro
            float percentualeCompletamento = (float) paginaDelSegnalibro / numeroTotalePagine * 100;
            binding.linearProgressIndicator.setProgress((int) percentualeCompletamento);
            binding.numPages.setText(String.valueOf(book.getBookMarker()).trim() + "/" + String.valueOf(numeroTotalePagine));
        }
    }

    public void aggiornaBottoneSalvato(){
        if(book.getBookMarker()>0){
            booksViewModel.removeSavedBook(book.getId(), idToken);
            binding.wantToRead.setText("Stai leggendo");
            binding.wantToRead.setClickable(false);
            if(book.getBookMarker()==book.getVolumeInfo().getPageCount()){
                String imageLink="";
                if(book.getVolumeInfo().getImageLinks()!=null)
                    imageLink= "https" + book.getVolumeInfo().getImageLinks().getThumbnail().substring(4);
                booksViewModel.addFinishedBook(book.getId(), imageLink, idToken);
                booksViewModel.removeReadingBook(book.getId(), idToken);
                binding.wantToRead.setText("Salva per dopo");
                binding.wantToRead.setClickable(true);
            }else {
                booksViewModel.removeFinishedBook(book.getId(), idToken);
            }
        }else{
            booksViewModel.removeReadingBook(book.getId(), idToken);
            binding.wantToRead.setText("Salva per dopo");
            binding.wantToRead.setClickable(true);
        }
    }

    @Override
    public void onButtonPressed() {
        Log.d("pagina1", String.valueOf(book.getBookMarker()) );
        aggiornaSegnalibro();
        aggiornaBottoneSalvato();
    }
}

