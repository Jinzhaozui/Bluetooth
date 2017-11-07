package com.example.bluetooth.le.activity;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bluetooth.le.R;
import com.example.bluetooth.le.base.BaseActivity;
import com.example.bluetooth.le.widget.LoadingDialog;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.hexing.bluetooth.BluetoothImpl;
import com.hexing.bluetooth.HexClientAPI;
import com.hexing.bluetooth.bean.MeterFreezeBean;
import com.hexing.bluetooth.bean.MeterPrepaidBean;
import com.utils.DayAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;

import cn.qqtheme.framework.picker.DatePicker;

import cn.qqtheme.framework.util.ConvertUtils;

/**
 * Created by caibinglong
 * on 2017/7/13.
 */

public class MeterFreezeActivity extends BaseActivity {
    private LinearLayout layout_start_date, layout_end_date;
    private TextView tv_start_date, tv_end_date;
    private TextView tv_power, tv_prepaid;
    private TextView tv_day, tv_month;
    private boolean isMonth = false, isPower = true;
    private TextView tv_get_data;
    private TextView tv_data;
    private BarChart chart_bar;
    private MultiAutoCompleteTextView tv_source;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meter_freeze);
        layout_start_date = (LinearLayout) findViewById(R.id.layout_start_date);
        layout_end_date = (LinearLayout) findViewById(R.id.layout_end_date);
        tv_start_date = (TextView) findViewById(R.id.tv_start_date);
        tv_end_date = (TextView) findViewById(R.id.tv_end_date);
        tv_day = (TextView) findViewById(R.id.tv_day);
        tv_data = (TextView) findViewById(R.id.tv_data);
        tv_get_data = (TextView) findViewById(R.id.tv_get_data);
        tv_power = (TextView) findViewById(R.id.tv_power);
        tv_prepaid = (TextView) findViewById(R.id.tv_prepaid);
        tv_month = (TextView) findViewById(R.id.tv_month);
        tv_source = (MultiAutoCompleteTextView) findViewById(R.id.tv_source);

        layout_end_date.setOnClickListener(this);
        layout_start_date.setOnClickListener(this);
        tv_day.setOnClickListener(this);
        tv_month.setOnClickListener(this);
        tv_get_data.setOnClickListener(this);
        tv_power.setOnClickListener(this);
        tv_prepaid.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.layout_start_date:
                onPicker(tv_start_date);
                break;
            case R.id.layout_end_date:
                onPicker(tv_end_date);
                break;
            case R.id.tv_month:
                isMonth = true;
                tv_day.setBackground(null);
                tv_month.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.main_color));
                break;
            case R.id.tv_day:
                isMonth = false;
                tv_month.setBackground(null);
                tv_day.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.main_color));
                break;
            case R.id.tv_power:
                isPower = true;
                tv_prepaid.setBackground(null);
                tv_power.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.main_color));
                break;
            case R.id.tv_prepaid:
                isPower = false;
                tv_power.setBackground(null);
                tv_prepaid.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.main_color));
                break;
            case R.id.tv_get_data:
                tv_data.setText("");
                tv_source.setText("");
                if (tv_start_date.getText().toString().length() == 0 || tv_end_date.getText().toString().length() == 0) {
                    Toast.makeText(getApplicationContext(), "请选择日期", Toast.LENGTH_LONG).show();
                    return;
                }
                LoadingDialog.showSysLoadingDialog(MeterFreezeActivity.this, getString(R.string.loading));
                HexClientAPI.getInstance().addListener(callback);
                if (isMonth) {
                    if (isPower) {
                        HexClientAPI.getInstance().readMonthFreezePower(tv_start_date.getText().toString(), tv_end_date.getText().toString());
                    } else {
                        HexClientAPI.getInstance().readMonthFreezePrepaid(tv_start_date.getText().toString(), tv_end_date.getText().toString());
                    }
                } else {
                    if (isPower) {
                        HexClientAPI.getInstance().readDayFreezePower(tv_start_date.getText().toString(), tv_end_date.getText().toString());
                    } else {
                        HexClientAPI.getInstance().readDayFreezePrepaid(tv_start_date.getText().toString(), tv_end_date.getText().toString());
                    }
                }
                break;
        }
    }

    public void onPicker(final TextView textView) {
        if (isMonth) {
            DatePicker picker = new DatePicker(this, DatePicker.YEAR_MONTH);
            picker.setRangeStart(2016, 10, 14);
            picker.setRangeEnd(2020, 11, 11);
            picker.setSelectedItem(2017, 7);
            picker.setOnDatePickListener(new DatePicker.OnYearMonthPickListener() {
                @Override
                public void onDatePicked(String year, String month) {
                    textView.setText(year + "-" + month);
                }
            });
            picker.show();
        } else {
            onYearMonthDayPicker(textView);
        }
    }

    public void onYearMonthDayPicker(final TextView textView) {
        final DatePicker picker = new DatePicker(this);

        picker.setCanceledOnTouchOutside(true);
        picker.setTopPadding(ConvertUtils.toPx(this, 20));
        picker.setRangeStart(2016, 8, 29);
        picker.setRangeEnd(2111, 1, 11);
        picker.setSelectedItem(2017, 7, 14);
        picker.setOnDatePickListener(new DatePicker.OnYearMonthDayPickListener() {
            @Override
            public void onDatePicked(String year, String month, String day) {
                textView.setText(year + "-" + month + "-" + day);
            }
        });
        picker.setOnWheelListener(new DatePicker.OnWheelListener() {
            @Override
            public void onYearWheeled(int index, String year) {
                picker.setTitleText(year + "-" + picker.getSelectedMonth() + "-" + picker.getSelectedDay());
            }

            @Override
            public void onMonthWheeled(int index, String month) {
                picker.setTitleText(picker.getSelectedYear() + "-" + month + "-" + picker.getSelectedDay());
            }

            @Override
            public void onDayWheeled(int index, String day) {
                picker.setTitleText(picker.getSelectedYear() + "-" + picker.getSelectedMonth() + "-" + day);
            }
        });
        picker.show();
    }

    private BluetoothImpl callback = new BluetoothImpl() {
        @Override
        public void connectFail() {
            super.connectFail();
            Log.e("connectFail", "connectFail");
            LoadingDialog.cancelLoadingDialog();
            Toast.makeText(getApplicationContext(), "蓝牙设备已断开", Toast.LENGTH_LONG).show();
        }

        @Override
        public void connectSuccess() {
            super.connectSuccess();
            Log.e("connectSuccess", "connectSuccess");
        }

        @Override
        public void analysis(Object data) {
            super.analysis(data);
            LoadingDialog.cancelLoadingDialog();
            try {
                if (isPower) {
                    List<MeterFreezeBean> dataList = (List<MeterFreezeBean>) data;
                    if (dataList != null && dataList.size() > 0) {
                        StringBuilder str = new StringBuilder();
                        for (MeterFreezeBean item : dataList) {
                            str.append(item.year + "-" + item.month + "-" + item.day);
                            int numResult = (int) item.positive;
                            String result = String.valueOf(item.positive);
                            if (numResult > 0) {
                                numResult = numResult / 100;
                                result = result.length() == 1 ? "0" + result : result;
                                result = numResult + "." + result.substring(result.length() - 2, result.length());
                            }

                            int numResult2 = (int) item.reverse;
                            String result2 = String.valueOf(item.reverse);
                            if (numResult2 > 0) {
                                numResult2 = numResult2 / 100;
                                result2 = result2.length() == 1 ? "0" + result2 : result2;
                                result2 = numResult2 + "." + result2.substring(result2.length() - 2, result2.length());
                            }
                            str.append(" 正向有功" + result + " 反向有功" + result2);
                            str.append("\n");
                        }
                        tv_data.setText(str.toString());
                    } else {
                        tv_data.setText("无记录");
                    }
                } else {
                    List<MeterPrepaidBean> dataList = (List<MeterPrepaidBean>) data;
                    if (dataList != null && dataList.size() > 0) {
                        StringBuilder str = new StringBuilder();
                        for (MeterPrepaidBean item : dataList) {
                            str.append(item.year + "-" + item.month + "-" + item.day);
                            int numResult2 = Integer.parseInt(item.balance);
                            String result2 = item.balance;
                            if (numResult2 > 0) {
                                numResult2 = numResult2 / 100;
                                result2 = result2.length() == 1 ? "0" + result2 : result2;
                                result2 = numResult2 + "." + result2.substring(result2.length() - 2, result2.length());
                            }
                            str.append(" 电表余额" + result2);
                            str.append("\n");
                        }
                        tv_data.setText(str.toString());
                    } else {
                        tv_data.setText("无记录");
                    }
                }
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "解析失败" + data.toString(), Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void receiver(Object data) {
            super.receiver(data);
            tv_source.append("\n" + data.toString());
        }
    };

}
