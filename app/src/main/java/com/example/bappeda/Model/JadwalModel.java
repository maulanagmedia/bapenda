package com.example.bappeda.Model;

public class JadwalModel {

    public String namamerchant;
    public String alamat;
    public String jam;

    public void JadwalModel(){
        this.namamerchant = namamerchant;
        this.alamat = alamat;
        this.jam = jam;
    }

    public String getNamamerchant() {
        return namamerchant;
    }
    public void setNamamerchant(String namamerchant) {
        this.namamerchant = namamerchant;
    }

    public String getAlamat() {
        return alamat;
    }
    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getJam() {
        return jam;
    }
    public void setJam(String jam) {
        this.jam = jam;
    }
}
