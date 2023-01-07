package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    ListView list;

    List<ContactModel> model = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        model = getAllContacts();
        list = findViewById(R.id.list_view);
        CustomAdapter adapter = new CustomAdapter(this, model);
        list.setAdapter(adapter);


        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckBox checkBox = view.findViewById(R.id.checkbox);
                if (checkBox.isChecked()) {
                    // Checkbox is checked
                    Toast.makeText(MainActivity.this, "Checkbox is checked", Toast.LENGTH_SHORT).show();
                } else {
                    // Checkbox is not checked
                    Toast.makeText(MainActivity.this, "Checkbox is not checked", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private List<ContactModel> getAllContacts() {
        List<ContactModel> nameList = new ArrayList<>();
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, ContactsContract.Contacts.DISPLAY_NAME + " ASC");
        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                ContactModel mymodel;
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                if (cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur =
                            cr.query(
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                    null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                    new String[]{id},
                                    null);
                    while (pCur.moveToNext()) {
                        String phoneNo =
                                pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        mymodel = new ContactModel(name, phoneNo);
                        nameList.add(mymodel);
                    }

                    pCur.close();
                }
            }
        }
        if (cur != null) {
            cur.close();
        }
        return nameList;
    }

    public void readFile(View v) {
//        File file = new File(getFilesDir(), "ContactsData.txt");
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "ContactsData.txt");
        ArrayList<String> contactNumbersForMessage = new ArrayList<>();

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
//        Toast.makeText(this, contactNumbersForMessage.toString(), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(MainActivity.this, MessageActivity.class);
        startActivity(intent);
    }
}