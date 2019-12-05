package com.example.bappeda.MenuAdmin.Monitoring;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bappeda.MenuAdmin.Adapter.KategoriMerchantAdapter;
import com.example.bappeda.MenuAdmin.Adapter.MerchantPenugasanAdapter;
import com.example.bappeda.MenuAdmin.Data.KategoriMerchantData;
import com.example.bappeda.MenuAdmin.Model.KategoriMerchantModel;
import com.example.bappeda.MenuAdmin.Model.PetugasModel;
import com.example.bappeda.Model.MerchantModel;
import com.example.bappeda.R;
import com.example.bappeda.Utils.ApiVolley;
import com.example.bappeda.Utils.AppLoadingScreen;
import com.example.bappeda.Utils.DialogFactory;
import com.example.bappeda.Utils.ItemValidation;
import com.example.bappeda.Utils.JSONBuilder;
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
import java.util.List;
import java.util.Locale;

public class MerchantUntukMonitoring extends AppCompatActivity {

    //LoadData List Merchant
    private MerchantPenugasanAdapter adapter;
    private ListView listmerchant;
    private List<MerchantModel> merchantModels = new ArrayList<>();
    public int start = 0, count = 10;
    private View footerList;
    private boolean isLoading = false;
    private ItemValidation iv = new ItemValidation();

    private PetugasModel p;

    private Integer idPetugas = 0;
    private String idMerchant = "";
    private String idUser = "";

    //Web service
    private ApiVolley apiVolley;
    private String kategori, search;

    //UI di dalam dialog
    private Dialog dialog;
    private TextView namapetugas, emailpetugas, namamerchant, alamatmerchant, isitanggal;
    private EditText keterangan, searchmerchant, judulMonitoring;
    private Button kirimmonitoring;
    private CardView tanggal;
    private Calendar myCalendar;

    //Kategori merchant
    private RecyclerView rvKategori;
    private KategoriMerchantAdapter kategoriMerchantAdapter;
    private ArrayList<KategoriMerchantModel> listKategori = new ArrayList<>();

    private final String TAG = "MerchantUntukMonitoring";

    //Dialog konfirmasi
    private Dialog confirm_dialog;
    private CardView simpan, batal;
    private TextView judul;
    private TextView cancel, save; //di CardView
    public String keyword = "", idKategori = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant_untuk_monitoring);

        //Inisialisasi UI
        searchmerchant = findViewById(R.id.edt_search);
        rvKategori = findViewById(R.id.rv_kategori);
        listmerchant = findViewById(R.id.list_merchantMonitoring);
        Toolbar toolbar = findViewById(R.id.toolbar);

        LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        footerList = li.inflate(R.layout.footer_list, null);

        //Get Id user from Shared Preferences
        idUser = Preferences.getId(MerchantUntukMonitoring.this);

        setSupportActionBar(toolbar);
        if (getSupportActionBar()!=null){
            getSupportActionBar().setTitle("Daftar Merchant");
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //Show category merchant
        rvKategori.setHasFixedSize(true);
        listKategori.addAll(KategoriMerchantData.getListData());
        showListKategori();

        if (getIntent().hasExtra("id_petugas")){
            Gson gson = new Gson();
            p = gson.fromJson(getIntent().getStringExtra("id_petugas"), PetugasModel.class);
        }

        keyword = "";
        adapter = new MerchantPenugasanAdapter(getApplication(), R.layout.list_merchant_tugas_survey, merchantModels);
        listmerchant.setAdapter(adapter);

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
                        loadMerchant(listKategori.get(kategoriMerchantAdapter.getPosition_aktif()).getId_kategori());
                        //Log.i(TAG, "onScroll: last ");
                    }
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });

        searchmerchant.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if(actionId == EditorInfo.IME_ACTION_SEARCH){

                    keyword = searchmerchant.getText().toString();
                    start = 0;
                    loadMerchant(listKategori.get(kategoriMerchantAdapter.getPosition_aktif()).getId_kategori());
                    iv.hideSoftKey(MerchantUntukMonitoring.this);
                    return true;
                }
                return false;
            }
        });

        listmerchant.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                dialog = DialogFactory.getInstance().createDialogTrans(MerchantUntukMonitoring.this, R.layout.popup_tugas_monitoring, 100);

                //Inisialisasi UI di dialog berbentuk popup
                namapetugas = dialog.findViewById(R.id.txtpetugas);
                emailpetugas = dialog.findViewById(R.id.txtemail);
                namamerchant = dialog.findViewById(R.id.txtmerchant);
                alamatmerchant = dialog.findViewById(R.id.txtalamat);
                keterangan = dialog.findViewById(R.id.edt_keterangan);
                kirimmonitoring = dialog.findViewById(R.id.button_kirimtugas);
                tanggal = dialog.findViewById(R.id.CardTanggal);
                isitanggal = dialog.findViewById(R.id.txttanggal);
                judulMonitoring = dialog.findViewById(R.id.edt_judulMonitoring);

                initDataPetugas(p);//menampilkan data dari activity sebelumnya melalui model (Petugas Model)
                DateCalendar();
                tanggalFormat();

                //Set Value
                idMerchant = merchantModels.get(i).getId();
                namamerchant.setText(merchantModels.get(i).getNamamerchant());
                alamatmerchant.setText(merchantModels.get(i).getAlamat());
                judulMonitoring.setText(R.string.judul_monitor);
                keterangan.setText(R.string.monitoring);

                kirimmonitoring.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showDialogConfirm();
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });
    }

    //Dialog
    private boolean isInputValid(){
        return true;
    }

    private void showDialogConfirm(){
        confirm_dialog = DialogFactory.getInstance().
                createDialog(MerchantUntukMonitoring.this, R.layout.popup_confirm, 90);

        simpan = confirm_dialog.findViewById(R.id.CardSimpan);
        batal = confirm_dialog.findViewById(R.id.CardCancel);
        save = confirm_dialog.findViewById(R.id.textSimpan);
        cancel = confirm_dialog.findViewById(R.id.textCancel);
        judul = confirm_dialog.findViewById(R.id.textJudulConfirm);
        judul.setText(R.string.confirm_kirim);
        save.setText(R.string.confirm_send);
        cancel.setText(R.string.confirm_no);

        batal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirm_dialog.dismiss();
            }
        });

        simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TambahData();
                confirm_dialog.dismiss();
            }
        });

        confirm_dialog.show();
    }

    public void loadMerchant(String kategori){

        isLoading = true;
        JSONBuilder body = new JSONBuilder();
        body.add("id_user", "");
        body.add("start", String.valueOf(start));
        body.add("count", String.valueOf(count));
        body.add("id_kategori", kategori);
        body.add("keyword", keyword);
        Log.d(TAG, "body: " + body.create());

        apiVolley = new ApiVolley(MerchantUntukMonitoring.this, body.create(), "POST", URL.getMerchantMonitoring, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                isLoading = false;
                listmerchant.removeFooterView(footerList);
                if(start == 0) merchantModels.clear();
                Log.d(TAG, "Response: " + result);

                try {

                    JSONObject object = new JSONObject(result);
                    int status = object.getJSONObject("metadata").getInt("status");
                    String message;

                    if(status== 200){

                        message = object.getJSONObject("metadata").getString("message");
                        JSONArray array = object.getJSONArray("response");
                        for (int i=0; i<array.length(); i++){
                            JSONObject dataObject = array.getJSONObject(i);
                            MerchantModel merchantModel = new MerchantModel(
                                    dataObject.getString("id"),
                                    dataObject.getString("nama"),
                                    dataObject.getString("alamat"));
                            ArrayList<String> gambar = new ArrayList<>();
                            final JSONArray arrray = dataObject.getJSONArray("image");
                            for (int p=0; p<arrray.length(); p++){
                                gambar.add(arrray.getJSONObject(p).getString("image"));
                            }
                            merchantModel.setImages(gambar);
                            merchantModels.add(merchantModel);
                        }
                    }else{

                        message = object.getJSONObject("metadata").getString("message");
                        AppLoadingScreen.getInstance().stopLoading();
                        Toast.makeText(MerchantUntukMonitoring.this, message, Toast.LENGTH_SHORT).show();
                    }

                    Log.d(TAG, "onSuccess: " + message);
                } catch (JSONException e) {

                    e.printStackTrace();
                    AppLoadingScreen.getInstance().stopLoading();
                    if (e.getMessage()!=null){
                        Log.d(TAG, "catch.response" + e.getMessage());
                    }
                }

                AppLoadingScreen.getInstance().stopLoading();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String result) {

                isLoading = false;
                listmerchant.removeFooterView(footerList);
                merchantModels.clear();
                adapter.notifyDataSetChanged();
                Log.d(TAG, "onError" + result);
                AppLoadingScreen.getInstance().stopLoading();
                Toast.makeText(MerchantUntukMonitoring.this, R.string.error_message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    //set value for each field
    private void initDataPetugas(PetugasModel p){

        idPetugas = p.getIdpetugas();
        namapetugas.setText(p.getNamapetugas());
        emailpetugas.setText(p.getEmail());
    }

    //Open Calender
    private void DateCalendar(){

        myCalendar = Calendar.getInstance();

        final DatePickerDialog.OnDateSetListener date_awal = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, day);
                updateDate();
            }
        };

        tanggal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(MerchantUntukMonitoring.this, date_awal, myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    //after the calender open
    private void updateDate() {
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        isitanggal.setText(sdf.format(myCalendar.getTime()));
    }

    //format awal dari tanggal untuk hari ini
    private void tanggalFormat(){
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        isitanggal.setText(sdf.format(new Date()));
    }

    private void TambahData(){

        AppLoadingScreen.getInstance().showLoading(MerchantUntukMonitoring.this);

        final String idP = idPetugas.toString();
        final String idM = idMerchant.toString();
        final String ket = keterangan.getText().toString();
        final String tgl = isitanggal.getText().toString();
        final String judul = judulMonitoring.getText().toString();

        JSONObject body = new JSONObject();
        try {
            body.put("id_petugas", idP);
            body.put("id_merchant", idM);
            body.put("title", judul);
            body.put("ket", ket);
            body.put("tgl_monitor", tgl);
        } catch (JSONException e) {
            e.printStackTrace();
            if (e.getMessage()!=null){
                Log.d(TAG, "error.body" + e.getMessage());
            }
        }

        apiVolley = new ApiVolley(MerchantUntukMonitoring.this, body, "POST", URL.URL_KIRIM_MONITORING, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                AppLoadingScreen.getInstance().stopLoading();
                Log.d(TAG, "Response" + result);
                try {
                    JSONObject response = new JSONObject(result);
                    int status = response.getJSONObject("metadata").getInt("status");
                    String message;
                    if(status== 200){
                        message = response.getJSONObject("metadata").getString("message");
                        Toast.makeText(MerchantUntukMonitoring.this, message, Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }else{
                        message = response.getJSONObject("metadata").getString("message");
                    }
                    Log.d(TAG, "onSuccess: " + message);
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (e.getMessage()!=null){
                        Log.d(TAG,"catch.response" + e.getMessage());
                    }
                }
            }

            @Override
            public void onError(String result) {
                AppLoadingScreen.getInstance().stopLoading();
                Toast.makeText(MerchantUntukMonitoring.this, R.string.error_message, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Error.Response" + result);
            }
        });
    }

    private void showListKategori(){

        rvKategori.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        kategoriMerchantAdapter= new KategoriMerchantAdapter(this, listKategori);
        rvKategori.setAdapter(kategoriMerchantAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMerchant(listKategori.get(kategoriMerchantAdapter.getPosition_aktif()).getId_kategori());
    }
}
