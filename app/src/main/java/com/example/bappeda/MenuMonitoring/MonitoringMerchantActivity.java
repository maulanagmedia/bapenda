package com.example.bappeda.MenuMonitoring;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
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
import com.example.bappeda.Utils.FormatItem;
import com.example.bappeda.Utils.ItemValidation;
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

public class MonitoringMerchantActivity extends AppCompatActivity {

    private TextView hari, tanggal;

    private ListView listmerchant;
    private SurveyAdapter adapter;
    private ArrayList<MerchantModel> merchantModels = new ArrayList<>();

    private ApiVolley apiVolley;
    private String status = "";
    private String message = "";
    private ItemValidation iv = new ItemValidation();
    private int start = 0, count = 10;
    private View footerList;
    private boolean isLoading = false;
    private String keyword = "";

    private final String TAG = "MonitoringMerchant";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitoring_merchant);

        //Inisialisasi UI
        listmerchant = findViewById(R.id.list_merchant);
        hari = findViewById(R.id.txtHari);
        tanggal = findViewById(R.id.txtTanggal);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar()!=null){
            getSupportActionBar().setTitle("Tugas Monitoring");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        keyword = "";
        LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        footerList = li.inflate(R.layout.footer_list, null);

        adapter = new SurveyAdapter(MonitoringMerchantActivity.this, R.layout.activity_list_view_survey, merchantModels);
        listmerchant.setAdapter(adapter);

        //Format tanggal hari ini
        tanggalFormat();
        DayFormat();

        listmerchant.addFooterView(footerList);
        listmerchant.setAdapter(adapter);
        listmerchant.removeFooterView(footerList);

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
                Intent intent = new Intent(MonitoringMerchantActivity.this, DetailMerchantActivity.class);
                Gson gson = new Gson();
                intent.putExtra("id_merchant", gson.toJson(ItemList));
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.surveymenu, menu);

        MenuItem getItem = menu.findItem(R.id.action_riwayat);
        if (getItem!=null){
            View layout_parent = getItem.getActionView();
            Button riwayat =  layout_parent.findViewById(R.id.btn_riwayat);
            riwayat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(MonitoringMerchantActivity.this, RiwayatMonitoringMerchantActivity.class));
                }
            });
        }
        return true;
    }

    private void loadData() {

        final String idUser = Preferences.getId(getBaseContext());
        isLoading = true;
        AppLoadingScreen.getInstance().showLoading(this);
        JSONObject body = new JSONObject();
        try {
            body.put("id_user", idUser);
            body.put("id_kategori", "");
            body.put("start", String.valueOf(start));
            body.put("count", String.valueOf(count));
            body.put("keyword", keyword);
        } catch (JSONException e) {
            e.printStackTrace();
            if (e.getMessage()!=null){
                Log.e(TAG, "body" + e.getMessage());
            }
        }

        apiVolley = new ApiVolley(MonitoringMerchantActivity.this, body, "POST", URL.URL_MONITORING_MERCHANT,
                new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                AppLoadingScreen.getInstance().stopLoading();
                isLoading = false;
                listmerchant.removeFooterView(footerList);
                if(start == 0) merchantModels.clear();
                Log.d(TAG, "Response" + result);
                try {

                    JSONObject object = new JSONObject(result);
                    status = object.getJSONObject("metadata").getString("status");
                    message = object.getJSONObject("metadata").getString("message");

                    if (status.equals("200")){

                        JSONArray array = object.getJSONArray("response");

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject dataObject = array.getJSONObject(i);
                            MerchantModel merchantModel = new MerchantModel();
                            //id_monitoring
                            merchantModel.setId(dataObject.getString("id_monitor"));
                            merchantModel.setNamamerchant(dataObject.getString("nama"));
                            merchantModel.setAlamat(dataObject.getString("alamat"));
                            merchantModel.setLongitude(dataObject.getDouble("longitude"));
                            merchantModel.setLatitude(dataObject.getDouble("latitude"));
                            CategoryModel categoryModel = new CategoryModel();
                            categoryModel.setIdKategori(dataObject.getString("id_kategori"));
                            merchantModel.setKategori(categoryModel);

                            ArrayList<String> gambar = new ArrayList<>();
                            final JSONArray arrray = dataObject.getJSONArray("image");
                            for (int p=0; p<arrray.length(); p++){
                                gambar.add(arrray.getJSONObject(p).getString("image"));
                            }
                            merchantModel.setImages(gambar);
                            merchantModels.add(merchantModel);
                        }


                        Log.d(TAG, "onSuccess" + message);
                    } else {

                        Toast.makeText(MonitoringMerchantActivity.this, message, Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onSuccess" + message);
                    }
                } catch (JSONException e) {

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
                merchantModels.clear();
                adapter.notifyDataSetChanged();
                AppLoadingScreen.getInstance().stopLoading();
                Toast.makeText(MonitoringMerchantActivity.this, R.string.error_message, Toast.LENGTH_SHORT).show();
                Log.d(TAG,"Error.Response" + result);
            }
        });
    }

    private void tanggalFormat(){
        String myFormat = FormatItem.formatDateDisplay; //format tanggal hari ini
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        String dateName = sdf.format(new Date());
        tanggal.setText(dateName);
    }

    private void DayFormat(){
        String myFormat = "EEEE"; //format Hari (ex: senin, selasa, dll)
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        String dayName = sdf.format(new Date());
        hari.setText(dayName);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }
}
