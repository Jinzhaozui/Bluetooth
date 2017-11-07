/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.bluetooth.le.activity;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.example.bluetooth.le.ConfigUtil;
import com.example.bluetooth.le.R;
import com.example.bluetooth.le.widget.LoadingDialog;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.hexing.bluetooth.BluetoothImpl;
import com.hexing.bluetooth.HexClientAPI;
import com.hexing.bluetooth.db.model.LocationStore;
import com.hexing.bluetooth.thread.WriteTask;
import com.utils.Checksum;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.litepal.crud.ClusterQuery;
import org.litepal.crud.DataSupport;

import cn.hexing.fdm.protocol.model.HXFramePara;

/**
 * For a given BLE device, this Activity provides the user interface to connect, display data,
 * and display GATT services and characteristics supported by the device.  The Activity
 * communicates with {@code BluetoothLeService}, which in turn interacts with the
 * Bluetooth LE API.
 */
@SuppressLint("NewApi")
public class MeterDeviceControlActivity extends Activity {
    private final static String TAG = MeterDeviceControlActivity.class.getSimpleName();

    private TextView mConnectionState;
    private TextView mDataField;
    private String mDeviceName;
    private String mDeviceAddress;
    private boolean mConnected = false;
    private Button button, button13, button14, button15;
    private EditText writeID;
    private EditText writeNS;
    private Button btn_map;
    private int selectId = 0;
    private Object ToDecUtil;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    private void clearUI() {
//        mGattServicesList.setAdapter((SimpleExpandableListAdapter) null);
        mDataField.setText(R.string.no_data);
    }

    HXFramePara FramePara = new HXFramePara();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meter_control);

        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(ConfigUtil.EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(ConfigUtil.EXTRAS_DEVICE_ADDRESS);


        ((TextView) findViewById(R.id.device_address)).setText(mDeviceAddress);
        mConnectionState = (TextView) findViewById(R.id.connection_state);
        mDataField = (TextView) findViewById(R.id.data_value);

        button = (Button) findViewById(R.id.button);
        button13 = (Button) findViewById(R.id.button13);
        button14 = (Button) findViewById(R.id.button14);
        button15 = (Button) findViewById(R.id.button15);
        btn_map = (Button) findViewById(R.id.btn_map);
        writeID = (EditText) findViewById(R.id.writeID);
        writeNS = (EditText) findViewById(R.id.writeNS);
        button.setOnClickListener(btnlisten);
        button13.setOnClickListener(btnlisten);
        button14.setOnClickListener(btnlisten);
        button15.setOnClickListener(btnlisten);
        btn_map.setOnClickListener(mapbtnlisten);

        MeterDeviceControlActivity.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        getActionBar().setIcon(R.drawable.hexpay_logo);
        getActionBar().setTitle(R.string.title_devices);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        LoadingDialog.showSysLoadingDialog(this, getString(R.string.loading));
        HexClientAPI.getInstance().init(getApplicationContext());
        HexClientAPI.getInstance().connect(mDeviceName, mDeviceAddress, callback);
        Log.e("Meter Mac address", mDeviceAddress);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    protected void onResume() {
        super.onResume();
        HexClientAPI.getInstance().addListener(callback);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        HexClientAPI.getInstance().onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gatt_services, menu);
        if (mConnected) {
            menu.findItem(R.id.menu_connect).setVisible(false);
            menu.findItem(R.id.menu_disconnect).setVisible(true);
        } else {
            menu.findItem(R.id.menu_connect).setVisible(true);
            menu.findItem(R.id.menu_disconnect).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_connect:
                HexClientAPI.getInstance().reconnect();
                return true;
            case R.id.menu_disconnect:
                HexClientAPI.getInstance().onDestroy();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private OnClickListener btnlisten = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (!mConnected) {
                Toast.makeText(getApplicationContext(), getString(R.string.please_connect_bluetooth_device), Toast.LENGTH_LONG).show();
                return;
            }
            // TODO Auto-generated method stub
            String commond = "";
            String com = "";
            selectId = 0;
            clearUI();
            switch (v.getId()) {
                case R.id.button:
                    //通信 发送通知服务
                    return;

                case R.id.button13://writeNS OCTS
                    String writeNSStr = writeNS.getText().toString();
                    Pattern pns = Pattern.compile("\\d{8}");
                    Matcher mns = pns.matcher(writeNSStr);
                    if (writeNSStr.equals("") || !mns.matches()) {
                        Toast.makeText(MeterDeviceControlActivity.this, "Invalid NS", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    selectId = 13;
                    com = "08014E53" + writeNSStr;
                    commond = "7E000808014E53" + writeNSStr + Checksum.makeChecksum(com);
                    Log.e("待发送蓝牙完整指令:", commond);
                    WriteTask writeTask = new WriteTask();
                    writeTask.execute(commond);
                    writeNS.setText(""); // 清空输入框中的内容
                    break;

                case R.id.button14://writeID OCTS
                    selectId = 14;
                    String writeIDStr = writeID.getText().toString();
                    Pattern pid = Pattern.compile("\\d{12}");
                    Matcher mid = pid.matcher(writeIDStr);
                    if (writeIDStr.equals("") || !mid.matches()) {
                        Toast.makeText(MeterDeviceControlActivity.this, "Invalid ID", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    com = "08014944" + writeIDStr;
                    String com1 = Checksum.makeChecksum(com);
                    commond = "7E000A08014944" + writeIDStr + com1;
                    Log.e("待发送蓝牙完整指令:", commond);
                    WriteTask writeTaskns = new WriteTask();
                    writeTaskns.execute(commond);
                    writeID.setText(""); // 清空输入框中的内容
                    break;

                case R.id.button15://CT OCTS
                    selectId = 15;
                    /**
                     *  commond = "7E0004080143545F";
                     Log.e("待发送蓝牙完整指令:", commond);
                     try {
                     CyclicCT();
                     } catch (Exception e) {
                     e.printStackTrace();
                     }

                     */
                    DataSupport.deleteAll(LocationStore.class);
                    break;


            }

        }
    };
    //打开百度地图
    private OnClickListener mapbtnlisten = new OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(MeterDeviceControlActivity.this, SignalTestActivity.class);
            startActivity(intent);
        }
    };

    static void CyclicCT() {
        String commond = "";
        commond = "7E0004080143545F";
        WriteTask writeTaskct = new WriteTask();
        writeTaskct.execute(commond);
    }


    public BluetoothImpl callback = new BluetoothImpl() {
        @Override
        public void connectFail() {
            super.connectFail();
            Log.e("connectFail", "connectFail");
            LoadingDialog.cancelLoadingDialog();
            mConnected = false;
            mConnectionState.setText(getString(R.string.disconnect));
        }

        @Override
        public void connectSuccess() {
            super.connectSuccess();
            Log.e("connectSuccess", "connectSuccess");
            LoadingDialog.cancelLoadingDialog();
            mConnected = true;
            mConnectionState.setText(getString(R.string.connected));
        }

        @Override
        public void receiver(Object data) {
            super.receiver(data);
            LoadingDialog.cancelLoadingDialog();
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
        switch (selectId) {
            case 15:

                if (data.toString().length() < 0) {
                    Toast.makeText(getApplicationContext(), "数据获取失败", Toast.LENGTH_LONG).show();
                } else {
                    mDataField.setText(data.toString() + "dBm");
                    CyclicCT();

                }

                break;
            case 13:
                if (data.toString().length() < 0) {
                    Toast.makeText(getApplicationContext(), "写NS编码失败", Toast.LENGTH_LONG).show();
                } else {
                    mDataField.setText(data.toString());
                }

                break;
            case 14:
                if (data.toString().length() < 0) {
                    Toast.makeText(getApplicationContext(), "写ID失败", Toast.LENGTH_LONG).show();
                } else {
                    mDataField.setText(data.toString());
                }

                break;

        }
    }


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("MeterDeviceControl Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
