package com.example.readtrack.ui;

import static com.example.readtrack.util.Constants.ENCRYPTED_SHARED_PREFERENCES_FILE_NAME;
import static com.example.readtrack.util.Constants.ID_TOKEN;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.room.RoomSQLiteQuery;

import com.example.readtrack.R;
import com.example.readtrack.databinding.FragmentResetPasswordBinding;
import com.example.readtrack.databinding.ModalBottomSheetContentBinding;
import com.example.readtrack.model.Books;
import com.example.readtrack.model.Result;
import com.example.readtrack.repository.books.BooksRepositoryWithLiveData;
import com.example.readtrack.repository.user.IUserRepository;
import com.example.readtrack.ui.welcome.UserViewModel;
import com.example.readtrack.ui.welcome.UserViewModelFactory;
import com.example.readtrack.util.DataEncryptionUtil;
import com.example.readtrack.util.ServiceLocator;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;

public class ModalBottomSheet extends BottomSheetDialogFragment {
    private final Books book;
    private UserViewModel userViewModel;

    public static final String TAG = "ModalBottomSheet";
    private ModalBottomSheetContentBinding binding;
    private DataEncryptionUtil dataEncryptionUtil;

    private BottomSheetListener mListener;
    private String segnalibro;
    private String idToken;

    public ModalBottomSheet(Books book, String segnalibro ){
        this.segnalibro=segnalibro;
        this.book=book;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ModalBottomSheetContentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BooksRepositoryWithLiveData booksRepositoryWithLiveData =
                ServiceLocator.getInstance().getBookRepository(requireActivity().getApplication());
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        if(segnalibro!=null)
          binding.textInputEditText.setText(segnalibro);
        else
            binding.textInputEditText.setText("0");
        binding.update.setOnClickListener(v->{
            int pagina = Integer.parseInt(binding.textInputEditText.getText().toString());
            if (pagina <= book.getVolumeInfo().getPageCount()&&pagina>0) {
                Log.d("null", String.valueOf(getParentFragment()==null));
                userViewModel.updateReadingBooks(book.getId(), pagina, "https" + book.getVolumeInfo().getImageLinks().getThumbnail().substring(4), idToken);
                Snackbar.make(requireActivity().findViewById(android.R.id.content),
                        "Segnalibro aggiornato",
                        Snackbar.LENGTH_SHORT).show();
                //dismiss(); //crea problemi sull'aggiornamento del segnalibro in BookFragment
            } else {
                binding.textInputEditText.setText(segnalibro);
                Snackbar.make(binding.standardBottomSheet,
                        "Inserisci un numero di pagine corretto",
                        Snackbar.LENGTH_SHORT).show();
            }
        });

        binding.btnPlus.setOnClickListener(v->{
            int pagina = Integer.parseInt(binding.textInputEditText.getText().toString());
            pagina++;
            binding.textInputEditText.setText(String.valueOf(pagina));
        });

        binding.btnMinus.setOnClickListener(v->{
            int pagina = Integer.parseInt(binding.textInputEditText.getText().toString());
            pagina--;
            binding.textInputEditText.setText(String.valueOf(pagina));
        });
    }
    public interface BottomSheetListener {
        void onButtonPressed();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        notifyButtonPressed();
    }

    public void setBottomSheetListener(BottomSheetListener listener) {
        this.mListener = listener;
    }

    private void notifyButtonPressed() {
        if (mListener != null) {
            mListener.onButtonPressed();
        }
    }

}
