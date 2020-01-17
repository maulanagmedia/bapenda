package com.example.bappeda.MenuReklame;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import com.example.bappeda.Adapter.RiwayatReklameAdapter;
import com.example.bappeda.Adapter.SurveyAdapter;
import com.example.bappeda.MenuMonitoring.PreviewMerchantActivity;
import com.example.bappeda.MenuSurvey.RiwayatSurveyActivity;
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
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class RiwayatReklameActivity extends AppCompatActivity {

    private final String TAG = "RiwayatReklame";
    private Calendar Mycalendar;
    private String start_date = "";
    private String end_date = "";

    private ListView listmerchant;
    private TextView hari, tanggal;
    private RiwayatReklameAdapter adapter;
    private ArrayList<MerchantModel> merchantModels = new ArrayList<>();
    private ApiVolley apiVolley;

    private int start = 0, count = 10;
    private View footerList;
    private boolean isLoading = false;
    private boolean bidangUsaha = true;
    private String keyword = "";
    private TextView tanggal_akhir, tanggal_awal;
    private ImageView button_proses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riwayat_reklame);

        //Inisialisasi UI
        listmerchant = findViewById(R.id.list_merchant);
        hari = findViewById(R.id.txtHari);
        tanggal = findViewById(R.id.txtTanggal);
        tanggal_awal = findViewById(R.id.txt_tanggalawal);
        tanggal_akhir = findViewById(R.id.txt_tanggalakhir);
        button_proses = findViewById(R.id.btnproses);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar()!=null){
            getSupportActionBar().setTitle("Riwayat Reklame");
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

        adapter = new RiwayatReklameAdapter(RiwayatReklameActivity.this, R.layout.activity_list_view_survey, merchantModels);
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

        button_proses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                start = 0;
                loadData();
            }
        });


        tanggalAwalFormat();
        tanggalAkhirFormat();
        DateCalendar();
        listmerchant.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                MerchantModel ItemList = (MerchantModel) listmerchant.getItemAtPosition(i);
                Intent intent = new Intent(RiwayatReklameActivity.this, ActivityDetailRiwayatReklame.class);
                Gson gson = new Gson();
                intent.putExtra("id_merchant", gson.toJson(ItemList));
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.searchbar, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                start = 0;
                keyword = s;
                //loadData();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                start = 0;
                keyword = s;
                /*if (!searchView.isIconified() && TextUtils.isEmpty(s)) {
                    loadData();
                }*/
                return false;
            }
        });
        return true;
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
                new DatePickerDialog(RiwayatReklameActivity.this, date_awal, Mycalendar.get(Calendar.YEAR),
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
                new DatePickerDialog(RiwayatReklameActivity.this, date_akhir, Mycalendar.get(Calendar.YEAR),
                        Mycalendar.get(Calendar.MONTH), Mycalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


    }

    private void updateDateAwal() {
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        tanggal_awal.setText(sdf.format(Mycalendar.getTime()));
        start_date = sdf.format(Mycalendar.getTime());
    }

    private void updateDateAkhir() {
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        tanggal_akhir.setText(sdf.format(Mycalendar.getTime()));
        end_date = sdf.format(Mycalendar.getTime());
    }

    private void tanggalAwalFormat(){
        String myFormat = "yyyy-MM-01";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        Date myDate = new Date();
        String dateName = sdf.format(myDate);
        tanggal_awal.setText(dateName);
        start_date = dateName;
    }

    private void tanggalAkhirFormat(){
        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        Date myDate = new Date();
        String dateName = sdf.format(myDate);
        tanggal_akhir.setText(dateName);
        end_date = dateName;
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
            body.put("keyword", keyword);
            body.put("tgl_awal",start_date);
            body.put("tgl_akhir",end_date);
            body.put("start", String.valueOf(start));
            body.put("count", String.valueOf(count));
        } catch (JSONException e) {
            e.printStackTrace();
            if (e.getMessage()!=null){
                Log.e(TAG, "body: " + e.getMessage());
            }
        }

        apiVolley = new ApiVolley(RiwayatReklameActivity.this, body, "POST", URL.getRiwayatReklame,
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
                                    merchantModel.setId(dataObject.getString("id"));
                                    merchantModel.setNamamerchant(dataObject.getString("nama"));
                                    merchantModel.setAlamat(dataObject.getString("alamat"));
                                    merchantModel.setLongitude(dataObject.getDouble("longitude"));
                                    merchantModel.setLatitude(dataObject.getDouble("latitude"));
                                    merchantModel.setNamapemilik(dataObject.getString("pemilik"));
                                    merchantModel.setBidangusaha(dataObject.getString("bidang_usaha"));
                                    merchantModel.setNotelp(dataObject.getString("telp_usaha"));
                                    merchantModel.setInsertat(dataObject.getString("insert_at"));
                                    merchantModel.setKeterangan(dataObject.getString("status_reklame"));
                                    merchantModel.setKetstatusreklame(dataObject.getString("ket_status_reklame"));
                                    merchantModel.setTanggal(dataObject.getString("tgl_reklame"));
                                    CategoryModel categoryModel = new CategoryModel();
                                    categoryModel.setIdKategori(dataObject.getString("id_kategori"));
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
                                Toast.makeText(RiwayatReklameActivity.this, message, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(RiwayatReklameActivity.this, R.string.error_message, Toast.LENGTH_SHORT).show();
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
