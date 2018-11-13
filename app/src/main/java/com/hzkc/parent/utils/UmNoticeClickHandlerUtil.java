package com.hzkc.parent.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.hzkc.parent.MainActivity;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;

/**
 * Created by Administrator on 2017/11/15.
 */

public class UmNoticeClickHandlerUtil extends UmengNotificationClickHandler {
    @Override
    public void dealWithCustomAction(Context context, UMessage msg) {
        String s = new Gson().toJson(msg);
        Log.e("-----------", "------handleMessage--------3----->" +s);
        //判断app进程是否存在
        if (SystemUtils.isAppAlive(context, "com.hzkc.parent")) {
            //app进程存在的情况
            //为了防止点击返回按钮不能退回到MainActivity，这里同时启动首页和通知详情页面，同时把MainActivity
            // 的launchMode设置成SingleTask, 或者在下面flag中加上Intent.FLAG_CLEAR_TOP
            //如果Task栈中有MainActivity的实例，就会把它移到栈顶，把在它之上的Activity都清理出栈，
            //如果Task栈不存在MainActivity实例，则在栈顶创建
            Log.e("推送UmNoticeClickUtil", msg.title+"--"+msg.text+"--"+msg.custom);
            Intent mainIntent = new Intent(context, MainActivity.class);
            mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            Intent detailIntent = new Intent(context, MainActivity.class);
            detailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            detailIntent.putExtra("push_title", msg.title);
            detailIntent.putExtra("push_text", msg.text);
            detailIntent.putExtra("push_custom", msg.custom);

            Intent[] intents = {mainIntent, detailIntent};
            context.startActivities(intents);
        } else {
            //app进程不存在
            //如果app进程已经被杀死，先重新启动app,然后传递数据到MainActivity在判断进入详情
            Log.e("推送NotificationReceiver", "the app process is dead");
            Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage("com.hzkc.parent");
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            Bundle args = new Bundle();
            launchIntent.putExtra("push_title", msg.title);
            launchIntent.putExtra("push_text", msg.text);
            launchIntent.putExtra("push_custom", msg.custom);
            launchIntent.putExtra(SystemShared.PUSH_EXTRA_BUNDLE, args);
            context.startActivity(launchIntent);
        }
    }
}
