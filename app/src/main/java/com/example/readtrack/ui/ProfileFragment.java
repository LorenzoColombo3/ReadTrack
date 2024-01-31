package com.example.readtrack.ui;

import static com.example.readtrack.util.Constants.ENCRYPTED_SHARED_PREFERENCES_FILE_NAME;
import static com.example.readtrack.util.Constants.ID_TOKEN;
import static com.example.readtrack.util.Constants.TOP_HEADLINES_PAGE_SIZE_VALUE;

import android.os.Binder;
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
import com.example.readtrack.databinding.FragmentProfileBinding;
import com.example.readtrack.databinding.FragmentResetPasswordBinding;
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
    private BooksViewModel booksViewModel;
    private FragmentProfileBinding binding;
    private UserViewModel userViewModel;
    private DataEncryptionUtil dataEncryptionUtil;

    String idToken;

    private HashMap<String, String> bookList;

    public ProfileFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
            idToken = dataEncryptionUtil.readSecretDataWithEncryptedSharedPreferences(
                    ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, ID_TOKEN);
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        binding.circularImageView.setOnClickListener(v -> {
            //inserire codice qui
        });
        binding.userName.setText(userViewModel.getLoggedUser().getEmail());
        generateRecyclerView(view, binding.readingBooks);
        generateRecyclerView(view, binding.booksRead);
        generateRecyclerView(view, binding.favBooks);
    }

    public void generateRecyclerView(View view, RecyclerView recyclerView) {
        this.bookList = new HashMap<String, String>();
        this.bookList.clear();
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        HashMapRecyclerViewAdapter booksRecyclerViewAdapter = new HashMapRecyclerViewAdapter(bookList,
                new HashMapRecyclerViewAdapter.OnItemClickListener() {
                    @Override
                    public void onBooksItemClick(String id) {
                        booksViewModel.getBooksById(id).observe(getViewLifecycleOwner(), res -> {
                            if (res.isSuccess()) {
                                ((MainActivity) requireActivity()).hideBottomNavigation();
                                Bundle bundle = new Bundle();
                                bundle.putParcelable("bookArgument", ((Result.BooksResponseSuccess) res).getData().getItems().get(0));
                                Navigation.findNavController(view).navigate(R.id.action_profile_fragment_to_bookFragment, bundle);
                            } else {
                                Log.d("search", "Nessun risultato trovato");
                            }
                        });
                    }
                });
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(booksRecyclerViewAdapter);
        userViewModel.getUserFavoriteBooksMutableLiveData(idToken).observe(
                getViewLifecycleOwner(), result -> {
                    if (result.isSuccess()) {
                        bookList = ((Result.BooksResponseSuccess) result).getFavData();
                        Log.d("size", String.valueOf(bookList.size()));
                        booksRecyclerViewAdapter.setBookList(bookList);
                        booksRecyclerViewAdapter.notifyDataSetChanged();
                    } else {
                        // Gestire il caso in cui la richiesta dei preferiti dell'utente non ha successo
                    }
                }
        );
    }

}