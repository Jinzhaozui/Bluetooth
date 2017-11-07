package com.hexing.bluetooth.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.hexing.bluetooth.BluetoothImpl;
import com.hexing.bluetooth.HexClientAPI;
import com.hexing.bluetooth.thread.WriteTask;

public class CyclicCTService extends Service {
    public CyclicCTService() {
        CyclicCT();

    }
        public BluetoothImpl callback = new BluetoothImpl() {

            @Override
            public void receiver(Object data) {
                super.receiver(data);
                //LoadingDialog.cancelLoadingDialog();
                callbackResult(data);
                Log.e("data", "data");
            }

        };


        /**
         * 解析 返回数据
         *
         * @param data 原数据
         */

    private void callbackResult(Object data) {
        //String result;

        if (data.toString().length() < 0) {
            Toast.makeText(getApplicationContext(), "数据获取失败", Toast.LENGTH_LONG).show();
        } else {
            try {
                //String strResult = data.toString().substring(16, 18); //从第16位开始取第17,18位
                CyclicCT();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

    private void CyclicCT() {
        String commond = "";
        commond = "7E0004080143545F";
        WriteTask writeTaskct = new WriteTask();
        writeTaskct.execute(commond);
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("CyclicCTService", "onCreate executed");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("CyclicCTService", "onStartCommand executed");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("CyclicCTService", "onDestroy executed");
    }


}
