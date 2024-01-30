package com.example.readtrack.ui;


import static com.example.readtrack.util.Constants.ENCRYPTED_SHARED_PREFERENCES_FILE_NAME;
import static com.example.readtrack.util.Constants.ID_TOKEN;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
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
import com.example.readtrack.model.Books;
import com.example.readtrack.model.Result;
import com.example.readtrack.model.User;
import com.example.readtrack.repository.user.IUserRepository;
import com.example.readtrack.ui.welcome.UserViewModel;
import com.example.readtrack.ui.welcome.UserViewModelFactory;
import com.example.readtrack.util.DataEncryptionUtil;
import com.example.readtrack.util.JSONparser;
import com.example.readtrack.util.ResponseCallback;
import com.example.readtrack.util.ServiceLocator;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

public class BooksFragment extends Fragment implements ResponseCallback{

    private UserViewModel userViewModel;
    DataEncryptionUtil dataEncryptionUtil;
    private List<Books> booksList;
    private BooksRecyclerViewAdapter booksRecyclerViewAdapter;
    private RecyclerView favouriteRecView;
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
        return inflater.inflate(R.layout.fragment_books, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(requireContext(),
                        LinearLayoutManager.HORIZONTAL, false);
        favouriteRecView= view.findViewById(R.id.recyclerview_fav_books);
        booksRecyclerViewAdapter = new BooksRecyclerViewAdapter(booksList,
                new BooksRecyclerViewAdapter.OnItemClickListener() {
                    @Override
                    public void onBooksItemClick(Books book) {
                    }
                 });
        favouriteRecView.setLayoutManager(layoutManager);
        favouriteRecView.setAdapter(booksRecyclerViewAdapter);
        retrieveUserInformationAndStartActivity(idToken);
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

    private List<Books> getBooksListWithWithGSon() {
        JSONparser jsonParserUtil = new JSONparser(requireActivity().getApplication());
        return jsonParserUtil.parseJSONFileWithGSon("Remote", "https://www.googleapis.com/books/v1/volumes?q=inauthor:Agatha%20Christie&startIndex=0&maxResults=40").getItems();
    }


    private void retrieveUserInformationAndStartActivity(String idToken) {
        Log.d("start", "Start");
        userViewModel.getUserFavoriteBooksMutableLiveData(idToken).observe(
                getViewLifecycleOwner(), result -> {
                    if(result.isSuccess()){
                        List<String> books = ((Result.BooksResponseSuccess) result).getFavData();
                        Log.d("idFavourite", books.get(0));
                    }
                }
        );
    }

}