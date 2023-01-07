package com.example.myapplication;

import android.content.Context;
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
//        File file = new File(context.getFilesDir(), "ContactsData.txt");
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "ContactsData.txt");
        if (file.exists()) {
            System.out.println("Existing file deleted");
            file.delete();
        }
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkBox.isChecked()) {
                    try {

                        File file1 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "my_file.txt");
                        if (file1.exists()) System.out.println("New file saved");
                        else System.out.println(file1.getAbsolutePath());
                        file1.createNewFile();
                        System.out.println(file1.getAbsolutePath());
//                        File file = new File(context.getFilesDir(), "ContactsData.txt");
                        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "ContactsData.txt");

                        FileOutputStream outputStream = new FileOutputStream(file, true);

                        String contactName = model.get(i).getContactName();
                        String contactNumber = model.get(i).getContactNumber();

                        String pattern = "Number:([0-9]*)";
                        String strippedPhoneNumber = contactNumber.replaceAll(pattern, "");

                        String data = "Name:" + contactName + " Number:" + strippedPhoneNumber + "\n";
                        System.out.println(data);
                        String line;
                        BufferedReader reader = new BufferedReader(new FileReader(file));
                        boolean found = false;
                        while ((line = reader.readLine()) != null) {
                            if (line.trim().equals(data.trim())) {
                                found = true;
                                break;
                            }
                        }
                        reader.close();

                        if (!found) {
                            System.out.println("Data: " + data + " -----" + strippedPhoneNumber);
                            outputStream.write(data.getBytes());
                            outputStream.close();
                        }

                        if (file.exists()) {
                            // File was saved successfully
                            Toast.makeText(context, "Contact Added", Toast.LENGTH_SHORT).show();
                        } else {
                            // File was not saved
                            Toast.makeText(context, "Unable to add contact", Toast.LENGTH_SHORT).show();
                        }
                    } catch (FileNotFoundException e) {
                        // File could not be created
                        Toast.makeText(context, "Unable to Add Contact", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    } catch (IOException e) {
                        // Error writing to file
                        Toast.makeText(context, "Unable to Write Contact", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            }
        });
        return view;
    }

}
