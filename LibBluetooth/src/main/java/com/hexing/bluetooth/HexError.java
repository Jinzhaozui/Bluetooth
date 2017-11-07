package com.hexing.bluetooth;

/**
 * Created by HEC271
 * on 2017/6/14.
 * 错误码
 */

public class HexError {
    public final static int SERVICE_ERROR = 10001;//服务失败
    public final static int NO_CARD_ERROR = 10002;//没有卡或不能读
    public final static int POWER_ON_CARD_ERROR = 10003; //上电失败
    public final static int CARD_NOT_READY_ERROR = 10004; //卡未初始化成功
    public final static int BLUETOOTH_CONNECT_ERROR = 10005; //蓝牙连接失败
    public final static int AUTHENTICATE_ERROR = 10006; //auth验证失败
    public final static int COMMAND_ERROR = 10007;//命令发送失败
    public final static int VERIFY_CARD_ERROR = 10008;// 卡号校验失败
    public final static int VERIFY_CARD_PASSWORD_ERROR = 10009;//密码校验失败
    public final static int WRITE_NEW_PASSWORD_ERROR = 10010;//写新密码失败
    public final static int WRITE_ERROR = 10011;//写卡串失败

    //智能设备 蓝牙表和蓝牙LCU 错误信息
    public final static int NO_ADDRESS_ERROR = 20001; //无obis
    public final static int NOT_INITIALIZED = 20002;//未初始化
    public final static int TIME_OUT_ERROR = 20003;//连接超时
    public final static int TIME_OUT_ANSWER = 20004;//响应超时
}
