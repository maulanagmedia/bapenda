package com.example.bappeda.MenuAdmin.HubungiPetugas;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
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

import com.example.bappeda.MenuAdmin.Adapter.JabatanAdapter;
import com.example.bappeda.MenuAdmin.Adapter.PetugasAdapter;
import com.example.bappeda.MenuAdmin.Data.JabatanData;
import com.example.bappeda.MenuAdmin.Model.JabatanModel;
import com.example.bappeda.MenuAdmin.Model.PetugasModel;
import com.example.bappeda.R;
import com.example.bappeda.Utils.ApiVolley;
import com.example.bappeda.Utils.AppLoadingScreen;
import com.example.bappeda.Utils.DialogFactory;
import com.example.bappeda.Utils.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class PetugasUntukHubungi extends AppCompatActivity {

    //LoadData List Petugas
    private PetugasAdapter adapter;
    private ArrayList<PetugasModel> petugasModels = new ArrayList<>();
    private ListView listPetugas;

    private ApiVolley apiVolley;
    private String status = "";
    private String message = "";

    private Integer idPetugas;

    //UI di dialog
    private Dialog dialog;
    private TextView namapetugas, emailpetugas, isitanggal;
    private EditText keterangan, judulHubungi;
    private Button kirimtugas;
    private CardView tanggal;
    private Calendar myCalendar;

    private RecyclerView rvJabatan;
    private ArrayList<JabatanModel> list = new ArrayList<>();
    private JabatanAdapter jabatanAdapter;

    private EditText searchPetugas;

    //Dialog konfirmasi
    private Dialog confirm_dialog;
    private CardView simpan, batal;
    private TextView judul;
    private TextView cancel, save; //di CardView

    private final String TAG = "PetugasUntukHubungi";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_petugas_untuk_hubungi);

        searchPetugas = findViewById(R.id.edt_search);
        rvJabatan = findViewById(R.id.rv_jabatan);
        listPetugas = findViewById(R.id.list_petugasHubungi);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar()!=null){
            getSupportActionBar().setTitle("Data Petugas");
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        rvJabatan.setHasFixedSize(true);
        list.addAll(JabatanData.getListData());
        showListJabatan();

        searchPetugas.addTextChangedListener(new TextWatcher() {
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
                loadPetugas(list.get(jabatanAdapter.getPosition_aktif()).getId_level(), searchPetugas.getText().toString());
            }
        });

        listPetugas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
               dialog = DialogFactory.getInstance().createDialogTrans(PetugasUntukHubungi.this, R.layout.popup_hubungi_petugas, 100);

               //Inisiailisasi UI
               namapetugas = dialog.findViewById(R.id.txtpetugas);
               emailpetugas = dialog.findViewById(R.id.txtemail);
               keterangan = dialog.findViewById(R.id.edt_keterangan);
               kirimtugas = dialog.findViewById(R.id.button_kirimtugas);
               tanggal = dialog.findViewById(R.id.CardTanggal);
               isitanggal = dialog.findViewById(R.id.txttanggal);
               judulHubungi = dialog.findViewById(R.id.edt_judulHubungi);

               idPetugas = petugasModels.get(position).getIdpetugas();
               namapetugas.setText(petugasModels.get(position).getNamapetugas());
               emailpetugas.setText(petugasModels.get(position).getEmail());

               DateCalendar();
               tanggalFormat();

               kirimtugas.setOnClickListener(new View.OnClickListener() {
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
                createDialog(PetugasUntukHubungi.this, R.layout.popup_confirm, 90);

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

    public void loadPetugas(String level, String keyword){

        JSONObject body = new JSONObject();
        try {
            body.put("keyword", keyword);
            body.put("level", level);
        } catch (JSONException e) {
            e.printStackTrace();
            if (e.getMessage()!=null){
                Log.d(TAG, "error.body" + e.getMessage());
            }
        }

        apiVolley = new ApiVolley(PetugasUntukHubungi.this, body, "POST", URL.URL_DATA_PETUGAS, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                Log.d("Response", result);
                try {
                    JSONObject object = new JSONObject(result);
                    status = object.getJSONObject("metadata").getString("status");
                    message = object.getJSONObject("metadata").getString("message");
                    if (status.equals("200")){
                        JSONArray array = object.getJSONArray("response");
                        petugasModels = new ArrayList<>();
                        for (int i=0; i<array.length(); i++){
                            JSONObject dataObject = array.getJSONObject(i);
                            PetugasModel petugasModel = new PetugasModel();
                            petugasModel.setIdpetugas(dataObject.getInt("id"));
                            petugasModel.setNamapetugas(dataObject.getString("nama"));
                            petugasModel.setEmail(dataObject.getString("email"));
                            petugasModel.setImages(dataObject.getString("foto"));
                            petugasModels.add(petugasModel);
                        }
                        adapter = new PetugasAdapter(PetugasUntukHubungi.this, R.layout.list_petugas, petugasModels);
                        listPetugas.setAdapter(adapter);
                        Log.d(TAG, "onSuccess" + message);
                    } else {
                        AppLoadingScreen.getInstance().stopLoading();
                        petugasModels.clear();
                        adapter.notifyDataSetChanged();
                        Toast.makeText(PetugasUntukHubungi.this, message, Toast.LENGTH_SHORT).show();
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
                Log.e(TAG, "Error.Response" + result);
                Toast.makeText(PetugasUntukHubungi.this, R.string.error_message, Toast.LENGTH_SHORT).show();
                AppLoadingScreen.getInstance().stopLoading();
            }
        });
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
                new DatePickerDialog(PetugasUntukHubungi.this, date_awal, myCalendar.get(Calendar.YEAR),
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
        isitanggal.setText(sdf.format(new Date()));
    }

    private void TambahData(){

        AppLoadingScreen.getInstance().showLoading(PetugasUntukHubungi.this);

        final String idP = idPetugas.toString();
        final String ket = keterangan.getText().toString();
        final String judul = judulHubungi.getText().toString();
        final String tanggal = isitanggal.getText().toString();

        if (TextUtils.isEmpty(ket)){
            keterangan.setError("Keterangan Harus Diisi");
            keterangan.requestFocus();
            return;
        }

        JSONObject body = new JSONObject();
        try {
            body.put("id_petugas", idP);
            body.put("title", judul);
            body.put("date", tanggal);
            body.put("keterangan", ket);
        } catch (JSONException e) {
            e.printStackTrace();
            if (e.getMessage()!=null){
                Log.d(TAG, "error.body" + e.getMessage());
            }
        }

        apiVolley = new ApiVolley(PetugasUntukHubungi.this, body, "POST", URL.URL_HUBUNGI_PETUGAS, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                Log.d("Response", result);
                try {
                    JSONObject response = new JSONObject(result);
                    int status = response.getJSONObject("metadata").getInt("status");
                    String message;
                    if(status== 200){
                        message = response.getJSONObject("metadata").getString("message");
                        Toast.makeText(PetugasUntukHubungi.this, message, Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }else{
                        message = response.getJSONObject("metadata").getString("message");
                    }
                    Log.d(TAG, "onSuccess: " + message);
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (e.getMessage()!=null){
                        Log.d(TAG, "catch.response" + e.getMessage());
                    }
                }
                AppLoadingScreen.getInstance().stopLoading();
            }

            @Override
            public void onError(String result) {
                Log.d(TAG, "Error.Response" + result);
                AppLoadingScreen.getInstance().stopLoading();
                Toast.makeText(PetugasUntukHubungi.this, R.string.error_message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showListJabatan(){
        rvJabatan.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        jabatanAdapter = new JabatanAdapter(this, list);
        rvJabatan.setAdapter(jabatanAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPetugas(list.get(jabatanAdapter.getPosition_aktif()).getId_level(), searchPetugas.getText().toString());
    }
}
