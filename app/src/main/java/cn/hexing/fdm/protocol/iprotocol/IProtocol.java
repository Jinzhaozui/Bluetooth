package cn.hexing.fdm.protocol.iprotocol;

import java.io.IOException;

import cn.hexing.fdm.protocol.icomm.ICommucation;
import cn.hexing.fdm.protocol.model.HXFramePara;

public interface IProtocol {

    /***
     * 璇诲彇
     * @param paraModel
     * @param commDevice
     * @return
     */
    byte[] Read(HXFramePara paraModel, ICommucation commDevice) throws IOException;

    byte[] convertSendLowBluetooth(HXFramePara paraModel, ICommucation commDevice) throws IOException;

    byte[] convertReceiverLowBluetooth(HXFramePara paraModel, ICommucation commDevice) throws IOException;

    /***
     * 璁剧疆
     * @param paraModel
     * @param commDevice
     * @return
     */
    boolean Write(HXFramePara paraModel, ICommucation commDevice) throws IOException;

    /***
     * 鎵ц
     * @param paraModel
     * @param commDevice
     * @return
     */
    boolean Action(HXFramePara paraModel, ICommucation commDevice) throws IOException;

    /***
     * 鏂紑閾捐矾
     * @param commDevice
     * @return
     */
    boolean DiscFrame(ICommucation commDevice) throws IOException;
}
