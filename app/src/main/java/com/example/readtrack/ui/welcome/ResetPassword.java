package com.example.readtrack.ui.welcome;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.readtrack.R;
import com.example.readtrack.databinding.FragmentRegistrationBinding;
import com.example.readtrack.databinding.FragmentResetPasswordBinding;

import org.apache.commons.validator.routines.EmailValidator;

public class ResetPassword extends Fragment {

    private FragmentResetPasswordBinding binding;
    private UserViewModel userViewModel;

    public ResetPassword() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        userViewModel.setAuthenticationError(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentResetPasswordBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        binding.resetButton.setOnClickListener(v->{
            String email = binding.textInputEditTextEmail.getText().toString().trim();
            if(isEmailOk(email)){
                binding.progressBar.setVisibility(View.VISIBLE);
                userViewModel.resetPassword(email);
                Navigation.findNavController(requireView()).navigate(R.id.action_resetPassword_to_loginFragment);
            }
        });
    }

    private boolean isEmailOk(String email) {
        if (!EmailValidator.getInstance().isValid((email))) {
            binding.textInputLayoutEmail.setError(getString(R.string.error_email));
            return false;
        } else {
            binding.textInputLayoutEmail.setError(null);
            return true;
        }
    }
}