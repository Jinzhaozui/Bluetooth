package com.hexing.bluetooth;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.RelativeLayout;

import com.hexing.bluetooth.db.DatabaseHelper;
import com.hexing.bluetooth.db.dao.CommonDao;
import com.hexing.bluetooth.db.model.HardwareDevice;
import com.hexing.bluetooth.db.model.MeterDayFreeze;
import com.hexing.bluetooth.db.model.MeterMonthFreeze;
import com.hexing.bluetooth.db.model.RelayActionTask;

import org.bouncycastle.util.encoders.Hex;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by caibinglong
 * on 2017/7/24.
 * 数据块操作管理
 */

public class DBManager {
    private CommonDao<MeterDayFreeze> dayFreezeCommonDao;
    private CommonDao<MeterMonthFreeze> monthFreezeCommonDao;
    private CommonDao<HardwareDevice> hdCommonDao;
    private CommonDao<RelayActionTask> relayDao;
    private boolean initialize = false;
    private static DBManager instance;

    /**
     * 获取实例对象
     *
     * @return instance
     */
    public static DBManager getInstance() {
        if (instance == null) {
            synchronized (DBManager.class) {
                if (instance == null) {
                    instance = new DBManager();
                }
            }
        }
        return instance;
    }

    public void init(@NonNull Context context) {
        DatabaseHelper databaseHelper = DatabaseHelper.open(context);
        dayFreezeCommonDao = new CommonDao<>(databaseHelper, MeterDayFreeze.class);
        monthFreezeCommonDao = new CommonDao<>(databaseHelper, MeterMonthFreeze.class);
        hdCommonDao = new CommonDao<>(databaseHelper, HardwareDevice.class);
        relayDao = new CommonDao<>(databaseHelper, RelayActionTask.class);
        this.initialize = true;
    }

    /**
     * 添加日冻结 或更新
     *
     * @param model 数据模型
     */
    public void addOrUpdateDayFreeze(MeterDayFreeze model) {
        if (this.initialize) {
            MeterDayFreeze queryModel = dayFreezeCommonDao.queryById(model.id);
            if (queryModel == null) dayFreezeCommonDao.add(model);
            else dayFreezeCommonDao.update(model);
        }
    }

    /**
     * 添加月冻结 或更新
     *
     * @param model 数据模型
     */
    public void addOrUpdateMonthFreeze(MeterMonthFreeze model) {
        if (this.initialize) {
            MeterMonthFreeze queryModel = monthFreezeCommonDao.queryById(model.id);
            if (queryModel == null) monthFreezeCommonDao.add(model);
            else monthFreezeCommonDao.update(model);
        }
    }

    /**
     * 添加 或 更新 硬件设备表
     *
     * @param model 数据模型
     */
    public void addOrUpdateHDDevice(HardwareDevice model) {
        if (this.initialize) {
            HardwareDevice queryModel = hdCommonDao.queryById(model.id);
            if (queryModel == null) hdCommonDao.add(model);
            else hdCommonDao.update(model);
        }
    }

    /**
     * 添加 或 更新 继电器 定时任务
     *
     * @param model 数据模型
     */
    public void addOrUpdateRelayAcTask(RelayActionTask model) {
        if (this.initialize) {
            RelayActionTask queryModel = relayDao.queryById(model.id);
            if (queryModel == null) relayDao.add(model);
            else relayDao.update(model);
        }
    }

    /**
     * 添加 硬件设备
     * @param model 数据模型
     */
    public void addHardwareDevice(HardwareDevice model) {
        if (this.initialize) {
            HardwareDevice queryModel = hdCommonDao.queryById(model.serverId);
            if (queryModel != null) {
                hdCommonDao.delete(queryModel);
            }
            hdCommonDao.add(model);
        }
    }

    /**
     * 获取 硬件设备列表
     *
     * @param type meter lcu
     * @return list
     */
    public List<HardwareDevice> getHdDeviceList(String type) {
        Map<String, Object> map = new HashMap<>();
        map.put("type", type);
        try {
            return hdCommonDao.queryByCondition("id", true, map);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    /**
     * 清除数据
     */
    public void clear() {
        if (this.initialize) {
            dayFreezeCommonDao.deleteAll(MeterDayFreeze.class);
            monthFreezeCommonDao.deleteAll(MeterMonthFreeze.class);
            hdCommonDao.deleteAll(HardwareDevice.class);
            relayDao.deleteAll(RelayActionTask.class);
        }
    }
}
