package com.example.bappeda.MenuMerchants;

import androidx.appcompat.app.AppCompatActivity;

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

import androidx.appcompat.widget.Toolbar;

import com.example.bappeda.Adapter.SurveyAdapter;
import com.example.bappeda.Model.CategoryModel;
import com.example.bappeda.Model.MerchantModel;
import com.example.bappeda.R;
import com.example.bappeda.Utils.ApiVolley;
import com.example.bappeda.Utils.AppLoadingScreen;
import com.example.bappeda.Utils.ItemValidation;
import com.example.bappeda.Utils.Preferences;
import com.example.bappeda.Utils.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class UbahLokasiMerchantActivity extends AppCompatActivity {

    private int start = 0, count = 10;
    private View footerList;
    private boolean isLoading = false;
    private String keyword = "";
    private EditText edtSearch;
    private ListView lvMerchant;
    private ItemValidation iv = new ItemValidation();
    private SurveyAdapter adapter;
    private ArrayList<MerchantModel> merchantModels = new ArrayList<>();
    private final String TAG = "UbahLokasi";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubah_lokasi_merchant);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar()!=null){
            getSupportActionBar().setTitle("Merchant Tutup");
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        initUI();
        initEvent();
    }

    private void initUI() {

        edtSearch = (EditText) findViewById(R.id.edt_search);
        lvMerchant = (ListView) findViewById(R.id.lv_merchant);

        LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        footerList = li.inflate(R.layout.footer_list, null);

    }

    private void initEvent() {

        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if(actionId == EditorInfo.IME_ACTION_SEARCH){

                    start = 0;
                    keyword = edtSearch.getText().toString();
                    initData();
                    iv.hideSoftKey(UbahLokasiMerchantActivity.this);
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

        merchantModels = new ArrayList<>();
        adapter = new SurveyAdapter(UbahLokasiMerchantActivity.this, R.layout.activity_list_view_survey, merchantModels);
        lvMerchant.setAdapter(adapter);

        lvMerchant.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

            }
        });

        lvMerchant.addFooterView(footerList);
        lvMerchant.setAdapter(adapter);
        lvMerchant.removeFooterView(footerList);
        lvMerchant.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

                int threshold = 1;
                int countMerchant = lvMerchant.getCount();

                if (i == SCROLL_STATE_IDLE) {
                    if (lvMerchant.getLastVisiblePosition() >= countMerchant - threshold && !isLoading) {

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
    }

    private void initData() {

        isLoading = true;
        AppLoadingScreen.getInstance().showLoading(this);
        final String idUser = Preferences.getId(getBaseContext());

        JSONObject body = new JSONObject();
        try {
            body.put("start", String.valueOf(start));
            body.put("count", String.valueOf(count));
            body.put("keyword", keyword);
        } catch (JSONException e) {
            e.printStackTrace();
            if (e.getMessage()!=null){
                Log.e("error.body", e.getMessage());
            }
        }

        new ApiVolley(UbahLokasiMerchantActivity.this, body, "POST", URL.getJadwalMerchantTertutup,
                new ApiVolley.VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {

                        AppLoadingScreen.getInstance().stopLoading();
                        isLoading = false;
                        lvMerchant.removeFooterView(footerList);
                        if(start == 0) merchantModels.clear();
                        Log.d(TAG, "Response" + result);

                        try {
                            JSONObject object = new JSONObject(result);
                            String message = object.getJSONObject("metadata").getString("message");
                            int status = object.getJSONObject("metadata").getInt("status");
                            if (status==200){
                                JSONArray array = object.getJSONArray("response");
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject dataObject = array.getJSONObject(i);
                                    MerchantModel merchantModel = new MerchantModel();
                                    merchantModel.setId(dataObject.getString("id"));
                                    merchantModel.setNamamerchant(dataObject.getString("nama"));
                                    merchantModel.setAlamat(dataObject.getString("alamat"));
                                    merchantModel.setNamapemilik(dataObject.getString("pemilik"));
                                    merchantModel.setNotelp(dataObject.getString("no_telp"));
                                    merchantModel.setLongitude(iv.parseNullDouble(dataObject.getString("longitude")));
                                    merchantModel.setLatitude(iv.parseNullDouble(dataObject.getString("latitude")));
                                    merchantModel.setFlag(dataObject.getString("flag"));
                                    CategoryModel kategori = new CategoryModel();
                                    kategori.setIdKategori(dataObject.getString("kategori"));
                                    merchantModel.setKategori(kategori);

                                    ArrayList<String> gambar = new ArrayList<>();
                                    final JSONArray arrray = dataObject.getJSONArray("image");
                                    for (int p=0; p<arrray.length(); p++){
                                        gambar.add(arrray.getJSONObject(p).getString("image"));
                                    }
                                    merchantModel.setImages(gambar);
                                    merchantModels.add(merchantModel);
                                }

                            } else {

                                Log.d(TAG, "onSuccess" + message);
                                Toast.makeText(UbahLokasiMerchantActivity.this, message, Toast.LENGTH_SHORT).show();
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

                        lvMerchant.removeFooterView(footerList);
                        merchantModels.clear();
                        adapter.notifyDataSetChanged();
                        isLoading = false;
                        Log.e("Error.Response", result);
                        Toast.makeText(UbahLokasiMerchantActivity.this, R.string.error_message, Toast.LENGTH_SHORT).show();
                        AppLoadingScreen.getInstance().stopLoading();
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
                    startActivity(new Intent(UbahLokasiMerchantActivity.this, RiwayatMerchantTutupActivity.class));
                }
            });
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

}
