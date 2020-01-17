package com.example.bappeda.MenuPendaftaran.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
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
import com.example.bappeda.Utils.FormatItem;
import com.example.bappeda.Utils.ItemValidation;
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

    public int start = 0, count = 10;
    private View footerList;
    private boolean isLoading = false;
    public static String statusTerdaftar = "Terdaftar";
    public static String statusTidakTerdaftar = "Tidak";

    public String keyword = "", tglAwal = "", tglAkhir = "", statusMerhcant = statusTerdaftar;
    private ItemValidation iv = new ItemValidation();

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

        keyword = "";
        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        footerList = li.inflate(R.layout.footer_list, null);

        tglAwal = iv.getCurrentDate(FormatItem.formatDate);
        tglAkhir = iv.getCurrentDate(FormatItem.formatDate);

        pendaftaranModels = new ArrayList<>();
        adapter = new PendaftaranAdapter(context, R.layout.list_daftar, pendaftaranModels);

        list_daftar.addFooterView(footerList);
        list_daftar.setAdapter(adapter);
        list_daftar.removeFooterView(footerList);
        list_daftar.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

                int threshold = 1;
                int countMerchant = list_daftar.getCount();

                if (i == SCROLL_STATE_IDLE) {
                    if (list_daftar.getLastVisiblePosition() >= countMerchant - threshold && !isLoading) {

                        isLoading = true;
                        start += count;
                        loadData();
                        //Log.i(TAG, "onScroll: last ");
                    }
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });

        return v;
    }

    public void loadData(){

        isLoading = true;
        AppLoadingScreen.getInstance().showLoading(context);
        final String idUser = Preferences.getId(context);

        JSONObject body = new JSONObject();
        try {
            body.put("tgl_awal", tglAwal);
            body.put("tgl_akhir", tglAkhir);
            body.put("merchant_status", statusMerhcant);
            body.put("start", String.valueOf(start));
            body.put("count", String.valueOf(count));
            body.put("keyword", keyword);
            body.put("id_user", idUser);

        } catch (JSONException e) {
            e.printStackTrace();
            if (e.getMessage()!=null){
                Log.e("daftarfragment_log", e.getMessage());
            }
        }

        apiVolley = new ApiVolley(context, body, "POST", URL.getRiwayatPendaftaran, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                AppLoadingScreen.getInstance().stopLoading();
                isLoading = false;
                list_daftar.removeFooterView(footerList);
                if(start == 0) pendaftaranModels.clear();

                Log.d("daftarfragment_log", result);
                try {

                    JSONObject object = new JSONObject(result);
                    int status = object.getJSONObject("metadata").getInt("status");
                    String message = object.getJSONObject("metadata").getString("message");
                    if (status==200){

                        JSONArray array = object.getJSONArray("response");
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
                            pendaftaranModel.setKeterangan(dataObject.getString("keterangan"));
                            pendaftaranModel.setTanggal(dataObject.getString("tanggal_putusan"));
                            pendaftaranModels.add(pendaftaranModel);
                        }

                    } else {

                        AppLoadingScreen.getInstance().stopLoading();
                        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                    }
                    Log.d("onSuccess", message);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("daftarfragment_log", result);
                }

                AppLoadingScreen.getInstance().stopLoading();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String result) {

                isLoading = false;
                list_daftar.removeFooterView(footerList);
                AppLoadingScreen.getInstance().stopLoading();
                adapter.notifyDataSetChanged();
                Toast.makeText(getContext(), R.string.error_message, Toast.LENGTH_LONG).show();
                Log.e("daftarfragment_log", result);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!isLoading) loadData();
    }
}
