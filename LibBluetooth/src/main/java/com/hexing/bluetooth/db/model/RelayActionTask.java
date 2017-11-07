package com.hexing.bluetooth.db.model;

import com.hexing.bluetooth.db.DBConfig;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by caibinglong
 * on 2017/7/24.
 * 继电器定时 动作
 */
@DatabaseTable(tableName = DBConfig.TABLE_RELAY_ACTION_TASK)
public class RelayActionTask {
    //id|server_id|relay_id|action_time|action|update_time
    @DatabaseField(columnName = "id", generatedId = true)
    public int id;//
    @DatabaseField(columnName = "server_id")
    public int serverId;//服务端db id
    @DatabaseField(columnName = "relay_id")
    public String relayId; //关联HardwareDevice  自增id
    @DatabaseField(columnName = "action_time")
    public String actionTime;//时间动作
    @DatabaseField(columnName = "action")
    public String action;//动作 开关
    @DatabaseField(columnName = "update_time")
    public String updateTime;//更新时间
}
