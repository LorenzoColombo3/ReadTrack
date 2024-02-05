package com.example.readtrack.source.books;


import com.example.readtrack.database.BookDao;
import com.example.readtrack.database.BookRoomDatabase;
import com.example.readtrack.model.Book;
import com.example.readtrack.model.BooksResponse;
import com.example.readtrack.util.DataEncryptionUtil;

import java.util.List;

public class BooksLocalDataSource extends BaseBooksLocalDataSource {
    private final BookDao bookDao;
    private final DataEncryptionUtil dataEncryptionUtil;

    public BooksLocalDataSource(BookRoomDatabase booksRoomDatabase, DataEncryptionUtil dataEncryptionUtil) {
        this.bookDao = booksRoomDatabase.bookDao();
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
    public void delete(Book book) {
        BookRoomDatabase.databaseWriteExecutor.execute(() -> {
           bookDao.delete(book);
        });
    }

    @Override
    public void insertBook(BooksResponse booksResponse) {
        BookRoomDatabase.databaseWriteExecutor.execute(() -> {
            List<Book> allBooks = bookDao.getAll();
            List<Book> bookList = booksResponse.getItems();

            if (bookList != null) {
                for (Book book : allBooks) {
                    if (bookList.contains(book)) {
                        bookList.set(bookList.indexOf(book), book);
                    }
                }
                List<Long> insertedBooksIds = bookDao.insertAll(bookList);
                booksCallback.onSuccessFromLocal(booksResponse);
            }
        });
    }

}


