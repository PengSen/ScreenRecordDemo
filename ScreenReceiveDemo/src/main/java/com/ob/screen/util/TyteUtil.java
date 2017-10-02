package com.ob.screen.util;

/**
 * Created by pengs on 2017/9/2.
 */

public class TyteUtil {

    /**
     * 把int转换成byte数组
     *
     * @param n 要转换的int值
     * @return 返回的byte数组
     */
    public static byte[] int2BytesArray(int n) {
        byte[] b = new byte[4];
        for (int i = 0; i < 4; i++) {
            b[i] = (byte) (n >> (24 - i * 8));
        }
        return b;
    }
    /**
     * 把byte数组转换成int类型
     *
     * @param b 源byte数组
     * @return 返回的int值
     */
    public static int byteArrayToInt(byte[] b) {
        return   b[3] & 0xFF |
                (b[2] & 0xFF) << 8 |
                (b[1] & 0xFF) << 16 |
                (b[0] & 0xFF) << 24;
    }

    public static byte[] intToBytes2(int value)
    {
        byte[] src = new byte[4];
        src[0] = (byte) ((value>>24) & 0xFF);
        src[1] = (byte) ((value>>16)& 0xFF);
        src[2] = (byte) ((value>>8)&0xFF);
        src[3] = (byte) (value & 0xFF);
        return src;
    }
    public static int bytesToInt(byte[] ary, int offset) {
        int value;
        value = (ary[offset]&0xFF)
                | ((ary[offset+1]<<8) & 0xFF00)
                | ((ary[offset+2]<<16)& 0xFF0000)
                | ((ary[offset+3]<<24) & 0xFF000000);
        return value;
    }
}
