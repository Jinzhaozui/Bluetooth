package com.hexing.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanResult;

import java.util.List;

/**
 * 接口
 * Created by caibinglong
 * on 2017/7/10.
 */

public interface BluetoothCallback {
    void onScanResult(BluetoothDevice device);

    void scanFail(int errorCode);

    void scanFinish();

    void onSendResult(String result);

    void connectSuccess();

    void connectFail();

    void receiver(Object data);

    void analysis(Object data);
}
