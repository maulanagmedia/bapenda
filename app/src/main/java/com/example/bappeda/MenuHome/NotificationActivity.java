package com.example.bappeda.MenuHome;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bappeda.Adapter.NotificationAdapter;
import com.example.bappeda.MenuHome.Adapter.NotificationsAdapter;
import com.example.bappeda.Model.NotificationModel;
import com.example.bappeda.R;
import com.example.bappeda.Utils.ApiVolley;
import com.example.bappeda.Utils.AppLoadingScreen;
import com.example.bappeda.Utils.Preferences;
import com.example.bappeda.Utils.URL;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {

    private Activity activity;
    private List<NotificationModel> listNotification = new ArrayList<>();

    private final String TAG = "NotificationActivity";
    private ListView lvNotification;
    private int start = 0, count = 10;
    private NotificationsAdapter adapterNotif;
    private View footerList;
    private boolean isLoading = false;

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

        activity = this;

        initUI();
    }

    private void initUI() {

        lvNotification = (ListView) findViewById(R.id.lv_notif);
        LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        footerList = li.inflate(R.layout.footer_list, null);

        adapterNotif = new NotificationsAdapter(activity, R.layout.activity_list_view_survey, listNotification);
        lvNotification.setAdapter(adapterNotif);
        isLoading = false;

        lvNotification.addFooterView(footerList);
        lvNotification.setAdapter(adapterNotif);
        lvNotification.removeFooterView(footerList);
        lvNotification.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

                int threshold = 1;
                int countMerchant = lvNotification.getCount();

                if (i == SCROLL_STATE_IDLE) {
                    if (lvNotification.getLastVisiblePosition() >= countMerchant - threshold && !isLoading) {

                        isLoading = true;
                        start += count;
                        loadNotif();
                        //Log.i(TAG, "onScroll: last ");
                    }
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });

        lvNotification.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


                NotificationModel item = (NotificationModel) adapterView.getItemAtPosition(i);
                Intent intent = new Intent(activity, DetailNotificationActivity.class);
                Gson gson = new Gson();
                intent.putExtra("notif", gson.toJson(item));
                startActivity(intent);
            }
        });
    }

    private void loadNotif (){

        isLoading = true;
        AppLoadingScreen.getInstance().showLoading(this);
        JSONObject jBody = new JSONObject();
        try {
            jBody.put("id_user", Preferences.getId(NotificationActivity.this));
            jBody.put("start", String.valueOf(start));
            jBody.put("count", String.valueOf(count));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new ApiVolley(NotificationActivity.this, jBody, "POST", URL.URL_NOTIFIKASI, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                AppLoadingScreen.getInstance().stopLoading();
                Log.d(TAG, "Response" + result);
                lvNotification.removeFooterView(footerList);
                if(start == 0) listNotification.clear();

                try {

                    isLoading = false;
                    JSONObject response = new JSONObject(result);
                    int status = response.getJSONObject("metadata").getInt("status");
                    String message = response.getJSONObject("metadata").getString("message");

                    if (status == 200){
                        JSONArray list_notif = response.getJSONArray("response");
                        //listNotification.clear();

                        for (int i = 0; i < list_notif.length(); i++){

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

                            ArrayList<String> listGambar = new ArrayList<>();
                            for(int j = 0; j < jImage.length();j++){

                                JSONObject jDataImage = jImage.getJSONObject(j);
                                listGambar.add(jDataImage.getString("image"));
                            }

                            notificationModel.setImages(listGambar);
                            if(jImage.length() > 0){

                                JSONObject jo = jImage.getJSONObject(0);
                                notificationModel.setImage(jo.getString("image"));
                            }

                            listNotification.add(notificationModel);

                        }

                        adapterNotif.notifyDataSetChanged();
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

                isLoading = false;
                AppLoadingScreen.getInstance().stopLoading();
                lvNotification.removeFooterView(footerList);
                listNotification.clear();
                adapterNotif.notifyDataSetChanged();
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
