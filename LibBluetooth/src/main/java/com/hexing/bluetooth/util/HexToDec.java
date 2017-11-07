package com.hexing.bluetooth.util;

/**
 * Created by HET075 on 2017/10/19.
 */

public class HexToDec {
    public static int ToDecUtil(String hexStr) {
        int result = 0; // 保存最终的结果
        result += CharToInt(hexStr.charAt(0)) * 16 + CharToInt(hexStr.charAt(1));
        System.out.println( "result=" + result);
        return result;
    }

    private static int CharToInt(char a) {

        int i = 0;
        if (a > '0' && a <= '9') {
            i = a - 0x30;
        } else if (a == 'A') {
            i = 10;
        } else if (a == 'B') {
            i = 11;
        } else if (a == 'C') {
            i = 12;
        } else if (a == 'D') {
            i = 13;
        } else if (a == 'E') {
            i = 14;
        } else if (a == 'F') {
            i = 15;
        } else {
            return i;
        }
        return i;

    }

}
