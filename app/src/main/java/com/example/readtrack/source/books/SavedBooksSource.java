package com.example.readtrack.source.books;

import static com.example.readtrack.util.Constants.FIREBASE_REALTIME_DATABASE;
import static com.example.readtrack.util.Constants.FIREBASE_USERS_COLLECTION;
import static com.example.readtrack.util.Constants.IMG;
import static com.example.readtrack.util.Constants.TITLE;
import static com.example.readtrack.util.Constants.WANT_TO_READ;

import androidx.annotation.NonNull;

import com.example.readtrack.model.Books;
import com.example.readtrack.util.OnCheckListener;
import com.example.readtrack.util.SharedPreferencesUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SavedBooksSource extends BaseSavedBooksSource{

    private final DatabaseReference databaseReference;
    private final SharedPreferencesUtil sharedPreferencesUtil;

    public SavedBooksSource(SharedPreferencesUtil sharedPreferencesUtil){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(FIREBASE_REALTIME_DATABASE);
        databaseReference = firebaseDatabase.getReference().getRef();
        this.sharedPreferencesUtil = sharedPreferencesUtil;
    }

    @Override
    public void getUserSavedBooks(String idToken) {
        databaseReference.child(FIREBASE_USERS_COLLECTION).child(idToken).
                child(WANT_TO_READ).get().addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        booksResponseCallback.onFailureFromRemote(task.getException());
                    } else {
                        DataSnapshot dataSnapshot = task.getResult();
                        String bookId;
                        String bookCover;
                        String bookTitle;
                        if (dataSnapshot.exists()) {
                            List<Books> booksList = new ArrayList<>();
                            for (DataSnapshot bookSnapshot : dataSnapshot.getChildren()) {
                                bookId = bookSnapshot.getKey();
                                bookTitle = bookSnapshot.child(TITLE).getValue(String.class);
                                if(!bookSnapshot.child(IMG).getValue(String.class).equals(""))
                                      bookCover = bookSnapshot.child(IMG).getValue(String.class).substring(5);
                                else
                                    bookCover = bookSnapshot.child(IMG).getValue(String.class);
                                booksList.add(new Books(bookId, "http"+bookCover, bookTitle, 0, 0));
                            }
                            booksResponseCallback.onSuccessFromRemoteDatabase(booksList, WANT_TO_READ);
                        } else {

                            booksResponseCallback.onFailureFromRemote(new Exception("libri non trovait"));
                        }
                    }
                });


    }

    @Override
    public void isSavedBook(String idBook, String idToken, OnCheckListener listener) {
        DatabaseReference userBooksRef = databaseReference.child(FIREBASE_USERS_COLLECTION).child(idToken).child(WANT_TO_READ);
        userBooksRef.orderByKey().equalTo(idBook).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean isSaved = snapshot.exists();
                listener.onCheckResult(isSaved);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    public void removeSavedBook(String idBook, String idToken) {
        DatabaseReference userBooksRef = databaseReference.child(FIREBASE_USERS_COLLECTION).child(idToken).child(WANT_TO_READ);
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
    public void addSavedBook(String idBook, String imageLink, String title, String idToken) {
        databaseReference.child(FIREBASE_USERS_COLLECTION).child(idToken).child(WANT_TO_READ).child(idBook).child(IMG).setValue(imageLink);
        databaseReference.child(FIREBASE_USERS_COLLECTION).child(idToken).child(WANT_TO_READ).child(idBook).child(TITLE).setValue(title);
    }
}
