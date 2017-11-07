package com.hexing.bluetooth.adapter;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.hexing.bluetooth.BluetoothImpl;
import com.hexing.bluetooth.HexCallback;

/**
 * Created by long
 * on 2017-7-10.
 * <p>
 * 支持API 18-20，从18开始Android支持低功耗BLE了，其实扫描的核心还是使用{@link BluetoothLeScanner}，
 * API 21以上请查看{@link ArBluetoothPeripheral}。
 */
@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class ArBluetoothLeGatt extends ArBluetooth {
    private BluetoothAdapter.LeScanCallback leScanCallback;
    private HexCallback callback;
    public ArBluetoothLeGatt(Context context, BluetoothAdapter.LeScanCallback leScanCallback,HexCallback callback) {
        super(context);
        this.leScanCallback = leScanCallback;
        this.callback = callback;
    }

    /**
     * 扫描设备
     */
    public void scanLeDevice() {
        if (isSupportBluetooth() && isEnabled()) {
            setScanning(true);
            getBluetoothAdapter().startLeScan(leScanCallback);
            getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    stopLeScan();
                }
            }, getScanPeriod());
        }
    }

    /**
     * 停止扫描
     */
    public void stopLeScan() {
        if (isSupportBluetooth() && isEnabled()) {
            setScanning(false);
            getBluetoothAdapter().stopLeScan(leScanCallback);
        }
        Log.e("蓝牙扫描定时结束", this.callback + "");
        if (this.callback != null) this.callback.onScanFinish(true);
    }

}
