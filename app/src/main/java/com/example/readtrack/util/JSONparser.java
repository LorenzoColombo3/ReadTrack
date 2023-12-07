package com.example.readtrack.util;

import static com.example.readtrack.util.Constants.BOOKS_API_TEST_JSON_FILE;

import android.content.Context;

import com.example.readtrack.model.BooksApiResponse;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/*
import android.app.Application;
import android.content.Context;

import com.example.readtrack.model.BooksApiResponse;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class JSONparser {
    private final Context context;

    public JSONparser(Application application) {
        this.context = application.getApplicationContext();
    }
    public BooksApiResponse parseJSONFileWithGSon(String fileName) throws IOException {
        InputStream inputStream = context.getAssets().open(fileName);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        return new Gson().fromJson(bufferedReader, BooksApiResponse.class);
    }
}*/
public class JSONparser {
    private final Context context;
    private final Executor executor = Executors.newSingleThreadExecutor();

    public JSONparser(Context context) {
        this.context = context;
    }

    public BooksApiResponse parseJSONFileWithGSon(String source, String url) {
        try {
            if (source.equals("Local")) {
                // Load from local file
                InputStream inputStream = context.getAssets().open(BOOKS_API_TEST_JSON_FILE);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                return new Gson().fromJson(bufferedReader, BooksApiResponse.class);
            } else if (source.equals("Remote")) {
                // Load from internet
                return loadFromInternet(url);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private BooksApiResponse loadFromInternet(String url) {
        final BooksApiResponse[] result = {null};

        Thread thread = new Thread(() -> {
            try {
                URL internetUrl = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) internetUrl.openConnection();
                InputStreamReader reader = new InputStreamReader(connection.getInputStream());
                result[0] = new Gson().fromJson(reader, BooksApiResponse.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return result[0];
    }
}