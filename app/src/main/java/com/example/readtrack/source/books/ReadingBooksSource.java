package com.example.readtrack.source.books;

import static com.example.readtrack.util.Constants.FIREBASE_REALTIME_DATABASE;
import static com.example.readtrack.util.Constants.FIREBASE_USERS_COLLECTION;
import static com.example.readtrack.util.Constants.IMG;
import static com.example.readtrack.util.Constants.NUMPAGES;
import static com.example.readtrack.util.Constants.PAGE;
import static com.example.readtrack.util.Constants.READING_BOOKS;
import static com.example.readtrack.util.Constants.TITLE;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.readtrack.model.Book;
import com.example.readtrack.source.user.UserDataRemoteDataSource;
import com.example.readtrack.util.OnCheckListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ReadingBooksSource extends BaseReadingBooksSource{

    private static final String TAG = UserDataRemoteDataSource.class.getSimpleName();

    private final DatabaseReference databaseReference;

    public ReadingBooksSource() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(FIREBASE_REALTIME_DATABASE);
        databaseReference = firebaseDatabase.getReference().getRef();
    }

    @Override
    public void getUserReadingBooks(String idToken) {
        databaseReference.child(FIREBASE_USERS_COLLECTION).child(idToken).
                child(READING_BOOKS).get().addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.d(TAG, "Error getting data", task.getException());
                        booksResponseCallback.onFailureFromRemote(task.getException());
                    } else {
                        DataSnapshot dataSnapshot = task.getResult();
                        List<Book> bookList = new ArrayList<>();
                        if (dataSnapshot.exists()) {
                            String bookCover;
                            String bookTitle;
                            int numPages;
                            int bookMarker;
                            for (DataSnapshot bookSnapshot : dataSnapshot.getChildren()) {
                                String bookId = bookSnapshot.getKey();
                                if(!bookSnapshot.child(IMG).getValue(String.class).equals(""))
                                     bookCover = bookSnapshot.child(IMG).getValue(String.class).substring(5);
                                else
                                     bookCover = bookSnapshot.child(IMG).getValue(String.class);
                                 bookTitle = bookSnapshot.child(TITLE).getValue(String.class);
                                 numPages = bookSnapshot.child(NUMPAGES).getValue(Integer.class);
                                 bookMarker= bookSnapshot.child(PAGE).getValue(Integer.class);
                                 bookList.add(new Book(bookId, "http"+bookCover, bookTitle, numPages, bookMarker));
                            }
                            booksResponseCallback.onSuccessFromRemoteDatabase(bookList, READING_BOOKS);
                        } else {
                            booksResponseCallback.onSuccessFromRemoteDatabase(bookList, READING_BOOKS);
                        }
                    }
                });
    }

    @Override
    public void removeReadingBook(String idBook, String idToken) {
        DatabaseReference userBooksRef = databaseReference.child(FIREBASE_USERS_COLLECTION).child(idToken).child(READING_BOOKS);
        userBooksRef.orderByKey().equalTo(idBook).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                        childSnapshot.getRef().removeValue();
                    }
                    booksResponseCallback.onSuccessFromDeletion(new Book(idBook, null, null, 0, 0));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    public void updateReadingBook(String idBook, int page, String imgLink, String title, int numPages,  String idToken){
        DatabaseReference userBooksRef = databaseReference.child(FIREBASE_USERS_COLLECTION).child(idToken).child(READING_BOOKS);
        userBooksRef.orderByKey().equalTo(idBook).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Book> bookList = new ArrayList<>();
                if (snapshot.exists()) {
                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                        childSnapshot.getRef().child(PAGE).setValue(page);
                    }
                }else{
                    userBooksRef.child(idBook).child(PAGE).setValue(page);
                    userBooksRef.child(idBook).child(IMG).setValue(imgLink);
                    userBooksRef.child(idBook).child(TITLE).setValue(title);
                    userBooksRef.child(idBook).child(NUMPAGES).setValue(numPages);
                }
                getSegnalibro(idBook, idToken);
                bookList.add(new Book(idBook, imgLink, title, numPages, page));
                booksResponseCallback.onSuccessFromRemoteWriting(bookList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    public void getSegnalibro(String idBook, String idToken) {
        databaseReference.child(FIREBASE_USERS_COLLECTION).child(idToken).
                child(READING_BOOKS).child(idBook).get().addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.d(TAG, "Error getting data", task.getException());
                        booksResponseCallback.onFailureFromRemote(task.getException());
                    }
                    else {
                        DataSnapshot dataSnapshot = task.getResult();
                        List<Book> bookList = new ArrayList<>();
                        if (dataSnapshot.exists()) {
                            String bookId = dataSnapshot.getKey();
                            int bookMarker = dataSnapshot.child(PAGE).getValue(Integer.class);
                            bookList.add(new Book(bookId, null, null, 0, bookMarker));
                            booksResponseCallback.onSuccessFromRemoteMarkReading(bookList);
                        } else {
                            booksResponseCallback.onFailureFromRemote(new Exception("Libro non esistente"));
                        }
                    }
                });
    }

    @Override
    public void isReadingBook(String idBook, String idToken, OnCheckListener listener) {
        DatabaseReference userBooksRef = databaseReference.child(FIREBASE_USERS_COLLECTION).child(idToken).child(READING_BOOKS);
        userBooksRef.orderByKey().equalTo(idBook).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean isReading = snapshot.exists();
                listener.onCheckResult(isReading);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

}
