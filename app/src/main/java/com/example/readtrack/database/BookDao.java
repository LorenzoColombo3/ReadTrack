package com.example.readtrack.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.example.readtrack.model.Book;

import java.util.List;

@Dao
public interface BookDao {
    /*@Query("SELECT * FROM book ORDER BY id ")
    List<Book> getAll();

    @Insert
    void insertBook(Book book);*/
}
