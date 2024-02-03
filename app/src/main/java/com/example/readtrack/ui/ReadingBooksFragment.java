package com.example.readtrack.ui;

import static com.example.readtrack.util.Constants.ENCRYPTED_SHARED_PREFERENCES_FILE_NAME;
import static com.example.readtrack.util.Constants.ID_TOKEN;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.readtrack.R;
import com.example.readtrack.adapter.BooksFragmentRecyclerViewAdapter;
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
import java.util.List;

public class ReadingBooksFragment extends Fragment {
    FragmentReadingBooksBinding binding;
    private List<Books> bookList;
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
        bookList=new ArrayList<>();
        BooksRepository booksRepositoryWithLiveData = ServiceLocator.getInstance().getBookRepository(requireActivity().getApplication());
        booksViewModel = new ViewModelProvider(this, new BooksViewModelFactory(booksRepositoryWithLiveData)).get(BooksViewModel.class);
        dataEncryptionUtil = new DataEncryptionUtil(requireActivity().getApplication());
        IUserRepository userRepository = ServiceLocator.getInstance().getUserRepository(getActivity().getApplication());
        userViewModel = new ViewModelProvider(this, new UserViewModelFactory(userRepository)).get(UserViewModel.class);
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
        binding= FragmentReadingBooksBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        binding.readingBooksRecyclerView.setLayoutManager(layoutManager);
        BooksFragmentRecyclerViewAdapter booksRecyclerViewAdapter = new BooksFragmentRecyclerViewAdapter(bookList,
                new BooksFragmentRecyclerViewAdapter.OnItemClickListener(){
                    @Override
                    public void onBooksItemClick(String id) {
                        booksViewModel.getBooksById(id).observe(getViewLifecycleOwner(), res -> {
                            if (res.isSuccess()) {
                                ((MainActivity) requireActivity()).hideBottomNavigation();
                                Bundle bundle = new Bundle();
                                bundle.putParcelable("bookArgument", ((Result.BooksResponseSuccess) res).getData().getItems().get(0));
                                NavController parentNavController = Navigation.findNavController(getParentFragment().getParentFragment().requireView());
                                parentNavController.setGraph(R.navigation.main_nav_graph);
                                parentNavController.navigate(R.id.action_books_fragment_to_bookFragment, bundle);
                            } else {
                                // Gestisci il caso in cui non ci sono risultati
                                Log.d("search", "Nessun risultato trovato");
                            }
                        });
                    }
                });
        booksViewModel.reset();
        binding.readingBooksRecyclerView.setAdapter(booksRecyclerViewAdapter);
        booksViewModel.getReadingBooksMutableLiveData(idToken).observe(
                getViewLifecycleOwner(), result -> {
                    if (result.isSuccess()) {
                        bookList = ((Result.BooksResponseSuccess) result).getDataBooks().getItems();
                        if(bookList!=null) {
                            booksRecyclerViewAdapter.setBookList(bookList);
                            booksRecyclerViewAdapter.notifyDataSetChanged();
                        }
                    } else {
                    }
                }
        );
    }
}