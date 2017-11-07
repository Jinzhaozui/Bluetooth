package com.hexing.bluetooth.util;


import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * 编码转换
 * <p>
 * version V1.0 <描述当前版本功能>
 * FileName:
 * author: caibinglong
 * date: 2017/7/6
 */

public class StringToAscii {
    private static String toHexUtil(int n) {
        String rt = "";
        switch (n) {
            case 10:
                rt += "A";
                break;
            case 11:
                rt += "B";
                break;
            case 12:
                rt += "C";
                break;
            case 13:
                rt += "D";
                break;
            case 14:
                rt += "E";
                break;
            case 15:
                rt += "F";
                break;
            default:
                rt += n;
        }
        return rt;
    }

    public static String toHex(int n) {
        StringBuilder sb = new StringBuilder();
        if (n / 16 == 0) {
            return toHexUtil(n);
        } else {
            String t = toHex(n / 16);
            int nn = n % 16;
            sb.append(t).append(toHexUtil(nn));
        }
        return sb.toString();
    }

    public static String parseAscii(String str) {
        StringBuilder sb = new StringBuilder();
        byte[] bs = str.getBytes();
        for (int i = 0; i < bs.length; i++)
            sb.append(toHex(bs[i]));
        return sb.toString();
    }

    public static String parseAscii(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++)
            sb.append(toHex(bytes[i]));
        return sb.toString();
    }

    /**
     * 将byte数组转换为int数据
     * @param b 字节数组
     * @return 生成的int数据
     */
    public static int byteToInt2(byte[] b){
        return (((int)b[0]) << 24) + (((int)b[1]) << 16) + (((int)b[2]) << 8) + b[3];
    }

    //100 5

    /**
     * 整数转换 100 转换 1.00
     *
     * @param number int
     * @return string
     */
    public static String parseString(String number) {
        String start = "";
        try {
            if (number.startsWith("-")) {
                //负数
                start = "-";
                number = number.substring(1, number.length());
            }
            float size = (float) Integer.parseInt(number) / 100;
            DecimalFormat df = new DecimalFormat("0.00");//格式化小数，不足的补0
            return start + df.format(size);//返回的是String类型的

        } catch (Exception ex) {
            return number;
        }
    }

    /**
     * 数据转换 float
     *
     * @param number float
     * @return float
     */
    public static float floatParse(String number, int scale) {
        // float sourceF = Float.valueOf(number);
//        String dennis = "0.00000008880000";
//        double f = Double.parseDouble(dennis);
//        System.out.println(f);
//        System.out.println(String.format("%.7f", f));
//        System.out.println(String.format("%.9f", new BigDecimal(f)));
//        System.out.println(String.format("%.35f", new BigDecimal(f)));
//        System.out.println(String.format("%.2f", new BigDecimal(f)));
        BigDecimal bd = new BigDecimal(number);
        bd = bd.setScale(scale, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }

    /**
     * 数据转换 double
     *
     * @param number float
     * @return double
     */
    public static double floatDouble(String number, int scale) {
        // float sourceF = Float.valueOf(number);
//        String dennis = "0.00000008880000";
//        double f = Double.parseDouble(dennis);
//        System.out.println(f);
//        System.out.println(String.format("%.7f", f));
//        System.out.println(String.format("%.9f", new BigDecimal(f)));
//        System.out.println(String.format("%.35f", new BigDecimal(f)));
//        System.out.println(String.format("%.2f", new BigDecimal(f)));

        BigDecimal b = new BigDecimal(number);
        BigDecimal one = new BigDecimal("1");
        return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }
}
