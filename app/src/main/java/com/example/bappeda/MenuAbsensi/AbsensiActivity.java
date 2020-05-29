package com.example.bappeda.MenuAbsensi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import com.camerakit.CameraKitView;
import com.example.bappeda.R;

import java.util.ArrayList;
import java.util.List;

public class AbsensiActivity extends AppCompatActivity {

    private Activity activity;
    private CardView cMasuk, cPulang;
    private String[] appPermission =  {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    private final int PERMIOSSION_REQUEST_CODE = 1240;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_absensi);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle("Absensi");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

    @Override
    protected void onResume() {
        super.onResume();
        if (checkPermission()){

            initEvent();
        }
    }

    private boolean checkPermission(){

        List<String> permissionList = new ArrayList<>();
        for (String perm : appPermission) {

            if (ContextCompat.checkSelfPermission(activity, perm) != PackageManager.PERMISSION_GRANTED){

                permissionList.add(perm);
            }
        }

        if (!permissionList.isEmpty()) {

            ActivityCompat.requestPermissions(activity, permissionList.toArray(new String[permissionList.size()]), PERMIOSSION_REQUEST_CODE);

            return  false;
        }

        return  true;
    }

    private void initUI() {

        cMasuk = (CardView) findViewById(R.id.c_masuk);
        cPulang = (CardView) findViewById(R.id.c_pulang);
    }

    private void initEvent() {

        cMasuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(activity, DetailAbsensi.class);
                intent.putExtra("flag", 1);
                startActivity(intent);
            }
        });

        cPulang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(activity, DetailAbsensi.class);
                intent.putExtra("flag", 2);
                startActivity(intent);
            }
        });
    }
}
