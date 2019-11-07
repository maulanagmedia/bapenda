package com.example.bappeda.MenuAdmin.Model;

import java.util.ArrayList;

public class MerchantPenugasanModel {

    private int idMerchant;
    private String namamerchant = "";
    public String alamat;
    private ArrayList<String> Images;

    public void MerchantPenugasanModel(){

    }

    public int getIdMerchant() {
        return idMerchant;
    }
    public void setIdMerchant(int idMerchant) {
        this.idMerchant = idMerchant;
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

    public ArrayList<String> getImages() {
        return Images;
    }
    public void setImages(ArrayList<String> urlImages) {
        this.Images = urlImages;
    }

    public String getFirstImage(){
        if(Images == null || Images.size() < 1){
            return "";
        }
        else{
            return Images.get(0);
        }
    }
}
