package com.hzkc.parent.utils;

import android.app.Activity;

import java.lang.ref.WeakReference;

/**
 * Created by wangyong on 2016/10/21.
 */
public class ActivityManager {
    private static ActivityManager sInstance = new ActivityManager();
    private WeakReference<Activity> sCurrentActivityWeakRef;
    private ActivityManager() {
    }

    public static ActivityManager getInstance() {
        return sInstance;
    }

    public Activity getCurrentActivity() {
        Activity currentActivity = null;
        if (sCurrentActivityWeakRef != null) {
            currentActivity = sCurrentActivityWeakRef.get();
        }
        return currentActivity;
    }
    public void setCurrentActivity(Activity activity) {
        sCurrentActivityWeakRef = new WeakReference<Activity>(activity);
    }
}
