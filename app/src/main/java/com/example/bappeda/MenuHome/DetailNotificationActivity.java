package com.example.bappeda.MenuHome;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.bappeda.Adapter.ImagesAdapter;
import com.example.bappeda.Model.ImagesModel;
import com.example.bappeda.Model.NotificationModel;
import com.example.bappeda.R;
import com.example.bappeda.Utils.FormatItem;
import com.example.bappeda.Utils.ItemValidation;
import com.google.gson.Gson;

import java.util.ArrayList;

public class DetailNotificationActivity extends AppCompatActivity {

    private Activity activity;
    private NotificationModel notifItem;
    private TextView tvTanggal, tvJudul, tvKeterangan, tvWp;
    private RecyclerView rvPhoto;
    private ItemValidation iv = new ItemValidation();

    private ImagesAdapter imageAdapter;
    public ArrayList<ImagesModel> list_images = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_notification);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Detail Notification");
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_name);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        activity = this;
        initUI();
    }

    private void initUI() {

        tvTanggal = (TextView) findViewById(R.id.tv_tanggal);
        tvJudul = (TextView) findViewById(R.id.tv_judul);
        tvKeterangan = (TextView) findViewById(R.id.tv_keterangan);
        tvWp = (TextView) findViewById(R.id.tv_wp);
        rvPhoto = (RecyclerView) findViewById(R.id.rv_photo);

        rvPhoto.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        imageAdapter = new ImagesAdapter(this, list_images);
        rvPhoto.setAdapter(imageAdapter);

        if (getIntent().hasExtra("notif")){

            Gson gson = new Gson();
            notifItem = gson.fromJson(getIntent().getStringExtra("notif"), NotificationModel.class);

            tvTanggal.setText(iv.ChangeFormatDateString(notifItem.getTanggal(), FormatItem.formatDate, FormatItem.formatDateDisplay));
            tvJudul.setText(notifItem.getJudul());
            tvKeterangan.setText(notifItem.getDeskripsi());
            tvWp.setText(notifItem.getMerchant());

            ArrayList<String> listImagesUrl = notifItem.getImages();
            if (listImagesUrl!=null){
                for(String url : listImagesUrl){
                    Glide.with(this).asBitmap().load(url).listener(new RequestListener<Bitmap>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                            Log.e("glide_log", "load failed");
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                            if(imageAdapter != null){
                                list_images.add(new ImagesModel(resource));
                                imageAdapter.notifyDataSetChanged();
                                return true;
                            }
                            else{
                                return false;
                            }
                        }
                    }).preload();
                }
            }
        }
    }
}
