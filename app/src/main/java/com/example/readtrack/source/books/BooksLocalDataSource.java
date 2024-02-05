package com.example.readtrack.source.books;


import com.example.readtrack.database.BookDao;
import com.example.readtrack.database.BookRoomDatabase;
import com.example.readtrack.model.Books;
import com.example.readtrack.model.BooksApiResponse;
import com.example.readtrack.model.BooksResponse;
import com.example.readtrack.util.DataEncryptionUtil;
import com.example.readtrack.util.SharedPreferencesUtil;

import java.util.List;

public class BooksLocalDataSource extends BaseBooksLocalDataSource {
    private final BookDao bookDao;
    private final SharedPreferencesUtil sharedPreferencesUtil;
    private final DataEncryptionUtil dataEncryptionUtil;

    public BooksLocalDataSource(BookRoomDatabase booksRoomDatabase,
                                SharedPreferencesUtil sharedPreferencesUtil,
                                DataEncryptionUtil dataEncryptionUtil
    ) {
        this.bookDao = booksRoomDatabase.bookDao();
        this.sharedPreferencesUtil = sharedPreferencesUtil;
        this.dataEncryptionUtil = dataEncryptionUtil;
    }


    @Override
    public void getBooks() {
        BookRoomDatabase.databaseWriteExecutor.execute(() -> {
            BooksResponse booksResponse = new BooksResponse();
            booksResponse.setItems(bookDao.getAll());
            booksCallback.onSuccessFromLocal(booksResponse);
        });
    }

    @Override
    public void updateBook(Books books) {
        BookRoomDatabase.databaseWriteExecutor.execute(() -> {
            if (books != null) {
                int rowUpdatedCounter = bookDao.updateSingleBook(books);
                // It means that the update succeeded because only one row had to be updated
                if (rowUpdatedCounter == 1) {
                    booksCallback.onBookUpdated();
                } else {
                    booksCallback.onFailureFromLocal(new Exception());
                }
            }
        });
    }

    @Override
    public void delete(Books book) {
        BookRoomDatabase.databaseWriteExecutor.execute(() -> {
           bookDao.delete(book);
        });
    }

    @Override
    public void insertBook(BooksResponse booksResponse) {
        BookRoomDatabase.databaseWriteExecutor.execute(() -> {
            // Reads the news from the database
            List<Books> allBooks = bookDao.getAll();
            List<Books> booksList = booksResponse.getItems();

            if (booksList != null) {

                // Checks if the news just downloaded has already been downloaded earlier
                // in order to preserve the news status (marked as favorite orot)
                for (Books news : allBooks) {
                    // This check works because News and NewsSource classes have their own
                    // implementation of equals(Object) and hashCode() methods
                    if (booksList.contains(news)) {
                        // The primary key and the favorite status is contained only in the News objects
                        // retrieved from the database, and not in the News objects downloaded from the
                        // Web Service. If the same news was already downloaded earlier, the following
                        // line of code replaces the the News object in booksList with the corresponding
                        // News object saved in the database, so that it has the primary key and the
                        // favorite status.
                        booksList.set(booksList.indexOf(news), news);
                    }
                }

                // Writes the news in the database and gets the associated primary keys
                List<Long> insertedNewsIds = bookDao.insertAll(booksList);
                booksCallback.onSuccessFromLocal(booksResponse);
            }
        });
    }

}


