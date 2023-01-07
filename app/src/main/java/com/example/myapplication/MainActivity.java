package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;


import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView list;

    ArrayList<String> mobileArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mobileArray = getAllContacts();
        list = findViewById(R.id.list_view);
        CustomAdapter adapter = new CustomAdapter(this, mobileArray);
        list.setAdapter(adapter);

        list.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        CheckBox checkBox = view.findViewById(R.id.checkbox);
                        boolean isChecked = checkBox.isChecked();
                        // do something with the checked state

                    }
                });
    }

    public void click(View v) {
        Toast.makeText(this, "Hello", Toast.LENGTH_LONG).show();
    }

    private ArrayList<String> getAllContacts() {
        ArrayList<String> nameList = new ArrayList<>();
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                // nameList.add(name);
                if (cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur =
                            cr.query(
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                    null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                    new String[] {id},
                                    null);
                    while (pCur.moveToNext()) {
                        String phoneNo =
                                pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        nameList.add(phoneNo);
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
    public void itemClicked(View view) {
        // code to check if this checkbox is checked!
        CheckBox checkBox = view.findViewById(R.id.checkbox);
        if (checkBox.isChecked()) {
            Toast.makeText(this, checkBox.getText().toString(), Toast.LENGTH_SHORT).show();
        }
    }
}