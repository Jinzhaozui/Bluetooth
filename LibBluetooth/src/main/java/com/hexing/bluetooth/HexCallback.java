package com.hexing.bluetooth;

import android.bluetooth.BluetoothDevice;

/**
 * Created by HEC271
 * on 2017/6/14.
 * 回调接口
 */

public interface HexCallback {
    void onVerifyConnect(byte[] readData);

    void onConnectSuccess();

    void connectFail();

    void onReadSuccess(String data);

    void onWriteSuccess();

    void onError(int code);

    void onReadySuccess();

    void onScanResult(BluetoothDevice device);

    void onScanFinish(boolean bool);

    void onScanFail(int errorCode);

    void onSendResult(String result);

    void onReceiver(Object data);

    void onAnalysis(Object data);
}
