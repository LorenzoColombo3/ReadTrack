package com.example.readtrack.repository.books;

import static com.example.readtrack.util.Constants.FAVOURITES_BOOKS;
import static com.example.readtrack.util.Constants.READING_BOOKS;
import static com.example.readtrack.util.Constants.RED_BOOKS;
import static com.example.readtrack.util.Constants.WANT_TO_READ;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.readtrack.model.Books;
import com.example.readtrack.model.BooksApiResponse;
import com.example.readtrack.model.Result;
import com.example.readtrack.source.books.BaseBooksSource;
import com.example.readtrack.source.books.BaseFavoriteBooksSource;
import com.example.readtrack.source.books.BaseFinishedBooksSource;
import com.example.readtrack.source.books.BaseReadingBooksSource;
import com.example.readtrack.source.books.BaseSavedBooksSource;
import com.example.readtrack.util.OnFavouriteCheckListener;

import java.util.ArrayList;
import java.util.HashMap;

public class BooksRepository implements BooksResponseCallback {

    private static final String TAG = BooksRepository.class.getSimpleName();

    private  MutableLiveData<Result> apiBooksLiveData;
    private  MutableLiveData<Result> idBooksLiveData;
    private MutableLiveData<Result> favBooksListLiveData;
    private MutableLiveData<Result> readingBooksLiveData;

    private MutableLiveData<Result> finishedBooksLiveData;

    private MutableLiveData<Result> savedBooksLiveData;
    private MutableLiveData<Result> markerLiveData;
    private MutableLiveData<Result> numPagesLiveData;
    private MutableLiveData<Result> titleLiveData;
    private final BaseBooksSource booksDataSource;
    private final BaseFavoriteBooksSource favoriteBooksSource;
    private final BaseReadingBooksSource readingBooksSource;
    private final BaseSavedBooksSource savedBooksSource;
    private final BaseFinishedBooksSource finishedBooksSource;
    private MutableLiveData<Result> booksLiveData;
    private MutableLiveData<Result> readingBooksCompleteLiveData;


    public BooksRepository(BaseBooksSource booksDataSource, BaseFavoriteBooksSource favoriteBooksSource,
                           BaseReadingBooksSource readingBooksSource, BaseSavedBooksSource savedBooksSource, BaseFinishedBooksSource finishedBooksSource) {

        apiBooksLiveData = new MutableLiveData<>();
        idBooksLiveData = new MutableLiveData<>();
        favBooksListLiveData = new MutableLiveData<>();
        readingBooksLiveData = new MutableLiveData<>();
        finishedBooksLiveData = new MutableLiveData<>();
        savedBooksLiveData = new MutableLiveData<>();
        markerLiveData = new MutableLiveData<>();
        titleLiveData = new MutableLiveData<>();
        numPagesLiveData = new MutableLiveData<>();
        booksLiveData =new MutableLiveData<>();
        readingBooksCompleteLiveData =new MutableLiveData<>();
        this.booksDataSource = booksDataSource;
        this.favoriteBooksSource = favoriteBooksSource;
        this.readingBooksSource = readingBooksSource;
        this.savedBooksSource = savedBooksSource;
        this.finishedBooksSource = finishedBooksSource;
        this.booksDataSource.setBooksCallback(this);
        this.favoriteBooksSource.setBooksCallback(this);
        this.readingBooksSource.setBooksCallback(this);
       /* this.savedBooksSource.setBooksCallback(this);
        this.finishedBooksSource.setBooksCallback(this);*/

    }


    public MutableLiveData<Result> searchBooks(String query, int page) {
        booksDataSource.searchBooks(query, page);
        return apiBooksLiveData;
    }
    public void reset(){
        apiBooksLiveData = new MutableLiveData<Result>();
        idBooksLiveData = new MutableLiveData<Result>();
        markerLiveData = new MutableLiveData<Result>();
    }

    public MutableLiveData<Result> searchBooksById(String id) {
        booksDataSource.searchBooksById(id);
        return idBooksLiveData;
    }

    @Override
    public void onSuccessFromRemote(BooksApiResponse booksApiResponse) {
        Result.BooksResponseSuccess result = new Result.BooksResponseSuccess(booksApiResponse);
        apiBooksLiveData.postValue(result);
    }

    @Override
    public void onSuccessFromRemoteId(BooksApiResponse booksApiResponse) {
        Result.BooksResponseSuccess result = new Result.BooksResponseSuccess(booksApiResponse);
        idBooksLiveData.setValue(result);
    }


    @Override
    public void onFailureFromRemote(Exception exception) {
       Log.d("errore",  exception.getMessage() );
    }

    public MutableLiveData<Result> getUserFavBooks(String idToken){
        favoriteBooksSource.getUserFavBooks(idToken);
        return favBooksListLiveData;
    }

    public MutableLiveData<Result> getUserReadingBooks(String idToken){
        readingBooksSource.getUserReadingBooksTumbnail(idToken);
        return readingBooksLiveData;
    }

    //TODO sistemare il live data ritornato
    public MutableLiveData<Result> getUserReadingBooksComplete(String idToken){
        readingBooksSource.getUserReadingBooks(idToken);
        return booksLiveData;
    }

    /*public MutableLiveData<Result> getUserFinishedBooks(String idToken){
        savedBooksSource.getUserFinishedBooks(idToken);
        return finishedBooksLiveData;
    }*/


    /*public MutableLiveData<Result> getUserStartBooks(String idToken){
        savedBooksSource.getUserStartBooks(idToken);
        return savedBooksLiveData;
    }*/

    public MutableLiveData<Result> getMarker(String idBook, String idToken){
        readingBooksSource.getSegnalibro(idBook, idToken);
        return markerLiveData;
    }

    public MutableLiveData<Result> getTitle(String idBook, String idToken){
        Log.d("passa2","");
        readingBooksSource.getTitle(idBook, idToken);
        return titleLiveData;
    }

    public MutableLiveData<Result> getNumPages(String idBook, String idToken){
        readingBooksSource.getNumPages(idBook, idToken);
        return numPagesLiveData;
    }

    @Override
    public void onSuccessFromRemoteDatabase(HashMap<String,String> booksList, String path) {
        Result.BooksResponseSuccess result = new Result.BooksResponseSuccess(booksList);
        switch (path) {
            case FAVOURITES_BOOKS:
                favBooksListLiveData.postValue(result);
                break;
            case READING_BOOKS:
                readingBooksLiveData.postValue(result);
                break;

            case RED_BOOKS:
                finishedBooksLiveData.postValue(result);
                break;

            case WANT_TO_READ:
                savedBooksLiveData.postValue(result);
                break;
        }
    }

    public void onSuccessFromRemoteMarkReading(HashMap<String, String> booksList) {
        Result.BooksResponseSuccess result = new Result.BooksResponseSuccess(booksList);
        markerLiveData.postValue(result);
        Log.d("entra","entra");
    }
    public void onSuccessFromRemoteTitleReading(HashMap<String, String> booksList) {
        Result.BooksResponseSuccess result = new Result.BooksResponseSuccess(booksList);
        titleLiveData.postValue(result);
        Log.d("entra","entra");
    }
    public void onSuccessFromRemoteNumPagesReading(HashMap<String, String> booksList) {
        Result.BooksResponseSuccess result = new Result.BooksResponseSuccess(booksList);
        numPagesLiveData.postValue(result);
        Log.d("entra","entra");
    }
    public void onSuccessFromRemoteReadingBooks(ArrayList<Books> readingBooks) {
        Log.d("daddaaa", String.valueOf(readingBooks.size()));
        Result.BooksReadingResponseSuccess result = new Result.BooksReadingResponseSuccess(readingBooks);
        booksLiveData.postValue(result);
    }


    public void isFavouriteBook(String idBook, String idToken, OnFavouriteCheckListener listener){
        favoriteBooksSource.isFavouriteBook(idBook,idToken,listener);
    }


    public void removeFavouriteBook(String idBook, String idToken) {
        favoriteBooksSource.removeFavouriteBook(idBook,idToken);
    }


    public void addFavouriteBook(String idBook, String imageLink, String idToken) {
        favoriteBooksSource.addFavouriteBook(idBook, imageLink, idToken);
    }

    public void updateReadingBook(String idBook, int page, String linkImg, String title, int numPages,  String idToken){
        readingBooksSource.updateReadingBook(idBook,page,linkImg,title,numPages,idToken);
    }



}
