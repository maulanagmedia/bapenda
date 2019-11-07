package com.example.bappeda.MenuPendaftaran.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.bappeda.Adapter.PendaftaranAdapter;
import com.example.bappeda.MenuMonitoring.PreviewMerchantActivity;
import com.example.bappeda.Model.MerchantModel;
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

public class DaftarFragment extends Fragment {

    private Context context;

    private ListView list_daftar;
    private ArrayList<MerchantModel> pendaftaranModels = new ArrayList<>();
    private PendaftaranAdapter adapter;
    public ApiVolley apiVolley;

    public DaftarFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        context = getContext();
        View v = inflater.inflate(R.layout.fragment_daftar, container, false);
        list_daftar = v.findViewById(R.id.list_daftar);
        list_daftar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Gson gson = new Gson();
                Intent a = new Intent(getActivity(), PreviewMerchantActivity.class);
                startActivity(a);
            }
        });
        return v;
    }

    public void loadData(String date_awal, String date_akhir){
        AppLoadingScreen.getInstance().showLoading(context);

        final String idUsername = Preferences.getId(context);

        JSONObject body = new JSONObject();
        try {
            body.put("start", date_awal);
            body.put("end", date_akhir);
            body.put("terdaftar", "Ya");
            body.put("id_user", idUsername);
            body.put("kategori", "");
            body.put("keyword", "");
        } catch (JSONException e) {
            e.printStackTrace();
            if (e.getMessage()!=null){
                Log.e("daftarfragment_log", e.getMessage());
            }
        }

        apiVolley = new ApiVolley(context, body, "POST", URL.URL_SEARCH_MERCHANT, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                Log.d("daftarfragment_log", result);
                try {
                    JSONObject object = new JSONObject(result);
                    int status = object.getJSONObject("metadata").getInt("status");
                    String message = object.getJSONObject("metadata").getString("message");
                    if (status==200){
                        JSONArray array = object.getJSONArray("response");
                        pendaftaranModels = new ArrayList<>();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject dataObject = array.getJSONObject(i);
                            MerchantModel pendaftaranModel= new MerchantModel(
                                    dataObject.getString("id"),
                                    dataObject.getString("nama"),
                                    dataObject.getString("alamat"));
                            JSONArray list_image = dataObject.getJSONArray("image");
                            ArrayList<String> listImages = new ArrayList<>();
                            for(int k = 0; k < list_image.length(); k++){
                                listImages.add(list_image.getJSONObject(k).getString("image"));
                            }
                            pendaftaranModel.setImages(listImages);
                            pendaftaranModels.add(pendaftaranModel);
                        }
                        adapter = new PendaftaranAdapter(context, R.layout.list_daftar, pendaftaranModels);
                        list_daftar.setAdapter(adapter);
                    } else {
                        AppLoadingScreen.getInstance().stopLoading();
                        pendaftaranModels.clear();
                        adapter.notifyDataSetChanged();
                        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                    }
                    Log.d("onSuccess", message);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("daftarfragment_log", result);
                }
                AppLoadingScreen.getInstance().stopLoading();
            }

            @Override
            public void onError(String result) {
                AppLoadingScreen.getInstance().stopLoading();
                Toast.makeText(getContext(), R.string.error_message, Toast.LENGTH_LONG).show();
                Log.e("daftarfragment_log", result);
            }
        });
    }

    public void searchByName(String nama){
        AppLoadingScreen.getInstance().showLoading(context);

        final String idUsername = Preferences.getId(context);

        JSONObject body = new JSONObject();
        try {
            body.put("terdaftar", "Ya");
            body.put("id_user", idUsername);
            body.put("start", "");
            body.put("end", "");
            body.put("kategori", "");
            body.put("keyword", nama);
        } catch (JSONException e) {
            e.printStackTrace();
            if (e.getMessage()!=null){
                Log.e("daftarfragment_log", e.getMessage());
            }
        }

        apiVolley = new ApiVolley(context, body, "POST", URL.URL_SEARCH_MERCHANT, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                Log.d("daftarfragment_log", result);
                try {
                    JSONObject object = new JSONObject(result);
                    int status = object.getJSONObject("metadata").getInt("status");
                    String message = object.getJSONObject("metadata").getString("message");
                    if (status==200){
                        JSONArray array = object.getJSONArray("response");
                        pendaftaranModels = new ArrayList<>();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject dataObject = array.getJSONObject(i);
                            MerchantModel pendaftaranModel= new MerchantModel(
                                    dataObject.getString("id"),
                                    dataObject.getString("nama"),
                                    dataObject.getString("alamat"));
                            JSONArray list_image = dataObject.getJSONArray("image");
                            ArrayList<String> listImages = new ArrayList<>();
                            for(int k = 0; k < list_image.length(); k++){
                                listImages.add(list_image.getJSONObject(k).getString("image"));
                            }
                            pendaftaranModel.setImages(listImages);
                            pendaftaranModels.add(pendaftaranModel);
                        }
                        adapter = new PendaftaranAdapter(context, R.layout.list_daftar, pendaftaranModels);
                        list_daftar.setAdapter(adapter);
                    } else {
                        AppLoadingScreen.getInstance().stopLoading();
                        pendaftaranModels.clear();
                        adapter.notifyDataSetChanged();
                        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                    }
                    Log.d("onSuccess", message);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("daftarfragment_log", result);
                }
                AppLoadingScreen.getInstance().stopLoading();
            }

            @Override
            public void onError(String result) {
                AppLoadingScreen.getInstance().stopLoading();
                Toast.makeText(getContext(), R.string.error_message, Toast.LENGTH_LONG).show();
                Log.e("daftarfragment_log", result);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData("", "");
    }
}
