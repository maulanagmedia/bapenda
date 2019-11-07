package com.example.bappeda.Utils;

public class userPosition {

    static double latitude;
    static double longitude;

    public static double getLatitude() {return latitude;}
    public static double getLongitude() {
        return longitude;
    }

    public static void setLatitude(double latitude) {
        userPosition.latitude = latitude;
    }
    public static void setLongitude(double longitude) {
        userPosition.longitude = longitude;
    }
}
