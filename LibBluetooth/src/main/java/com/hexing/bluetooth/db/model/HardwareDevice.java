package com.hexing.bluetooth.db.model;

import com.hexing.bluetooth.db.DBConfig;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by caibinglong
 * on 2017/7/24.
 * 智能硬件设备
 */
@DatabaseTable(tableName = DBConfig.TABLE_HARDWARE_DEVICE)
public class HardwareDevice {
    //id|server_id|device_id|name|icon|create_time|status|update_time|device_type|userId|deleteStatus
    @DatabaseField(columnName = "id", generatedId = true)
    public int id;//
    @DatabaseField(columnName = "server_id")
    public int serverId;//服务端db id
    @DatabaseField(columnName = "device_id")
    public String deviceId;//设备id
    @DatabaseField(columnName = "name")
    public String name;
    @DatabaseField(columnName = "icon")
    public String icon;
    @DatabaseField(columnName = "create_time")
    public String createTime;
    @DatabaseField(columnName = "update_time")
    public String updateTime;//更新时间
    @DatabaseField(columnName = "status")
    public String status;//物理状态
    @DatabaseField(columnName = "device_type")
    public String deviceType;//设备类型 lcu meter
    @DatabaseField(columnName = "user_id")
    public String userId;//用户id
    @DatabaseField(columnName = "del_status")
    public int deleteStatus;//删除状态 1 删除绑定 0有效绑定
}
