package com.example.bappeda.MenuPendaftaran;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.bappeda.MenuHome.HomeActivity;
import com.example.bappeda.R;
import com.example.bappeda.Utils.AppLoadingScreen;
import com.example.bappeda.Utils.DialogFactory;
import com.example.bappeda.Utils.Preferences;
import com.example.bappeda.Utils.URL;

import java.util.Objects;

public class FormIsianActivity extends AppCompatActivity {

    private String idMerchant = "";
    private String idKategori = "";
    private String idUser = "";
    String urlForm;
    String urlSuccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_isian);

        if(getIntent().hasExtra(URL.EXTRA_ID_MERCHANT)){
            idMerchant = getIntent().getStringExtra(URL.EXTRA_ID_MERCHANT);
        }
        if(getIntent().hasExtra(URL.EXTRA_ID_KATEGORI)){
            idKategori = getIntent().getStringExtra(URL.EXTRA_ID_KATEGORI);
        }

        idUser = Preferences.getId(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar()!=null){
            getSupportActionBar().setTitle("Isi Kelengkapan");
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        final WebView webView = findViewById(R.id.webview);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setDomStorageEnabled(true);

        // Tiga baris di bawah ini agar laman yang dimuat dapat
        // melakukan zoom.
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        // Baris di bawah untuk menambahkan scrollbar di dalam WebView-nya
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        AppLoadingScreen.getInstance().showLoading(this);
        urlForm = String.format(URL.URL_FORM + "?id=%s&kategori=%s&petugas=%s", idMerchant, idKategori, idUser);
        urlSuccess = "http://gmedia.bz/bapenda/Form/sukses_respon";
//        webView.setWebViewClient(new CustomWebViewClient());
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                AppLoadingScreen.getInstance().stopLoading();
                super.onPageFinished(view, url);
            }
        });
        webView.loadUrl(urlForm);
        Log.d("webview_log", urlForm);
    }

    @Override
    public void onBackPressed() {
        final Dialog dialog = DialogFactory.getInstance().createDialog(this, R.layout.popup_konfirmasi, 90);

        dialog.findViewById(R.id.txt_batal).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });

        dialog.findViewById(R.id.txt_ya).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent i = new Intent(FormIsianActivity.this, HomeActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        });

        dialog.show();
    }

    class CustomWebViewClient extends WebViewClient{
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.equals(urlForm)){
                AppLoadingScreen.getInstance().stopLoading();
                Log.d("webview_log", "loadUrl Success");
                return false;
            } else if (url.equals(urlSuccess)){
                AppLoadingScreen.getInstance().stopLoading();
                Intent intent = new Intent(FormIsianActivity.this, PendaftaranActivity.class);
                startActivity(intent);
                finish();
                Log.d("webview_log", "Success Intent");
            }
            return true;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            if (Objects.equals(request.getUrl().getHost(), urlForm)){
                AppLoadingScreen.getInstance().stopLoading();
                Log.d("webview_log", "loadUrl Success");
                return false;
            } else if (request.getUrl().getHost().equals(urlSuccess)){
                AppLoadingScreen.getInstance().stopLoading();
                Intent intent = new Intent(FormIsianActivity.this, PendaftaranActivity.class);
                startActivity(intent);
                finish();
                Log.d("webview_log", "Success Intent");
            }
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            AppLoadingScreen.getInstance().stopLoading();
            super.onPageFinished(view, url);
        }

        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
        }
    }

}
