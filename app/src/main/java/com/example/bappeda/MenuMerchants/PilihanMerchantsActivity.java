package com.example.bappeda.MenuMerchants;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.example.bappeda.R;

public class PilihanMerchantsActivity extends AppCompatActivity {

    CardView merchantSekitar, merchantTutup;

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

        merchantSekitar = findViewById(R.id.CardSekitar);
        merchantTutup = findViewById(R.id.CardTutup);

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
    }
}
