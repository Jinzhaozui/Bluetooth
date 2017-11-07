/*
 * Copyright (C) 2014 Advanced Card Systems Ltd. All Rights Reserved.
 * 
 * This software is the confidential and proprietary information of Advanced
 * Card Systems Ltd. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with ACS.
 */

package com.hexing.bluetooth.util;

import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Locale;

/**
 * The
 * <code>CardUtils<code> class contains static methods which operate on arrays and
 * string.
 *
 * @author Gary Wong
 * @version 1.0, 4 Jun 2014
 */
public class CardUtils {

    /**
     * Creates a hexadecimal <code>String</code> representation of the
     * <code>byte[]</code> passed. Each element is converted to a
     * <code>String</code> via the {@link Integer#toHexString(int)} and
     * separated by <code>" "</code>. If the array is <code>null</code>, then
     * <code>""<code> is returned.
     *
     * @param array the <code>byte</code> array to convert.
     * @return the <code>String</code> representation of <code>array</code> in
     * hexadecimal.
     */
    public static String toHexString(byte[] array) {
        String bufferString = "";
        if (array != null) {
            for (int i = 0; i < array.length; i++) {
                String hexChar = Integer.toHexString(array[i] & 0xFF);
                if (hexChar.length() == 1) {
                    hexChar = "0" + hexChar;
                }
                bufferString += hexChar.toUpperCase(Locale.US) + " ";
            }
        }
        return bufferString;
    }

    private static boolean isHexNumber(byte value) {
        if (!(value >= '0' && value <= '9') && !(value >= 'A' && value <= 'F')
                && !(value >= 'a' && value <= 'f')) {
            return false;
        }
        return true;
    }

    /**
     * Checks a hexadecimal <code>String</code> that is contained hexadecimal
     * value or not.
     *
     * @param string the string to check.
     * @return <code>true</code> the <code>string</code> contains Hex number
     * only, <code>false</code> otherwise.
     * @throws NullPointerException if <code>string == null</code>.
     */
    public static boolean isHexNumber(String string) {
        if (string == null)
            throw new NullPointerException("string was null");

        boolean flag = true;

        for (int i = 0; i < string.length(); i++) {
            char cc = string.charAt(i);
            if (!isHexNumber((byte) cc)) {
                flag = false;
                break;
            }
        }
        return flag;
    }

    private static byte uniteBytes(byte src0, byte src1) {
        byte _b0 = Byte.decode("0x" + new String(new byte[]{src0})).byteValue();
        _b0 = (byte) (_b0 << 4);
        byte _b1 = Byte.decode("0x" + new String(new byte[]{src1})).byteValue();
        byte ret = (byte) (_b0 ^ _b1);
        return ret;
    }

    /**
     * Creates a <code>byte[]</code> representation of the hexadecimal
     * <code>String</code> passed.
     *
     * @param string the hexadecimal string to be converted.
     * @return the <code>array</code> representation of <code>String</code>.
     * @throws IllegalArgumentException if <code>string</code> length is not in even number.
     * @throws NullPointerException     if <code>string == null</code>.
     * @throws NumberFormatException    if <code>string</code> cannot be parsed as a byte value.
     */
    public static byte[] hexString2Bytes(String string) {
        if (string == null)
            throw new NullPointerException("string was null");

        int len = string.length();

        if (len == 0)
            return new byte[0];
        if (len % 2 == 1)
            throw new IllegalArgumentException(
                    "string length should be an even number");

        byte[] ret = new byte[len / 2];
        byte[] tmp = string.getBytes();

        for (int i = 0; i < len; i += 2) {
            if (!isHexNumber(tmp[i]) || !isHexNumber(tmp[i + 1])) {
                throw new NumberFormatException(
                        "string contained invalid value");
            }
            ret[i / 2] = uniteBytes(tmp[i], tmp[i + 1]);
        }
        return ret;
    }

    /**
     * Creates a <code>byte[]</code> representation of the hexadecimal
     * <code>String</code> in the EditText control.
     *
     * @param rawData the EditText control which contains hexadecimal string to be
     *                converted.
     * @return the <code>array</code> representation of <code>String</code> in
     * the EditText control. <code>null</code> if the string format is
     * not correct.
     */

    public static byte[] getEditTextInHexBytes(String rawData) {

        if (rawData == null || rawData.isEmpty()) {
            return null;
        }
        String command = rawData.replace(" ", "").replace("\n", "");
        if (command.isEmpty() || command.length() % 2 != 0 || !isHexNumber(command)) {
            Log.e("getEditTextInHexBytes", "命令错误-》" + rawData);
            return null;
        }
        command = command.toUpperCase();
        return hexString2Bytes(command);
    }

    /**
     * 转换 16进制 数据
     *
     * @param len     长度
     * @param haveOne true 一个字节  false 2个字节
     * @return 16进制数据
     */
    public static String getHex(int len, boolean haveOne) {
        String result;
        if (haveOne) {//一个字节
            result = Integer.toHexString(len);
            if (result.length() == 1) {
                result = "0" + result;
            }
        } else { //2个字节
            result = Integer.toHexString(len);
            for (int i = result.length(); i < 4; i++) {
                result = "0" + result;
            }
        }
        return result;
    }

    // byte转char

    public static char[] getChars(byte[] bytes) {
        Charset cs = Charset.forName("UTF-8");
        ByteBuffer bb = ByteBuffer.allocate(bytes.length);
        bb.put(bytes);
        bb.flip();
        CharBuffer cb = cs.decode(bb);

        return cb.array();
    }
}
