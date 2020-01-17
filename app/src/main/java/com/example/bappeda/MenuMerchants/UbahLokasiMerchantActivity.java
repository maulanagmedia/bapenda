package com.example.bappeda.MenuMerchants;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
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

import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
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
import com.example.bappeda.Adapter.SurveyAdapter;
import com.example.bappeda.Model.CategoryModel;
import com.example.bappeda.Model.ImagesModel;
import com.example.bappeda.Model.MerchantModel;
import com.example.bappeda.R;
import com.example.bappeda.Utils.ApiVolley;
import com.example.bappeda.Utils.AppLoadingScreen;
import com.example.bappeda.Utils.Converter;
import com.example.bappeda.Utils.DialogFactory;
import com.example.bappeda.Utils.GoogleLocationManager;
import com.example.bappeda.Utils.ImageLoader;
import com.example.bappeda.Utils.ItemValidation;
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
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class UbahLokasiMerchantActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

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
    private Dialog dialog;

    private MerchantModel m;

    //For Location
    private double lat, lng;
    private GoogleMap mGoogleMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private MarkerOptions options;
    private GoogleLocationManager locationManager;
    private LocationCallback locationCallback = new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult location) {
            if (location == null) {
                Toast.makeText(UbahLokasiMerchantActivity.this,
                        "Gagal memperoleh lokasi", Toast.LENGTH_LONG).show();
            } else {
                /*lat = location.getLastLocation().getLatitude();
                lng = location.getLastLocation().getLongitude();*/
                lat = m.getLatitude();
                lng = m.getLongitude();
//                LatLng ll = new LatLng(location.getLastLocation().getLatitude(), location.getLastLocation().getLongitude());
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

                tvLatitude.setText(lat_string);
                tvLongitude.setText(long_string);
            }
            //menghentikan pembaruan lokasi
            if (mGoogleApiClient != null) {
                LocationServices.getFusedLocationProviderClient(UbahLokasiMerchantActivity.this).
                        removeLocationUpdates(locationCallback);
            }
        }
    };
    private String idMerchant = "";
    private String flag = "";
    private ImageView ivMerchant;
    private TextView tvNama;
    private TextView tvAlamat;
    private TextView tvLatitude, tvLongitude;
    private CardView cvSimpan;
    private String npwpdwp;
    private MapView mvPeta;
    private Button btnReset;
    private Dialog confirm_dialog;
    private RecyclerView recyclerImages;

    private ImagesAdapter imageAdapter;
    public ArrayList<ImagesModel> list_images = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubah_lokasi_merchant);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar()!=null){
            getSupportActionBar().setTitle("Wajib Pajak");
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

                m = (MerchantModel) adapterView.getItemAtPosition(position);

                dialog = DialogFactory.getInstance().createDialog(UbahLokasiMerchantActivity.this, R.layout.popup_merchant_lokasi, 90);

                //Inisialisasi UI di dialog
                ivMerchant = (ImageView) dialog.findViewById(R.id.image_merchant);
                tvNama = (TextView) dialog.findViewById(R.id.NamaMerchant);
                tvAlamat = (TextView) dialog.findViewById(R.id.AlamatMerchant);
                tvLatitude = (TextView) dialog.findViewById(R.id.textLatitude);
                tvLongitude = (TextView) dialog.findViewById(R.id.textLongitude);
                btnReset = (Button) dialog.findViewById(R.id.btnreset);
                cvSimpan = (CardView) dialog.findViewById(R.id.cv_simpan);
                recyclerImages = dialog.findViewById(R.id.recyclerView);

                list_images.clear();

                //Init foto
                ArrayList<String> listImagesUrl = m.getImages();
                if (listImagesUrl!=null){
                    for(String url : listImagesUrl){
                        Glide.with(UbahLokasiMerchantActivity.this).asBitmap().load(url).listener(new RequestListener<Bitmap>() {
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

                recyclerImages.setLayoutManager(new LinearLayoutManager(UbahLokasiMerchantActivity.this, LinearLayoutManager.HORIZONTAL, false));
                imageAdapter = new ImagesAdapter(UbahLokasiMerchantActivity.this, list_images);
                recyclerImages.setAdapter(imageAdapter);
                recyclerImages = dialog.findViewById(R.id.recyclerView);

                MapsInitializer.initialize(UbahLokasiMerchantActivity.this);
                mvPeta = (MapView) dialog.findViewById(R.id.mv_peta);

                // set Value
                idMerchant = m.getId();
                flag = m.getFlag();
                npwpdwp = m.getNpwpdwp();
                tvNama.setText(m.getNamamerchant());
                tvAlamat.setText(m.getAlamat());
                String gambar = m.getImage();
                ImageLoader.load(UbahLokasiMerchantActivity.this, gambar, ivMerchant);

                if (googleServicesAvailable()) {

                    mvPeta.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {

                            mGoogleMap = googleMap;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                                        && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                                    Log.d("_log", "Permission needed");
                                    ActivityCompat.requestPermissions(UbahLokasiMerchantActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                            Manifest.permission.ACCESS_COARSE_LOCATION}, URL.CODE_PERMISSION_LOCATION);
                                    return;
                                }
                            }
                            initLocation();
                        }
                    });
                }

                mvPeta.onCreate(dialog.onSaveInstanceState());
                mvPeta.onResume();

                btnReset.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ResetLokasi();
                    }
                });

                cvSimpan.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        showDialogConfirm();
                        dialog.dismiss();
                    }
                });

                dialog.show();
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

    private void initLocation(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                Log.d("location_log", "Permission needed");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION}, URL.CODE_PERMISSION_LOCATION);
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

        tvLatitude.setText(lat_string);
        tvLongitude.setText(long_string);

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

                    tvLatitude.setText(lat_string);
                    tvLongitude.setText(long_string);
                    Log.i(TAG, "Drag End: " +  "location : " + ll);
                }
            });

            mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {

                    String lat_string = "Latitude : " + latLng.latitude;
                    String long_string = "Longitude : " + latLng.longitude;

                    lat = latLng.latitude;
                    lng = latLng.longitude;

                    mGoogleMap.clear();
                    options = new MarkerOptions()
                            .position(latLng)
                            .draggable(true)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                    mGoogleMap.addMarker(options);

                    tvLatitude.setText(lat_string);
                    tvLongitude.setText(long_string);
                }
            });
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
        LocationServices.getFusedLocationProviderClient(UbahLokasiMerchantActivity.this).requestLocationUpdates(mLocationRequest, locationCallback, Looper.myLooper());
        SettingsClient settingsClient = LocationServices.getSettingsClient(UbahLokasiMerchantActivity.this);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        settingsClient.checkLocationSettings(locationSettingsRequest).
                addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {

                    }
                }).addOnFailureListener(UbahLokasiMerchantActivity.this, new OnFailureListener() {
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
                        resolvable.startResolutionForResult(UbahLokasiMerchantActivity.this,
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

    private void ResetLokasi(){
        LocationCallback callback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult location) {
                if (location == null) {
                    Toast.makeText(UbahLokasiMerchantActivity.this,
                            "Gagal memperoleh lokasi", Toast.LENGTH_LONG).show();
                } else {
                    lat = location.getLastLocation().getLatitude();
                    lng = location.getLastLocation().getLongitude();
                  /*  lat = m.getLatitude();
                    lng = m.getLongitude();*/
                    LatLng ll = new LatLng(location.getLastLocation().getLatitude(), location.getLastLocation().getLongitude());
//                    LatLng ll = new LatLng(m.getLatitude(), m.getLongitude());
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

                    tvLatitude.setText(lat_string);
                    tvLongitude.setText(long_string);
                }
                //menghentikan pembaruan lokasi
                if (mGoogleApiClient != null) {
                    LocationServices.getFusedLocationProviderClient(UbahLokasiMerchantActivity.this).
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
                }
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

        initData();
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
                    startActivity(new Intent(UbahLokasiMerchantActivity.this, RiwayatUpdateLokasiMerchant.class));
                }
            });
        }
        return true;
    }

    //Dialog
    private void showDialogConfirm(){

        confirm_dialog = DialogFactory.getInstance().
                createDialog(UbahLokasiMerchantActivity.this, R.layout.popup_confirm, 90);

        View simpan = confirm_dialog.findViewById(R.id.CardSimpan);
        CardView batal = confirm_dialog.findViewById(R.id.CardCancel);
        TextView save = confirm_dialog.findViewById(R.id.textSimpan);
        TextView cancel = confirm_dialog.findViewById(R.id.textCancel);
        TextView judul = confirm_dialog.findViewById(R.id.textJudulConfirm);
        judul.setText(R.string.confirm_update_loc);
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
                updateLokasi();
            }
        });

        confirm_dialog.show();
    }

    private void updateLokasi(){

        String idUser = Preferences.getId(this);
        String nama = Preferences.getUsername(this);

        ArrayList<String> listImageString = new ArrayList<>();
        for(ImagesModel i : list_images){
            listImageString.add(Converter.convertToBase64(i.getBitmap()));
        }

        JSONObject body = new JSONObject();
        try {

            body.put("id_user", idUser);
            body.put("id", idMerchant);
            body.put("latitude", iv.doubleToStringFull(lat));
            body.put("longitude", iv.doubleToStringFull(lng));
            body.put("user_update", nama);
            body.put("flag", flag);
            body.put("foto", new JSONArray(listImageString));
        } catch (JSONException e) {
            e.printStackTrace();
            if (e.getMessage()!=null){
                Log.d(TAG, "body_log: " + e.getMessage());
            }
        }

        AppLoadingScreen.getInstance().showLoading(this);


        new ApiVolley(UbahLokasiMerchantActivity.this, body, "POST", URL.simpanLokasiMerchant, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                AppLoadingScreen.getInstance().stopLoading();
                Log.d(TAG, "Response" + result);

                try {
                    JSONObject object = new JSONObject(result);
                    String message = object.getJSONObject("metadata").getString("message");
                    int status = object.getJSONObject("metadata").getInt("status");
                    if (status==200){

                        Toast.makeText(UbahLokasiMerchantActivity.this, message, Toast.LENGTH_SHORT).show();
                        if (confirm_dialog != null) confirm_dialog.dismiss();
                        if(dialog != null) dialog.dismiss();
                        start = 0;
                        initData();
                    } else {

                        Toast.makeText(UbahLokasiMerchantActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                    Log.d(TAG, "onSuccess: " + message);
                } catch (JSONException e) {

                    e.printStackTrace();
                    AppLoadingScreen.getInstance().stopLoading();
                    if (e.getMessage()!=null){
                        Log.e(TAG, "response" + e.getMessage());
                    }
                }

            }

            @Override
            public void onError(String result) {
                Log.e(TAG, "Error.Response" + result);
                Toast.makeText(UbahLokasiMerchantActivity.this, R.string.error_message, Toast.LENGTH_SHORT).show();
                AppLoadingScreen.getInstance().stopLoading();
            }
        });
    }

}
