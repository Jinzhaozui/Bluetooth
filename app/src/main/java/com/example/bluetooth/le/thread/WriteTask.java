package com.example.bluetooth.le.thread;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;

import com.example.bluetooth.le.BluetoothLeService;
import com.example.bluetooth.le.SampleGattAttributes;
import com.example.bluetooth.le.handler.MsgType;

import java.util.Arrays;
import java.util.UUID;

/**
 * Created by HEC271
 * on 2017/6/6.
 * 执行task
 */

public class WriteTask extends AsyncTask<String, Void, String> {
    private Handler handler;

    public WriteTask(Handler handler) {
        this.handler = handler;
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
            int len = 10;
            String temp;
            while (command.length() > len) {
                temp = command.substring(start, start + len);
                wcharacteristic.setValue(hexStringToByte(temp));
                Log.i("Debug", "Command last -->" + Arrays.toString(hexStringToByte(temp)));
                BluetoothLeService.getInstance().writeCharacteristic(wcharacteristic);//写命令到设备，
                command = command.substring(start + len);
                SystemClock.sleep(150);
            }
            wcharacteristic.setValue(hexStringToByte(command));
            BluetoothLeService.getInstance().writeCharacteristic(wcharacteristic);//写命令到设备，
        }
        return "00";
    }

    @Override
    protected void onPostExecute(String result) {
        handler.obtainMessage(MsgType.COMMAND_TASK_SUCCESS, result).sendToTarget();
    }

    private byte[] hexStringToByte(String hex) {
        int len = (hex.length() / 2);
        byte[] result = new byte[len];
        char[] achar = hex.toCharArray();
        for (int i = 0; i < len; i++) {
            int pos = i * 2;
            result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
        }
        return result;
    }

    private int toByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

}
