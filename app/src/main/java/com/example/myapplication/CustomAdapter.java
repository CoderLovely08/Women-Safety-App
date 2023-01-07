package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;

import androidx.core.app.ActivityCompat;

import java.util.ArrayList;

public class CustomAdapter extends BaseAdapter {

//    List<ContactModel> model;
    ArrayList<String> items;
    Context context;
    LayoutInflater inflater;

    public CustomAdapter(Context context, ArrayList<String> items) {
        this.context= context;
        this.items = items;
        inflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        return items.size();
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
        checkBox.setText(items.get(i));
        return view;
    }
}
