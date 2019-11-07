package com.example.bappeda.Login;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bappeda.MenuHome.HomeActivity;
import com.example.bappeda.R;
import com.example.bappeda.Utils.Preferences;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Preferences.isLoggedIn(getApplicationContext())){
                    Intent a = new Intent(SplashScreen.this, HomeActivity.class);
                    startActivity(a);
                    finish();
                } else {
                    Intent b = new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(b);
                    finish();
                }
            }
        },3000L);
    }
}
