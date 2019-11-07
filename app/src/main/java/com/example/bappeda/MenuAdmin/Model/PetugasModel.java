package com.example.bappeda.MenuAdmin.Model;

public class PetugasModel {

    private int idpetugas;
    private String namapetugas;
    private String email;
    private String images;

    public void PetugasModel(){

    }

    public int getIdpetugas() {
        return idpetugas;
    }
    public void setIdpetugas(int idpetugas) {
        this.idpetugas = idpetugas;
    }

    public String getNamapetugas() {
        return namapetugas;
    }
    public void setNamapetugas(String namapetugas) {
        this.namapetugas = namapetugas;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getImages() {
        return images;
    }
    public void setImages(String urlImages) {
        this.images = urlImages;
    }
}
