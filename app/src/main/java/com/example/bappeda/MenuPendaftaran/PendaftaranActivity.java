package com.example.bappeda.MenuPendaftaran;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.example.bappeda.Adapter.SurveyAdapter;
import com.example.bappeda.Model.CategoryModel;
import com.example.bappeda.Model.MerchantModel;
import com.example.bappeda.R;
import com.example.bappeda.Utils.ApiVolley;
import com.example.bappeda.Utils.AppLoadingScreen;
import com.example.bappeda.Utils.ItemValidation;
import com.example.bappeda.Utils.Preferences;
import com.example.bappeda.Utils.URL;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PendaftaranActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private Toolbar toolbar;
    private final String TAG = "DetailMerchantActivity";
    private ItemValidation iv = new ItemValidation();

    private ArrayList<MerchantModel> daftarMerchantModels = new ArrayList<>();
    private SurveyAdapter adapter;
    private int start = 0, count = 10;
    private ListView listDaftarMerchant;

    public ApiVolley apiVolley;

    private RadioGroup rg_keputusan;
    private RadioButton daftar, tidakdaftar, radioButton;
    private EditText alasan;

    private Dialog dialog;
    private View footerList;
    private boolean isLoading = false;

    //For Location
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private double lat, lng;
    private LocationCallback locationCallback = new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult location) {
            if (location == null) {
                Toast.makeText(PendaftaranActivity.this, "Can't get current location", Toast.LENGTH_LONG).show();
            } else {
                lat = location.getLastLocation().getLatitude();
                lng = location.getLastLocation().getLongitude();
            }

            //menghentikan pembaruan lokasi
            if (mGoogleApiClient != null) {
                LocationServices.getFusedLocationProviderClient(PendaftaranActivity.this).
                        removeLocationUpdates(locationCallback);
            }
        }
    };

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pendaftaran);

        initUI();
    }

    private void initUI() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar()!=null){
            getSupportActionBar().setTitle("List Merchant");
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        footerList = li.inflate(R.layout.footer_list, null);
        listDaftarMerchant = findViewById(R.id.list_daftarmerchant);
        loadData();

        listDaftarMerchant.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                MerchantModel listItem = (MerchantModel) listDaftarMerchant.getItemAtPosition(i);
                Intent intent = new Intent(PendaftaranActivity.this, PutusanPendaftaranActivity.class);
                Gson gson = new Gson();
                intent.putExtra(URL.EXTRA_MERCHANT, gson.toJson(listItem));
                startActivity(intent);
            }
        });

        adapter = new SurveyAdapter(PendaftaranActivity.this, R.layout.activity_list_view_survey, daftarMerchantModels);
        listDaftarMerchant.setAdapter(adapter);

        listDaftarMerchant.addFooterView(footerList);
        listDaftarMerchant.setAdapter(adapter);
        listDaftarMerchant.removeFooterView(footerList);
        listDaftarMerchant.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

                int threshold = 1;
                int countMerchant = listDaftarMerchant.getCount();

                if (i == SCROLL_STATE_IDLE) {
                    if (listDaftarMerchant.getLastVisiblePosition() >= countMerchant - threshold && !isLoading) {

                        isLoading = true;
                        start += count;
                        loadData();
                        //Log.i(TAG, "onScroll: last ");
                    }
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });
    }

    private void loadData(){

        isLoading = true;
        AppLoadingScreen.getInstance().showLoading(this);
        final String idUsername = Preferences.getId(getBaseContext());
        final JSONObject body = new JSONObject();
        try {
            body.put("status_merchant", "belum");
            body.put("id_user", idUsername);
            body.put("start", String.valueOf(start));
            body.put("count", String.valueOf(count));
        } catch (JSONException e) {
            e.printStackTrace();
            if (e.getMessage()!=null){
                Log.e(TAG, "body" + e.getMessage());
            }
        }

        apiVolley = new ApiVolley(PendaftaranActivity.this, body, "POST",
                URL.URL_VIEW_MERCHANT_PENDAFTARAN, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                isLoading = false;
                listDaftarMerchant.removeFooterView(footerList);
                if(start == 0) daftarMerchantModels.clear();
                Log.d(TAG, "survey_log" + result);

                try {

                    JSONObject object = new JSONObject(result);
                    String message =  object.getJSONObject("metadata").getString("message");
                    int status = object.getJSONObject("metadata").getInt("status");

                    if (status == 200){

                        JSONArray array = object.getJSONArray("response");
                        for (int i=0; i<array.length(); i++){
                            JSONObject dataObject = array.getJSONObject(i);
                            MerchantModel merchantModel = new MerchantModel();
                            merchantModel.setId(dataObject.getString("id"));
                            merchantModel.setNamamerchant(dataObject.getString("nama"));
                            merchantModel.setNamapemilik(dataObject.getString("pemilik"));
                            merchantModel.setAlamat(dataObject.getString("alamat"));
                            merchantModel.setLatitude(iv.parseNullDouble(dataObject.getString("latitude")));
                            merchantModel.setLongitude(iv.parseNullDouble(dataObject.getString("longitude")));
                            merchantModel.setNotelp(dataObject.getString("no_telp"));
                            merchantModel.setFlag(dataObject.getString("flag"));
                            merchantModel.setIdPenugasan(dataObject.getString("idp"));
                            merchantModel.setStatusPendaftaran(dataObject.getString("status_merchant"));
                            merchantModel.setKodePendaftaran(dataObject.getString("kode_status"));
                            CategoryModel kategori = new CategoryModel();
                            kategori.setIdKategori(dataObject.getString("kategori"));
                            merchantModel.setKategori(kategori);

                            JSONArray list_gambar = dataObject.getJSONArray("image");
                            ArrayList<String> listGambar = new ArrayList<>();
                            for(int k = 0; k < list_gambar.length(); k++){
                                listGambar.add(list_gambar.getJSONObject(k).getString("image"));
                            }
                            merchantModel.setImages(listGambar);
                            daftarMerchantModels.add(merchantModel);
                        }

                    } else {

                        //daftarMerchantModels.clear();
                        Toast.makeText(PendaftaranActivity.this, message, Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onSuccess: " + message);
                    }

                    AppLoadingScreen.getInstance().stopLoading();
                    Log.d(TAG, "onSuccess: " + message);
                } catch (JSONException e) {

                    e.printStackTrace();
                    if (e.getMessage()!=null){
                        Log.e(TAG, "survey_log" + e.getMessage());
                    }
                }

                AppLoadingScreen.getInstance().stopLoading();
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onError(String result) {

                isLoading = false;
                listDaftarMerchant.removeFooterView(footerList);
                AppLoadingScreen.getInstance().stopLoading();
                daftarMerchantModels.clear();
                adapter.notifyDataSetChanged();
                Log.e(TAG, "survey_log" + result);
                Toast.makeText(PendaftaranActivity.this, R.string.error_message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (dialog != null && dialog.isShowing()){
            dialog.dismiss();
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
            //Toast.makeText(this, "Please Install google play services to use this application", Toast.LENGTH_LONG).show();
        }
        return false;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(100000);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION}, 999);
                return;
            }
        }
        LocationServices.getFusedLocationProviderClient(PendaftaranActivity.this).requestLocationUpdates(mLocationRequest, locationCallback, Looper.myLooper());
        SettingsClient settingsClient = LocationServices.getSettingsClient(PendaftaranActivity.this);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        settingsClient.checkLocationSettings(locationSettingsRequest).
                addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                    }
                }).addOnFailureListener(PendaftaranActivity.this, new OnFailureListener() {
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
                        resolvable.startResolutionForResult(PendaftaranActivity.this,
                                999);
                    } catch (IntentSender.SendIntentException sendEx) {
                        if (sendEx.getMessage()!=null){
                            Log.e("GoogleLocationManager", sendEx.getMessage());
                        }
                    }
                }
                else{
                    if (e.getMessage()!=null){
                        Log.e("GoogleLocationManager", e.getMessage());
                    }
                }
            }
        });
        Log.d("_log", "onConnected");
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    protected synchronized void buildGoogleApiClient(){
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.surveymenu, menu);

        MenuItem getItem = menu.findItem(R.id.action_riwayat);
        if (getItem != null) {
            View layout_parent = getItem.getActionView();
            Button btn_tambah = layout_parent.findViewById(R.id.btn_riwayat);
            btn_tambah.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(PendaftaranActivity.this,
                            RiwayatPendaftaranActivity.class));
                }
            });
        }

        return true;
    }
}
