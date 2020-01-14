package com.example.bappeda.MenuHome;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.se.omapi.Session;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.example.bappeda.MenuAdmin.PenugasanPetugasActivity;
import com.example.bappeda.MenuMerchants.PilihanMerchantsActivity;
import com.example.bappeda.MenuMonitoring.MonitoringMerchantActivity;
import com.example.bappeda.MenuPendaftaran.PendaftaranActivity;
import com.example.bappeda.MenuReklame.ReklameActivity;
import com.example.bappeda.MenuSurvey.SurveyActivity;
import com.example.bappeda.Model.AccountModel;
import com.example.bappeda.R;
import com.example.bappeda.Services.DemoLocationService;
import com.example.bappeda.Services.InitFirebaseSetting;
import com.example.bappeda.Utils.ApiVolley;
import com.example.bappeda.Utils.ImageLoader;
import com.example.bappeda.Utils.ItemValidation;
import com.example.bappeda.Utils.Preferences;
import com.example.bappeda.Utils.URL;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

public class HomeActivity extends AppCompatActivity {

    private TextView namacustomer;
    private CardView pendaftaran, survey, monitoring, menu_admin, merchants, reklame;
    private ImageView profile;

    private String name;
    private String id_level;

    private ApiVolley apiVolley;
    private final String TAG = "HomeActivity";
    private ItemValidation iv = new ItemValidation();
    private Context context;
    private LinearLayout menuContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        InitFirebaseSetting.getFirebaseSetting(HomeActivity.this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(new Intent(HomeActivity.this, DemoLocationService.class));
        } else {
            startService(new Intent(HomeActivity.this, DemoLocationService.class));
        }

        context = this;

        Toolbar toolbar = findViewById(R.id.hometoolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar()!=null){
            getSupportActionBar().setTitle("HOME");
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }

        namacustomer = findViewById(R.id.txt_nama_petugas);
        profile = findViewById(R.id.img_profile);
        pendaftaran = findViewById(R.id.CardPendaftaran);
        survey = findViewById(R.id.CardSurvey);
        monitoring = findViewById(R.id.CardMonitoring);
        menu_admin = findViewById(R.id.CardAdmin);
        merchants = findViewById(R.id.CardMerchant);
        reklame = findViewById(R.id.CardReklame);
        menuContainer = (LinearLayout) findViewById(R.id.LinearPutih);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        try {
            // this is why the minimal sdk must be JB
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                display.getRealSize(size);
            }else {
                display.getSize(size);
            }
        } catch (NoSuchMethodError err) {
            display.getSize(size);
        }

        int menuWidth = 0;
        menuWidth = (size.x / 2) - iv.dpToPx(HomeActivity.this, 32  );

        LinearLayout.LayoutParams lpp = (LinearLayout.LayoutParams) menuContainer.getLayoutParams();
        lpp.height = size.y;
        menuContainer.setLayoutParams(lpp);

        GridLayout.LayoutParams lp = (GridLayout.LayoutParams) pendaftaran.getLayoutParams();
        lp.width = menuWidth;
        lp.height = menuWidth;
        int lenght = iv.dpToPx(context, 8);
        lp.setMargins(lenght, lenght, lenght, lenght);
        pendaftaran.setLayoutParams(lp);

        GridLayout.LayoutParams lp1 = (GridLayout.LayoutParams) survey.getLayoutParams();
        lp1.width = menuWidth;
        lp1.height = menuWidth;
        lp1.setMargins(lenght, lenght, lenght, lenght);
        survey.setLayoutParams(lp1);

        GridLayout.LayoutParams lp2 = (GridLayout.LayoutParams) merchants.getLayoutParams();
        lp2.width = menuWidth;
        lp2.height = menuWidth;
        lp2.setMargins(lenght, lenght, lenght, lenght);
        merchants.setLayoutParams(lp2);

        GridLayout.LayoutParams lp3 = (GridLayout.LayoutParams) menu_admin.getLayoutParams();
        lp3.width = menuWidth;
        lp3.height = menuWidth;
        lp3.setMargins(lenght, lenght, lenght, lenght);
        menu_admin.setLayoutParams(lp3);

        GridLayout.LayoutParams lp4 = (GridLayout.LayoutParams) monitoring.getLayoutParams();
        lp4.width = menuWidth;
        lp4.height = menuWidth;
        lp4.setMargins(lenght, lenght, lenght, lenght);
        monitoring.setLayoutParams(lp4);

        GridLayout.LayoutParams lp5 = (GridLayout.LayoutParams) reklame.getLayoutParams();
        lp5.width = menuWidth;
        lp5.height = menuWidth;
        lp5.setMargins(lenght, lenght, lenght, lenght);
        reklame.setLayoutParams(lp5);

        id_level = Preferences.getLevelPref(getBaseContext());

        /*if (id_level.equals("2")){ //Petugas
           forPetugas();
        } else if (id_level.equals("3")){ //Supervisor
            forSupervisor();
        } else if (id_level.equals("4")){ //Kasubbag
           forKasubbag();
        } else if (id_level.equals("5")){ //Admin
           forAdmin();
        }
*/
        menu_admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent b = new Intent(HomeActivity.this, PenugasanPetugasActivity.class);
                startActivity(b);
            }
        });

        pendaftaran.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent m = new Intent(HomeActivity.this, PendaftaranActivity.class);
                startActivity(m);
            }
        });

        survey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent p = new Intent(HomeActivity.this, SurveyActivity.class);
                startActivity(p);
            }
        });

        monitoring.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent l = new Intent(HomeActivity.this, MonitoringMerchantActivity.class);
                startActivity(l);
            }
        });

        merchants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent a = new Intent(HomeActivity.this, PilihanMerchantsActivity.class);
                startActivity(a);
            }
        });

        reklame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent a = new Intent(HomeActivity.this, ReklameActivity.class);
                startActivity(a);
            }
        });

        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (!task.isSuccessful()) {
                    //To do//
                    return;
                }
                // Get the Instance ID token//
                String token = task.getResult().getToken();
                String msg = getString(R.string.fcm_token, token);
                Preferences.setFcmPref(HomeActivity.this, token);
                Log.d(TAG, msg);
            }
        });

        updateMenu();
    }

    @Override
    protected void onResume() {
        LoadImage();
        updateFCM();
        loadMenu();
        super.onResume();
    }

    private void updateMenu(){

        Set<String> listMenu = Preferences.getMenu(HomeActivity.this);
        //Set<String> listSubMenu = Preferences.getSubMenu(HomeActivity.this);

        menu_admin.setVisibility(View.GONE);
        survey.setVisibility(View.GONE);
        pendaftaran.setVisibility(View.GONE);
        monitoring.setVisibility(View.GONE);
        merchants.setVisibility(View.GONE);
        reklame.setVisibility(View.GONE);

        for(String menu: listMenu){

            if(menu.equals("admin")){

                menu_admin.setVisibility(View.VISIBLE);
            }else if (menu.equals("survey")){

                survey.setVisibility(View.VISIBLE);
            }else if (menu.equals("pendaftaran")){

                pendaftaran.setVisibility(View.VISIBLE);
            }else if (menu.equals("monitoring")){

                monitoring.setVisibility(View.VISIBLE);
            }else if (menu.equals("merchants")){

                merchants.setVisibility(View.VISIBLE);
            }else if (menu.equals("reklame")){

                reklame.setVisibility(View.VISIBLE);
            }
        }
    }

    private void loadMenu() {

        JSONObject jBody = new JSONObject();
        try {
            jBody.put("id_user", Preferences.getId(HomeActivity.this));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        apiVolley = new ApiVolley(HomeActivity.this, jBody, "POST", URL.getMenu,
                new ApiVolley.VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {
                        Log.d(TAG, "OnSuccess" +result);
                        try {
                            JSONObject object = new JSONObject(result);
                            int status = object.getJSONObject("metadata").getInt("status") ;
                            String message = "";
                            if (status == 200){

                                //getting information
                                message = object.getJSONObject("metadata").getString("message");
                                Log.d(TAG, "onSuccess" + message);

                                Set<String> listMenu = new HashSet<>();
                                Set<String> listSubMenu = new HashSet<>();
                                JSONArray jMenu = object.getJSONArray("response");

                                for(int i = 0; i < jMenu.length(); i++){

                                    JSONObject jo = jMenu.getJSONObject(i);
                                    listMenu.add(jo.getString("code"));
                                    JSONArray jSubMenu = jo.getJSONArray("sub");

                                    for(int j = 0; j < jSubMenu.length(); j++){

                                        JSONObject jA = jSubMenu.getJSONObject(j);
                                        listSubMenu.add(jA.getString("code"));
                                    }
                                }

                                Preferences.setMenu(HomeActivity.this, listMenu);
                                Preferences.setSubMenu(HomeActivity.this, listSubMenu);

                                updateMenu();
                            } else {
                                message = object.getJSONObject("metadata").getString("message");
                                Log.d(TAG, "onSuccess.else: " + message);
                                Toast.makeText(HomeActivity.this, message, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(HomeActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onError(String result) {
                        Log.d(TAG, "onError: " +result);
                        Toast.makeText(HomeActivity.this, R.string.error_message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void LoadImage(){
        final String idUser = Preferences.getId(getBaseContext());
        JSONObject body = new JSONObject();
        try {
            body.put("id_user", idUser);
        } catch (JSONException e) {
            e.printStackTrace();
            if (e.getMessage()!=null){
                Log.e("body.error", e.getMessage());
            }
        }

        apiVolley = new ApiVolley(HomeActivity.this, body, "POST", URL.URL_VIEW_USER_BY_ID, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                Log.d(TAG,"Response" + result);
                try {
                    JSONObject object = new JSONObject(result);
                    int status = object.getJSONObject("metadata").getInt("status");
                    String message;
                    if(status== 200){
                        message = object.getJSONObject("metadata").getString("message");
                        JSONObject dataObject = object.getJSONObject("response");
                        AccountModel accountModel = new AccountModel();
                        name = accountModel.setNamaLengkap(dataObject.getString("nama"));
                        namacustomer.setText(name);
                        ImageLoader.loadPersonImage(HomeActivity.this, dataObject.getString("foto_profil"), profile);
                    }else{
                        message = object.getJSONObject("metadata").getString("message");
                    }
                    Log.d(TAG, "onSuccess: " + message);
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (e.getMessage()!=null){
                        Log.e(TAG, "catch.response" + e.getMessage());
                    }
                }
            }
            @Override
            public void onError(String result) {
                Toast.makeText(HomeActivity.this, R.string.error_message, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Error.Response" + result);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.notifmenu, menu);
        inflater.inflate(R.menu.settingmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.btnsetting){
            startActivity(new Intent(HomeActivity.this, AccountActivity.class));
        } else if (item.getItemId() == R.id.btnnotif){
            startActivity(new Intent(HomeActivity.this, NotificationActivity.class));
        }
        return true;
    }

    public void updateFCM(){
        final String idUser = Preferences.getId(getBaseContext());
        final String getToken = Preferences.getFcmPref(HomeActivity.this);
        Log.d("_log", "getToken: " + getToken);

        JSONObject body = new JSONObject();
        try {
            body.put("id_user", idUser);
            body.put("fcm_id", getToken);
            Log.d("_log", body.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            if (e.getMessage() != null){
               Log.e(TAG, "body.error" + e.getMessage());
            }
        }

        apiVolley = new ApiVolley(HomeActivity.this, body, "POST", URL.URL_UPDATE_FCM, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject response = new JSONObject(result);
                    Log.d("fcm_log", response.getJSONObject("metadata").getString("message"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (e.getMessage()!=null){
                        Log.e("fcm_log", e.getMessage());
                    }
                }
            }

            @Override
            public void onError(String result) {
                Toast.makeText(HomeActivity.this, R.string.error_message, Toast.LENGTH_SHORT).show();
                Log.d("fcm_log", result);
            }
        });
    }

    private void forPetugas(){
        survey.setVisibility(View.VISIBLE);
        survey.setVisibility(View.VISIBLE);
        monitoring.setVisibility(View.VISIBLE);
        menu_admin.setVisibility(View.GONE);
        merchants.setVisibility(View.VISIBLE);
    }

    private void forSupervisor(){
        survey.setVisibility(View.VISIBLE);
        survey.setVisibility(View.VISIBLE);
        monitoring.setVisibility(View.VISIBLE);
        menu_admin.setVisibility(View.GONE);
        merchants.setVisibility(View.VISIBLE);
    }

    private void forKasubbag(){
        survey.setVisibility(View.VISIBLE);
        survey.setVisibility(View.VISIBLE);
        monitoring.setVisibility(View.VISIBLE);
        menu_admin.setVisibility(View.VISIBLE);
        merchants.setVisibility(View.VISIBLE);
    }

    private void forAdmin(){
        survey.setVisibility(View.VISIBLE);
        survey.setVisibility(View.VISIBLE);
        monitoring.setVisibility(View.VISIBLE);
        menu_admin.setVisibility(View.VISIBLE);
        merchants.setVisibility(View.VISIBLE);
    }
}
