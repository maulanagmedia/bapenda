package com.example.bappeda.Services;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.example.bappeda.Utils.ApiVolley;
import com.example.bappeda.Utils.Preferences;
import com.example.bappeda.Utils.URL;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class DemoLocationService extends Service implements LocationListener {

    private Context context;
    private static final String TAG = "LocationService";
    private final int timerTime = 1000 * 60 * 30; //30 minutes //1000 * 60 ^ 30
    private Timer timer = new Timer();
    private double latitude, longitude;
    private LocationManager locationManager;
    private Location location;
    private final int REQUEST_PERMISSION_COARSE_LOCATION=2;
    private final int REQUEST_PERMISSION_FINE_LOCATION=3;
    public boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    boolean canGetLocation = false;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1; // 1 meters
    private static final long MIN_TIME_BW_UPDATES = 2; // 2 minute
    private boolean onUpdate = false;
    public ApiVolley apiVolley;

    public DemoLocationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "my_channel_01";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Service Bapenda",
                    NotificationManager.IMPORTANCE_DEFAULT);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("")
                    .setContentText("").build();

            startForeground(1, notification);
        }

        context = this;
        onUpdate = false;
        initLocation();
    }

    private void initLocation() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        latitude = 0;
        longitude = 0;
        location = new Location("set");
        location.setLatitude(latitude);
        location.setLongitude(longitude);

        try {
            location = getLocation();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void showExplanation(String title,
                                 String message,
                                 final String permission,
                                 final int permissionRequestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        requestPermission(permission, permissionRequestCode);
                    }
                });
        builder.create().show();
    }

    private void requestPermission(String permissionName, int permissionRequestCode) {
        ActivityCompat.requestPermissions((Activity) context,
                new String[]{permissionName}, permissionRequestCode);
    }

    private Location getLocation() {
        onUpdate = true;
        try {

            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            Log.v("isGPSEnabled", "=" + isGPSEnabled);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            Log.v("isNetworkEnabled", "=" + isNetworkEnabled);

            if (isGPSEnabled == false && isNetworkEnabled == false) {
                // no network provider is enabled
                Toast.makeText(context, "Cannot identify the location.\nPlease turn on GPS or turn on your data.",
                        Toast.LENGTH_LONG).show();

            } else {
                this.canGetLocation = true;
                if (isNetworkEnabled) {
                    location = null;

                    // Granted the permission first
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
                                Manifest.permission.ACCESS_COARSE_LOCATION)) {
                            showExplanation("Permission Needed", "Rationale", Manifest.permission.ACCESS_COARSE_LOCATION, REQUEST_PERMISSION_COARSE_LOCATION);
                        } else {
                            requestPermission(Manifest.permission.ACCESS_COARSE_LOCATION, REQUEST_PERMISSION_COARSE_LOCATION);
                        }

                        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
                                Manifest.permission.ACCESS_FINE_LOCATION)) {
                            showExplanation("Permission Needed", "Rationale", Manifest.permission.ACCESS_FINE_LOCATION, REQUEST_PERMISSION_FINE_LOCATION);
                        } else {
                            requestPermission(Manifest.permission.ACCESS_FINE_LOCATION, REQUEST_PERMISSION_FINE_LOCATION);
                        }
                        onUpdate = false;
                        return null;
                    }

                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("Network", "Network");

                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            onLocationChanged(location);
                        }
                    }
                }

                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {

                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, (android.location.LocationListener) this);
                    Log.d("GPS Enabled", "GPS Enabled");

                    if (locationManager != null) {
                        Location bufferLocation = locationManager
                                .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (bufferLocation != null) {
                            //onLocationChanged(location);
                            location = bufferLocation;
                        }
                    }
                }else{
                    //Toast.makeText(context, "Turn on your GPS for better accuracy", Toast.LENGTH_SHORT).show();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        if(location != null){
            onLocationChanged(location);
        }

        onUpdate = false;
        timer = new Timer();
        timer.scheduleAtFixedRate(new mainTask(), 1000, timerTime);
        return location;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
//        Toast.makeText(this, "Stop Service", Toast.LENGTH_SHORT).show();
        if (timer != null) {
            timer.cancel();
        }

        stopForeground(true);
        super.onDestroy();
    }

    class mainTask extends TimerTask {
        public void run()
        {
            Log.d(TAG, "onLocationChanged: " +latitude+" , "+ longitude);
                if(latitude != 0 && longitude != 0 && !onUpdate){ // lat long tidak kosong
                    saveLocation();
                    // default SF
                    /*if(iv.isMoreThanCurrentDate(iv.getCurrentDate(FormatItem.formatTime), "08:30:00", FormatItem.formatTime)
                            && iv.isMoreThanCurrentDate( "20:00:00", iv.getCurrentDate(FormatItem.formatTime), FormatItem.formatTime)){
                    }*/
                }
            }
    }

    private void saveLocation(){
        onUpdate = true;

        String idUser = Preferences.getId(getBaseContext());

        JSONObject body = new JSONObject();
        try {
            body.put("id_petugas", idUser);
            body.put("lat", String.valueOf(latitude));
            body.put("long", String.valueOf(longitude));
        } catch (JSONException e) {
            e.printStackTrace();
            if (e.getMessage()!=null){
                Log.e("error.body", e.getMessage());
            }
        }

        apiVolley = new ApiVolley(this, body, "POST", URL.URL_UPDATE_LOCATION, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                Log.d("Response", result);
                try {
                    JSONObject response = new JSONObject(result);
                    int status = response.getJSONObject("metadata").getInt("status");
                    String message ="";
                    if(status== 200){
                        message = response.getJSONObject("metadata").getString("message");
                    }else{
                        message = response.getJSONObject("metadata").getString("message");
                    }
                    Log.d(TAG, "onSuccess: " + message);

                } catch (JSONException e) {
                    e.printStackTrace();
                    if (e.getMessage()!=null){
                        Log.e("catch.onSuccess", e.getMessage());
                    }
                }
                onUpdate = false;
            }

            @Override
            public void onError(String result) {
                Log.d(TAG, "onError: " + result);
                onUpdate = false;
            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }


}
