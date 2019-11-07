package com.example.bappeda.Model;

public class NotificationModel {

    private String judul;
    private String tanggal;
    private String deskripsi;
    private String merchant;
    private String alamat;

    //Type
    public static final int TYPE_GENERAL = 0;
    public static final int TYPE_HUBUNGI = 1;

    private int type;

    public NotificationModel(){}

    public NotificationModel(int type){
        this.type = type;
    }

    public NotificationModel(String judul, String tanggal,String deskripsi){
        this.judul = judul;
        this.tanggal = tanggal;
        this.deskripsi = deskripsi;
    }

    public NotificationModel(String judul, String tanggal,
                             String merchant, String deskripsi, String alamat){
        this.judul = judul;
        this.tanggal = tanggal;
        this.merchant = merchant;
        this.deskripsi = deskripsi;
        this.alamat = alamat;
    }

    public void setMerchant(String merchant) {
        this.merchant = merchant;
    }
    public String getMerchant() {
        return merchant;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }
    public String getTanggal() {
        return tanggal;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }
    public String getDeskripsi() {
        return deskripsi;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }
    public String getJudul() {
        return judul;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }
    public String getAlamat() {
        return alamat;
    }

    public int getType() {
        return type;
    }
    public void setType(int type) {
        this.type = type;
    }
}
