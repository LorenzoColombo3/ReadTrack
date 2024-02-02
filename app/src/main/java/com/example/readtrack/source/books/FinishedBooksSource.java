package com.example.readtrack.source.books;

import static com.example.readtrack.util.Constants.FAVOURITES_BOOKS;
import static com.example.readtrack.util.Constants.FIREBASE_REALTIME_DATABASE;
import static com.example.readtrack.util.Constants.FIREBASE_USERS_COLLECTION;
import static com.example.readtrack.util.Constants.IMG;
import static com.example.readtrack.util.Constants.NUMPAGES;
import static com.example.readtrack.util.Constants.PAGE;
import static com.example.readtrack.util.Constants.READING_BOOKS;
import static com.example.readtrack.util.Constants.RED_BOOKS;
import static com.example.readtrack.util.Constants.TITLE;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.readtrack.model.Books;
import com.example.readtrack.util.SharedPreferencesUtil;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FinishedBooksSource extends BaseFinishedBooksSource{

    private final DatabaseReference databaseReference;
    private final SharedPreferencesUtil sharedPreferencesUtil;

    public FinishedBooksSource(SharedPreferencesUtil sharedPreferencesUtil) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(FIREBASE_REALTIME_DATABASE);
        databaseReference = firebaseDatabase.getReference().getRef();
        this.sharedPreferencesUtil = sharedPreferencesUtil;
    }
    @Override
    public void removeUserFinishedBook(String idBook, String idToken) {
        DatabaseReference userBooksRef = databaseReference.child(FIREBASE_USERS_COLLECTION).child(idToken).child(READING_BOOKS);
        userBooksRef.orderByKey().equalTo(idBook).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                        childSnapshot.getRef().removeValue();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Gestisci l'errore
            }
        });
    }

    @Override
    public void addUserFinishedBook(String idBook, String imgLink, String idToken) {
        DatabaseReference userBooksRef = databaseReference.child(FIREBASE_USERS_COLLECTION).child(idToken).child(READING_BOOKS).child(idBook).child(imgLink);
    }

    @Override
    public void getUserFinishedBooks(String idToken) {
        databaseReference.child(FIREBASE_USERS_COLLECTION).child(idToken).
                child(READING_BOOKS).get().addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        booksResponseCallback.onFailureFromRemote(task.getException());
                    } else {
                        DataSnapshot dataSnapshot = task.getResult();
                        List<Books> booksList = new ArrayList<>();
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot bookSnapshot : dataSnapshot.getChildren()) {
                                String bookId = bookSnapshot.getKey();
                                String bookData = String.valueOf(bookSnapshot.getValue());
                                booksList.add(new Books(bookId, bookData, null, 0, 0));
                            }
                            booksResponseCallback.onSuccessFromRemoteDatabase(booksList, RED_BOOKS);
                        } else {
                            // Nessun dato trovato
                            booksResponseCallback.onSuccessFromRemoteDatabase(booksList, RED_BOOKS);
                        }
                    }
                });
    }
}
