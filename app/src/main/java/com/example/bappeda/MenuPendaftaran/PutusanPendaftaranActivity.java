package com.example.bappeda.MenuPendaftaran;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.bappeda.Model.MerchantModel;
import com.example.bappeda.R;
import com.example.bappeda.Utils.ApiVolley;
import com.example.bappeda.Utils.AppLoadingScreen;
import com.example.bappeda.Utils.GoogleLocationManager;
import com.example.bappeda.Utils.JSONBuilder;
import com.example.bappeda.Utils.Preferences;
import com.example.bappeda.Utils.URL;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

public class PutusanPendaftaranActivity extends AppCompatActivity {

    TextView txt_nama_merchant, txt_nama_pemilik, txt_alamat_merchant;
    RadioButton daftar, tidakdaftar;
    Button simpan, isiKelengkapan;

    private MerchantModel merchant;
    private double lat = 0, lng = 0;

    private EditText alasan;
    private View layout_setuju, layout_menolak;

    private GoogleLocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_putusan_pendaftaran);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar()!=null){
            getSupportActionBar().setTitle("Putusan Pendaftaran");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        layout_setuju = findViewById(R.id.layout_setuju);
        layout_menolak = findViewById(R.id.layout_menolak);
        txt_nama_merchant = findViewById(R.id.txt_nama_merchant);
        txt_alamat_merchant = findViewById(R.id.txt_alamat_merchant);
        txt_nama_pemilik = findViewById(R.id.txt_nama_pemilik);
        daftar = findViewById(R.id.rb_daftar);
        tidakdaftar = findViewById(R.id.rb_tidakdaftar);
        alasan = findViewById(R.id.edt_keterangan);
        simpan = findViewById(R.id.button_simpan);
        isiKelengkapan = findViewById(R.id.formLengkap);

        if (getIntent().hasExtra(URL.EXTRA_MERCHANT)){
            Gson gson = new Gson();
            merchant = gson.fromJson(getIntent().getStringExtra(URL.EXTRA_MERCHANT), MerchantModel.class);
            txt_nama_merchant.setText(merchant.getNamamerchant());
            txt_nama_pemilik.setText(merchant.getNamapemilik());
            txt_alamat_merchant.setText(merchant.getAlamat());
        }

        daftar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    layout_setuju.setVisibility(View.VISIBLE);
                } else {
                    layout_setuju.setVisibility(View.GONE);
                }
            }
        });

        tidakdaftar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    layout_menolak.setVisibility(View.VISIBLE);
                } else {
                    layout_menolak.setVisibility(View.GONE);
                }
            }
        });

        isiKelengkapan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                kirimHasilSurvey(true);
                Intent i = new Intent(PutusanPendaftaranActivity.this,
                        PendaftaranWajibPajakActivity.class);

                Gson gson = new Gson();
                i.putExtra(URL.EXTRA_MERCHANT, gson.toJson(merchant));
                startActivity(i);
            }
        });

        simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(alasan.getText().toString().equals("")){
                    Toast.makeText(PutusanPendaftaranActivity.this,
                            "Isi alasan penolakan terlebih dahulu", Toast.LENGTH_SHORT).show();
                }
                else{

                    AlertDialog dialog = new AlertDialog.Builder(PutusanPendaftaranActivity.this)
                            .setTitle("Konfirmasi")
                            .setMessage("Apakah anda yakin ingin melanjutkan proses?")
                            .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            })
                            .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    kirimHasilSurvey(false);
                                }
                            })
                            .show();
                }
            }
        });

        initLocation();
    }

    private void kirimHasilSurvey(final boolean setuju){
        if(lat == 0 || lng == 0){
            Toast.makeText(this, "Lokasi tidak ditemukan, nyalakan lokasi dan ijin lokasi anda", Toast.LENGTH_SHORT).show();
            return;
        }

        String idUser = Preferences.getId(PutusanPendaftaranActivity.this);
        AppLoadingScreen.getInstance().showLoading(this);
        JSONBuilder body = new JSONBuilder();
        body.add("id_merchant", merchant.getId());
//        body.add("status", setuju?"Ya":"Tidak");
        body.add("id_user", idUser);
        body.add("status", "Tidak");
        body.add("alasan", setuju?"":alasan.getText().toString());
        body.add("lat", lat);
        body.add("long", lng);

        new ApiVolley(PutusanPendaftaranActivity.this, body.create(),
                "POST", URL.URL_DAFTAR_STATUS, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                Log.d("survey_log", result);
                try{
                    JSONObject object = new JSONObject(result);
                    int status = object.getJSONObject("metadata").getInt("status");
                    String message = object.getJSONObject("metadata").getString("message");

                    if(status == 200){
                        if(setuju){
                            /*Intent i = new Intent(PutusanPendaftaranActivity.this,
                                    PendaftaranWajibPajakActivity.class);

                            Gson gson = new Gson();
                            i.putExtra(URL.EXTRA_MERCHANT, gson.toJson(merchant));
                            startActivity(i);*/
                        }
                        else{
                            Toast.makeText(getApplication(), message, Toast.LENGTH_LONG).show();
                            finish();
                        }
                    }
                    else{
                        Toast.makeText(getApplication(), message, Toast.LENGTH_LONG).show();
                    }
                    Log.e("survey_log", message);

                } catch (JSONException e) {
                    e.printStackTrace();
                    if (e.getMessage()!=null){
                        Log.e("survey_log", e.getMessage());
                    }
                }
                AppLoadingScreen.getInstance().stopLoading();
            }

            @Override
            public void onError(String result) {
                Toast.makeText(PutusanPendaftaranActivity.this, R.string.error_message, Toast.LENGTH_SHORT).show();
                Log.e("survey_log", result);
                AppLoadingScreen.getInstance().stopLoading();
            }
        });
    }

    private void initLocation(){
        locationManager = new GoogleLocationManager(this, new GoogleLocationManager.LocationUpdateListener() {
            @Override
            public void onChange(Location location) {
                lat = location.getLatitude();
                lng = location.getLongitude();

                locationManager.stopLocationUpdates();
            }
        });
        locationManager.startLocationUpdates();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == GoogleLocationManager.PERMISSION_LOCATION){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                locationManager.startLocationUpdates();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
