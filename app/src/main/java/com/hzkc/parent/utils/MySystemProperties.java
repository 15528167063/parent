package com.hzkc.parent.utils;

import java.lang.reflect.Method;

/**
 * Created by Administrator on 2018/7/12.
 */

public class MySystemProperties {
    public static String get(String key, String defaultValue) {
        String value = defaultValue;
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class, String.class);
            value = (String) (get.invoke(c, key, ""));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return value;
        }
    }
}
