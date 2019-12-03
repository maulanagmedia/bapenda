package com.example.bappeda.Login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bappeda.MenuHome.HomeActivity;
import com.example.bappeda.R;
import com.example.bappeda.Utils.ApiVolley;
import com.example.bappeda.Utils.AppLoadingScreen;
import com.example.bappeda.Utils.CustomModel;
import com.example.bappeda.Utils.Preferences;
import com.example.bappeda.Utils.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private Button btn_login;
    private ImageButton btn_eye;
    private EditText username, password;
    public ApiVolley apiVolley;
    private Preferences preferences;
    private int isVisible = 1;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        username = findViewById(R.id.edtusername);
        password = findViewById(R.id.edtpassword);
        btn_login = findViewById(R.id.btnlogin);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserLogin();
            }
        });
        preferences = new Preferences();
        btn_eye = findViewById(R.id.show_pass_btn);

        btn_eye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowAndHidePass(password, btn_eye);
            }
        });
    }

    private void UserLogin(){
        final String user = username.getText().toString();
        final String pass = password.getText().toString();

        if (TextUtils.isEmpty(user)){
            username.setError("Please enter username");
            username.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(pass)){
            password.setError("Please enter password");
            password.requestFocus();
            return;
        }

        final JSONObject body = new JSONObject();
        try {
            body.put("username", username.getText());
            body.put("password", password.getText());
        } catch (JSONException e) {
            if (e.getMessage()!=null){
                Log.e(TAG, "body.error" + e.getMessage());
            }
        }

        AppLoadingScreen.getInstance().showLoading(MainActivity.this);

        apiVolley = new ApiVolley(MainActivity.this, body, "POST", URL.URL_LOGIN,
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
                        String id = object.getJSONObject("response").getString("id");
                        String level = object.getJSONObject("response").getString("level");
                        String nama  = object.getJSONObject("response").getString("nama");
                        String no_telp = object.getJSONObject("response").getString("no_telp");

                        //Shared Preferences
                        Preferences.setNamaPref(MainActivity.this, nama);
                        Preferences.setKontakPref(MainActivity.this, no_telp);
                        Preferences.Login(MainActivity.this, user, pass, id, level);

                        message = object.getJSONObject("metadata").getString("message");
                        Log.d(TAG, "onSuccess" + message);

                        Set<String> listMenu = new HashSet<>();
                        Set<String> listSubMenu = new HashSet<>();
                        JSONArray jMenu = object.getJSONObject("response").getJSONArray("menu");

                        for(int i = 0; i < jMenu.length(); i++){

                            JSONObject jo = jMenu.getJSONObject(i);
                            listMenu.add(jo.getString("code"));
                            JSONArray jSubMenu = jo.getJSONArray("sub");

                            for(int j = 0; j < jSubMenu.length(); j++){

                                JSONObject jA = jSubMenu.getJSONObject(j);
                                listSubMenu.add(jA.getString("code"));
                            }
                        }

                        Preferences.setMenu(MainActivity.this, listMenu);
                        Preferences.setSubMenu(MainActivity.this, listSubMenu);

                        Toast.makeText(getApplicationContext(), "Login Success", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity.this, HomeActivity.class));
                        finish();
                    } else {
                        message = object.getJSONObject("metadata").getString("message");
                        Log.d(TAG, "onSuccess.else: " + message);

                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setMessage("Wrong Username or Password").setNegativeButton("Ok", null).create().show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    AppLoadingScreen.getInstance().stopLoading();
                    if (e.getMessage()!=null){
                        Log.e(TAG,"catch.Response" + e.getMessage());
                    }
                }
                AppLoadingScreen.getInstance().stopLoading();
            }

            @Override
            public void onError(String result) {
                Log.d(TAG, "onError: " +result);
                AppLoadingScreen.getInstance().stopLoading();
                Toast.makeText(MainActivity.this, R.string.error_message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void ShowAndHidePass(EditText editText, ImageButton imageButton){
        if (isVisible==1){
            isVisible = 0;
            editText.setTransformationMethod(null);
            if (editText.getText().length()<0)
                editText.setSelection(editText.getText().length());
            imageButton.setImageResource(R.drawable.eye); //saat password terlihat
        } else {
            isVisible = 1;
            editText.setTransformationMethod(new PasswordTransformationMethod());
            if (editText.getText().length()<0)
                editText.setSelection(editText.getText().length());
            imageButton.setImageResource(R.drawable.eye_white); //saat password tidak terlihat
        }
    }
}
