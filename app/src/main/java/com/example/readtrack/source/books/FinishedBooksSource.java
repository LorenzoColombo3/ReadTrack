package com.example.readtrack.source.books;

import static com.example.readtrack.util.Constants.FIREBASE_REALTIME_DATABASE;
import static com.example.readtrack.util.Constants.FIREBASE_USERS_COLLECTION;
import static com.example.readtrack.util.Constants.FINISHED_BOOKS;

import androidx.annotation.NonNull;

import com.example.readtrack.model.Book;
import com.example.readtrack.util.OnCheckListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FinishedBooksSource extends BaseFinishedBooksSource{

    private final DatabaseReference databaseReference;

    public FinishedBooksSource() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(FIREBASE_REALTIME_DATABASE);
        databaseReference = firebaseDatabase.getReference().getRef();
    }
    @Override
    public void removeUserFinishedBook(String idBook, String idToken) {
        DatabaseReference userBooksRef = databaseReference.child(FIREBASE_USERS_COLLECTION).child(idToken).child(FINISHED_BOOKS);
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
            }
        });
    }

    @Override
    public void addUserFinishedBook(String idBook, String imgLink, String idToken) {
        databaseReference.child(FIREBASE_USERS_COLLECTION).child(idToken).child(FINISHED_BOOKS).child(idBook).setValue(imgLink);
    }

    @Override
    public void getUserFinishedBooks(String idToken) {
        databaseReference.child(FIREBASE_USERS_COLLECTION).child(idToken).
                child(FINISHED_BOOKS).get().addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        booksResponseCallback.onFailureFromRemote(task.getException());
                    } else {
                        DataSnapshot dataSnapshot = task.getResult();
                        List<Book> bookList = new ArrayList<>();
                        String bookId;
                        String bookData;
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot bookSnapshot : dataSnapshot.getChildren()) {
                                bookId = bookSnapshot.getKey();
                                if(!bookSnapshot.getValue().equals(""))
                                     bookData = bookSnapshot.getValue(String.class).substring(5);
                                else
                                    bookData = bookSnapshot.getValue(String.class);
                                bookList.add(new Book(bookId, "http"+bookData, null, 0, 0));
                            }
                            booksResponseCallback.onSuccessFromRemoteDatabase(bookList, FINISHED_BOOKS);
                        } else {
                            booksResponseCallback.onSuccessFromRemoteDatabase(bookList, FINISHED_BOOKS);
                        }
                    }
                });
    }

    @Override
    public void isFinishedBook(String idBook, String idToken, OnCheckListener listener) {
        DatabaseReference userBooksRef = databaseReference.child(FIREBASE_USERS_COLLECTION).child(idToken).child(FINISHED_BOOKS);
        userBooksRef.orderByKey().equalTo(idBook).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean isFinished = snapshot.exists();
                listener.onCheckResult(isFinished);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}
