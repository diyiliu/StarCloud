package com.tiza.gateways.common;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Description: CommonUtil
 * Author: DIYILIU
 * Update: 2017-08-23 10:23
 */
public class CommonUtil {

    /**
     * 解析终端ID
     *
     * @param bytes
     * @return
     */
    public static String parseTerminal(byte[] bytes) {
        Long sim = 0l;
        int len = bytes.length;
        for (int i = 0; i < len; i++) {
            sim += (long) (bytes[i] & 0xff) << ((len - i - 1) * 8);
        }

        return sim.toString();
    }

    /**
     * IP封装字节数组
     *
     * @param host
     * @return
     */
    public static byte[] ipToBytes(String host) {

        String[] array = host.split("\\.");

        byte[] bytes = new byte[array.length];

        for (int i = 0; i < array.length; i++) {

            bytes[i] = (byte) Integer.parseInt(array[i]);
        }

        return bytes;
    }

    /**
     * 获取校验位
     *
     * @param bytes
     * @return
     */
    public static byte checkBits(byte[] bytes) {
        byte b = bytes[0];
        for (int i = 1; i < bytes.length; i++) {
            b ^= bytes[i];
        }

        return b;
    }


    /**
     * 字节数组转字符串
     *
     * @param bytes
     * @return
     */
    public static String bytesToStr(byte[] bytes) {
        StringBuffer buf = new StringBuffer();
        for (byte a : bytes) {
            buf.append(String.format("%02X", getNoSin(a)));
        }

        return buf.toString();
    }

    /**
     * 无符号位字节转整数
     *
     * @param b
     * @return
     */
    public static int getNoSin(byte b) {
        if (b >= 0) {
            return b;
        } else {
            return 256 + b;
        }
    }

    /**
     * 整数转16进制字符串
     * @param i
     * @return
     */
    public static String toHex(int i){

        return  String.format("%02X", i);
    }
}
