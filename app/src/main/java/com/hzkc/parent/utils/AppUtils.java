package com.hzkc.parent.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;

import com.hzkc.parent.appcation.MyApplication;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by wangyong on 2016/12/2.
 */

public class AppUtils {
    private static final String KEY_MIUI_VERSION_CODE = "ro.miui.ui.version.code";
    private static final String KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name";
    private static final String KEY_MIUI_INTERNAL_STORAGE = "ro.miui.internal.storage";

    public static final String KEY_EMUI_VERSION_CODE = "ro.build.hw_emui_api_level";

    public  static final String KEY_FLYME_ID = "ro.build.display.id";

    //* sp转px
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }
    /**
     * px转sp
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 测试当前摄像头能否被使用
     * @return
     */
    public static boolean isCameraCanUse() {
        boolean canUse = true;
        Camera mCamera = null;
        try {
            mCamera = Camera.open(0);
            mCamera.setDisplayOrientation(90);
        } catch (Exception e) {
            canUse = false;
        }
        if (canUse) {
            mCamera.release();
            mCamera = null;
        }
        return canUse;
    }

    public static boolean isChinaPhoneLegal(String str)  {
        String regExp = "^((13[0-9])|(15[^4])|(18[0,1，2,3,5-9])|(17[0-8])|(147))\\d{8}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(str);
        return m.matches();
    }


    /**
     * 但是当我们没在AndroidManifest.xml中设置其debug属性时:
     * 使用Eclipse运行这种方式打包时其debug属性为true,使用Eclipse导出这种方式打包时其debug属性为法false.
     * 在使用ant打包时，其值就取决于ant的打包参数是release还是debug.
     * 因此在AndroidMainifest.xml中最好不设置android:debuggable属性置，而是由打包方式来决定其值.
     */
    public static boolean isApkDebugable() {
        try {
            ApplicationInfo info= MyApplication.getContext().getApplicationInfo();
            return (info.flags&ApplicationInfo.FLAG_DEBUGGABLE)!=0;
        } catch (Exception e) {
        }
        return false;
    }


    public static int getVersionCode() {
        int versionCode = 0;
        try {
            //获取软件版本号，对应AndroidManifest.xml下android:versionCode
            versionCode = MyApplication.getContext().getPackageManager().
                    getPackageInfo(MyApplication.getContext().getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }
    /**
     * 获取版本号名称
     */
    public static String getVerName() {
        String verName = "";
        try {
            verName =  MyApplication.getContext().getPackageManager().
                    getPackageInfo( MyApplication.getContext().getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return verName;
    }
    /**
     * 获取手机系统
     */
    public static String getSpecificMobileRomInfo() {
        String str = "";
        try {
            if (SysUtils.isHuaWei()){
                return MySystemProperties.get("ro.build.version.emui", "");
            }
            if (SysUtils.isXiaoMi()){
                return MySystemProperties.get("ro.miui.ui.version.name", "") + " " + MySystemProperties.get("ro.build.version.incremental", "");
            }
            if (SysUtils.isVivo()){
                str = MySystemProperties.get("ro.vivo.os.build.display.id", "");
                return "".equals(str) ? MySystemProperties.get("ro.vivo.os.name", "") + " " + MySystemProperties.get("ro.vivo.rom.version", "") : str;
            }
            if (SysUtils.isOppo()){
                return MySystemProperties.get("ro.build.version.opporom", "");
            }
            if (SysUtils.isMeiZu()){
                return MySystemProperties.get("ro.build.display.id", "");
            }
            if (SysUtils.isSpecialLetv()){
                return MySystemProperties.get("ro.letv.release.version", "");
            }
            if ("GiONEE".equalsIgnoreCase(Build.MANUFACTURER)){
                return MySystemProperties.get("ro.build.display.id", "");
            }
            if (SysUtils.isYunOS()){
                return "yunos_" + MySystemProperties.get("ro.yunos.version", "");
            }
            if (SysUtils.isNubia()){
                return MySystemProperties.get("ro.build.nubia.rom.name", "") + MySystemProperties.get("ro.build.nubia.rom.code", "") + MySystemProperties.get("ro.build.rom.id", "");
            }
            if (SysUtils.isSmart()){
                return MySystemProperties.get("ro.smartisan.version", "");
            }
        }catch (Exception e){
        }
        return str;
    }

}
