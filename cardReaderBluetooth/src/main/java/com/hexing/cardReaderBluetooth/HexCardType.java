package com.hexing.cardReaderBluetooth;

/**
 * Created by HEC271
 * on 2017/6/14.
 */

public class HexCardType {
    public final static String COMMAND_SUCCESS = "9000";// 返回完整数据结束标识
    public final static String READ_COMMAND = "READ";
    public final static String WRITE_DATA_COMMAND = "WRITE";
    public final static String VERIFY_COMMAND_CARD = "VERITY_CARD";
    public final static String VERIFY_COMMAND_PASSWORD = "VERITY_PASSWORD";
    public final static String BACKUP_COMMAND = "BACKUP";//备份数据
    public final static String WRITE_PASSWORD_COMMAND = "WRITE_PASSWORD";

    public class Card_4442 {//小卡
        public final static int CARD_CODE = 4442; //256位
        public final static int CARD_LEN = 256;
        public final static String CARD_ATR = "3B04A2131091";
        public final static String READ_COMMAND = "FFB000";  //READ_COMMAND+ Byte Address +  数据长度
        public final static String WRITE_COMMAND = "FFD000";// READ_COMMAND+ Byte Address +  数据长度+ 数据  //一个字节地址
        public final static String VERIFY_COMMAND = "FF20000003";
        public final static String VERIFY_PASSWORD_SUCCESS = "9007";
        public final static String PARAM = "{\"read\":{\"offset\":[32],\"value\":[224]}}";
    }

    public class Card_4428 {//大卡
        public final static int CARD_CODE = 4428; //1024位
        public final static int CARD_LEN = 1024;
        public final static String CARD_ATR = "3B0492231091";
        public final static String READ_COMMAND = "FFB0";  //READ_COMMAND+ Byte Address +  数据长度
        public final static String WRITE_COMMAND = "FFD0";// READ_COMMAND+ Byte Address +  数据长度+ 数据 //2个字节地址
        //public final static String WRITE_PASS_COMMAND = WRITE_COMMAND + "002"; //写密码命令 0000地址 02个字节
        public final static String VERIFY_COMMAND = "FF20000002";
        public final static String VERIFY_PASSWORD_SUCCESS = "90FF";
        public final static String PARAM = "{\"read\":{\"offset\":[0],\"value\":[1024]}}";
    }
}
