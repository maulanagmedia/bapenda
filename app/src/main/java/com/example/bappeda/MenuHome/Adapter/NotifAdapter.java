package com.example.bappeda.MenuHome.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bappeda.MenuHome.Model.NotifModel;
import com.example.bappeda.R;

import java.util.ArrayList;

public class NotifAdapter extends RecyclerView.Adapter<NotifAdapter.NotifHolder>{

    private Activity activity;
    private ArrayList<NotifModel> listNotif;

    public NotifAdapter(Activity activity, ArrayList<NotifModel> list){
       this.activity = activity;
       this.listNotif = list;
    }

    @NonNull
    @Override
    public NotifAdapter.NotifHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_kategori_merchant_white, parent, false);
        return new NotifHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotifAdapter.NotifHolder holder, int position) {
        NotifModel notifModel = listNotif.get(position);
        holder.btnnamaKategori.setText(notifModel.getNamaKategori());
    }

    @Override
    public int getItemCount() {
        return listNotif.size();
    }

    public class NotifHolder extends RecyclerView.ViewHolder {

        Button btnnamaKategori;

        public NotifHolder(@NonNull View itemView) {
            super(itemView);
            btnnamaKategori = itemView.findViewById(R.id.btn_kategori);
        }
    }
}
