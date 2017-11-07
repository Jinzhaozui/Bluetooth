package com.hexing.bluetooth;

import java.util.UUID;

/**
 * Created by long
 * on 2017/7/14.
 */

public final class BluetoothConstant {
    public static UUID UUID_SERVICE = UUID
            .fromString("0000fff0-0000-1000-8000-00805f9b34fb"); // 服务UUID
    public static UUID UUID_CHARACTERISTIC = UUID
            .fromString("0000fff4-0000-1000-8000-00805f9b34fb"); // 蓝牙发数据特征UUID
    public static UUID UUID_RECEIVER_CHAR = UUID
            .fromString("0000fff1-0000-1000-8000-00805f9b34fb"); // 蓝牙收数据特征UUID
    public static UUID UUID_DESCRIPTOR = UUID.
            fromString("00002902-0000-1000-8000-00805f9b34fb");
    public static UUID UUID_HEART_RATE_MEASUREMENT = UUID
            .fromString("00002a37-0000-1000-8000-00805f9b34fb");
}
