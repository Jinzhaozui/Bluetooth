package com.hexing.bluetooth.util;

import com.hexing.bluetooth.thread.WriteTask;

/**
 * Created by HET075 on 2017/10/25.
 */

public class CyclicCT {
    public static void CyclicCT() {
        String commond = "";
        commond = "7E0004080143545F";
        WriteTask writeTaskct = new WriteTask();
        writeTaskct.execute(commond);
    }
}

