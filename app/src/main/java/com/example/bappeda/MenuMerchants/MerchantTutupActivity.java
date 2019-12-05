package com.example.bappeda.MenuMerchants;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bappeda.Adapter.ImagesAdapter;
import com.example.bappeda.Adapter.SurveyAdapter;
import com.example.bappeda.Model.CategoryModel;
import com.example.bappeda.Model.ImagesModel;
import com.example.bappeda.Model.MerchantModel;
import com.example.bappeda.R;
import com.example.bappeda.Utils.ApiVolley;
import com.example.bappeda.Utils.AppLoadingScreen;
import com.example.bappeda.Utils.Converter;
import com.example.bappeda.Utils.DialogFactory;
import com.example.bappeda.Utils.ImageLoader;
import com.example.bappeda.Utils.ItemValidation;
import com.example.bappeda.Utils.Preferences;
import com.example.bappeda.Utils.URL;
import com.fxn.pix.Pix;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MerchantTutupActivity extends AppCompatActivity {

    private final String TAG = "MerchantTutupActivity";

    private EditText searchmerchant;

    private int start = 0, count = 10;
    private View footerList;
    private boolean isLoading = false;
    private String keyword = "";

    private ListView listMerchantTutup;
    private ApiVolley apiVolley;
    private SurveyAdapter adapter;
    private ArrayList<MerchantModel> merchantModels = new ArrayList<>();

    //Popup dialog
    private Dialog dialog;
    private ImageView imageMerchant;
    private TextView namaMerchant, alamatMerchant;
    private EditText keteranganTutup;
    private CardView cardTutup;

    private String idMerchant;
    private String flag;
    private String npwpdwp;

    //Dialog konfirmasi
    private Dialog confirm_dialog;
    private CardView simpan, batal;
    private TextView judul;
    private TextView cancel, save; //di CardView

    private ImagesAdapter imageAdapter;
    public ArrayList<ImagesModel> list_images = new ArrayList<>();

    private ItemValidation iv = new ItemValidation();
    private RecyclerView recyclerImages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant_tutup);

        //Inisialisasi UI
        listMerchantTutup = findViewById(R.id.list_merchantTutup);
        searchmerchant = findViewById(R.id.edt_search);
        LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        footerList = li.inflate(R.layout.footer_list, null);

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

        searchmerchant.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if(actionId == EditorInfo.IME_ACTION_SEARCH){

                    start = 0;
                    keyword = searchmerchant.getText().toString();
                    loadMerchant();
                    iv.hideSoftKey(MerchantTutupActivity.this);
                    return true;
                }
                return false;
            }
        });

        searchmerchant.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(searchmerchant.getText().toString().length() == 0){

                    start = 0;
                    keyword = "";
                    loadMerchant();
                }
            }
        });

        merchantModels = new ArrayList<>();
        adapter = new SurveyAdapter(MerchantTutupActivity.this, R.layout.activity_list_view_survey, merchantModels);
        listMerchantTutup.setAdapter(adapter);

        listMerchantTutup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                dialog = DialogFactory.getInstance().createDialog(MerchantTutupActivity.this, R.layout.popup_merchant_tutup, 90);

                //Inisialisasi UI di dialog
                imageMerchant = dialog.findViewById(R.id.image_merchant);
                namaMerchant = dialog.findViewById(R.id.NamaMerchant);
                alamatMerchant = dialog.findViewById(R.id.AlamatMerchant);
                keteranganTutup = dialog.findViewById(R.id.edt_keteranganTutup);
                cardTutup = dialog.findViewById(R.id.CardTutupMerchant);
                recyclerImages = dialog.findViewById(R.id.recyclerView);

                list_images.clear();
                recyclerImages.setLayoutManager(new LinearLayoutManager(MerchantTutupActivity.this, LinearLayoutManager.HORIZONTAL, false));
                imageAdapter = new ImagesAdapter(MerchantTutupActivity.this, list_images);
                recyclerImages.setAdapter(imageAdapter);

                // set Value
                idMerchant = merchantModels.get(position).getId();
                flag = merchantModels.get(position).getFlag();
                npwpdwp = merchantModels.get(position).getNpwpdwp();
                namaMerchant.setText(merchantModels.get(position).getNamamerchant());
                alamatMerchant.setText(merchantModels.get(position).getAlamat());
                String gambar = merchantModels.get(position).getImage();
                ImageLoader.load(MerchantTutupActivity.this, gambar, imageMerchant);

                cardTutup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showDialogConfirm();
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

        listMerchantTutup.addFooterView(footerList);
        listMerchantTutup.setAdapter(adapter);
        listMerchantTutup.removeFooterView(footerList);
        listMerchantTutup.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

                int threshold = 1;
                int countMerchant = listMerchantTutup.getCount();

                if (i == SCROLL_STATE_IDLE) {
                    if (listMerchantTutup.getLastVisiblePosition() >= countMerchant - threshold && !isLoading) {

                        isLoading = true;
                        start += count;
                        loadMerchant();
                        //Log.i(TAG, "onScroll: last ");
                    }
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });

    }

    //Dialog
    private void showDialogConfirm(){
        confirm_dialog = DialogFactory.getInstance().
                createDialog(MerchantTutupActivity.this, R.layout.popup_confirm, 90);

        simpan = confirm_dialog.findViewById(R.id.CardSimpan);
        batal = confirm_dialog.findViewById(R.id.CardCancel);
        save = confirm_dialog.findViewById(R.id.textSimpan);
        cancel = confirm_dialog.findViewById(R.id.textCancel);
        judul = confirm_dialog.findViewById(R.id.textJudulConfirm);
        judul.setText(R.string.confirm_tutup);
        save.setText(R.string.confirm_close);
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
                tambahMerchantTutup();
                confirm_dialog.dismiss();
                loadMerchant();
            }
        });

        confirm_dialog.show();
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == URL.CODE_UPLOAD){
            if (data!=null){
                ArrayList<String> returnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);

                if(returnValue!=null){
                    for(String s : returnValue){
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),
                                    Uri.fromFile(new File(s)));
                            list_images.add(new ImagesModel(bitmap));
                        } catch (IOException e) {
                            e.printStackTrace();
                            if (e.getMessage()!=null){
                                Log.e("_log", e.getMessage());
                            }
                        }
                    }
                    imageAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    private void loadMerchant(){

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

        apiVolley = new ApiVolley(MerchantTutupActivity.this, body, "POST", URL.getJadwalMerchantTertutup,
                new ApiVolley.VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {

                        AppLoadingScreen.getInstance().stopLoading();
                        isLoading = false;
                        listMerchantTutup.removeFooterView(footerList);
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
                                Toast.makeText(MerchantTutupActivity.this, message, Toast.LENGTH_SHORT).show();
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

                        listMerchantTutup.removeFooterView(footerList);
                        merchantModels.clear();
                        adapter.notifyDataSetChanged();
                        isLoading = false;
                        Log.e("Error.Response", result);
                        Toast.makeText(MerchantTutupActivity.this, R.string.error_message, Toast.LENGTH_SHORT).show();
                        AppLoadingScreen.getInstance().stopLoading();
                    }
                });
    }

    private void tambahMerchantTutup(){
        String idUser = Preferences.getId(this);

        if (keteranganTutup.getText().toString().isEmpty()){
            keteranganTutup.setError("Alasan harus diisi");
            keteranganTutup.requestFocus();
            return;
        }

        ArrayList<String> listImageString = new ArrayList<>();
        for(ImagesModel i : list_images){
            listImageString.add(Converter.convertToBase64(i.getBitmap()));
        }

        JSONObject body = new JSONObject();
        try {
            body.put("id_petugas", idUser);
            body.put("id_merchant", idMerchant);
            body.put("t_npwpdwp", idMerchant);
            body.put("alasan_tutup", keteranganTutup.getText().toString());
            body.put("flag", flag);
            body.put("foto", new JSONArray(listImageString));
        } catch (JSONException e) {
            e.printStackTrace();
            if (e.getMessage()!=null){
                Log.d(TAG, "body_log: " + e.getMessage());
            }
        }

        AppLoadingScreen.getInstance().showLoading(this);

        new ApiVolley(MerchantTutupActivity.this, body, "POST", URL.URL_ADD_MERCHANT_TUTUP, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                Log.d(TAG, "Response" + result);
                try {
                    JSONObject object = new JSONObject(result);
                    String message = object.getJSONObject("metadata").getString("message");
                    int status = object.getJSONObject("metadata").getInt("status");
                    if (status==200){
                        AppLoadingScreen.getInstance().stopLoading();
                        Toast.makeText(MerchantTutupActivity.this, message, Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } else {
                        AppLoadingScreen.getInstance().stopLoading();
                        Toast.makeText(MerchantTutupActivity.this, message, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(MerchantTutupActivity.this, R.string.error_message, Toast.LENGTH_SHORT).show();
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
                    startActivity(new Intent(MerchantTutupActivity.this, RiwayatMerchantTutupActivity.class));
                }
            });
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMerchant();
    }
}
