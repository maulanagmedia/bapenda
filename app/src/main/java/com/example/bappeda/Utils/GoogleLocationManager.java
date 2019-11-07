package com.example.bappeda.Utils;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

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

public class GoogleLocationManager {
    private final String TAG = "location_log";
    private final int UPDATE_INTERVAL = 5000;

    public final static int ACTIVATE_LOCATION = 16;
    public final static int PERMISSION_LOCATION = 17;

    private AppCompatActivity activity;

    private LocationRequest locationRequest;
    private LocationUpdateListener listener;
    private LocationCallback locationCallback;

    public GoogleLocationManager(AppCompatActivity activity, LocationUpdateListener listener){
        this.activity = activity;
        this.listener = listener;
    }

    public void startLocationUpdates() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(UPDATE_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        SettingsClient settingsClient = LocationServices.getSettingsClient(activity);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        settingsClient.checkLocationSettings(locationSettingsRequest).
                addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        if (ActivityCompat.checkSelfPermission(activity,
                                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                                ActivityCompat.checkSelfPermission(activity,
                                        Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                            locationCallback = new LocationCallback() {
                                @Override
                                public void onLocationResult(LocationResult locationResult) {
                                    super.onLocationResult(locationResult);
                                    Log.d(TAG, "LOCATION RESULT " + locationResult);
                                    Location location = locationResult.getLastLocation();
                                    listener.onChange(location);
                                }
                            };

                            LocationServices.getFusedLocationProviderClient(activity).requestLocationUpdates(
                                    locationRequest, locationCallback, Looper.myLooper());

                        }
                        else{
                            askPermission();
                        }
                    }
                }).addOnFailureListener(activity, new OnFailureListener() {
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
                        resolvable.startResolutionForResult(activity,
                                ACTIVATE_LOCATION);
                    } catch (IntentSender.SendIntentException sendEx) {
                        Log.e(TAG, sendEx.getMessage());
                    }
                }
                else{
                    Log.e(TAG, e.getMessage());
                }
            }
        });
    }

    private void showActivateGPS(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Akses Lokasi Tidak Menyala");
        builder.setMessage("Aplikasi membutuhkan akses lokasi untuk dapat berjalan dengan benar. Nyalakan sekarang?");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                activity.startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), ACTIVATE_LOCATION);
            }
        });
       /*builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });*/
        builder.setCancelable(false);
        AlertDialog dialog_lokasi = builder.create();
        if(!activity.isFinishing()){
            dialog_lokasi.show();
        }
    }

    private void askPermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION) ||
                ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_COARSE_LOCATION)){

            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle("Izin Lokasi");
            builder.setMessage("Aplikasi membutuhkan izin lokasi untuk dapat berjalan dengan benar.");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION},
                            PERMISSION_LOCATION);
                }
            });
            builder.setCancelable(false);
            builder.create().show();
        }
        else{
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSION_LOCATION);
        }
    }

    /*private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(activity, resultCode, 999)
                        .show();
            } else

            return false;
        }
        return true;
    }*/

    public void stopLocationUpdates()
    {
        if(locationCallback != null){
            LocationServices.getFusedLocationProviderClient(activity).removeLocationUpdates(locationCallback);
        }
    }

    public interface LocationUpdateListener{
        void onChange(Location location);
    }
}
