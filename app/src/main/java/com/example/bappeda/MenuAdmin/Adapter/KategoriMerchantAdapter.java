package com.example.bappeda.MenuAdmin.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bappeda.MenuAdmin.Model.KategoriMerchantModel;
import com.example.bappeda.MenuAdmin.Monitoring.MerchantUntukMonitoring;
import com.example.bappeda.MenuAdmin.Survey.MerchantUntukSurveyActivity;
import com.example.bappeda.R;

import java.util.ArrayList;

public class KategoriMerchantAdapter extends RecyclerView.Adapter<KategoriMerchantAdapter.KategoriMerchantHolder> {

    private Context context;
    private Activity activity;
    private ArrayList<KategoriMerchantModel> listKategori;

    public int position_aktif = 0;

    public KategoriMerchantAdapter(Activity activity, ArrayList<KategoriMerchantModel> list){
        this.activity = activity;
        this.listKategori = list;
    }

    public int getPosition_aktif() {
        return position_aktif;
    }

    @NonNull
    @Override
    public KategoriMerchantAdapter.KategoriMerchantHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_kategori_merchant_white, parent, false);
        return new KategoriMerchantHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KategoriMerchantHolder holder, final int position) {
        final KategoriMerchantModel kategoriMerchantModel= listKategori.get(position);
        holder.btnNamaKategori.setText(kategoriMerchantModel.getNamaMerchant());

        if (position == position_aktif){
            holder.btnNamaKategori.setBackground(context.getDrawable(R.drawable.style_rounded_blue_dark_rectangle));
            holder.btnNamaKategori.setTextColor(Color.WHITE);
        } else {
            holder.btnNamaKategori.setBackground(context.getDrawable(R.drawable.style_rounded_white_rectangle));
            holder.btnNamaKategori.setTextColor(Color.parseColor("#19096F"));
        }

        //untuk category merchant per activity
        holder.btnNamaKategori.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (activity instanceof MerchantUntukSurveyActivity){
                    ((MerchantUntukSurveyActivity)activity).loadMerchant(kategoriMerchantModel.getId_kategori(), "");
                }

                if (activity instanceof MerchantUntukMonitoring){
                    ((MerchantUntukMonitoring)activity).start = 0;
                    ((MerchantUntukMonitoring)activity).loadMerchant(kategoriMerchantModel.getId_kategori());
                }

                int position_temp = position_aktif;
                kategoriMerchantModel.setAktif(true);
                listKategori.get(position_aktif).setAktif(false);
                position_aktif = position;

                notifyItemChanged(position_temp);
                notifyItemChanged(position_aktif);

            }
        });

    }

    @Override
    public int getItemCount() {
        return listKategori.size();
    }

    public class KategoriMerchantHolder extends RecyclerView.ViewHolder {

        Button btnNamaKategori;

        public KategoriMerchantHolder(@NonNull View itemView) {
            super(itemView);
            btnNamaKategori = itemView.findViewById(R.id.btn_kategori);
        }
    }
}
