package com.hexing.bluetooth.protocol.iprotocol;

import com.hexing.bluetooth.protocol.icomm.ICommAction;
import com.hexing.bluetooth.protocol.model.HXFramePara;

import java.io.IOException;

public interface IProtocol {

    byte[] Read(HXFramePara paraModel, ICommAction commDevice) throws IOException;

    byte[] convertSendLowBluetooth(HXFramePara paraModel, ICommAction commDevice) throws IOException;

    byte[] convertReceiverLowBluetooth(HXFramePara paraModel, ICommAction commDevice) throws IOException;

    boolean Write(HXFramePara paraModel, ICommAction commDevice) throws IOException;

    boolean Action(HXFramePara paraModel, ICommAction commDevice) throws IOException;

    boolean DiscFrame(ICommAction commDevice) throws IOException;
}
