package com.example.bluetooth.le.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.example.bluetooth.le.ConfigUtil;
import com.example.bluetooth.le.R;
import com.example.bluetooth.le.widget.LoadingDialog;
import com.hexing.bluetooth.BluetoothImpl;
import com.hexing.bluetooth.HexClientAPI;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import cn.hexing.fdm.protocol.model.CommPara;
import cn.hexing.fdm.protocol.model.HXFramePara;
import cn.hexing.fdm.protocol.model.TranXADRAssist;
import cn.hexing.fdm.services.CommServer;
import com.example.bluetooth.le.R;
/**
 * For a given BLE device, this Activity provides the user interface to connect, display data,
 * and display GATT services and characteristics supported by the device.  The Activity
 * communicates with {@code BluetoothLeService}, which in turn interacts with the
 * Bluetooth LE API.
 */



@SuppressLint("NewApi")

public class BluetoothModelControlActivity extends AppCompatActivity {
    private final static String TAG = MeterDeviceControlActivity.class.getSimpleName();

    private TextView mConnectionState;
    private TextView mDataField;
    private String mDeviceName;
    private String mDeviceAddress;
    private boolean mConnected = false;
    private Button button, button1, button2, button3, button4, button5, button6, button7, button8, button9, button10, button11, button12;
    private EditText token;
    private Button btn_freeze;
    private int selectId = 0;

    private void clearUI() {
//        mGattServicesList.setAdapter((SimpleExpandableListAdapter) null);
        mDataField.setText(R.string.no_data);
    }

    HXFramePara FramePara = new HXFramePara();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_model_control);

        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(ConfigUtil.EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(ConfigUtil.EXTRAS_DEVICE_ADDRESS);

        // Sets up UI references.
        ((TextView) findViewById(R.id.device_address)).setText(mDeviceAddress);
//        mGattServicesList = (ExpandableListView) findViewById(R.id.gatt_services_list);
//        mGattServicesList.setOnChildClickListener(servicesListClickListner);
        mConnectionState = (TextView) findViewById(R.id.connection_state);
        mDataField = (TextView) findViewById(R.id.data_value);

        button = (Button) findViewById(R.id.button);
        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);
        button4 = (Button) findViewById(R.id.button4);
        button5 = (Button) findViewById(R.id.button5);
        button6 = (Button) findViewById(R.id.button6);
        button7 = (Button) findViewById(R.id.button7);
        button8 = (Button) findViewById(R.id.button8);
        button9 = (Button) findViewById(R.id.button9);
        button10 = (Button) findViewById(R.id.button10);
        button11 = (Button) findViewById(R.id.button11);
        button12 = (Button) findViewById(R.id.button12);
        token = (EditText) findViewById(R.id.token);
        btn_freeze = (Button) findViewById(R.id.btn_freeze);

        button.setOnClickListener(btnlisten);
        button1.setOnClickListener(btnlisten);
        button2.setOnClickListener(btnlisten);
        button3.setOnClickListener(btnlisten);
        button4.setOnClickListener(btnlisten);
        button5.setOnClickListener(btnlisten);
        button6.setOnClickListener(btnlisten);
        button7.setOnClickListener(btnlisten);
        button8.setOnClickListener(btnlisten);
        button9.setOnClickListener(btnlisten);
        button10.setOnClickListener(btnlisten);
        button11.setOnClickListener(btnlisten);
        button12.setOnClickListener(btnlisten);
        btn_freeze.setOnClickListener(btnlisten);
        BluetoothModelControlActivity.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        getActionBar().setIcon(R.drawable.chilun_logo);
        getActionBar().setTitle(R.string.title_devices);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        LoadingDialog.showSysLoadingDialog(this, getString(R.string.loading));
        HexClientAPI.getInstance().init(getApplicationContext());
        HexClientAPI.getInstance().connect(mDeviceName, mDeviceAddress, callback);
        Log.e("Meter Mac address", mDeviceAddress);
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

    android.view.View.OnClickListener btnlisten = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (!mConnected) {
                Toast.makeText(getApplicationContext(), getString(R.string.please_connect_bluetooth_device), Toast.LENGTH_LONG).show();
                return;
            }
            // TODO Auto-generated method stub
            String commond = "";
            selectId = 0;
            clearUI();
            switch (v.getId()) {
                case R.id.button:
                    //通信 发送通知服务
                    return;
                case R.id.button1: //进入正常运行模式 Octs_ascii
                    commond = "7E A0 21 00 02 FE FF 03 32 DC 33 E6 E6 00 C1 01 C1 00 01 01 00 8C 82 00 FF 02 00 0A 03 38 36 35 90 6F 7E";
                    FramePara.WriteData = "865";
                    FramePara.strDecDataType = "Octs_ascii";
                    selectId = 1;
                    break;
                case R.id.button2: //取消声音报警
                    selectId = 2;
                    HexClientAPI.getInstance().cancelMeterAlarm();
                    break;
                case R.id.button3: //紧急透支
                    selectId = 3;
                    HexClientAPI.getInstance().writeUrgentOverdraft();
                    break;
                case R.id.button4: //继电器操作原因（读）
                    selectId = 4;
                    HexClientAPI.getInstance().readMeterRelayReason();
                    break;
                case R.id.button5://查询电表余额（读）
                    selectId = 5;
                    HexClientAPI.getInstance().readMeterBalance();
                    break;
                case R.id.button6://正向有功总电能（读）
                    selectId = 6;
                    HexClientAPI.getInstance().readMeterPositiveActive();
                    break;
                case R.id.button7://月冻结电能信息（读）
                    commond = "7E A0 1C 00 02 FE FF 03 32 D9 CB E6 E6 00 C0 01 C1 00 07 00 00 62 01 00 FF 02 00 C0 0C 7E";
                    //日冻结
                    FramePara.OBISattri = "7#0.0.98.2.0.255#2";
                    FramePara.strDecDataType = "Struct_Billing";
                    List<TranXADRAssist> temp = new ArrayList<TranXADRAssist>();
                    TranXADRAssist item1 = new TranXADRAssist();
                    item1.strName = "DateTime";
                    temp.add(item1);

                    TranXADRAssist item2 = new TranXADRAssist();
                    item2.strName = "Active energy (+)";
                    item2.unit = "kWh";
                    item2.nScaler = 0;
                    temp.add(item2);

                    TranXADRAssist item3 = new TranXADRAssist();
                    item3.strName = "Active energy (-)";
                    item3.unit = "kWh";
                    item3.nScaler = 0;
                    temp.add(item3);

                    FramePara.listTranXADRAssist = temp;
                    selectId = 7;
                    break;
                case R.id.button8://日冻结电能信息（读）
                    commond = "7E A0 1C 00 02 FE FF 03 32 D9 CB E6 E6 00 C0 01 C1 00 07 00 00 62 02 00 FF 02 00 0C 11 7E";
                    //日冻结
                    FramePara.OBISattri = "7#0.0.98.2.0.255#2";
                    FramePara.strDecDataType = "Struct_Billing";
                    List<TranXADRAssist> temp1 = new ArrayList<TranXADRAssist>();
                    TranXADRAssist item11 = new TranXADRAssist();
                    item11.strName = "DateTime";
                    temp1.add(item11);

                    TranXADRAssist item12 = new TranXADRAssist();
                    item12.strName = "Active energy (+)";
                    item12.unit = "kWh";
                    item12.nScaler = 0;
                    temp1.add(item12);

                    TranXADRAssist item13 = new TranXADRAssist();
                    item13.strName = "Active energy (-)";
                    item13.unit = "kWh";
                    item13.nScaler = 0;
                    temp1.add(item13);

                    FramePara.listTranXADRAssist = temp1;
                    selectId = 8;
                    break;
                case R.id.button9://月冻结预付费信息（读）
                    commond = "7E A0 1C 00 02 FE FF 03 32 D9 CB E6 E6 00 C0 01 C1 00 07 00 00 63 01 00 FF 02 00 EB 08 7E";
                    //日冻结
                    FramePara.OBISattri = "7#0.0.98.2.0.255#2";
                    FramePara.strDecDataType = "Struct_Billing";
                    List<TranXADRAssist> temp2 = new ArrayList<TranXADRAssist>();
                    TranXADRAssist item21 = new TranXADRAssist();
                    item21.strName = "DateTime";
                    temp2.add(item21);

                    TranXADRAssist item22 = new TranXADRAssist();
                    item22.strName = "Active energy (+)";
                    item22.unit = "kWh";
                    item22.nScaler = 0;
                    temp2.add(item22);

                    TranXADRAssist item23 = new TranXADRAssist();
                    item23.strName = "Active energy (-)";
                    item23.unit = "kWh";
                    item23.nScaler = 0;
                    temp2.add(item23);

                    FramePara.listTranXADRAssist = temp2;
                    selectId = 9;
                    break;
                case R.id.button10://日冻结预付费信息（读）
                    commond = "7E A0 1C 00 02 FE FF 03 32 D9 CB E6 E6 00 C0 01 C1 00 07 00 00 63 02 00 FF 02 00 27 15 7E";
                    //日冻结
                    FramePara.OBISattri = "7#0.0.98.2.0.255#2";
                    FramePara.strDecDataType = "Struct_Billing";
                    List<TranXADRAssist> temp3 = new ArrayList<TranXADRAssist>();
                    TranXADRAssist item31 = new TranXADRAssist();
                    item31.strName = "DateTime";
                    temp3.add(item31);

                    TranXADRAssist item32 = new TranXADRAssist();
                    item32.strName = "Active energy (+)";
                    item32.unit = "kWh";
                    item32.nScaler = 0;
                    temp3.add(item32);

                    TranXADRAssist item33 = new TranXADRAssist();
                    item33.strName = "Active energy (-)";
                    item33.unit = "kWh";
                    item33.nScaler = 0;
                    temp3.add(item33);

                    FramePara.listTranXADRAssist = temp3;
                    selectId = 10;
                    break;
                case R.id.button11://月冻结特殊信息包（读）
                    commond = "7E A0 1C 00 02 FE FF 03 32 D9 CB E6 E6 00 C0 01 C1 00 07 00 00 62 09 00 FF 02 00 E0 56 7E";
                    break;
                case R.id.button12://token OCTS
                    commond = "";
                    String tokenStr = token.getText().toString().replaceAll(" ", "");
                    Pattern p = Pattern.compile("\\d{20}");
                    Matcher m = p.matcher(tokenStr);
                    if (tokenStr.equals("") || !m.matches()) {
                        Toast.makeText(BluetoothModelControlActivity.this, "Invalid Token", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    selectId = 12;
                    HexClientAPI.getInstance().writeMeterToken(tokenStr);
                    break;
                case R.id.btn_freeze:
                    startActivity(new Intent(BluetoothModelControlActivity.this, MeterFreezeActivity.class));
                    break;
            }
            LoadingDialog.showSysLoadingDialog(BluetoothModelControlActivity.this, getString(R.string.loading));
            Log.i("命令：", commond);
        }
    };

    private BluetoothImpl callback = new BluetoothImpl() {
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
        public void analysis(Object data) {
            super.analysis(data);
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
        String result;
        switch (selectId) {
            case 12:
                Toast.makeText(getApplicationContext(), data.toString(), Toast.LENGTH_LONG).show();
                break;
            case 6:
            case 5:
                result = data.toString();//CommandAPI.getInstance().displayResponseData(data);
                if (result.indexOf("7E") > 0) {
                    Toast.makeText(getApplicationContext(), "数据获取失败", Toast.LENGTH_LONG).show();
                    return;
                }
                try {
                    int numResult = Integer.parseInt(result);
                    if (numResult > 0) {
                        numResult = numResult / 100;
                    }
                    result = numResult + "." + result.substring(result.length() - 2, result.length());
                    mDataField.setText(result + "KWh");
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
                break;
            case 1:
                mDataField.setText(data.toString());
                break;
            case 3:
            case 2:
                mDataField.setText(data.toString());
                break;
            case 4:
                String str = data.toString();
                switch (data.toString()) {
                    case "00":
                        str = "Normal";
                        break;
                    case "01":
                        str = "Out of credit but not use overdraft";
                        break;
                    case "02":
                        str = "Out of credit after overdraft";
                        break;
                    case "03":
                        str = "Over load";
                        break;
                    case "04":
                        str = "Disconnection test";
                        break;
                    case "05":
                        str = "Main cover removed";
                        break;
                    case "06":
                        str = "Terminal cover removed";
                        break;
                    case "07":
                        str = "Remote disconnection";
                        break;
                    case "08":
                        str = "Over current";
                        break;
                    case "09":
                        str = "Over voltage";
                        break;
                    case "10":
                        str = "Reconnection test";
                        break;
                    case "11":
                        str = "cover removed";
                        break;
                    case "12":
                        str = "Firmware update";
                        break;
                    case "13":
                        str = "Low frequency";
                        break;
                    case "0A":
                        str = "No charge for long period";
                        break;
                    case "0B":
                        str = "Default operation after factory";
                        break;
                    case "0C":
                        str = "Under voltage";
                        break;
                    case "0D":
                        str = "Current reversal";
                        break;
                    case "0E":
                        str = "Missing neutral";
                        break;
                    case "0F":
                        str = "Data Error";
                        break;
                    case "A1":
                        str = "Low voltage of battery";
                        break;
                    case "20":
                        str = "Remote reconnection";
                        break;
                    case "30":
                        str = "Remote control to never 'be disconnected' mode";
                        break;
                    case "91":
                        str = "CIU 通过电表后级窃电拉闸";
                        break;
                    case "92":
                        str = "电表故障";
                        break;
                    case "93":
                        str = "计量芯片出错";
                        break;
                    case "94":
                        str = "Bypass disconnection";
                        break;
                    case "95":
                        str = "Power unbalance";
                        break;
                    case "96":
                        str = "Battery cover removed";
                        break;
                }
                mDataField.setText(str);
                break;
        }
    }
}
