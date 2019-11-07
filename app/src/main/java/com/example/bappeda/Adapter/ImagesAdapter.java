package com.example.bappeda.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bappeda.Model.ImagesModel;
import com.example.bappeda.R;
import com.example.bappeda.Utils.URL;
import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.fxn.utility.ImageQuality;

import java.util.List;

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ImagesViewHolder> {

    private boolean can_edit;
    private List<ImagesModel> list_photo;
    private Activity activity;

    public ImagesAdapter(Activity activity, List<ImagesModel> list_photo){
        this.activity = activity;
        this.list_photo = list_photo;
        can_edit = true;
    }

    public ImagesAdapter(Activity activity, List<ImagesModel> list_photo, boolean can_edit){
        this.activity = activity;
        this.list_photo = list_photo;
        this.can_edit = can_edit;
    }

    @NonNull
    @Override
    public ImagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ImagesViewHolder(LayoutInflater.from(parent.getContext()).
                inflate(R.layout.list_photo, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ImagesViewHolder holder, int position) {
        if(can_edit){
            if (position == 0){
                holder.images.setImageResource(R.drawable.big_cam);
                holder.img_hapus.setVisibility(View.GONE);
                holder.images.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Options options = Options.init()
                                .setRequestCode(URL.CODE_UPLOAD)
                                .setCount(10)
                                .setFrontfacing(false)
                                .setImageQuality(ImageQuality.HIGH)
                                .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT)
                                .setPath("/images");
                        Pix.start((FragmentActivity) activity, options);
                    }
                });
            } else {
                holder.images.setImageBitmap(list_photo.get(position - 1).getBitmap());
                holder.img_hapus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        list_photo.remove(holder.getAdapterPosition() - 1);
                        notifyItemRemoved(holder.getAdapterPosition());
                    }
                });
            }
        }
        else{
            holder.images.setImageBitmap(list_photo.get(position).getBitmap());
            holder.img_hapus.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        if(can_edit){
            return list_photo.size() + 1;
        }
        else{
            return list_photo.size();
        }
    }

    class ImagesViewHolder extends RecyclerView.ViewHolder {

        ImageView images;
        ImageView img_hapus;

        ImagesViewHolder(@NonNull View itemView) {
            super(itemView);
            images = itemView.findViewById(R.id.images);
            img_hapus = itemView.findViewById(R.id.img_hapus);
        }
    }
}
