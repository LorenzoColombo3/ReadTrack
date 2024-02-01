package com.example.readtrack.ui;

import com.example.readtrack.R;
import static com.example.readtrack.util.Constants.ENCRYPTED_SHARED_PREFERENCES_FILE_NAME;
import static com.example.readtrack.util.Constants.ID_TOKEN;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.readtrack.adapter.BooksRecyclerViewAdapter;
import com.example.readtrack.databinding.FragmentBooksBinding;
import com.example.readtrack.model.Books;
import com.example.readtrack.repository.user.IUserRepository;
import com.example.readtrack.ui.welcome.UserViewModel;
import com.example.readtrack.ui.welcome.UserViewModelFactory;
import com.example.readtrack.util.DataEncryptionUtil;
import com.example.readtrack.util.JSONparser;
import com.example.readtrack.util.ResponseCallback;
import com.example.readtrack.util.ServiceLocator;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

public class BooksFragment extends Fragment implements ResponseCallback{
    FragmentBooksBinding binding;
    private UserViewModel userViewModel;
    DataEncryptionUtil dataEncryptionUtil;
    private List<Books> booksList;
    private BooksRecyclerViewAdapter booksRecyclerViewAdapter;
    private String idToken;

    public BooksFragment() {}

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataEncryptionUtil = new DataEncryptionUtil(requireActivity().getApplication());
        booksList=new ArrayList<>();
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
        binding= FragmentBooksBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.toggleButton.check(R.id.reading_books);
        binding.toggleButton.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            final int buttonReading = R.id.reading_books;
            final int buttonSaved = R.id.saved_for_later;

            @Override
            public void onButtonChecked(MaterialButtonToggleGroup toggleButton, int checkedId, boolean isChecked) {
                if (checkedId == buttonReading && isChecked) {
                    replaceFragment(new ReadingBooksFragment());
                }
                if (checkedId == buttonSaved && isChecked) {
                    replaceFragment(new SavedBooksFragment());
                }
            }
        });
    }
    private void replaceFragment(Fragment fragment) {
        getChildFragmentManager().beginTransaction()
                .replace(R.id.nav_books_fragment, fragment)
                .commit();
    }

    @Override
    public void onSuccess(List<Books> newsList, long lastUpdate) {
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