package com.example.bappeda.MenuMerchants;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.example.bappeda.R;
import com.example.bappeda.Utils.Preferences;

import java.util.Set;

public class PilihanMerchantsActivity extends AppCompatActivity {

    private CardView merchantSekitar, merchantTutup, cvUbahLokasiMerchant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar_merchants);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Daftar Merchants");
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        merchantSekitar = (CardView) findViewById(R.id.CardSekitar);
        merchantTutup = (CardView) findViewById(R.id.CardTutup);
        cvUbahLokasiMerchant = (CardView) findViewById(R.id.cv_lokasi);

        merchantSekitar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent a = new Intent(PilihanMerchantsActivity.this, MerchantSekitarActivity.class);
                startActivity(a);
            }
        });

        merchantTutup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent b = new Intent(PilihanMerchantsActivity.this, MerchantTutupActivity.class);
                startActivity(b);
            }
        });

        cvUbahLokasiMerchant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent b = new Intent(PilihanMerchantsActivity.this, UbahLokasiMerchantActivity.class);
                startActivity(b);
            }
        });

        Set<String> listSubMenu = Preferences.getSubMenu(PilihanMerchantsActivity.this);

        for(String menu: listSubMenu){

            if(menu.equals("merchant_sekitar")){

                merchantSekitar.setVisibility(View.VISIBLE);
            }else if (menu.equals("merchant_tutup")){

                merchantTutup.setVisibility(View.VISIBLE);
            }else if (menu.equals("ubah_lokasi_merchant")){

                cvUbahLokasiMerchant.setVisibility(View.VISIBLE);
            }
        }
    }
}
