package com.hexing.bluetooth;

import android.bluetooth.BluetoothDevice;

/**
 * Created by caibinglong
 * on 2017/7/10.
 */

public abstract class BluetoothImpl implements BluetoothCallback{
    @Override
    public void connectFail() {

    }

    @Override
    public void onSendResult(String result) {

    }

    @Override
    public void scanFinish() {

    }

    @Override
    public void connectSuccess() {

    }

    @Override
    public void analysis(Object data) {

    }

    @Override
    public void onScanResult(BluetoothDevice device) {

    }

    @Override
    public void receiver(Object data) {

    }

    @Override
    public void scanFail(int errorCode) {

    }
}
