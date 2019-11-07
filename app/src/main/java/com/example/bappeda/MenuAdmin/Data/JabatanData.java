package com.example.bappeda.MenuAdmin.Data;

import com.example.bappeda.MenuAdmin.Model.JabatanModel;

import java.util.ArrayList;

public class JabatanData {

    private static String[] jabatanNames = {
            "All",
            "Petugas",
            "Supervisor",
            "Kasubbag",
            "Admin"
    };

    private static String[] jabatanId = {
            "",
            "2",
            "3",
            "4",
            "5"
    };

    public static ArrayList<JabatanModel> getListData(){
        ArrayList<JabatanModel> list = new ArrayList<>();
        for (int position = 0; position < jabatanNames.length; position++){
            JabatanModel jabatanModel = new JabatanModel();
            jabatanModel.setNamaJabatan(jabatanNames[position]);
            jabatanModel.setId_level(jabatanId[position]);
            list.add(jabatanModel);
        }
        return list;
    }
}
