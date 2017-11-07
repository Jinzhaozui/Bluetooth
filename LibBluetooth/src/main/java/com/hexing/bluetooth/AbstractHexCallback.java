package com.hexing.bluetooth;

import android.bluetooth.BluetoothDevice;

/**
 * Created by caibinglong
 * on 2017/8/8.
 */

public class AbstractHexCallback implements HexCallback {
    @Override
    public void onVerifyConnect(byte[] readData) {
        //验证连接 通过发 校验成功调用 onConnectSuccess
    }

    @Override
    public void onConnectSuccess() {

    }

    @Override
    public void connectFail() {

    }

    @Override
    public void onError(int code) {

    }

    @Override
    public void onReadSuccess(String data) {

    }

    @Override
    public void onReadySuccess() {

    }

    @Override
    public void onWriteSuccess() {

    }

    @Override
    public void onScanFinish(boolean bool) {

    }

    @Override
    public void onScanResult(BluetoothDevice device) {

    }

    @Override
    public void onScanFail(int errorCode) {

    }

    @Override
    public void onSendResult(String result) {

    }

    @Override
    public void onReceiver(Object data) {

    }

    @Override
    public void onAnalysis(Object data) {

    }
}
