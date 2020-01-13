package com.example.bappeda.MenuHome;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.example.bappeda.Login.MainActivity;
import com.example.bappeda.Model.AccountModel;
import com.example.bappeda.R;
import com.example.bappeda.Utils.ApiVolley;
import com.example.bappeda.Utils.AppLoadingScreen;
import com.example.bappeda.Utils.DialogFactory;
import com.example.bappeda.Utils.ImageLoader;
import com.example.bappeda.Utils.Preferences;
import com.example.bappeda.Utils.TopCropCircularImageView;
import com.example.bappeda.Utils.URL;
import com.fxn.pix.Pix;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class AccountActivity extends AppCompatActivity {

    //UI di AccountActivity
    private TextView namaLengkap, nomorKontak, password;
    private TextView logout, edit;
    private ImageButton editnama, editkontak, editpassword;

    //UI di masing - masing dialog
    private Dialog dialog;
    private EditText fieldnama, fieldkontak;
    private EditText oldpass, newpass, repeatnewpass;
    private Button batalnama, batalkontak, batalpass, batal;
    private Button simpannama, simpankontak, simpanpass, simpan;
    private String name, contact;
    private ImageButton eye_old, eye_new, eye_retype;
    private int isVisible  = 1;

    //Untuk ganti foto
    private ImageView gantifoto;
    private static final int UPLOAD_CODE = 999;
    private String imageString;
    private Bitmap bitmap;
    private TopCropCircularImageView profile; //Gambar dengan bentuk lingkaran

    private ApiVolley apiVolley;
    private String status = ""; //response from web service
    private String message=""; //response from web service
    private final String TAG = "AccountActivity";

    //Dialog konfirmasi
    private Dialog confirm_dialog;
    private CardView yes, no;
    private TextView judul;
    private TextView cancel, save; //di CardView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar()!=null){
            getSupportActionBar().setTitle("Setelan Akun");
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        namaLengkap = findViewById(R.id.edtNama);
        nomorKontak = findViewById(R.id.edtKontak);
        password = findViewById(R.id.edtPass);
        logout = findViewById(R.id.txtLogout);
        edit = findViewById(R.id.txtEditSeluruh);
        editnama = findViewById(R.id.btnEditNama);
        editkontak = findViewById(R.id.btnEdtKontak);
        editpassword = findViewById(R.id.btnEdtPass);
        gantifoto = findViewById(R.id.btnGantiProfile);
        profile = findViewById(R.id.fotoprof);

        EditTextFalse();
//        LoadData();

        //Ubah nama
        editnama.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = DialogFactory.getInstance().createDialogTrans(AccountActivity.this, R.layout.popup_nama, 100);
                dialog.show();

                fieldnama = dialog.findViewById(R.id.editTextNama);
                batalnama = dialog.findViewById(R.id.btnBatalNama);
                simpannama = dialog.findViewById(R.id.btnSimpanNama);

                String NamaLengkap = Preferences.getNama(AccountActivity.this);
                fieldnama.setText(NamaLengkap);

                batalnama.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                simpannama.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showDialogConfirmNama();
                        dialog.dismiss();
                    }
                });
            }
        });

        //Ubah no hp
        editkontak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = DialogFactory.getInstance().createDialogTrans(AccountActivity.this, R.layout.popup_kontak, 100);
                dialog.show();

                fieldkontak = dialog.findViewById(R.id.editTextKontak);
                batalkontak = dialog.findViewById(R.id.btnBatalKontak);
                simpankontak = dialog.findViewById(R.id.btnSimpanKontak);

                String kontak = Preferences.getKontakPref(AccountActivity.this);
                fieldkontak.setText(kontak);

                batalkontak.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                simpankontak.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        showDialogConfirmKontak();
                    }
                });
            }
        });

        //Ubah password
        editpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = DialogFactory.getInstance().createDialogTrans(AccountActivity.this, R.layout.popup_password, 100);
                dialog.show();

                oldpass = dialog.findViewById(R.id.editOldPass);
                newpass = dialog.findViewById(R.id.editNewPass);
                repeatnewpass = dialog.findViewById(R.id.editRepeatPass);
                batalpass = dialog.findViewById(R.id.btnBatalPass);
                simpanpass = dialog.findViewById(R.id.btnSimpanPass);
                eye_old = dialog.findViewById(R.id.show_old_pass_btn);
                eye_new = dialog.findViewById(R.id.show_new_pass_btn);
                eye_retype = dialog.findViewById(R.id.show_retype_pass_btn);

                oldpass.setText(Preferences.getPassword(AccountActivity.this));

                eye_old.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ShowAndHidePass(oldpass, eye_old);
                    }
                });

                eye_new.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ShowAndHidePass(newpass, eye_new);
                    }
                });

                eye_retype.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ShowAndHidePass(repeatnewpass, eye_retype);
                    }
                });

                repeatnewpass.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                        //charSequence = parameter is the text before any change is applied.
                        //start = the position of the beginning of the changed part in the text.
                        //count = the length of the changed part in the s sequence since the start position.
                        //after = the length of the new sequence which will replace the part of the charSequence from start to start+count.
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                        //charSequence = parameter is the text after changes have been applied.
                        //start = is the position of the beginning of the changed part in the text.
                        //count = is the after parameter in the beforeTextChanged method.
                        //before = is the length of the changed part in the s sequence since the start position.
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        //You can change the text in the TextView from this method.
                        // Warning: When you change the text in the TextView, the TextWatcher will be triggered again,
                        // starting an infinite loop. You should then add like a boolean _ignore property which prevent
                        // the infinite loop.
                        String password = newpass.getText().toString();
                        String confirm = repeatnewpass.getText().toString();
                        if (editable.length() > 0 && password.length() > 0){
                            if (!confirm.equals(password)){
                                repeatnewpass.setError("Password doesn't match");
                            }
                        }
                    }
                });

                batalpass.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                simpanpass.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        showDialogConfirmPassword();
                    }
                });
            }
        });

        gantifoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Pix.start(AccountActivity.this, UPLOAD_CODE);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog dialog = new AlertDialog.Builder(AccountActivity.this)
                        .setTitle("Konfirmasi")
                        .setMessage("Apakah anda yakin ingin keluar?")
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                Preferences.Logout(AccountActivity.this, "", "", "", "");
                                Intent intent  = new Intent(AccountActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();


            }
        });
    }

    private void showDialogConfirmNama(){
        confirm_dialog = DialogFactory.getInstance().
                createDialog(AccountActivity.this, R.layout.popup_confirm, 90);

        yes = confirm_dialog.findViewById(R.id.CardSimpan);
        no = confirm_dialog.findViewById(R.id.CardCancel);
        save = confirm_dialog.findViewById(R.id.textSimpan);
        cancel = confirm_dialog.findViewById(R.id.textCancel);
        judul = confirm_dialog.findViewById(R.id.textJudulConfirm);
        judul.setText(R.string.confirm_simpan);
        save.setText(R.string.confirm_yes);
        cancel.setText(R.string.confirm_no);

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirm_dialog.dismiss();
            }
        });

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TambahNama();
                confirm_dialog.dismiss();
            }
        });

        confirm_dialog.show();
    }

    private void showDialogConfirmKontak(){
        confirm_dialog = DialogFactory.getInstance().
                createDialog(AccountActivity.this, R.layout.popup_confirm, 90);

        yes = confirm_dialog.findViewById(R.id.CardSimpan);
        no = confirm_dialog.findViewById(R.id.CardCancel);
        save = confirm_dialog.findViewById(R.id.textSimpan);
        cancel = confirm_dialog.findViewById(R.id.textCancel);
        judul = confirm_dialog.findViewById(R.id.textJudulConfirm);
        judul.setText(R.string.confirm_simpan);
        save.setText(R.string.confirm_yes);
        cancel.setText(R.string.confirm_no);

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirm_dialog.dismiss();
            }
        });

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TambahKontak();
                confirm_dialog.dismiss();
            }
        });

        confirm_dialog.show();
    }

    private void showDialogConfirmPassword(){
        confirm_dialog = DialogFactory.getInstance().
                createDialog(AccountActivity.this, R.layout.popup_confirm, 90);

        yes = confirm_dialog.findViewById(R.id.CardSimpan);
        no = confirm_dialog.findViewById(R.id.CardCancel);
        save = confirm_dialog.findViewById(R.id.textSimpan);
        cancel = confirm_dialog.findViewById(R.id.textCancel);
        judul = confirm_dialog.findViewById(R.id.textJudulConfirm);
        judul.setText(R.string.confirm_simpan);
        save.setText(R.string.confirm_yes);
        cancel.setText(R.string.confirm_no);

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirm_dialog.dismiss();
            }
        });

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TambahPassword();
                confirm_dialog.dismiss();
            }
        });

        confirm_dialog.show();
    }

    private void TambahNama(){
        AppLoadingScreen.getInstance().showLoading(AccountActivity.this);

        final String idUser = Preferences.getId(getBaseContext());
        final String nama_lengkap = fieldnama.getText().toString();
        final String nomor_telp = nomorKontak.getText().toString();

        JSONObject body = new JSONObject();
        try {
            body.put("id_user", idUser);
            body.put("nama", nama_lengkap);
            body.put("kontak", nomor_telp);
        } catch (JSONException e) {
            e.printStackTrace();
            if (e.getMessage()!=null){
                Log.e(TAG,"body.error" + e.getMessage());
            }
        }

        apiVolley = new ApiVolley(AccountActivity.this, body, "POST", URL.URL_PROSES_USER_EDIT,
                new ApiVolley.VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {
                        Log.d(TAG,"Response" + result);
                        try {
                            JSONObject object = new JSONObject(result);
                            status = object.getJSONObject("metadata").getString("status");

                            if (status.equals("200")){
                                message = object.getJSONObject("metadata").getString("message");
                                Preferences.setNamaPref(AccountActivity.this, nama_lengkap);
                                Toast.makeText(AccountActivity.this, "Nama Berhasil Diubah", Toast.LENGTH_LONG).show();
                                LoadData();
                                dialog.dismiss();
                            } else {
                                message = object.getJSONObject("metadata").getString("message");
                                Toast.makeText(AccountActivity.this, message, Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "Failed: " + message);
                            }
                            Log.d(TAG, "onSuccess: " + message);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            if (e.getMessage()!=null){
                                Log.e(TAG,"response" + e.getMessage());
                            }
                        }
                        AppLoadingScreen.getInstance().stopLoading();
                    }

                    @Override
                    public void onError(String result) {
                        AppLoadingScreen.getInstance().stopLoading();
                        Toast.makeText(AccountActivity.this, R.string.error_message, Toast.LENGTH_LONG).show();
                        Log.d(TAG,"Error.Response" + result);
                    }
                });
    }

    private void TambahKontak(){
        AppLoadingScreen.getInstance().showLoading(AccountActivity.this);

        final String idUser = Preferences.getId(getBaseContext());
        final String nomor_kontak = fieldkontak.getText().toString();
        final String nama_lengkap = namaLengkap.getText().toString();

        JSONObject body = new JSONObject();
        try {
            body.put("id_user", idUser);
            body.put("nama", nama_lengkap);
            body.put("kontak", nomor_kontak);
        } catch (JSONException e) {
            e.printStackTrace();
            if (e.getMessage()!=null){
                Log.e(TAG,"body.error" + e.getMessage());
            }
        }

        apiVolley = new ApiVolley(AccountActivity.this, body, "POST", URL.URL_PROSES_USER_EDIT,
                new ApiVolley.VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {
                        Log.d(TAG,"Response" + result);
                        try {
                            JSONObject object = new JSONObject(result);
                            status = object.getJSONObject("metadata").getString("status");

                            if (status.equals("200")){
                                message = object.getJSONObject("metadata").getString("message");
                                Preferences.setKontakPref(AccountActivity.this, nomor_kontak);
                                Toast.makeText(AccountActivity.this, "Nomor Telepon Berhasil Diubah", Toast.LENGTH_LONG).show();
                                LoadData();
                                dialog.dismiss();
                            } else {
                                message = object.getJSONObject("metadata").getString("message");
                                Toast.makeText(AccountActivity.this, message, Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "Failed: " + message);
                            }
                            Log.d(TAG, "onSuccess: " + message);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(TAG,"response" + result);
                        }
                        AppLoadingScreen.getInstance().stopLoading();
                    }
                    @Override
                    public void onError(String result) {
                        AppLoadingScreen.getInstance().stopLoading();
                        Toast.makeText(AccountActivity.this, R.string.error_message, Toast.LENGTH_LONG).show();
                        Log.d(TAG,"Error.Response" + result);
                    }
                });
    }

    private void TambahPassword(){
        AppLoadingScreen.getInstance().showLoading(AccountActivity.this);

        final String idUser = Preferences.getId(getBaseContext());

        if (oldpass.getText().toString().isEmpty()){
            oldpass.setError("Please enter your password");
            oldpass.requestFocus();
        }
        if (newpass.getText().toString().isEmpty()){
            newpass.setError("Please enter your new password");
            newpass.requestFocus();
        }
        if (repeatnewpass.getText().toString().isEmpty()){
            repeatnewpass.setError("Please confirm your new password");
            repeatnewpass.requestFocus();
        }

        JSONObject body = new JSONObject();
        try {
            body.put("id", idUser);
            body.put("password_lama", oldpass.getText().toString());
            body.put("password_baru", newpass.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
            if (e.getMessage()!=null){
                Log.e(TAG, "body.error" + e.getMessage());
            }
        }

        apiVolley = new ApiVolley(AccountActivity.this, body, "POST", URL.URL_GANTI_PASSWORD,
                new ApiVolley.VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {
                        Log.d("Response", result);
                        try {
                            JSONObject object = new JSONObject(result);
                            status = object.getJSONObject("metadata").getString("status");
                            if (status.equals("200")){
                                message = object.getJSONObject("metadata").getString("message");
                                Preferences.setPassPref(AccountActivity.this, newpass.getText().toString());
                                oldpass.setText(Preferences.getPassword(AccountActivity.this));
                                password.setText(Preferences.getPassword(AccountActivity.this));
                                Toast.makeText(AccountActivity.this, "Password Berhasil Diubah", Toast.LENGTH_LONG).show();
                                LoadData();
                                dialog.dismiss();
                            } else {
                                message = object.getJSONObject("metadata").getString("message");
                                Toast.makeText(AccountActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                            Log.d(TAG, "onSuccess: " + message);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(TAG, "response" + result);
                        }
                        AppLoadingScreen.getInstance().stopLoading();
                    }
                    @Override
                    public void onError(String result) {
                        AppLoadingScreen.getInstance().stopLoading();
                        Toast.makeText(AccountActivity.this, R.string.error_message, Toast.LENGTH_LONG).show();
                        Log.d(TAG, "Error.Response" + result);
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == UPLOAD_CODE){
            ArrayList<String> returnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
            if(returnValue!=null){
                for(String s : returnValue){
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),
                                Uri.fromFile(new File(s)));
                    } catch (IOException e) {
                        e.printStackTrace();
                        if (e.getMessage()!=null){
                            Log.e(TAG, e.getMessage());
                        }
                    }
                }
            }
            TambahGambar(bitmap);
        }
    }

    private void LoadData(){

        final String idUser = Preferences.getId(getBaseContext());

        JSONObject body = new JSONObject();
        try {
            body.put("id_user", idUser);
        } catch (JSONException e) {
            e.printStackTrace();
            if (e.getMessage()!=null){
                Log.e(TAG, "body.error" + e.getMessage());
            }
        }

        apiVolley = new ApiVolley(AccountActivity.this, body, "POST", URL.URL_VIEW_USER_BY_ID, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                Log.d("Response", result);
                try {
                    JSONObject object = new JSONObject(result);
                    status = object.getJSONObject("metadata").getString("status");

                    if (status.equals("200")){
                        message = object.getJSONObject("metadata").getString("message");
                        JSONObject dataObject = object.getJSONObject("response");
                        AccountModel accountModel = new AccountModel();
                        name = accountModel.setNamaLengkap(dataObject.getString("nama"));
                        namaLengkap.setText(name);
                        contact = accountModel.setKontak(dataObject.getString("no_telp"));
                        nomorKontak.setText(contact);
                        Glide.with(AccountActivity.this)
                                .load(dataObject.getString("foto_profil"))
                                .placeholder(R.drawable.ic_personblue)
                                .into(profile);
                    } else {
                        message = object.getJSONObject("metadata").getString("message");
                        Toast.makeText(AccountActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                    Log.d(TAG, "onSuccess" + message);

                } catch (JSONException e) {
                    e.printStackTrace();
                    if (e.getMessage()!=null){
                        Log.e("response", e.getMessage());
                    }
                }
            }
            @Override
            public void onError(String result) {
                Toast.makeText(AccountActivity.this, R.string.error_message, Toast.LENGTH_SHORT).show();
                Log.d("Error.Response", result);
            }
        });
    }

    private void TambahGambar(final Bitmap bitmap){

        AppLoadingScreen.getInstance().showLoading(this);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        final String idUser = Preferences.getId(getBaseContext());

        final JSONObject body = new JSONObject();
        try {
            body.put("id_user", idUser);
            body.put("foto", imageString);
        } catch (JSONException e) {
            e.printStackTrace();
            if (e.getMessage()!=null){
                Log.e(TAG, "body.error" + e.getMessage());
            }
        }

        apiVolley = new ApiVolley(AccountActivity.this, body, "POST", URL.URL_UPDATE_GAMBAR,
                new ApiVolley.VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {
                        Log.d(TAG, "Response" + result);
                        AppLoadingScreen.getInstance().stopLoading();
                        try {
                            JSONObject response = new JSONObject(result);
                            ImageLoader.loadProfileImage(AccountActivity.this, bitmap, profile);
                            int status = response.getJSONObject("metadata").getInt("status");
                            String message;
                            if(status== 200){
                                message = response.getJSONObject("metadata").getString("message");
                                Toast.makeText(AccountActivity.this, "Foto Berhasil Diubah", Toast.LENGTH_LONG).show();
                            }else{
                                message = response.getJSONObject("metadata").getString("message");
                                Toast.makeText(AccountActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                            Log.d(TAG, "onSuccess: " + message);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(TAG, "response" + result);
                        }
                    }

                    @Override
                    public void onError(String result) {
                        Log.d(TAG,"Error.Response" + result);
                        AppLoadingScreen.getInstance().stopLoading();
                        Toast.makeText(AccountActivity.this, R.string.error_message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void EditTextFalse(){
        namaLengkap.setEnabled(false);
        nomorKontak.setEnabled(false);
        password.setEnabled(false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (dialog != null && dialog.isShowing()){
            dialog.dismiss();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LoadData();
        password.setText(Preferences.getPassword(this));
    }

    private void ShowAndHidePass(EditText editText, ImageButton imageButton){
        if (isVisible==1){
            isVisible = 0;
            editText.setTransformationMethod(null);
            if (editText.getText().length() < 0)
                editText.setSelection(editText.getText().length());
            imageButton.setImageResource(R.drawable.eye_grey); //saat password terlihat
        } else {
            isVisible = 1;
            editText.setTransformationMethod(new PasswordTransformationMethod());
            if (editText.getText().length() < 0)
                editText.setSelection(editText.getText().length());
            imageButton.setImageResource(R.drawable.eye); //saat password tidak terlihat
        }
    }

}
