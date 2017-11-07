package com.hexing.bluetooth.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SignalStrength;
import android.util.Log;

import com.hexing.bluetooth.BluetoothImpl;
import com.hexing.bluetooth.CommandAPI;
import com.hexing.bluetooth.HexCallback;
import com.hexing.bluetooth.services.BluetoothLeService;
import com.hexing.bluetooth.util.HexToDec;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by HEC271
 * on 2017/6/6.
 * 蓝牙数据广播 - 低功耗
 */

public class BluetoothReceiver extends BroadcastReceiver {
    private Context context;
    private HexCallback callBack;
    private StringBuilder AvaData = new StringBuilder();

    public BluetoothReceiver(Context context, HexCallback callBack) {
        this.context = context;
        this.callBack = callBack;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.e("onReceive", action);
        if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
            //连接成功
            if (this.callBack != null)
                this.callBack.onConnectSuccess();
        }
        if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
            //连接失败
            if (this.callBack != null)
                this.callBack.connectFail();
        }
        if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
            //搜索到蓝牙服务 才能正常使用
            if (this.callBack != null)
                this.callBack.connectFail();
        }
        if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
            //数据
            String data = intent.getStringExtra(BluetoothLeService.EXTRA_DATA).toUpperCase().replace(" ", "");
            if (data.length() < 18){
                String strResulT= "4F4B";
              if (data == strResulT){
                  AvaData.append(data);
                  String ans = "Stop_Success";
                  Log.i("返回数据", AvaData.toString());
                  this.callBack.onReceiver(ans);
                  AvaData = new StringBuilder();
              }
            }else if (data.length() == 18) {
                String strResult = data.toString().substring(14, 16);
                if (strResult.equals("00")) {
                    AvaData.append(data);
                    String ans = "Success";
                    Log.i("返回数据", AvaData.toString());
                    this.callBack.onReceiver(ans);
                    AvaData = new StringBuilder();
                } else if (strResult.equals("01")) {
                    AvaData.append(data);
                    String ans = "Faild";
                    Log.i("返回数据", AvaData.toString());
                    this.callBack.onReceiver(ans);
                    AvaData = new StringBuilder();
                }
            } else if (data.length() > 18) {
                try {
                    String strResult = data.toString().substring(16, 18); //从第16位开始取第17,18位
                    int numResult = HexToDec.ToDecUtil(strResult);
                    int numResult1 = 0 - numResult;
                    String strResult1 = numResult1 + "";//强制类型转换int转string
                    AvaData.append(data);
                    Log.i("返回数据", AvaData.toString());
                    this.callBack.onReceiver(strResult1.toString());
                    AvaData = new StringBuilder();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 注册
     *
     * @param receiver
     */
    public void register(BluetoothReceiver receiver) {
        if (context != null) {
            context.registerReceiver(receiver, makeGattUpdateIntentFilter());
        }
    }

    public void unRegister(BluetoothReceiver receiver) {
        if (context != null) {
            context.unregisterReceiver(receiver);
        }
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }
}
