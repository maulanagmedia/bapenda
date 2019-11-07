package com.example.bappeda.Adapter;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.example.bappeda.Model.JadwalModel;

import java.util.List;

public class JadwalAdapter extends ArrayAdapter<JadwalModel> {

    private Context mContext;

    public JadwalAdapter(Context context, int resource, List<JadwalModel> objects) {
        super(context, resource, objects);
        mContext = context;
    }
}
