package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;

public class CustomAdapter extends BaseAdapter {

    List<ContactModel> model;
//    ArrayList<String> items;
    Context context;
    LayoutInflater inflater;

    public CustomAdapter(Context context, List<ContactModel> model) {
        this.context= context;
//        this.items = items;
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

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, model.get(i).getContactNumber().toString(), Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }
}
