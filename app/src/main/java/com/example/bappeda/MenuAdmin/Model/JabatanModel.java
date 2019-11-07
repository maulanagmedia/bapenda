package com.example.bappeda.MenuAdmin.Model;

public class JabatanModel {

    private String id_level;
    private  String namaJabatan;
    private boolean aktif = false;

    public void JabatanModel(){}

    public void setAktif(boolean aktif) {
        this.aktif = aktif;
    }
    public boolean isAktif() {
        return aktif;
    }

    public void setId_level(String id_level) {
        this.id_level = id_level;
    }
    public String getId_level() {
        return id_level;
    }

    public String getNamaJabatan() {
        return namaJabatan;
    }
    public void setNamaJabatan(String namaJabatan) {
        this.namaJabatan = namaJabatan;
    }
}
