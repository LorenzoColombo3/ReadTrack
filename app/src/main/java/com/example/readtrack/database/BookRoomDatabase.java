package com.example.readtrack.database;

import static com.example.readtrack.util.Constants.BOOK_DATABASE_NAME;
import static com.example.readtrack.util.Constants.DATABASE_VERSION;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.readtrack.model.Book;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//@Database(entities = {Book.class}, version = DATABASE_VERSION)
public abstract class BookRoomDatabase extends RoomDatabase {

    public abstract BookDao bookDao();

    private static volatile BookRoomDatabase INSTANCE;

    private static final int NUMBER_OF_THREADS = Runtime.getRuntime().availableProcessors();
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static BookRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (BookRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            BookRoomDatabase.class, BOOK_DATABASE_NAME).build();
                }
            }
        }
        return INSTANCE;
    }
}


