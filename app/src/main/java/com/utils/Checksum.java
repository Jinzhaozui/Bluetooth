package com.utils;

/**
 * Created by HET075 on 2017/10/9.
 */

public class Checksum {

    public static String makeChecksum(String data) {
        if (data == null || data.equals("")) {
            return "";
        }
        int total = 0;
        int len = data.length();
        int num = 0;
        while (num < len) {
            String s = data.substring(num, num + 2);
            System.out.println(s);
            total += Integer.parseInt(s, 16);
            num = num + 2;
        }
        /**
         * 用256求余最大是255，即16进制的FF
         */
        int mod = total % 256;
        String hex = Integer.toHexString(mod);   //十进制转换成十六进制
        int leng = hex.length();
        // 如果累加和的长度小于2，补0,这里用的是两位累加和
        if (leng < 2) {
            hex = "0" + hex;
        }
        String aa = String.valueOf(0xFF);
        String hex1 = xor(aa, hex);
        int lenhex1 = hex1.length();
        // 如果校验和的长度小于2，补0,这里用的是两位校验
        if (lenhex1<2){
            hex1 = "0" + hex1;
            return hex1;
        }
        return hex1;
    }

    private static String xor(String strHex_X, String strHex_Y) {
        //将x、y转成二进制形式
        String anotherBinary = Integer.toBinaryString(Integer.valueOf(strHex_X));
        // String anotherBinary=Integer.toBinaryString(255);
        String thisBinary = Integer.toBinaryString(Integer.valueOf(strHex_Y,16));
        String result = "";
        //判断是否为8位二进制，否则左补零
        if (anotherBinary.length() != 8) {
            for (int i = anotherBinary.length(); i < 8; i++) {
                anotherBinary = "0" + anotherBinary;
            }
        }
        if (thisBinary.length() != 8) {
            for (int i = thisBinary.length(); i < 8; i++) {
                thisBinary = "0" + thisBinary;
            }
        }
        //异或运算
        for (int i = 0; i < anotherBinary.length(); i++) {
            //如果相同位置数相同，则补0，否则补1
            if (thisBinary.charAt(i) == anotherBinary.charAt(i))
                result += "0";
            else {
                result += "1";
            }
        }
        String res = Integer.toHexString(Integer.parseInt(result, 2));
        String str[] = new String[res.length()]; //5个长度的字符数组
        //return Integer.toHexString(Integer.parseInt(result, 2));
        int i = 0;
        for (i = 0; i < res.length(); i++) {
            char c = res.charAt(i);     //获取当前char字符
            if (java.lang.Character.isLowerCase(c)) {   //如果是小写
                str[i] = String.valueOf(c).toUpperCase();   //变成大写
            } else {
                str[i] = String.valueOf(c);     //将字符装成字符串
            }}
            StringBuffer re = new StringBuffer();
            for (String string : str) {
                re=re.append(string);   //拼接
            }
            res=re.toString();      //转成String
        return res;
    }
}



