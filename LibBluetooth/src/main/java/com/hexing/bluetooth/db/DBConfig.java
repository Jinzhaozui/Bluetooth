package com.hexing.bluetooth.db;

import com.hexing.bluetooth.db.model.HardwareDevice;
import com.hexing.bluetooth.db.model.MeterDayFreeze;
import com.hexing.bluetooth.db.model.MeterMonthFreeze;
import com.hexing.bluetooth.db.model.RelayActionTask;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by long on 16-5-31.
 * 数据库配置
 */
public class DBConfig {

    //db_name
    public static final String DB_NAME = "HexBluetooth.db";
    //db_version
    public static final int DB_VERSION = 1;

    //table_name
    public static final String TABLE_METER_DAY_FREEZE = "MeterDayFreeze";
    public static final String TABLE_METER_MONTH_FREEZE = "MeterMonthFreeze";
    public static final String TABLE_HARDWARE_DEVICE = "HardwareDevice";
    public static final String TABLE_RELAY_ACTION_TASK = "RelayActionTask";

    //table class list
    public static final List<Class<?>> DB_CLASSES = new ArrayList<>();

    static {
        DB_CLASSES.add(MeterDayFreeze.class);
        DB_CLASSES.add(MeterMonthFreeze.class);
        DB_CLASSES.add(HardwareDevice.class);
        DB_CLASSES.add(RelayActionTask.class);
    }
}
