package com.example.readtrack.source.books;

import static com.example.readtrack.util.Constants.FAVOURITES_BOOKS;
import static com.example.readtrack.util.Constants.FIREBASE_REALTIME_DATABASE;
import static com.example.readtrack.util.Constants.FIREBASE_USERS_COLLECTION;

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

public class FavoriteBooksSource extends BaseFavoriteBooksSource {

    private static final String TAG = UserDataRemoteDataSource.class.getSimpleName();

    private final DatabaseReference databaseReference;

    public FavoriteBooksSource() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(FIREBASE_REALTIME_DATABASE);
        databaseReference = firebaseDatabase.getReference().getRef();
    }
    @Override
    public void getUserFavBooks(String idToken) {
        databaseReference.child(FIREBASE_USERS_COLLECTION).child(idToken).
                child(FAVOURITES_BOOKS).get().addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        booksResponseCallback.onFailureFromRemote(task.getException());
                    } else {
                        DataSnapshot dataSnapshot = task.getResult();
                        if (dataSnapshot.exists()) {
                            List<Book> bookList = new ArrayList<>();
                            String bookId;
                            String bookCover;
                            for (DataSnapshot bookSnapshot : dataSnapshot.getChildren()) {
                                 bookId = bookSnapshot.getKey();
                                if(!bookSnapshot.getValue(String.class).equals(""))
                                    bookCover = bookSnapshot.getValue(String.class).substring(5);
                                else
                                    bookCover = bookSnapshot.getValue(String.class);
                                bookList.add(new Book(bookId, "http"+bookCover, null, 0, 0));
                            }
                            booksResponseCallback.onSuccessFromRemoteDatabase(bookList, FAVOURITES_BOOKS);
                        } else {
                            booksResponseCallback.onFailureFromRemote(new Exception("libri non trovait"));
                        }
                    }
                });

    }

    @Override
    public void isFavouriteBook(String idBook, String idToken, OnCheckListener listener) {
        DatabaseReference userBooksRef = databaseReference.child(FIREBASE_USERS_COLLECTION).child(idToken).child(FAVOURITES_BOOKS);
        userBooksRef.orderByKey().equalTo(idBook).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean isFavourite = snapshot.exists();
                listener.onCheckResult(isFavourite);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    public void removeFavouriteBook(String idBook, String idToken) {
        DatabaseReference userBooksRef = databaseReference.child(FIREBASE_USERS_COLLECTION).child(idToken).child(FAVOURITES_BOOKS);
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
    public void addFavouriteBook(String idBook, String imageLink, String idToken){
             databaseReference.child(FIREBASE_USERS_COLLECTION).child(idToken).child(FAVOURITES_BOOKS).child(idBook).setValue(imageLink);
    }
}
