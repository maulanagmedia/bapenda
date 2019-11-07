package com.example.bappeda.MenuMerchants;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.bappeda.Adapter.RiwayatTutupAdapter;
import com.example.bappeda.Model.MerchantModel;
import com.example.bappeda.R;
import com.example.bappeda.Utils.ApiVolley;
import com.example.bappeda.Utils.AppLoadingScreen;
import com.example.bappeda.Utils.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class RiwayatMerchantTutupActivity extends AppCompatActivity {

    final String TAG = "RiwayatMerchantTutup";

    //Variabel UI
    private TextView tanggal_awal, tanggal_akhir;
    ImageButton button_proses;
    private ListView listTutup;
    private Calendar Mycalendar;

    private RiwayatTutupAdapter adapter;
    private ArrayList<MerchantModel> merchantModels = new ArrayList<>();

    //Filter
    String start_date = "";
    String end_date = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riwayat_merchant_tutup);

        tanggal_awal = findViewById(R.id.txt_tanggalawal);
        tanggal_akhir = findViewById(R.id.txt_tanggalakhir);
        listTutup = findViewById(R.id.list_tutup);
        button_proses = findViewById(R.id.btnproses);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar()!=null){
            getSupportActionBar().setTitle("Riwayat");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        DateCalendar();
        tanggaFormat();
        loadMerchantTutup();

        button_proses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadMerchantTutup();
            }
        });
    }

    private void loadMerchantTutup(){

        AppLoadingScreen.getInstance().showLoading(this);

        JSONObject body = new JSONObject();
        try {
            body.put("tanggal_awal", start_date);
            body.put("tanggal_akhir", end_date);
        } catch (JSONException e) {
            e.printStackTrace();
            if (e.getMessage()!=null){
                Log.d(TAG, "body: " + e.getMessage());
            }
        }

        new ApiVolley(RiwayatMerchantTutupActivity.this, body, "POST", URL.URL_VIEW_MERCHANT_TUTUP,
                new ApiVolley.VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {
                        Log.d(TAG, "Response" + result);
                        try {
                            JSONObject object = new JSONObject(result);
                            String message = object.getJSONObject("metadata").getString("message");
                            int status = object.getJSONObject("metadata").getInt("status");
                            if (status==200){
                                JSONArray list_tutup = object.getJSONArray("response");
                                merchantModels = new ArrayList<>();
                                for (int i=0; i<list_tutup.length(); i++){
                                    JSONObject tutup = list_tutup.getJSONObject(i);
                                    MerchantModel merchantModel = new MerchantModel();
                                    //id Merchant
                                    merchantModel.setId(tutup.getString("id_merchant"));
                                    merchantModel.setNamamerchant(tutup.getString("nama"));
                                    merchantModel.setAlamat(tutup.getString("alamat"));
                                    merchantModel.setAlasan_tutup(tutup.getString("alasan_tutup"));

                                    ArrayList<String> gambar = new ArrayList<>();
                                    final JSONArray arrray = tutup.getJSONArray("image");
                                    for (int p=0; p<arrray.length(); p++){
                                        gambar.add(arrray.getJSONObject(p).getString("image"));
                                    }
                                    merchantModel.setImages(gambar);

                                    merchantModels.add(merchantModel);
                                }
                                adapter = new RiwayatTutupAdapter(RiwayatMerchantTutupActivity.this, R.layout.list_merchant_tutup, merchantModels);
                                listTutup.setAdapter(adapter);
                            } else {
                                AppLoadingScreen.getInstance().stopLoading();
                                merchantModels.clear();
//                                adapter.notifyDataSetChanged();
                                Log.d(TAG, "onSuccess" + message);
                                Toast.makeText(RiwayatMerchantTutupActivity.this, R.string.riwayat_merchant_tutup, Toast.LENGTH_SHORT).show();
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
                        Log.e(TAG, "Error.Response" + result);
                        Toast.makeText(RiwayatMerchantTutupActivity.this, R.string.error_message, Toast.LENGTH_SHORT).show();
                        AppLoadingScreen.getInstance().stopLoading();
                    }
                });
    }

    //Tanggal
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
                new DatePickerDialog(RiwayatMerchantTutupActivity.this, date_awal, Mycalendar.get(Calendar.YEAR),
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
                new DatePickerDialog(RiwayatMerchantTutupActivity.this, date_akhir, Mycalendar.get(Calendar.YEAR),
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

    private void tanggaFormat(){
        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        String dateName = sdf.format(new Date());
        //tanggal awal
        tanggal_awal.setText(dateName);
        start_date = dateName;
        //tanggal akhir
        tanggal_akhir.setText(dateName);
        end_date = dateName;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
