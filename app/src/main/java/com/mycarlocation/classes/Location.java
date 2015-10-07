package com.mycarlocation.classes;

/**
 * Created by rafaxplayer on 04/10/2015.
 */
public class Location {
    public String latitude;
    public String longitude;
    public String direction;
    public String date;
    public String keyfirebase;


    public Location() {


    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getDirection() {
        return direction;
    }

    public String getDate() {
        return date;
    }

    public String getKeyfirebase() {
        return keyfirebase;
    }

    public void setKeyfirebase(String keyfirebase) {
        this.keyfirebase = keyfirebase;
    }
}
