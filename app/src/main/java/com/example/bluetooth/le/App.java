package com.example.bluetooth.le;

import android.app.Application;

import com.utils.LocalDisplay;

/**
 * Created by HEC271
 * on 2017/6/12.
 */

public class App extends Application {
    public final static String CARD_4428_LENGTH = "1024"; //1024位 4428类型的卡 读取卡起始位置从0 开始
    public final static String CARD_4442_LENGTH = "256"; //256位 4442类型的卡 读取卡起始位置从32 开始
    public final static String READ_START_POSITION = "32"; //读取卡 起始位置

    @Override
    public void onCreate() {
        super.onCreate();
        LocalDisplay.init(this);
    }
}
