package com.example.bluetooth.le.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bluetooth.le.ConfigUtil;
import com.example.bluetooth.le.R;
import com.example.bluetooth.le.base.BaseActivity;
import com.example.bluetooth.le.widget.LoadingDialog;
import com.hexing.cardReaderBluetooth.HexCallback;
import com.hexing.cardReaderBluetooth.HexCardType;
import com.hexing.cardReaderBluetooth.HexError;
import com.hexing.cardReaderBluetooth.api.HexReaderClient;

/**
 * Created by HEC271
 * on 2017/6/14.
 * sdk  demo
 */

public class CardReaderDemoActivity extends BaseActivity {
    private TextView tv_read, tv_write, tv_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_reader_demo);
        tv_read = (TextView) findViewById(R.id.tv_read);
        tv_write = (TextView) findViewById(R.id.tv_write);
        tv_data = (TextView) findViewById(R.id.tv_data);
        String mDeviceName = getIntent().getStringExtra(ConfigUtil.EXTRAS_DEVICE_NAME);
        String mDeviceAddress = getIntent().getStringExtra(ConfigUtil.EXTRAS_DEVICE_ADDRESS);

        tv_read.setOnClickListener(this);
        tv_write.setOnClickListener(this);

        HexReaderClient readerClient = HexReaderClient.getInstance();
        readerClient.setDebugMode(true);
        readerClient.init(getApplicationContext());
        readerClient.addCallback(callback);
        if (mDeviceAddress != null && mDeviceName != null)
            readerClient.connect(mDeviceName, mDeviceAddress);
    }

    @Override
    protected void onPause() {
        Log.i(TAG, "onPause()");
        super.onPause();
        /* Disconnect Bluetooth reader */
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_read:
                LoadingDialog.showSysLoadingDialog(this, getString(R.string.loading));
                HexReaderClient.getInstance().read(HexCardType.Card_4428.PARAM, HexCardType.Card_4442.PARAM);
                break;
            case R.id.tv_write:
                LoadingDialog.showSysLoadingDialog(this, getString(R.string.loading));
                String json = "{\"verify\":{\"offset\":[416],\"value\":[\"FFFFFF\"]},\"passwd\":\"b408\",\"newpasswd\":\"4198\",\"write\":{\"offset\":[103,152,416],\"value\":[\"0524a7FF\",\"30479594809064492538\",\"26613064c7ca06a8\"]}}";
                HexReaderClient.getInstance().write(json);
                break;
        }
    }

    private HexCallback callback = new HexCallback() {
        @Override
        public void onConnectSuccess() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    LoadingDialog.cancelLoadingDialog();
                    Toast.makeText(getApplicationContext(), "成功", Toast.LENGTH_LONG).show();
                }
            });
        }

        @Override
        public void onReadySuccess() {
            //卡准备工作完成
        }

        @Override
        public void onReadSuccess(final String data) {
            Log.e("sdk返回数据-》", data);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    LoadingDialog.cancelLoadingDialog();
                    tv_data.setText(data);
                }
            });
        }

        @Override
        public void onWriteSuccess() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    LoadingDialog.cancelLoadingDialog();
                    Toast.makeText(getApplicationContext(), "写卡成功", Toast.LENGTH_LONG).show();
                }
            });
        }

        @Override
        public void onError(final int code) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    LoadingDialog.cancelLoadingDialog();
                    Toast.makeText(getApplicationContext(), "失败" + code, Toast.LENGTH_LONG).show();
                    if (HexError.BLUETOOTH_CONNECT_ERROR == code)
                        finish();
                }
            });
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        HexReaderClient.getInstance().onDestroy();
    }
}
