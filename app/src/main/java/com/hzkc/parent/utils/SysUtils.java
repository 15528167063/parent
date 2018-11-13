package com.hzkc.parent.utils;

import android.os.Build;

/**
 * Created by Administrator on 2018/7/12.
 */
public class SysUtils {

    private static final String KEY_YUNOS_SDK_VERSION = "ro.yunos.sdk.version";
    private static final String KEY_YUNOS_MODEL = "ro.yunos.model";

    public static boolean isXiaoMi() {
        return "xiaomi".equals(Build.BRAND.toLowerCase());
    }

    public static boolean isHuaWei() {
        return Build.BRAND.toLowerCase().contains("huawei") || Build.BRAND.toLowerCase().equals("honor");
    }

    public static boolean isVivo() {
        return "vivo".equals(Build.BRAND.toLowerCase());
    }

    public static boolean isCube() {
        return "cube".equals(Build.BRAND.toLowerCase());
    }

    /**
     * 判断是否是华为EMUI的系统
     * @return
     */
    public static boolean isEMUI() {
        String val = MySystemProperties.get(AppUtils.KEY_EMUI_VERSION_CODE, "");
        return !val.equals("");
    }
    /**
     * 判断是否是魅族的系统
     * @return
     */
    public static boolean isFlyme() {

        String val = MySystemProperties.get(AppUtils.KEY_FLYME_ID, "");
        if (val.equals("")) {
            return false;
        }
        return val.toLowerCase().contains("flyme".toLowerCase());
    }
    /**
     * 判断是否是金立的系统
     * @return
     */
    public static boolean isGIONEE() {
        return !MySystemProperties.get("ro.gn.gnromvernumber", "").equals("");
    }

    /**
     * 酷派定制
     * @return
     */
    public static boolean isCoolpadOs() {
        String val = MySystemProperties.get("ro.yulong.version.software", "");
        return !val.trim().equals("");
    }
    public static boolean isSmart() {
        return "smartisan".equals(Build.MANUFACTURER.toLowerCase());
    }

    public static boolean isOppo() {
        return Build.BRAND.toLowerCase().contains("oppo");
    }

    public static boolean isMeiZu() {
        return Build.BRAND.toLowerCase().contains("meizu");
    }

    public static boolean isNubia() {
        return "nubia".equals(Build.MANUFACTURER.toLowerCase());
    }
    public static Boolean isYunOS() {
        return (!MySystemProperties.get(KEY_YUNOS_MODEL, "").equals(""))
                || !(MySystemProperties.get(KEY_YUNOS_SDK_VERSION, "").equals(""));
    }
    public static boolean isSpecialLetv() {
        return (Build.MANUFACTURER.toLowerCase().equals("letv") || "LeMobile".equals(Build.MANUFACTURER)) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }


}
