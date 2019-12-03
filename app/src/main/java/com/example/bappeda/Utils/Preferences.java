package com.example.bappeda.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Preferences {
    private static String LOGIN_PREF = "login_status";
    private static String USER_PREF = "username";
    private static String PASS_PREF = "password";
    private static String ID_PREF = "id";
    private static String NAMA_PREF = "namaLengkap";
    private static String KONTAK_PREF = "no_telp";
    private static String LEVEL_PREF = "level";
    public static String MERCHANT_PREF = "merchant";
    private static String FCM_PREF = "fcmPref";
    private static String TAG_MENU = "menu";
    private static String TAG_SUBMENU = "submenu";

    private static final SharedPreferences getPreferences(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static boolean isLoggedIn(Context context){
        return getPreferences(context).getBoolean(LOGIN_PREF, false);
    }

    public static String getUsername(Context context){
        return getPreferences(context).getString(USER_PREF, "");
    }

    public static String getNama(Context context){
        return getPreferences(context).getString(NAMA_PREF, "");
    }

    public static void setNamaPref(Context context, String nama){
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putString(NAMA_PREF, nama);
        editor.apply();
    }

    public static void setMenu(Context context, Set<String> listMenu){

        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putStringSet(TAG_MENU, listMenu);
        editor.apply();
    }

    public static void setSubMenu(Context context, Set<String> listSubMenu){

        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putStringSet(TAG_SUBMENU, listSubMenu);
        editor.apply();
    }

    public static Set<String> getMenu(Context context){

        return getPreferences(context).getStringSet(TAG_MENU, new HashSet<String>());
    }

    public static Set<String> getSubMenu(Context context){

        return getPreferences(context).getStringSet(TAG_SUBMENU, new HashSet<String>());
    }

    public static String getId(Context context){
        return getPreferences(context).getString(ID_PREF, "");
    }

    public static void setPassPref(Context context, String pass) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putString(PASS_PREF, pass);
        editor.apply();
    }

    public static String getPassword(Context context){
        return getPreferences(context).getString(PASS_PREF, "");
    }

    public static String getLevelPref(Context context){
        return getPreferences(context).getString(LEVEL_PREF, "");
    }

    public static String getMerchantPref(Context context){
        return getPreferences(context).getString(MERCHANT_PREF, "");
    }

    public static String getFcmPref (Context context){
        return getPreferences(context).getString(FCM_PREF, "");
    }

    public static void setFcmPref(Context context, String fcmPref) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putString(FCM_PREF, fcmPref);
        editor.apply();
    }

    public static String getKontakPref (Context context){
        return getPreferences(context).getString(KONTAK_PREF, "");
    }

    public static void setKontakPref (Context context, String no_telp){
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putString(KONTAK_PREF, no_telp);
        editor.apply();
    }

    public static void Login(Context context, String username, String password, String id, String level){
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putBoolean(LOGIN_PREF, true);
        editor.putString(USER_PREF, username);
        editor.putString(PASS_PREF, password);
        editor.putString(ID_PREF, id);
        editor.putString(LEVEL_PREF, level);
        editor.apply();
    }

    public static void Logout(Context context, String username, String password, String id, String level){
        SharedPreferences.Editor editor= getPreferences(context).edit();
        editor.putBoolean(LOGIN_PREF, false);
        editor.putString(USER_PREF, username);
        editor.putString(PASS_PREF, password);
        editor.putString(ID_PREF, id);
        editor.putString(LEVEL_PREF, level);
        editor.apply();
    }
}
