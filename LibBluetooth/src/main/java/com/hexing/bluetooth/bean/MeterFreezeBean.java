package com.hexing.bluetooth.bean;

import java.io.Serializable;

/**
 * Created by caibinglong
 * on 2017/7/17.
 * 电表冻结数据
 */

public class MeterFreezeBean implements Serializable {
    public String datetime;
    public String year;
    public String month;
    public String day;
    public long positive;//正向有功
    public long reverse;//反向有功

    @Override
    public String toString() {
        return "MeterFreezeBean{" +
                "datetime='" + datetime + '\'' +
                ", year='" + year + '\'' +
                ", month='" + month + '\'' +
                ", day='" + day + '\'' +
                ", positive=" + positive +
                ", reverse=" + reverse +
                '}';
    }
}
