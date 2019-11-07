package com.example.bappeda.MenuPendaftaran.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.bappeda.Adapter.BelumDaftarAdapter;
import com.example.bappeda.Model.MerchantModel;
import com.example.bappeda.R;
import com.example.bappeda.Utils.ApiVolley;
import com.example.bappeda.Utils.AppLoadingScreen;
import com.example.bappeda.Utils.Preferences;
import com.example.bappeda.Utils.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class BelumTerdaftarFragment extends Fragment {

    private Context context;

    private ListView list_tidakdaftar;
    private ArrayList<MerchantModel> belumDaftarModels = new ArrayList<>();
    private BelumDaftarAdapter adapter;
    public ApiVolley apiVolley;

    public BelumTerdaftarFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        context = getContext();
        View v = inflater.inflate(R.layout.fragment_belum_terdaftar, container, false);
        list_tidakdaftar = v.findViewById(R.id.list_tidak_daftar);
        return v;
    }

    public void loadData(String date_awal, String date_akhir){
        AppLoadingScreen.getInstance().showLoading(context);
        final String idUsername = Preferences.getId(getContext());

        JSONObject body = new JSONObject();
        try {
            body.put("terdaftar", "Tidak");
            body.put("start", date_awal);
            body.put("end", date_akhir);
            body.put("id_user", idUsername);
            body.put("kategori", "");
            body.put("keyword", "");
        } catch (JSONException e) {
            e.printStackTrace();
            if (e.getMessage()!=null){
                Log.e("tag", e.getMessage());
            }
        }

        apiVolley = new ApiVolley(context, body, "POST", URL.URL_SEARCH_MERCHANT, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                AppLoadingScreen.getInstance().stopLoading();
                Log.d("belumdaftar_log", result);
                try{
                    JSONObject object = new JSONObject(result);
                    int status = object.getJSONObject("metadata").getInt("status");
                    String message = object.getJSONObject("metadata").getString("message");
                    if (status==200){
                        JSONArray array = object.getJSONArray("response");
                        belumDaftarModels = new ArrayList<>();
                        for (int i=0; i<array.length(); i++){
                            JSONObject dataObject = array.getJSONObject(i);
                            MerchantModel belumDaftarModel= new MerchantModel(
                                    dataObject.getString("id"),
                                    dataObject.getString("nama"),
                                    dataObject.getString("alamat"));

                            JSONArray list_image = dataObject.getJSONArray("image");
                            ArrayList<String> listImages = new ArrayList<>();
                            for(int k = 0; k < list_image.length(); k++){
                                listImages.add(list_image.getJSONObject(k).getString("image"));
                            }

                            belumDaftarModel.setImages(listImages);
                            belumDaftarModels.add(belumDaftarModel);
                        }
                        adapter = new BelumDaftarAdapter(context, R.layout.list_belum_daftar, belumDaftarModels);
                        list_tidakdaftar.setAdapter(adapter);
                    }
                    else {
                        AppLoadingScreen.getInstance().stopLoading();
                        belumDaftarModels.clear();
                        adapter.notifyDataSetChanged();
                        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                    }
                    Log.d("onSuccess", message);
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (e.getMessage()!=null) {
                        Log.e("belumdaftar_log", e.getMessage());
                    }
                }
            }

            @Override
            public void onError(String result) {
                AppLoadingScreen.getInstance().stopLoading();
                Toast.makeText(context, R.string.error_message, Toast.LENGTH_SHORT).show();
                Log.d("belumdaftar_log", result);
            }
        });
    }

    public void searchByName(String nama){
        AppLoadingScreen.getInstance().showLoading(context);
        final String idUsername = Preferences.getId(getContext());

        JSONObject body = new JSONObject();
        try {
            body.put("terdaftar", "Tidak");
            body.put("start", "");
            body.put("end", "");
            body.put("id_user", idUsername);
            body.put("kategori", "");
            body.put("keyword", nama);
        } catch (JSONException e) {
            e.printStackTrace();
            if (e.getMessage()!=null){
                Log.e("tag", e.getMessage());
            }
        }

        apiVolley = new ApiVolley(context, body, "POST", URL.URL_SEARCH_MERCHANT, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                AppLoadingScreen.getInstance().stopLoading();
                Log.d("belumdaftar_log", result);
                try{
                    JSONObject object = new JSONObject(result);
                    int status = object.getJSONObject("metadata").getInt("status");
                    String message = object.getJSONObject("metadata").getString("message");
                    if (status==200){
                        JSONArray array = object.getJSONArray("response");
                        belumDaftarModels = new ArrayList<>();
                        for (int i=0; i<array.length(); i++){
                            JSONObject dataObject = array.getJSONObject(i);
                            MerchantModel belumDaftarModel= new MerchantModel(
                                    dataObject.getString("id"),
                                    dataObject.getString("nama"),
                                    dataObject.getString("alamat"));

                            JSONArray list_image = dataObject.getJSONArray("image");
                            ArrayList<String> listImages = new ArrayList<>();
                            for(int k = 0; k < list_image.length(); k++){
                                listImages.add(list_image.getJSONObject(k).getString("image"));
                            }

                            belumDaftarModel.setImages(listImages);
                            belumDaftarModels.add(belumDaftarModel);
                        }
                        adapter = new BelumDaftarAdapter(context, R.layout.list_belum_daftar, belumDaftarModels);
                        list_tidakdaftar.setAdapter(adapter);
                    }
                    else {
                        AppLoadingScreen.getInstance().stopLoading();
                        belumDaftarModels.clear();
                        adapter.notifyDataSetChanged();
                        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                    }
                    Log.d("onSuccess", message);
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (e.getMessage()!=null) {
                        Log.e("belumdaftar_log", e.getMessage());
                    }
                }
            }

            @Override
            public void onError(String result) {
                AppLoadingScreen.getInstance().stopLoading();
                Toast.makeText(context, R.string.error_message, Toast.LENGTH_SHORT).show();
                Log.d("belumdaftar_log", result);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData("", "");
    }
}
