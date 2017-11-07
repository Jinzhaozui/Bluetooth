package com.hexing.bluetooth.thread;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;

import com.hexing.bluetooth.BluetoothImpl;
import com.hexing.bluetooth.HexCallback;
import com.hexing.bluetooth.services.BluetoothLeService;
import com.hexing.bluetooth.services.SampleGattAttributes;
import com.hexing.bluetooth.util.CRCUtil;

import java.util.Arrays;
import java.util.UUID;

/**
 * Created by HEC271
 * on 2017/6/6.
 * 执行task
 */

public class WriteTask extends AsyncTask<String, Void, String> {

    private HexCallback callback;

    public WriteTask() {
        this.callback = callback;
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected String doInBackground(String... params) {
        String command = params[0];
        //mBluetoothLeService = BluetoothLeService.getInstance();
        BluetoothGattService nservice = BluetoothLeService.getInstance().getService(UUID.fromString(SampleGattAttributes.METER_NOTIFY_SERVICE));
        if (nservice == null) {
            // Toast.makeText(getApplicationContext(), "获取服务失败", Toast.LENGTH_SHORT).show();
            return "02";
        }
        BluetoothGattCharacteristic ncharacteristic = BluetoothLeService.getInstance().getCharacteristic(nservice, UUID.fromString(SampleGattAttributes.METER_NOTIFY_CHARACTERISTIC));
        if (ncharacteristic == null) return "02";
        BluetoothLeService.getInstance().setCharacteristicNotification(ncharacteristic, true);
        SystemClock.sleep(200);
        //通信 接受命令服务
        BluetoothGattService wservice = BluetoothLeService.getInstance().getService(UUID.fromString(SampleGattAttributes.METER_WRITE_SERVICE));
        if (wservice == null) {
            //Toast.makeText(getApplicationContext(), "获取服务失败", Toast.LENGTH_SHORT).show();
            return "02";
        }
        BluetoothGattCharacteristic wcharacteristic = BluetoothLeService.getInstance().getCharacteristic(wservice, UUID.fromString(SampleGattAttributes.METER_WRITE_CHARACTERISTIC));

        if (wcharacteristic == null) {
            //Toast.makeText(getApplicationContext(), "获取服务失败", Toast.LENGTH_SHORT).show();
            return "02";
        }
        command = command.replaceAll(" ", "");
        if (command.equals("")) {
            //Toast.makeText(getApplicationContext(), "命令发送失败", Toast.LENGTH_SHORT).show();
            return "03";
        } else {
            int start = 0;
            int len = 40;
            String temp;
            while (command.length() > len) {
                temp = command.substring(start, start + len);
                wcharacteristic.setValue(CRCUtil.hexStringToByte(temp));
                Log.i("Debug", "Command last -->" + Arrays.toString(CRCUtil.hexStringToByte(temp)));
                BluetoothLeService.getInstance().writeCharacteristic(wcharacteristic);//写命令到设备，
                command = command.substring(start + len);
                SystemClock.sleep(150);
            }
            wcharacteristic.setValue(CRCUtil.hexStringToByte(command));
            BluetoothLeService.getInstance().writeCharacteristic(wcharacteristic);//写命令到设备，
        }
        return "00";
    }

    @Override
    protected void onPostExecute(String result) {
        if (this.callback != null) callback.onSendResult(result);
    }

}
