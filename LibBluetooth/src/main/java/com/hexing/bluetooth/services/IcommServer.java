package com.hexing.bluetooth.services;

import com.hexing.bluetooth.protocol.icomm.ICommAction;
import com.hexing.bluetooth.protocol.model.CommPara;
import com.hexing.bluetooth.protocol.model.HXFramePara;

public interface IcommServer {
    ICommAction OpenDevice(CommPara cpara, ICommAction commDevice);

    boolean Close(ICommAction commDevice);

    String Read(HXFramePara paraModel, ICommAction commDevice);

    boolean Write(HXFramePara paraModel, ICommAction commDevice);

    boolean Action(HXFramePara paraModel, ICommAction commDevice);
}
