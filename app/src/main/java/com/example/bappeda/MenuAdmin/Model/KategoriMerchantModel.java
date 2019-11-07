package com.example.bappeda.MenuAdmin.Model;

public class KategoriMerchantModel {

    private String id_kategori;
    private  String namaMerchant;
    private boolean aktif = false;

    public void JabatanModel(){}

    public void setId_kategori(String id_kategori) {
        this.id_kategori = id_kategori;
    }
    public String getId_kategori() {
        return id_kategori;
    }

    public void setAktif(boolean aktif) {
        this.aktif = aktif;
    }
    public boolean isAktif() {
        return aktif;
    }

    public String getNamaMerchant() {
        return namaMerchant;
    }
    public void setNamaMerchant(String namaMerchant) {
        this.namaMerchant = namaMerchant;
    }
}
