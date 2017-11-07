package com.hexing.bluetooth.db.model;

import com.hexing.bluetooth.db.DBConfig;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by caibinglong
 * on 2017/7/24.
 * 蓝牙表 月冻结数据
 */

@DatabaseTable(tableName = DBConfig.TABLE_METER_MONTH_FREEZE)
public class MeterMonthFreeze {
    @DatabaseField(columnName = "id", generatedId = true)
    public int id;//
    @DatabaseField(columnName = "server_id")
    public int serverId;//服务端db id
    @DatabaseField(columnName = "create_time")
    public String createTime;
    @DatabaseField(columnName = "positive_active")
    public String positive;//正向有功
    @DatabaseField(columnName = "reverse_active")
    public String reverse;//反向有功
    @DatabaseField(columnName = "freeze_time")
    public String freezeTime;//冻结时间
    @DatabaseField(columnName = "update_time")
    public String updateTime;//更新时间
    @DatabaseField(columnName = "retry_num")
    public String retryNum;//重试查询次数
    @DatabaseField(columnName = "device_id")
    public String deviceId;//设备id
}
