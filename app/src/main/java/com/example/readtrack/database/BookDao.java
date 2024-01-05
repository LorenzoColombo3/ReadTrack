package com.example.readtrack.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.readtrack.model.Books;

import java.util.List;

@Dao
public interface BookDao {
    @Query("SELECT * FROM BOOKS ORDER BY id ")
    List<Books> getAll();

    @Insert
    void insertBook(Books book);
}
