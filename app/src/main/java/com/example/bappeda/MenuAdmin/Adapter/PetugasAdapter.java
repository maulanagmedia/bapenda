package com.example.bappeda.MenuAdmin.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.bappeda.MenuAdmin.Model.PetugasModel;
import com.example.bappeda.R;
import com.example.bappeda.Utils.ImageLoader;

import java.util.List;

public class PetugasAdapter extends ArrayAdapter<PetugasModel> {

    private Context mContext;

    public PetugasAdapter(@NonNull Context context, int resource, @NonNull List<PetugasModel> objects) {
        super(context, resource, objects);
        mContext = context;
    }

    class Viewholder{
        ImageView img_petugas;
        TextView tnamapetugas;
        TextView temail;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Viewholder holder;
        PetugasModel petugasModel = getItem(position);

        if (convertView == null){
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.list_petugas, parent, false);
            holder = new Viewholder();
            holder.tnamapetugas = convertView.findViewById(R.id.txt_petugas);
            holder.temail = convertView.findViewById(R.id.txt_email);
            holder.img_petugas = convertView.findViewById(R.id.img_petugas);
            convertView.setTag(holder);
        } else
            holder = (PetugasAdapter.Viewholder) convertView.getTag();

        if (petugasModel!=null){
            holder.tnamapetugas.setText(petugasModel.getNamapetugas());
            holder.temail.setText(petugasModel.getEmail());
            ImageLoader.loadPersonImage(mContext, petugasModel.getImages(), holder.img_petugas);
        }

        return convertView;
    }


}
