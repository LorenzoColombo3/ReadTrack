package com.example.readtrack.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.readtrack.model.Book;

import java.util.List;

@Dao
public interface BookDao {
    @Query("SELECT * FROM Book ORDER BY id ")
    List<Book> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long>  insertAll(List<Book> newsList);

    @Delete
    void delete(Book book);
}
