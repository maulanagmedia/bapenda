package com.example.bappeda.MenuPendaftaran;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.bappeda.Adapter.DialogChooserAdapter;
import com.example.bappeda.Adapter.ImagesAdapter;
import com.example.bappeda.Model.CategoryModel;
import com.example.bappeda.Model.ImagesModel;
import com.example.bappeda.Model.MerchantModel;
import com.example.bappeda.R;
import com.example.bappeda.Signature.SignatureBuilder;
import com.example.bappeda.Utils.ApiVolley;
import com.example.bappeda.Utils.AppLoadingScreen;
import com.example.bappeda.Utils.DialogFactory;
import com.example.bappeda.Utils.JSONBuilder;
import com.example.bappeda.Utils.Preferences;
import com.example.bappeda.Utils.ScrollableMapView;
import com.example.bappeda.Utils.URL;
import com.fxn.pix.Pix;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.example.bappeda.Utils.Converter.convertToBase64;

public class PendaftaranWajibPajakActivity extends AppCompatActivity implements
        OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private MerchantModel merchant;
    private String jenis_usaha = "";

    private GoogleMap mGoogleMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private double lat, lng;
    private MarkerOptions options;

    public static ArrayList<ImagesModel> list_images = new ArrayList<>();
    private ImagesAdapter imageAdapter;

    private TextView text_latitude, text_longitude;

    private LocationCallback locationCallback = new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult location) {
            if (location == null) {
                Toast.makeText(PendaftaranWajibPajakActivity.this,
                        "Gagal memperoleh lokasi", Toast.LENGTH_LONG).show();
            } else {
                lat = location.getLastLocation().getLatitude();
                lng = location.getLastLocation().getLongitude();
                LatLng ll = new LatLng(location.getLastLocation().getLatitude(), location.getLastLocation().getLongitude());
                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, 17);
                mGoogleMap.animateCamera(update);
                mGoogleMap.clear();
                options = new MarkerOptions()
                        .position(ll)
                        .draggable(true)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                mGoogleMap.addMarker(options);

                String lat_string = "Latitude : " + lat;
                String long_string = "Longitude : " + lng;

                text_latitude.setText(lat_string);
                text_longitude.setText(long_string);
            }
            //menghentikan pembaruan lokasi
            if (mGoogleApiClient != null) {
                LocationServices.getFusedLocationProviderClient(PendaftaranWajibPajakActivity.this).
                        removeLocationUpdates(locationCallback);
            }
        }
    };

    private SignatureBuilder mSignature;

    //Dialog konfirmasi
    private Dialog confirm_dialog;
    private CardView simpan, batal;
    private TextView judul;
    private TextView cancel, save; //di CardView

    //Variabel UI
    private EditText txt_nama_usaha, txt_alamat_usaha, txt_telp_usaha, txt_nama_pemilik,
        txt_nik_pemilik, txt_alamat_pemilik, txt_telp_pemilik, txt_keterangan;
    private Spinner spn_klasifikasi_usaha;
    private TextView txt_kota, txt_kecamatan, txt_kelurahan;

    private List<CategoryModel> listKategori = new ArrayList<>();
    private List<CategoryModel> listKota = new ArrayList<>();
    private List<CategoryModel> listKecamatan = new ArrayList<>();
    private List<CategoryModel> listKelurahan = new ArrayList<>();

    private CategoryModel selectedKota = new CategoryModel();
    private CategoryModel selectedKecamatan = new CategoryModel();
    private CategoryModel selectedKelurahan = new CategoryModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pendaftaran_wajib_pajak);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Input Data Survey");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //Inisialisasi UI
        txt_nama_usaha = findViewById(R.id.txt_nama_usaha);
        txt_alamat_usaha = findViewById(R.id.txt_alamat_usaha);
        txt_telp_usaha = findViewById(R.id.txt_telp_usaha);
        txt_nama_pemilik = findViewById(R.id.txt_nama_pemilik);
        txt_alamat_pemilik = findViewById(R.id.txt_alamat_pemilik);
        txt_nik_pemilik = findViewById(R.id.txt_nik_pemilik);
        txt_telp_pemilik = findViewById(R.id.txt_telp_pemilik);
        text_latitude = findViewById(R.id.textLatitude);
        text_longitude = findViewById(R.id.textLongitude);
        spn_klasifikasi_usaha = findViewById(R.id.spn_klasifikasi_usaha);
        txt_kota = findViewById(R.id.txt_kota);
        txt_kecamatan = findViewById(R.id.txt_kecamatan);
        txt_kelurahan = findViewById(R.id.txt_kelurahan);
        txt_keterangan = findViewById(R.id.txt_keterangan);
        RadioGroup rg = findViewById(R.id.rgBidangUsaha);

        txt_kota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showKotaDialog();
            }
        });

        txt_kecamatan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selectedKota.getIdKategori().equals("")){
                    Toast.makeText(PendaftaranWajibPajakActivity.this,
                            "Pilih kota terlebih dahulu", Toast.LENGTH_SHORT).show();
                }
                else{
                    showKecamatanDialog();
                }
            }
        });

        txt_kelurahan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selectedKecamatan.getIdKategori().equals("")){
                    Toast.makeText(PendaftaranWajibPajakActivity.this,
                            "Pilih kecamatan terlebih dahulu", Toast.LENGTH_SHORT).show();
                }
                else{
                    showKelurahanDialog();
                }
            }
        });

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.rbBadanPribadi:
                        jenis_usaha = "Badan Pribadi";
                        break;
                    case R.id.rbBadanUsaha:
                        jenis_usaha = "Badan Usaha";
                        break;
                }
            }
        });

        if(getIntent().hasExtra(URL.EXTRA_MERCHANT)){
            Gson gson = new Gson();
            merchant = gson.fromJson(getIntent().
                    getStringExtra(URL.EXTRA_MERCHANT), MerchantModel.class);
        }

        findViewById(R.id.btnreset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mGoogleApiClient == null){
                    initLocation();
                }
                else{
                    ResetLokasi();
                }
            }
        });

        findViewById(R.id.btnPreview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MerchantModel m = new MerchantModel();
                m.setId(merchant.getId());
                m.setIdPenugasan(merchant.getIdPenugasan());
                m.setNamamerchant(txt_nama_usaha.getText().toString());
                m.setAlamat(txt_alamat_usaha.getText().toString());
                m.setNotelp(txt_telp_usaha.getText().toString());
                m.setBadan_usaha(jenis_usaha.equals("Badan Usaha"));
                m.setNamapemilik(txt_nama_pemilik.getText().toString());
                m.setNik(txt_nik_pemilik.getText().toString());
                m.setAlamatpemilik(txt_alamat_pemilik.getText().toString());
                m.setNotelppemilik(txt_telp_pemilik.getText().toString());
                m.setKlasifikasi_usaha(listKategori.get(spn_klasifikasi_usaha.
                        getSelectedItemPosition()));
                m.setKeterangan(txt_keterangan.getText().toString());
                m.setLatitude(lat);
                m.setLongitude(lng);
                m.setKota(selectedKota);
                m.setKecamatan(selectedKecamatan);
                m.setKelurahan(selectedKelurahan);

                Gson gson = new Gson();
                Intent i = new Intent(PendaftaranWajibPajakActivity.this, PreviewActivity.class);
                i.putExtra(URL.EXTRA_MERCHANT, gson.toJson(m));
                startActivity(i);
            }
        });

        list_images.clear();
        RecyclerView recyclerImages = findViewById(R.id.recyclerView);
        recyclerImages.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        imageAdapter = new ImagesAdapter(this, list_images);
        recyclerImages.setAdapter(imageAdapter);

        findViewById(R.id.btnSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isInputValid()){
                    showSignature();
                }
            }
        });

        if (googleServicesAvailable()) {
            initMap();
        }

        loadKategori();
        loadKota();
    }

    private void showKotaDialog(){
        final Dialog dialogChooser = DialogFactory.getInstance().createDialog(this,
                R.layout.popup_chooser, 90, 70);

        //TextView txt_search = dialogKota.findViewById(R.id.txt_search);
        RecyclerView rv_items = dialogChooser.findViewById(R.id.rv_items);
        rv_items.setItemAnimator(new DefaultItemAnimator());
        rv_items.setLayoutManager(new LinearLayoutManager(this));
        DialogChooserAdapter adapter = new DialogChooserAdapter(this,
                listKota, new DialogChooserAdapter.ChooserListener() {
            @Override
            public void onSelected(String id, String value) {
                txt_kota.setText(value);
                selectedKota = new CategoryModel();
                selectedKota.setIdKategori(id);
                selectedKota.setNama(value);

                selectedKecamatan = new CategoryModel();
                selectedKelurahan = new CategoryModel();
                txt_kecamatan.setText("");
                txt_kelurahan.setText("");

                loadKecamatan();
                dialogChooser.dismiss();
            }
        });
        rv_items.setAdapter(adapter);
        dialogChooser.show();
    }

    private void showKecamatanDialog(){
        final Dialog dialogChooser = DialogFactory.getInstance().createDialog(this,
                R.layout.popup_chooser, 90, 70);

        //TextView txt_search = dialogKota.findViewById(R.id.txt_search);
        RecyclerView rv_items = dialogChooser.findViewById(R.id.rv_items);
        rv_items.setItemAnimator(new DefaultItemAnimator());
        rv_items.setLayoutManager(new LinearLayoutManager(this));
        DialogChooserAdapter adapter = new DialogChooserAdapter(this,
                listKecamatan, new DialogChooserAdapter.ChooserListener() {
            @Override
            public void onSelected(String id, String value) {
                txt_kecamatan.setText(value);
                selectedKecamatan = new CategoryModel();
                selectedKecamatan.setIdKategori(id);
                selectedKecamatan.setNama(value);

                selectedKelurahan = new CategoryModel();
                txt_kelurahan.setText("");

                loadKelurahan();
                dialogChooser.dismiss();
            }
        });
        rv_items.setAdapter(adapter);
        dialogChooser.show();
    }

    private void showKelurahanDialog(){
        final Dialog dialogChooser = DialogFactory.getInstance().createDialog(this,
                R.layout.popup_chooser, 90, 70);

        //TextView txt_search = dialogKota.findViewById(R.id.txt_search);
        RecyclerView rv_items = dialogChooser.findViewById(R.id.rv_items);
        rv_items.setItemAnimator(new DefaultItemAnimator());
        rv_items.setLayoutManager(new LinearLayoutManager(this));
        DialogChooserAdapter adapter = new DialogChooserAdapter(this,
                listKelurahan, new DialogChooserAdapter.ChooserListener() {
            @Override
            public void onSelected(String id, String value) {
                txt_kelurahan.setText(value);
                selectedKelurahan = new CategoryModel();
                selectedKelurahan.setIdKategori(id);
                selectedKelurahan.setNama(value);

                dialogChooser.dismiss();
            }
        });
        rv_items.setAdapter(adapter);
        dialogChooser.show();
    }

    private void initMerchant(){
        txt_nama_usaha.setText(merchant.getNamamerchant());
        txt_alamat_usaha.setText(merchant.getAlamat());
        txt_nama_pemilik.setText(merchant.getNamapemilik());
        txt_telp_usaha.setText(merchant.getNotelp());

        LatLng ll = new LatLng(merchant.getLatitude(), merchant.getLongitude());
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, 17);
        mGoogleMap.animateCamera(update);
        mGoogleMap.clear();
        options = new MarkerOptions()
                .position(ll)
                .draggable(true)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        mGoogleMap.addMarker(options);

        lat = merchant.getLatitude();
        lng = merchant.getLongitude();

        String lat_string = "Latitude : " + lat;
        String long_string = "Longitude : " + lng;

        text_latitude.setText(lat_string);
        text_longitude.setText(long_string);

        //Init foto
        ArrayList<String> listImagesUrl = merchant.getImages();
        Log.d("init_log", listImagesUrl.toString());
        if (listImagesUrl!=null){
            for(String url : listImagesUrl){
                Glide.with(this).asBitmap().load(url).listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        Log.e("glide_log", "load failed");
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        if(imageAdapter != null){
                            list_images.add(new ImagesModel(resource));
                            imageAdapter.notifyDataSetChanged();
                            return true;
                        }
                        else{
                            return false;
                        }
                    }
                }).preload();
            }
        }
    }

    private boolean isInputValid(){
        return true;
    }

    private void showSignature(){
        final Dialog signature_dialog = DialogFactory.getInstance().
                createDialog(this, R.layout.popup_signature, 90);

        final FrameLayout layout_signature = signature_dialog.findViewById(R.id.layout_signature);
        mSignature = new SignatureBuilder(this, null, layout_signature);
        mSignature.setBackgroundColor(Color.WHITE);
        layout_signature.addView(mSignature, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        signature_dialog.findViewById(R.id.btn_simpan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mSignature.getBitmap(layout_signature) == null){
                    Toast.makeText(PendaftaranWajibPajakActivity.this,
                            "Tanda tangan belum terisi", Toast.LENGTH_SHORT).show();
                }
                else{
                    showDialogConfirm(mSignature.getBitmap(layout_signature));
                    signature_dialog.dismiss();
                }
            }
        });

        signature_dialog.findViewById(R.id.img_refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSignature.clear();
            }
        });

        signature_dialog.show();
    }

    private void showDialogConfirm(final Bitmap bitmap_signature){
        confirm_dialog = DialogFactory.getInstance().
                createDialog(PendaftaranWajibPajakActivity.this, R.layout.popup_confirm, 90);

        simpan = confirm_dialog.findViewById(R.id.CardSimpan);
        batal = confirm_dialog.findViewById(R.id.CardCancel);
        save = confirm_dialog.findViewById(R.id.textSimpan);
        cancel = confirm_dialog.findViewById(R.id.textCancel);
        judul = confirm_dialog.findViewById(R.id.textJudulConfirm);
        judul.setText(R.string.confirm_simpan);
        save.setText(R.string.confirm_yes);
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
                updateData(bitmap_signature);
                confirm_dialog.dismiss();
            }
        });

        confirm_dialog.show();
    }

    private void updateData(Bitmap signature){

        AppLoadingScreen.getInstance().showLoading(this);
        String base64 = convertToBase64(signature);
        String idUser = Preferences.getId(PendaftaranWajibPajakActivity.this);

        ArrayList<String> listImageString = new ArrayList<>();
        for(ImagesModel i : list_images){
            listImageString.add(convertToBase64(i.getBitmap()));
        }

        JSONBuilder body = new JSONBuilder();body.add("id", merchant.getId());
        body.add("user_login", idUser);
        body.add("id", merchant.getId());
        body.add("idp", merchant.getIdPenugasan());
        body.add("nama", txt_nama_usaha.getText().toString());
        body.add("alamat", txt_alamat_usaha.getText().toString());
        body.add("telp_usaha", txt_telp_usaha.getText().toString());
        body.add("bidang_usaha", jenis_usaha);
        body.add("pemilik", txt_nama_pemilik.getText().toString());
        body.add("alamat_pemilik", txt_alamat_pemilik.getText().toString());
        body.add("nik_pemilik", txt_nik_pemilik.getText().toString());
        body.add("latitude", lat);
        body.add("longitude", lng);
        body.add("kategori", listKategori.get(spn_klasifikasi_usaha.
                getSelectedItemPosition()).getIdKategori());
        body.add("no_telp", txt_telp_pemilik.getText().toString());
        body.add("kelurahan", selectedKelurahan.getIdKategori());
        body.add("kecamatan", selectedKecamatan.getIdKategori());
        body.add("kota", selectedKota.getIdKategori());
        body.add("ttd", base64);
        body.add("image", new JSONArray(listImageString));


        new ApiVolley(this, body.create(), "POST", URL.URL_EDIT_MERCHANT,
                new ApiVolley.VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {
                        Log.d("wajibpajak_log", result);
                        try{
                            JSONObject response = new JSONObject(result);
                            String message =  response.getJSONObject("metadata").getString("message");
                            int status = response.getJSONObject("metadata").getInt("status");

                            if(status == 200){
                                Intent i = new Intent(PendaftaranWajibPajakActivity.this, FormIsianActivity.class);
                                i.putExtra(URL.EXTRA_ID_MERCHANT, merchant.getId());
                                i.putExtra(URL.EXTRA_IDP, merchant.getIdPenugasan());
                                i.putExtra(URL.EXTRA_ID_KATEGORI, listKategori.get(spn_klasifikasi_usaha.
                                        getSelectedItemPosition()).getIdKategori());
                                startActivity(i);
                            }
                            else{
                                Toast.makeText(PendaftaranWajibPajakActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                            Log.d("wajibpajak_log", message);
                        }
                        catch (JSONException e){
                            if (e.getMessage()!=null){
                                Log.e("wajibpajak_log", e.getMessage());
                            }
                        }
                        AppLoadingScreen.getInstance().stopLoading();
                    }

                    @Override
                    public void onError(String result) {
                        Toast.makeText(PendaftaranWajibPajakActivity.this, R.string.error_message, Toast.LENGTH_SHORT).show();
                        Log.e("wajibpajak_log", result);
                        AppLoadingScreen.getInstance().stopLoading();
                    }
                });
    }

    private void loadKategori(){
        AppLoadingScreen.getInstance().showLoading(this);
        new ApiVolley(this, new JSONObject(), "GET", URL.URL_CATEGORY,
                new ApiVolley.VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {
                        try{
                            JSONObject response = new JSONObject(result);
                            String message = response.getJSONObject("metadata").getString("message");
                            int status = response.getJSONObject("metadata").getInt("status");

                            if(status == 200){
                                JSONArray array = response.getJSONArray("response");
                                listKategori = new ArrayList<>();
                                for (int i=0; i<array.length(); i++){
                                    JSONObject dataObject = array.getJSONObject(i);
                                    CategoryModel categoryModel = new CategoryModel();
                                    categoryModel.setIdKategori(dataObject.getString("id"));
                                    categoryModel.setNama(dataObject.getString("kategori"));
                                    listKategori.add(categoryModel);
                                }

                                ArrayAdapter<CategoryModel> adapter = new ArrayAdapter<>(PendaftaranWajibPajakActivity.this,
                                        android.R.layout.simple_list_item_1, listKategori);
                                spn_klasifikasi_usaha.setAdapter(adapter);
                                initKategori();
                            }
                            else{
                                Toast.makeText(PendaftaranWajibPajakActivity.this,
                                        message, Toast.LENGTH_SHORT).show();
                            }
                        }
                        catch (JSONException e){
                            Toast.makeText(PendaftaranWajibPajakActivity.this,
                                    R.string.error_message, Toast.LENGTH_SHORT).show();
                            Log.e("kategori_log", result);
                        }

                        AppLoadingScreen.getInstance().stopLoading();
                    }

                    @Override
                    public void onError(String result) {
                        Toast.makeText(PendaftaranWajibPajakActivity.this,
                                R.string.error_message, Toast.LENGTH_SHORT).show();
                        Log.e("kategori_log", result);
                        AppLoadingScreen.getInstance().stopLoading();
                    }
                });
    }

    private void initKategori(){
        for(int i = 0; i < listKategori.size(); i++){
            if(listKategori.get(i).getIdKategori().equals(merchant.getKategori().getIdKategori())){
                spn_klasifikasi_usaha.setSelection(i);
                break;
            }
        }
    }

    private void loadKota(){
        new ApiVolley(this, new JSONObject(), "GET", URL.URL_MASTER_KOTA,
                new ApiVolley.VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {
                        Log.d("kota_log", result);
                        try{
                            JSONObject response = new JSONObject(result);
                            int status = response.getJSONObject("metadata").getInt("status");
                            String message = response.getJSONObject("metadata").getString("message");

                            if(status == 200){
                                listKota.clear();
                                JSONArray list_kota = response.getJSONArray("response");
                                for(int i = 0; i < list_kota.length(); i++){
                                    JSONObject kota = list_kota.getJSONObject(i);
                                    CategoryModel c = new CategoryModel();
                                    c.setIdKategori(kota.getString("id"));
                                    c.setNama(kota.getString("kota"));
                                    listKota.add(c);
                                }
                            }
                            else{
                                Toast.makeText(PendaftaranWajibPajakActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        }
                        catch (JSONException e){
                            Log.e("kota_log", result);
                        }
                    }

                    @Override
                    public void onError(String result) {
                        Toast.makeText(PendaftaranWajibPajakActivity.this, R.string.error_message, Toast.LENGTH_SHORT).show();
                        Log.e("kota_log", result);
                    }
                });
    }

    private void loadKecamatan(){
        JSONBuilder body = new JSONBuilder();
        body.add("id_kota", selectedKota.getIdKategori());

        new ApiVolley(this, body.create(), "POST", URL.URL_MASTER_KECAMATAN,
                new ApiVolley.VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {
                        Log.d("kecamatan_log", result);
                        try{
                            JSONObject response = new JSONObject(result);
                            int status = response.getJSONObject("metadata").getInt("status");
                            String message = response.getJSONObject("metadata").getString("message");

                            if(status == 200){
                                listKecamatan.clear();
                                JSONArray list_kota = response.getJSONArray("response");
                                for(int i = 0; i < list_kota.length(); i++){
                                    JSONObject kecamatan = list_kota.getJSONObject(i);
                                    CategoryModel c = new CategoryModel();
                                    c.setIdKategori(kecamatan.getString("id"));
                                    c.setNama(kecamatan.getString("kecamatan"));
                                    listKecamatan.add(c);
                                }
                            }
                            else{
                                Toast.makeText(PendaftaranWajibPajakActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        }
                        catch (JSONException e){
                            Log.e("kecamatan_log", result);
                        }
                    }

                    @Override
                    public void onError(String result) {
                        Toast.makeText(PendaftaranWajibPajakActivity.this, R.string.error_message, Toast.LENGTH_SHORT).show();
                        Log.e("kecamatan_log", result);
                    }
                });
    }

    private void loadKelurahan(){
        JSONBuilder body = new JSONBuilder();
        body.add("id_kecamatan", selectedKecamatan.getIdKategori());

        new ApiVolley(this, body.create(), "POST", URL.URL_MASTER_KELURAHAN,
                new ApiVolley.VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {
                        Log.d("kelurahan_log", result);
                        try{
                            JSONObject response = new JSONObject(result);
                            int status = response.getJSONObject("metadata").getInt("status");
                            String message = response.getJSONObject("metadata").getString("message");

                            if(status == 200){
                                listKelurahan.clear();
                                JSONArray list_kota = response.getJSONArray("response");
                                for(int i = 0; i < list_kota.length(); i++){
                                    JSONObject kelurahan = list_kota.getJSONObject(i);
                                    CategoryModel c = new CategoryModel();
                                    c.setIdKategori(kelurahan.getString("id"));
                                    c.setNama(kelurahan.getString("kelurahan"));
                                    listKelurahan.add(c);
                                }
                            }
                            else{
                                Toast.makeText(PendaftaranWajibPajakActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        }
                        catch (JSONException e){
                            Log.e("kelurahan_log", result);
                        }
                    }

                    @Override
                    public void onError(String result) {
                        Toast.makeText(PendaftaranWajibPajakActivity.this, R.string.error_message, Toast.LENGTH_SHORT).show();
                        Log.e("kelurahan_log", result);
                    }
                });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        final ScrollView scrollView = findViewById(R.id.scrollview);
        ScrollableMapView mapView = (ScrollableMapView) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        if (mapView!=null){
            mapView.setListener(new ScrollableMapView.OnTouchListener() {
                @Override
                public void onTouch() {
                    scrollView.requestDisallowInterceptTouchEvent(true);
                }
            });
        }

        initMerchant();
    }

    private void initLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                Log.d("location_log", "Permission needed");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION}, 999);
                return;
            }
        }

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();
        if (mGoogleMap != null) {
            mGoogleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                @Override
                public void onMarkerDragStart(Marker marker) {

                }

                @Override
                public void onMarkerDrag(Marker marker) {

                }

                @Override
                public void onMarkerDragEnd(Marker marker) {
                    LatLng ll = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
                    marker.setPosition(ll);
                    lat = marker.getPosition().latitude;
                    lng = marker.getPosition().longitude;

                    String lat_string = "Latitude : " + lat;
                    String long_string = "Longitude : " + lng;

                    text_latitude.setText(lat_string);
                    text_longitude.setText(long_string);
                    Log.i("Drag End", "location : " + ll);
                }
            });
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(100000);
        LocationServices.getFusedLocationProviderClient(this).
                requestLocationUpdates(mLocationRequest, locationCallback, Looper.myLooper());
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        settingsClient.checkLocationSettings(locationSettingsRequest).
                addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {

                    }
                }).addOnFailureListener(PendaftaranWajibPajakActivity.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //showActivateGPS();
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(PendaftaranWajibPajakActivity.this,
                                999);
                    } catch (IntentSender.SendIntentException sendEx) {
                        if (sendEx.getMessage()!=null){
                            Log.e("googlelocation_log", sendEx.getMessage());
                        }
                    }
                }
                else{
                    if (e.getMessage()!=null){
                        Log.e("googlelocation_log", e.getMessage());
                    }
                }
            }
        });
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void ResetLokasi(){
        LocationServices.getFusedLocationProviderClient(this).
                requestLocationUpdates(mLocationRequest, locationCallback, Looper.myLooper());
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
    }

    private void initMap() {
        ScrollableMapView mapFragment = (ScrollableMapView) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        if (mapFragment!=null){
            mapFragment.getMapAsync(this);
        }
    }

    public boolean googleServicesAvailable() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int isAvailable = api.isGooglePlayServicesAvailable(this);
        if (isAvailable == ConnectionResult.SUCCESS) {
            return true;
        } else if (api.isUserResolvableError(isAvailable)) {
            Dialog dialog = api.getErrorDialog(this, isAvailable, 0);
            dialog.show();
        } else {
            Toast.makeText(this, "Please Install google play services to use this application", Toast.LENGTH_LONG).show();
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == URL.CODE_PERMISSION_LOCATION){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                initLocation();
            }
            else{
                Toast.makeText(this, "Tidak memperoleh ijin mengakses lokasi", Toast.LENGTH_SHORT).show();
            }
        }
        else if(requestCode == URL.CODE_PERMISSION_WRITE_STORAGE){
            if(grantResults[0] == PackageManager.PERMISSION_DENIED){
                Toast.makeText(this, "Tidak memperoleh ijin menyimpan tanda tangan", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
