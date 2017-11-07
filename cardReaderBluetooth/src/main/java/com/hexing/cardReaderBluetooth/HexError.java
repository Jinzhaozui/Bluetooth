package com.hexing.cardReaderBluetooth;

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
}
