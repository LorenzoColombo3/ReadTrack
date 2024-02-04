package com.example.readtrack.ui;

import static com.example.readtrack.util.Constants.ENCRYPTED_SHARED_PREFERENCES_FILE_NAME;
import static com.example.readtrack.util.Constants.FAVOURITES_BOOKS;
import static com.example.readtrack.util.Constants.ID_TOKEN;
import static com.example.readtrack.util.Constants.READING_BOOKS;
import static com.example.readtrack.util.Constants.FINISHED_BOOKS;
import static com.example.readtrack.util.Constants.WANT_TO_READ;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.readtrack.R;
import com.example.readtrack.adapter.BooksRecyclerViewAdapter;
import com.example.readtrack.databinding.FragmentProfileBinding;
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
import java.util.List;

public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;
    private BooksViewModel booksViewModel;
    private UserViewModel userViewModel;
    private DataEncryptionUtil dataEncryptionUtil;
    private String idToken;
    private List<Books> booksList;

    public ProfileFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BooksRepository booksRepositoryWithLiveData =
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
        binding.buttonLogout.setOnClickListener(v -> {
            userViewModel.logout();
            Navigation.findNavController(requireView()).navigate(R.id.action_profile_fragment_to_welcomeActivity);
            requireActivity().finish();
        });
        binding.profileImageView.setOnClickListener(v->{
            openGallery();
        });
        binding.userName.setText(userViewModel.getLoggedUser().getEmail());
        generateRecyclerView(view, binding.favBooks, FAVOURITES_BOOKS);
        generateRecyclerView(view, binding.readingBooks, READING_BOOKS);
        generateRecyclerView(view, binding.booksRead, FINISHED_BOOKS);
        generateRecyclerView(view, binding.startBooks, WANT_TO_READ);
    }

    private void generateRecyclerView(View view, RecyclerView recyclerViewBooks, String path){
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        this.booksList = new ArrayList<>();
        BooksRecyclerViewAdapter booksRecyclerViewAdapter = new BooksRecyclerViewAdapter(booksList,
                new BooksRecyclerViewAdapter.OnItemClickListener(){
                    @Override
                    public void onBooksItemClick(Books books) {
                        booksViewModel.getBooksById(books.getId()).observe(getViewLifecycleOwner(), res -> {
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
        recyclerViewBooks.setLayoutManager(layoutManager);
        recyclerViewBooks.setAdapter(booksRecyclerViewAdapter);
        switch (path) {
            case FAVOURITES_BOOKS:
                booksViewModel.getFavBooksMutableLiveData(idToken).observe(
                        getViewLifecycleOwner(), result -> {
                            if (result.isSuccess()) {
                                booksList.clear();
                                booksList = ((Result.BooksResponseSuccess) result).getDataBooks().getItems();
                                booksRecyclerViewAdapter.setBookList(booksList);
                                booksRecyclerViewAdapter.notifyDataSetChanged();
                            } else {
                            }
                        }
                );
                break;
            case READING_BOOKS:
               booksViewModel.getReadingBooksMutableLiveData(idToken).observe(
                        getViewLifecycleOwner(), result -> {
                            if (result.isSuccess()) {
                                booksList.clear();
                                booksList = ((Result.BooksResponseSuccess) result).getDataBooks().getItems();
                                booksRecyclerViewAdapter.setBookList(booksList);
                                booksRecyclerViewAdapter.notifyDataSetChanged();
                            } else {
                            }
                        }
                );
                break;

            case FINISHED_BOOKS:
                booksViewModel.getFinishedBooksMutableLiveData(idToken).observe(
                        getViewLifecycleOwner(), result -> {
                            if (result.isSuccess()) {
                                booksList.clear();
                                booksList = ((Result.BooksResponseSuccess) result).getDataBooks().getItems();
                                booksRecyclerViewAdapter.setBookList(booksList);
                                booksRecyclerViewAdapter.notifyDataSetChanged();
                            } else {
                            }
                        }
                );
                break;

            case WANT_TO_READ:
                booksViewModel.getSavedBooksMutableLiveData(idToken).observe(
                        getViewLifecycleOwner(), result -> {
                            if (result.isSuccess()) {
                                booksList.clear();
                                booksList = ((Result.BooksResponseSuccess) result).getDataBooks().getItems();
                                booksRecyclerViewAdapter.setBookList(booksList);
                                booksRecyclerViewAdapter.notifyDataSetChanged();
                            } else {
                            }
                        }
                );
                break;

            default:
                Log.d("errore", "path non valido");
        }

    }
    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) requireActivity()).showBottomNavigation();
    }
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap img=setProfileImage(requestCode,resultCode,data);
        Log.d("img", img.toString());
        binding.profileImageView.setImageBitmap(img);
    }

    private Bitmap setProfileImage(int requestCode, int resultCode, @Nullable Intent data){
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            Bitmap resizedBitmap=null;
            try {
                Bitmap originalBitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), selectedImageUri);

                int rotation = getRotationFromImage(selectedImageUri);
                Bitmap rotatedBitmap = rotateBitmap(originalBitmap, rotation);

                Bitmap squareBitmap = cropToSquare(rotatedBitmap);

                int desiredWidth = binding.profileImageView.getWidth();
                int desiredHeight = binding.profileImageView.getHeight();

                float widthScale = (float) desiredWidth / squareBitmap.getWidth();
                float heightScale = (float) desiredHeight / squareBitmap.getHeight();

                float scale = Math.min(widthScale, heightScale);

                Matrix matrix = new Matrix();
                matrix.postScale(scale, scale);

                resizedBitmap = Bitmap.createBitmap(squareBitmap, 0, 0, squareBitmap.getWidth(), squareBitmap.getHeight(), matrix, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return resizedBitmap;
        }else {
            return null;
        }
    }

    private Bitmap cropToSquare(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int size = Math.min(width, height);

        int left = (width - size) / 2;
        int top = (height - size) / 2;

        Bitmap squareBitmap = Bitmap.createBitmap(bitmap, left, top, size, size);
        return squareBitmap;
    }


    private int getRotationFromImage(Uri imageUri) {
        String[] projection = {MediaStore.Images.ImageColumns.ORIENTATION};
        Cursor cursor = requireActivity().getContentResolver().query(imageUri, projection, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int rotation = cursor.getInt(0);
            cursor.close();
            return rotation;
        }

        return 0;
    }

    private Bitmap rotateBitmap(Bitmap source, int rotation) {
        Matrix matrix = new Matrix();
        matrix.postRotate(rotation);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }
}