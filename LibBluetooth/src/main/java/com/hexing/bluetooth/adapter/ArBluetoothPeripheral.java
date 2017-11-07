package com.hexing.bluetooth.adapter;

import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.hexing.bluetooth.BluetoothImpl;
import com.hexing.bluetooth.HexCallback;

import java.util.List;

/**
 * Created by 980086933@qq.com on 2016-12-12.
 * <p>
 * API 21以上支持，API 18-20请查看{@link ArBluetoothLeGatt}
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class ArBluetoothPeripheral extends ArBluetooth {
    private ScanCallback scanCallback;
    private BluetoothLeScanner bluetoothLeScanner;
    private List<ScanFilter> scanFilters;
    private HexCallback callback;

    public ArBluetoothPeripheral(Context context, ScanCallback scanCallback, HexCallback callback) {
        super(context);
        this.scanCallback = scanCallback;
        this.callback = callback;
        createScanner();
    }

    /**
     * 创建扫描器
     */
    private boolean createScanner() {
        boolean scanner = false;
        if (isEnabled()) {
            if (bluetoothLeScanner == null) {
                this.bluetoothLeScanner = getBluetoothAdapter().getBluetoothLeScanner();
                scanner = bluetoothLeScanner != null;
            } else {
                scanner = true;
            }
        }
        return scanner;
    }


    /**
     * @return
     */
    public boolean isMultipleAdvertisementSupport() {
        if (isSupportBluetooth()) {
            return getBluetoothAdapter().isMultipleAdvertisementSupported();
        }
        return false;
    }

    /**
     * 开始扫描
     */
    public void startScan() {
        if (createScanner()) {
            setScanning(true);
            //开始扫描
            bluetoothLeScanner.startScan(scanFilters, buildScanSettings(), scanCallback);
            //定时停止扫描
            getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    stopScanning();
                }
            }, getScanPeriod());
        }
    }

    /**
     * 设置扫描过滤
     *
     * @param scanFilters
     */
    public void setScanFilters(List<ScanFilter> scanFilters) {
        this.scanFilters = scanFilters;
    }

    /**
     * 设置扫描模式
     *
     * @return
     */
    private ScanSettings buildScanSettings() {
        ScanSettings.Builder builder = new ScanSettings.Builder();
        builder.setScanMode(ScanSettings.SCAN_MODE_LOW_POWER);
        return builder.build();
    }

    /**
     * 停止扫描
     */
    public void stopScanning() {
        if (isSupportBluetooth() && isEnabled()) {
            setScanning(false);
            bluetoothLeScanner.stopScan(scanCallback);
        }
        Log.e("蓝牙扫描定时结束", this.callback + "");
        if (this.callback != null) this.callback.onScanFinish(true);
    }
}
