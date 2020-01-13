package com.example.bappeda.MenuSurvey;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bappeda.Adapter.ImagesAdapter;
import com.example.bappeda.Model.CategoryModel;
import com.example.bappeda.Model.ImagesModel;
import com.example.bappeda.R;
import com.example.bappeda.Utils.ApiVolley;
import com.example.bappeda.Utils.AppLoadingScreen;
import com.example.bappeda.Utils.DialogFactory;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class SurveyActivity extends AppCompatActivity implements
        OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private final String TAG = "SurveyActivity";

    //UI
    private EditText nama_merchant, nama_pemilik, txt_alamat, notelp;
    private TextView text_latitude, text_longitude;
    private Button tambah;
    private View reset;

    //spinner category
    private Spinner spinnercategory;
    private ArrayList<CategoryModel> categoryModels = new ArrayList<>();

    //Dialog konfirmasi
    private Dialog confirm_dialog;
    private CardView simpan, batal;
    private TextView keterangan;
    private TextView cancel, save; //di CardView

    private GoogleMap mGoogleMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private MarkerOptions options;
    private double lat, lng;

    //marker location change
    private LocationCallback locationCallback = new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult location) {
            if (location == null) {
                Toast.makeText(SurveyActivity.this, "Can't get current location", Toast.LENGTH_LONG).show();
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
                mGoogleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                    @Override
                    public void onMarkerDragStart(Marker marker) {
//                    Log.i("System out", "onMarkerDragStart...");
                    }
                    @Override
                    public void onMarkerDrag(Marker marker) {
//                    Log.i("System out", "onMarkerDrag...");
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

                String lat_string = "Latitude : " + lat;
                String long_string = "Longitude : " + lng;

                text_latitude.setText(lat_string);
                text_longitude.setText(long_string);
            }
            //menghentikan pembaruan lokasi
            if (mGoogleApiClient != null) {
                LocationServices.getFusedLocationProviderClient(SurveyActivity.this).
                        removeLocationUpdates(locationCallback);
            }
        }
    };

    public ApiVolley apiVolley;

    //for images
    RecyclerView recyclerImages;
    public ArrayList<ImagesModel> list_images = new ArrayList<>();
    String imageString;
    private ImagesAdapter imageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);

        //Inisialisasi UI
        nama_merchant = findViewById(R.id.edtmerchant);
        nama_pemilik = findViewById(R.id.edtpemilik);
        txt_alamat = findViewById(R.id.edtalamat);
        notelp = findViewById(R.id.edtnotelp);
        text_latitude = findViewById(R.id.textLatitude);
        text_longitude = findViewById(R.id.textLongitude);
        recyclerImages = findViewById(R.id.recyclerView);
        tambah = findViewById(R.id.buttontambah);
        reset = findViewById(R.id.btnreset);
        spinnercategory = findViewById(R.id.category_spinner);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar()!=null){
            getSupportActionBar().setTitle("Survey");
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //Load Images
        recyclerImages.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        imageAdapter = new ImagesAdapter(SurveyActivity.this, list_images);
        recyclerImages.setAdapter(imageAdapter);

        tambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isInputValid()){
                    showDialogConfirm();
                }
            }
        });
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ResetLokasi();
            }
        });
        LoadSpinner();

        if (googleServicesAvailable()) {
            initMap();
        }
    }

    private boolean isInputValid(){
        return true;
    }

    private void showDialogConfirm(){
        confirm_dialog = DialogFactory.getInstance().
                createDialog(SurveyActivity.this, R.layout.popup_confirm, 100);

        simpan = confirm_dialog.findViewById(R.id.CardSimpan);
        batal = confirm_dialog.findViewById(R.id.CardCancel);
        save = confirm_dialog.findViewById(R.id.textSimpan);
        cancel = confirm_dialog.findViewById(R.id.textCancel);
        keterangan = confirm_dialog.findViewById(R.id.textJudulConfirm);
        keterangan.setText(R.string.confirm_simpan);
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
                TambahData();
                confirm_dialog.dismiss();
            }
        });

        confirm_dialog.show();
    }

    private void TambahData(){
        final String nama = nama_merchant.getText().toString();
        final String namapemilik = nama_pemilik.getText().toString();
        final String alamat = txt_alamat.getText().toString();
        final String telepon = notelp.getText().toString();
        final String id = categoryModels.get(spinnercategory.getSelectedItemPosition()).getIdKategori();
        final String idUser = Preferences.getId(this);

        if (TextUtils.isEmpty(nama)){
            nama_merchant.setError("Please enter the field");
            nama_merchant.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(alamat)){
            txt_alamat.setError("Please enter the field");
            txt_alamat.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(namapemilik)){
            nama_pemilik.setError("Please enter the field");
            nama_pemilik.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(telepon)){
            notelp.setError("Please enter the field");
            notelp.requestFocus();
            return;
        }
        final JSONObject body = new JSONObject();
        try {
            ArrayList<String> listImageString = new ArrayList<>();
            for(ImagesModel i : list_images){
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                i.getBitmap().compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] imageBytes = baos.toByteArray();
                imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                listImageString.add(imageString);
            }
            body.put("id_user", idUser);
            body.put("nama", nama);
            body.put("pemilik", namapemilik);
            body.put("alamat", alamat);
            body.put("no_telp", telepon);
            body.put("latitude", lat);
            body.put("longitude", lng);
            body.put("kategori", id);
            body.put("image", new JSONArray(listImageString));
            Log.d("pendaftaran_body_log", body.toString());
        } catch (JSONException e) {
            if (e.getMessage()!=null){
                Log.e(TAG, "exception_log" + e.getMessage());
            }
        }

        AppLoadingScreen.getInstance().showLoading(SurveyActivity.this);

        apiVolley = new ApiVolley(SurveyActivity.this, body, "POST", URL.URL_ADD_MERCHANT, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                Log.d(TAG, "Response" + result);
                try {
                    JSONObject response = new JSONObject(result);
                    int status = response.getJSONObject("metadata").getInt("status");
                    String message;
                    if(status== 200){
                        message = response.getJSONObject("metadata").getString("message");
                        confirm_dialog.dismiss();
                        Toast.makeText(SurveyActivity.this, "Data Berhasil Ditambahkan", Toast.LENGTH_LONG).show();
                        finish();
                    }else{
                        message = response.getJSONObject("metadata").getString("message");
                        confirm_dialog.dismiss();
                        Toast.makeText(SurveyActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                    Log.d(TAG, "onSuccess: " + message);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, "response" + result);
                }
                AppLoadingScreen.getInstance().stopLoading();
            }
            @Override
            public void onError(String result) {
                AppLoadingScreen.getInstance().stopLoading();
                Toast.makeText(SurveyActivity.this, R.string.error_message, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Error.Response" + result);
            }
        });
    }

    // Untuk gambar
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == URL.CODE_UPLOAD){
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

    //Kategori spinner
    private void LoadSpinner(){
        apiVolley = new ApiVolley(SurveyActivity.this, new JSONObject(), "GET", URL.URL_CATEGORY, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                Log.d(TAG, "Response" + result);
                try {
                    JSONObject object = new JSONObject(result);
                    JSONArray array = object.getJSONArray("response");
                    categoryModels = new ArrayList<>();
                    for (int i=0; i<array.length(); i++){
                        JSONObject dataObject = array.getJSONObject(i);
                        CategoryModel categoryModel = new CategoryModel();
                        categoryModel.setIdKategori(dataObject.getString("id"));
                        categoryModel.setNama(dataObject.getString("kategori"));
                        categoryModels.add(categoryModel);
                    }

                    ArrayAdapter<CategoryModel> adapter = new ArrayAdapter<>(SurveyActivity.this, android.R.layout.simple_list_item_1, categoryModels);
                    spinnercategory.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (e.getMessage()!=null){
                        Log.e(TAG, "response" + e.getMessage());
                    }
                }
            }
            @Override
            public void onError(String result) {
                Log.d(TAG, "Error.Response" + result);
            }
        });
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
                    startActivity(new Intent(SurveyActivity.this,
                            RiwayatSurveyActivity.class));
                }
            });
        }

        return true;
    }

    //Location
    private void initMap() {
        ScrollableMapView mapFragment = (ScrollableMapView) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        if (mapFragment!=null){
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        final ScrollView scrollView = findViewById(R.id.scrollview1);
        ScrollableMapView mapView = (ScrollableMapView) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        if (mapView!=null){
            mapView.setListener(new ScrollableMapView.OnTouchListener() {
                @Override
                public void onTouch() {
                    scrollView.requestDisallowInterceptTouchEvent(true);
                }
            });
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                Log.d("_log", "Permission needed");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION}, 999);
                return;
            }
        }
        initLocation();
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

    private void ResetLokasi(){
        LocationServices.getFusedLocationProviderClient(SurveyActivity.this).
                requestLocationUpdates(mLocationRequest, locationCallback, Looper.myLooper());
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
    }

    private void initLocation(){
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();
        if (mGoogleMap != null){
            mGoogleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                @Override
                public void onMarkerDragStart(Marker marker) {
                    Log.d("System out", "onMarkerDragStart...");
                }
                @Override
                public void onMarkerDrag(Marker marker) {
                    Log.i("System out", "onMarkerDrag...");
                }

                @Override
                public void onMarkerDragEnd(Marker marker) {
                    Log.d("System out", "onMarkerDragEnd...");
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 999){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                initLocation();
                Log.d("_log", String.valueOf(grantResults[0] == PackageManager.PERMISSION_GRANTED));
            }
        }
        else{
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(100000);
        LocationServices.getFusedLocationProviderClient(SurveyActivity.this).requestLocationUpdates(mLocationRequest, locationCallback, Looper.myLooper());
        SettingsClient settingsClient = LocationServices.getSettingsClient(SurveyActivity.this);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        settingsClient.checkLocationSettings(locationSettingsRequest).
                addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {

                    }
                }).addOnFailureListener(SurveyActivity.this, new OnFailureListener() {
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
                        resolvable.startResolutionForResult(SurveyActivity.this,
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
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}