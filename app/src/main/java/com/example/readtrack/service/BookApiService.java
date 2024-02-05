package com.example.readtrack.service;

import static com.example.readtrack.util.Constants.TOP_HEADLINES_PAGE_PARAMETER;
import static com.example.readtrack.util.Constants.TOP_HEADLINES_PAGE_SIZE_PARAMETER;
import static com.example.readtrack.util.Constants.TOP_HEADLINES_QUERY_PARAMETER;

import com.example.readtrack.model.Book;
import com.example.readtrack.model.BooksApiResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface BookApiService {
    @GET("v1/volumes")
    Call<BooksApiResponse> searchBooks(
            @Query(TOP_HEADLINES_QUERY_PARAMETER) String query,
            @Query(TOP_HEADLINES_PAGE_SIZE_PARAMETER) int pageSize,
            @Query(TOP_HEADLINES_PAGE_PARAMETER) int page
    );
    @GET("v1/volumes/{id}")
    Call<Book> searchBooksById(
            @Path("id") String id
    );
}
