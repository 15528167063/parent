package com.hzkc.parent.utils;

import android.content.Context;
import android.text.TextUtils;

import com.hzkc.parent.Bean.UseAppDataBean;
import com.hzkc.parent.jsondata.AppUsageData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo-s on 2016/12/20.
 */

public class AppUseTimeThread implements Runnable {
    private int size;
    private Context context;
    private List<AppUsageData> AppUseTimeDataList;
    boolean flag = true;
    private List<UseAppDataBean> mlist;
    private RefleshListener listener;
    private String childuuid;

    public AppUseTimeThread(int size, Context context, List<AppUsageData> AppUseTimeDataList, String childuuid) {
        this.size = size;
        this.context = context;
        this.AppUseTimeDataList = AppUseTimeDataList;
        this.childuuid = childuuid;
        mlist = new ArrayList<>();

    }

    public interface RefleshListener {
        void sendData(List<UseAppDataBean> mlist);
    }

    @Override
    public void run() {
        while (flag) {
            save();
        }
    }
//{"c":"amigo桌面","a":"com.gionee.amisystem","b":"49","d":1540021011977},{"c":"WLAN","a":"com.gionee.setting.adapter.wifi","b":"29","d":1540017982778},{"c":"WLAN","a":"com.gionee.setting.adapter.wifi"":"2331"},{"a":"com.eg.android.AlipayGphone","b":"3744"},{"a":"com.hzkc.parent","b":"1271389"}]
    public synchronized void save() {
        if (size >= 0) {
            String appPkgName = AppUseTimeDataList.get(size).getAppPkgName();
            String percent = AppUseTimeDataList.get(size).getAppTime();
            String appuseTime= AppUseTimeDataList.get(size).getAppUsage();
            String appName = AppUseTimeDataList.get(size).getAppName();
            if(!TextUtils.isEmpty(appName)){
                UseAppDataBean useAppDataBean = new UseAppDataBean(appName,appuseTime,percent,appPkgName);
                mlist.add(useAppDataBean);
            }
            size--;
            if (listener != null) {
                listener.sendData(mlist);
            }
        } else {
            flag = false;
            if (listener != null) {
                listener.sendData(mlist);
            }
        }
    }

    public void reflushPart(RefleshListener listener) {
        this.listener = listener;
    }
}
