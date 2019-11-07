package com.example.bappeda.MenuPendaftaran;

import android.app.DatePickerDialog;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.bappeda.Adapter.PageAdapter;
import com.example.bappeda.MenuPendaftaran.Fragment.BelumTerdaftarFragment;
import com.example.bappeda.MenuPendaftaran.Fragment.DaftarFragment;
import com.example.bappeda.R;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class RiwayatPendaftaranActivity extends AppCompatActivity {

    TabLayout tabLayout;
    TabItem tabdaftar, tabbelum;
    ViewPager viewPager;
    PageAdapter pageAdapter;

    TextView tanggal_awal, tanggal_akhir;
    ImageButton buttonproses;
    Calendar myCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_riwayat);

        tanggal_awal = findViewById(R.id.txt_tanggalawal);
        tanggal_akhir = findViewById(R.id.txt_tanggalakhir);
        buttonproses = findViewById(R.id.btnnproses);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar()!=null){
            getSupportActionBar().setTitle("Riwayat Hasil Pendaftaran");
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        tabLayout = findViewById(R.id.tabLayout);
        tabdaftar = findViewById(R.id.tabDaftar);
        tabbelum = findViewById(R.id.tabBelumDaftar);
        viewPager = findViewById(R.id.viewPager);
        tanggalAwalFormat();
        tanggalAkhirFormat();
        DateCalendar();
        buttonproses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = pageAdapter.getActiveFragment();
                if (fragment!=null){
                    if (fragment instanceof DaftarFragment){
                        ((DaftarFragment)fragment).loadData(tanggal_awal.getText().toString(), tanggal_akhir.getText().toString());
                    } else if (fragment instanceof BelumTerdaftarFragment){
                        ((BelumTerdaftarFragment) fragment).loadData(tanggal_awal.getText().toString(), tanggal_akhir.getText().toString());
                    }
                }
            }
        });

        pageAdapter = new PageAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(pageAdapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.searchbar, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Fragment fragment = pageAdapter.getActiveFragment();
                if (fragment!=null){
                    if (fragment instanceof DaftarFragment){
                        ((DaftarFragment) fragment).searchByName(s);
                        if (TextUtils.isEmpty(s)){
                            ((DaftarFragment)fragment).loadData(tanggal_awal.getText().toString(), tanggal_akhir.getText().toString());
                        }
                        Log.d("Riwayat_log", String.valueOf(s));
                    } else if (fragment instanceof BelumTerdaftarFragment){
                        ((BelumTerdaftarFragment) fragment).searchByName(s);
                        if (TextUtils.isEmpty(s)){
                            ((BelumTerdaftarFragment) fragment).loadData(tanggal_awal.getText().toString(), tanggal_akhir.getText().toString());
                        }
                        Log.d("Riwayat_log", String.valueOf(s));
                    }
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (!searchView.isIconified() && TextUtils.isEmpty(s)) {
                    Fragment fragment = pageAdapter.getActiveFragment();
                    if (fragment!=null){
                        if (fragment instanceof DaftarFragment){
                            ((DaftarFragment) fragment).searchByName(s);
                            if (TextUtils.isEmpty(s)){
                                ((DaftarFragment)fragment).loadData(tanggal_awal.getText().toString(), tanggal_akhir.getText().toString());
                            }
                            Log.d("Riwayat_log", String.valueOf(s));
                        } else if (fragment instanceof BelumTerdaftarFragment){
                            ((BelumTerdaftarFragment) fragment).searchByName(s);
                            if (TextUtils.isEmpty(s)){
                                ((BelumTerdaftarFragment) fragment).loadData(tanggal_awal.getText().toString(), tanggal_akhir.getText().toString());
                            }
                            Log.d("Riwayat_log", String.valueOf(s));
                        }
                    }
                }
                return false;
            }
        });
        return true;
    }

    private void DateCalendar(){
        myCalendar = Calendar.getInstance();

        final DatePickerDialog.OnDateSetListener date_awal = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, day);
                updateDateAwal();
            }
        };

        tanggal_awal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(RiwayatPendaftaranActivity.this, date_awal, myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        final DatePickerDialog.OnDateSetListener date_akhir = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, day);
                updateDateAkhir();
            }
        };

        tanggal_akhir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(RiwayatPendaftaranActivity.this, date_akhir, myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void updateDateAwal() {
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        tanggal_awal.setText(sdf.format(myCalendar.getTime()));
    }

    private void updateDateAkhir() {
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        tanggal_akhir.setText(sdf.format(myCalendar.getTime()));
    }

    private void tanggalAwalFormat(){
        String myFormat = "yyyy-MM-01";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        String dateName = sdf.format(new Date());
        tanggal_awal.setText(dateName);
    }

    private void tanggalAkhirFormat(){
        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        String dateName = sdf.format(new Date());
        tanggal_akhir.setText(dateName);
    }
}
