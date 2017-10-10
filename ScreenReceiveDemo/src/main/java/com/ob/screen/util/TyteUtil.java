package com.ob.screen.util;

public class TyteUtil {
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
}
