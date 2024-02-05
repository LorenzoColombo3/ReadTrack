package com.example.readtrack.ui;

import static com.example.readtrack.util.Constants.ENCRYPTED_SHARED_PREFERENCES_FILE_NAME;
import static com.example.readtrack.util.Constants.ID_TOKEN;

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

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.readtrack.R;
import com.example.readtrack.databinding.FragmentSettingsBinding;
import com.example.readtrack.model.Result;
import com.example.readtrack.repository.user.IUserRepository;
import com.example.readtrack.ui.welcome.UserViewModel;
import com.example.readtrack.ui.welcome.UserViewModelFactory;
import com.example.readtrack.util.DataEncryptionUtil;
import com.example.readtrack.util.ServiceLocator;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.security.GeneralSecurityException;


public class SettingsFragment extends Fragment {
    private FragmentSettingsBinding binding;
    private UserViewModel userViewModel;
    private DataEncryptionUtil dataEncryptionUtil;
    private String idToken;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        binding.buttonLogout.setOnClickListener(v->{
            userViewModel.logout();
            Navigation.findNavController(view).navigate(R.id.action_settingsFragment_to_welcomeActivity);
            requireActivity().finish();
        });
        binding.changeProfileImage.setOnClickListener(v ->{
            openGallery();
        });
        userViewModel.getUserImage(idToken).observe(
                getViewLifecycleOwner(), result -> {
                    if (result.isSuccess()) {
                        Picasso.get()
                                .load(((Result.UserResponseSuccess) result).getData().getImageLink())
                                .error(R.drawable.image_not_found)
                                .into(binding.profileImageView);
                    } else {
                    }
                }
        );
    }


    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap img=setProfileImage(requestCode,resultCode,data);
        if(img!=null) {
            userViewModel.saveUserProfileImg(idToken, img);
            binding.profileImageView.setImageBitmap(img);
        }else{

        }
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