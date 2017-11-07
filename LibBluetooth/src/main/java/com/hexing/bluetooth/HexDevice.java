package com.hexing.bluetooth;

/**
 * 在此写用途
 * <p>
 * version V1.0 <描述当前版本功能>
 * FileName:
 * author: caibinglong
 * date: 2017/8/15
 */

public class HexDevice {
    public final static String STATUS_ON = "ON"; //设备正常运行状态
    public final static String STATUS_OFF = "OFF";//设备关闭状态

    //蓝牙表
    public class METER {
        public final static String TYPE = "METER";//设备类型
        public final static String CARD_METER = "CARD_METER";
        public final static String BLUETOOTH_METER = "BLUETOOTH_METER";

        private String deviceName;
        private String address;

        public String getDeviceName() {
            return deviceName;
        }

        public void setDeviceName(String deviceName) {
            this.deviceName = deviceName;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }
    }

    //蓝牙LCU
    public class LCU {
        public final static String TYPE = "LCU";//设备类型

        //继电器
        public final class RELAY {
            public final static String ENABLED_ON = "Y";//继电器时段启用标识
            public final static String ENABLED_OFF = "N";//继电器时段停用标识
        }

        private String deviceName;
        private String address;

        public String getDeviceName() {
            return deviceName;
        }

        public void setDeviceName(String deviceName) {
            this.deviceName = deviceName;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }
    }

    public class CARD { //蓝牙读卡器
        public final static String TYPE = "CARD";//设备类型
        private String deviceName;
        private String address;

        public String getDeviceName() {
            return deviceName;
        }

        public void setDeviceName(String deviceName) {
            this.deviceName = deviceName;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }
    }
}
