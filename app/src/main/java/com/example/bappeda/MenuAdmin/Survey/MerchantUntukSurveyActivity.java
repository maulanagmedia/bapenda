package com.example.bappeda.MenuAdmin.Survey;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
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

public class MerchantUntukSurveyActivity extends AppCompatActivity {

    //LoadData
    private MerchantPenugasanAdapter adapter;
    private ListView listmerchant;
    private ArrayList<MerchantModel> merchantModels = new ArrayList<>();

    private ApiVolley apiVolley;
    private String status = "";
    private String message = "";

    private Integer idPetugas;
    private String idMerchant;

    private PetugasModel p;

    //UI di dialog
    private Dialog dialog;
    private TextView namapetugas, emailpetugas, namamerchant, alamatmerchant, isitanggal;
    private EditText keterangan, searchmerchant, judulSurvey;
    private Button kirimsurvey;
    private CardView tanggal;
    private Calendar myCalendar;

    //Kategori Merchant
    private RecyclerView rvKategori;
    private ArrayList<KategoriMerchantModel> list = new ArrayList<>();
    private KategoriMerchantAdapter kategoriMerchantAdapter;

    private final String TAG = "MerchantSurvey";

    //Dialog konfirmasi
    private Dialog confirm_dialog;
    private ImageView checked;
    private CardView simpan, batal;
    private TextView judul;
    private TextView cancel, save; //di CardView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant_untuk_penugasan);

        searchmerchant = findViewById(R.id.edt_search);
        rvKategori = findViewById(R.id.rv_kategori);
        listmerchant = findViewById(R.id.list_merchant);
        Toolbar toolbar = findViewById(R.id.toolbar);
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

        rvKategori.setHasFixedSize(true);
        list.addAll(KategoriMerchantData.getListData());
        showListKategori();

        searchmerchant.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                //charSequence = parameter is the text before any change is applied.
                //start = the position of the beginning of the changed part in the text.
                //count = the length of the changed part in the s sequence since the start position.
                //after = the length of the new sequence which will replace the part of the charSequence from start to start+count.
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                //charSequence = parameter is the text after changes have been applied.
                //start = is the position of the beginning of the changed part in the text.
                //count = is the after parameter in the beforeTextChanged method.
                //before = is the length of the changed part in the s sequence since the start position.
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //You can change the text in the TextView from this method.
                // Warning: When you change the text in the TextView, the TextWatcher will be triggered again,
                // starting an infinite loop. You should then add like a boolean _ignore property which prevent
                // the infinite loop.
                loadMerchant(list.get(kategoriMerchantAdapter.position_aktif).getId_kategori(), searchmerchant.getText().toString());
            }
        });

        if (getIntent().hasExtra("id_petugas")){
            Gson gson = new Gson();
            p = gson.fromJson(getIntent().getStringExtra("id_petugas"), PetugasModel.class);
        }
        listmerchant.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                dialog = DialogFactory.getInstance().createDialogTrans(MerchantUntukSurveyActivity.this,
                        R.layout.popup_tugas_survey, 100);

                //Inisialisasi UI di dialog
                namapetugas = dialog.findViewById(R.id.txtpetugas);
                emailpetugas = dialog.findViewById(R.id.txtemail);
                namamerchant = dialog.findViewById(R.id.txtmerchant);
                alamatmerchant = dialog.findViewById(R.id.txtalamat);
                keterangan = dialog.findViewById(R.id.edt_keterangan);
                kirimsurvey = dialog.findViewById(R.id.button_kirimtugas);
                tanggal = dialog.findViewById(R.id.CardTanggal);
                isitanggal = dialog.findViewById(R.id.txttanggal);
                judulSurvey = dialog.findViewById(R.id.edt_judulSurvey);

                initDataPetugas(p);//menampilkan data dari activity sebelumnya melalui model (Petugas Model)
                tanggalFormat();
                DateCalendar();

                //set Value
                idMerchant = merchantModels.get(position).getId();
                namamerchant.setText(merchantModels.get(position).getNamamerchant());
                alamatmerchant.setText(merchantModels.get(position).getAlamat());
                judulSurvey.setText(R.string.judul_survey);
                keterangan.setText(R.string.survey);

                kirimsurvey.setOnClickListener(new View.OnClickListener() {
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
                createDialog(MerchantUntukSurveyActivity.this, R.layout.popup_confirm, 90);

        checked = confirm_dialog.findViewById(R.id.img_check);
        simpan = confirm_dialog.findViewById(R.id.CardSimpan);
        batal = confirm_dialog.findViewById(R.id.CardCancel);
        save = confirm_dialog.findViewById(R.id.textSimpan);
        cancel = confirm_dialog.findViewById(R.id.textCancel);
        judul = confirm_dialog.findViewById(R.id.textJudulConfirm);
        checked.setImageResource(R.drawable.checked);
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

    private void initDataPetugas(PetugasModel p){
        idPetugas = p.getIdpetugas();
        namapetugas.setText(p.getNamapetugas());
        emailpetugas.setText(p.getEmail());
    }

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
                new DatePickerDialog(MerchantUntukSurveyActivity.this, date_awal, myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void updateDate() {
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        isitanggal.setText(sdf.format(myCalendar.getTime()));
    }

    private void tanggalFormat(){
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        Date myDate = new Date();
        isitanggal.setText(sdf.format(myDate));
    }

    public void loadMerchant(String kategori, String search){

        JSONObject body = new JSONObject();
        try {
            body.put("kategori", kategori);
            body.put("keyword", search);
        } catch (JSONException e) {
            e.printStackTrace();
            if (e.getMessage()!=null){
                Log.d(TAG, "error.body" + e.getMessage());
            }
        }

        apiVolley = new ApiVolley(this, body , "POST", URL.URL_MERCHANT_PENUGASAN, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                Log.d("Response", result);
                try {
                    JSONObject object = new JSONObject(result);
                    int status = object.getJSONObject("metadata").getInt("status");
                    String message;
                    if(status== 200){
                        message = object.getJSONObject("metadata").getString("message");
                        JSONArray array = object.getJSONArray("response");
                        merchantModels = new ArrayList<>();
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
                        adapter = new MerchantPenugasanAdapter(getApplication(), R.layout.list_merchant_tugas_survey, merchantModels);
                        listmerchant.setAdapter(adapter);
                    }else{
                        message = object.getJSONObject("metadata").getString("message");
                        Toast.makeText(MerchantUntukSurveyActivity.this, message, Toast.LENGTH_SHORT).show();
                        merchantModels.clear();
                        adapter.notifyDataSetChanged();
                    }
                    Log.d(TAG, "onSuccess: " + message);
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
                Toast.makeText(MerchantUntukSurveyActivity.this, R.string.error_message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void TambahData(){

        AppLoadingScreen.getInstance().showLoading(MerchantUntukSurveyActivity.this);

        final String idP = idPetugas.toString();
        final String idM = idMerchant;
        final String ket = keterangan.getText().toString();
        final String tgl = isitanggal.getText().toString();
        final String judul = judulSurvey.getText().toString();

        JSONObject body = new JSONObject();
        try {
            body.put("id_petugas", idP);
            body.put("id_merchant", idM);
            body.put("tgl_survey", tgl);
            body.put("title", judul);
            body.put("keterangan", ket);
        } catch (JSONException e) {
            e.printStackTrace();
            if (e.getMessage()!=null){
                Log.d(TAG,"error.response" + e.getMessage());
            }
        }

        apiVolley = new ApiVolley(MerchantUntukSurveyActivity.this, body, "POST", URL.URL_KIRIM_SURVEY, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                AppLoadingScreen.getInstance().stopLoading();
                Log.d("Response", result);
                try {
                    JSONObject response = new JSONObject(result);
                    int status = response.getJSONObject("metadata").getInt("status");
                    String message;
                    if(status== 200){
                        message = response.getJSONObject("metadata").getString("message");
                        Toast.makeText(MerchantUntukSurveyActivity.this, message, Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }else{
                        message = response.getJSONObject("metadata").getString("message");
                    }
                    Log.d(TAG, "onSuccess: " + message);
                } catch (JSONException e) {
                    e.printStackTrace();
                    AppLoadingScreen.getInstance().stopLoading();
                    if (e.getMessage()!=null){
                        Log.d(TAG,"catch.response" + e.getMessage());
                    }
                }
            }

            @Override
            public void onError(String result) {
                AppLoadingScreen.getInstance().stopLoading();
                Toast.makeText(MerchantUntukSurveyActivity.this, R.string.error_message, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Error.Response" + result);
            }
        });
    }

    private void showListKategori(){
        rvKategori.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        kategoriMerchantAdapter= new KategoriMerchantAdapter(this, list);
        rvKategori.setAdapter(kategoriMerchantAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMerchant(list.get(kategoriMerchantAdapter.position_aktif).getId_kategori(), "");
    }
}
