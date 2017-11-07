package com.hexing.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.annotation.NonNull;

import com.hexing.bluetooth.bean.LcuStateBean;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by caibinglong
 * on 2017/7/12.
 * 对外 API
 */

public class HexClientAPI {
    private final String TAG = HexClientAPI.class.getName();
    private static HexClientAPI instance;
    private boolean initialize = false;
    private BluetoothImpl callBack;
    private List<BluetoothImpl> listeners = new ArrayList<>();
    private boolean autoConnect = false;
    private String mDeviceName;
    private String mDeviceAddress;
    private boolean isConnect = false;//是否已经连接
    private boolean isScanDevice = false;//是否已经扫描

    /**
     * 获取实例对象
     *
     * @return instance
     */
    public static HexClientAPI getInstance() {
        if (instance == null) {
            synchronized (HexClientAPI.class) {
                if (instance == null) {
                    instance = new HexClientAPI();
                }
            }
        }
        return instance;
    }

    /**
     * 初始化
     *
     * @param context 上下文
     * @return bool
     */
    public boolean init(@NonNull Context context) {
        CommandAPI.getInstance().init(iCallback);//初始化 command
        AnalysisAPI.getInstance().init();//解析初始化
        AnalysisAPI.getInstance().addListener(iCallback);
        DBManager.getInstance().init(context);
        /**
         * 回调  包含蓝牙连接状态 、数据接收回调
         */

        return this.initialize = ConnectAPI.getInstance().init(context, this.iCallback);
    }

    /**
     * 蓝牙连接
     *
     * @param deviceName    address
     * @param deviceAddress address
     */
    public void connect(String deviceName, String deviceAddress, BluetoothImpl cBack) {
        if (this.initialize) {
            this.callBack = cBack;
            this.listeners.add(cBack);
            this.mDeviceName = deviceName;
            this.mDeviceAddress = deviceAddress;
            if (deviceName == null || deviceAddress == null || !this.isScanDevice) {
                this.autoConnect = true;
                this.isScanDevice = true;
                scanDevice();
            } else {
                ConnectAPI.getInstance().connect(deviceName, deviceAddress);
            }
        }
    }

    /**
     * 重新连接
     */
    public void reconnect() {
        ConnectAPI.getInstance().reconnect();
    }

    /**
     * 扫描蓝牙设备
     */
    public void scanDevice() {
        ConnectAPI.getInstance().scanDevice();
    }

    public void stopScanDevice() {
        ConnectAPI.getInstance().stopScanDevice();
    }

    public boolean isEnabled() {
        return ConnectAPI.getInstance().isEnabled();
    }

    /*
    监听事件
     */
    public void addListener(BluetoothImpl callBack) {
        this.callBack = callBack;
        if (!this.listeners.contains(callBack)) {
            this.listeners.add(callBack);
        }
    }

    /**
     * 查找蓝牙
     *
     * @param deviceAddress mac 地址
     */
    public void findMacAddress(@NonNull String deviceAddress) {
        ConnectAPI.getInstance().scanDevice();
    }

    /**
     * 读取表编号
     */
    public void readMeterNumber() {
        if (this.isConnect) {
            CommandAPI.getInstance().readMeterNumber();
        }
    }

    /**
     * 紧急透支
     */
    public void writeUrgentOverdraft() {
        if (this.isConnect) {
            CommandAPI.getInstance().writeUrgentOverdraft();
        }
    }

    /**
     * 读取月冻结电能信息
     *
     * @param startDate 开始时间
     * @param endDate   结束时间
     */
    public void readMonthFreezePower(@NonNull String startDate, @NonNull String endDate) {
        if (this.isConnect) {
            CommandAPI.getInstance().readMonthFreezePower(startDate, endDate);
        }
    }

    /**
     * 读取日冻结预付费信息
     *
     * @param startDate 开始时间
     * @param endDate   结束时间
     */
    public void readDayFreezePrepaid(@NonNull String startDate, @NonNull String endDate) {
        if (this.isConnect) {
            CommandAPI.getInstance().readDayFreezePrepaid(startDate, endDate);
        }
    }

    /**
     * 读取月冻结预付费信息
     *
     * @param startDate 开始时间
     * @param endDate   结束时间
     */
    public void readMonthFreezePrepaid(@NonNull String startDate, @NonNull String endDate) {
        if (this.isConnect) {
            CommandAPI.getInstance().readMonthFreezePrepaid(startDate, endDate);
        }
    }

    /**
     * 读取日冻结电能信息
     *
     * @param startDate 开始时间
     * @param endDate   结束时间
     */
    public void readDayFreezePower(@NonNull String startDate, @NonNull String endDate) {
        if (this.isConnect) {
            CommandAPI.getInstance().readDayFreezePower(startDate, endDate);
        }
    }

    /**
     * 查询余额
     */
    public void readMeterBalance() {
        if (this.isConnect) {
            CommandAPI.getInstance().readMeterBalance();
        }
    }

    /**
     * 读取电表继电器操作原因
     */
    public void readMeterRelayReason() {
        if (this.isConnect) {
            CommandAPI.getInstance().readMeterRelayReason();
        }
    }

    /**
     * 读取继电器状态
     *
     * @param lcuIndex index
     */
    public void readLcuState(int lcuIndex) {
        if (this.isConnect) {
            CommandAPI.getInstance().readLcuState(lcuIndex);
        }
    }

    /**
     * 查询正向有功
     */
    public void readMeterPositiveActive() {
        if (this.isConnect) {
            CommandAPI.getInstance().readMeterPositiveActive();
        }
    }

    /**
     * 读取时间
     */
    public void readLCUTime() {
        if (this.isConnect) {
            CommandAPI.getInstance().readLCUTime();
        }
    }

    /**
     * 下发token 到表
     *
     * @param token token
     */
    public void writeMeterToken(String token) {
        CommandAPI.getInstance().writeMeterToken(token);
    }

    /**
     * 取消警报
     */
    public void cancelMeterAlarm() {
        if (this.isConnect) {
            CommandAPI.getInstance().cancelMeterAlarm();
        }
    }

    /**
     * 读取 历史命令 记录
     *
     * @param lcuIndex index
     */
    public void readLCUCommandHistory(int lcuIndex) {
        if (this.isConnect) {
            CommandAPI.getInstance().readLCUCommandHistory(lcuIndex);
        }
    }

    /**
     * 读取时段 继电器
     * 定时 闭合 断开
     */
    public void readLCUTimePeriod(int lcuIndex) {
        if (this.isConnect) {
            CommandAPI.getInstance().readLCUTimePeriod(lcuIndex);
        }
    }

    /**
     * 设置定时开关
     *
     * @param lcuIndex index 继电器位置 共有4个 下标1 开始
     * @param dataList 数组
     */
    public void writeLCUTimingSwitch(int lcuIndex, List<LcuStateBean> dataList) {
        if (this.isConnect) {
            CommandAPI.getInstance().writeLCUTimingSwitch(lcuIndex, dataList);
        }
    }

    public String getErrorString(String strResult) {
        if (strResult.equals("00"))
            strResult = "Token Download Success";
        else if (strResult.equals("01")) {
            strResult = "TOKEN Parse Error";
        } else if (strResult.equals("02")) {
            strResult = "TOKEN Used";
        } else if (strResult.equals("03")) {
            strResult = "TOKEN Out of Date";
        } else if (strResult.equals("04")) {
            strResult = "Key Out of Date";
        } else if (strResult.equals("05")) {
            strResult = "Recharge value over accumulation limit";
        } else if (strResult.equals("06")) {
            strResult = "Key type is not allowed to recharge";
        } else if (strResult.equals("07")) {
            strResult = "Test code produced by a non designated manufacturer";
        } else if (strResult.equals("08")) {
            strResult = "Token Type Error";
        } else if (strResult.equals("09")) {
            strResult = "Key Type Error";
        } else if (strResult.equals("10")) {
            strResult = "Token Type Error";
        } else {
            strResult = "Token Download Failed";
        }
        return strResult;
    }

    /**
     * 销毁
     */
    public void onDestroy() {
        this.isConnect = false;
        this.isScanDevice = false;
        ConnectAPI.getInstance().onDestroy();
    }

    public HexCallback getCallback() {
        return this.iCallback;
    }

    private AbstractHexCallback iCallback = new AbstractHexCallback() {
        @Override
        public void onReceiver(Object data) {
            super.onReceiver(data);
            //AnalysisAPI.getInstance().analysisReceiverData(data.toString());
            if (callBack != null)
                callBack.receiver(data);
        }

        @Override
        public void onAnalysis(Object data) {
            super.onAnalysis(data);
            //if (callBack != null) callBack.analysis(data);
            for (BluetoothImpl item : listeners) {
                item.analysis(data);
            }
        }

        @Override
        public void connectFail() {
            super.connectFail();
            isConnect = false;
            reconnect();
            for (BluetoothImpl item : listeners) {
                item.connectFail();
            }
        }

        @Override
        public void onConnectSuccess() {
            super.onConnectSuccess();
            isConnect = true;
            //if (callBack != null) callBack.connectSuccess();
            for (BluetoothImpl item : listeners) {
                item.connectSuccess();
            }
        }

        @Override
        public void onScanResult(BluetoothDevice device) {
            super.onScanResult(device);
            if (device != null && device.getAddress() != null && device.getName() != null
                    && autoConnect && mDeviceAddress != null && mDeviceName != null) { //扫描自动连接
                if (device.getAddress().equals(mDeviceAddress) || device.getName().equals(mDeviceName)) {
                    ConnectAPI.getInstance().connect(mDeviceName, mDeviceAddress);
                }
            }
            if (callBack != null) callBack.onScanResult(device);
        }

        @Override
        public void onSendResult(String result) {
            super.onSendResult(result);
            if (callBack != null) callBack.onSendResult(result);
        }

        @Override
        public void onScanFail(int errorCode) {
            super.onScanFail(errorCode);
            if (callBack != null) callBack.scanFail(errorCode);
        }

        @Override
        public void onScanFinish(boolean bool) {
            super.onScanFinish(bool);
            if (callBack != null) callBack.scanFinish();
        }
    };
}

