package com.example.readtrack.source.books;

import static com.example.readtrack.util.Constants.FAVOURITES_BOOKS;
import static com.example.readtrack.util.Constants.FIREBASE_REALTIME_DATABASE;
import static com.example.readtrack.util.Constants.FIREBASE_USERS_COLLECTION;
import static com.example.readtrack.util.Constants.IMG;
import static com.example.readtrack.util.Constants.PAGE;
import static com.example.readtrack.util.Constants.READING_BOOKS;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.readtrack.source.user.UserDataRemoteDataSource;
import com.example.readtrack.util.SharedPreferencesUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ReadingBooksSource extends BaseReadingBooksSource{

    private static final String TAG = UserDataRemoteDataSource.class.getSimpleName();

    private final DatabaseReference databaseReference;
    private final SharedPreferencesUtil sharedPreferencesUtil;

    public ReadingBooksSource(SharedPreferencesUtil sharedPreferencesUtil) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(FIREBASE_REALTIME_DATABASE);
        databaseReference = firebaseDatabase.getReference().getRef();
        this.sharedPreferencesUtil = sharedPreferencesUtil;
    }
    @Override
    public void getUserReadingBooks(String idToken) {
        Log.d("start", "UserData");
        databaseReference.child(FIREBASE_USERS_COLLECTION).child(idToken).
                child(READING_BOOKS).get().addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.d(TAG, "Error getting data", task.getException());
                        booksResponseCallback.onFailureFromRemote(task.getException());
                    } else {
                        DataSnapshot dataSnapshot = task.getResult();
                        if (dataSnapshot.exists()) {
                            // Ottieni i dati come HashMap<String, String>
                            HashMap<String, String> favoritesMap = new HashMap<>();
                            for (DataSnapshot bookSnapshot : dataSnapshot.getChildren()) {
                                String bookId = bookSnapshot.getKey();
                                String bookData = bookSnapshot.child(IMG).getValue(String.class);
                                favoritesMap.put(bookId, bookData);
                            }
                            // Passa l'HashMap al callback di successo
                            booksResponseCallback.onSuccessFromRemoteDatabase(favoritesMap, READING_BOOKS);
                        } else {
                            // Nessun dato trovato
                            booksResponseCallback.onSuccessFromRemoteDatabase(new HashMap<>(),READING_BOOKS);
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
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Gestisci l'errore
            }
        });
    }

    @Override
    public void updateReadingBook(String idBook, int page, String imgLink, String idToken){
        DatabaseReference userBooksRef = databaseReference.child(FIREBASE_USERS_COLLECTION).child(idToken).child(READING_BOOKS);
        userBooksRef.orderByKey().equalTo(idBook).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                        childSnapshot.getRef().child(PAGE).setValue(page);
                    }
                }else{
                    userBooksRef.child(idBook).child(PAGE).setValue(page);
                    userBooksRef.child(idBook).child(IMG).setValue(imgLink);
                }
                getSegnalibro(idBook, idToken);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Gestisci l'errore
            }
        });
    }

    @Override
    public void getSegnalibro(String idBook, String idToken) {
        databaseReference.child(FIREBASE_USERS_COLLECTION).child(idToken).
                child(READING_BOOKS).child(idBook).child(PAGE).get().addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.d(TAG, "Error getting data", task.getException());
                        booksResponseCallback.onFailureFromRemote(task.getException());
                    }
                    else {
                        DataSnapshot dataSnapshot = task.getResult();
                        if (dataSnapshot.exists()) {
                            // Ottieni i dati come HashMap<String, String>
                            HashMap<String, String> readMap = new HashMap<>();
                            readMap.put(dataSnapshot.getKey(), String.valueOf( dataSnapshot.getValue()));
                            booksResponseCallback.onSuccessFromRemoteMarkReading(readMap);
                        } else {
                            booksResponseCallback.onSuccessFromRemoteMarkReading(new HashMap<>());
                        }
                    }
                });
    }
}
