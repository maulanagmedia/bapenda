package com.example.bappeda.MenuAdmin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.example.bappeda.MenuAdmin.HubungiPetugas.PetugasUntukHubungi;
import com.example.bappeda.MenuAdmin.Monitoring.PetugasMonitoringActivity;
import com.example.bappeda.MenuAdmin.Survey.PetugasSurveyActivity;
import com.example.bappeda.R;
import com.example.bappeda.Utils.Preferences;

import java.util.Set;

public class PenugasanPetugasActivity extends AppCompatActivity {

    private CardView survey, monitoring, hubungipetugas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_penugasan_petugas);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar()!=null){
            getSupportActionBar().setTitle("Admin");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        survey = findViewById(R.id.CardPendaftaran);
        monitoring = findViewById(R.id.CardMonitoring);
        hubungipetugas = findViewById(R.id.CardPetugas);

        survey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent m = new Intent(PenugasanPetugasActivity.this, PetugasSurveyActivity.class);
                startActivity(m);
            }
        });

        monitoring.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent m = new Intent(PenugasanPetugasActivity.this, PetugasMonitoringActivity.class);
                startActivity(m);
            }
        });

        hubungipetugas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent m = new Intent(PenugasanPetugasActivity.this, PetugasUntukHubungi.class);
                startActivity(m);
            }
        });

        Set<String> listSubMenu = Preferences.getSubMenu(PenugasanPetugasActivity.this);

        for(String menu: listSubMenu){

            if(menu.equals("tugas_untuk_survey")){

                survey.setVisibility(View.VISIBLE);
            }else if (menu.equals("tugas_untuk_monitoring")){

                monitoring.setVisibility(View.VISIBLE);
            }else if (menu.equals("hubungi_petugas")){

                hubungipetugas.setVisibility(View.VISIBLE);
            }
        }
    }
}
