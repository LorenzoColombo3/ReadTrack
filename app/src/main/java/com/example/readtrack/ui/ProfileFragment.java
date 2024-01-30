package com.example.readtrack.ui;

import static com.example.readtrack.util.Constants.ENCRYPTED_SHARED_PREFERENCES_FILE_NAME;
import static com.example.readtrack.util.Constants.ID_TOKEN;
import static com.example.readtrack.util.Constants.TOP_HEADLINES_PAGE_SIZE_VALUE;

import android.os.Bundle;

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

import com.example.readtrack.R;
import com.example.readtrack.adapter.BooksRecyclerViewAdapter;
import com.example.readtrack.adapter.HashMapRecyclerViewAdapter;
import com.example.readtrack.model.Books;
import com.example.readtrack.model.BooksApiResponse;
import com.example.readtrack.model.Result;
import com.example.readtrack.repository.books.BooksRepositoryWithLiveData;
import com.example.readtrack.repository.user.IUserRepository;
import com.example.readtrack.ui.welcome.UserViewModel;
import com.example.readtrack.ui.welcome.UserViewModelFactory;
import com.example.readtrack.util.DataEncryptionUtil;
import com.example.readtrack.util.ServiceLocator;
import com.example.readtrack.ui.BooksViewModel;


import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private BooksViewModel booksViewModel;
    private HashMapRecyclerViewAdapter booksRecyclerViewAdapter;
    private RecyclerView recyclerViewOthBooks;
    private UserViewModel userViewModel;
    private DataEncryptionUtil dataEncryptionUtil;
    private String mParam1;
    private String mParam2;
    String idToken;

    private HashMap<String, String> bookList;
    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.bookList = new HashMap<String, String>();
        BooksRepositoryWithLiveData booksRepositoryWithLiveData =
                ServiceLocator.getInstance().getBookRepository(requireActivity().getApplication());

        booksViewModel = new ViewModelProvider(
                requireActivity(),
                new BooksViewModelFactory(booksRepositoryWithLiveData)).get(BooksViewModel.class);

        dataEncryptionUtil = new DataEncryptionUtil(requireActivity().getApplication());
        IUserRepository userRepository = ServiceLocator.getInstance().
                getUserRepository(getActivity().getApplication());
        userViewModel = new ViewModelProvider(
                this, new UserViewModelFactory(userRepository)).get(UserViewModel.class);
        try {
            Log.d("idToken encrypted", dataEncryptionUtil.readSecretDataWithEncryptedSharedPreferences(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, ID_TOKEN));
            idToken = dataEncryptionUtil.readSecretDataWithEncryptedSharedPreferences(
                    ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, ID_TOKEN);
            Log.d("Token",idToken);
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }
    @Override
    public void onViewCreated (@NonNull View view, @Nullable Bundle savedInstanceState){
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewOthBooks= view.findViewById(R.id.fav_books);
        booksRecyclerViewAdapter = new HashMapRecyclerViewAdapter(bookList,
                new HashMapRecyclerViewAdapter.OnItemClickListener(){
                      @Override
                      public void onBooksItemClick(String id) {
                          booksViewModel.getBooksById(id).observe(getViewLifecycleOwner(), res -> {
                              if (res.isSuccess()) {
                                  Bundle bundle = new Bundle();
                                  bundle.putParcelable("bookArgument", ((Result.BooksResponseSuccess) res).getData().getItems().get(0));
                                  Navigation.findNavController(view).navigate(R.id.action_profile_fragment_to_bookFragment, bundle);
                              } else {
                                  // Gestisci il caso in cui non ci sono risultati
                                  Log.d("search", "Nessun risultato trovato");
                              }
                          });
                      }
                });

        recyclerViewOthBooks.setLayoutManager(layoutManager);
        recyclerViewOthBooks.setAdapter(booksRecyclerViewAdapter);
        userViewModel.getUserFavoriteBooksMutableLiveData(idToken).observe(
                getViewLifecycleOwner(), result -> {
                    if (result.isSuccess()) {
                        // Aggiorna la lista dei libri
                        bookList = ((Result.BooksResponseSuccess) result).getFavData();
                        // Aggiorna l'adapter con la nuova lista
                        booksRecyclerViewAdapter.setBookList(bookList);
                        // Notifica il cambiamento solo dopo aver aggiornato i dati
                        booksRecyclerViewAdapter.notifyDataSetChanged();
                    } else {
                        // Gestire il caso in cui la richiesta dei preferiti dell'utente non ha successo
                    }
                }
        );

    }

    private void generateRecyclerView(List<Books> booksList){
        /*LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        booksRecyclerViewAdapter = new BooksRecyclerViewAdapter(booksList,
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
                    booksList.clear();
                    booksViewModel.setTotalResults(booksApiResponse.getTotalItems());
                    booksList.addAll(booksSearched);
                    recyclerViewOthBooks.setLayoutManager(layoutManager);
                    recyclerViewOthBooks.setAdapter(booksRecyclerViewAdapter);
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
                    booksRecyclerViewAdapter.notifyItemRangeInserted(initialSize, booksList.size());
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
                            booksList.add(null);
                            booksRecyclerViewAdapter.notifyItemRangeInserted(booksList.size(), booksList.size() + 1);
                            int page = booksViewModel.getPage() + TOP_HEADLINES_PAGE_SIZE_VALUE;
                            booksViewModel.setPage(page);
                            booksViewModel.getBooks(query);
                        }
                    }
                }
            }
        });*/
    }
    private void retrieveUserInformationAndStartActivity(String idToken) {
        Log.d("start", "Start");
        userViewModel.getUserFavoriteBooksMutableLiveData(idToken).observe(
                getViewLifecycleOwner(), result -> {
                    if(result.isSuccess()){
                        HashMap<String,String> books = ((Result.BooksResponseSuccess) result).getFavData();
                    }
                }
        );
    }
}