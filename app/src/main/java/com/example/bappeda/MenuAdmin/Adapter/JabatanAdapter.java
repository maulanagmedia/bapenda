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

import com.example.bappeda.MenuAdmin.HubungiPetugas.PetugasUntukHubungi;
import com.example.bappeda.MenuAdmin.Model.JabatanModel;
import com.example.bappeda.MenuAdmin.Monitoring.PetugasMonitoringActivity;
import com.example.bappeda.MenuAdmin.Survey.PetugasSurveyActivity;
import com.example.bappeda.R;

import java.util.ArrayList;

public class JabatanAdapter extends RecyclerView.Adapter<JabatanAdapter.JabatanHolder> {

    private Context context;
    private Activity activity;
    private ArrayList<JabatanModel> listJabatan;

    private int position_aktif = 0;

    public JabatanAdapter(Activity activity, ArrayList<JabatanModel> list){
        this.activity = activity;
        this.listJabatan = list;
    }

    public int getPosition_aktif() {
        return position_aktif;
    }

    @NonNull
    @Override
    public JabatanAdapter.JabatanHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_jabatan_white, parent, false);
        return new JabatanHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final JabatanHolder holder, final int position) {
        final JabatanModel jabatanModel = listJabatan.get(position);
        holder.btnnamaJabatan.setText(jabatanModel.getNamaJabatan());

        if(position == position_aktif){
            holder.btnnamaJabatan.setBackground(context.getDrawable(R.drawable.style_rounded_blue_dark_rectangle));
            holder.btnnamaJabatan.setTextColor(Color.WHITE);
        }
        else{
            holder.btnnamaJabatan.setBackground(context.getDrawable(R.drawable.style_rounded_white_rectangle));
            holder.btnnamaJabatan.setTextColor(Color.parseColor("#19096F"));
        }

        holder.btnnamaJabatan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(activity instanceof PetugasSurveyActivity){
                    ((PetugasSurveyActivity)activity).loadPetugas(jabatanModel.getId_level(), "");
                } else if (activity instanceof PetugasMonitoringActivity){
                    ((PetugasMonitoringActivity)activity).loadPetugas(jabatanModel.getId_level(), "");
                } else if (activity instanceof PetugasUntukHubungi) {
                    ((PetugasUntukHubungi)activity).loadPetugas(jabatanModel.getId_level(), "");
                }

                int position_temp = position_aktif;
                jabatanModel.setAktif(true);
                listJabatan.get(position_aktif).setAktif(false);
                position_aktif = position;

                notifyItemChanged(position_temp);
                notifyItemChanged(position_aktif);

            }
        });
    }

    @Override
    public int getItemCount() {
        return listJabatan.size();
    }

    static class JabatanHolder extends RecyclerView.ViewHolder {

        Button btnnamaJabatan;

        JabatanHolder(@NonNull View itemView) {
            super(itemView);
            btnnamaJabatan = itemView.findViewById(R.id.btn_jabatan);
        }
    }

}
