package com.example.readtrack.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.readtrack.R;
import com.example.readtrack.adapter.BooksRecyclerViewAdapter;
import com.example.readtrack.model.Books;
import com.example.readtrack.model.Result;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class BookFragment extends Fragment  {

    private List<Books> otherBooks;
    private BooksRecyclerViewAdapter booksRecyclerViewAdapter;
    private RecyclerView recyclerViewOthBooks;
    private TextView title;
    private ImageView cover;
    private TextView author;
    private TextView year;
    private TextView genre;
    private TextView publisher;
    private TextView isbn;
    private TextView numPages;
    private TextView description;
    private TextView titleOthBooks;
    private TextView readMoreButton;
    private TextView readLessButton;
    private BooksViewModel booksViewModel;

    private String query;
    public BookFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        booksViewModel = new ViewModelProvider(requireActivity()).get(BooksViewModel.class);
        otherBooks=new ArrayList<>();
        query="";

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
        numPages=view.findViewById(R.id.num_pages);
        description=view.findViewById(R.id.description);
        recyclerViewOthBooks=view.findViewById(R.id.recyclerview_other_books);
        titleOthBooks=view.findViewById(R.id.title_oth_books);
        readMoreButton=view.findViewById(R.id.read_more_button);
        readLessButton=view.findViewById(R.id.read_less_button);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("inizio", "");
        Bundle args = getArguments();
        if (args != null) {
            Books book = null;
            if (args.containsKey("bookArgument") && args.get("bookArgument") instanceof Books) {
                // Puoi essere sicuro che l'oggetto sia di tipo Books
                book = (Books) args.get("bookArgument");
                if(book.getVolumeInfo().getAuthors()!=null) {
                    query="autor:"+ book.getVolumeInfo().getAuthors().get(0);
                    Log.d("autore", query);
                }else {
                    this.titleOthBooks.setText("Altri libri di Sconosciuto");
                }
            }
            setBook(book);
        }

        booksViewModel.getBooks(query).observe(getViewLifecycleOwner(), result -> {
            if (result.isSuccess()) {
                RecyclerView.LayoutManager layoutManager =
                        new LinearLayoutManager(requireContext(),
                                LinearLayoutManager.HORIZONTAL, false);
                this.otherBooks.addAll(((Result.BooksResponseSuccess) result).getData().getItems());
                booksRecyclerViewAdapter = new BooksRecyclerViewAdapter(otherBooks,
                        new BooksRecyclerViewAdapter.OnItemClickListener() {
                            @Override
                            public void onBooksItemClick(Books books) {
                                String id=books.getId();
                                booksViewModel.getBooksById(id).observe(getViewLifecycleOwner(), res -> {
                                    if (res.isSuccess()) {
                                        Bundle bundle = new Bundle();
                                        bundle.putParcelable("bookArgument", ((Result.BooksResponseSuccess) res).getData().getItems().get(0));
                                        Log.d("libro cliccato", ((Result.BooksResponseSuccess) res).getData().getItems().get(0).getVolumeInfo().getTitle());  //return errato
                                        Navigation.findNavController(view).navigate(R.id.action_bookFragment_self, bundle);
                                    } else {
                                        // Gestisci il caso in cui non ci sono risultati
                                        Log.d("search", "Nessun risultato trovato");
                                    }
                                });
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

    //TODO controllare i casi null che potrebbero crashare
    private void setBook(Books book){
        this.title.setText(book.getVolumeInfo().getTitle());
        Picasso.get()
                .load("https"+book.getVolumeInfo().getImageLinks().getThumbnail().substring(4))
                .into(this.cover);
        if(book.getVolumeInfo().getAuthors()!=null){
            this.author.setText(book.getVolumeInfo().getAuthors().get(0));
        }else{
            this.author.setText("Sconosciuto");
        }
        this.year.setText(book.getVolumeInfo().getPublishedDate().substring(0,4));
        if(book.getVolumeInfo().getCategories()!=null) {
            this.genre.setText(book.getVolumeInfo().getCategories().get(0));
        }else{
            this.genre.setText("Sconosciuto");
        }
        this.publisher.setText(book.getVolumeInfo().getPublisher());
        if(book.getVolumeInfo().getIndustryIdentifiers()!=null) {
            if (book.getVolumeInfo().getIndustryIdentifiers().size() == 2) {
                this.isbn.setText(book.getVolumeInfo().getIndustryIdentifiers().get(1).getIdentifier());
            } else {
                this.isbn.setText(book.getVolumeInfo().getIndustryIdentifiers().get(0).getIdentifier());
            }
        }
        this.numPages.setText("/"+String.valueOf(book.getVolumeInfo().getPageCount()));
        this.description.setText(book.getVolumeInfo().getDescription());// Aggiungi un listener per ascoltare i cambiamenti nel layout del TextView
        description.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                description.getViewTreeObserver().removeOnPreDrawListener(this);
                int lineCount = description.getLineCount();
                checkReadMoreButtonVisibility();
                return true;
            }
        });
        readMoreButton.setOnClickListener(view -> {
            description.setMaxLines(Integer.MAX_VALUE);
            description.setEllipsize(null);
            readMoreButton.setVisibility(View.INVISIBLE);
            readLessButton.setVisibility(View.VISIBLE);
        });
        readLessButton.setOnClickListener(view -> {
            description.setMaxLines(10);
            description.setEllipsize(null);
            readMoreButton.setVisibility(View.VISIBLE);
            readLessButton.setVisibility(View.INVISIBLE);
        });
    }
    private void checkReadMoreButtonVisibility() {
        // Verifica se il testo è troncato
        if (description.getLineCount() > description.getMaxLines()) {
            readMoreButton.setVisibility(View.VISIBLE);
        } else {
            readMoreButton.setVisibility(View.INVISIBLE);
        }
    }
}
