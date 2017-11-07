package com.hexing.cardReaderBluetooth;

/**
 * Created by HEC271
 * on 2017/6/14.
 * 回调接口
 */

public interface HexCallback {
    void onConnectSuccess();

    void onReadSuccess(String data);

    void onWriteSuccess();

    void onError(int code);

    void onReadySuccess();
}
