package com.hexing.bluetooth;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.util.Log;

import com.hexing.bluetooth.adapter.ArBluetooth;
import com.hexing.bluetooth.adapter.ArBluetoothLeGatt;
import com.hexing.bluetooth.adapter.ArBluetoothPeripheral;
import com.hexing.bluetooth.receiver.BluetoothReceiver;
import com.hexing.bluetooth.services.BluetoothLeService;

import java.util.Arrays;
import java.util.List;

import static android.os.Build.VERSION_CODES.LOLLIPOP;


/**
 * Created by caibinglong
 * on 2017/7/7.
 */

public class ConnectAPI {
    private final String TAG = ConnectAPI.class.getName();
    private BluetoothLeService mBluetoothLeService;
    private BluetoothReceiver bluetoothReceiver;
    private BluetoothAdapter mBluetoothAdapter;

    //蓝牙适配器，Android 17版本就不支持了
    protected ArBluetooth arBluetooth;
    protected ArBluetoothLeGatt gatt;
    protected ArBluetoothPeripheral peripheral;
    private HexCallback callback;

    private static ConnectAPI instance;

    private Context mContext;
    private String mDeviceAddress;
    private boolean initialize = false;
    private boolean isReadyConnect = false;

    /**
     * 获取实例对象
     *
     * @return instance
     */
    public static ConnectAPI getInstance() {
        if (instance == null) {
            synchronized (ConnectAPI.class) {
                if (instance == null) {
                    instance = new ConnectAPI();
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
    public boolean init(@NonNull Context context, HexCallback callBack) {
        this.mContext = context;
        this.callback = callBack;
        // 检查当前手机是否支持ble 蓝牙,如果不支持退出程序
        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            return false;
        }
        startBluetooth();
        Intent gattServiceIntent = new Intent(this.mContext, BluetoothLeService.class);
        this.mContext.bindService(gattServiceIntent, mServiceConnection, android.content.Context.BIND_AUTO_CREATE);
        this.initialize = true;
        return true;
    }

    /**
     * 蓝牙连接
     *
     * @param deviceName    deviceName
     * @param deviceAddress address
     */
    public void connect(String deviceName, @NonNull String deviceAddress) {
        if (this.initialize && this.mBluetoothLeService != null) {
            if (!this.isReadyConnect) {
                AnalysisAPI.getInstance().setFramePara(deviceName, deviceAddress);
                this.bluetoothReceiver = new BluetoothReceiver(this.mContext, this.callback);
                this.bluetoothReceiver.register(this.bluetoothReceiver);
                this.mDeviceAddress = deviceAddress;
                this.mBluetoothLeService.connect(this.mDeviceAddress);
                this.isReadyConnect = true;
            }
        } else {
            Log.e(TAG, "未初始化成功");
        }
    }

    /**
     * 重新连接
     */
    public void reconnect() {
        if (mBluetoothLeService != null && this.initialize) {
            this.mBluetoothLeService.connect(this.mDeviceAddress);
        }
    }

    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                return;
            }
            mBluetoothAdapter = mBluetoothLeService.getBluetoothAdapter();
            // Automatically connects to the device upon successful start-up initialization.
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.e(TAG, "onServiceDisconnected");
            mBluetoothLeService = null;
        }
    };

    public boolean isEnabled() {
        if (this.mBluetoothAdapter != null)
            return this.mBluetoothAdapter.isEnabled();
        return false;
    }

    /**
     * 获取蓝牙广播对象
     * @return BluetoothReceiver
     */
    public BluetoothReceiver getBluetoothReceiver(){
        return this.bluetoothReceiver;
    }

    /**
     * 创建蓝牙适配器
     */
    public final void startBluetooth() {
        //根据系统版本创建初始化蓝牙适配器
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 &&
                Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT_WATCH) {
            this.gatt();
        } else if (Build.VERSION.SDK_INT >= LOLLIPOP) {
            this.peripheral();
        }
    }

    /**
     * API 18-20版本的蓝牙适配器
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void gatt() {
        this.gatt = new ArBluetoothLeGatt(this.mContext, new BluetoothAdapter.LeScanCallback() {
            @Override
            public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                if (callback != null) {
                    callback.onScanResult(device);
                }
            }
        }, this.callback);
        this.arBluetooth = this.gatt;
    }

    /**
     * API 21版本的蓝牙适配器
     */
    @TargetApi(LOLLIPOP)
    private void peripheral() {
        this.peripheral = new ArBluetoothPeripheral(this.mContext, new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                super.onScanResult(callbackType, result);
                if (callback != null) {
                    callback.onScanResult(result.getDevice());
                }
            }

            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
            }
        }, this.callback);
        this.arBluetooth = this.peripheral;
    }

    /**
     * 开始扫描
     */
    public void scanDevice() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 &&
                Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT_WATCH) {
            this.gatt.scanLeDevice();
        } else if (Build.VERSION.SDK_INT >= LOLLIPOP) {
            this.peripheral.startScan();
        }
    }

    /**
     * 停止扫描
     */
    public void stopScanDevice() {
        if (arBluetooth == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 &&
                Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT_WATCH) {
            gatt.stopLeScan();
        } else if (Build.VERSION.SDK_INT >= LOLLIPOP) {
            peripheral.stopScanning();
        }
    }

    public void onDestroy() {
        if (this.bluetoothReceiver != null)
            this.bluetoothReceiver.unRegister(this.bluetoothReceiver);
        if (this.mBluetoothLeService != null)
            this.mBluetoothLeService.disconnect();
        this.isReadyConnect = false;
    }

}
