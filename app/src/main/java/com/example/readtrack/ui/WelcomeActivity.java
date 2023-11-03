package com.example.readtrack.ui;

import static com.example.readtrack.util.Constants.EMAIL_ADDRESS;
import static com.example.readtrack.util.Constants.ENCRYPTED_DATA_FILE_NAME;
import static com.example.readtrack.util.Constants.ENCRYPTED_SHARED_PREFERENCES_FILE_NAME;
import static com.example.readtrack.util.Constants.PASSWORD;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.readtrack.R;
import com.example.readtrack.util.DataEncryptionUtil;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import org.apache.commons.validator.routines.EmailValidator;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class WelcomeActivity extends AppCompatActivity {
    private static final String TAG = com.example.readtrack.ui.WelcomeActivity.class.getSimpleName();
    private TextInputLayout textInputLayoutEmail;
    private TextInputLayout textInputLayoutPassword;
    private DataEncryptionUtil dataEncryptionUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }/*
        textInputLayoutEmail = findViewById(R.id.text_input_layout_email);
        textInputLayoutPassword = findViewById(R.id.text_input_layout_password);
        final Button buttonLogin = findViewById(R.id.button_login);

        dataEncryptionUtil = new DataEncryptionUtil(this);

        try {
            Log.d(TAG, "Email address from encrypted SharedPref: " + dataEncryptionUtil.
                    readSecretDataWithEncryptedSharedPreferences(
                            ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, EMAIL_ADDRESS));
            Log.d(TAG, "Password from encrypted SharedPref: " + dataEncryptionUtil.
                    readSecretDataWithEncryptedSharedPreferences(
                            ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, PASSWORD));
            Log.d(TAG, "Login data from encrypted file: " + dataEncryptionUtil.
                    readSecretDataOnFile(ENCRYPTED_DATA_FILE_NAME));
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }

        buttonLogin.setOnClickListener(v -> {

            String email = textInputLayoutEmail.getEditText().getText().toString();
            String password = textInputLayoutPassword.getEditText().getText().toString();

            // Start login if email and password are ok
            if (isEmailOk(email) & isPasswordOk(password)) {
                Log.d(TAG, "Email and password are ok");
                saveLoginData(email, password);
            } else {
                Snackbar.make(findViewById(android.R.id.content),
                        R.string.check_login_data_message, Snackbar.LENGTH_SHORT).show();
                        Log.d(TAG, "Email and password are not ok");
            }
        });
    }
    private boolean isEmailOk(String email) {
        // Check if the email is valid through the use of this library:
        // https://commons.apache.org/proper/commons-validator/
        if (!EmailValidator.getInstance().isValid((email))) {
            textInputLayoutEmail.setError(getString(R.string.error_email));
            return false;
        } else {
            textInputLayoutEmail.setError(null);
            return true;
        }
    }
    private boolean isPasswordOk(String password) {
        // Check if the password length is correct
        if (password.isEmpty()) {
            textInputLayoutPassword.setError(getString(R.string.error_password));
            return false;
        } else {
            textInputLayoutPassword.setError(null);
            return true;
        }
    }

    private void saveLoginData(String email, String password) {
        try {
            dataEncryptionUtil.writeSecretDataWithEncryptedSharedPreferences(
                    ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, EMAIL_ADDRESS, email);
            dataEncryptionUtil.writeSecretDataWithEncryptedSharedPreferences(
                    ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, PASSWORD, password);
            dataEncryptionUtil.writeSecreteDataOnFile(ENCRYPTED_DATA_FILE_NAME,
                    email.concat(":").concat(password));
            Log.d(TAG, "login saved");
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
            Log.d(TAG, "login failed");
        }
    }*/
}
