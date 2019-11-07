package com.example.bappeda.MenuMonitoring;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.bappeda.Adapter.SurveyAdapter;
import com.example.bappeda.Model.CategoryModel;
import com.example.bappeda.Model.MerchantModel;
import com.example.bappeda.R;
import com.example.bappeda.Utils.ApiVolley;
import com.example.bappeda.Utils.AppLoadingScreen;
import com.example.bappeda.Utils.Preferences;
import com.example.bappeda.Utils.URL;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class RiwayatMonitoringMerchantActivity extends AppCompatActivity {

    private final String TAG = "RiwayatMonitoring";

    ListView listmerchant;
    TextView hari, tanggal;
    private SurveyAdapter adapter;
    ArrayList<MerchantModel> merchantModels = new ArrayList<>();
    ApiVolley apiVolley;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riwayat_monitoring_merchant);

        //Inisialisasi UI
        listmerchant = findViewById(R.id.list_merchant);
        hari = findViewById(R.id.txtHari);
        tanggal = findViewById(R.id.txtTanggal);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar()!=null){
            getSupportActionBar().setTitle("Riwayat Monitoring");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        tanggalFormat(); //format tanggal hari ini
        DayFormat(); //format hari ini (ex: senin, selasa, dll)

        listmerchant.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                MerchantModel ItemList = (MerchantModel) listmerchant.getItemAtPosition(i);
                Intent intent = new Intent(RiwayatMonitoringMerchantActivity.this, PreviewMerchantActivity.class);
                Gson gson = new Gson();
                intent.putExtra("id_merchant", gson.toJson(ItemList));
                startActivity(intent);
            }
        });
    }

    private void tanggalFormat(){
        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        Date myDate = new Date();
        String dateName = sdf.format(myDate);
        tanggal.setText(dateName);
    }

    private void DayFormat(){
        String myFormat = "EEEE";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        Date myDate = new Date();
        String dayName = sdf.format(myDate);
        hari.setText(dayName);
    }

    private void loadData() {

        AppLoadingScreen.getInstance().showLoading(RiwayatMonitoringMerchantActivity.this);

        final String idUser = Preferences.getId(getBaseContext());

        JSONObject body = new JSONObject();
        try {
            body.put("id_user", idUser);
            // "0" = sudah dikunjungi
            // "1" = belum dikunjungi
            body.put("status", "0");
        } catch (JSONException e) {
            e.printStackTrace();
            if (e.getMessage()!=null){
                Log.e(TAG, "body: " + e.getMessage());
            }
        }

        apiVolley = new ApiVolley(RiwayatMonitoringMerchantActivity.this, body, "POST", URL.URL_MONITORING_MERCHANT,
                new ApiVolley.VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {
                        Log.d(TAG, "Response" + result);
                        try {
                            JSONObject object = new JSONObject(result);
                            String message =  object.getJSONObject("metadata").getString("message");
                            int status = object.getJSONObject("metadata").getInt("status");
                            if (status==200){
                                JSONArray array = object.getJSONArray("response");
                                merchantModels = new ArrayList<>();
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject dataObject = array.getJSONObject(i);
                                    MerchantModel merchantModel = new MerchantModel();
                                    //id_monitoring
                                    merchantModel.setId(dataObject.getString("id"));
                                    merchantModel.setNamamerchant(dataObject.getString("nama"));
                                    merchantModel.setAlamat(dataObject.getString("alamat"));
                                    merchantModel.setLongitude(dataObject.getDouble("longitude"));
                                    merchantModel.setLatitude(dataObject.getDouble("latitude"));
                                    merchantModel.setDeskripsi(dataObject.getString("hasil_monitor"));
                                    CategoryModel categoryModel = new CategoryModel();
                                    categoryModel.setIdKategori(dataObject.getString("kategori"));
                                    merchantModel.setKategori(categoryModel);

                                    ArrayList<String> gambar = new ArrayList<>();
                                    final JSONArray arrray = dataObject.getJSONArray("image");
                                    for (int p=0; p<arrray.length(); p++){
                                        gambar.add(arrray.getJSONObject(p).getString("image"));
                                    }
                                    merchantModel.setImages(gambar);
                                    merchantModels.add(merchantModel);
                                }
                                adapter = new SurveyAdapter(RiwayatMonitoringMerchantActivity.this, R.layout.activity_list_view_survey, merchantModels);
                                listmerchant.setAdapter(adapter);
                            } else {
                                AppLoadingScreen.getInstance().stopLoading();
                                merchantModels.clear();
                                adapter.notifyDataSetChanged();
                                Toast.makeText(RiwayatMonitoringMerchantActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                            Log.d(TAG, "onSuccess: " + message);
                        } catch (JSONException e) {
                            AppLoadingScreen.getInstance().stopLoading();
                            e.printStackTrace();
                            if (e.getMessage()!=null){
                                Log.e(TAG, "response" + e.getMessage());
                            }
                        }
                        AppLoadingScreen.getInstance().stopLoading();
                    }

                    @Override
                    public void onError(String result) {
                        AppLoadingScreen.getInstance().stopLoading();
                        Toast.makeText(RiwayatMonitoringMerchantActivity.this, R.string.error_message, Toast.LENGTH_SHORT).show();
                        Log.d("Error.Response", result);
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }
}
