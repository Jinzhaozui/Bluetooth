package com.hexing.bluetooth.db.model;

import android.provider.ContactsContract;


import org.litepal.crud.DataSupport;

import java.sql.Time;

/**
 * Created by HET075 on 2017/10/26.
 */

public class LocationStore extends DataSupport {

    private double latitude;
    private double longitude;
    private double signal;
    private String date;

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

    public double getSignal() {
        return signal;
    }

    public void setSignal(double signal) {
        this.signal = signal;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
