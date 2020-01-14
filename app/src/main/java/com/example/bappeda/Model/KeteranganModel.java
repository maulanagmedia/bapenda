package com.example.bappeda.Model;

import androidx.annotation.NonNull;

public class KeteranganModel {

    public String nama = "";
    private String idKategori = "";

    public KeteranganModel(){

    }

    public KeteranganModel(String idKategori, String nama){
        this.idKategori = idKategori;
        this.nama = nama;
    }

    public String getNama() {
        return nama;
    }
    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getIdKategori() {
        return idKategori;
    }
    public void setIdKategori(String id) {
        this.idKategori = id;
    }

    @NonNull
    @Override
    public String toString() {
        return getNama();
    }
}
