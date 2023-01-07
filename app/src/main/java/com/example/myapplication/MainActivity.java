package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
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


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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



//        String state = Environment.getExternalStorageState();
//        if (Environment.MEDIA_MOUNTED.equals(state)) {
//            // External storage is available and writable
//            Toast.makeText(this, "AW", Toast.LENGTH_SHORT).show();
//        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
//            // External storage is available and read-only
//            Toast.makeText(this, "AR", Toast.LENGTH_SHORT).show();
//        } else {
//            // External storage is not available
//            Toast.makeText(this, "NA", Toast.LENGTH_SHORT).show();
//        }


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
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                ContactModel mymodel;
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                // nameList.add(name);
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
//                        nameList.add(phoneNo);
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
        File file = new File(getFilesDir(), "sample1.txt");
//        if (file.exists()) {
//            file.delete();
//        }

        try {
            FileInputStream inputStream = new FileInputStream(file);
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();

            // Convert the buffer to a string and print it
            String text = new String(buffer);
            Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
            TextView txt = findViewById(R.id.textView);
            txt.setText(text.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}