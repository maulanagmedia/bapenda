package com.example.bappeda.MenuHome.Data;

import com.example.bappeda.MenuHome.Model.NotifModel;

import java.util.ArrayList;

public class NotifData {

    private static String[] notifNames = {
            "Survey",
            "Monitoring",
            "Hubungi Petugas"
    };

    private static String[] notifId = {
            "1",
            "2",
            "3"
    };

    public static ArrayList<NotifModel> getListData(){
        ArrayList<NotifModel> list = new ArrayList<>();
        for (int position = 0; position < notifNames.length; position++){
            NotifModel notifModel = new NotifModel();
            notifModel.setNamaKategori(notifNames[position]);
            list.add(notifModel);
        }
        return list;
    }
}
