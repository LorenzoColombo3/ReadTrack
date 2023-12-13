package com.example.readtrack.model;

import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.readtrack.service.BookApiService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
public class BookViewModel extends ViewModel {
    private MutableLiveData<List<Book>> searchResults = new MutableLiveData<>();
    private BookApiService bookApiService;

    public BookViewModel() {
        // Inizializza Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.googleapis.com/books/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Crea un'istanza del servizio API
        bookApiService = retrofit.create(BookApiService.class);
    }

    // Metodo per eseguire la ricerca dei libri tramite le API di Google Books
    public void searchBooks(String query, String searchField) {
        Call<BooksApiResponse> call = bookApiService.searchBooks(query, 40, searchField);
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




    public MutableLiveData<List<Book>> getSearchResults() {
        return searchResults;
    }
}
