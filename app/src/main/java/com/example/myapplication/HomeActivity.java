package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

public class HomeActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1;
    String[] PERMISSIONS = {
            Manifest.permission.SEND_SMS,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private static boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_REQUEST_CODE);
        } else {
            flag = true;
        }
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
        Toast.makeText(this, "Sending alert!!!", Toast.LENGTH_SHORT).show();
    }
}