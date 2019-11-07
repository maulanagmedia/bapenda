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

import com.example.bappeda.Model.MerchantModel;
import com.example.bappeda.R;
import com.example.bappeda.Utils.ImageLoader;

import java.util.List;

public class MerchantPenugasanAdapter extends ArrayAdapter<MerchantModel> {

    private Context mContext;

    public MerchantPenugasanAdapter(@NonNull Context context, int resource, @NonNull List<MerchantModel> objects) {
        super(context, resource, objects);
        mContext = context;
    }

    class Viewholder{
        ImageView img_merchant;
        TextView tmerchant;
        TextView talamat;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Viewholder holder;
        MerchantModel merchantModel = getItem(position);

        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).
                    inflate(R.layout.list_merchant_tugas_survey, parent, false);
            holder = new Viewholder();
            holder.tmerchant = convertView.findViewById(R.id.txt_merchant);
            holder.talamat = convertView.findViewById(R.id.txt_alamat);
            holder.img_merchant = convertView.findViewById(R.id.img_survey);
            convertView.setTag(holder);
        } else
            holder = (MerchantPenugasanAdapter.Viewholder) convertView.getTag();

        if (merchantModel!=null){
            holder.tmerchant.setText(merchantModel.getNamamerchant());
            holder.talamat.setText(merchantModel.getAlamat());
            ImageLoader.load(mContext, merchantModel.getImage(), holder.img_merchant);
        }

        return convertView;
    }
}
