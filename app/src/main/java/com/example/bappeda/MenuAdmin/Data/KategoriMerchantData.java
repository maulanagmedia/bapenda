package com.example.bappeda.MenuAdmin.Data;

import com.example.bappeda.MenuAdmin.Model.KategoriMerchantModel;

import java.util.ArrayList;

public class KategoriMerchantData {

    private static String[] merchantNames = {
            "All",
            "Restoran",
            "Hotel",
            "Hiburan",
            "Parkir"
    };

    private static String [] merchantId = {
            "",
            "1",
            "2",
            "3",
            "4"
    };

    public static ArrayList<KategoriMerchantModel> getListData(){
        ArrayList<KategoriMerchantModel> list = new ArrayList<>();
        for (int position = 0; position < merchantNames.length; position++){
            KategoriMerchantModel kategoriMerchantModel = new KategoriMerchantModel();
            kategoriMerchantModel.setNamaMerchant(merchantNames[position]);
            kategoriMerchantModel.setId_kategori(merchantId[position]);
            list.add(kategoriMerchantModel);
        }
        return list;
    }
}
