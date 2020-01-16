package com.example.bappeda.Model;

import java.util.ArrayList;

public class MerchantModel {
    //Info Usaha
    private String id = "";
    private String namamerchant = "";
    private String alamat = "";
    private String tanggal = "";
    private boolean badan_usaha = true;
    private String notelp = "";
    private CategoryModel kota = new CategoryModel();
    private CategoryModel kecamatan = new CategoryModel();
    private CategoryModel kelurahan = new CategoryModel();

    //Info Pemilik
    private String namapemilik = "";
    private String nik = "";
    private String alamatpemilik = "";
    private String notelppemilik = "";
    private CategoryModel klasifikasi_usaha = new CategoryModel();
    private String keterangan = "";
    private String idPenugasan = "";
    private String statusPendaftaran = "";
    private String kodePendaftaran = "";
    private String latitudeString = "";
    private String longitudeString = "";
    private String statusreklame = "";
    private String insertat = "";
    private String ketstatusreklame = "";
    private String bidangusaha = "";
    private String idketerangan = "";




    //list Gambar
    private ArrayList<String> Images = new ArrayList<>();

    //String 1 Gambar
    private String image = "";

    //Lokasi
    private double latitude = 0;
    private double longitude = 0;

    //Deskripsi Monitoring
    private String deskripsi = "";

    //Merchant Terdekat
    private String jarak = "";

    //Merchant Tutup
    private String alasan_tutup = "";
    private String  flag = "";

    //Notifikasi
    private String title = "";

    //NPWPDWP
    private String npwpdwp;

    //constructor method used to create new properties later as the data source
    public MerchantModel(){
    }

    public MerchantModel(String id, String nama, String alamat){
        this.id = id;
        this.namamerchant = nama;
        this.alamat = alamat;
    }

    public String getNamamerchant() {return namamerchant; }
    public void setNamamerchant(String namamerchant) {this.namamerchant = namamerchant; }

    public String getAlamat() {return alamat; }
    public void setAlamat(String alamat) {this.alamat = alamat;}

    public String getTanggal() {return tanggal;}
    public void setTanggal(String tanggal) {this.tanggal = tanggal;}

    public String getId() {
        return this.id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getIdKterangan() {
        return this.idketerangan;
    }
    public void setIdketerangan(String idketerangan) {
        this.idketerangan = idketerangan;
    }

    public String getNamapemilik() {
        return namapemilik;
    }
    public void setNamapemilik(String namapemilik) {
        this.namapemilik = namapemilik;
    }

    public String getBidangusaha() {
        return bidangusaha;
    }
    public void setBidangusaha(String bidangusaha) {
        this.bidangusaha = bidangusaha;
    }

    public String getNotelp() {
        return notelp;
    }
    public void setNotelp(String notelp) {
        this.notelp = notelp;
    }

    public CategoryModel getKategori() {
        return klasifikasi_usaha;
    }
    public void setKategori(CategoryModel kategori) {
        this.klasifikasi_usaha = kategori;
    }

    public double getLatitude() {
        return latitude;
    }
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setKecamatan(CategoryModel kecamatan) {
        this.kecamatan = kecamatan;
    }
    public CategoryModel getKecamatan() {
        return kecamatan;
    }

    public void setKelurahan(CategoryModel kelurahan) {
        this.kelurahan = kelurahan;
    }
    public CategoryModel getKelurahan() {
        return kelurahan;
    }

    public void setKota(CategoryModel kota) {
        this.kota = kota;
    }
    public CategoryModel getKota() {
        return kota;
    }

    public void setKlasifikasi_usaha(CategoryModel klasifikasi_usaha) {
        this.klasifikasi_usaha = klasifikasi_usaha;
    }
    public CategoryModel getKlasifikasi_usaha() {
        return klasifikasi_usaha;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }
    public String getKeterangan() {
        return keterangan;
    }

    public void setNik(String nik) {
        this.nik = nik;
    }
    public String getNik() {
        return nik;
    }

    public void setNotelppemilik(String notelppemilik) {
        this.notelppemilik = notelppemilik;
    }
    public String getNotelppemilik() {
        return notelppemilik;
    }

    public void setAlamatpemilik(String alamatpemilik) {
        this.alamatpemilik = alamatpemilik;
    }
    public String getAlamatpemilik() {
        return alamatpemilik;
    }

    public void setBadan_usaha(boolean badan_usaha) {
        this.badan_usaha = badan_usaha;
    }
    public boolean isBadan_usaha() {
        return badan_usaha;
    }

    public String getDeskripsi() {
        return deskripsi;
    }
    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public String getJarak() {
        return jarak;
    }
    public void setJarak(String jarak) {
        this.jarak = jarak;
    }

    public String getAlasan_tutup() {
        return alasan_tutup;
    }
    public void setAlasan_tutup(String alasan_tutup) {
        this.alasan_tutup = alasan_tutup;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getFlag() {
        return flag;
    }
    public void setFlag(String flag) {
        this.flag = flag;
    }

    //Ambil gambar pertama
    public ArrayList<String> getImages() {
        return Images;
    }

    //Ambil satu gambar
    public String getImage(){
        if(Images == null || Images.size() < 1){
            return "";
        }
        else{
            return Images.get(0);
        }
    }

    public String getOneImage(){
        return image;
    }

    public void setImages(ArrayList<String> urlImages) {
        this.Images = urlImages;
    }

    // 1 gambar
    public void setImage(String urlImages) {
        this.image = urlImages;
    }

    public String getNpwpdwp() {
        return npwpdwp;
    }
    public void setNpwpdwp(String npwpdwp) {
        this.npwpdwp = npwpdwp;
    }

    public String getIdPenugasan() {
        return idPenugasan;
    }

    public void setIdPenugasan(String idPenugasan) {
        this.idPenugasan = idPenugasan;
    }

    public String getStatusPendaftaran() {
        return statusPendaftaran;
    }

    public void setStatusPendaftaran(String statusPendaftaran) {
        this.statusPendaftaran = statusPendaftaran;
    }

    ////////////////////////////////////////////

    public void setStatusReklame(String statusReklame) {
        this.statusreklame = statusReklame;
    }

    public void setInsertat(String statusInsertat) {
        this.insertat = statusInsertat;
    }

    public  String getKetstatusreklame(){
        return ketstatusreklame;
    }
    public void setKetstatusreklame(String ketstatusreklame) {
        this.ketstatusreklame = ketstatusreklame;
    }

    public String getKodePendaftaran() {
        return kodePendaftaran;
    }

    public void setKodePendaftaran(String kodePendaftaran) {
        this.kodePendaftaran = kodePendaftaran;
    }

    public String getLatitudeString() {
        return latitudeString;
    }

    public void setLatitudeString(String latitudeString) {
        this.latitudeString = latitudeString;
    }

    public String getLongitudeString() {
        return longitudeString;
    }

    public void setLongitudeString(String longitudeString) {
        this.longitudeString = longitudeString;
    }
}
