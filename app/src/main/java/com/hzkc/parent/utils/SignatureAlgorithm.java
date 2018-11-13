package com.hzkc.parent.utils;

import java.security.MessageDigest;

/**
 * Created by lenovo-s on 2017/5/11.
 */

public class SignatureAlgorithm {

    public static String getMD5(String instr) {
        String s = null;
        // 用来将字节转换成 16 进制表示的字
//        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
//                'a', 'b', 'c', 'd', 'e', 'f'};
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'A', 'B', 'C', 'D', 'E', 'F'};
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(instr.getBytes());
            byte tmp[] = md.digest(); // MD5 的计算结果是 128 位的长整数，
            // 用字节表示就16 个字
            char str[] = new char[16 * 2]; // 每个字节16 进制表示的话，使用两个字符，
            // 表示16 进制 32 个字
            int k = 0; // 表示转换结果中对应的字符位置
            for (int i = 0; i < 16; i++) { // 从第字节，对 MD5 的每字节
                // 转换16 进制字符的转
                byte byte0 = tmp[i]; // 取第 i 个字
                str[k++] = hexDigits[byte0 >>> 4 & 0xf]; // 取字节中4 位的数字转换,
                // >>>
                // 为辑右移，将符号位右移
                str[k++] = hexDigits[byte0 & 0xf]; // 取字节中4 位的数字转换
            }
            s = new String(str).toUpperCase(); // 换后的结果转换为字符

        } catch (Exception e) {

        }
        return s;
    }

}
