package com.example.bluetooth.le.bean;

import java.io.Serializable;

/**
 * Created by caibinglong
 * on 2017/6/9.
 */

public class LcuStateBean implements Serializable{
    public String hour;
    public String minute;
    public String action;

    @Override
    public String toString() {
        return "LcuStateBean{" +
                "action='" + action + '\'' +
                ", hour='" + hour + '\'' +
                ", minute='" + minute + '\'' +
                '}';
    }
}
