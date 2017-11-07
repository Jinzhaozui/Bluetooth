package com.hexing.bluetooth.db.model;

import com.j256.ormlite.field.DatabaseField;

/**
 * Created by caibinglong
 * on 2017/7/24.
 * 蓝牙表信息
 */

public class MeterModel {
    //id|name|address|balance|positive|meter_no|device_id
    @DatabaseField(columnName = "id", generatedId = true)
    public int id;//
    @DatabaseField(columnName = "name")
    public String name;// 名称
    @DatabaseField(columnName = "address")
    public String address;//mac address
    @DatabaseField(columnName = "balance")
    public String balance;//余额
    @DatabaseField(columnName = "positive")
    public String positive;//正向有功
    @DatabaseField(columnName = "meter_no")
    public String number;//表号
    @DatabaseField(columnName = "device_id")
    public String deviceId;//设备id
}
