package com.example.bappeda.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bappeda.Model.NotificationModel;
import com.example.bappeda.R;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<NotificationModel> listNotification;

    public NotificationAdapter(Context context, List<NotificationModel> listNotification){
        this.context = context;
        this.listNotification = listNotification;
    }

    @Override
    public int getItemViewType(int position) {
        return listNotification.get(position).getType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        if (viewType == NotificationModel.TYPE_GENERAL){
            return new NotificationViewHolder(inflater.inflate(R.layout.item_notification, parent, false));
        } else {
            return new HubungiPetugasHolder(inflater.inflate(R.layout.item_notif_hubungi, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof NotificationViewHolder){
            ((NotificationViewHolder) holder).bind(listNotification.get(position));
        } else if (holder instanceof HubungiPetugasHolder){
            ((HubungiPetugasHolder) holder).bind(listNotification.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return listNotification.size();
    }

    class NotificationViewHolder extends RecyclerView.ViewHolder{

        View layout_parent;
        TextView txt_judul, txt_tanggal, txt_nama, txt_alamat, txt_deskripsi; //untuk survey dan monitoring


        NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            layout_parent = itemView.findViewById(R.id.layout_parent);
            txt_judul = itemView.findViewById(R.id.txt_judul);
            txt_tanggal = itemView.findViewById(R.id.txt_tanggal);
            txt_nama = itemView.findViewById(R.id.txt_nama);
            txt_alamat = itemView.findViewById(R.id.txt_alamat);
            txt_deskripsi = itemView.findViewById(R.id.txt_deskripsi);
        }

        void bind(final NotificationModel n){
            txt_judul.setText(n.getJudul());
            txt_nama.setText(n.getMerchant());
            txt_alamat.setText(n.getAlamat());
            txt_deskripsi.setText(n.getDeskripsi());
            txt_tanggal.setText(n.getTanggal());

            /*layout_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(context, PutusanPendaftaranActivity.class);
                    Gson gson = new Gson();
                    i.putExtra(URL.EXTRA_MERCHANT, gson.toJson(n.getMerchant()));
                    context.startActivity(i);
                }
            });*/
        }
    }

    class HubungiPetugasHolder extends RecyclerView.ViewHolder{

        TextView judul, tanggal, deskripsi; //untuk hubungi petugas

        public HubungiPetugasHolder(@NonNull View itemView) {
            super(itemView);
            judul = itemView.findViewById(R.id.txt_judulHub);
            tanggal = itemView.findViewById(R.id.txt_tanggalHub);
            deskripsi = itemView.findViewById(R.id.txt_deskripsiHub);
        }

        void bind(final NotificationModel n){
            judul.setText(n.getJudul());
            tanggal.setText(n.getTanggal());
            deskripsi.setText(n.getDeskripsi());
        }
    }

}
