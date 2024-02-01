package com.example.readtrack.ui;

import static com.example.readtrack.util.Constants.ENCRYPTED_SHARED_PREFERENCES_FILE_NAME;
import static com.example.readtrack.util.Constants.ID_TOKEN;
import static com.example.readtrack.util.Constants.TITLE;

import android.os.Binder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.readtrack.R;
import com.example.readtrack.adapter.HashMapRecyclerViewAdapter;
import com.example.readtrack.databinding.FragmentBooksBinding;
import com.example.readtrack.databinding.FragmentReadingBooksBinding;
import com.example.readtrack.model.Books;
import com.example.readtrack.model.Result;
import com.example.readtrack.repository.books.BooksRepository;
import com.example.readtrack.repository.user.IUserRepository;
import com.example.readtrack.ui.welcome.UserViewModel;
import com.example.readtrack.ui.welcome.UserViewModelFactory;
import com.example.readtrack.util.DataEncryptionUtil;
import com.example.readtrack.util.ServiceLocator;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ReadingBooksFragment extends Fragment {
    FragmentReadingBooksBinding binding;
    private ArrayList<Books> bookList;
    private BooksViewModel booksViewModel;
    private DataEncryptionUtil dataEncryptionUtil;
    private UserViewModel userViewModel;
    private String idToken;
    private HashMap<String,String> titolo;
    public ReadingBooksFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BooksRepository booksRepositoryWithLiveData =
                ServiceLocator.getInstance().getBookRepository(requireActivity().getApplication());
        booksViewModel = new ViewModelProvider(
                this, new BooksViewModelFactory(booksRepositoryWithLiveData)).get(BooksViewModel.class);
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
        binding= FragmentReadingBooksBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        booksViewModel.getUserReadingBooksComplete(idToken).observe(
                getViewLifecycleOwner(), result -> {
                    if (result.isSuccess()) {
                        // Aggiorna la lista dei libri
                        bookList = ((Result.BooksReadingResponseSuccess) result).getBooksData();
                        Log.d("title",bookList.get(0).getVolumeInfo().getTitle());
                        // Inizializza due ArrayList per le chiavi e i valori
                        // Itera attraverso le chiavi della HashMap e aggiungi le coppie corrispondenti agli ArrayList

                    } else {
                        // Gestire il caso in cui la richiesta dei preferiti dell'utente non ha successo
                    }
                }
        );
    }
}