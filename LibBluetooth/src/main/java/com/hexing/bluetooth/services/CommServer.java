package com.hexing.bluetooth.services;

import android.util.Log;

import com.hexing.bluetooth.protocol.bll.dlmsService;
import com.hexing.bluetooth.protocol.bll.dlmsService.DataType;
import com.hexing.bluetooth.protocol.dlms.HXHdlcDLMS;
import com.hexing.bluetooth.protocol.icomm.ICommAction;
import com.hexing.bluetooth.protocol.iprotocol.IProtocol;
import com.hexing.bluetooth.protocol.model.CommPara;
import com.hexing.bluetooth.protocol.model.HXFramePara;

import java.io.IOException;
import java.util.List;

public class CommServer implements IcommServer {

    IProtocol DLMSProtocol = new HXHdlcDLMS();

    @Override
    public ICommAction OpenDevice(CommPara cpara, ICommAction commDevice) {
        ICommAction DevOpen = null;
        try {
            boolean blOpen = false;
            blOpen = commDevice.OpenDevice(cpara);
            if (blOpen) {
                DevOpen = commDevice;
            }
        } catch (Exception e) {
        }
        return DevOpen;
    }

    @Override
    public boolean Close(ICommAction commDevice) {
        boolean blClose = false;
        try {
            blClose = commDevice.Close();
        } catch (Exception e) {
        }
        return blClose;
    }

    public static byte[] stringArrayToByteArray(String[] strAryHex, int nLen) {
        if (strAryHex == null)
            return null;
        if (strAryHex.length < nLen) {
            nLen = strAryHex.length;
        }

        byte[] btAryHex = new byte[nLen];

        try {
            for (int i = 0; i < nLen; i++) {
                btAryHex[i] = (byte) Integer.parseInt(strAryHex[i], 16);
            }
        } catch (NumberFormatException e) {

        }

        return btAryHex;
    }

    private static byte uniteBytes(String src0, String src1) {
        byte b0 = Byte.decode("0x" + src0).byteValue();
        b0 = (byte) (b0 << 4);
        byte b1 = Byte.decode("0x" + src1).byteValue();
        byte ret = (byte) (b0 | b1);
        return ret;
    }

    public String strTakeNum(String str) {
        str = str.trim();
        String str2 = "";
        if (!"".equals(str)) {
            for (int i = 0; i < str.length(); i++) {
                if (str.charAt(i) >= 48 && str.charAt(i) <= 57) {
                    str2 += str.charAt(i);
                }
            }
        }
        return str2;
    }

    public byte[] hexStr2Bytes(String src) {

        int m = 0, n = 0;
        int l = src.length() / 2;
        byte[] ret = new byte[l];
        for (int i = 0; i < l; i++) {
            m = i * 2 + 1;
            n = m + 1;
            ret[i] = uniteBytes(src.substring(i * 2, m), src.substring(m, n));
        }
        return ret;
    }

    public byte[] GetCommuniteMeterAddr(String MeterAddr) {
        byte[] btyResult = null;
        String strData = "";
        try {
            MeterAddr = strTakeNum(MeterAddr);
            if (MeterAddr.length() < 9) {
                strData = String
                        .format("%08x", Integer.parseInt(MeterAddr, 10));
                btyResult = hexStr2Bytes(strData);
            } else {
                strData = MeterAddr.substring(MeterAddr.length() - 9,
                        MeterAddr.length());

            }

            strData = String.format("%08x", Integer.parseInt(strData, 10));

            btyResult = hexStr2Bytes(strData);
        } catch (Exception ex) {
        }
        return btyResult;
    }


    char HexChar(char c) {
        if ((c >= '0') && (c <= '9'))
            return (char) (c - 0x30);
        else if ((c >= 'A') && (c <= 'F'))
            return (char) (c - 'A' + 10);
        else if ((c >= 'a') && (c <= 'f'))
            return (char) (c - 'a' + 10);
        else
            return 0x10;
    }

    public byte[] Str2Hex(String str) {
        int t, t1;
        int rlen = 0, len = str.length();
        final byte[] byteArray = new byte[str.length()];
        // data.SetSize(len/2);
        for (int i = 0; i < len; ) {
            char l, h = str.charAt(i);
            if (h == ' ') {
                i++;
                continue;
            }
            i++;
            if (i >= len)
                break;
            l = str.charAt(i);
            t = HexChar(h);
            t1 = HexChar(l);
            if ((t == 16) || (t1 == 16))
                break;
            else
                t = t * 16 + t1;
            i++;
            byteArray[rlen] = (byte) t;
            rlen++;
        }

        final byte[] byteArray1 = new byte[rlen];
        System.arraycopy(byteArray, 0, byteArray1, 0, rlen);

        return byteArray1;

    }

    public DataType turnType(String strType) {
        DataType dtResult = null;
        if (strType.equals("Octs_ascii")) {
            dtResult = DataType.Octs_ascii;
        } else if (strType.equals("Octs_datetime")) {
            dtResult = DataType.Octs_datetime;
        } else if (strType.equals("Octs_hex")) {
            dtResult = DataType.Octs_hex;
        } else if (strType.equals("U8")) {
            dtResult = DataType.U8;
        } else if (strType.equals("U8_hex")) {
            dtResult = DataType.U8_hex;
        } else if (strType.equals("U16")) {
            dtResult = DataType.U16;
        } else if (strType.equals("U32")) {
            dtResult = DataType.U32;
        } else if (strType.equals("U32_hex")) {
            dtResult = DataType.U32_hex;
        } else if (strType.equals("Ascs")) {
            dtResult = DataType.Ascs;
        } else if (strType.equals("Octs_string")) {
            dtResult = DataType.Octs_string;
        } else if (strType.equals("Array_dd")) {
            dtResult = DataType.Array_dd;
        } else if (strType.equals("Bool")) {
            dtResult = DataType.Bool;
        } else if (strType.equals("Struct_Billing")) {
            dtResult = DataType.Struct_Billing;
        } else if (strType.equals("Int32")) {
            dtResult = DataType.Int32;
        } else if (strType.equals("enumm")) {
            dtResult = DataType.enumm;
        } else if (strType.equals("Int16")) {
            dtResult = DataType.Int16;
        }
        return dtResult;
    }

    @Override
    public String Read(HXFramePara paraModel, ICommAction commDevice) {
        String strValue = "";
        try {
            paraModel.decDataType = this.turnType(paraModel.strDecDataType);
            paraModel.OBISattri = dlmsService.fnChangeOBIS(paraModel.OBISattri);
            paraModel.sysTitleC = Str2Hex(paraModel.StrsysTitleC);
            if (paraModel.CommDeviceType.equals("Optical")) {
                byte[] bDestAddr = {0x03};
                paraModel.DestAddr = bDestAddr;
            } else if (paraModel.CommDeviceType.equals("RF")) {
                paraModel.DestAddr = GetCommuniteMeterAddr(paraModel.strMeterNo);

            }
            byte[] recbyt = DLMSProtocol.Read(paraModel, commDevice);
            if (paraModel.decDataType == DataType.Struct_Billing) {
                List<String> listStrValue = dlmsService.TranBillingCode(recbyt, paraModel.listTranXADRAssist);

                for (int i = 0; i < listStrValue.size(); i++) {
                    strValue += listStrValue.get(i) + "&";
                }
                strValue = strValue.substring(0, strValue.length() - 1);
            } else {
                strValue = dlmsService.TranXADRCode(recbyt, paraModel.decDataType);
            }
        } catch (Exception e) {
            Log.e("错误日志-》", e.toString());
        }
        return strValue;
    }

    public byte[] sendByte(HXFramePara paraModel, ICommAction commDevice) {
        byte[] recbyt = null;
        try {
            paraModel.decDataType = this.turnType(paraModel.strDecDataType);
            paraModel.OBISattri = dlmsService.fnChangeOBIS(paraModel.OBISattri);
            paraModel.sysTitleC = Str2Hex(paraModel.StrsysTitleC);
            if (paraModel.CommDeviceType.equals("Optical")) {
                byte[] bDestAddr = {0x03};
                paraModel.DestAddr = bDestAddr;
            } else if (paraModel.CommDeviceType.equals("RF")) {
                paraModel.DestAddr = GetCommuniteMeterAddr(paraModel.strMeterNo);
            }
            recbyt = DLMSProtocol.convertSendLowBluetooth(paraModel, commDevice);
        } catch (Exception e) {

        }
        return recbyt;
    }

    /**
     * 低功耗 蓝牙数据 接收 解析
     *
     * @param paraModel  paraModel
     * @param commDevice commDevice
     * @return str
     */
    public String readReceiver(HXFramePara paraModel, ICommAction commDevice) {
        String strValue = "";
        try {
            paraModel.decDataType = this.turnType(paraModel.strDecDataType);
            paraModel.OBISattri = dlmsService.fnChangeOBIS(paraModel.OBISattri);
            paraModel.sysTitleC = Str2Hex(paraModel.StrsysTitleC);
            if (paraModel.CommDeviceType.equals("Optical")) {
                byte[] bDestAddr = {0x03};
                paraModel.DestAddr = bDestAddr;
            } else if (paraModel.CommDeviceType.equals("RF")) {
                paraModel.DestAddr = GetCommuniteMeterAddr(paraModel.strMeterNo);
            }
            byte[] recByt = DLMSProtocol.convertReceiverLowBluetooth(paraModel, commDevice);
            if (recByt != null)
                strValue = dlmsService.TranXADRCode(recByt, paraModel.decDataType);
        } catch (Exception e) {

        }
        return strValue;
    }

    @Override
    public boolean Write(HXFramePara paraModel, ICommAction commDevice) {
        boolean blWrite = false;
        try {
            paraModel.decDataType = this.turnType(paraModel.strDecDataType);
            paraModel.OBISattri = dlmsService.fnChangeOBIS(paraModel.OBISattri);
            if (paraModel.CommDeviceType.equals("Optical")) {
                byte[] bDestAddr = {0x03};
                paraModel.DestAddr = bDestAddr;
            } else if (paraModel.CommDeviceType.equals("RF")) {
                paraModel.DestAddr = GetCommuniteMeterAddr(paraModel.strMeterNo);

            }
            paraModel.WriteData = dlmsService.GetXADRCode(paraModel.WriteData,
                    paraModel.decDataType);
            blWrite = DLMSProtocol.Write(paraModel, commDevice);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return blWrite;
    }

    @Override
    public boolean Action(HXFramePara paraModel, ICommAction commDevice) {
        boolean blAction = false;
        try {
            paraModel.decDataType = this.turnType(paraModel.strDecDataType);
            paraModel.OBISattri = dlmsService.fnChangeOBIS(paraModel.OBISattri);
            if (paraModel.CommDeviceType.equals("Optical")) {
                byte[] bDestAddr = {0x03};
                paraModel.DestAddr = bDestAddr;
            } else if (paraModel.CommDeviceType.equals("RF")) {
                paraModel.DestAddr = GetCommuniteMeterAddr(paraModel.strMeterNo);

            }
            blAction = DLMSProtocol.Action(paraModel, commDevice);
        } catch (Exception e) {

        }
        return blAction;
    }

    public boolean DiscFrame(ICommAction commDevice) throws IOException {
        boolean blDisc = false;
        DLMSProtocol.DiscFrame(commDevice);
        return blDisc;
    }

    public ICommAction OpenDeviceTest(CommPara cpara, ICommAction commDevice) {
        ICommAction DevOpen = null;
        return DevOpen;
    }

    public boolean CloseTest(ICommAction commDevice) {
        boolean blClose = false;
        return blClose;
    }

    public String ReadTest(HXFramePara paraModel, ICommAction commDevice) {
        return "1000Kw";
    }

    public boolean WriteTest(HXFramePara paraModel, ICommAction commDevice) {
        return true;
    }

    public boolean ActionTest(HXFramePara paraModel, ICommAction commDevice) {
        return true;
    }

    public boolean DiscFrameTest(ICommAction commDevice) {
        return true;

    }

}
