package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MessageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
    }

    public void readInputMessage(View v){
        TextInputEditText messageInput = findViewById(R.id.inputMessage);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Submit?");
        builder.setMessage("Are you sure you want to submit?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Perform action when "Yes" button is clicked
                String data = messageInput.getText().toString().trim();
                if(data.length()<=10){
                    Toast.makeText(MessageActivity.this, "SOS Message too short!", Toast.LENGTH_SHORT).show();
                }else {
                    try {
                        File file = new File(getFilesDir(), "MessageFile.txt");
                        FileOutputStream outputStream = new FileOutputStream(file);
                        outputStream.write(data.getBytes());
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(MessageActivity.this, "Message Saved", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MessageActivity.this, ThankYouActivity.class);
                    startActivity(intent);
                }
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Perform action when "No" button is clicked
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();


    }
}