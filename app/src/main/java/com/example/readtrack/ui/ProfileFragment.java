package com.example.readtrack.ui;

import static com.example.readtrack.util.Constants.ENCRYPTED_SHARED_PREFERENCES_FILE_NAME;
import static com.example.readtrack.util.Constants.FAVOURITES_BOOKS;
import static com.example.readtrack.util.Constants.ID_TOKEN;
import static com.example.readtrack.util.Constants.READING_BOOKS;
import static com.example.readtrack.util.Constants.RED_BOOKS;
import static com.example.readtrack.util.Constants.WANT_TO_READ;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.readtrack.R;
import com.example.readtrack.adapter.HashMapRecyclerViewAdapter;
import com.example.readtrack.databinding.FragmentProfileBinding;
import com.example.readtrack.databinding.FragmentResetPasswordBinding;
import com.example.readtrack.model.Result;
import com.example.readtrack.repository.books.BooksRepositoryWithLiveData;
import com.example.readtrack.repository.user.IUserRepository;
import com.example.readtrack.ui.welcome.UserViewModel;
import com.example.readtrack.ui.welcome.UserViewModelFactory;
import com.example.readtrack.util.DataEncryptionUtil;
import com.example.readtrack.util.ServiceLocator;


import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;

public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;
    private BooksViewModel booksViewModel;
    private UserViewModel userViewModel;
    private DataEncryptionUtil dataEncryptionUtil;
    String idToken;

    private HashMap<String, String> bookList;
    public ProfileFragment() {
        // Required empty public constructor
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
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    @Override
    public void onViewCreated (@NonNull View view, @Nullable Bundle savedInstanceState){
        //TODO da rifare dividendo ogni caso, non è possibile farlo selezionando un path differente a causa dei livedata, bisogna creare 4 metodi differenti con 4 live data differenti
        binding.buttonLogout.setOnClickListener(v -> {
            userViewModel.logout();
            Navigation.findNavController(requireView()).navigate(R.id.action_profile_fragment_to_welcomeActivity);
        });
        binding.userName.setText(userViewModel.getLoggedUser().getEmail());
        generateRecyclerView(view, binding.favBooks, FAVOURITES_BOOKS);
        generateRecyclerView(view, binding.readingBooks, READING_BOOKS);
        generateRecyclerView(view, binding.booksRead, RED_BOOKS);
        generateRecyclerView(view, binding.startBooks, WANT_TO_READ);
    }

    private void generateRecyclerView(View view, RecyclerView recyclerViewBooks, String path){
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);

        HashMapRecyclerViewAdapter booksRecyclerViewAdapter = new HashMapRecyclerViewAdapter(bookList,
                new HashMapRecyclerViewAdapter.OnItemClickListener(){
                    @Override
                    public void onBooksItemClick(String id) {
                        booksViewModel.getBooksById(id).observe(getViewLifecycleOwner(), res -> {
                            if (res.isSuccess()) {
                                ((MainActivity) requireActivity()).hideBottomNavigation();
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
        recyclerViewBooks.setLayoutManager(layoutManager);
        recyclerViewBooks.setAdapter(booksRecyclerViewAdapter);
        switch (path) {
            case FAVOURITES_BOOKS:
                userViewModel.getFavBooksMutableLiveData(idToken).observe(
                        getViewLifecycleOwner(), result -> {
                            if (result.isSuccess()) {
                                // Aggiorna la lista dei libri
                                bookList = ((Result.BooksResponseSuccess) result).getFavData();
                                booksRecyclerViewAdapter.setBookList(bookList);
                                booksRecyclerViewAdapter.notifyDataSetChanged();
                            } else {
                                // Gestire il caso in cui la richiesta dei preferiti dell'utente non ha successo
                            }
                        }
                );
                break;
            case READING_BOOKS:
                userViewModel.getFavBooksMutableLiveData(idToken).observe(
                        getViewLifecycleOwner(), result -> {
                            if (result.isSuccess()) {
                                // Aggiorna la lista dei libri
                                bookList = ((Result.BooksResponseSuccess) result).getFavData();
                                booksRecyclerViewAdapter.setBookList(bookList);
                                booksRecyclerViewAdapter.notifyDataSetChanged();
                            } else {
                                // Gestire il caso in cui la richiesta dei preferiti dell'utente non ha successo
                            }
                        }
                );
                break;

            case RED_BOOKS:
                userViewModel.getFavBooksMutableLiveData(idToken).observe(
                        getViewLifecycleOwner(), result -> {
                            if (result.isSuccess()) {
                                // Aggiorna la lista dei libri
                                bookList = ((Result.BooksResponseSuccess) result).getFavData();
                                booksRecyclerViewAdapter.setBookList(bookList);
                                booksRecyclerViewAdapter.notifyDataSetChanged();
                            } else {
                                // Gestire il caso in cui la richiesta dei preferiti dell'utente non ha successo
                            }
                        }
                );
                break;

            case WANT_TO_READ:
                userViewModel.getFavBooksMutableLiveData(idToken).observe(
                        getViewLifecycleOwner(), result -> {
                            if (result.isSuccess()) {
                                // Aggiorna la lista dei libri
                                bookList = ((Result.BooksResponseSuccess) result).getFavData();
                                booksRecyclerViewAdapter.setBookList(bookList);
                                booksRecyclerViewAdapter.notifyDataSetChanged();
                            } else {
                                // Gestire il caso in cui la richiesta dei preferiti dell'utente non ha successo
                            }
                        }
                );
                break;

            default:
                Log.d("errore", "path non valido");
        }

    }

}