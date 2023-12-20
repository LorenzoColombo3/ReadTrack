package com.example.readtrack.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.readtrack.R;
import com.example.readtrack.adapter.BooksSearchRecyclerAdapter;
import com.example.readtrack.model.Book;
import com.example.readtrack.model.BookViewModel;
import com.google.android.material.search.SearchBar;
import com.google.android.material.search.SearchView;

import org.w3c.dom.Text;

public class BookFragment extends Fragment {

    TextView title;
    ImageView cover;
    TextView author;
    TextView year;
    TextView genre;
    TextView publisher;
    TextView isbn;
    TextView description;
    public BookFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book, container, false);
        title=view.findViewById(R.id.title);
        cover=view.findViewById(R.id.cover);
        author=view.findViewById(R.id.author);
        year=view.findViewById(R.id.year);
        genre=view.findViewById(R.id.genre);
        publisher=view.findViewById(R.id.publisher);
        isbn=view.findViewById(R.id.isbn);
        description=view.findViewById(R.id.description);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                Book book = args.getSerializable("bookArgument", Book.class);
                Log.d("book", book.getVolumeInfo().getTitle());
            }
            // Usa l'oggetto Book come necessario
        }
    }
}
