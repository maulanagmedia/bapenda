package com.example.bappeda.Model;

public class AccountModel {

    public String namaLengkap = "";
    public String kontak;
    public String password;

    public AccountModel(){}

    public String getNamaLengkap() {
        return namaLengkap;
    }
    public String setNamaLengkap(String namaLengkap) {
        this.namaLengkap = namaLengkap;
        return namaLengkap;
    }

    public String getKontak() {
        return kontak;
    }
    public String setKontak(String kontak) {
        this.kontak = kontak;
        return kontak;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
