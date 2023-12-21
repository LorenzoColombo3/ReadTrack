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
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.readtrack.R;
import com.example.readtrack.adapter.BooksRecyclerViewAdapter;
import com.example.readtrack.adapter.BooksSearchRecyclerAdapter;
import com.example.readtrack.model.Book;
import com.example.readtrack.model.BookViewModel;
import com.google.android.material.search.SearchBar;
import com.google.android.material.search.SearchView;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.List;

public class BookFragment extends Fragment {

    private List<Book> otherBooks;
    private BooksRecyclerViewAdapter booksRecyclerViewAdapter;
    private RecyclerView recyclerViewOthBooks;
    private TextView title;
    private ImageView cover;
    private TextView author;
    private TextView year;
    private TextView genre;
    private TextView publisher;
    private TextView isbn;
    private TextView description;
    private TextView titleOthBooks;
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
        recyclerViewOthBooks=view.findViewById(R.id.recyclerview_other_books);
        titleOthBooks=view.findViewById(R.id.title_oth_books);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle args = getArguments();
        BookViewModel bookViewModel=new BookViewModel();
        if (args != null) {
            Book book = null;
            if (args.containsKey("bookArgument") && args.get("bookArgument") instanceof Book) {
                // Puoi essere sicuro che l'oggetto sia di tipo Book
                book = (Book) args.get("bookArgument");
                bookViewModel.searchBooks(book.getVolumeInfo().getAuthors().get(0), "inhautor");
                this.titleOthBooks.setText("Altri libri di "+book.getVolumeInfo().getAuthors().get(0));
            }
            setBook(book);
        }

        bookViewModel.getSearchResults().observe(getViewLifecycleOwner(), books -> {
            if (books != null && !books.isEmpty()) {
                Log.d("search result", books.get(0).getVolumeInfo().getTitle());
                Log.d("search result", String.valueOf(books.size()));
                RecyclerView.LayoutManager layoutManager =
                        new LinearLayoutManager(requireContext(),
                                LinearLayoutManager.HORIZONTAL, false);

                booksRecyclerViewAdapter = new BooksRecyclerViewAdapter(books,
                        new BooksRecyclerViewAdapter.OnItemClickListener() {
                            @Override
                            public void onBooksItemClick(Book book) {
                                Snackbar.make(view, book.getVolumeInfo().getTitle(), Snackbar.LENGTH_SHORT).show();
                            }
                        });
                recyclerViewOthBooks.setLayoutManager(layoutManager);
                recyclerViewOthBooks.setAdapter(booksRecyclerViewAdapter);
            } else {
                // Gestisci il caso in cui non ci sono risultati
                Log.d("search result", "Nessun risultato trovato");
            }
        });
    }

    private void setBook(Book book){
        this.title.setText(book.getVolumeInfo().getTitle());
        Picasso.get()
                .load("https"+book.getVolumeInfo().getImageLinks().getThumbnail().substring(4))
                .into(this.cover);
        this.author.setText(book.getVolumeInfo().getAuthors().get(0));
        this.year.setText(book.getVolumeInfo().getPublishedDate());
        this.genre.setText(book.getVolumeInfo().getCategories().get(0));
        this.publisher.setText(book.getVolumeInfo().getPublisher());
        this.isbn.setText(book.getVolumeInfo().getIndustryIdentifiers().get(1).getIdentifier());
        this.description.setText(book.getVolumeInfo().getDescription());
    }
}
