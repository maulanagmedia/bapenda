package com.example.bappeda.MenuMonitoring;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
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

    private ListView listmerchant;
    private TextView hari, tanggal;
    private SurveyAdapter adapter;
    private ArrayList<MerchantModel> merchantModels = new ArrayList<>();
    private ApiVolley apiVolley;

    private int start = 0, count = 10;
    private View footerList;
    private boolean isLoading = false;
    private String keyword = "";

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

        keyword = "";
        LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        footerList = li.inflate(R.layout.footer_list, null);

        adapter = new SurveyAdapter(RiwayatMonitoringMerchantActivity.this, R.layout.activity_list_view_survey, merchantModels);
        listmerchant.setAdapter(adapter);

        listmerchant.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

                int threshold = 1;
                int countMerchant = listmerchant.getCount();

                if (i == SCROLL_STATE_IDLE) {
                    if (listmerchant.getLastVisiblePosition() >= countMerchant - threshold && !isLoading) {

                        isLoading = true;
                        start += count;
                        loadData();
                        //Log.i(TAG, "onScroll: last ");
                    }
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });

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

        isLoading = true;
        AppLoadingScreen.getInstance().showLoading(this);
        final String idUser = Preferences.getId(getBaseContext());

        JSONObject body = new JSONObject();
        try {
            body.put("id_user", idUser);
            body.put("keyword", "");
            body.put("start", String.valueOf(start));
            body.put("count", String.valueOf(count));
        } catch (JSONException e) {
            e.printStackTrace();
            if (e.getMessage()!=null){
                Log.e(TAG, "body: " + e.getMessage());
            }
        }

        apiVolley = new ApiVolley(RiwayatMonitoringMerchantActivity.this, body, "POST", URL.URL_RIWAYAT_MONITORING,
                new ApiVolley.VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {

                        isLoading = false;
                        listmerchant.removeFooterView(footerList);
                        if(start == 0) merchantModels.clear();
                        AppLoadingScreen.getInstance().stopLoading();
                        Log.d(TAG, "Response" + result);

                        try {
                            JSONObject object = new JSONObject(result);
                            String message =  object.getJSONObject("metadata").getString("message");
                            int status = object.getJSONObject("metadata").getInt("status");
                            if (status==200){
                                JSONArray array = object.getJSONArray("response");
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

                            } else {
                                AppLoadingScreen.getInstance().stopLoading();
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
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(String result) {
                        isLoading = false;
                        listmerchant.removeFooterView(footerList);
                        Log.e("Error.Response", result);
                        merchantModels.clear();
                        adapter.notifyDataSetChanged();
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
