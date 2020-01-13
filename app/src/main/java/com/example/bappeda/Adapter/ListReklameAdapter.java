package com.example.bappeda.Adapter;

import android.content.Context;
import android.util.Log;
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

//Use for Pendaftaran Activity
public class ListReklameAdapter extends ArrayAdapter<MerchantModel> {

    private Context mContext;

    public ListReklameAdapter(Context context, int resource, List<MerchantModel> objects) {
        super(context, resource, objects);
        mContext = context;
    }

    class Viewholder{
        TextView tnamamerchant;
        TextView talamat;
        ImageView tgambar;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        Viewholder holder;
        MerchantModel daftarMerchantModel = getItem(position);

        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_daftar_merchant, parent, false);
            holder = new Viewholder();
            holder.tgambar = convertView.findViewById(R.id.img_survey);
            holder.tnamamerchant = convertView.findViewById(R.id.txt_merchant);
            holder.talamat = convertView.findViewById(R.id.txt_alamat);
            convertView.setTag(holder);
        } else
            holder = (Viewholder) convertView.getTag();

        if(daftarMerchantModel != null){
            ImageLoader.load(mContext, daftarMerchantModel.getImage(), holder.tgambar);
            Log.e("glide_log", "getImage: " + daftarMerchantModel.getImage());
            holder.tnamamerchant.setText(daftarMerchantModel.getNamamerchant());
            holder.talamat.setText(daftarMerchantModel.getAlamat());
        }

        return convertView;
    }
}
