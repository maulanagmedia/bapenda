package com.example.bappeda.MenuPendaftaran;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.example.bappeda.Utils.MultipleImageLoader;
import com.example.bappeda.Utils.Preferences;
import com.example.bappeda.Utils.ScrollableMapView;
import com.example.bappeda.Utils.URL;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.example.bappeda.Utils.Converter.convertToBase64;

public class PreviewActivity extends AppCompatActivity  implements OnMapReadyCallback {

    private RecyclerView rv_foto;

    private MerchantModel m;
    private TextView txt_nama_usaha, txt_alamat_usaha, txt_kecamatan, txt_kelurahan, txt_kota,
        txt_telp_usaha, txt_nama_pemilik, txt_nik_pemilik, txt_alamat_pemilik, txt_telp_pemilik,
        txt_klasifikasi_usaha, txt_keterangan, textLatitude, textLongitude;
    private RadioButton rbBadanUsaha, rbBadanPribadi;

    private SignatureBuilder mSignature;

    //Dialog konfirmasi
    private Dialog confirm_dialog;
    private CardView simpan, batal;
    private TextView judul;
    private TextView cancel, save; //di CardView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Preview Form");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //Init UI
        txt_nama_usaha = findViewById(R.id.txt_nama_usaha);
        txt_alamat_usaha = findViewById(R.id.txt_alamat_usaha);
        txt_kecamatan = findViewById(R.id.txt_kecamatan);
        txt_kelurahan = findViewById(R.id.txt_kelurahan);
        txt_kota = findViewById(R.id.txt_kota);
        txt_telp_usaha = findViewById(R.id.txt_telp_usaha);
        txt_nama_pemilik = findViewById(R.id.txt_nama_pemilik);
        txt_alamat_pemilik = findViewById(R.id.txt_alamat_pemilik);
        txt_telp_pemilik = findViewById(R.id.txt_telp_pemilik);
        txt_klasifikasi_usaha = findViewById(R.id.txt_klasifikasi_usaha);
        txt_keterangan = findViewById(R.id.txt_keterangan);
        txt_nik_pemilik = findViewById(R.id.txt_nik_pemilik);
        textLatitude = findViewById(R.id.textLatitude);
        textLongitude = findViewById(R.id.textLongitude);
        rbBadanUsaha = findViewById(R.id.rbBadanUsaha);
        rbBadanPribadi = findViewById(R.id.rbBadanPribadi);
        rbBadanUsaha.setEnabled(false);
        rbBadanPribadi.setEnabled(false);
        View btnSimpan = findViewById(R.id.btnSimpan);

        rv_foto = findViewById(R.id.rv_foto);
        rv_foto.setItemAnimator(new DefaultItemAnimator());
        rv_foto.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));

        if(getIntent().hasExtra(URL.EXTRA_MERCHANT)){
            btnSimpan.setVisibility(View.VISIBLE);

            Gson gson = new Gson();
            String merchant_json = getIntent().getStringExtra(URL.EXTRA_MERCHANT);
            m = gson.fromJson(merchant_json, MerchantModel.class);

            ImagesAdapter imageAdapter = new ImagesAdapter(this, PendaftaranWajibPajakActivity.list_images, false);
            rv_foto.setAdapter(imageAdapter);

            initData();
        }
        else if(getIntent().hasExtra(URL.EXTRA_ID_MERCHANT)){
            btnSimpan.setVisibility(View.GONE);
            loadData(getIntent().getStringExtra(URL.EXTRA_ID_MERCHANT));
        }

        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSignature();
            }
        });
    }

    private void initMap(){
        ScrollableMapView mapFragment = (ScrollableMapView) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        if(mapFragment != null){
            mapFragment.getMapAsync(this);
        }
    }

    private void initData(){
        txt_nama_usaha.setText(m.getNamamerchant());
        txt_alamat_usaha.setText(m.getAlamat());
        txt_kecamatan.setText(m.getKecamatan().getNama());
        txt_kelurahan.setText(m.getKelurahan().getNama());
        txt_kota.setText(m.getKota().getNama());
        txt_telp_usaha.setText(m.getNotelp());
        txt_nama_pemilik.setText(m.getNamapemilik());
        txt_alamat_pemilik.setText(m.getAlamatpemilik());
        txt_telp_pemilik.setText(m.getNotelppemilik());
        txt_klasifikasi_usaha.setText(m.getKlasifikasi_usaha().getNama());
        txt_keterangan.setText(m.getKeterangan());
        txt_nik_pemilik.setText(m.getNik());
        String latitude_string = "Latitude : " + m.getLatitude();
        textLatitude.setText(latitude_string);
        String longitude_string = "Longitude : " + m.getLongitude();
        textLongitude.setText(longitude_string);
        if(m.isBadan_usaha()){
            rbBadanUsaha.setChecked(true);
            rbBadanPribadi.setChecked(false);
        }
        else{
            rbBadanUsaha.setChecked(false);
            rbBadanPribadi.setChecked(true);
        }

        initMap();
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
                    Toast.makeText(PreviewActivity.this,
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
                createDialog(PreviewActivity.this, R.layout.popup_confirm, 90);

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

    private void loadData(String merchant_id){
        AppLoadingScreen.getInstance().showLoading(this);

        JSONBuilder body = new JSONBuilder();
        body.add("id", merchant_id);

        new ApiVolley(this, body.create(), "POST",
                URL.URL_VIEW_MERCHANT_BY_ID, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                AppLoadingScreen.getInstance().stopLoading();
                try{
                    JSONObject obj = new JSONObject(result);
                    int status = obj.getJSONObject("metadata").getInt("status");
                    String message = obj.getJSONObject("metadata").getString("message");

                    if(status == 200){
                        JSONObject merchant = obj.getJSONObject("response");

                        m = new MerchantModel();
                        m.setId(merchant.getString("id"));
                        m.setNamamerchant(merchant.getString("nama_usaha"));
                        m.setAlamat(merchant.getString("alamat"));
                        m.setNotelp(merchant.getString("telp_usaha"));
                        m.setBadan_usaha(merchant.getString("bidang_usaha").equals("Badan Usaha"));
                        m.setNamapemilik(merchant.getString("pemilik"));
                        m.setNik(merchant.getString("nik_pemilik"));
                        m.setAlamatpemilik(merchant.getString("alamat_pemilik"));
                        m.setNotelppemilik(merchant.getString("no_telp_pemilik"));

                        m.setKlasifikasi_usaha(new CategoryModel("", merchant.getString("klasifikasi_usaha")));
                        m.setKeterangan("");
                        m.setLatitude(merchant.getDouble("latitude"));
                        m.setLongitude(merchant.getDouble("longitude"));
                        m.setKota(new CategoryModel("", merchant.getString("kota")));
                        m.setKecamatan(new CategoryModel("", merchant.getString("kecamatan")));
                        m.setKelurahan(new CategoryModel("", merchant.getString("kelurahan")));

                        JSONArray listImagesJson = merchant.getJSONArray("img");
                        List<String> listImagesString = new ArrayList<>();
                        for(int i = 0; i < listImagesJson.length(); i++){
                            listImagesString.add(listImagesJson.getJSONObject(i).getString("image"));
                        }
                        loadImages(listImagesString);

                        initData();
                    }
                    else{
                        Toast.makeText(PreviewActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                }
                catch (JSONException e){
                    if (e.getMessage()!=null){
                        Log.e("preview_log", e.getMessage());
                    }
                }
            }

            @Override
            public void onError(String result) {
                Toast.makeText(PreviewActivity.this, R.string.error_message, Toast.LENGTH_SHORT).show();
                Log.e("preview_log", result);
                AppLoadingScreen.getInstance().stopLoading();
            }
        });
    }

    private void loadImages(List<String> listUrl){
        MultipleImageLoader multipleImageLoader = new MultipleImageLoader(this, listUrl,
                400, 400, new MultipleImageLoader.MultipleImageLoaderListener(){
            @Override
            public void onAllLoaded(List<Bitmap> lisLoaded) {
                List<ImagesModel> listImage = new ArrayList<>();
                for(Bitmap b : lisLoaded){
                    listImage.add(new ImagesModel(b));
                }

                ImagesAdapter imageAdapter = new ImagesAdapter(PreviewActivity.this, listImage,false);
                rv_foto.setAdapter(imageAdapter);
            }
        });
    }

    private void updateData(Bitmap signature){

        AppLoadingScreen.getInstance().showLoading(this);
        String base64 = convertToBase64(signature);
        String idUser = Preferences.getId(PreviewActivity.this);

        ArrayList<String> listImageString = new ArrayList<>();
        for(ImagesModel i : PendaftaranWajibPajakActivity.list_images){
            listImageString.add(convertToBase64(i.getBitmap()));
        }

        JSONBuilder body = new JSONBuilder();
        body.add("user_login", idUser);
        body.add("id", m.getId());
        body.add("idp", m.getIdPenugasan());
        body.add("nama", m.getNamamerchant());
        body.add("alamat", m.getAlamat());
        body.add("telp_usaha", m.getNotelp());
        body.add("bidang_usaha", m.isBadan_usaha()?"Badan Usaha":"Badan Pribadi");
        body.add("pemilik", m.getNamapemilik());
        body.add("alamat_pemilik", m.getAlamatpemilik());
        body.add("nik_pemilik", m.getNik());
        body.add("latitude", m.getLatitude());
        body.add("longitude", m.getLongitude());
        body.add("kategori", m.getKlasifikasi_usaha().getIdKategori());
        body.add("no_telp", m.getNotelppemilik());
        body.add("kelurahan", m.getKelurahan().getIdKategori());
        body.add("kecamatan", m.getKecamatan().getIdKategori());
        body.add("kota", m.getKota().getIdKategori());
        body.add("ttd", base64);
        body.add("image", new JSONArray(listImageString));
        body.add("keterangan", txt_keterangan.getText().toString());
        Log.d("body_log", "body : " + body.create());

        new ApiVolley(this, body.create(), "POST", URL.URL_EDIT_MERCHANT,
                new ApiVolley.VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {
                        Log.d("Response", result);
                        try{
                            JSONObject response = new JSONObject(result);
                            String message =  response.getJSONObject("metadata").getString("message");
                            int status = response.getJSONObject("metadata").getInt("status");

                            if(status == 200){
                                Intent i = new Intent(PreviewActivity.this, FormIsianActivity.class);
                                i.putExtra(URL.EXTRA_ID_MERCHANT, m.getId());
                                i.putExtra(URL.EXTRA_IDP, m.getIdPenugasan());
                                i.putExtra(URL.EXTRA_ID_KATEGORI, m.getKlasifikasi_usaha().getIdKategori());
                                startActivity(i);
                            }
                            else{
                                Toast.makeText(PreviewActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
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
                        Toast.makeText(PreviewActivity.this, R.string.error_message, Toast.LENGTH_SHORT).show();
                        Log.e("wajibpajak_log", result);
                        AppLoadingScreen.getInstance().stopLoading();
                    }
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        final ScrollView scrollView = findViewById(R.id.scrollview);
        ScrollableMapView mapView = (ScrollableMapView) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        mapView.setListener(new ScrollableMapView.OnTouchListener() {
            @Override
            public void onTouch() {
                scrollView.requestDisallowInterceptTouchEvent(true);
            }
        });

        LatLng ll = new LatLng(m.getLatitude(), m.getLongitude());
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, 17);
        googleMap.animateCamera(update);
        MarkerOptions options = new MarkerOptions()
                .position(ll)
                .draggable(false)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        googleMap.addMarker(options);
    }
}
