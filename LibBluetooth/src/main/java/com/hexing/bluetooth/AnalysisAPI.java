package com.hexing.bluetooth;

import android.support.annotation.NonNull;
import android.util.Log;

import com.hexing.bluetooth.HexCallback;
import com.hexing.bluetooth.bean.LcuStateBean;
import com.hexing.bluetooth.bean.MeterFreezeBean;
import com.hexing.bluetooth.bean.MeterPrepaidBean;
import com.hexing.bluetooth.protocol.comm.CommBLEBlueTooth;
import com.hexing.bluetooth.protocol.icomm.ICommAction;
import com.hexing.bluetooth.protocol.model.CommPara;
import com.hexing.bluetooth.protocol.model.HXFramePara;
import com.hexing.bluetooth.services.CommServer;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 硬件返回数据 解析 API
 * Created by caibinglong
 * on 2017/7/12.
 */

public class AnalysisAPI {
    private final String TAG = AnalysisAPI.class.getName();
    private String decDataType;
    private static AnalysisAPI instance;
    private HexCallback callback;
    public HXFramePara FramePara;
    public CommServer commDlmsServer;
    public CommPara cPara;
    public ICommAction iComm;
    private int index = 0; //数据块下标
    private StringBuilder dataBlock = new StringBuilder();//数据块数据

    public static AnalysisAPI getInstance() {
        if (instance == null) {
            synchronized (AnalysisAPI.class) {
                if (instance == null) {
                    instance = new AnalysisAPI();
                }
            }
        }
        return instance;
    }

    /**
     * 初始化
     */
    public void init() {
        // DLMS 协议通讯参数
        commDlmsServer = new CommServer();
        cPara = new CommPara();
        iComm = new CommBLEBlueTooth();
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

    public void addListener(HexCallback callback) {
        this.callback = callback;
    }

    /**
     * 设置 必需参数
     *
     * @param strMeterNo    电表号
     * @param deviceAddress 蓝牙地址
     */
    public void setFramePara(String strMeterNo, String deviceAddress) {
        FramePara.strMeterNo = strMeterNo;
        cPara.ComName = deviceAddress;
        this.iComm = this.commDlmsServer.OpenDevice(this.cPara, this.iComm);
    }

    public class DecDataType {
        public static final String TYPE_BOOL = "Bool";
        public static final String TYPE_ARRAY_DD = "Array_dd";
        public static final String TYPE_OCTS_STRING = "Octs_string";
        public static final String TYPE_U8 = "U8";
        public static final String TYPE_INT32 = "Int32";
        public static final String TYPE_ASCS = "Ascs";
        public static final String TYPE_INT8 = "INT8";
        public static final String TYPE_OCTS_ASCII = "Octs_ascii";
        public static final String TYPE_OCTS_DATETIME = "Octs_datetime";
        public static final String TYPE_STRUCT_BILLING = "Struct_Billing";
        public static final String TYPE_LCU_ACTION = "TYPE_LCU_ACTION";

        public static final String TYPE_FREEZE = ""; //日月冻结
        public static final String TYPE_DATA_BLOCK = "data_block";// 数据块
        public static final String TYPE_PREPAID_FREEZE = ""; //预付费冻结

        public static final String SUCCESS = "4100";
        public static final String BLOCK_FINISH = "41FF";
        public static final String FAIL = "4101";
        public static final String NORMAL = "C401"; //普通
        public static final String DATA_BLOCK = "C402";//数据块
        public static final String LIST = "C403";//list
    }

    public void setDecDataType(String type) {
        this.decDataType = type;
    }

    public String getDecDataType() {
        return this.decDataType;
    }

    /**
     * 解析 收到的 数据
     *
     * @param data data
     * @return str
     */
    public void analysisReceiverData(String data) {
        this.iComm.setORIData(data);
        switch (this.decDataType) {
            case DecDataType.TYPE_BOOL:
                this.analysisBoolData(data);
                break;
            case DecDataType.TYPE_LCU_ACTION:
                this.analysisBoolAction(data);
                break;
            case DecDataType.TYPE_ARRAY_DD:
                this.analysisArray(data);
                break;
            case DecDataType.TYPE_OCTS_STRING:
                this.analysisData(data);
                break;
            case DecDataType.TYPE_U8:
                this.analysisData(data);
                break;
            case DecDataType.TYPE_INT32:
                this.analysisData(data);
                break;
            case DecDataType.TYPE_ASCS:
                this.analysisData(data);
                break;
            case DecDataType.TYPE_OCTS_ASCII:
                this.analysisData(data);
                break;
            case DecDataType.TYPE_OCTS_DATETIME:
                this.analysisData(data);
                break;
            case DecDataType.TYPE_STRUCT_BILLING:
                this.analysisData(data);
                break;
            case DecDataType.TYPE_FREEZE:
                this.analysisData(data);
                break;
            case DecDataType.TYPE_DATA_BLOCK:
                this.analysisData(data);
                break;
        }
    }

    /**
     * 写 token 返回解析
     *
     * @param data 蓝牙返回数据
     */
    private void writeTokenAnalysis(@NonNull String data) {
        Object strResult = null;
        //写token  暂时 特别处理 只拿结构 U8 一个字节
        //02031100
        if (data.length() > 44) {
            strResult = data.substring(42, 44); //从第42个字符开始取值到第44个字符
        }
        if (strResult != null && strResult.toString().length() > 0) {
            strResult = HexClientAPI.getInstance().getErrorString(strResult.toString());
            if (this.callback != null) {
                this.callback.onAnalysis(strResult);
            }
        }
    }
    /**
     * writeNS 返回解析
     * @param data 蓝牙返回
     * @return TRUE flase
     */
   private void writeNSAnalysis(@NonNull String data){
       boolean bool;
       String ansNS = "7E000588014E5300D5";
       if (data == ansNS){
           bool = true;
           if (this.callback != null) {
               this.callback.onAnalysis(bool);
           }
       }
   }


    /**
     * writeID 返回解析
     * @param data 蓝牙返回
     * @return TRUE flase
     */
    private void writeIDAnalysis(@NonNull String data){
        boolean bool;
        String ansID = "7E00058801494400E9";
        if (data == ansID){
            bool = true;
            if (this.callback != null) {
                this.callback.onAnalysis(bool);
            }
        }
    }


    /**
     * CT通讯返回解析
     * @param data 蓝牙返回
     */


    /**
     * 数据解析
     *
     * @param data 蓝牙表或蓝牙LCU 返回数据
     */
    public void analysisData(@NonNull String data) {
        Object strResult;
        switch (CommandAPI.getInstance().getDataType().getValue()) {
            case CommandAPI.DataType.ACTION:
                strResult = this.commDlmsServer.Action(this.FramePara, this.iComm);
                if (this.callback != null) {
                    this.callback.onAnalysis(strResult);
                }
            case CommandAPI.DataType.METER_WRITE_TOKEN:
                //写token  暂时 特别处理 只拿结构 U8 一个字节
                writeTokenAnalysis(data);
                break;
            case CommandAPI.DataType.METER_DAILY_FREEZE_POWER_INFORMATION:
            case CommandAPI.DataType.METER_MONTHLY_FREEZE_POWER_INFORMATION:
                //冻结
                powerAction(data);
                break;
            case CommandAPI.DataType.METER_MONTH_FREEZE_PREPAID:
            case CommandAPI.DataType.METER_DAY_FREEZE_PREPAID:
                //预付费冻结
                prepaidAction(data);
                break;
            default:
                strResult = this.commDlmsServer.readReceiver(this.FramePara, this.iComm);
                if (strResult == null || strResult.toString().indexOf("7E") > 0) {
                    strResult = "";
                }
                if (this.callback != null) {
                    this.callback.onAnalysis(strResult);
                }
                break;
        }
    }

    /**
     * 数据块 冻结 处理
     * data 有时间字段和 正向有功 反向有功
     *
     * @param data 蓝牙返回数据
     */
    private void powerAction(String data) {
        Object strResult;
        //冻结电能信息
//7EA02A030002FEFFDA865BE6E700C401410001010202090C07E10701FF000000008000010500000528FE977E
        if ((data.contains(DecDataType.SUCCESS) && data.contains(DecDataType.NORMAL)) || data.contains(DecDataType.BLOCK_FINISH)) {
            //普通数据
            this.index = 0;
            //解析数据 待完成
            if (data.indexOf("0203") > 0) {
                dataBlock.append(data.substring(data.indexOf("0203"), data.length()));
            } else {
                dataBlock.append(data);
            }
            strResult = this.analysisFreezeArray(dataBlock.toString());
            dataBlock = new StringBuilder();
            if (this.callback != null) {
                this.callback.onAnalysis(strResult);
            }
        } else if (data.contains(DecDataType.SUCCESS) && data.contains(DecDataType.DATA_BLOCK)) {
            if (this.index == 0) {
                dataBlock.append(data.substring(data.indexOf(DecDataType.SUCCESS) + 16, data.length() - 6));
            } else {
                dataBlock.append(data.substring(data.indexOf(DecDataType.SUCCESS) + 12, data.length() - 6));
            }
            this.index = this.index + 1;
            String blockLabel = data.substring(data.indexOf(DecDataType.SUCCESS) + 4, data.indexOf(DecDataType.SUCCESS) + 12);
            Log.e(TAG, "blockLabel=" + blockLabel);
            Log.e(TAG, "dataBlock=" + dataBlock.toString());
            CommandAPI.getInstance().readDataBlock(blockLabel);
        } else {
            this.index = 0;
            dataBlock = new StringBuilder();
            strResult = "";
            if (this.callback != null) {
                this.callback.onAnalysis(strResult);
            }
        }
    }

    /**
     * 数据块 预付费冻结 处理
     * data 有时间字段和 电表余额
     *
     * @param data 蓝牙返回数据
     */
    private void prepaidAction(String data) {
        Object strResult;
        if ((data.contains(DecDataType.SUCCESS) && data.contains(DecDataType.NORMAL)) || data.contains(DecDataType.BLOCK_FINISH)) {
            //普通数据
            this.index = 0;
            //解析数据 待完成
            if (data.indexOf("0202") > 0) {
                dataBlock.append(data.substring(data.indexOf("0202"), data.length()));
            } else {
                dataBlock.append(data);
            }
            strResult = this.analysisPrepaidArray(dataBlock.toString());
            dataBlock = new StringBuilder();
            if (this.callback != null) {
                this.callback.onAnalysis(strResult);
            }
        } else if (data.contains(DecDataType.SUCCESS) && data.contains(DecDataType.DATA_BLOCK)) {
            //含数据块
            if (this.index == 0) {
                dataBlock.append(data.substring(data.indexOf(DecDataType.SUCCESS) + 20, data.length() - 6));
            } else {
                dataBlock.append(data.substring(data.indexOf(DecDataType.SUCCESS) + 12, data.length() - 6));
            }
            this.index = this.index + 1;
            String blockLabel = data.substring(data.indexOf(DecDataType.SUCCESS) + 4, data.indexOf(DecDataType.SUCCESS) + 12);
            Log.e(TAG, "blockLabel=" + blockLabel);
            Log.e(TAG, "dataBlock=" + dataBlock.toString());
            CommandAPI.getInstance().readDataBlock(blockLabel);
        } else {
            this.index = 0;
            dataBlock = new StringBuilder();
            strResult = "";
            if (this.callback != null) {
                this.callback.onAnalysis(strResult);
            }
        }
    }

    public String analysisEnumData(@NonNull String data) {
        data = data.substring(0, data.length() - 6);
        data = data.substring(data.length() - 2, data.length());
        if (data.equals("00")) {
            return "No Command";
        }
        return "Connect Command";
    }

    /**
     * 写入数据 返回 成功失败
     *
     * @param data 返回数据
     * @return TRUE false
     */
    public void analysisBoolData(@NonNull String data) {
        data = data.substring(0, data.length() - 6);
        boolean bool;
        if (data.lastIndexOf("4100") > -1) {
            bool = true;
        } else {
            bool = false;
        }
        if (this.callback != null) {
            this.callback.onAnalysis(bool);
        }
    }

    /**
     * 读取物理状态
     *
     * @param data 返回数据
     * @return TRUE false
     */
    public void analysisBoolAction(@NonNull String data) {
        data = data.substring(0, data.length() - 6);
        boolean bool;
        if (data.lastIndexOf("41000300") > -1) {
            bool = false; //断开
        } else {
            bool = true;//闭合 41000301
        }
        if (this.callback != null) {
            this.callback.onAnalysis(bool);
        }
    }

    /**
     * 读取物理状态
     *
     * @param data 返回数据
     * @return TRUE false
     */
    public void analysisBool(@NonNull String data) {
        data = data.substring(0, data.length() - 6);
        boolean bool;
        if (data.lastIndexOf("41000") > -1) {
            bool = true; //成功
        } else {
            bool = false;//失败
        }
        if (this.callback != null) {
            this.callback.onAnalysis(bool);
        }
    }

    /**
     * 解析数组 LCU 时段控制操作
     *
     * @param data data
     * @return
     */
    public void analysisArray(@NonNull String data) {
        String[] attrs = data.split("0203"); //结构体 3个字节
        List<LcuStateBean> list = new ArrayList<>();
        LcuStateBean bean;
        String temp;
        for (String item : attrs) {
            Log.e("数据解析字符串--》", item);
            if (!item.contains("7EA0")) {
                bean = new LcuStateBean();
                temp = Integer.valueOf(item.substring(2, 4), 16).toString();
                bean.hour = temp.length() == 1 ? "0" + temp : temp;
                temp = Integer.valueOf(item.substring(6, 8), 16).toString();
                bean.minute = temp.length() == 1 ? "0" + temp : temp;
                bean.action = Integer.valueOf(item.substring(10, 12), 16).toString();
                list.add(bean);
                Log.e("数据解析字符串--》", bean.toString());
            }
        }
        if (this.callback != null) {
            this.callback.onAnalysis(list);
        }
    }

    /**
     * 解析数组 蓝牙表冻结数据
     *
     * @param data data
     * @return obj
     */
    public Object analysisFreezeArray(@NonNull String data) {
        //090C07E10701FF000000008000010500000528FE977E
        if (data.length() == 0) return new ArrayList<>();
        Pattern p = Pattern.compile("7E+\\w+7E");
        Matcher m = p.matcher(data);
        if (m.matches()) { //包含7E开头 7E 结尾 去除尾部 6个字符串
            data = data.substring(0, data.length() - 6);
        }
        Log.e(TAG, "FreezeArray待解析-" + data);
        String[] attrs = data.split("0203"); //结构体 3个字节
        List<MeterFreezeBean> list = new ArrayList<>();
        MeterFreezeBean bean;
        String temp;
        for (String item : attrs) {
            Log.e("数据解析字符串--》", item);
            if (!item.contains("7EA0") && item.length() > 0) {
                bean = new MeterFreezeBean();
                try {
                    item = item.substring(4, item.length());
                    bean.year = Integer.valueOf(item.substring(0, 4), 16).toString();
                    temp = Integer.valueOf(item.substring(4, 6), 16).toString();
                    bean.month = temp.length() == 1 ? "0" + temp : temp;
                    temp = Integer.valueOf(item.substring(6, 8), 16).toString();
                    bean.day = temp.length() == 1 ? "0" + temp : temp;
                    bean.positive = Integer.valueOf(item.substring(24 + 2, 24 + 2 + 8), 16);
                    bean.reverse = Integer.valueOf(item.substring(24 + 2 + 10, 24 + 2 + 10 + 8), 16);
                    Log.e("数据解析字符串--》", bean.toString());
                    list.add(bean);
                } catch (Exception ex) {
                    Log.e("数据解析错误--》", ex.getMessage());
                }
            }
        }
        return list;
    }

    /**
     * 解析数组 蓝牙表预付费冻结
     *
     * @param data data
     * @return Object
     */
    public Object analysisPrepaidArray(@NonNull String data) {
        if (data.length() == 0) return new ArrayList<>();
        Pattern p = Pattern.compile("7E+\\w+7E");
        Matcher m = p.matcher(data);
        if (m.matches()) { //包含7E开头 7E 结尾 去除尾部 6个字符串
            data = data.substring(0, data.length() - 6);
        }
        Log.e(TAG, "FreezeArray待解析-" + data);
        String[] attrs = data.split("0202"); //结构体 2个元素
        List<MeterPrepaidBean> list = new ArrayList<>();
        MeterPrepaidBean bean;
        for (String item : attrs) {
            Log.e("数据解析字符串--》", item);
            if (!item.contains("7EA0") && item.length() > 0) {
                bean = new MeterPrepaidBean();
                try {
                    item = item.substring(4, item.length()); //去除 090C
                    bean.year = Integer.valueOf(item.substring(0, 4), 16).toString();
                    bean.month = Integer.valueOf(item.substring(4, 6), 16).toString();
                    bean.day = Integer.valueOf(item.substring(6, 8), 16).toString();
                    bean.balance = Integer.valueOf(item.substring(24 + 2, 24 + 2 + 8), 16).toString();
                    Log.e("数据解析字符串--》", bean.toString());
                    list.add(bean);
                } catch (Exception ex) {
                    Log.e("数据解析错误--》", ex.getMessage());
                }
            }
        }
        return list;
    }
}
