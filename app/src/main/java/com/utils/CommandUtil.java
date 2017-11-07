package com.utils;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.example.bluetooth.le.R;
import com.example.bluetooth.le.bean.LcuStateBean;
import com.example.bluetooth.le.thread.WriteTask;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cn.hexing.fdm.protocol.bll.dlmsService;
import cn.hexing.fdm.protocol.comm.CommBLEBlueTooth;
import cn.hexing.fdm.protocol.dlms.HXHdlcDLMSFrame;
import cn.hexing.fdm.protocol.icomm.ICommucation;
import cn.hexing.fdm.protocol.model.CommPara;
import cn.hexing.fdm.protocol.model.HXFramePara;
import cn.hexing.fdm.services.CommServer;

/**
 * Created by HEC271
 * on 2017/6/6.、
 * 命令拼接工具类
 */

public class CommandUtil {
    private final static String READ = "READ";
    private final static String WRITE = "WRITE";
    private final static String ACTION = "ACTION";
    private final static String READ_COMMAND = "READ_COMMAND";
    private final static String READ_STATE = "READ_STATE";

    private HXFramePara FramePara = new HXFramePara();
    private CommServer commDlmsServer;
    private CommPara Cpara = new CommPara();
    private ICommucation icomm;
    private String commandState; //READ WRITE ACTION
    private String OBISattri;
    private String dataType;

    HXHdlcDLMSFrame hdlcFrame = new HXHdlcDLMSFrame();
    // 发送计数 SSS
    private int Nsend = 0;
    // 接收计数 RRR
    private int Nrec = 0;
    private Handler handler;

    public CommandUtil(Handler handler) {
        this.handler = handler;
    }

    public void readStrCommand() {
        commandState = READ;
        icomm = commDlmsServer.OpenDevice(Cpara, icomm);
        new WriteTask(this.handler).execute(getCommand(true));
    }

    public void writeCommand() {
        commandState = WRITE;
        icomm = commDlmsServer.OpenDevice(Cpara, icomm);
    }

    /**
     * 读取继电器状态
     *
     * @param lcuIndex index
     */
    public void readLcuState(int lcuIndex) {
        commandState = READ_STATE;
        String obis = "";
        switch (lcuIndex) {
            case 1:
                obis = Command.LCU_ONE_STATE;
                break;
            case 2:
                obis = Command.LCU_TWO_STATE;
                break;
            case 3:
                obis = Command.LCU_THREE_STATE;
                break;
            case 4:
                obis = Command.LCU_FOUR_STATE;
                break;
        }

        Log.e("LCU状态obis:", obis);
        String command = getToHexadecimal(obis) + Command.NO_MODE_PARA;
        Log.e("LCU状态obis:", command);

        String strLen = "A0" + Command.ADDRESS + Command.ADDRESS3 + command;
        int len = (strLen.length() + 10) / 2;
        strLen = String.format("%02x", len & 0xff);// 字节长度
        String oneCRC = CRCUtil.getCRC16("A0" + strLen + Command.ADDRESS);
        String twoCRC = CRCUtil.getCRC16("A0" + strLen + Command.ADDRESS + oneCRC + Command.ADDRESS3 + command);
        StringBuilder commandBuilder = new StringBuilder();
        commandBuilder.append(Command.HEAD);
        commandBuilder.append("A0");
        commandBuilder.append(strLen);
        commandBuilder.append(Command.ADDRESS);
        commandBuilder.append(oneCRC);
        commandBuilder.append(Command.ADDRESS3);
        commandBuilder.append(command);
        commandBuilder.append(twoCRC);
        commandBuilder.append(Command.HEAD);
        Log.e("LCU历史命令完整obis:", commandBuilder.toString().toUpperCase());
        FramePara.OBISattri = obis;
        FramePara.strDecDataType = "Bool";
        new WriteTask(this.handler).execute(commandBuilder.toString().toUpperCase());
    }

    /**
     * 读取表编号
     */
    public void readMeterNumber() {
        commandState = READ;
        FramePara.OBISattri = "7EA01C0002FEFF0354E9CDE6E600C001C100010000600100FF020089A07E";
        FramePara.strDecDataType = "Ascs";
        icomm = commDlmsServer.OpenDevice(Cpara, icomm);
        new WriteTask(this.handler).execute(getCommand(true));
    }

    public void writeMeterNumber(String meterNo) {
        commandState = WRITE;
        String obis = Command.READ_METER_NUMBER;
        FramePara.strDecDataType = "Ascs";
        Log.e("LCU表序列obis:", obis);
        String command = getToHexadecimal(obis) + Command.NO_MODE_PARA;
        Log.e("LCU表序列设置obis:", command);
        StringBuilder writeData = new StringBuilder();
        writeData.append("0A0B");
        writeData.append(StringToAscii.parseAscii(meterNo));

        String strLen = "A0" + Command.ADDRESS + Command.ADDRESS2 + command + writeData.toString();
        int len = (strLen.length() + 10) / 2;
        strLen = String.format("%02x", len & 0xff);// 字节长度
        String oneCRC = CRCUtil.getCRC16("A0" + strLen + Command.ADDRESS);
        String twoCRC = CRCUtil.getCRC16("A0" + strLen + Command.ADDRESS + oneCRC + Command.ADDRESS2 + command + writeData.toString());
        StringBuilder commandBuilder = new StringBuilder();
        commandBuilder.append(Command.HEAD);
        commandBuilder.append("A0");
        commandBuilder.append(strLen);
        commandBuilder.append(Command.ADDRESS);
        commandBuilder.append(oneCRC);
        commandBuilder.append(Command.ADDRESS2);
        commandBuilder.append(command);
        commandBuilder.append(writeData);
        commandBuilder.append(twoCRC);
        commandBuilder.append(Command.HEAD);
        Log.e("LCU时间设置完整obis:", commandBuilder.toString().toUpperCase());
        FramePara.OBISattri = obis;
        FramePara.WriteData = writeData.toString();
        new WriteTask(this.handler).execute(commandBuilder.toString().toUpperCase());
    }

    public void readMeterBalance() {
        commandState = READ;
        FramePara.OBISattri = "3#1.0.140.129.0.255#2";
        FramePara.OBISattri = "7E A0 1C 00 02 FE FF 03 32 D9 CB E6 E6 00 C0 01 C1 00 03 01 00 8C 81 00 FF 02 00 7E 83 7E";
        FramePara.strDecDataType = "Int32";
        icomm = commDlmsServer.OpenDevice(Cpara, icomm);
        new WriteTask(this.handler).execute(getCommand(true));
    }

    /**
     * 读取时间
     */
    public void readTime() {
        commandState = READ;
        FramePara.OBISattri = "7EA01C0002FEFF0354E9CDE6E600C001C100080000010000FF0200601A7E";
        FramePara.strDecDataType = "Octs_datetime";
        icomm = commDlmsServer.OpenDevice(Cpara, icomm);
        new WriteTask(this.handler).execute(getCommand(true));
    }

    /**
     * 读取 历史命令 记录
     *
     * @param lcuIndex index
     */
    public void readCommandHistory(int lcuIndex) {
        commandState = READ_COMMAND;
        String obis = "";
        switch (lcuIndex) {
            case 1:
                obis = Command.LCU_LAST_COMMAND_ONE;
                break;
            case 2:
                obis = Command.LCU_LAST_COMMAND_TWO;
                break;
            case 3:
                obis = Command.LCU_LAST_COMMAND_THREE;
                break;
            case 4:
                obis = Command.LCU_LAST_COMMAND_FOUR;
                break;
        }

        Log.e("LCU历史命令设置obis:", obis);
        String command = getToHexadecimal(obis) + Command.NO_MODE_PARA;
        Log.e("LCU历史命令设置obis:", command);

        String strLen = "A0" + Command.ADDRESS + Command.ADDRESS3 + command;
        int len = (strLen.length() + 10) / 2;
        strLen = String.format("%02x", len & 0xff);// 字节长度
        String oneCRC = CRCUtil.getCRC16("A0" + strLen + Command.ADDRESS);
        String twoCRC = CRCUtil.getCRC16("A0" + strLen + Command.ADDRESS + oneCRC + Command.ADDRESS3 + command);
        StringBuilder commandBuilder = new StringBuilder();
        commandBuilder.append(Command.HEAD);
        commandBuilder.append("A0");
        commandBuilder.append(strLen);
        commandBuilder.append(Command.ADDRESS);
        commandBuilder.append(oneCRC);
        commandBuilder.append(Command.ADDRESS3);
        commandBuilder.append(command);
        commandBuilder.append(twoCRC);
        commandBuilder.append(Command.HEAD);
        Log.e("LCU历史命令完整obis:", commandBuilder.toString().toUpperCase());
        FramePara.OBISattri = "7EA01C0002FEFF0354E9CDE6E600C001C10046008060030AFF0300EBFA7E";//commandBuilder.toString().toUpperCase();
        FramePara.strDecDataType = "U8";
        new WriteTask(this.handler).execute(FramePara.OBISattri);
    }

    /**
     * 设置时间
     *
     */
    public void writeTime() {
        String obis = Command.WRITE_TIME;
        commandState = WRITE;
        FramePara.strDecDataType = "Octs_string";
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
        int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        int week = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;
        int hour = Calendar.getInstance().get(Calendar.HOUR);
        int minute = Calendar.getInstance().get(Calendar.MINUTE);
        int second = Calendar.getInstance().get(Calendar.SECOND);
        String last = "FF800002";

        Log.e("LCU时间设置obis:", obis);
        String command = getToHexadecimal(obis) + Command.NO_MODE_PARA;
        Log.e("LCU时间设置obis:", command);
        StringBuilder writeData = new StringBuilder();
        writeData.append("090C");
        writeData.append(String.format("%04x", year & 0xffff));
        writeData.append(getToHexadecimal(String.valueOf(month)));
        writeData.append(getToHexadecimal(String.valueOf(day)));
        writeData.append(getToHexadecimal(String.valueOf(week)));
        writeData.append(getToHexadecimal(String.valueOf(hour)));
        writeData.append(getToHexadecimal(String.valueOf(minute)));
        writeData.append(getToHexadecimal(String.valueOf(second)));
        writeData.append(last);

        String strLen = "A0" + Command.ADDRESS + Command.ADDRESS2 + command + writeData.toString();
        int len = (strLen.length() + 10) / 2;
        strLen = String.format("%02x", len & 0xff);// 字节长度
        String oneCRC = CRCUtil.getCRC16("A0" + strLen + Command.ADDRESS);
        String twoCRC = CRCUtil.getCRC16("A0" + strLen + Command.ADDRESS + oneCRC + Command.ADDRESS2 + command + writeData.toString());
        StringBuilder commandBuilder = new StringBuilder();
        commandBuilder.append(Command.HEAD);
        commandBuilder.append("A0");
        commandBuilder.append(strLen);
        commandBuilder.append(Command.ADDRESS);
        commandBuilder.append(oneCRC);
        commandBuilder.append(Command.ADDRESS2);
        commandBuilder.append(command);
        commandBuilder.append(writeData);
        commandBuilder.append(twoCRC);
        commandBuilder.append(Command.HEAD);
        Log.e("LCU时间设置完整obis:", commandBuilder.toString().toUpperCase());
        FramePara.OBISattri = commandBuilder.toString().toUpperCase();
        FramePara.WriteData = writeData.toString();
        new WriteTask(this.handler).execute(commandBuilder.toString().toUpperCase());
    }

    /**
     * 读取时段 继电器
     * 定时 闭合 断开
     */
    public void readArray() {
        commandState = "ARRAY";
        FramePara.OBISattri = "7EA01C0002FEFF0354 E9CD E6E600C001C1 0001000092811BFF02 00 9DE1 7E";
        dataType = "Array_dd";
        icomm = commDlmsServer.OpenDevice(Cpara, icomm);
        new WriteTask(this.handler).execute(getCommand(true));
    }

    /**
     * 设置定时开关
     *
     * @param LcuIndex index 继电器位置 共有4个 下标1 开始
     * @param dataList 数组
     */
    public void writeTimingSwitch(int LcuIndex, List<LcuStateBean> dataList) {
        if (dataList == null || dataList.size() == 0 || LcuIndex <= 0) return;
        String command = "";
        switch (LcuIndex) {
            case 1:
                command = Command.LCU_WRITE_TIME_PERIOD_ONE;
                break;
            case 2:
                command = Command.LCU_WRITE_TIME_PERIOD_TWO;
                break;
            case 3:
                command = Command.LCU_WRITE_TIME_PERIOD_THREE;
                break;
            case 4:
                command = Command.LCU_WRITE_TIME_PERIOD_FOUR;
                break;
        }
        Log.e("LCU定时开关设置obis:", command);
        command = getToHexadecimal(command) + Command.NO_MODE_PARA;
        Log.e("LCU定时开关设置obis:", command);

        commandState = WRITE;
        FramePara.strDecDataType = "Array_dd";

        int arrSize = dataList.size();
        StringBuilder writeData = new StringBuilder();
        //以下是16进制数据
        writeData.append("01");//数组
        writeData.append(getToHexadecimal(String.valueOf(arrSize)));//数组个数
        for (LcuStateBean item : dataList) {
            writeData.append("0203"); //结构体 结构体长度
            writeData.append("11");//u8
            writeData.append(String.format("%02x", Integer.parseInt(item.hour) & 0xff)); //一个字节小时
            writeData.append("11"); //u8
            writeData.append(String.format("%02x", Integer.parseInt(item.minute) & 0xff));//一个字节分钟
            writeData.append("11");//u8 action connect disconnect
            if (item.action.equals("1")) writeData.append("01");
            else writeData.append("00");
        }
        Log.e("LCU定时开关设置数据", writeData.toString());
        String strLen = "A0" + Command.ADDRESS + Command.ADDRESS2 + command + writeData.toString();
        int len = (strLen.length() + 10) / 2;
        strLen = String.format("%02x", len & 0xff);// 字节长度
        String oneCRC = CRCUtil.getCRC16("A0" + strLen + Command.ADDRESS);
        String twoCRC = CRCUtil.getCRC16("A0" + strLen + Command.ADDRESS + oneCRC + Command.ADDRESS2 + command + writeData.toString());
        StringBuilder commandBuilder = new StringBuilder();
        commandBuilder.append(Command.HEAD);
        commandBuilder.append("A0");
        commandBuilder.append(strLen);
        commandBuilder.append(Command.ADDRESS);
        commandBuilder.append(oneCRC);
        commandBuilder.append(Command.ADDRESS2);
        commandBuilder.append(command);
        commandBuilder.append(writeData);
        commandBuilder.append(twoCRC);
        commandBuilder.append(Command.HEAD);
        Log.e("LCU定时开关设置完整obis:", commandBuilder.toString().toUpperCase());
        FramePara.OBISattri = commandBuilder.toString().toUpperCase();
        FramePara.WriteData = writeData.toString();
        new WriteTask(this.handler).execute(commandBuilder.toString().toUpperCase());
    }

    /**
     * LCU闭合 断开 指令
     *
     * @param lcuIndex index
     * @param aConnect true 闭合
     */
    public void actionCommand(int lcuIndex, boolean aConnect) {
        commandState = ACTION;
        icomm = commDlmsServer.OpenDevice(Cpara, icomm);
        String obis = "";
        if (aConnect) {
            switch (lcuIndex) {
                case 1:
                    obis = Command.AISLE_ONE_CON;
                    break;
                case 2:
                    obis = Command.AISLE_TWO_CON;
                    break;
                case 3:
                    obis = Command.AISLE_THREE_CON;
                    break;
                case 4:
                    obis = Command.AISLE_FOUR_CON;
                    break;
            }
        } else {
            switch (lcuIndex) {
                case 1:
                    obis = Command.AISLE_ONE_DIS;
                    break;
                case 2:
                    obis = Command.AISLE_TWO_DIS;
                    break;
                case 3:
                    obis = Command.AISLE_THREE_DIS;
                    break;
                case 4:
                    obis = Command.AISLE_FOUR_DIS;
                    break;
            }
        }
        Log.e("LCU定时开关设置obis:", obis);
        String command = getToHexadecimal(obis) + Command.NO_MODE_PARA;
        Log.e("LCU定时开关设置obis:", command);
        String write = "";
        if (!aConnect) write = "1100";
        String strLen = "A0" + Command.ADDRESS + Command.ADDRESS4 + command + write;
        int len = (strLen.length() + 10) / 2;
        strLen = String.format("%02x", len & 0xff);// 字节长度
        String oneCRC = CRCUtil.getCRC16("A0" + strLen + Command.ADDRESS);
        String twoCRC = CRCUtil.getCRC16("A0" + strLen + Command.ADDRESS + oneCRC + Command.ADDRESS4 + command + write);
        StringBuilder commandBuilder = new StringBuilder();
        commandBuilder.append(Command.HEAD);
        commandBuilder.append("A0");
        commandBuilder.append(strLen);
        commandBuilder.append(Command.ADDRESS);
        commandBuilder.append(oneCRC);
        commandBuilder.append(Command.ADDRESS4);
        commandBuilder.append(command);
        commandBuilder.append(write);
        commandBuilder.append(twoCRC);
        commandBuilder.append(Command.HEAD);
        Log.e("LCU定时开关设置完整obis:", commandBuilder.toString().toUpperCase());
        FramePara.OBISattri = commandBuilder.toString().toUpperCase();
        FramePara.strDecDataType = "Bool";
        new WriteTask(this.handler).execute(commandBuilder.toString().toUpperCase());
    }

    /**
     * 获取命令字符串
     *
     * @return str
     */
    private String getCommand(boolean isComplete) {
        if (isComplete) return FramePara.OBISattri;
        byte[] byt = commDlmsServer.sendByte(FramePara, icomm);
        StringBuilder str = new StringBuilder();
        StringBuilder str2 = new StringBuilder();
        StringBuilder str3 = new StringBuilder();
        if (byt != null) {
            for (int i = 0; i < byt.length; i++) {
                str.append(String.format("%02x", byt[i] & 0xff)); //+= (char) byt[i]
            }
            str2.append(dlmsService.TranXADRCode(byt));
            str3.append(dlmsService.TranXADRCode(byt, dlmsService.DataType.Octs_string));
            Log.e("byte-->", dlmsService.Byte2String(byt, 0, byt.length));
            Log.e("byte-->", str.toString().toUpperCase());
            Log.e("byte2-->", str2.toString());
            Log.e("byte3-->", str3.toString());
        }
        return str.toString().toUpperCase();
    }

    //拼接lcu 命令
    public static class Command {
        public final static String HEAD = "7E";
        public final static String ADDRESS = "0002FEFF0354";
        public final static String ADDRESS2 = "E6E600C101C100"; //写入
        public final static String ADDRESS3 = "E6E600C001C100";//  读取
        public final static String ADDRESS4 = "E6E600C301C100";//指令action
        public final static String NO_MODE_PARA = "00"; //不带选择性参数
        public final static String MODE_PARA = "01"; //带选择性参数

        public final static String AISLE_ONE_DIS = "70#0.128.96.3.10.255#1";//LCU1断开 通道1  integer 数据类型
        public final static String AISLE_TWO_DIS = "70#0.129.96.3.10.255#1"; //LCU2断开 通道2
        public final static String AISLE_THREE_DIS = "70#0.130.96.3.10.255#1";//LCU3断开 通道3
        public final static String AISLE_FOUR_DIS = "70#0.131.96.3.10.255#1";//LCU4断开 通道4

        public final static String AISLE_ONE_CON = "70#0.128.96.3.10.255#2"; // LCU1闭合
        public final static String AISLE_TWO_CON = "70#0.129.96.3.10.255#2";// LCU2闭合
        public final static String AISLE_THREE_CON = "70#0.130.96.3.10.255#2";// LCU3闭合
        public final static String AISLE_FOUR_CON = "70#0.131.96.3.10.255#2";// LCU4闭合

        public final static String LCU_ONE_STATE = "70#0.128.96.3.10.255#2"; //lcu1 物理状态 boolean  0: disconnected 1: connected
        public final static String LCU_TWO_STATE = "70#0.129.96.3.10.255#2"; //lcu2 物理状态 boolean
        public final static String LCU_THREE_STATE = "70#0.130.96.3.10.255#2"; //lcu3 物理状态 boolean
        public final static String LCU_FOUR_STATE = "70#0.131.96.3.10.255#2"; //lcu4 物理状态 boolean

        public final static String LCU_LAST_COMMAND_ONE = "70#0.128.96.3.10.255#3"; //lcu1 最后一次命令 enum  00: no command   01: connected
        public final static String LCU_LAST_COMMAND_TWO = "70#0.129.96.3.10.255#3"; //lcu2 最后一次命令 enum
        public final static String LCU_LAST_COMMAND_THREE = "70#0.130.96.3.10.255#3"; //lcu3 最后一次命令 enum
        public final static String LCU_LAST_COMMAND_FOUR = "70#0.131.96.3.10.255#3"; //lcu4 最后一次命令 enum

        public final static String LCU_WRITE_TIME_PERIOD_ONE = "1#0.0.146.129.27.255#2"; //设置LCU1 时间 "01000092811BFF02" + "00"; //"
        public final static String LCU_WRITE_TIME_PERIOD_TWO = "1#0.0.146.129.28.255#2"; //设置LCU2 时间
        public final static String LCU_WRITE_TIME_PERIOD_THREE = "1#0.0.146.129.29.255#2"; //设置LCU3 时间
        public final static String LCU_WRITE_TIME_PERIOD_FOUR = "1#0.0.146.129.30.255#2"; //设置LCU4 时间

        public final static String WRITE_TIME = "8#0.0.1.0.0.255#2"; //设置时间 读时间 写时间
        public final static String READ_METER_NUMBER = "1#0.0.96.1.0.255#2"; //ASCS 读表

        //数据格式  数组
        //array[max=12]
        // {
//            structure
//            include
//            {
//                hour; unsigned
//                minute; unsigned
//                action; unsigned. (disconnect--0x00; connect--0x01)
//            }
//        }
    }

    /**
     * 设置 必需参数
     *
     * @param strMeterNo    电表号
     * @param deviceAddress 蓝牙地址
     * @param dataType      数据类型
     */
    public void setFramePara(String strMeterNo, String deviceAddress, String obis, String dataType) {
        this.dataType = dataType;
        FramePara.OBISattri = obis;//"1#1.0.144.129.0.255#2";//
        FramePara.strDecDataType = dataType; //"Array_dd"
        FramePara.strMeterNo = strMeterNo;
        Cpara.ComName = deviceAddress;
    }

    /**
     * 设置 必需参数
     *
     * @param strMeterNo    电表号
     * @param deviceAddress 蓝牙地址
     */
    public void setFramePara(String strMeterNo, String deviceAddress) {
        FramePara.strDecDataType = dataType; //"Array_dd"
        FramePara.strMeterNo = strMeterNo;
        Cpara.ComName = deviceAddress;
    }

    public void init() {
        // DLMS 协议通讯参数
        commDlmsServer = new CommServer();
        Cpara = new CommPara();
        icomm = new CommBLEBlueTooth();
        FramePara = new HXFramePara();

        FramePara.CommDeviceType = "RF";// RF  Optical
        FramePara.FirstFrame = true;
        FramePara.Mode = HXFramePara.AuthMode.HLS;
        FramePara.enLevel = 0x00;
        FramePara.SourceAddr = 0x03;

        FramePara.WaitT = 3000;
        FramePara.ByteWaitT = 1500;
        FramePara.Pwd = "00000000";
        FramePara.aesKey = new byte[16];
        FramePara.auKey = new byte[16];
        FramePara.enKey = new byte[16];
        String sysTstr = "4845430005000001";
        FramePara.StrsysTitleC = "4845430005000001";
        FramePara.encryptionMethod = HXFramePara.AuthMethod.AES_GCM_128;
        FramePara.sysTitleS = new byte[8];
        FramePara.MaxSendInfo_Value = 255;
    }

    /**
     * 数据解析
     *
     * @param data str
     */
    public String displayResponseData(String data) {
        String strResult = "";
        if (data == null) {
            Log.e("返回原始数据", "null");
            return "";
        }
        icomm.setORIData(data);
        if (commandState.equals(READ)) {
            //strResult = commDlmsServer.Read(FramePara, icomm);
            strResult = analysisData(data);
        } else if (commandState.equals(ACTION)) {
            strResult = commDlmsServer.Action(FramePara, icomm) + "";
        } else if (commandState.equals(WRITE)) {
            strResult = analysisBoolData(data);
        } else if (commandState.equals(READ_COMMAND)) {
            strResult = analysisEnumData(data);
        } else if (commandState.equals(READ_STATE)) {
        }
        return strResult;
    }

    /**
     * 数据解析
     *
     * @param data str
     */
    public String analysisData(@NonNull String data) {
        //icomm.setORIData(data);
        String strResult = commDlmsServer.readReceiver(FramePara, icomm);
        Log.e("数据解析字符串原数据--》", data);
        Log.e("数据解析字符串--》", strResult);
        return strResult.equals("") ? data : strResult;
    }

    /**
     * 写入数据 返回 成功失败
     *
     * @param data 返回数据
     * @return TRUE false
     */
    private String analysisBoolData(@NonNull String data) {
        data = data.substring(0, data.length() - 6);
        if (data.lastIndexOf("4100") > -1) {
            return "true";
        }
        return "false";
    }

    private String analysisEnumData(@NonNull String data) {
        data = data.substring(0, data.length() - 6);
        data = data.substring(data.length() - 2, data.length());
        if (data.equals("00")) {
            return "No Command";
        }
        return "Connect Command";
    }

    /**
     * 解析数组
     *
     * @param data
     * @return
     */
    public List<LcuStateBean> analysisArray(@NonNull String data) {
        data = data.substring(0, data.length() - 6);
        String[] attrs = data.split("0203"); //结构体 3个字节
        List<LcuStateBean> list = new ArrayList<>();
        LcuStateBean bean;
        for (String item : attrs) {
            Log.e("数据解析字符串--》", item);
            if (!item.contains("7EA0")) {
                bean = new LcuStateBean();
                bean.hour = Integer.valueOf(item.substring(2, 4), 16).toString();
                bean.minute = Integer.valueOf(item.substring(6, 8), 16).toString();
                bean.action = Integer.valueOf(item.substring(10, 12), 16).toString();
                list.add(bean);
                Log.e("数据解析字符串--》", bean.toString());
            }
        }
        return list;
    }

    /**
     * CRC校验和检验
     *
     * @param receiveByt byte
     * @return bool
     */
    private boolean CheckFrame(byte[] receiveByt) {
        boolean isOK = false;
        if (receiveByt == null) {
            return isOK;
        }
        if (receiveByt.length > 3) {
            if ((receiveByt[2] & 0xff) == receiveByt.length - 2) {
                isOK = true;
                byte[] FrameEnd = hdlcFrame.CRC16(receiveByt, 1,
                        receiveByt.length - 4);
                if (FrameEnd[0] != receiveByt[receiveByt.length - 3]
                        && FrameEnd[1] != receiveByt[receiveByt.length - 2]) {
                    isOK = false;
                }
            }
        }
        return isOK;
    }

    /**
     * 协议回复异常解析提示
     *
     * @param FaultCode
     * @return
     */
    private String AccessResult(byte FaultCode) {
        int FaultStr = (int) (FaultCode);
        String RtnStr = "";
        switch (FaultStr) {
            case 1:
                RtnStr = "DLMS_HARDWARE_FAULT";
                break;
            case 2:
                RtnStr = "DLMS_TEMP_FAILURE";
                break;
            case 3:
                RtnStr = "DLMS_RW_DENIED";
                break;
            case 4:
                RtnStr = "DLMS_OBJECT_UNDEF";
                break;
            case 9:
                RtnStr = "DLMS_OBJECT_INCON";
                break;
            case 11:
                RtnStr = "DLMS_OBJECT_UNAVAILABLE";
                break;
            case 12:
                RtnStr = "DLMS_TYPE_UNMATCHED";
                break;
            case 13:
                RtnStr = "DLMS_ACCESS_VIOLATED";
                break;
            case 14:
                RtnStr = "DLMS_DATA_UNAVAILABLE";
                break;
            case 15:
                RtnStr = "DLMS_LONGGET_ABORTED";
                break;
            case 16:
                RtnStr = "DLMS_NOLONG_GET";
                break;
            case 17:
                RtnStr = "DLMS_LONGSET_ABORTED";
                break;
            case 18:
                RtnStr = "DLMS_NOLONG_SET";
                break;
            default:
                RtnStr = "DLMS_DATA_DENIED";
                break;
        }
        return RtnStr;
    }

    /**
     * 解析OBIS
     *
     * @param obis "1#1.0.144.129.0.255#2";
     * @return 转换 16进制
     */
    private String getToHexadecimal(String obis) {
        obis = obis.replace(" ", "");
        String[] arr = obis.split("\\.|#");
        StringBuilder stringBuilder = new StringBuilder();
        if (arr.length > 0) {
            for (String item : arr) {
                if (!TextUtils.isEmpty(item))
                    stringBuilder.append(String.format("%02x", Integer.parseInt(item) & 0xff));
            }
        }
        return stringBuilder.toString();
    }

}
