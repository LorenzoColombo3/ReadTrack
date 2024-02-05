package com.example.readtrack.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import androidx.room.Update;

import com.example.readtrack.model.Books;

import java.util.List;

@Dao
public interface BookDao {
    @Query("SELECT * FROM BOOKS ORDER BY id ")
    List<Books> getAll();

    @Insert
    void insertBook(Books book);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long>  insertAll(List<Books> newsList);

    @Update
    int updateSingleBook(Books book);

    @Delete
    void delete(Books book);
}
