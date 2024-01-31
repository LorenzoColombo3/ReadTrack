package com.example.readtrack.util;

public class Constants {
    // Constants for EncryptedSharedPreferences
    public static final String ENCRYPTED_SHARED_PREFERENCES_FILE_NAME = "it.unimib.worldnews.encrypted_preferences";
    public static final String EMAIL_ADDRESS = "email_address";
    public static final String PASSWORD = "password";
    public static final String ID_TOKEN = "google_token";
    // Constants for encrypted files
    public static final String ENCRYPTED_DATA_FILE_NAME = "com.example.readtrack.encrypted_file.txt";
    public static final String BOOKS_API_TEST_JSON_FILE = "testBooks.json";
    public static final String BOOKS_API_BASE_URL = "https://www.googleapis.com/books/";
    public static final int DATABASE_VERSION = 1;

    public static final String RETROFIT_ERROR = "retrofit_error";
    public static final String API_KEY_ERROR = "api_key_error";
    public static final String UNEXPECTED_ERROR = "unexpected_error";

    public static final String TOP_HEADLINES_QUERY_PARAMETER = "q";
    public static final String TOP_HEADLINES_PAGE_SIZE_PARAMETER = "pageSize";
    public static final String TOP_HEADLINES_PAGE_PARAMETER = "startIndex";
    public static final int TOP_HEADLINES_PAGE_SIZE_VALUE = 10;
    public static final String BOOK_DATABASE_NAME = "book_db";
    // Constants for managing errors
    public static final String INVALID_USER_ERROR = "invalidUserError";
    public static final String INVALID_CREDENTIALS_ERROR = "invalidCredentials";
    public static final String USER_COLLISION_ERROR = "userCollisionError";
    public static final String WEAK_PASSWORD_ERROR = "passwordIsWeak";

    public static final int MINIMUM_PASSWORD_LENGTH = 6;

    // Constants for Firebase Realtime Database
    public static final String FIREBASE_REALTIME_DATABASE = "https://readtracj-default-rtdb.europe-west1.firebasedatabase.app/";
    public static final String FIREBASE_USERS_COLLECTION = "users";
    public static final String FAVOURITES_BOOKS = "preferiti";
    public static final String READING_BOOKS = "reading";
    public static final String RED_BOOKS = "read";
    public static final String WANT_TO_READ="wantRead";
    public static final String PAGE="page";
    public static final String IMG="img";
    public static final int PICK_PROFILE_IMAGE_REQUEST_CODE=1;
}
