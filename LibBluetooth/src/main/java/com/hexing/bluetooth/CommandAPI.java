package com.hexing.bluetooth;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.hexing.bluetooth.bean.LcuStateBean;
import com.hexing.bluetooth.thread.WriteTask;
import com.hexing.bluetooth.util.CRCUtil;
import com.hexing.bluetooth.util.DateTimeTool;
import com.hexing.bluetooth.util.StringToAscii;

import java.util.Calendar;
import java.util.List;

/**
 * Created by caibinglong
 * on 2017/7/7.
 */

public class CommandAPI {
    private static CommandAPI instance;
    private static final String TAG = ConnectAPI.class.getName();
    private HexCallback callback;
    private DataType dataType;

    public class DataType {
        public static final String LCU_READ_TIME_PERIOD = "LCU_READ_TIME_PERIOD"; //读写时段
        public static final String LCU_WRITE_TIME_PERIOD = "LCU_WRITE_TIME_PERIOD"; //读写时段
        public static final String LCU_STATUS = "LCU_STATUS"; //读物理状态
        public static final String LCU_ACTION = "LCU_ACTION";//指令
        public static final String LCU_READ_TIME = "LCU_READ_TIME";// 读时间
        public static final String LCU_WRITE_TIME = "LCU_WRITE_TIME";// 写时间
        public static final String LCU_READ_COMMAND = "LCU_READ_COMMAND";// 读取命令
        public static final String METER_WRITE_TOKEN = "METER_WRITE_TOKEN";// 写token
        public static final String METER_READ_NUMBER = "READ_METER_NUMBER";//读表号
        public static final String METER_CANCEL_ALARM = "METER_CANCEL_ALARM";//取消报警
        public static final String METER_OVERDRAFT = "METER_OVERDRAFT";//电表紧急透支
        public static final String METER_READ_RELAY_REASON = "METER_READ_RELAY_REASON";//继电器操作原因
        public static final String METER_BALANCE = "METER_BALANCE";//查询 电余额
        public static final String METER_POSITIVE_ACTIVE = "METER_POSITIVE_ACTIVE";// 查询正向有功
        public static final String LCU_WRITE_NUMBER = "LCU_WRITE_NUMBER";// 写表号
        public static final String METER_MONTHLY_FREEZE_POWER_INFORMATION = "METER_MONTHLY_FREEZE_POWER_INFORMATION";//月冻结电能
        public static final String METER_DAILY_FREEZE_POWER_INFORMATION = "METER_DAILY_FREEZE_POWER_INFORMATION";//日冻结电能
        public static final String METER_DAY_FREEZE_PREPAID = "METER_DAY_FREEZE_PREPAID";//预付费日冻结信息
        public static final String METER_MONTH_FREEZE_PREPAID = "METER_MONTH_FREEZE_PREPAID";//预付费月冻结信息

        //操作类型  读取 写入 指令
        public static final String READ = "READ";
        public static final String WRITE = "WRITE";
        public static final String ACTION = "ACTION";

        //设备类型 电表 和 LCU
        public static final String DEVICE_METER = "METER";
        public static final String DEVICE_LCU = "LCU";

        private String value;
        private String action;
        private String deviceType;

        public String getDeviceType() {
            return deviceType;
        }

        public void setDeviceType(String deviceType) {
            this.deviceType = deviceType;
        }

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public DataType getDataType() {
        return dataType;
    }

    public static CommandAPI getInstance() {
        if (instance == null) {
            synchronized (CommandAPI.class) {
                if (instance == null) {
                    instance = new CommandAPI();
                }
            }
        }
        return instance;
    }

    /**
     * 初始化
     */
    public void init(HexCallback callback) {
        this.callback = callback;
        this.dataType = new DataType();
    }

    /**
     * 读取继电器状态
     *
     * @param lcuIndex index
     */
    public synchronized void readLcuState(int lcuIndex) {
        this.dataType.setValue(DataType.LCU_STATUS);
        this.dataType.setAction(DataType.READ);
        this.dataType.setDeviceType(DataType.DEVICE_LCU);
        String obis = this.getObisByPosition(lcuIndex);
        Log.e("LCU状态obis:", obis);
        AnalysisAPI.getInstance().FramePara.strDecDataType = AnalysisAPI.DecDataType.TYPE_LCU_ACTION;
        AnalysisAPI.getInstance().setDecDataType(AnalysisAPI.DecDataType.TYPE_LCU_ACTION);
        this.stitchingData(obis, Command.NO_MODE_PARA);
    }

    /**
     * 读取表编号
     */
    public void readMeterNumber() {
        this.dataType.setValue(DataType.METER_READ_NUMBER);
        this.dataType.setAction(DataType.READ);
        this.dataType.setDeviceType(DataType.DEVICE_METER);
        AnalysisAPI.getInstance().FramePara.OBISattri = this.getObisByType();
        AnalysisAPI.getInstance().FramePara.strDecDataType = "Ascs";
        AnalysisAPI.getInstance().setDecDataType(AnalysisAPI.DecDataType.TYPE_ASCS);
        this.stitchingData(AnalysisAPI.getInstance().FramePara.OBISattri, Command.NO_MODE_PARA);
    }

    /**
     * 紧急透支
     */
    public void writeUrgentOverdraft() {
        //811
        //7E A0 21 00 02 FE FF 03 32 DC 33 E6 E6 00 C1 01 C1 00 01 01 00 8C 82 00 FF 02 00 0A 03 38 31 31 BC 64 7E
        dataType.setValue(DataType.METER_OVERDRAFT);
        dataType.setAction(DataType.WRITE);
        dataType.setDeviceType(DataType.DEVICE_METER);
        AnalysisAPI.getInstance().FramePara.OBISattri = this.getObisByPosition(0);
        AnalysisAPI.getInstance().FramePara.strDecDataType = AnalysisAPI.DecDataType.TYPE_BOOL;
        AnalysisAPI.getInstance().setDecDataType(AnalysisAPI.DecDataType.TYPE_BOOL);
        this.stitchingData(AnalysisAPI.getInstance().FramePara.OBISattri, Command.NO_MODE_PARA, "0A03" + StringToAscii.parseAscii("811"));
    }

    /**
     * 查询余额
     */
    public void readMeterBalance() {
        this.dataType.setValue(DataType.METER_BALANCE);
        this.dataType.setAction(DataType.READ);
        this.dataType.setDeviceType(DataType.DEVICE_METER);
        AnalysisAPI.getInstance().FramePara.OBISattri = this.getObisByType();
        //String command = "7E A0 1C 00 02 FE FF 03 32 D9 CB E6 E6 00 C0 01 C1 00 03 01 00 8C 81 00 FF 02 00 7E 83 7E";
        AnalysisAPI.getInstance().FramePara.strDecDataType = "Int32";
        AnalysisAPI.getInstance().setDecDataType(AnalysisAPI.DecDataType.TYPE_INT32);
        this.stitchingData(AnalysisAPI.getInstance().FramePara.OBISattri, Command.NO_MODE_PARA);
    }

    /**
     * 查询正向有功
     */
    public void readMeterPositiveActive() {
        this.dataType.setValue(DataType.METER_POSITIVE_ACTIVE);
        this.dataType.setAction(DataType.READ);
        this.dataType.setDeviceType(DataType.DEVICE_METER);
        AnalysisAPI.getInstance().FramePara.OBISattri = this.getObisByType();//
        //String command = "7E A0 1C 00 02 FE FF 03 32 D9 CB E6 E6 00 C0 01 C1 00 03 01 00 01 08 00 FF 02 00 32 68 7E";
        AnalysisAPI.getInstance().FramePara.strDecDataType = "Int32";
        AnalysisAPI.getInstance().setDecDataType(AnalysisAPI.DecDataType.TYPE_INT32);
        this.stitchingData(AnalysisAPI.getInstance().FramePara.OBISattri, Command.NO_MODE_PARA);
    }

    /**
     * 下发token 到表
     *
     * @param token token
     */
    public void writeMeterToken(String token) {
        this.dataType.setValue(DataType.METER_WRITE_TOKEN);
 this.dataType.setAction(DataType.WRITE);
 this.dataType.setDeviceType(DataType.DEVICE_METER);
 AnalysisAPI.getInstance().FramePara.OBISattri = this.getObisByType();
 AnalysisAPI.getInstance().FramePara.strDecDataType = "Octs_string";
 AnalysisAPI.getInstance().setDecDataType(AnalysisAPI.DecDataType.TYPE_OCTS_STRING);
 this.stitchingData(AnalysisAPI.getInstance().FramePara.OBISattri, Command.NO_MODE_PARA, "7E000A08014944" + token+"54");

        //this.stitchingData(AnalysisAPI.getInstance().FramePara.OBISattri, Command.NO_MODE_PARA, "7E000A08014944" + token);

    }

    /**
     * 取消警报
     */
    public void cancelMeterAlarm() {
        //write 812
        //String command = "7E A0 21 00 02 FE FF 03 32 DC 33 E6 E6 00 C1 01 C1 00 01 01 00 8C 82 00 FF 02 00 0A 03 38 31 32 27 56 7E";
        this.dataType.setValue(DataType.METER_CANCEL_ALARM);
        this.dataType.setAction(DataType.WRITE);
        this.dataType.setDeviceType(DataType.DEVICE_METER);
        AnalysisAPI.getInstance().FramePara.OBISattri = this.getObisByPosition(0);
        AnalysisAPI.getInstance().FramePara.strDecDataType = "Octs_ascii";
        AnalysisAPI.getInstance().setDecDataType(AnalysisAPI.DecDataType.TYPE_BOOL);
        this.stitchingData(AnalysisAPI.getInstance().FramePara.OBISattri, Command.NO_MODE_PARA, "0A03" + StringToAscii.parseAscii("812"));
    }

    /**
     * 读取 电表 继电器 操作原因
     */
    public void readMeterRelayReason() {
        //7E A0 1C 00 02 FE FF 03 32 D9 CB E6 E6 00 C0 01 C1 00 46 00 00 60 03 0A FF 05 00 26 28 7E
        this.dataType.setValue(DataType.METER_READ_RELAY_REASON);
        this.dataType.setAction(DataType.READ);
        this.dataType.setDeviceType(DataType.DEVICE_METER);
        AnalysisAPI.getInstance().FramePara.OBISattri = this.getObisByType();
        AnalysisAPI.getInstance().FramePara.strDecDataType = AnalysisAPI.DecDataType.TYPE_INT8;
        AnalysisAPI.getInstance().setDecDataType(AnalysisAPI.DecDataType.TYPE_INT8);
        this.stitchingData(AnalysisAPI.getInstance().FramePara.OBISattri, Command.NO_MODE_PARA);
    }

    /**
     * 写 表号 LCU 设备
     *
     * @param meterNo number
     */
    public void writeMeterNumber(String meterNo) {
        this.dataType.setValue(DataType.LCU_WRITE_NUMBER);
        this.dataType.setAction(DataType.WRITE);
        this.dataType.setDeviceType(DataType.DEVICE_LCU);
        String obis = this.getObisByType();
        AnalysisAPI.getInstance().FramePara.strDecDataType = "Ascs";

        StringBuilder writeData = new StringBuilder();
        writeData.append("0A0B");
        writeData.append(StringToAscii.parseAscii(meterNo));
        this.stitchingData(obis, Command.NO_MODE_PARA, writeData.toString());
    }

    /**
     * 读取时间
     */
    public void readLCUTime() {
        ///String command = "7EA01C0002FEFF0354E9CDE6E600C001C100080000010000FF0200601A7E";
        dataType.setValue(DataType.LCU_READ_TIME);
        dataType.setAction(DataType.READ);
        dataType.setDeviceType(DataType.DEVICE_LCU);
        AnalysisAPI.getInstance().FramePara.OBISattri = this.getObisByType();
        AnalysisAPI.getInstance().FramePara.strDecDataType = "Octs_datetime";
        AnalysisAPI.getInstance().setDecDataType(AnalysisAPI.DecDataType.TYPE_OCTS_DATETIME);
        stitchingData(AnalysisAPI.getInstance().FramePara.OBISattri, Command.NO_MODE_PARA);
    }

    /**
     * 读取 历史命令 记录
     *
     * @param lcuIndex index
     */
    public void readLCUCommandHistory(int lcuIndex) {
        dataType.setValue(DataType.LCU_READ_COMMAND);
        dataType.setAction(DataType.READ);
        dataType.setDeviceType(DataType.DEVICE_LCU);
        String obis = getObisByPosition(lcuIndex);
        Log.e("LCU历史命令设置obis:", obis);
        //FramePara.OBISattri = "7EA01C0002FEFF0354E9CDE6E600C001C10046008060030AFF0300EBFA7E";//commandBuilder.toString().toUpperCase();
        AnalysisAPI.getInstance().FramePara.strDecDataType = "U8";
        AnalysisAPI.getInstance().setDecDataType(AnalysisAPI.DecDataType.TYPE_U8);
        stitchingData(obis, Command.NO_MODE_PARA);
    }

    /**
     * 设置时间
     */
    public void writeLCUTime() {
        this.dataType.setValue(DataType.LCU_WRITE_TIME);
        this.dataType.setDeviceType(DataType.DEVICE_LCU);
        this.dataType.setAction(DataType.WRITE);
        AnalysisAPI.getInstance().FramePara.strDecDataType = "Octs_string";
        AnalysisAPI.getInstance().setDecDataType(AnalysisAPI.DecDataType.TYPE_BOOL);
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
        int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        int week = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int minute = Calendar.getInstance().get(Calendar.MINUTE);
        int second = Calendar.getInstance().get(Calendar.SECOND);
        String last = "FF800002";

        Log.e("LCU时间设置obis:", this.getObisByType());
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
        AnalysisAPI.getInstance().FramePara.OBISattri = this.getObisByType();
        this.stitchingData(AnalysisAPI.getInstance().FramePara.OBISattri, Command.NO_MODE_PARA, writeData.toString());
    }

    /**
     * 读取时段 继电器
     * 定时 闭合 断开
     */
    public void readLCUTimePeriod(int position) {
        this.dataType.setValue(DataType.LCU_READ_TIME_PERIOD);
        this.dataType.setAction(DataType.READ);
        this.dataType.setDeviceType(DataType.DEVICE_LCU);
        AnalysisAPI.getInstance().FramePara.strDecDataType = "Array_dd";
        AnalysisAPI.getInstance().setDecDataType(AnalysisAPI.DecDataType.TYPE_ARRAY_DD);
        this.dataType.setAction(DataType.READ);
        this.dataType.setValue(DataType.LCU_READ_TIME_PERIOD);
        AnalysisAPI.getInstance().FramePara.OBISattri = this.getObisByPosition(position);
        this.stitchingData(AnalysisAPI.getInstance().FramePara.OBISattri, Command.NO_MODE_PARA);
    }

    private String getObisByPosition(int position) {
        String obis = "";
        switch (position) {
            case 0:
                obis = getObisByType();
                break;
            case 1:
                if (this.dataType.getValue().equals(DataType.LCU_READ_TIME_PERIOD) ||
                        this.dataType.getValue().equals(DataType.LCU_WRITE_TIME_PERIOD))
                    obis = Command.LCU_WRITE_TIME_PERIOD_ONE;
                else if (this.dataType.getValue().equals(DataType.LCU_STATUS))
                    obis = Command.LCU_ONE_STATE;
                else if (this.dataType.getValue().equals(DataType.LCU_ACTION))
                    obis = Command.LCU_AISLE_ONE_CON;
                else if (dataType.getValue().equals(DataType.LCU_READ_COMMAND))
                    obis = Command.LCU_LAST_COMMAND_ONE;
                break;
            case 2:
                if (this.dataType.getValue().equals(DataType.LCU_READ_TIME_PERIOD)
                        || this.dataType.getValue().equals(DataType.LCU_WRITE_TIME_PERIOD))
                    obis = Command.LCU_WRITE_TIME_PERIOD_TWO;
                else if (this.dataType.getValue().equals(DataType.LCU_STATUS))
                    obis = Command.LCU_TWO_STATE;
                else if (this.dataType.getValue().equals(DataType.LCU_ACTION))
                    obis = Command.LCU_AISLE_TWO_CON;
                else if (this.dataType.getValue().equals(DataType.LCU_READ_COMMAND))
                    obis = Command.LCU_LAST_COMMAND_TWO;
                break;
            case 3:
                if (this.dataType.getValue().equals(DataType.LCU_READ_TIME_PERIOD)
                        || this.dataType.getValue().equals(DataType.LCU_WRITE_TIME_PERIOD))
                    obis = Command.LCU_WRITE_TIME_PERIOD_THREE;
                else if (this.dataType.getValue().equals(DataType.LCU_STATUS))
                    obis = Command.LCU_THREE_STATE;
                else if (this.dataType.getValue().equals(DataType.LCU_ACTION))
                    obis = Command.LCU_AISLE_THREE_CON;
                else if (this.dataType.getValue().equals(DataType.LCU_READ_COMMAND))
                    obis = Command.LCU_LAST_COMMAND_THREE;
                break;
            case 4:
                if (this.dataType.getValue().equals(DataType.LCU_READ_TIME_PERIOD)
                        || this.dataType.getValue().equals(DataType.LCU_WRITE_TIME_PERIOD))
                    obis = Command.LCU_WRITE_TIME_PERIOD_FOUR;
                else if (this.dataType.getValue().equals(DataType.LCU_STATUS))
                    obis = Command.LCU_FOUR_STATE;
                else if (this.dataType.getValue().equals(DataType.LCU_ACTION))
                    obis = Command.LCU_AISLE_FOUR_CON;
                else if (this.dataType.getValue().equals(DataType.LCU_READ_COMMAND))
                    obis = Command.LCU_LAST_COMMAND_FOUR;
                break;
        }
        return obis;
    }

    private String getObisByType() {
        String obis = null;
        switch (this.dataType.getValue()) {
            case DataType.LCU_WRITE_TIME:
            case DataType.LCU_READ_TIME:
                obis = Command.WRITE_TIME;
                break;
            case DataType.METER_WRITE_TOKEN:
                obis = Command.WRITE_TOKEN;
                break;
            case DataType.METER_CANCEL_ALARM:
                obis = Command.METER_CANCEL_ALARM;
                break;
            case DataType.METER_READ_NUMBER:
                obis = Command.READ_METER_NUMBER;
                break;
            case DataType.METER_POSITIVE_ACTIVE:
                obis = Command.METER_POSITIVE_ACTIVE;
                break;
            case DataType.METER_BALANCE:
                obis = Command.METER_BALANCE;
                break;
            case DataType.METER_MONTHLY_FREEZE_POWER_INFORMATION:
                obis = Command.METER_MONTHLY_FREEZE_POWER_INFORMATION;
                break;
            case DataType.METER_DAILY_FREEZE_POWER_INFORMATION:
                obis = Command.METER_DAILY_FREEZE_POWER_INFORMATION;
                break;
            case DataType.METER_DAY_FREEZE_PREPAID:
                obis = Command.METER_DAY_FREEZE_PREPAID;
                break;
            case DataType.METER_MONTH_FREEZE_PREPAID:
                obis = Command.METER_MONTH_FREEZE_PREPAID;
                break;
            case DataType.METER_READ_RELAY_REASON:
                obis = Command.METER_READ_REALY_REASON;
                break;
            case DataType.METER_OVERDRAFT:
                obis = Command.METER_EMERGENCY_OVERDRAFT;
                break;
        }
        return obis;
    }

    /**
     * 设置定时开关
     *
     * @param LcuIndex index 继电器位置 共有4个 下标1 开始
     * @param dataList 数组
     */
    public void writeLCUTimingSwitch(int LcuIndex, List<LcuStateBean> dataList) {
        if (dataList == null || LcuIndex <= 0) return;
        AnalysisAPI.getInstance().FramePara.strDecDataType = "Array_dd";
        this.dataType.setValue(DataType.LCU_WRITE_TIME_PERIOD);
        this.dataType.setAction(DataType.WRITE);
        this.dataType.setDeviceType(DataType.DEVICE_LCU);
        AnalysisAPI.getInstance().setDecDataType(AnalysisAPI.DecDataType.TYPE_BOOL);
        String obis = this.getObisByPosition(LcuIndex);
        Log.e("LCU定时开关设置obis:", obis);
        int arrSize = dataList.size();
        StringBuilder writeData = new StringBuilder();
        //以下是16进制数据
        //  if (dataList.size() > 0) {
        writeData.append("01");//数组
        writeData.append(getToHexadecimal(String.valueOf(arrSize)));//数组个数
        //}
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

        Log.e(TAG, "writeLCUTimingSwitch-obis=" + obis + "||write->" + writeData.toString());
        this.stitchingData(obis, Command.NO_MODE_PARA, writeData.toString());
    }

    /**
     * LCU闭合 断开 指令
     *
     * @param lcuIndex index
     * @param aConnect true 闭合
     */
    public void actionLCUCommand(int lcuIndex, boolean aConnect) {
        AnalysisAPI.getInstance().FramePara.strDecDataType = "Bool";
        AnalysisAPI.getInstance().setDecDataType(AnalysisAPI.DecDataType.TYPE_BOOL);
        this.dataType.setAction(DataType.ACTION);
        this.dataType.setValue(DataType.LCU_ACTION);
        this.dataType.setDeviceType(DataType.DEVICE_LCU);
        String obis = "";
        if (aConnect) {
            obis = getObisByPosition(lcuIndex);
        } else {
            switch (lcuIndex) {
                case 1:
                    obis = Command.LCU_AISLE_ONE_DIS;
                    break;
                case 2:
                    obis = Command.LCU_AISLE_TWO_DIS;
                    break;
                case 3:
                    obis = Command.LCU_AISLE_THREE_DIS;
                    break;
                case 4:
                    obis = Command.LCU_AISLE_FOUR_DIS;
                    break;
            }
        }
        Log.e("LCU定时开关设置obis:", obis);
        String write = "";
        if (!aConnect) write = "1100";
        this.stitchingData(obis, Command.NO_MODE_PARA, write);
    }

    /**
     * 读取日冻结电能信息
     *
     * @param startDate 开始时间
     * @param endDate   结束时间
     */
    public void readDayFreezePower(@NonNull String startDate, @NonNull String endDate) {
        startDate = startDate.replace("-", "");
        endDate = endDate.replace("-", "");
        if (startDate.length() != 8 || endDate.length() != 8) return;
        String startYear = startDate.substring(0, 4);
        String startMonth = startDate.substring(4, 6);
        String startDay = startDate.substring(6, 8);
        String endYear = endDate.substring(0, 4);
        String endMonth = endDate.substring(4, 6);
        String endDay = endDate.substring(6, 8);
        AnalysisAPI.getInstance().FramePara.strDecDataType = AnalysisAPI.DecDataType.TYPE_DATA_BLOCK;
        AnalysisAPI.getInstance().setDecDataType(AnalysisAPI.DecDataType.TYPE_DATA_BLOCK);
        this.dataType.setAction(DataType.READ);
        this.dataType.setValue(DataType.METER_DAILY_FREEZE_POWER_INFORMATION);
        this.dataType.setDeviceType(DataType.DEVICE_METER);
        String obis = getObisByPosition(0);
        String last = "FF800000";
        StringBuilder writeData = new StringBuilder();
        writeData.append("01");
        writeData.append("0204"); //结构体 4个元素

        writeData.append("0204");// 第一个数据为结构体 4个元素
        writeData.append("120008");//Unsigned16 2个字节 class id 08  data_time(时间费率class id)
        writeData.append("09060000010000FF"); //octet-string 6个字节 obis data_time(时间费率obis)
        writeData.append("0F02");// integer8 一个字节 02 属性
        writeData.append("120000");//

        writeData.append("090C");//第2个元素 //octet-string 12个字节
        writeData.append(String.format("%04x", Integer.parseInt(startYear) & 0xffff));
        writeData.append(getToHexadecimal(String.valueOf(startMonth)));
        writeData.append(getToHexadecimal(String.valueOf(startDay)));//日
        writeData.append("00");//星期
        writeData.append("00");//时
        writeData.append("00");//分
        writeData.append("00");//秒
        writeData.append(last);

        writeData.append("090C");//第3个元素 //octet-string 12个字节
        writeData.append(String.format("%04x", Integer.parseInt(endYear) & 0xffff));
        writeData.append(getToHexadecimal(String.valueOf(endMonth)));
        writeData.append(getToHexadecimal(String.valueOf(endDay)));//日
        writeData.append("00");//星期
        writeData.append("00");//时
        writeData.append("00");//分
        writeData.append("00");//秒
        writeData.append(last);

        writeData.append("0100");//selected values 数据类型 数组 01 00获取全部
        this.stitchingData(obis, Command.MODE_PARA, writeData.toString());
    }

    /**
     * 读取日冻结预付费信息
     *
     * @param startDate 开始时间
     * @param endDate   结束时间
     */
    public void readDayFreezePrepaid(@NonNull String startDate, @NonNull String endDate) {
        startDate = startDate.replace("-", "");
        endDate = endDate.replace("-", "");
        if (startDate.length() != 8 || endDate.length() != 8) return;
        String startYear = startDate.substring(0, 4);
        String startMonth = startDate.substring(4, 6);
        String startDay = startDate.substring(6, 8);
        String endYear = endDate.substring(0, 4);
        String endMonth = endDate.substring(4, 6);
        String endDay = endDate.substring(6, 8);
        AnalysisAPI.getInstance().FramePara.strDecDataType = AnalysisAPI.DecDataType.TYPE_DATA_BLOCK;
        AnalysisAPI.getInstance().setDecDataType(AnalysisAPI.DecDataType.TYPE_DATA_BLOCK);
        this.dataType.setAction(DataType.READ);
        this.dataType.setValue(DataType.METER_DAY_FREEZE_PREPAID);
        this.dataType.setDeviceType(DataType.DEVICE_METER);
        String obis = getObisByPosition(0);
        String last = "FF800000";
        StringBuilder writeData = new StringBuilder();
        writeData.append("01");
        writeData.append("0204"); //结构体 4个元素

        writeData.append("0204");// 第一个数据为结构体 4个元素
        writeData.append("120008");//Unsigned16 2个字节 class id 08  data_time(时间费率class id)
        writeData.append("09060000010000FF"); //octet-string 6个字节 obis data_time(时间费率obis)
        writeData.append("0F02");// integer8 一个字节 02 属性
        writeData.append("120000");//

        writeData.append("090C");//第2个元素 //octet-string 12个字节
        writeData.append(String.format("%04x", Integer.parseInt(startYear) & 0xffff));
        writeData.append(getToHexadecimal(String.valueOf(startMonth)));
        writeData.append(getToHexadecimal(String.valueOf(startDay)));//日
        writeData.append("00");//星期
        writeData.append("00");//时
        writeData.append("00");//分
        writeData.append("00");//秒
        writeData.append(last);

        writeData.append("090C");//第3个元素 //octet-string 12个字节
        writeData.append(String.format("%04x", Integer.parseInt(endYear) & 0xffff));
        writeData.append(getToHexadecimal(String.valueOf(endMonth)));
        writeData.append(getToHexadecimal(String.valueOf(endDay)));//日
        writeData.append("00");//星期
        writeData.append("00");//时
        writeData.append("00");//分
        writeData.append("00");//秒
        writeData.append(last);

        writeData.append("0100");//selected values 数据类型 数组 01 00获取全部
        this.stitchingData(obis, Command.MODE_PARA, writeData.toString());
    }

    /**
     * 读取月冻结预付费信息
     *
     * @param startDate 开始时间
     * @param endDate   结束时间
     */
    public void readMonthFreezePrepaid(@NonNull String startDate, @NonNull String endDate) {
        startDate = startDate.replace("-", "");
        endDate = endDate.replace("-", "");
        if (startDate.length() != 6 || endDate.length() != 6) return;
        String startYear = startDate.substring(0, 4);
        String startMonth = startDate.substring(4, 6);
        String endYear = endDate.substring(0, 4);
        String endMonth = endDate.substring(4, 6);
        AnalysisAPI.getInstance().FramePara.strDecDataType = AnalysisAPI.DecDataType.TYPE_DATA_BLOCK;
        AnalysisAPI.getInstance().setDecDataType(AnalysisAPI.DecDataType.TYPE_DATA_BLOCK);
        this.dataType.setAction(DataType.READ);
        this.dataType.setValue(DataType.METER_MONTH_FREEZE_PREPAID);
        this.dataType.setDeviceType(DataType.DEVICE_METER);
        String obis = getObisByPosition(0);
        String last = "FF800000";
        StringBuilder writeData = new StringBuilder();
        writeData.append("01");//selctive-acess  range-descriptor
        writeData.append("0204"); //结构体 4个元素

        writeData.append("0204");// 第一个数据为结构体 4个元素
        writeData.append("120008");//Unsigned16 2个字节 class id 08  data_time(时间费率class id)
        writeData.append("09060000010000FF"); //octet-string 6个字节 obis data_time(时间费率obis)
        writeData.append("0F02");// integer8 一个字节 02 属性
        writeData.append("120000");//

        writeData.append("090C");//第2个元素 //octet-string 12个字节
        writeData.append(String.format("%04x", Integer.parseInt(startYear) & 0xffff));
        writeData.append(getToHexadecimal(String.valueOf(startMonth)));
        writeData.append("00");//日
        writeData.append("00");//星期
        writeData.append("00");//时
        writeData.append("00");//分
        writeData.append("00");//秒
        writeData.append(last);

        writeData.append("090C");//第3个元素 //octet-string 12个字节
        writeData.append(String.format("%04x", Integer.parseInt(endYear) & 0xffff));
        writeData.append(getToHexadecimal(String.valueOf(endMonth)));
        writeData.append("00");//日
        writeData.append("00");//星期
        writeData.append("00");//时
        writeData.append("00");//分
        writeData.append("00");//秒
        writeData.append(last);

        writeData.append("0100");//selected values 数据类型 数组 01 00获取全部
        this.stitchingData(obis, Command.MODE_PARA, writeData.toString());
    }

    /**
     * 读取月冻结电能信息
     *
     * @param startDate 开始时间
     * @param endDate   结束时间
     */
    public void readMonthFreezePower(@NonNull String startDate, @NonNull String endDate) {
        startDate = startDate.replace("-", "");
        endDate = endDate.replace("-", "");
        if (startDate.length() != 6 || endDate.length() != 6) return;
        String startYear = startDate.substring(0, 4);
        String startMonth = startDate.substring(4, 6);
        String endYear = endDate.substring(0, 4);
        String endMonth = endDate.substring(4, 6);
        AnalysisAPI.getInstance().FramePara.strDecDataType = AnalysisAPI.DecDataType.TYPE_DATA_BLOCK;
        AnalysisAPI.getInstance().setDecDataType(AnalysisAPI.DecDataType.TYPE_DATA_BLOCK);
        this.dataType.setAction(DataType.READ);
        this.dataType.setValue(DataType.METER_MONTHLY_FREEZE_POWER_INFORMATION);
        this.dataType.setDeviceType(DataType.DEVICE_METER);
        String obis = getObisByPosition(0);
        String last = "FF800000";
        StringBuilder writeData = new StringBuilder();
        writeData.append("01");//selctive-acess  range-descriptor
        writeData.append("0204"); //结构体 4个元素

        writeData.append("0204");// 第一个数据为结构体 4个元素
        writeData.append("120008");//Unsigned16 2个字节 class id 08  data_time(时间费率class id)
        writeData.append("09060000010000FF"); //octet-string 6个字节 obis data_time(时间费率obis)
        writeData.append("0F02");// integer8 一个字节 02 属性
        writeData.append("120000");//

        writeData.append("090C");//第2个元素 //octet-string 12个字节
        writeData.append(String.format("%04x", Integer.parseInt(startYear) & 0xffff));
        writeData.append(getToHexadecimal(String.valueOf(startMonth)));
        writeData.append("01");//日
        writeData.append("00");//星期
        writeData.append("00");//时
        writeData.append("00");//分
        writeData.append("00");//秒
        writeData.append(last);

        writeData.append("090C");//第3个元素 //octet-string 12个字节
        writeData.append(String.format("%04x", Integer.parseInt(endYear) & 0xffff));
        writeData.append(getToHexadecimal(String.valueOf(endMonth)));
        writeData.append(getToHexadecimal(String.valueOf(DateTimeTool.getDaysByYearMonth(Integer.parseInt(endYear), Integer.parseInt(endMonth)))));
        writeData.append("00");//星期
        writeData.append(getToHexadecimal("23"));//时
        writeData.append(getToHexadecimal("59"));//分
        writeData.append(getToHexadecimal("59"));//秒
        writeData.append(last);

        writeData.append("0100");//selected values 数据类型 数组 01 00获取全部
        this.stitchingData(obis, Command.MODE_PARA, writeData.toString());
    }

    /**
     * 读取数据块
     */
    public void readDataBlock(String blockLabel) {
        AnalysisAPI.getInstance().FramePara.strDecDataType = AnalysisAPI.DecDataType.TYPE_DATA_BLOCK;
        AnalysisAPI.getInstance().setDecDataType(AnalysisAPI.DecDataType.TYPE_DATA_BLOCK);
        this.dataType.setAction(DataType.READ);
        this.dataType.setDeviceType(DataType.DEVICE_METER);
        this.stitchBlockData(blockLabel);
    }

    /**
     * 拼接 命令
     *
     * @param obis     硬件 obis
     * @param modePara 选择性访问参数 00 不带选择性 01 选择性
     */
    private void stitchingData(String obis, String modePara) {
        this.stitchingData(obis, modePara, null);
    }

    /**
     * 拼接 命令
     *
     * @param obis      硬件 obis
     * @param writeData 代写入数据
     */
    private void stitchingData(String obis, String modePara, String writeData) {
        String targetAddress = null, sourceAddress = null;
        if (writeData == null) writeData = "";
        if (this.dataType.getDeviceType().equals(DataType.DEVICE_LCU))
            targetAddress = Command.LCU_ADDRESS;
        else if (this.dataType.getDeviceType().equals(DataType.DEVICE_METER))
            targetAddress = Command.METER_ADDRESS;
        if (this.dataType.getAction().equals(DataType.READ))
            sourceAddress = Command.READ_ADDRESS;
        else if (this.dataType.getAction().equals(DataType.WRITE))
            sourceAddress = Command.WRITE_ADDRESS;
        else if (this.dataType.getAction().equals(DataType.ACTION))
            sourceAddress = Command.ACTION_ADDRESS;
        if (targetAddress == null || sourceAddress == null) {
            if (this.callback != null) {
                this.callback.onError(HexError.NO_ADDRESS_ERROR);
            }
            Log.e("拼接蓝牙指令:", "targetAddress=" + targetAddress + "||sourceAddress=" + sourceAddress);
            return;
        }
        obis = getToHexadecimal(obis) + modePara; //转换格式
        String strLen = "A0" + targetAddress + sourceAddress + obis + writeData;
        int len = (strLen.length() + 10) / 2;
        strLen = String.format("%02x", len & 0xff);// 字节长度
        String oneCRC = CRCUtil.getCRC16("A0" + strLen + targetAddress);
        String twoCRC = CRCUtil.getCRC16("A0" + strLen + targetAddress + oneCRC + sourceAddress + obis + writeData);
        StringBuilder commandBuilder = new StringBuilder();
        commandBuilder.append(Command.HEAD);
        commandBuilder.append("A0");
        commandBuilder.append(strLen);
        commandBuilder.append(targetAddress);
        commandBuilder.append(oneCRC);
        commandBuilder.append(sourceAddress);
        commandBuilder.append(obis);
        commandBuilder.append(writeData);
        commandBuilder.append(twoCRC);
        commandBuilder.append(Command.HEAD);
        Log.e("待发送蓝牙完整指令:", commandBuilder.toString().toUpperCase());

        WriteTask writeTask = new WriteTask();
        writeTask.execute(commandBuilder.toString().toUpperCase());
    }

    /**
     * 数据块 命令 拼接
     *
     * @param blockData 数据块 C00241 + 数据块序列号(4个字节)
     */
    private void stitchBlockData(String blockData) {
        String targetAddress = null, sourceAddress = null;
        if (this.dataType.getDeviceType().equals(DataType.DEVICE_LCU))
            targetAddress = Command.LCU_ADDRESS;
        else if (this.dataType.getDeviceType().equals(DataType.DEVICE_METER))
            targetAddress = Command.METER_ADDRESS;
        if (this.dataType.getAction().equals(DataType.READ))
            sourceAddress = Command.READ_BLOCK;
        else if (this.dataType.getAction().equals(DataType.WRITE))
            sourceAddress = Command.WRITE_ADDRESS;
        else if (this.dataType.getAction().equals(DataType.ACTION))
            sourceAddress = Command.ACTION_ADDRESS;
        if (targetAddress == null || sourceAddress == null) return;
        blockData = Command.DATA_BLOCK + blockData;
        String strLen = "A0" + targetAddress + sourceAddress + blockData;
        int len = (strLen.length() + 10) / 2;
        strLen = String.format("%02x", len & 0xff);// 字节长度
        String oneCRC = CRCUtil.getCRC16("A0" + strLen + targetAddress);
        String twoCRC = CRCUtil.getCRC16("A0" + strLen + targetAddress + oneCRC + sourceAddress + blockData);
        StringBuilder commandBuilder = new StringBuilder();
        commandBuilder.append(Command.HEAD);
        commandBuilder.append("A0");
        commandBuilder.append(strLen);
        commandBuilder.append(targetAddress);
        commandBuilder.append(oneCRC);
        commandBuilder.append(sourceAddress);
        commandBuilder.append(blockData);
        commandBuilder.append(twoCRC);
        commandBuilder.append(Command.HEAD);
        Log.e("待发送蓝牙完整指令:", commandBuilder.toString().toUpperCase() + "||block=" + blockData);
        new WriteTask().execute(commandBuilder.toString().toUpperCase());
    }

    public String getCommand(String commandType) {
        switch (commandType) {
            case "READ_LCU_TIME":
                readLCUTime();
                break;
        }
        return "";
    }

    public class CommandType {
        public static final String READ_LCU_TIME = "READ_LCU_TIME";
        public static final String READ_LCU_STATE = "READ_LCU_STATE";
        public static final String READ_LCU_PERIOD = "READ_LCU_PERIOD";
    }

    /**
     * 解析OBIS
     *
     * @param obis "1#1.0.144.129.0.255#2";
     * @return 转换 16进制
     */

    private String getToHexadecimal(String obis) {
        obis = obis.replace(" ", "");
        if (obis.length() == 0) {
            return "";
        }
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

    //classId obis attr 命令
    private static class Command {
        private final static String HEAD = "7E";
        private final static String LCU_ADDRESS = "0002FEFF0354"; //LCU目标地址
        private final static String WRITE_ADDRESS = "E6E600C101C100"; //写入
        private final static String READ_ADDRESS = "E6E600C001C100";//  读取
        private final static String READ_BLOCK = "E6E600";//读取块
        private final static String ACTION_ADDRESS = "E6E600C301C100";//指令action
        private final static String METER_ADDRESS = "0002FEFF0332";//电表目标地址

        private final static String NO_MODE_PARA = "00"; //不带选择性参数
        private final static String MODE_PARA = "01"; //带选择性参数

        private final static String DATA_BLOCK = "C00241"; //数据块命令 前缀

        //蓝牙LCU start
        private final static String LCU_AISLE_ONE_DIS = "70#0.128.96.3.10.255#1";//LCU1断开 通道1  integer 数据类型
        private final static String LCU_AISLE_TWO_DIS = "70#0.129.96.3.10.255#1"; //LCU2断开 通道2
        private final static String LCU_AISLE_THREE_DIS = "70#0.130.96.3.10.255#1";//LCU3断开 通道3
        private final static String LCU_AISLE_FOUR_DIS = "70#0.131.96.3.10.255#1";//LCU4断开 通道4

        private final static String LCU_AISLE_ONE_CON = "70#0.128.96.3.10.255#2"; // LCU1闭合
        private final static String LCU_AISLE_TWO_CON = "70#0.129.96.3.10.255#2";// LCU2闭合
        private final static String LCU_AISLE_THREE_CON = "70#0.130.96.3.10.255#2";// LCU3闭合
        private final static String LCU_AISLE_FOUR_CON = "70#0.131.96.3.10.255#2";// LCU4闭合

        private final static String LCU_ONE_STATE = "70#0.128.96.3.10.255#2"; //lcu1 物理状态 boolean  0: disconnected 1: connected
        private final static String LCU_TWO_STATE = "70#0.129.96.3.10.255#2"; //lcu2 物理状态 boolean
        private final static String LCU_THREE_STATE = "70#0.130.96.3.10.255#2"; //lcu3 物理状态 boolean
        private final static String LCU_FOUR_STATE = "70#0.131.96.3.10.255#2"; //lcu4 物理状态 boolean

        private final static String LCU_LAST_COMMAND_ONE = "70#0.128.96.3.10.255#3"; //lcu1 最后一次命令 enum  00: no command   01: connected
        private final static String LCU_LAST_COMMAND_TWO = "70#0.129.96.3.10.255#3"; //lcu2 最后一次命令 enum
        private final static String LCU_LAST_COMMAND_THREE = "70#0.130.96.3.10.255#3"; //lcu3 最后一次命令 enum
        private final static String LCU_LAST_COMMAND_FOUR = "70#0.131.96.3.10.255#3"; //lcu4 最后一次命令 enum

        private final static String LCU_WRITE_TIME_PERIOD_ONE = "1#0.0.146.129.27.255#2"; //设置LCU1 时段 "01000092811BFF02" + "00"; //"
        private final static String LCU_WRITE_TIME_PERIOD_TWO = "1#0.0.146.129.28.255#2"; //设置LCU2 时段
        private final static String LCU_WRITE_TIME_PERIOD_THREE = "1#0.0.146.129.29.255#2"; //设置LCU3 时段
        private final static String LCU_WRITE_TIME_PERIOD_FOUR = "1#0.0.146.129.30.255#2"; //设置LCU4 时段

        private final static String LCU_READ_WRITE_PULSE = "1#1.0.31.128.48.255#2";//秒脉冲和电能切换
        private final static String LCU_READ_WRITE_CHANNEL = "1#0.0.96.2.192.255#2";//LCU 继电器通道数
        private final static String LCU_WRITE_COMMUNICATION = "1#1.0.31.128.106.255#2";//表记直接通讯
        private final static String LCU_READ_INTERNAL_CONTROL_VERSION = "1#0.0.96.1.146.255#2";//内控测试版本号
        private final static String LCU_READ_WRITE_TEMPLATE = "1#1.0.31.128.82.255#2";//写模板
        //蓝牙LCU end

        private final static String WRITE_TIME = "8#0.0.1.0.0.255#2"; //设置时间 读时间 写时间
        private final static String READ_METER_NUMBER = "1#0.0.96.1.0.255#2"; //ASCS 读表

        //蓝牙电表start
        private final static String METER_CANCEL_ALARM = "1#1.0.140.130.0.255#2";//取消声音报警
        private final static String METER_EMERGENCY_OVERDRAFT = "1#1.0.140.130.0.255#2";//透支
        private final static String METER_BALANCE = "3#1.0.140.129.0.255#2";//电表余额
        private final static String METER_READ_REALY_REASON = "70#0.0.96.3.10.255#5";//读继电器原因
        private final static String METER_POSITIVE_ACTIVE = "3#1.0.1.8.0.255#2";//
        private final static String METER_MONTHLY_FREEZE_POWER_INFORMATION = "7#0.0.98.1.0.255#2";//月冻结电能信息
        private final static String METER_DAILY_FREEZE_POWER_INFORMATION = "7#0.0.98.2.0.255#2";//日冻结电能信息
        private final static String METER_MONTH_FREEZE_PREPAID = "7#0.0.99.1.0.255#2"; //预付费月冻结信息
        private final static String METER_DAY_FREEZE_PREPAID = "7#0.0.99.2.0.255#2";//预付费日冻结信息
        // Struct_Billing 7#0.0.98.2.0.255#2 月冻结预付费信息
        // 7#0.0.98.2.0.255#2 日冻结预付费信息
        private final static String WRITE_TOKEN = "1#1.0.129.129.2.255#2";//token 写入 Octs_string
        //蓝牙电表end
    }
}
