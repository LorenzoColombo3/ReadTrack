package com.example.readtrack.repository;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.readtrack.database.BookDao;
import com.example.readtrack.database.BookRoomDatabase;
import com.example.readtrack.model.Books;
import com.example.readtrack.model.BooksApiResponse;
import com.example.readtrack.service.BookApiService;
import com.example.readtrack.util.ResponseCallback;
import com.example.readtrack.util.ServiceLocator;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookRepository extends ViewModel {
    private MutableLiveData<List<Books>> searchResults = new MutableLiveData<>();
    private final Application application;
    private final BookDao bookDao;
    private final ResponseCallback responseCallback;
    private BookApiService bookApiService;

    public BookRepository(Application application, ResponseCallback responseCallback) {

        this.bookApiService = ServiceLocator.getInstance().getBookApiService();
        this.application = application;
        this.responseCallback = responseCallback;
        BookRoomDatabase newsRoomDatabase = ServiceLocator.getInstance().getNewsDao(application);
        this.bookDao = newsRoomDatabase.bookDao();
    }

    // Metodo per eseguire la ricerca dei libri tramite le API di Google Books
    public void searchBooks(String query) {
        Call<BooksApiResponse> call = bookApiService.searchBooks(query, 40);
        call.enqueue(new Callback<BooksApiResponse>() {
            @Override
            public void onResponse(Call<BooksApiResponse> call, Response<BooksApiResponse> response) {
                if (response.isSuccessful()) {
                    List<Books> books = response.body().getItems();
                    // Aggiorna i risultati della ricerca nel LiveData
                    searchResults.setValue(books);
                    Log.d("Response Body", response.toString());
                } else {
                    Log.d("errore ricezione","errore ricezione");
                }
            }

            @Override
            public void onFailure(Call<BooksApiResponse> call, Throwable t) {
                Log.d("errore ricezione 2","errore ricezione 2");
            }
        });
    }
    public void searchBooksById(String query) {
        Call<Books> call = bookApiService.searchBooksById(query);
        call.enqueue(new Callback<Books>() {

            @Override
            public void onResponse(Call<Books> call, Response<Books> response) {
                if (response.isSuccessful()) {
                    Books book = response.body();
                    Log.d("contenuto libro",book.getVolumeInfo().getDescription());
                    List<Books> books=new ArrayList<>();
                    books.add(book);
                    Log.d("call", call.request().url().toString());
                    searchResults.setValue(books);
                } else {
                    Log.d("errore ricezione","errore ricezione");
                }
            }

            @Override
            public void onFailure(Call<Books> call, Throwable t) {
                Log.d("errore ricezione 2","errore ricezione 2");
            }
        });
    }

    public void saveDataInDatabase(Books book){
        BookRoomDatabase.databaseWriteExecutor.execute(() -> {
            List<Books> allBooks = bookDao.getAll();
            if (!allBooks.contains(book)) {
                bookDao.insertBook(book);
            }

            responseCallback.onSuccess(allBooks, System.currentTimeMillis());
        });
    }

    private void readDataFromDatabase(long lastUpdate) {
        BookRoomDatabase.databaseWriteExecutor.execute(() -> {
            responseCallback.onSuccess(bookDao.getAll(), lastUpdate);
        });
    }



    public MutableLiveData<List<Books>> getSearchResults() {
        return searchResults;
    }
}
