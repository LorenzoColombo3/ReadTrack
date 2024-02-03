package com.example.readtrack.ui;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.readtrack.model.Result;
import com.example.readtrack.repository.books.BooksRepository;
import com.example.readtrack.util.OnCheckListener;

public class BooksViewModel extends ViewModel {
    private static final String TAG = BooksViewModel.class.getSimpleName();

    private final BooksRepository booksRepository;

    private MutableLiveData<Result> apiBooksLiveData;
    private MutableLiveData<Result> idBookLiveData;
    private MutableLiveData<Result> favBooksListLiveData;
    private MutableLiveData<Result> readingBooksLiveData;
    private MutableLiveData<Result> finishedBooksLiveData;
    private MutableLiveData<Result> savedBooksLiveData;
    private MutableLiveData<Result> markerLiveData;

    private int page;
    private int currentResults;
    private int totalResults;
    private boolean isLoading;


    public BooksViewModel(BooksRepository booksRepository) {
        this.booksRepository = booksRepository;
        favBooksListLiveData = new MutableLiveData<>();
        readingBooksLiveData = new MutableLiveData<>();
        finishedBooksLiveData = new MutableLiveData<>();
        savedBooksLiveData = new MutableLiveData<>();
        markerLiveData =new MutableLiveData<>();
    }

    public void reset(){
        apiBooksLiveData = new MutableLiveData<Result>();
        idBookLiveData = new MutableLiveData<Result>();
        markerLiveData = new MutableLiveData<Result>();
        this.page=0;
        this.totalResults=0;
        booksRepository.reset();
    }

    public MutableLiveData<Result> getBooks(String query) {
        apiBooksLiveData= booksRepository.searchBooks(query, page);
        return apiBooksLiveData;
    }

    public MutableLiveData<Result> getBooksById(String id) {;
        idBookLiveData= booksRepository.searchBooksById(id);
        return idBookLiveData;
    }

    public MutableLiveData<Result> getBooksResponseLiveData() {
        return apiBooksLiveData;
    }

    public MutableLiveData<Result> getMarkerLiveData(String idBook, String idToken){
        markerLiveData= booksRepository.getMarker(idBook, idToken);
        return markerLiveData;
    }

    public MutableLiveData<Result> getFavBooksMutableLiveData(String idToken) {
        favBooksListLiveData = booksRepository.getUserFavBooks(idToken);
        return favBooksListLiveData;
    }

    public MutableLiveData<Result> getReadingBooksMutableLiveData(String idToken) {
        readingBooksLiveData = booksRepository.getUserReadingBooks(idToken);
        return readingBooksLiveData;
    }

    public MutableLiveData<Result> getSavedBooksMutableLiveData(String idToken) {
        savedBooksLiveData = booksRepository.getUserSavedBooks(idToken);
        return savedBooksLiveData;
    }

    public MutableLiveData<Result> getFinishedBooksMutableLiveData(String idToken) {
        finishedBooksLiveData = booksRepository.getUserFinishedBooks(idToken);
        return finishedBooksLiveData;
    }

    public void isFavouriteBook(String idBook, String idToken, OnCheckListener listener) {
        booksRepository.isFavouriteBook(idBook,idToken,listener);
    }

    public void isReadingBook(String idBook, String idToken, OnCheckListener listener) {
        booksRepository.isReadingBook(idBook,idToken,listener);
    }

    public void isFinishedBook(String idBook, String idToken, OnCheckListener listener) {
        booksRepository.isFinishedBook(idBook,idToken,listener);
    }

    public void isSavedBook(String idBook, String idToken, OnCheckListener listener) {
        booksRepository.isSavedBook(idBook,idToken,listener);
    }

    public void removeFavouriteBook(String idBook, String idToken){
        booksRepository.removeFavouriteBook(idBook,idToken);
    }
    public void addFavouriteBook(String idBook, String imageLink, String idToken){
        booksRepository.addFavouriteBook(idBook, imageLink, idToken);
    }

    public void removeSavedBook(String idBook, String idToken){
        booksRepository.removeSavedBook(idBook,idToken);
    }
    public void addSavedBook(String idBook, String imageLink, String title, String idToken){
        booksRepository.addSavedBook(idBook, imageLink, title, idToken);
    }
    public void removeFinishedBook(String idBook, String idToken){
        booksRepository.removeFinishedBook(idBook,idToken);
    }
    public void addFinishedBook(String idBook, String imageLink, String idToken){
        booksRepository.addFinishedBook(idBook, imageLink, idToken);
    }

    public void updateReadingBooks(String idBook, int page, String linkImg,String title,int numPages, String idToken){
        booksRepository.updateReadingBook(idBook,page,linkImg,title,numPages,idToken);
    }

    public void removeReadingBook(String idBook, String idToken){
        booksRepository.removeReadingBook(idBook,idToken);
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    public int getCurrentResults() {
        return currentResults;
    }

    public void setCurrentResults(int currentResults) {
        this.currentResults = currentResults;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }
}
