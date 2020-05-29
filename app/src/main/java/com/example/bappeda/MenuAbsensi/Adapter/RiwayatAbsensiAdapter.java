package com.example.bappeda.MenuAbsensi.Adapter;

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
import com.example.bappeda.Utils.CustomModel;
import com.example.bappeda.Utils.FormatItem;
import com.example.bappeda.Utils.ImageLoader;
import com.example.bappeda.Utils.ItemValidation;

import java.util.List;

public class RiwayatAbsensiAdapter extends ArrayAdapter {

    private Context mContext;
    private ItemValidation iv = new ItemValidation();
    private List<CustomModel> listItem;

    public RiwayatAbsensiAdapter(Context context, List<CustomModel> listItem){
        super(context, R.layout.list_riwayat_absensi, listItem);
        this.mContext = context;
        this.listItem = listItem;
    }

    class Viewholder{

        TextView tvItem1, tvItem2, tvItem3;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Viewholder holder;

        CustomModel item = listItem.get(position);

        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).
                    inflate(R.layout.list_riwayat_absensi, parent, false);
            holder = new Viewholder();
            holder.tvItem1 = convertView.findViewById(R.id.tv_item1);
            holder.tvItem2 = convertView.findViewById(R.id.tv_item2);
            holder.tvItem3 = convertView.findViewById(R.id.tv_item3);
            convertView.setTag(holder);
        } else
            holder = (Viewholder) convertView.getTag();

        holder.tvItem1.setText(item.getItem1());
        holder.tvItem2.setText(item.getItem2());
        holder.tvItem3.setText(item.getItem3());

        return convertView;
    }
}
