package com.example.readtrack.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.readtrack.R;
import com.example.readtrack.databinding.FragmentReadingBooksBinding;
import com.example.readtrack.databinding.FragmentSavedBooksBinding;

public class SavedBooksFragment extends Fragment {

    private FragmentSavedBooksBinding binding;
    public SavedBooksFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding= FragmentSavedBooksBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}