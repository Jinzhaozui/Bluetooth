package cn.hexing.fdm.protocol.icomm;

import cn.hexing.fdm.protocol.model.CommPara;

public interface ICommucation {

    boolean OpenDevice(CommPara cpara);

    boolean Close();

    byte[] ReceiveByt(int SleepT, int WaitT);

    boolean SendByt(byte[] sndByte);

    void SetBaudRate(int Baudrate);

    void setORIData(String ORIData);
}
