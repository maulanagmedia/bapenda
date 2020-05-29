package com.example.bappeda.MenuAbsensi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bappeda.Adapter.RiwayatTutupAdapter;
import com.example.bappeda.MenuAbsensi.Adapter.RiwayatAbsensiAdapter;
import com.example.bappeda.Model.MerchantModel;
import com.example.bappeda.R;
import com.example.bappeda.Utils.ApiVolley;
import com.example.bappeda.Utils.AppLoadingScreen;
import com.example.bappeda.Utils.CustomModel;
import com.example.bappeda.Utils.FormatItem;
import com.example.bappeda.Utils.ItemValidation;
import com.example.bappeda.Utils.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class RiwayatAbsensi extends AppCompatActivity {

    final String TAG = "RiwayatAbsensi";

    //Variabel UI
    private TextView tanggal_awal, tanggal_akhir;
    private ImageButton button_proses;
    private ListView lvAbsensi;
    private Calendar Mycalendar;
    private Activity activity;
    private ItemValidation iv = new ItemValidation();

    private RiwayatAbsensiAdapter adapter;
    private ArrayList<CustomModel> listItem = new ArrayList<>();

    //Filter
    private String start_date = "";
    private String end_date = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riwayat_absensi);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar()!= null){
            getSupportActionBar().setTitle("Riwayat Absensi");
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
        DateCalendar();
        tanggaFormat();
        LoadData();

        button_proses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadData();
            }
        });
    }

    private void LoadData(){

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

        new ApiVolley(activity, body, "POST", URL.getRiwayatAbsensi,
                new ApiVolley.VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {

                        listItem.clear();
                        Log.d(TAG, "Response" + result);
                        try {

                            JSONObject object = new JSONObject(result);
                            String message = object.getJSONObject("metadata").getString("message");
                            int status = object.getJSONObject("metadata").getInt("status");

                            if (status == 200){

                                JSONArray listJson = object.getJSONArray("response");

                                for (int i=0; i<listJson.length(); i++){

                                    JSONObject jo = listJson.getJSONObject(i);
                                    listItem.add(new CustomModel(
                                            iv.ChangeFormatDateString(jo.getString("timestamp"), FormatItem.formatTimestamp, FormatItem.formatDateDisplay)
                                            , iv.ChangeFormatDateString(jo.getString("timestamp"), FormatItem.formatTimestamp, FormatItem.formatTime)
                                            , jo.getString("status")
                                    ));

                                }

                            } else {

                                AppLoadingScreen.getInstance().stopLoading();
                                listItem.clear();
                                Log.d(TAG, "onSuccess" + message);
                                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            AppLoadingScreen.getInstance().stopLoading();
                            if (e.getMessage()!=null){
                                Log.e(TAG, "response" + e.getMessage());
                            }
                        }
                        AppLoadingScreen.getInstance().stopLoading();
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(String result) {
                        Log.e(TAG, "Error.Response" + result);
                        Toast.makeText(activity, R.string.error_message, Toast.LENGTH_SHORT).show();
                        AppLoadingScreen.getInstance().stopLoading();
                    }
                });
    }

    private void initUI() {

        tanggal_awal = findViewById(R.id.txt_tanggalawal);
        tanggal_akhir = findViewById(R.id.txt_tanggalakhir);
        lvAbsensi = findViewById(R.id.lv_absensi);
        button_proses = findViewById(R.id.btnproses);

        adapter = new RiwayatAbsensiAdapter(activity, listItem);
        lvAbsensi.setAdapter(adapter);
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
                new DatePickerDialog(activity, date_awal, Mycalendar.get(Calendar.YEAR),
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
                new DatePickerDialog(activity, date_akhir, Mycalendar.get(Calendar.YEAR),
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
