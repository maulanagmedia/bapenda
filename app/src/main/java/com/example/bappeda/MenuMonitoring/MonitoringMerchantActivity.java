package com.example.bappeda.MenuMonitoring;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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

    TextView hari, tanggal;

    ListView listmerchant;
    private SurveyAdapter adapter;
    ArrayList<MerchantModel> merchantModels = new ArrayList<>();

    ApiVolley apiVolley;
    String status = "";
    String message = "";

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

        //Format tanggal hari ini
        tanggalFormat();
        DayFormat();

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

        JSONObject body = new JSONObject();
        try {
            body.put("id_user", idUser);
            // "0" = sudah dikunjungi
            // "1" = belum dikunjungi
            body.put("status", "1");
        } catch (JSONException e) {
            e.printStackTrace();
            if (e.getMessage()!=null){
                Log.e(TAG, "body" + e.getMessage());
            }
        }

        AppLoadingScreen.getInstance().showLoading(MonitoringMerchantActivity.this);

        apiVolley = new ApiVolley(MonitoringMerchantActivity.this, body, "POST", URL.URL_MONITORING_MERCHANT,
                new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                Log.d(TAG, "Response" + result);
                try {
                    JSONObject object = new JSONObject(result);
                    status = object.getJSONObject("metadata").getString("status");
                    message = object.getJSONObject("metadata").getString("message");
                    if (status.equals("200")){
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
                        adapter = new SurveyAdapter(MonitoringMerchantActivity.this, R.layout.activity_list_view_survey, merchantModels);
                        listmerchant.setAdapter(adapter);
                        Log.d(TAG, "onSuccess" + message);
                    } else {
                        AppLoadingScreen.getInstance().stopLoading();
                        merchantModels.clear();
                        adapter.notifyDataSetChanged();
                        Toast.makeText(MonitoringMerchantActivity.this, message, Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onSuccess" + message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    AppLoadingScreen.getInstance().stopLoading();
                    if (e.getMessage()!=null){
                        Log.e(TAG, "response" + e.getMessage());
                    }
                }
                AppLoadingScreen.getInstance().stopLoading();
            }

            @Override
            public void onError(String result) {
                AppLoadingScreen.getInstance().stopLoading();
                Toast.makeText(MonitoringMerchantActivity.this, R.string.error_message, Toast.LENGTH_SHORT).show();
                Log.d(TAG,"Error.Response" + result);
            }
        });
    }

    private void tanggalFormat(){
        String myFormat = "yyyy-MM-dd"; //format tanggal hari ini
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

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.searchbar, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                final JSONObject body = new JSONObject();
                try {
                    body.put("search", s);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("eeee", e.getMessage());
                }

                apiVolley = new ApiVolley(MonitoringMerchantActivity.this, body, "POST", URL.URL_VIEW_MERCHANT_BY_ID, new ApiVolley.VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {
                        Log.d("Response", result);
                        try {
                            JSONObject object = new JSONObject(result);
                            JSONArray array = object.getJSONArray("response");
                            surveyModels = new ArrayList<>();
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject dataObject = array.getJSONObject(i);
                                SurveyModel surveyModel = new SurveyModel();
                                surveyModel.setId(dataObject.getInt("id"));
                                surveyModel.setNamamerchant(dataObject.getString("nama"));
                                surveyModel.setAlamat(dataObject.getString("alamat"));
                                surveyModel.setTanggal(dataObject.getString("tgl"));
                                surveyModels.add(surveyModel);
                            }
                            listmerchant.setAdapter(new SurveyAdapter(MonitoringMerchantActivity.this, R.layout.activity_list_view_survey, surveyModels));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("response", e.getMessage());
                        }
                    }

                    @Override
                    public void onError(String result) {
                        Log.d("Error.Response", result);
                    }
                });
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (!searchView.isIconified() && TextUtils.isEmpty(s)) {
                    loadData();
                }
                return false;
            }
        });
        return true;
    }

    private void searchDatabyDate() {
        final JSONObject body = new JSONObject();
        try {
            body.put("start", tanggal_awal.getText());
            body.put("end", tanggal_akhir.getText());
        } catch (JSONException e) {
            if (e.getMessage()!= null){
                Log.e("", e.getMessage());
            }
        }

        apiVolley = new ApiVolley(MonitoringMerchantActivity.this, body, "POST", URL.URL_SEARCH_MERCHANT, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                Log.d("Response", result);
                try {
                    JSONObject object = new JSONObject(result);
                    JSONArray array = object.getJSONArray("response");
                    surveyModels = new ArrayList<>();
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject dataObject = array.getJSONObject(i);
                        SurveyModel surveyModel = new SurveyModel();
                        surveyModel.setNamamerchant(dataObject.getString("nama"));
                        surveyModel.setTanggal(dataObject.getString("tgl"));
                        surveyModel.setAlamat(dataObject.getString("alamat"));
                        surveyModel.setNamapemilik(dataObject.getString("pemilik"));
                        surveyModel.setNotelp(dataObject.getString("no_telp"));
                        surveyModel.setLatitude(dataObject.getDouble("latitude"));
                        surveyModel.setLongitude(dataObject.getDouble("longitude"));
                        surveyModels.add(surveyModel);
                    }
                    adapter = new SurveyAdapter(MonitoringMerchantActivity.this, R.layout.activity_list_view_survey, surveyModels);
                    listmerchant.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (e.getMessage()!=null){
                        Log.e("response", e.getMessage());
                    }
                }
            }

            @Override
            public void onError(String result) {
                Log.d("Error.Response", result);
            }
        });
    }

    private void DateCalendar() {
        Mycalendar = Calendar.getInstance();

        final DatePickerDialog.OnDateSetListener date_awal = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                Mycalendar.set(Calendar.YEAR, year);
                Mycalendar.set(Calendar.MONTH, month);
                Mycalendar.set(Calendar.DAY_OF_MONTH, day);
                updateDateAwal();
            }
        };

        tanggal_awal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(MonitoringMerchantActivity.this, date_awal, Mycalendar.get(Calendar.YEAR),
                        Mycalendar.get(Calendar.MONTH), Mycalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        final DatePickerDialog.OnDateSetListener date_akhir = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                Mycalendar.set(Calendar.YEAR, year);
                Mycalendar.set(Calendar.MONTH, month);
                Mycalendar.set(Calendar.DAY_OF_MONTH, day);
                updateDateAkhir();
            }
        };

        tanggal_akhir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(MonitoringMerchantActivity.this, date_akhir, Mycalendar.get(Calendar.YEAR),
                        Mycalendar.get(Calendar.MONTH), Mycalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

    }

    private void updateDateAwal() {
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        tanggal.setText(sdf.format(Mycalendar.getTime()));
    }

    private void updateDateAkhir() {
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        tanggal_akhir.setText(sdf.format(Mycalendar.getTime()));
    }

    private void tanggalAwalFormat(){
        String myFormat = "yyyy-MM-01";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        Date myDate = new Date();
        String dateName = sdf.format(myDate);
        tanggal_awal.setText(dateName);
    }*/
}
