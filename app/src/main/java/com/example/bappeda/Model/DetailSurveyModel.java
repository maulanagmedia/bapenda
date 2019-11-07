package com.example.bappeda.Model;

public class DetailSurveyModel {

    public String nama;
    public String pemilik;
    public String alamat;
    public String latitude;
    public String longitude;
    public String telepon;

    public DetailSurveyModel(){
        this.nama = nama;
        this.pemilik = pemilik;
        this.alamat = alamat;
        this.latitude = latitude;
        this.longitude = longitude;
        this.telepon = telepon;
    }

    public String getNama() {
        return nama;
    }
    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getAlamat() {
        return alamat;
    }
    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getPemilik() {
        return pemilik;
    }
    public void setPemilik(String pemilik) {
        this.pemilik = pemilik;
    }

    public String getLatitude() {
        return latitude;
    }
    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }
    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getTelepon() {
        return telepon;
    }
    public void setTelepon(String telepon) {
        this.telepon = telepon;
    }
}
