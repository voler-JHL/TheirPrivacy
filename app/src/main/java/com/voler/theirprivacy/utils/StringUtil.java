package com.voler.theirprivacy.utils;

/**
 * 三尺春光驱我寒，一生戎马为长安
 * Created by Han on 16/12/14.
 */

public class StringUtil {
    /**
     * unicode 转字符串
     */
    public static String unicode2String(String unicode) {

        StringBuffer string = new StringBuffer();

        String[] hex = unicode.split("\\\\u");

        for (int i = 1; i < hex.length; i++) {

            // 转换出每一个代码点
            int data = Integer.parseInt(hex[i], 16);

            // 追加成string
            string.append((char) data);
        }

        return string.toString();
    }
}
