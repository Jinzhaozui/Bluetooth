package com.example.bluetooth.le.base;

import android.os.Bundle;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.example.bluetooth.le.handler.CommonDoHandler;
import com.example.bluetooth.le.handler.CommonHandler;


public class BaseActivity extends FragmentActivity implements View.OnClickListener, CommonDoHandler {
    protected String TAG = BaseActivity.class.getName();
    protected CommonHandler<BaseActivity> uiHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //sharedUtil.getInstance(mContext).putBoolean(sharedUtil.PREFERENCES_RESTART, true);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void doHandler(Message msg) {
        uiHandler.handleMessage(msg);
    }

    //私有方法区域
    private void init() {
        uiHandler = new CommonHandler(this);
        HandlerThread handlerThread = new HandlerThread(getClass().getName());
        handlerThread.start();
    }
}
