package com.example.bluetooth.le.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bluetooth.le.ConfigUtil;
import com.example.bluetooth.le.R;
import com.example.bluetooth.le.adapter.CommonAdapter;
import com.example.bluetooth.le.adapter.ViewHolder;
import com.example.bluetooth.le.base.BaseActivity;
import com.example.bluetooth.le.bean.LcuStateBean;
import com.example.bluetooth.le.receiver.BluetoothReceiver;
import com.example.bluetooth.le.widget.swipemenu.SwipeMenu;
import com.example.bluetooth.le.widget.swipemenu.SwipeMenuCreator;
import com.example.bluetooth.le.widget.swipemenu.SwipeMenuItem;
import com.example.bluetooth.le.widget.swipemenu.SwipeMenuListView;
import com.utils.CommandUtil;
import com.utils.LocalDisplay;

import java.util.ArrayList;
import java.util.List;

import cn.qqtheme.framework.picker.TimePicker;
import cn.qqtheme.framework.widget.WheelView;

/**
 * Created by caibinglong
 * on 2017/6/9.
 */

public class TimePeriodActivity extends BaseActivity {
    private SwipeMenuListView listView;
    private TextView tv_add;
    private CommonAdapter<LcuStateBean> commonAdapter;
    private List<LcuStateBean> data = new ArrayList<>();
    private WheelView wheelView;
    private ViewGroup viewGroup;
    private LinearLayout layout_wheel;
    private TextView tv_cancel, tv_submit;
    private int pos; //选状态
    private String hour = "00", minute = "00";
    private CommandUtil commandUtil;
    private String mDeviceName, mDeviceAddress;
    private BluetoothReceiver bluetoothReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_period);
        listView = (SwipeMenuListView) findViewById(R.id.listView);
        tv_add = (TextView) findViewById(R.id.tv_add);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        mDeviceName = bundle.getString(ConfigUtil.EXTRAS_DEVICE_NAME);
        mDeviceAddress = bundle.getString(ConfigUtil.EXTRAS_DEVICE_ADDRESS);
        wheelView = (WheelView) findViewById(R.id.wheel_view_single);
        viewGroup = (ViewGroup) findViewById(R.id.wheel_view_container);
        layout_wheel = (LinearLayout) findViewById(R.id.layout_wheel);
        tv_cancel = (TextView) findViewById(R.id.tv_cancel);
        tv_submit = (TextView) findViewById(R.id.tv_submit);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        initData();

        commonAdapter = new CommonAdapter<LcuStateBean>(getApplicationContext(), data, R.layout.item_time_period) {
            @Override
            public void convert(ViewHolder helper, LcuStateBean item, int position) {
                helper.setText(R.id.tv_hour, getString(R.string.str_hour, item.hour));
                helper.setText(R.id.tv_minute, getString(R.string.str_minute, item.minute));
                helper.setText(R.id.tv_action, item.action.equals("0") ? getString(R.string.str_lcu_connect) : getString(R.string.str_lcu_disconnect));
            }
        };

        listView.setAdapter(commonAdapter);
        listView.setMenuCreator(new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem menuItem = new SwipeMenuItem(TimePeriodActivity.this);
                menuItem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));
                // set item width
                menuItem.setWidth(LocalDisplay.dp2px(90));
                // set item title
                menuItem.setTitle(getString(R.string.delete));
                // set item title fontsize
                menuItem.setTitleSize(18);
                // set item title font color
                menuItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(menuItem);
            }
        });

        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public void onMenuItemClick(int position, SwipeMenu menu, int index) {
                data.remove(position);
                commonAdapter.notifyDataSetChanged();
            }
        });

        tv_add.setOnClickListener(this);
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout_wheel.setVisibility(View.GONE);
            }
        });
        tv_submit.setOnClickListener(this);

        bluetoothReceiver = new BluetoothReceiver(getApplicationContext(), uiHandler);
        bluetoothReceiver.register(bluetoothReceiver);
    }

    private void initData() {
        final String[] strings = {getString(R.string.str_lcu_connect), getString(R.string.str_lcu_disconnect), "不启用"};
        wheelView.setItems(strings, 2);
        wheelView.setTextSize(16);
        wheelView.setOnItemSelectListener(new WheelView.OnItemSelectListener() {
            @Override
            public void onSelected(int index) {
                pos = index;
            }
        });

        TimePicker picker = new TimePicker(this, TimePicker.HOUR_24);
        picker.setRangeStart(0, 0);
        picker.setRangeEnd(23, 59);
        picker.setOnWheelListener(new TimePicker.OnWheelListener() {
            @Override
            public void onHourWheeled(int i, String s) {
                hour = s;
            }

            @Override
            public void onMinuteWheeled(int i, String s) {
                minute = s;
            }
        });
        viewGroup.addView(picker.getContentView());
        commandUtil = new CommandUtil(uiHandler);
        commandUtil.init();
        commandUtil.setFramePara(mDeviceName, mDeviceAddress);
        commandUtil.readArray();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                commandUtil.setFramePara(mDeviceName, mDeviceAddress);
                commandUtil.writeTimingSwitch(1, data);
                finish();
                break;
        }
        return true;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_add:
                if (data.size() >= 12) {
                    Toast.makeText(getApplicationContext(), getString(R.string.str_max_period), Toast.LENGTH_LONG).show();
                    return;
                }
                layout_wheel.setVisibility(View.VISIBLE);
                break;
            case R.id.tv_submit:
                layout_wheel.setVisibility(View.GONE);
                LcuStateBean bean = new LcuStateBean();
                bean.hour = hour;
                bean.minute = minute;
                bean.action = pos + "";
                data.add(bean);
                commonAdapter.notifyDataSetChanged();

                break;
        }
    }

    @Override
    public void doHandler(Message msg) {
        switch (msg.what) {
            case BluetoothReceiver.ACTION_DATA_AVAILABLE:
                String result = msg.obj.toString();
                data = commandUtil.analysisArray(result);
                commonAdapter.setData(data);
                commonAdapter.notifyDataSetChanged();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bluetoothReceiver != null)
            bluetoothReceiver.unRegister(bluetoothReceiver);
    }
}
