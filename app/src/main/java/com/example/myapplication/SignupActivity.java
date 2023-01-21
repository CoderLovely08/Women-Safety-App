package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class SignupActivity extends AppCompatActivity {

    TextInputEditText mNameField, mEmailField, mPasswordField, mConfirmPasswordField;
    private SharedPreferences mPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mPreferences = getSharedPreferences("loginPreference",Context.MODE_PRIVATE);

        mEmailField = findViewById(R.id.userEmail);
        mNameField = findViewById(R.id.userName);
        mPasswordField = findViewById(R.id.userPassword);
        mConfirmPasswordField = findViewById(R.id.userConfirmPassword);

        // Check if the user is already logged in
        if (mPreferences.getBoolean("is_logged_in", false)) {
            redirectHome();
        }

    }

    private void redirectHome() {
        Intent intent = new Intent(SignupActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    public void onSignupClicked(View view) {
        String name = mNameField.getText().toString().trim();
        String email = mEmailField.getText().toString().trim();
        String password = mPasswordField.getText().toString().trim();
        String confirmPassword = mConfirmPasswordField.getText().toString().trim();

        if (name.isEmpty()) {
            Toast.makeText(this, "Name field is required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (email.isEmpty()) {
            Toast.makeText(this, "Email field is required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.isEmpty()) {
            Toast.makeText(this, "Password field is required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }
        DataBaseHelper dataBaseHelper = new DataBaseHelper(this);
        try {
            boolean success = dataBaseHelper.addUser(new User(-1, name, email, password));;
            if(success) {
                Toast.makeText(this, "Signup successful", Toast.LENGTH_SHORT).show();
                redirectLogin();
            }
            else Toast.makeText(this, "Email already exists", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Toast.makeText(this,"Error, please try again!", Toast.LENGTH_SHORT).show();
        }


//        File file = new File(getFilesDir(), "signup_info.txt");
//        try (FileOutputStream fos = new FileOutputStream(file, true)) {
//            String info = "Name: " + name + "\nEmail: " + email + "\nPassword: " + password + "\n\n";
//            fos.write(info.getBytes());
//            Toast.makeText(this, "Signup successful", Toast.LENGTH_SHORT).show();
//            redirectLogin();
//        } catch (IOException e) {
//            Toast.makeText(this, "Error saving signup info", Toast.LENGTH_SHORT).show();
//        }
    }

    public void redirectLogin(View view) {
        redirectLogin();
    }

    public void redirectLogin() {
        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}