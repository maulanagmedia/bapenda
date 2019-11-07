package com.example.bappeda.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.bappeda.MenuPendaftaran.PreviewActivity;
import com.example.bappeda.Model.MerchantModel;
import com.example.bappeda.R;
import com.example.bappeda.Utils.ImageLoader;
import com.example.bappeda.Utils.URL;

import java.util.List;

public class BelumDaftarAdapter extends ArrayAdapter<MerchantModel> {

    private Context mContext;

    public BelumDaftarAdapter(Context context, int resource, List<MerchantModel> objects) {
        super(context, resource, objects);
        mContext = context;
    }

    class Viewholder{
        View layout_parent;
        ImageView img_merchant;
        TextView tnamamerchant, talamat, tdecision;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Viewholder holder;
        final MerchantModel belumDaftarModel = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).
                    inflate(R.layout.list_belum_daftar, parent, false);
            holder = new Viewholder();
            holder.layout_parent = convertView.findViewById(R.id.layout_parent);
            holder.img_merchant = convertView.findViewById(R.id.img_merchant);
            holder.tnamamerchant = convertView.findViewById(R.id.txt_merchant);
            holder.talamat = convertView.findViewById(R.id.txt_alamat);
            holder.tdecision = convertView.findViewById(R.id.txt_status);
            convertView.setTag(holder);
        } else
            holder = (Viewholder) convertView.getTag();

        if(belumDaftarModel != null){
            ImageLoader.load(mContext, belumDaftarModel.getImage(), holder.img_merchant);
            Log.e("glide_log", "getImage: " + belumDaftarModel.getImage());
            holder.tnamamerchant.setText(belumDaftarModel.getNamamerchant());
            holder.talamat.setText(belumDaftarModel.getAlamat());
            holder.tdecision.setText("Menolak");

            holder.layout_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(mContext, PreviewActivity.class);
                    i.putExtra(URL.EXTRA_ID_MERCHANT, belumDaftarModel.getId());
                    mContext.startActivity(i);
                }
            });
        }

        return convertView;
    }
}
