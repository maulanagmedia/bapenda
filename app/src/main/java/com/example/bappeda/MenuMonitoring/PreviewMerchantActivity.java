package com.example.bappeda.MenuMonitoring;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.bappeda.Adapter.ImagesAdapter;
import com.example.bappeda.Model.ImagesModel;
import com.example.bappeda.Model.MerchantModel;
import com.example.bappeda.R;
import com.example.bappeda.Signature.SignatureBuilder;
import com.example.bappeda.Utils.ApiVolley;
import com.example.bappeda.Utils.AppLoadingScreen;
import com.example.bappeda.Utils.DialogFactory;
import com.example.bappeda.Utils.ImageLoader;
import com.example.bappeda.Utils.JSONBuilder;
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

import static com.example.bappeda.Utils.Converter.convertToBase64;

public class PreviewMerchantActivity extends AppCompatActivity implements
        OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private final String TAG = "PreviewMerchantActivity";

    private ImageView imageHeader;
    private TextView namMerchant, alamatMerchant;
    private EditText keterangan;
    private TextView text_latitude, text_longitude;
    private Button tambah;
    private View reset;

    private RecyclerView recyclerImages;
    private MerchantModel m;

    private ImagesAdapter imageAdapter;
    public ArrayList<ImagesModel> list_images = new ArrayList<>();
    private String imageString;
    private Bitmap bitmap;
    private Dialog signature_dialog;

    public ApiVolley apiVolley;
    private String idMonitoring;

    private String tempDir;
    private SignatureBuilder mSignature;

    private double lat, lng;
    private GoogleMap mGoogleMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private MarkerOptions options;
    private LocationCallback locationCallback = new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult location) {
            if (location == null) {
                Toast.makeText(PreviewMerchantActivity.this,
                        "Gagal memperoleh lokasi", Toast.LENGTH_LONG).show();
            } else {
                /*lat = location.getLastLocation().getLatitude();
                lng = location.getLastLocation().getLongitude();*/
                lat = m.getLatitude();
                lng = m.getLongitude();
                LatLng ll = new LatLng(m.getLatitude(), m.getLongitude());
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
                LocationServices.getFusedLocationProviderClient(PreviewMerchantActivity.this).
                        removeLocationUpdates(locationCallback);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_merchant);

        //Inisialisasi UI
        imageHeader = findViewById(R.id.image_header);
        namMerchant = findViewById(R.id.txtnamaMerchant);
        alamatMerchant = findViewById(R.id.txtalamatMerchant);
        keterangan = findViewById(R.id.edt_deskripsi);
        text_latitude = findViewById(R.id.textLatitude);
        text_longitude = findViewById(R.id.textLongitude);
        recyclerImages = findViewById(R.id.recyclerView);
        tambah = findViewById(R.id.buttontambah);
        reset = findViewById(R.id.btnreset);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar()!=null){
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        if (googleServicesAvailable()) {
            initMap();
        }

        if (getIntent().hasExtra("id_merchant")){
            Gson gson = new Gson();
            m = gson.fromJson(getIntent().getStringExtra("id_merchant"), MerchantModel.class);
            InitData();
        }

        //Load Images
        recyclerImages.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        imageAdapter = new ImagesAdapter(this, list_images);
        recyclerImages.setAdapter(imageAdapter);

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ResetLokasi();
            }
        });
        tambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isInputValid()){
                    showSignature();
                }
            }
        });
    }

    private boolean isInputValid(){
        return true;
    }

    private void showSignature(){
        signature_dialog = DialogFactory.getInstance().
                createDialog(this, R.layout.popup_signature, 90);

        final FrameLayout layout_signature = signature_dialog.findViewById(R.id.layout_signature);
        mSignature = new SignatureBuilder(this, null, layout_signature);
        mSignature.setBackgroundColor(Color.WHITE);
        layout_signature.addView(mSignature, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        signature_dialog.findViewById(R.id.btn_simpan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mSignature.getBitmap(layout_signature) == null){
                    Toast.makeText(PreviewMerchantActivity.this,
                            "Tanda tangan belum terisi", Toast.LENGTH_SHORT).show();
                }
                else{
                    updateData(mSignature.getBitmap(layout_signature));
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

    private void updateData(final Bitmap signature){
        AppLoadingScreen.getInstance().showLoading(this);
        String base64 = convertToBase64(signature);

        ArrayList<String> listImageString = new ArrayList<>();
        for(ImagesModel i : list_images){
            listImageString.add(convertToBase64(i.getBitmap()));
        }

        JSONBuilder body = new JSONBuilder();
        body.add("id_monitor", idMonitoring );
        body.add("deskripsi",keterangan.getText().toString());
        body.add("lat", lat);
        body.add("long", lng);
        body.add("ttd", base64);
        body.add("foto", new JSONArray(listImageString));
        Log.d(TAG, "body : " + body.create());

        new ApiVolley(this, body.create(), "POST", URL.URL_DETAIL_MONITORING,
                new ApiVolley.VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {
                        Log.d("Response", result);
                        try{
                            JSONObject response = new JSONObject(result);
                            String message =  response.getJSONObject("metadata").getString("message");
                            int status = response.getJSONObject("metadata").getInt("status");
                            if (status == 200){
                                Toast.makeText(PreviewMerchantActivity.this, message, Toast.LENGTH_SHORT).show();
                                signature_dialog.dismiss();
                                startActivity(new Intent(PreviewMerchantActivity.this, MonitoringMerchantActivity.class));
                                finish();
                            } else {
                                Toast.makeText(PreviewMerchantActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                            Log.d(TAG, "onSuccess: " + message);
                        }
                        catch (JSONException e){
                            if (e.getMessage()!=null){
                                Log.e("errorResponse", e.getMessage());
                            }
                            signature_dialog.dismiss();
                        }
                        AppLoadingScreen.getInstance().stopLoading();
                    }

                    @Override
                    public void onError(String result) {
                        Toast.makeText(PreviewMerchantActivity.this, R.string.error_message, Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Error.Response" + result);
                        AppLoadingScreen.getInstance().stopLoading();
                    }
                });
    }

    private void InitData(){
        idMonitoring = m.getId();
        namMerchant.setText(m.getNamamerchant());
        alamatMerchant.setText(m.getAlamat());
        keterangan.setText(m.getDeskripsi());
        text_latitude.setText(String.valueOf(m.getLatitude()));
        Log.d("_log", "Latitude: " + m.getLatitude());
        text_longitude.setText(String.valueOf(m.getLongitude()));
        Log.d("_log", "Longitude: " + m.getLongitude());

        //Init foto
        ArrayList<String> listImagesUrl = m.getImages();
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
        String gambar = m.getImage();
        ImageLoader.load(PreviewMerchantActivity.this, gambar, imageHeader);

    }

    private void initMap() {
        ScrollableMapView mapFragment = (ScrollableMapView) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        if (mapFragment!=null){
            mapFragment.getMapAsync(this);
        }
    }

    private void initLocation(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                Log.d("location_log", "Permission needed");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION}, 999);
                return;
            }
        }

        //getLocation
        LatLng ll = new LatLng(m.getLatitude(), m.getLongitude());
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, 17);
        mGoogleMap.animateCamera(update);
        mGoogleMap.clear();
        options = new MarkerOptions()
                .position(ll)
                .draggable(true)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        mGoogleMap.addMarker(options);

        lat = m.getLatitude();
        lng = m.getLongitude();

        String lat_string = "Latitude : " + lat;
        String long_string = "Longitude : " + lng;

        text_latitude.setText(lat_string);
        text_longitude.setText(long_string);

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
        LocationServices.getFusedLocationProviderClient(PreviewMerchantActivity.this).requestLocationUpdates(mLocationRequest, locationCallback, Looper.myLooper());
        SettingsClient settingsClient = LocationServices.getSettingsClient(PreviewMerchantActivity.this);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        settingsClient.checkLocationSettings(locationSettingsRequest).
                addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {

                    }
                }).addOnFailureListener(PreviewMerchantActivity.this, new OnFailureListener() {
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
                        resolvable.startResolutionForResult(PreviewMerchantActivity.this,
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        final NestedScrollView scrollView = findViewById(R.id.nestedScroll);
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
            LocationCallback callback = new LocationCallback(){
                @Override
                public void onLocationResult(LocationResult location) {
                    if (location == null) {
                        Toast.makeText(PreviewMerchantActivity.this,
                                "Gagal memperoleh lokasi", Toast.LENGTH_LONG).show();
                    } else {
                        lat = location.getLastLocation().getLatitude();
                        lng = location.getLastLocation().getLongitude();
                  /*  lat = m.getLatitude();
                    lng = m.getLongitude();*/
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
                        LocationServices.getFusedLocationProviderClient(PreviewMerchantActivity.this).
                                removeLocationUpdates(locationCallback);
                    }
                }
            };
            LocationServices.getFusedLocationProviderClient(this).
                    requestLocationUpdates(mLocationRequest, callback, Looper.myLooper());
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
            builder.addLocationRequest(mLocationRequest);
        }

        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            if(requestCode == URL.CODE_PERMISSION_LOCATION){
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if(mGoogleMap != null){
                        initLocation();
                    }initLocation();
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

    @Override
    protected void onResume() {
        super.onResume();
        if(mGoogleMap != null){
            initLocation();
        }
    }

        /*@Override
        protected void onDestroy() {
            list_images = null;
            super.onDestroy();
        }*/

}

