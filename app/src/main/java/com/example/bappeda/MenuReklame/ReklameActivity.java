package com.example.bappeda.MenuReklame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bappeda.Adapter.DaftarMerchantAdapter;
import com.example.bappeda.MenuMonitoring.RiwayatMonitoringMerchantActivity;
import com.example.bappeda.Model.CategoryModel;
import com.example.bappeda.Model.MerchantModel;
import com.example.bappeda.R;
import com.example.bappeda.Utils.ApiVolley;
import com.example.bappeda.Utils.AppLoadingScreen;
import com.example.bappeda.Utils.CustomModel;
import com.example.bappeda.Utils.ItemValidation;
import com.example.bappeda.Utils.URL;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ReklameActivity extends AppCompatActivity {

    private ListView lvReklame;
    private DaftarMerchantAdapter adapter;
    private ArrayList<MerchantModel> listItem = new ArrayList<>();

    private ApiVolley apiVolley;
    private String status = "";
    private String message = "";
    private ItemValidation iv = new ItemValidation();
    private int start = 0, count = 10;
    private View footerList;
    private boolean isLoading = false;
    private String keyword = "";
    private Activity context;
    private EditText edtSearch;
    private final String TAG = "Reklame";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reklame);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle("Reklame");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        context = this;
        initUI();
    }

    private void initUI() {

        edtSearch = (EditText) findViewById(R.id.edt_search);
        lvReklame = (ListView) findViewById(R.id.lv_reklame);
        LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        footerList = li.inflate(R.layout.footer_list, null);

        adapter = new DaftarMerchantAdapter(context, R.layout.activity_list_view_survey, listItem);
        lvReklame.setAdapter(adapter);
        isLoading = false;

        lvReklame.addFooterView(footerList);
        lvReklame.setAdapter(adapter);
        lvReklame.removeFooterView(footerList);
        lvReklame.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

                int threshold = 1;
                int countMerchant = lvReklame.getCount();

                if (i == SCROLL_STATE_IDLE) {
                    if (lvReklame.getLastVisiblePosition() >= countMerchant - threshold && !isLoading) {

                        isLoading = true;
                        start += count;
                        initData();
                        //Log.i(TAG, "onScroll: last ");
                    }
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });

        lvReklame.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                MerchantModel item = (MerchantModel) adapterView.getItemAtPosition(i);

                Intent intent = new Intent(context, DetailReklameActivity.class);
                Gson gson = new Gson();
                intent.putExtra("merchant", gson.toJson(item));
                startActivity(intent);
            }
        });

        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if(actionId == EditorInfo.IME_ACTION_SEARCH){

                    start = 0;
                    keyword = edtSearch.getText().toString();
                    initData();
                    iv.hideSoftKey(context);
                    return true;
                }
                return false;
            }
        });

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(edtSearch.getText().toString().length() == 0){

                    start = 0;
                    keyword = "";
                    initData();
                }
            }
        });
    }

    private void initData(){

        isLoading = true;
        AppLoadingScreen.getInstance().showLoading(this);
        JSONObject jBody = new JSONObject();
        try {
            jBody.put("keyword", keyword);
            jBody.put("start", String.valueOf(start));
            jBody.put("count", String.valueOf(count));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new ApiVolley(context, jBody, "POST", URL.getReklame, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                AppLoadingScreen.getInstance().stopLoading();
                Log.d(TAG, "Response" + result);
                lvReklame.removeFooterView(footerList);
                if(start == 0) listItem.clear();

                try {

                    isLoading = false;
                    JSONObject response = new JSONObject(result);
                    int status = response.getJSONObject("metadata").getInt("status");
                    String message = response.getJSONObject("metadata").getString("message");

                    if (status == 200){

                        JSONArray array = response.getJSONArray("response");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject dataObject = array.getJSONObject(i);
                            MerchantModel merchantModel = new MerchantModel();
                            merchantModel.setId(dataObject.getString("id"));
                            merchantModel.setNamamerchant(dataObject.getString("nama"));
                            merchantModel.setAlamat(dataObject.getString("alamat"));
                            merchantModel.setNamapemilik(dataObject.getString("pemilik"));
                            merchantModel.setNotelp(dataObject.getString("telp_usaha"));
                            merchantModel.setLongitude(iv.parseNullDouble(dataObject.getString("longitude")));
                            merchantModel.setLatitude(iv.parseNullDouble(dataObject.getString("latitude")));
                            CategoryModel kategori = new CategoryModel();
                            kategori.setIdKategori(dataObject.getString("id_kategori"));
                            merchantModel.setKategori(kategori);

                            ArrayList<String> gambar = new ArrayList<>();
                            final JSONArray arrray = dataObject.getJSONArray("image");
                            for (int p=0; p < arrray.length(); p++){
                                gambar.add(arrray.getJSONObject(p).getString("image"));
                            }
                            merchantModel.setImages(gambar);
                            listItem.add(merchantModel);
                        }

                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (e.getMessage()!=null){
                        Log.e(TAG, "notif_log" + e.getMessage());
                    }
                }
            }

            @Override
            public void onError(String result) {

                isLoading = false;
                AppLoadingScreen.getInstance().stopLoading();
                lvReklame.removeFooterView(footerList);
                listItem.clear();
                adapter.notifyDataSetChanged();
                Toast.makeText(context, R.string.error_message, Toast.LENGTH_SHORT).show();
                Log.e(TAG, "notif_log" + result);
            }
        });
    }

    @Override
    protected void onResume() {

        super.onResume();
        start = 0;
        initData();
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

                    startActivity(new Intent(context, RiwayatMonitoringMerchantActivity.class));
                }
            });
        }
        return true;
    }
}
