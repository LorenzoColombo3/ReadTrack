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
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.readtrack.R;
import com.example.readtrack.adapter.BooksRecyclerViewAdapter;
import com.example.readtrack.model.Book;
import com.example.readtrack.repository.BookRepository;
import com.example.readtrack.util.ResponseCallback;
import com.squareup.picasso.Picasso;

import java.util.List;

public class BookFragment extends Fragment implements ResponseCallback {

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
    private TextView numPages;
    private TextView description;
    private TextView titleOthBooks;
    private TextView readMoreButton;
    private TextView readLessButton;
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
        Bundle args = getArguments();
        BookRepository bookRepository =new BookRepository(requireActivity().getApplication(), this);
        if (args != null) {
            Book book = null;
            if (args.containsKey("bookArgument") && args.get("bookArgument") instanceof Book) {
                // Puoi essere sicuro che l'oggetto sia di tipo Book
                book = (Book) args.get("bookArgument");
                Log.d("Numero Pagine", Integer.toString(book.getVolumeInfo().getPageCount()));
                if(book.getVolumeInfo().getAuthors()!=null) {
                    bookRepository.searchBooks("autor:"+ book.getVolumeInfo().getAuthors().get(0));
                }else {
                    this.titleOthBooks.setText("Altri libri di Sconosciuto");
                }
            }
            setBook(book);
        }

        bookRepository.getSearchResults().observe(getViewLifecycleOwner(), books -> {
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
                                Bundle bundle = new Bundle();
                                bundle.putParcelable("bookArgument", book);
                                Navigation.findNavController(view).navigate(R.id.action_bookFragment_self, bundle);
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

    @Override
    public void onSuccess(List<Book> newsList, long lastUpdate) {

    }

    @Override
    public void onFailure(String errorMessage) {

    }
    //TODO controllare i casi null che potrebbero crashare
    private void setBook(Book book){
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
