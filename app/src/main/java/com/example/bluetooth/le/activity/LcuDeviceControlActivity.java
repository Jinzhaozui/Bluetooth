package com.example.bluetooth.le.activity;

import android.bluetooth.BluetoothGattService;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bluetooth.le.BluetoothLeService;
import com.example.bluetooth.le.ConfigUtil;
import com.example.bluetooth.le.SampleGattAttributes;
import com.example.bluetooth.le.base.BaseActivity;
import com.example.bluetooth.le.bean.LcuStateBean;
import com.example.bluetooth.le.handler.MsgType;
import com.example.bluetooth.le.receiver.BluetoothReceiver;
import com.example.bluetooth.le.widget.LoadingDialog;
import com.utils.CommandUtil;
import com.example.bluetooth.le.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.qqtheme.framework.picker.TimePicker;
import cn.qqtheme.framework.widget.WheelView;

import static com.example.bluetooth.le.R.id.btn_map;


/**
 * Created by caibinglong
 * on 2017/6/5.
 * lcu 界面
 */

public class LcuDeviceControlActivity extends BaseActivity implements View.OnClickListener {
    private Button btn_read_meter;
    private Button btn_read_time, btn_write_time;
    private Button btn_read_period;
    private Button btn_read_state;
    private Button btn_read_command;
    private Button btn_disconnect;
    private Button btn_connect;
    private Button btn_write_period;
    private Button btn_write_meter;
    private Button btn_map;
    private TextView connection_state;
    private TextView tv_data, data_source_value;
    private WheelView wheelView;
    private EditText et_meter_no;
    private String mDeviceName;
    private String mDeviceAddress;
    private boolean mConnected = false;
    private BluetoothLeService mBluetoothLeService;

    private int selectId;

    private BluetoothReceiver bluetoothReceiver;
    private CommandUtil commandUtil;
    private String dataType;
    List<LcuStateBean> dataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lcu_control);

        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(ConfigUtil.EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(ConfigUtil.EXTRAS_DEVICE_ADDRESS);

        ((TextView) findViewById(R.id.device_address)).setText(mDeviceAddress);

        btn_read_meter = (Button) findViewById(R.id.btn_read_meter);
        btn_read_time = (Button) findViewById(R.id.btn_read_time);
        btn_write_time = (Button) findViewById(R.id.btn_write_time);
        btn_read_period = (Button) findViewById(R.id.btn_read_period);
        btn_read_state = (Button) findViewById(R.id.btn_read_state);
        btn_read_command = (Button) findViewById(R.id.btn_read_command);
        btn_disconnect = (Button) findViewById(R.id.btn_disconnect);
        btn_connect = (Button) findViewById(R.id.btn_connect);
        btn_write_period = (Button) findViewById(R.id.btn_write_period);
        btn_write_meter = (Button) findViewById(R.id.btn_write_meter);
        btn_map = (Button) findViewById(R.id.btn_map);
        connection_state = (TextView) findViewById(R.id.connection_state);
        tv_data = (TextView) findViewById(R.id.data_value);
        data_source_value = (TextView) findViewById(R.id.data_source_value);
        wheelView = (WheelView) findViewById(R.id.wheel_view_single);
        et_meter_no = (EditText) findViewById(R.id.et_meter_no);
        btn_read_meter.setOnClickListener(this);
        btn_read_time.setOnClickListener(this);
        btn_write_time.setOnClickListener(this);
        btn_read_period.setOnClickListener(this);
        btn_read_state.setOnClickListener(this);
        btn_write_meter.setOnClickListener(this);
        btn_read_command.setOnClickListener(this);
        btn_disconnect.setOnClickListener(this);
        btn_connect.setOnClickListener(this);
        btn_write_period.setOnClickListener(this);
        btn_map.setOnClickListener(this);
        bluetoothReceiver = new BluetoothReceiver(getApplicationContext(), uiHandler);

        getActionBar().setIcon(R.drawable.chilun_logo);
        getActionBar().setTitle(R.string.lcu_title_devices);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        commandUtil = new CommandUtil(uiHandler);
        commandUtil.init();

//        final String[] strings = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24"};
//        wheelView.setItems(strings, 0);
//        wheelView.setTextSize(16);
//        wheelView.setOnItemSelectListener(new WheelView.OnItemSelectListener() {
//            @Override
//            public void onSelected(int index) {
//
//            }
//        });
    }


    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e("TAG", "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d("TAG", "Connect request result=" + result);
        }
        bluetoothReceiver.register(bluetoothReceiver);
    }

    @Override
    protected void onPause() {
        super.onPause();
        bluetoothReceiver.unRegister(bluetoothReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }

    @Override
    public void onClick(View v) {
//        if (!mConnected) {
//            Toast.makeText(getApplicationContext(), getString(R.string.please_connect_bluetooth_device), Toast.LENGTH_LONG).show();
//            return;
//        }

        String command = "";
        selectId = 0;
        clearUI();
        switch (v.getId()) {
            case R.id.btn_read_meter:
                LoadingDialog.showSysLoadingDialog(LcuDeviceControlActivity.this, getString(R.string.loading));
                selectId = 1;
                commandUtil.setFramePara(mDeviceName, mDeviceAddress);
                commandUtil.readMeterNumber();
                break;
            case R.id.btn_read_time:
                LoadingDialog.showSysLoadingDialog(LcuDeviceControlActivity.this, getString(R.string.loading));
                selectId = 2;
                commandUtil.setFramePara(mDeviceName, mDeviceAddress);
                commandUtil.readTime();
                break;
            case R.id.btn_write_time:
                selectId = 9;
                onTimePicker(new CallBack() {
                    @Override
                    public void complete(String hour, String minute) {
                        commandUtil.setFramePara(mDeviceName, mDeviceAddress);
                        // commandUtil.writeTime(Integer.parseInt(hour), Integer.parseInt(minute));
                        commandUtil.writeTime();
                    }
                });
                break;
            case R.id.btn_read_period:
                LoadingDialog.showSysLoadingDialog(LcuDeviceControlActivity.this, getString(R.string.loading));
                selectId = 3;
                commandUtil.setFramePara(mDeviceName, mDeviceAddress);
                commandUtil.readArray();
                break;
            case R.id.btn_read_state:
                LoadingDialog.showSysLoadingDialog(LcuDeviceControlActivity.this, getString(R.string.loading));
//                command = "7EA01C0002FEFF0354E9CDE6E600C001C10046008060030AFF020033E37E";
                selectId = 4;
                commandUtil.setFramePara(mDeviceName, mDeviceAddress);
                commandUtil.readLcuState(1);
                break;
            case R.id.btn_read_command:
                LoadingDialog.showSysLoadingDialog(LcuDeviceControlActivity.this, getString(R.string.loading));
                selectId = 5;
                commandUtil.setFramePara(mDeviceName, mDeviceAddress);
                commandUtil.readCommandHistory(1);
                break;
            case R.id.btn_disconnect:
                LoadingDialog.showSysLoadingDialog(LcuDeviceControlActivity.this, getString(R.string.loading));
                selectId = 6;
                commandUtil.setFramePara(mDeviceName, mDeviceAddress);
                commandUtil.actionCommand(1, false);
                break;
            case R.id.btn_connect:
                LoadingDialog.showSysLoadingDialog(LcuDeviceControlActivity.this, getString(R.string.loading));
                selectId = 7;
                commandUtil.setFramePara(mDeviceName, mDeviceAddress);
                commandUtil.actionCommand(1, true);
                break;
            case R.id.btn_write_period: //定时断电
                selectId = 8;
                dataType = "Array_dd";
                Intent intent = new Intent(LcuDeviceControlActivity.this, TimePeriodActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(ConfigUtil.EXTRAS_DEVICE_NAME, mDeviceName);
                bundle.putString(ConfigUtil.EXTRAS_DEVICE_ADDRESS, mDeviceAddress);
                //bundle.putSerializable("data", (Serializable) dataList);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.btn_write_meter:
                if (et_meter_no.getText().toString().length() == 11) {
                    et_meter_no.setText(""); // 清空输入框中的内容
                    LoadingDialog.showSysLoadingDialog(LcuDeviceControlActivity.this, getString(R.string.loading));
                    selectId = 9;
                    dataType = "Ascs";
                    commandUtil.setFramePara(mDeviceName, mDeviceAddress);
                    commandUtil.writeMeterNumber(et_meter_no.getText().toString());
                } else {
                    Toast.makeText(getApplicationContext(), "目前只支持输入11位表号", Toast.LENGTH_LONG).show();
                }
                break;

            case R.id.btn_map:
                selectId = 10;
                Intent intent1 = new Intent(LcuDeviceControlActivity.this, SignalTestActivity.class);
         startActivity(intent1);
         break;

         }
         /**
         * btn_map.setOnClickListener(new View.OnClickListener(){
        @Override
        public void onClick(View v){
        Intent intent = new Intent(LcuDeviceControlActivity.this, SignalTestActivity.class);
        startActivity(intent);
        }
        });
         */
        Log.i("LCU命令：", command);
        //new WriteTask().execute(command);
    }

    /**
     * 蓝牙数据返回
     */
    private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                connection_state.setText(resourceId);
            }
        });
    }

    private void clearUI() {
        tv_data.setText(R.string.no_data);
    }

    // Demonstrates how to iterate through the supported GATT Services/Characteristics.
    // In this sample, we populate the data structure that is bound to the ExpandableListView
    // on the UI.
    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
        boolean write_uuid = false;
        boolean notify_uuid = false;
        String services = "";
        for (BluetoothGattService gattService : gattServices) {
            String temp = gattService.getUuid().toString();
            if (temp.equalsIgnoreCase(SampleGattAttributes.METER_WRITE_SERVICE)) {
                write_uuid = true;
            }
            if (temp.equalsIgnoreCase(SampleGattAttributes.METER_NOTIFY_SERVICE)) {
                notify_uuid = true;
            }
            services = services + "\n" + temp;
        }
        if (write_uuid && notify_uuid) {
            Toast.makeText(getApplicationContext(), "Connect Success", Toast.LENGTH_SHORT).show();
            updateConnectionState(R.string.connected);
            //设置按钮可用
            //...
        } else {
            Toast.makeText(getApplicationContext(), "invalid device", Toast.LENGTH_SHORT).show();
            //设置按钮非可用
            //...
        }
    }

    @Override
    public void doHandler(Message msg) {
        switch (msg.what) {
            case BluetoothReceiver.ACTION_GATT_CONNECTED:
                Log.i("Handler通知", "ACTION_GATT_CONNECTED");
                mConnected = true;
                updateConnectionState(R.string.ready);
                invalidateOptionsMenu();
                break;
            case BluetoothReceiver.ACTION_GATT_DISCONNECTED:
                Log.i("Handler通知", "ACTION_GATT_DISCONNECTED");
                LoadingDialog.cancelLoadingDialog();
                mConnected = false;
                updateConnectionState(R.string.disconnected);
                invalidateOptionsMenu();
                clearUI();
                break;
            case BluetoothReceiver.ACTION_GATT_SERVICES_DISCOVERED:
                Log.i("Handler通知", "ACTION_GATT_SERVICES_DISCOVERED");
                displayGattServices(mBluetoothLeService.getSupportedGattServices());
                break;
            case BluetoothReceiver.ACTION_DATA_AVAILABLE:
                LoadingDialog.cancelLoadingDialog();
                Log.i("Handler通知", "ACTION_DATA_AVAILABLE");
                String data = msg.obj.toString();
                Log.i("返回数据解析前", data);
                data_source_value.setText(data);

                if (selectId == 3) {
                    StringBuilder str = new StringBuilder();
                    dataList = commandUtil.analysisArray(data);
                    for (LcuStateBean item : dataList) {
                        str.append(getString(R.string.str_hour, item.hour));
                        str.append(getString(R.string.str_minute, item.minute));
                        str.append(item.action.equals("0") ? getString(R.string.str_lcu_disconnect) : getString(R.string.str_lcu_connect));
                        str.append("\n");
                    }
                    tv_data.setText(str.toString());
                } else if (selectId == 6 || selectId == 7) {
                    tv_data.setText(getString(R.string.command_success));
                } else {
                    data = commandUtil.displayResponseData(data);
                    if (data != null && selectId == 4) {
                        if (data.equals("00")) {
                            tv_data.setText(getString(R.string.disconnect_state));
                        } else {
                            tv_data.setText(getString(R.string.connect_state));
                        }
                    } else {
                        if (selectId == 2) {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String dateTime = data;
                            Calendar nowDate = Calendar.getInstance();
                            nowDate.setTime(new Date());
                            String nowTime = sdf.format(nowDate.getTime());

                            try {
                                Date d1 = sdf.parse(dateTime);
                                Date d2 = sdf.parse(nowTime);
                                long longTime = d1.getTime() - d2.getTime();
                                if (longTime >= 5 * 60 * 1000) {
                                    commandUtil.setFramePara(mDeviceName, mDeviceAddress);
                                    commandUtil.writeTime();
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }


                        }
                        tv_data.setText(data);
                    }

                }
                Log.i("返回数据解析后", data);
                break;
            case MsgType.COMMAND_TASK_SUCCESS:
                String result = msg.obj.toString();
                if (result.equals("00")) {

                } else if (result.equals("01")) {
                    Toast.makeText(getApplicationContext(), "Invalid Token", Toast.LENGTH_SHORT).show();
                } else if (result.equals("02")) {
                    Toast.makeText(getApplicationContext(), "Get BLE services failed", Toast.LENGTH_SHORT).show();
                } else if (result.equals("03")) {
                    Toast.makeText(getApplicationContext(), "send data failed", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    /**
     * 时间选择
     */
    private void onTimePicker(final CallBack callBack) {
        TimePicker picker = new TimePicker(this, TimePicker.HOUR_24);
        picker.setRangeStart(0, 0);
        picker.setRangeEnd(23, 59);
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int minute = Calendar.getInstance().get(Calendar.MINUTE);
        picker.setSelectedItem(hour, minute);
        picker.setOnTimePickListener(new TimePicker.OnTimePickListener() {
            @Override
            public void onTimePicked(String s, String s1) {
                Log.e("Time->", "Hour-" + s + "||Min-" + s1);
                if (callBack != null) {
                    callBack.complete(s, s1);
                }
            }
        });
        picker.show();
    }

    interface CallBack {
        void complete(String hour, String minute);
    }
}
