package com.hexing.bluetooth.protocol.icomm;

import com.hexing.bluetooth.protocol.model.CommPara;

public interface ICommAction {

    boolean OpenDevice(CommPara cpara);

    boolean Close();

    byte[] ReceiveByt(int SleepT, int WaitT);

    boolean SendByt(byte[] sndByte);

    void SetBaudRate(int Baudrate);

    void setORIData(String ORIData);
}
