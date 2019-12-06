package com.example.bappeda.MenuMerchants;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bappeda.Adapter.CategoryAdapter;
import com.example.bappeda.Adapter.SurveyAdapter;
import com.example.bappeda.Model.CategoryModel;
import com.example.bappeda.Model.MerchantModel;
import com.example.bappeda.R;
import com.example.bappeda.Utils.ApiVolley;
import com.example.bappeda.Utils.AppLoadingScreen;
import com.example.bappeda.Utils.ItemValidation;
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
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MerchantSekitarActivity extends AppCompatActivity implements
        OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private final String TAG = "MerchantSekitarActivity";

    private SlidingUpPanelLayout mLayout;

    //For BOTTOM in Layout
    //Location
    public String selectedKategori = "";
    private Button reset; //reset location
    private GoogleMap mGoogleMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private MarkerOptions options;
    private ItemValidation iv = new ItemValidation();
    private LocationCallback locationCallback = new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult location) {
            if (location == null) {
                Toast.makeText(MerchantSekitarActivity.this, "Can't get current location", Toast.LENGTH_LONG).show();
            } else {
                lat = location.getLastLocation().getLatitude();
                lng = location.getLastLocation().getLongitude();
                Log.d("_log", "latitude: " + lat + " longitude: " + lng);

                start = 0;
                loadListMerchant();
                LatLng ll = new LatLng(location.getLastLocation().getLatitude(), location.getLastLocation().getLongitude());
                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, 17);
                mGoogleMap.animateCamera(update);
                mGoogleMap.clear();
                options = new MarkerOptions()
                        .position(ll)
                        .draggable(false)
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

                       /* text_latitude.setText(lat_string);
                        text_longitude.setText(long_string);*/
                        Log.i("Drag End", "location : " + ll);
                    }
                });

                String lat_string = "Latitude : " + lat;
                String long_string = "Longitude : " + lng;

                /*text_latitude.setText(lat_string);
                text_longitude.setText(long_string);*/
            }
            //menghentikan pembaruan lokasi
            if (mGoogleApiClient != null) {
                LocationServices.getFusedLocationProviderClient(MerchantSekitarActivity.this).
                        removeLocationUpdates(locationCallback);
            }
        }
    };

    private double lat, lng;

    //For TOP in Layout
    private ListView listMerchantSekitar;
    private SurveyAdapter adapter;
    private ArrayList<MerchantModel> merchantModels = new ArrayList<>();

    private List<CategoryModel> listKategori = new ArrayList<>();
    private RecyclerView rvMerchantKategori;
    private CategoryAdapter categoryAdapter;

    public int start = 0, count = 10;
    private View footerList;
    private boolean isLoading = false;
    private String keyword = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant_sekitar);

        mLayout = findViewById(R.id.sliding_layout);
        //Top
        listMerchantSekitar = findViewById(R.id.list_merchantSekitar);
        rvMerchantKategori = findViewById(R.id.rv_merchantkategori);
        //Bottom
        reset = findViewById(R.id.btnreset);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ResetLokasi();
            }
        });

        mLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                Log.i(TAG, "onPanelSlide, offset " + slideOffset);
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                Log.i(TAG, "onPanelStateChanged " + newState);
            }
        });
        mLayout.setFadeOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });

        if (googleServicesAvailable()) {
            initMap();
        }

        //Kategori
        rvMerchantKategori.setLayoutManager(new LinearLayoutManager(MerchantSekitarActivity.this, RecyclerView.HORIZONTAL, false));
        rvMerchantKategori.setItemAnimator(new DefaultItemAnimator());
        categoryAdapter = new CategoryAdapter(this, listKategori);
        rvMerchantKategori.setAdapter(categoryAdapter);

        listKategori.clear();
        loadKategoriMerchant();

        keyword = "";
        LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        footerList = li.inflate(R.layout.footer_list, null);

        merchantModels = new ArrayList<>();
        adapter = new SurveyAdapter(MerchantSekitarActivity.this, R.layout.activity_list_view_survey, merchantModels);
        listMerchantSekitar.setAdapter(adapter);

        listMerchantSekitar.addFooterView(footerList);
        listMerchantSekitar.setAdapter(adapter);
        listMerchantSekitar.removeFooterView(footerList);
        listMerchantSekitar.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

                int threshold = 1;
                int countMerchant = listMerchantSekitar.getCount();

                if (i == SCROLL_STATE_IDLE) {
                    if (listMerchantSekitar.getLastVisiblePosition() >= countMerchant - threshold && !isLoading) {

                        isLoading = true;
                        start += count;
                        loadListMerchant();
                        //Log.i(TAG, "onScroll: last ");
                    }
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });

}

    //Location
    private void initMap() {
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment);
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
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(100000);
        LocationServices.getFusedLocationProviderClient(MerchantSekitarActivity.this).requestLocationUpdates(mLocationRequest, locationCallback, Looper.myLooper());
        SettingsClient settingsClient = LocationServices.getSettingsClient(MerchantSekitarActivity.this);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        settingsClient.checkLocationSettings(locationSettingsRequest).
                addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {

                    }
                }).addOnFailureListener(MerchantSekitarActivity.this, new OnFailureListener() {
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
                        resolvable.startResolutionForResult(MerchantSekitarActivity.this,
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
                    LatLng ll = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
                    marker.setPosition(ll);
                    lat = marker.getPosition().latitude;
                    lng = marker.getPosition().longitude;
                    CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, 17);
                    mGoogleMap.animateCamera(update);
                    mGoogleMap.clear();
                    options = new MarkerOptions()
                            .position(ll)
                            .draggable(false)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                    mGoogleMap.addMarker(options);
                    Log.i("Drag End", "location : " + ll);
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

    private void ResetLokasi(){
        LocationServices.getFusedLocationProviderClient(MerchantSekitarActivity.this).
                requestLocationUpdates(mLocationRequest, locationCallback, Looper.myLooper());
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
    }

    public void loadListMerchant(){

        isLoading = true;
        AppLoadingScreen.getInstance().showLoading(this);

        final JSONObject body = new JSONObject();
        try {
            body.put("longitude", lng);
            body.put("latitude", lat);
            body.put("start", String.valueOf(start));
            body.put("count", String.valueOf(count));
            body.put("id_kategori", selectedKategori);
        } catch (JSONException e) {
            e.printStackTrace();
            if (e.getMessage()!=null){
                Log.e(TAG, "body" + e.getMessage());
            }
        }

        new ApiVolley(this, body, "POST", URL.URL_MERCHANT_TERDEKAT,
                new ApiVolley.VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {
                        Log.d(TAG, "Response" + result);

                        AppLoadingScreen.getInstance().stopLoading();
                        isLoading = false;
                        listMerchantSekitar.removeFooterView(footerList);
                        if(start == 0) merchantModels.clear();
                        try {
                            JSONObject object = new JSONObject(result);
                            String message =  object.getJSONObject("metadata").getString("message");
                            int status = object.getJSONObject("metadata").getInt("status");
                            if (status==200){
                                JSONArray array = object.getJSONArray("response");
                                for (int i=0; i<array.length(); i++){
                                    JSONObject dataObject = array.getJSONObject(i);
                                    MerchantModel merchantModel = new MerchantModel();
                                    merchantModel.setId(dataObject.getString("id"));
                                    merchantModel.setNamamerchant(dataObject.getString("nama"));
                                    merchantModel.setAlamat(dataObject.getString("alamat"));
                                    merchantModel.setJarak(dataObject.getString("jarak"));
                                    merchantModel.setLatitudeString(dataObject.getString("latitude"));
                                    merchantModel.setLongitudeString(dataObject.getString("longitude"));

                                    CategoryModel kategori = new CategoryModel();
                                    kategori.setNama(dataObject.getString("kategori"));
                                    merchantModel.setKategori(kategori);

                                    ArrayList<String> gambar = new ArrayList<>();
                                    final JSONArray arrray = dataObject.getJSONArray("image");
                                    for (int p=0; p<arrray.length(); p++){
                                        gambar.add(arrray.getJSONObject(p).getString("image"));
                                    }
                                    merchantModel.setImages(gambar);
                                    merchantModels.add(merchantModel);
                                }

                                Log.d(TAG, "onSuccess" + message);
//                                Toast.makeText(MerchantSekitarActivity.this, message, Toast.LENGTH_SHORT).show();
                            } else {
                                Log.d(TAG, "onSuccess" + message + "," + status);
                                Toast.makeText(MerchantSekitarActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {

                            e.printStackTrace();
                            if (e.getMessage()!=null){
                                Log.d(TAG, e.getMessage());
                            }
                        }

                        setMarker();
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(String result) {

                        isLoading = false;
                        listMerchantSekitar.removeFooterView(footerList);
                        adapter.notifyDataSetChanged();
                        AppLoadingScreen.getInstance().stopLoading();
                        Toast.makeText(MerchantSekitarActivity.this, R.string.error_message, Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onError: " + result);
                    }
                });
    }

    private void setMarker() {


        if(mGoogleMap != null){

            mGoogleMap.clear();
            for(MerchantModel item : merchantModels){

                LatLng ll = new LatLng(iv.parseNullDouble(item.getLatitudeString()), iv.parseNullDouble(item.getLongitudeString()));
                options = new MarkerOptions()
                        .position(ll)
                        .draggable(false)
                        .title(item.getNamamerchant())
                        .snippet(item.getAlamat())
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                mGoogleMap.addMarker(options);

            }

            LatLng ll = new LatLng(lat, lng);
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, 17);
            mGoogleMap.animateCamera(update);

            options = new MarkerOptions()
                    .position(ll)
                    .draggable(false)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            mGoogleMap.addMarker(options);
        }
    }

    private void loadKategoriMerchant(){

        new ApiVolley(MerchantSekitarActivity.this, new JSONObject(), "GET", URL.URL_CATEGORY, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject response = new JSONObject(result);
                    String message = response.getJSONObject("metadata").getString("message");
                    int status = response.getJSONObject("metadata").getInt("status");

                    if(status == 200){
                        JSONArray array = response.getJSONArray("response");
                        for (int i=0; i<array.length(); i++){
                            JSONObject category = array.getJSONObject(i);
                            CategoryModel categoryModel = new CategoryModel();
                            categoryModel.setIdKategori(category.getString("id"));
                            categoryModel.setNama(category.getString("kategori"));
                            listKategori.add(categoryModel);
                        }
                        categoryAdapter.notifyDataSetChanged();
                    }
                    else{
                        Toast.makeText(MerchantSekitarActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    AppLoadingScreen.getInstance().stopLoading();
                    if (e.getMessage()!=null){
                        Log.e(TAG, "notif_log" + e.getMessage());
                    }
                }
            }

            @Override
            public void onError(String result) {
                AppLoadingScreen.getInstance().stopLoading();
                Toast.makeText(MerchantSekitarActivity.this, R.string.error_message, Toast.LENGTH_SHORT).show();
                Log.e(TAG, "notif_log" + result);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (mLayout != null && (mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED)){
            mLayout.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
        } else if (mLayout != null && (mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED)){
            mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }
}
