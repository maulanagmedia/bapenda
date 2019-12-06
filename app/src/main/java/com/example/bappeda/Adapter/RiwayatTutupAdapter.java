package com.example.bappeda.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.bappeda.Model.MerchantModel;
import com.example.bappeda.R;
import com.example.bappeda.Utils.FormatItem;
import com.example.bappeda.Utils.ImageLoader;
import com.example.bappeda.Utils.ItemValidation;

import java.util.List;

public class RiwayatTutupAdapter extends ArrayAdapter<MerchantModel> {

    private Context mContext;
    private ItemValidation iv = new ItemValidation();

    public RiwayatTutupAdapter(@NonNull Context context, int resource, @NonNull List<MerchantModel> objects) {
        super(context, resource, objects);
        this.mContext = context;
    }

    class Viewholder{

        LinearLayout llAlasan;
        ImageView img_merchant;
        TextView tmerchant;
        TextView talamat;
        TextView talasan;
        TextView tvDate;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Viewholder holder;
        MerchantModel merchantModel = getItem(position);

        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).
                    inflate(R.layout.list_merchant_tutup, parent, false);
            holder = new Viewholder();
            holder.tmerchant = convertView.findViewById(R.id.txt_merchant);
            holder.talamat = convertView.findViewById(R.id.txt_alamat);
            holder.img_merchant = convertView.findViewById(R.id.img_survey);
            holder.talasan = convertView.findViewById(R.id.txt_alasan);
            holder.tvDate = convertView.findViewById(R.id.tv_date);
            holder.llAlasan = convertView.findViewById(R.id.ll_alasan);
            convertView.setTag(holder);
        } else
            holder = (Viewholder) convertView.getTag();

        if (merchantModel !=null){
            holder.tmerchant.setText(merchantModel.getNamamerchant());
            holder.talamat.setText(merchantModel.getAlamat());
            holder.talasan.setText(merchantModel.getAlasan_tutup());
            ImageLoader.load(mContext, merchantModel.getImage(), holder.img_merchant);
            holder.tvDate.setText(iv.ChangeFormatDateString(merchantModel.getTanggal(), FormatItem.formatTimestamp, FormatItem.formatDateTimeDisplay));

            if(!merchantModel.getAlasan_tutup().isEmpty()){

                holder.llAlasan.setVisibility(View.VISIBLE);
            }else{

                holder.llAlasan.setVisibility(View.GONE);
            }

        }

        return convertView;
    }
}
