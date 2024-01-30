package com.example.readtrack.source.user;

import static com.example.readtrack.util.Constants.FAVOURITES_BOOKS;
import static com.example.readtrack.util.Constants.FIREBASE_REALTIME_DATABASE;
import static com.example.readtrack.util.Constants.FIREBASE_USERS_COLLECTION;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.readtrack.model.User;
import com.example.readtrack.util.OnFavouriteCheckListener;
import com.example.readtrack.util.SharedPreferencesUtil;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserDataRemoteDataSource extends BaseUserDataRemoteDataSource {

    private static final String TAG = UserDataRemoteDataSource.class.getSimpleName();

    private final DatabaseReference databaseReference;
    private final SharedPreferencesUtil sharedPreferencesUtil;

    public UserDataRemoteDataSource(SharedPreferencesUtil sharedPreferencesUtil) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(FIREBASE_REALTIME_DATABASE);
        databaseReference = firebaseDatabase.getReference().getRef();
        this.sharedPreferencesUtil = sharedPreferencesUtil;
    }

    @Override
    public void saveUserData(User user) {
        Log.d("aaa","saveUserData");
        databaseReference.child(FIREBASE_USERS_COLLECTION).child(user.getIdToken()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Log.d(TAG, "User already present in Firebase Realtime Database");
                    userResponseCallback.onSuccessFromRemoteDatabase(user);
                } else {
                    Log.d(TAG, "User not present in Firebase Realtime Database");
                    databaseReference.child(FIREBASE_USERS_COLLECTION).child(user.getIdToken()).setValue(user)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    userResponseCallback.onSuccessFromRemoteDatabase(user);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    userResponseCallback.onFailureFromRemoteDatabase(e.getLocalizedMessage());
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("On cancelled","");
                userResponseCallback.onFailureFromRemoteDatabase(error.getMessage());
            }
        });
    }

    @Override
    public void isFavouriteBook(String idBook, String idToken, OnFavouriteCheckListener listener) {
        DatabaseReference userBooksRef = databaseReference.child(FIREBASE_USERS_COLLECTION).child(idToken).child(FAVOURITES_BOOKS);
        userBooksRef.orderByValue().equalTo(idBook).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean isFavourite = snapshot.exists();
                listener.onFavouriteCheckResult(isFavourite);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    @Override
    public void addFavouriteBook(String idBook, String idToken){
        databaseReference.child(FIREBASE_USERS_COLLECTION).child(idToken).child(FAVOURITES_BOOKS).push().setValue(idBook);
    }

    @Override
    public void removeFavouriteBook(String idBook, String idToken) {
        DatabaseReference userBooksRef = databaseReference.child(FIREBASE_USERS_COLLECTION).child(idToken).child(FAVOURITES_BOOKS);
        userBooksRef.orderByValue().equalTo(idBook).addListenerForSingleValueEvent(new ValueEventListener() {
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

    /*
        @Override
        public void saveUserFavBooks(String idBook, String idToken, OnSaveUserFavBooksListener listener) {
            DatabaseReference userBooksRef = databaseReference.child(FIREBASE_USERS_COLLECTION).child(idToken).child(FAVOURITES_BOOKS);

            userBooksRef.orderByValue().equalTo(idBook).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!snapshot.exists()) {
                        userBooksRef.push().setValue(idBook);
                        listener.onSaveSuccess(true);
                    } else {
                        // L'ID del libro esiste già nella lista
                        listener.onSaveSuccess(false); // Libro già presente
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Gestisci l'errore
                    listener.onSaveFailure(error.getMessage());
                }
            });
        }*/
    /*
    @Override
    public void saveUserFavBooks(String idBook, String idToken){
        databaseReference.child(FIREBASE_USERS_COLLECTION).child(idToken).child(FAVOURITES_BOOKS).push().setValue(idBook);
    }*/
//TODO da rifare getUserFavoriteNews
    @Override
    public void getUserFavoriteNews(String idToken) {
        /*databaseReference.child(FIREBASE_USERS_COLLECTION).child(idToken).
                child(FIREBASE_FAVORITE_NEWS_COLLECTION).get().addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.d(TAG, "Error getting data", task.getException());
                        userResponseCallback.onFailureFromRemoteDatabase(task.getException().getLocalizedMessage());
                    }
                    else {
                        Log.d(TAG, "Successful read: " + task.getResult().getValue());

                        List<News> newsList = new ArrayList<>();
                        for(DataSnapshot ds : task.getResult().getChildren()) {
                            News news = ds.getValue(News.class);
                            newsList.add(news);
                        }

                        userResponseCallback.onSuccessFromRemoteDatabase(newsList);
                    }
                });*/
    }
}
