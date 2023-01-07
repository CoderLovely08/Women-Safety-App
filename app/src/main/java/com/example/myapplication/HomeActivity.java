package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HomeActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1;
    String[] PERMISSIONS = {
            Manifest.permission.SEND_SMS,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CALL_PHONE
    };

    private static boolean flag = false;

    private static final String PREFERENCES_NAME = "MyPreferences";
    private static final String FIRST_LAUNCH = "FirstLaunch";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        SharedPreferences preferences = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
        boolean isFirstLaunch = preferences.getBoolean(FIRST_LAUNCH, true);

        if (isFirstLaunch) {
            showAlertDialogue();
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(FIRST_LAUNCH, false);
            editor.apply();
        }

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_REQUEST_CODE);
        } else {
            flag = true;
        }



    }

    private void showAlertDialogue() {
        String[] steps = {"Step 1: Click on Edit Alert", "Step 2: Select trusted contacts (max 5)", "Step 3: Enter an emergency message and hit submit."};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("How to Use the App")
                .setItems(steps, null)
                .setPositiveButton("Got it!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // your code here
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        flag = true;
                    } else {
                        Toast.makeText(this, " Permission Denied", Toast.LENGTH_SHORT).show();
                        System.exit(0);
                    }
                }
            }
        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }


    public void EditAlert(View v) {
        if (flag) {
            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            startActivity(intent);
        } else {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_REQUEST_CODE);
        }
    }

    public void SendAlert(View v) {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "ContactsData.txt");
        File messageFile = new File(getFilesDir(), "MessageFile.txt");
        if(!file.exists() || !messageFile.exists()){
            Toast.makeText(this, "Create a new alert by clicking Edit alert!", Toast.LENGTH_SHORT).show();
        }else sendSMSAlert();
    }

    private void phoneCallAlert(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        startActivity(intent);
    }


    public void sendSMSAlert(){
        ArrayList<String> contactNumbersForMessage = new ArrayList<>();
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "ContactsData.txt");

        String pattern = "(?<=Number:).*";
        Pattern p = Pattern.compile(pattern);

        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                Matcher m = p.matcher(line);
                if (m.find()) {
                    String phoneNumber = m.group();
                    System.out.println(phoneNumber);
                    contactNumbersForMessage.add(phoneNumber);
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Collections.reverse(contactNumbersForMessage);

        File messageFile = new File(getFilesDir(), "MessageFile.txt");
        String alertMessage;
        try {
            // Create a BufferedReader to read the file
            BufferedReader reader = new BufferedReader(new FileReader(messageFile));

            // Use a StringBuilder to concatenate the lines of the file
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();

            // Save the contents of the file to a string variable
            alertMessage = sb.toString();

            SmsManager smsManager = SmsManager.getDefault();
            int counter = 4;
            for (String contactNumber : contactNumbersForMessage) {
                if (counter-- != 0) {
                    smsManager.sendTextMessage(contactNumber, null, alertMessage, null, null);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        Toast.makeText(this, "Sending alert!!!", Toast.LENGTH_SHORT).show();
        phoneCallAlert(contactNumbersForMessage.get(0));
    }
}