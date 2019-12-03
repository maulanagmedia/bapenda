package com.example.bappeda.MenuHome;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bappeda.Adapter.NotificationAdapter;
import com.example.bappeda.Model.NotificationModel;
import com.example.bappeda.R;
import com.example.bappeda.Utils.ApiVolley;
import com.example.bappeda.Utils.AppLoadingScreen;
import com.example.bappeda.Utils.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {

    private NotificationAdapter adapter;
    private List<NotificationModel> listNotification = new ArrayList<>();

    private final String TAG = "NotificationActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Notification");
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_name);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        RecyclerView rv_notification = findViewById(R.id.rv_notification);

        rv_notification.setLayoutManager(new LinearLayoutManager(this));
        rv_notification.setItemAnimator(new DefaultItemAnimator());
        adapter = new NotificationAdapter(this, listNotification);
        rv_notification.setAdapter(adapter);
    }

    private void loadNotif (){
        AppLoadingScreen.getInstance().showLoading(this);
        new ApiVolley(NotificationActivity.this, new JSONObject(), "GET", URL.URL_NOTIFIKASI, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                AppLoadingScreen.getInstance().stopLoading();
                Log.d(TAG, "Response" + result);
                try {
                    JSONObject response = new JSONObject(result);
                    int status = response.getJSONObject("metadata").getInt("status");
                    String message = response.getJSONObject("metadata").getString("message");

                    if (status == 200){
                        JSONArray list_notif = response.getJSONArray("response");
                        listNotification.clear();

                        for (int i=0; i<list_notif.length(); i++){

                            JSONObject notif  = list_notif.getJSONObject(i);
                            NotificationModel notificationModel = new NotificationModel();
                            notificationModel.setMerchant(notif.getString("nama"));
                            notificationModel.setAlamat(notif.getString("alamat"));
                            notificationModel.setDeskripsi(notif.getString("ket"));
                            notificationModel.setJudul(notif.getString("title"));
                            notificationModel.setTanggal(notif.getString("tgl"));
                            notificationModel.setType(NotificationModel.TYPE_GENERAL);

                            if (notif.getString("nama").equals("") && notif.getString("alamat").equals("")){
                                notificationModel.setType(NotificationModel.TYPE_HUBUNGI);
                            }

                            JSONArray jImage = notif.getJSONArray("image");
                            if(jImage.length() > 0){

                                JSONObject jo = jImage.getJSONObject(0);
                                notificationModel.setImage(jo.getString("image"));
                            }

                            listNotification.add(notificationModel);

                        }

                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(NotificationActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (e.getMessage()!=null){
                        Log.e(TAG, "notif_log" + e.getMessage());
                    }
                }
            }

            @Override
            public void onError(String result) {

                AppLoadingScreen.getInstance().stopLoading();
                Toast.makeText(NotificationActivity.this, R.string.error_message, Toast.LENGTH_SHORT).show();
                Log.e(TAG, "notif_log" + result);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadNotif();
    }
}
