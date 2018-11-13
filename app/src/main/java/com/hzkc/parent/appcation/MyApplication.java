package com.hzkc.parent.appcation;

import android.app.Notification;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.StrictMode;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.baidu.mapapi.SDKInitializer;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.google.gson.Gson;
import com.hzkc.parent.R;
import com.hzkc.parent.greendao.GreenDaoManager;
import com.hzkc.parent.utils.CrashHandler;
import com.hzkc.parent.utils.UmNoticeClickHandlerUtil;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.MsgConstant;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.entity.UMessage;

import cn.smssdk.SMSSDK;

/**
 * Created by lenovo-s on 2016/10/21.
 */

public class MyApplication extends MultiDexApplication {
    private static Context mContext;
    private static RequestQueue requestQueue;
    private static MyApplication instance;
    public final String PREF_USERNAME = "username";
    /**
     * 当前用户的昵称，昵称代替ID显示当用户收到通知从APNs
     */
    private static String[] channels = {"bd","hw", "yyb", "oppo", "vivo", "az","yyh","jf", "mmy", "ppzs",
            "lx","sg", "xm","mz","_360", "yczgw"};

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        //短信验证
        SMSSDK.initSDK(this, "1c112c0cbe21f", "07f97b83481ccfe21f6c644ec7044100");
        initUpush();
        //初始化百度地图
        SDKInitializer.initialize(this);
        //初始化greendao
        GreenDaoManager.getInstance();
        //初始化Fresco
        ImagePipelineConfig config = ImagePipelineConfig.newBuilder(this)
                .setDownsampleEnabled(true)
                .build();
        Fresco.initialize(this);
        requestQueue = Volley.newRequestQueue(mContext);

        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }
    }
    public static Context getContext() {
        return mContext;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static RequestQueue getRequestQueue() {
        return requestQueue;
    }

    public static MyApplication getInstance() {
        return instance;
    }

    public static String getChannelName(Context ctx) {
        if (ctx == null) {
            return null;
        }
        String channelName = null;
        try {
            PackageManager packageManager = ctx.getPackageManager();
            if (packageManager != null) {
                //注意此处为ApplicationInfo 而不是 ActivityInfo,因为友盟设置的meta-data是在application标签中，而不是某activity标签中，所以用ApplicationInfo
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(ctx.getPackageName(), PackageManager.GET_META_DATA);
                if (applicationInfo != null) {
                    if (applicationInfo.metaData != null) {
                        channelName = applicationInfo.metaData.getString("UMENG_CHANNEL");
                    }
                }

            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        int i =-1;
        if(channelName.equals("my Channel ID")){
            i=16;
        }else {
            i=Integer.parseInt(channelName);
        }
        return channels[i-1];
    }
    /**
     * 获取Application
     *
     * @return Appliaction实体类
     */
    public static Context getAppContext() {
        if (instance != null) return instance.getApplicationContext();
        else throw new NullPointerException("Application is null!");
    }

    private Handler handler;
    private void initUpush() {
        final PushAgent mPushAgent = PushAgent.getInstance(this);
        handler = new Handler(getMainLooper());
        //sdk开启通知声音
        mPushAgent.setNotificationPlaySound(MsgConstant.NOTIFICATION_PLAY_SDK_ENABLE);
        mPushAgent.setNotificationClickHandler( new UmNoticeClickHandlerUtil());
        UmengMessageHandler messageHandler = new UmengMessageHandler() {
            /**
             * 自定义消息的回调方法
             */
            @Override
            public void dealWithCustomMessage(final Context context, final UMessage msg) {
                String s = new Gson().toJson(msg);
                Log.e("自定义消息收到推送",s.toString());
                handler.post(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        // 对自定义消息的处理方式，点击或者忽略
                        boolean isClickOrDismissed = true;
                        if (isClickOrDismissed) {
                            //自定义消息的点击统计
                            UTrack.getInstance(getApplicationContext()).trackMsgClick(msg);
                        } else {
                            //自定义消息的忽略统计
                            UTrack.getInstance(getApplicationContext()).trackMsgDismissed(msg);
                        }
                        Toast.makeText(context, msg.custom, Toast.LENGTH_LONG).show();
                    }
                });
            }

            /**
             * 自定义通知栏样式的回调方法
             */
            @Override
            public Notification getNotification(Context context, UMessage msg) {
                String s = new Gson().toJson(msg);
                Log.e("自定义消息收到推送1",s.toString());
                switch (msg.builder_id) {
                    case 1:
                        boolean notienadle =  getSharedPreferences("info", MODE_PRIVATE).getBoolean("notienadle", true);
                        if(notienadle){
                            Notification.Builder builder = new Notification.Builder(context);
                            RemoteViews myNotificationView = new RemoteViews(context.getPackageName(), R.layout.item_logo);
                            myNotificationView.setTextViewText(R.id.notification_title, msg.title);
                            myNotificationView.setTextViewText(R.id.notification_text, msg.text);
                            builder.setContent(myNotificationView)
                                    .setSmallIcon(getSmallIconId(context, msg))
                                    .setTicker(msg.ticker)
                                    .setAutoCancel(true);

                            return builder.getNotification();
                        }
                    default:
                        //默认为0，若填写的builder_id并不存在，也使用默认。
                        return super.getNotification(context, msg);
                }
            }
        };
        mPushAgent.setMessageHandler(messageHandler);

        //注册推送服务 每次调用register都会回调该接口
        mPushAgent.register(new IUmengRegisterCallback() {
            @Override
            public void onSuccess(String deviceToken) {
                Log.e("------------","deviceToken"+deviceToken);
                getSharedPreferences("info", MODE_PRIVATE).edit().putString("deviceToken",deviceToken).commit();
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        mPushAgent.onAppStart();
                    }
                });
            }

            @Override
            public void onFailure(String s, String s1) {
            }
        });


//        MiPushRegistar.register(this, "2882303761517591493", "5141759194493");
//        HuaWeiRegister.register(this);
    }
}
