package com.example.bappeda.Utils;

public class URL {
    //URL
    private static final String BASE_URL = "http://gmedia.bz/bapenda/api/";

    public static final String URL_LOGIN = BASE_URL + "authentication";
    public static final String URL_SEARCH_MERCHANT = BASE_URL + "Survey/search_merchant";
    public static final String URL_VIEW_MERCHANT = BASE_URL + "Survey/view_merchant";
    public static final String URL_ALL_MERCHANT = BASE_URL + "Survey/all_merchant";
    public static final String URL_ADD_MERCHANT = BASE_URL + "Survey/add_merchant";
    public static final String URL_DAFTAR_STATUS = BASE_URL + "Pendaftaran/Survey";
    public static final String URL_VIEW_MERCHANT_BY_ID = BASE_URL + "Survey/view_merchant_by_id";
    public static final String URL_CATEGORY = BASE_URL + "Survey/kategori";
    public static final String URL_EDIT_MERCHANT = BASE_URL + "Survey/edit_merchant";
    public static final String URL_VIEW_USER_BY_ID = BASE_URL + "User/view_user_by_id";
    public static final String URL_PROSES_USER_EDIT = BASE_URL + "User/prosess_user";
    public static final String URL_GANTI_PASSWORD = BASE_URL + "Authentication/update_password";
    public static final String URL_UPDATE_GAMBAR = BASE_URL + "User/update_gambar_user";
    public static final String URL_UPDATE_FCM = BASE_URL + "Authentication/update_fcm";
    public static final String URL_UPDATE_LOCATION = BASE_URL + "Monitoring/udpate_lokasi_petugas";
    public static final String URL_MONITORING_MERCHANT = BASE_URL + "MonitoringMerchant/jadwal_monitoring";
    public static final String URL_RIWAYAT_MONITORING = BASE_URL + "MonitoringMerchant/riwayat_monitoring";
    public static final String URL_DETAIL_MONITORING = BASE_URL + "Monitoring/monitoring_merchant";
    public static final String URL_MERCHANT_PENUGASAN = BASE_URL + "Penugasan/merchant_penugasan";
    public static final String URL_DATA_PETUGAS = BASE_URL + "Authentication/data_petugas";
    public static final String URL_KIRIM_SURVEY = BASE_URL + "Penugasan/penugasan_survey_admin";
    public static final String URL_KIRIM_MONITORING = BASE_URL + "Monitoring/jadwal_monitoring_petugas";
    public static final String URL_HUBUNGI_PETUGAS = BASE_URL + "Monitoring/hubungi_petugas";
    public static final String URL_DATA_PENUGASAN_SURVEY = BASE_URL + "Penugasan/data_penugasan_survey";
    public static final String URL_MASTER_KOTA = BASE_URL + "Master/kota";
    public static final String URL_MASTER_KECAMATAN = BASE_URL + "Master/kecamatan";
    public static final String URL_MASTER_KELURAHAN = BASE_URL + "Master/kelurahan";
    public static final String URL_MERCHANT_TERDEKAT = BASE_URL + "merchant";
    public static final String URL_NOTIFIKASI = BASE_URL + "Notifikasi";
    public static final String URL_ADD_MERCHANT_TUTUP = BASE_URL + "MerchantTutup";
    public static final String URL_VIEW_MERCHANT_TUTUP = BASE_URL + "MerchantTutup/view_merchant_tutup";
    public static final String URL_VIEW_MERCHANT_PENDAFTARAN = BASE_URL + "PendaftaranMerchant/";
    public static final String getRiwayatSurvey = BASE_URL + "RiwayatSurvey/";
    public static final String getMerchantMonitoring = BASE_URL + "MonitoringMerchant/";
    public static final String getRiwayatPendaftaran = BASE_URL + "PendaftaranMerchant/riwayat_pendaftaran/";
    public static final String getJadwalMerchantTertutup = BASE_URL + "MerchantTutup/view_merchant_buka/";
    public static final String simpanLokasiMerchant = BASE_URL + "MerchantTutup/update_tempat/";
    public static final String getRiwayatPerubahanLokasi = BASE_URL + "MerchantTutup/riwayat_tempat/";
    public static final String getMenu = BASE_URL + "authentication/menu/";
    public static final String getReklame = BASE_URL + "Merchant/reklame/";
    public static final String getKetReklame = BASE_URL + "merchant/ms_ket_reklame";
    public static final String getSimpanReklame = BASE_URL + "merchant/monitor_reklame/";
    public static final String getRiwayatReklame = BASE_URL + "merchant/riwayat_reklame/";
    public static final String takeAbsen = BASE_URL + "authentication/absens/";
    public static final String getRiwayatAbsensi = BASE_URL + "authentication/riwayat_absens/";

    //Form
    public static final String URL_FORM = "http://gmedia.bz/bapenda/Form/form_kategori";

    //INTENT EXTRA
    public static final String EXTRA_ID_MERCHANT = "id_merchant";
    public static final String EXTRA_ID_KATEGORI = "id_kategori";
    public static final String EXTRA_MERCHANT = "merchant";
    public static final String EXTRA_IMAGES = "images";
    public static final String EXTRA_IDP = "idp";

    //CODE
    public static final int CODE_UPLOAD = 909;
    public static final int CODE_PERMISSION_WRITE_STORAGE = 900;
    public static final int CODE_PERMISSION_LOCATION = 91;
}
