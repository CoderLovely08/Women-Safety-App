package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class CustomAdapter extends BaseAdapter {

    List<ContactModel> model;
    Context context;
    LayoutInflater inflater;

    public CustomAdapter(Context context, List<ContactModel> model) {
        this.context = context;
        this.model = model;
        inflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        return model.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.custom_list, null);
        CheckBox checkBox = view.findViewById(R.id.checkbox);
        checkBox.setText(model.get(i).getContactName().toString());

        String contactName = model.get(i).getContactName();
        String contactNumber = model.get(i).getContactNumber();

        SharedPreferences sharedPreferences = context.getSharedPreferences("loginPreference", Context.MODE_PRIVATE);

        DataBaseHelper dataBaseHelper = new DataBaseHelper(context);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkBox.isChecked()) {
                    try {
                        boolean success = dataBaseHelper.addContact(new ContactModel(-1, contactName, contactNumber), sharedPreferences.getString("userEmail", ""));
                        if (success)
                            Toast.makeText(context, "Contact Added", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(context, "Unable to add contact", Toast.LENGTH_SHORT).show();

                    } catch (Exception e) {
                        // Error writing to file
                        Toast.makeText(context, "Unable to Add Contact", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
//                ----------------------------------------------------------------------------------
//                                  To add this thing later
//                ----------------------------------------------------------------------------------
//                }else if (!checkBox.isChecked()){
//                    DataBaseHelper dataBaseHelper1 =  new DataBaseHelper(context);
//                    boolean success = dataBaseHelper1.deleteContact(contactName, sharedPreferences.getString("userEmail", ""));
//                    if(success) Toast.makeText(context, "Contact removed", Toast.LENGTH_SHORT).show();
//                    else Toast.makeText(context, "Unable to remove Contact", Toast.LENGTH_SHORT).show();
//                }
            }
        });
        return view;
    }

}
