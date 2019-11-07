package com.example.bappeda.MenuAdmin.Survey;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import com.example.bappeda.Utils.URL;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PetugasSurveyActivity extends AppCompatActivity {

    //LoadData List Petugas
    private PetugasAdapter adapter;
    private ArrayList<PetugasModel> petugasModels = new ArrayList<>();
    private ListView listPetugas;

    ApiVolley apiVolley;
    String status = "";
    String message = "";

    //Kategori jabatan
    RecyclerView rvJabatan;
    private ArrayList<JabatanModel> list = new ArrayList<>();
    JabatanAdapter jabatanAdapter;

    EditText search;
    String keyword;
    String value;

    private final String TAG = "PetugasSurvey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_petugas_survey);

        search = findViewById(R.id.edt_search);
        rvJabatan = findViewById(R.id.rv_jabatan);
        listPetugas = findViewById(R.id.list_petugas);
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

        search.addTextChangedListener(new TextWatcher() {
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
                loadPetugas(list.get(jabatanAdapter.getPosition_aktif()).getId_level(), search.getText().toString());
            }
        });

        listPetugas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                PetugasModel itemList = (PetugasModel) listPetugas.getItemAtPosition(i);
                Intent intent = new Intent(PetugasSurveyActivity.this, MerchantUntukSurveyActivity.class);
                Gson gson = new Gson();
                intent.putExtra("id_petugas", gson.toJson(itemList));
                startActivity(intent);
            }
        });
    }

    public void loadPetugas(String level, String keyword){

        JSONObject body = new JSONObject();
        try {
            body.put("keyword", keyword);
            body.put("level", level);
            Log.d("_log", keyword);
        } catch (JSONException e) {
            e.printStackTrace();
            if (e.getMessage()!=null){
                Log.d(TAG, "error.body" + e.getMessage());
            }
        }

        apiVolley = new ApiVolley(PetugasSurveyActivity.this, body, "POST", URL.URL_DATA_PETUGAS, new ApiVolley.VolleyCallback() {
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
                        adapter = new PetugasAdapter(PetugasSurveyActivity.this, R.layout.list_petugas, petugasModels);
                        listPetugas.setAdapter(adapter);
                        Log.d(TAG, "onSuccess" + message);
                    } else {
                        AppLoadingScreen.getInstance().stopLoading();
                        petugasModels.clear();
                        adapter.notifyDataSetChanged();
                        Toast.makeText(PetugasSurveyActivity.this, message, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(PetugasSurveyActivity.this, R.string.error_message, Toast.LENGTH_SHORT).show();
                AppLoadingScreen.getInstance().stopLoading();
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
        loadPetugas(list.get(jabatanAdapter.getPosition_aktif()).getId_level(), "");
    }
}
