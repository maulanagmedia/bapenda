package com.example.bappeda.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bappeda.MenuHome.DetailNotificationActivity;
import com.example.bappeda.Model.NotificationModel;
import com.example.bappeda.R;
import com.example.bappeda.Utils.FormatItem;
import com.example.bappeda.Utils.ImageLoader;
import com.example.bappeda.Utils.ItemValidation;
import com.google.gson.Gson;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<NotificationModel> listNotification;
    private ItemValidation iv = new ItemValidation();

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
        return new NotificationViewHolder(inflater.inflate(R.layout.adapter_notification, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        ((NotificationViewHolder) holder).bind(listNotification.get(position));
    }

    @Override
    public int getItemCount() {
        return listNotification.size();
    }

    class NotificationViewHolder extends RecyclerView.ViewHolder{

        TextView tvItem1, tvItem2, tvItem3, tvItem4, tvItem5;
        ImageView ivIcon;
        RelativeLayout rvContainer;

        NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            rvContainer = (RelativeLayout) itemView.findViewById(R.id.rv_container);
            tvItem1 = (TextView) itemView.findViewById(R.id.tv_item1);
            tvItem2 = (TextView) itemView.findViewById(R.id.tv_item2);
            tvItem3 = (TextView) itemView.findViewById(R.id.tv_item3);
            tvItem4 = (TextView) itemView.findViewById(R.id.tv_item4);
            tvItem5 = (TextView) itemView.findViewById(R.id.tv_item5);
            ivIcon = (ImageView) itemView.findViewById(R.id.iv_icon);
        }

        void bind(final NotificationModel n){

            tvItem1.setText(n.getJudul());
            tvItem2.setText(n.getMerchant());
            tvItem3.setText(n.getAlamat());
            tvItem4.setText(n.getDeskripsi());
            tvItem5.setText(iv.ChangeFormatDateString(n.getTanggal(), FormatItem.formatDate, FormatItem.formatDateDisplay));

            ImageLoader.load(context, n.getImage(), ivIcon);

            rvContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(context, DetailNotificationActivity.class);
                    Gson gson = new Gson();
                    intent.putExtra("notif", gson.toJson(n));
                    ((Activity) context).startActivity(intent);
                }
            });
        }
    }

}
