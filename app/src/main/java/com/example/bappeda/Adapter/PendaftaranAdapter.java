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

import com.example.bappeda.MenuPendaftaran.PreviewActivity;
import com.example.bappeda.Model.MerchantModel;
import com.example.bappeda.R;
import com.example.bappeda.Utils.FormatItem;
import com.example.bappeda.Utils.ImageLoader;
import com.example.bappeda.Utils.ItemValidation;
import com.example.bappeda.Utils.URL;

import java.util.List;

public class PendaftaranAdapter extends ArrayAdapter<MerchantModel> {

    private Context mContext;
    private ItemValidation iv = new ItemValidation();

    public PendaftaranAdapter(Context context, int resource, List<MerchantModel> objects) {
        super(context, resource, objects);
        mContext = context;
    }

    class Viewholder{
        View layout_parent;
        ImageView img_merchant;
        TextView txt_merchant, txt_alamat, txt_status, txt_status_batal, tvTime;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Viewholder holder = null;

        //Get the data from this position
        final MerchantModel pendaftaranModel = getItem(position);

        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).
                    inflate(R.layout.list_daftar, parent, false);
            holder = new Viewholder();
            holder.layout_parent = convertView.findViewById(R.id.layout_parent);
            holder.txt_merchant = convertView.findViewById(R.id.txt_merchant);
            holder.txt_alamat = convertView.findViewById(R.id.txt_alamat);
            holder.txt_status = convertView.findViewById(R.id.txt_status);
            holder.txt_status_batal = convertView.findViewById(R.id.txt_status_batal);
            holder.img_merchant = convertView.findViewById(R.id.img_merchant);
            holder.tvTime  = (TextView) convertView.findViewById(R.id.tv_time);
            convertView.setTag(holder);
        } else
            holder = (Viewholder) convertView.getTag();

        if(pendaftaranModel != null){

            ImageLoader.load(mContext, pendaftaranModel.getImage(), holder.img_merchant);
            Log.e("glide_log", "getImage: " + pendaftaranModel.getImage());
            holder.txt_merchant.setText(pendaftaranModel.getNamamerchant());
            holder.txt_alamat.setText(pendaftaranModel.getAlamat());

            if(pendaftaranModel.getStatusPendaftaran().toLowerCase().equals("ya")){

                holder.txt_status.setVisibility(View.VISIBLE);
                holder.txt_status_batal.setVisibility(View.GONE);
            }else{

                holder.txt_status.setVisibility(View.GONE);
                holder.txt_status_batal.setVisibility(View.VISIBLE);
            }

            holder.txt_status.setText(pendaftaranModel.getStatusPendaftaran());
            holder.txt_status_batal.setText(pendaftaranModel.getStatusPendaftaran());

            holder.tvTime.setText(iv.ChangeFormatDateString(pendaftaranModel.getTanggal(), FormatItem.formatTimestamp, FormatItem.formatDateTimeDisplay));

            holder.layout_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(mContext, PreviewActivity.class);
                    i.putExtra(URL.EXTRA_ID_MERCHANT, pendaftaranModel.getId());
                    mContext.startActivity(i);
                }
            });
        }

        return convertView;
    }
}
