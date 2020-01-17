package com.example.bappeda.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.bappeda.Model.MerchantModel;
import com.example.bappeda.R;
import com.example.bappeda.Utils.ImageLoader;

import java.util.List;

//use for Monitoring Merchant Activity,
//        Riwayat Monitoring Merchant Activity,
//        Riwayat Survey Activity,
//        Merchant Sekitar Activity
//        Merchant Tutup Activity
public class RiwayatReklameAdapter extends ArrayAdapter<MerchantModel> {

    private Context mContext;

    public RiwayatReklameAdapter(Context context, int resource, List<MerchantModel> objects) {
        super(context, resource, objects);
        mContext = context;
    }

    class Viewholder{
        ImageView img_merchant;
        TextView tmerchant;
        TextView talamat;
        TextView tvStatus;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        Viewholder holder;
        MerchantModel merchantModel = getItem(position);

        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).
                    inflate(R.layout.activity_list_view_survey, parent, false);
            holder = new Viewholder();
            holder.tmerchant = convertView.findViewById(R.id.txt_merchant);
            holder.talamat = convertView.findViewById(R.id.txt_alamat);
            holder.img_merchant = convertView.findViewById(R.id.img_survey);
            holder.tvStatus = convertView.findViewById(R.id.tv_status);
            convertView.setTag(holder);
        } else
            holder = (Viewholder) convertView.getTag();

        if (merchantModel !=null){
            holder.tmerchant.setText(merchantModel.getNamamerchant());
            holder.talamat.setText(merchantModel.getAlamat());
            ImageLoader.load(mContext, merchantModel.getImage(), holder.img_merchant);
            holder.tvStatus.setText(merchantModel.getTanggal());

           /* if(merchantModel.getKodePendaftaran().equals("0")){

                holder.tvStatus.setTextColor(mContext.getResources().getColor(R.color.orange));
            }else if(merchantModel.getKodePendaftaran().equals("0")){

                holder.tvStatus.setTextColor(mContext.getResources().getColor(R.color.redSoft));
            }else{

                holder.tvStatus.setTextColor(mContext.getResources().getColor(R.color.blueLight));
            }*/
/*
            holder.tvStatus.setText(merchantModel.getStatusPendaftaran());*/
        }

        return convertView;
    }
}
