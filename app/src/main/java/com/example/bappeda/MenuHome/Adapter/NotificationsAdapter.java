package com.example.bappeda.MenuHome.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.bappeda.Model.NotificationModel;
import com.example.bappeda.R;
import com.example.bappeda.Utils.FormatItem;
import com.example.bappeda.Utils.ImageLoader;
import com.example.bappeda.Utils.ItemValidation;

import java.util.List;

public class NotificationsAdapter extends ArrayAdapter<NotificationModel> {

    private Context mContext;
    private ItemValidation iv = new ItemValidation();

    public NotificationsAdapter(Context context, int resource, List<NotificationModel> objects) {
        super(context, resource, objects);
        mContext = context;
    }

    class Viewholder{

        TextView tvItem1, tvItem2, tvItem3, tvItem4, tvItem5;
        ImageView ivIcon;
        RelativeLayout rvContainer;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        Viewholder holder;
        NotificationModel item = getItem(position);

        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).
                    inflate(R.layout.adapter_notification, parent, false);

            holder = new Viewholder();
            holder.rvContainer = (RelativeLayout) convertView.findViewById(R.id.rv_container);
            holder.tvItem1 = (TextView) convertView.findViewById(R.id.tv_item1);
            holder.tvItem2 = (TextView) convertView.findViewById(R.id.tv_item2);
            holder.tvItem3 = (TextView) convertView.findViewById(R.id.tv_item3);
            holder.tvItem4 = (TextView) convertView.findViewById(R.id.tv_item4);
            holder.tvItem5 = (TextView) convertView.findViewById(R.id.tv_item5);
            holder.ivIcon = (ImageView) convertView.findViewById(R.id.iv_icon);

            convertView.setTag(holder);
        } else
            holder = (Viewholder) convertView.getTag();

        if (item !=null){

            holder.tvItem1.setText(item.getJudul());
            holder.tvItem2.setText(item.getMerchant());
            holder.tvItem3.setText(item.getAlamat());
            holder.tvItem4.setText(item.getDeskripsi());
            holder.tvItem5.setText(iv.ChangeFormatDateString(item.getTanggal(), FormatItem.formatDate, FormatItem.formatDateDisplay));

            ImageLoader.load(mContext, item.getImage(), holder.ivIcon);
        }

        return convertView;
    }
}
