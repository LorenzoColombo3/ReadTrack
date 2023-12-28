package com.example.readtrack.repository;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.readtrack.database.BookDao;
import com.example.readtrack.database.BookRoomDatabase;
import com.example.readtrack.model.Book;
import com.example.readtrack.model.BooksApiResponse;
import com.example.readtrack.service.BookApiService;
import com.example.readtrack.util.ResponseCallback;
import com.example.readtrack.util.ServiceLocator;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookRepository extends ViewModel {
    private MutableLiveData<List<Book>> searchResults = new MutableLiveData<>();
    private final Application application;
    //private final BookDao bookDao;
    private final ResponseCallback responseCallback;
    private BookApiService bookApiService;

    public BookRepository(Application application, ResponseCallback responseCallback) {

        this.bookApiService = ServiceLocator.getInstance().getBookApiService();
        this.application = application;
        this.responseCallback = responseCallback;
       /* BookRoomDatabase newsRoomDatabase = ServiceLocator.getInstance().getNewsDao(application);
        this.bookDao = newsRoomDatabase.bookDao();*/
    }

    // Metodo per eseguire la ricerca dei libri tramite le API di Google Books
    public void searchBooks(String query) {
        Call<BooksApiResponse> call = bookApiService.searchBooks(query, 40);
        call.enqueue(new Callback<BooksApiResponse>() {
            @Override
            public void onResponse(Call<BooksApiResponse> call, Response<BooksApiResponse> response) {
                if (response.isSuccessful()) {
                    List<Book> books = response.body().getItems();
                    // Aggiorna i risultati della ricerca nel LiveData
                    searchResults.setValue(books);
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

   /* public void saveDataInDatabase(Book book){
        BookRoomDatabase.databaseWriteExecutor.execute(() -> {
            List<Book> allBooks = bookDao.getAll();
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
    }*/



    public MutableLiveData<List<Book>> getSearchResults() {
        return searchResults;
    }
}
