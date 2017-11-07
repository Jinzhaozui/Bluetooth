package cn.hexing.fdm.services;

import cn.hexing.fdm.protocol.icomm.ICommucation;
import cn.hexing.fdm.protocol.model.CommPara;
import cn.hexing.fdm.protocol.model.HXFramePara;

public interface IcommServer {
	
	/***
	 * 鎵撳紑閫氳妯″潡
	 * @param cpara
	 * @return
	 */
	ICommucation OpenDevice(CommPara cpara, ICommucation commDevice);
	/***
	 * 鍏抽棴閫氳妯″潡
	 * @return
	 */
	boolean Close(ICommucation commDevice);
	/**
	 * 璇诲彇鐢佃〃
	 * @return
	 */
	public String Read(HXFramePara paraModel, ICommucation commDevice);
	/**
	 * 璁剧疆鐢佃〃
	 * @param strData
	 * @return
	 */
	public boolean Write(HXFramePara paraModel, ICommucation commDevice);
	/**
	 * 鎵ц鐢佃〃
	 * @param strData
	 * @return
	 */
	public boolean Action(HXFramePara paraModel, ICommucation commDevice);
}
