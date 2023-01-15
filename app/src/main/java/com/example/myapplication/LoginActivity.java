package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class LoginActivity extends AppCompatActivity {
    TextInputEditText mEmailFieldLogin, mPasswordFieldLogin;
//    private SharedPreferences mPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmailFieldLogin = findViewById(R.id.userEmail);
        mPasswordFieldLogin = findViewById(R.id.userPassword);

    }

    public void redirectSignup(View view) {
        Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
        startActivity(intent);
    }

    public void redirectHome() {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    public void onLoginClicked(View view) {
        String email = mEmailFieldLogin.getText().toString();
        String password = mPasswordFieldLogin.getText().toString();

        if (email.isEmpty()) {
            Toast.makeText(this, "Email field is required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.isEmpty()) {
            Toast.makeText(this, "Password field is required", Toast.LENGTH_SHORT).show();
            return;
        }

        File file = new File(getFilesDir(), "signup_info.txt");
        if (!file.exists()) {
            Toast.makeText(this, "Signup information not found", Toast.LENGTH_SHORT).show();
            return;
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("Email: ") && line.substring(7).equals(email)) {
                    while ((line = br.readLine()) != null) {
                        if (line.startsWith("Password: ") && line.substring(10).equals(password)) {
                            Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
                            // Save login information in SharedPreferences
                            SharedPreferences.Editor editor = getSharedPreferences("loginPreference",Context.MODE_PRIVATE).edit();
                            editor.putBoolean("is_logged_in", true);
                            editor.apply();
                            redirectHome();
                            return;
                        }
                    }
                    Toast.makeText(this, "Incorrect password", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            Toast.makeText(this, "Email not found", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, "Error reading signup info", Toast.LENGTH_SHORT).show();
        }
    }
}