package com.example.bluetooth.le.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.util.Log;

import com.example.bluetooth.le.BluetoothLeService;
import com.hexing.bluetooth.thread.WriteTask;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by HEC271
 * on 2017/6/6.
 * 蓝牙数据广播 - 低功耗
 */

public class BluetoothReceiver extends BroadcastReceiver {
    private Handler handler;
    private Context context;
    public final static int ACTION_GATT_CONNECTED = 0x123;
    public final static int ACTION_GATT_DISCONNECTED = 0x124;
    public final static int ACTION_GATT_SERVICES_DISCOVERED = 0x125;
    public final static int ACTION_DATA_AVAILABLE = 0x126;
    private StringBuilder AvaData = new StringBuilder();



    public BluetoothReceiver(Context context, Handler handler) {
        this.context = context;
        this.handler = handler;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
            //连接成功
            handler.sendEmptyMessage(ACTION_GATT_CONNECTED);
        }
        if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
            //连接失败
            handler.sendEmptyMessage(ACTION_GATT_DISCONNECTED);
        }
        if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
            //搜索到蓝牙服务 才能正常使用
            handler.sendEmptyMessage(ACTION_GATT_SERVICES_DISCOVERED);
        }
          if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
         //数据
         String data = intent.getStringExtra(BluetoothLeService.EXTRA_DATA).toUpperCase().replace(" ", "");
         AvaData.append(data);
              Log.i("返回数据", AvaData.toString());
              handler.obtainMessage(ACTION_DATA_AVAILABLE, AvaData.toString()).sendToTarget();
              AvaData = new StringBuilder();

         }



    }

    /**
     * 注册
     *
     * @param receiver receiver
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
