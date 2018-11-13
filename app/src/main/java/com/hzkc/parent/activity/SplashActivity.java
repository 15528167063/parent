package com.hzkc.parent.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.hzkc.parent.Bean.UpDownMsgInfo;
import com.hzkc.parent.Bean.VersonBean;
import com.hzkc.parent.MainActivity;
import com.hzkc.parent.R;
import com.hzkc.parent.appcation.MyApplication;
import com.hzkc.parent.mina.Constants;
import com.hzkc.parent.service.MianService;
import com.hzkc.parent.solider.activity.MainSoldActivity;
import com.hzkc.parent.utils.AppUtils;
import com.hzkc.parent.utils.LogUtil;
import com.hzkc.parent.utils.MyUpdateUtils;
import com.hzkc.parent.utils.MyUtils;

import org.simple.eventbus.EventBus;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


public class SplashActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "SplashActivity";
    private String isfirststart;
    private SharedPreferences sp;
    private int versionCode;// 版本号
    private String versionName;// 版本名
    private UpDownMsgInfo info2;
    private VersonBean info_last;
    private String token;
    private TextView tv_verson;
    public boolean isqzup;   //判断是不是强制跟新
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 601:
                    //对话框通知用户升级程序
                    String qz =  msg.obj+"";
                    //如果是q 强制升级
                    if (qz.equals("1")) {
                        isqzup=true;
                        QzUpdataDialog();
                    } else {
                        isqzup=false;
                        showUpdataDialog();
                    }
                    break;
                case 602:
                    //家长端服务器超时
                    //Toast.makeText(getApplication(), "获取服务器更新信息失败", Toast.LENGTH_SHORT).show();
//                    handler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            loginActivity();
//                        }
//                    },1500);
                    checkAdvertion();
                    break;
                case 603:
                    //下载apk失败
                    if(!isqzup){
                        loginActivity();
                        Toast.makeText(getApplication(), "下载新版本失败,缺少必要的权限", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(getApplication(), "下载失败,请在权限管理中打开必要的权限", Toast.LENGTH_LONG).show();
                    }
                    break;
            }
        }
    };
    private String isguide;
    private ImageView iv_splash;
    private TextView skipTv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            try {
                Class decorViewClazz = Class.forName("com.android.internal.policy.DecorView");
                Field field = decorViewClazz.getDeclaredField("mSemiTransparentStatusBarColor");
                field.setAccessible(true);
                field.setInt(getWindow().getDecorView(), Color.TRANSPARENT);  //改为透明
            } catch (Exception e) {}
        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            Window window =  getWindow();
//            // Translucent status bar
//            window.setFlags(
//                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
//                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Window window = getWindow();
//            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
//                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(Color.TRANSPARENT);
//            window.setNavigationBarColor(Color.TRANSPARENT);
//        }
        setContentView(R.layout.activity_splash);

        sp = getSharedPreferences("info", MODE_PRIVATE);
        sp.edit().putBoolean("childFlag1", false).putBoolean("childFlag2", false).putBoolean("childFlag3", false).commit();
        sp.edit().putString("viptime", "1499900000000").commit();
        token = sp.getString("token", "");
        isfirststart = sp.getString("isfirststart", "");
        isguide = sp.getString("isguide", "");
        tv_verson= (TextView) findViewById(R.id.tv_verson);
        skipTv= (TextView) findViewById(R.id.skipTv);
        iv_splash= (ImageView) findViewById(R.id.iv_splash);
        iv_splash.setOnClickListener(this);
        skipTv.setOnClickListener(this);

        LogUtil.e("SplashActivity","IP:"+Constants.IP);
        //开启服务
        startService();
        //获取当前版本信息
        getMyVersion();
        //判断是不是第一次安装
        isfirst();
        //每次APP启动返回接口
        openApp();
        //判断是否进入引导界面
        if (TextUtils.isEmpty(isguide)) {
            Intent intent = new Intent(this, GuideActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        //检查家长端的版本情况
        checkParentVersion();

    }




    /**
     * 判断是否进入引导界面
     */
    private void loginActivity() {
        String apptype = sp.getString("apptype", "");
        if (TextUtils.isEmpty(token)) {
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            if(apptype!=null && apptype.equals("wj")){
                Intent intent = new Intent(SplashActivity.this, MainSoldActivity.class);
                startActivity(intent);
                finish();
            }else {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

        }
    }

    /**
     * 每次APP启动返回接口
     */
    private void openApp() {
        //注册完成接口
        String url = Constants.FIND_URL_API_OPEN;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        LogUtil.e("每次APP启动返回接口", "updateDatas:" + response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogUtil.e("每次APP启动返回接口", "updateDatas:" + error);
            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.getRequestQueue().add(stringRequest);
    }

    /**
     * 家长端的版本情况
     */

    private void checkParentVersion() {

        String url=Constants.PHP_URL+"v"+ AppUtils.getVerName()+"/version";
        Log.e("家长端的版本情况完成接口", "url:" + url);
        StringRequest postsr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("家长端的版本情况完成接口", "updateDatas:" + response);
                if(TextUtils.isEmpty(response)){
                    checkAdvertion();//广告
                    return;
                }
                try{
                    info_last = new Gson().fromJson(response, VersonBean.class);
                }catch (Exception e){
                    checkAdvertion();//广告
                    return;
                }
                String currentVersion="V" + versionCode + "." + versionName;
                Boolean update = MyUtils.isNeedUpdate(SplashActivity.this, currentVersion, info_last.getData().getVersion_code());
                if(!update){
                    LogUtil.e(TAG, "版本号相同无需升级：：" + currentVersion);
                    sp.edit().putBoolean("isupdate",false).commit();    //在关于我的界面会使用到是不是需要跟新
                    checkAdvertion();//广告
                }else{
                    LogUtil.i(TAG, "版本号不同 ,提示用户升级：：" + currentVersion);
                    sp.edit().putBoolean("isupdate",true).commit();  //在关于我的界面会使用到是不是需要跟新
                    sp.edit().putString("updatemessage",info_last.getData().getVersion_info()).commit();
                    Message msg = Message.obtain();
                    msg.obj = info_last.getData().getIs_force();
                    msg.what = 601;
                    handler.sendMessage(msg);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Message msg = Message.obtain();
                msg.what = 602;
                handler.sendMessage(msg);
                LogUtil.e("家长端的版本情况", "error:" + volleyError);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //创建一个集合，放的是keyvalue的key是参数名与value是参数值
                Map<String, String> map = new HashMap<String, String>();
                map.put("port_src", "parent");
                return map;
            }
        };
        postsr.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.getRequestQueue().add(postsr);
    }

    /**
     * 启动service
     */
    private void startService() {
        Intent intent = new Intent(SplashActivity.this, MianService.class);
        LogUtil.i(TAG, "开启服务");
        startService(intent);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    /**
     * 判断是不是第一次安装
     */
    public void isfirst() {
        if (TextUtils.isEmpty(isfirststart)) {
            String url = Constants.FIND_URL_API_FIRST;
            //注册完成接口
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            LogUtil.e("判断是不是第一次安装", "updateDatas:" + response);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    LogUtil.e("判断是不是第一次安装", "updateDatas:" + error);
                }
            });
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            MyApplication.getRequestQueue().add(stringRequest);
            sp.edit().putString("isfirststart", "isfirststart").commit();
        }
    }

    /**
     * 家长端升级对话框
     */
    protected void showUpdataDialog() {
        AlertDialog.Builder builer = new AlertDialog.Builder(this);
        builer.setTitle("发现家长端新版本");
        builer.setMessage(info_last.getData().getVersion_info());
        builer.setCancelable(false);
        builer.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                    dialog.dismiss();
                    finish();
                }
                return false;
            }
        });
        //当点确定按钮时从服务器上下载 新的apk 然后安装
        builer.setPositiveButton("立即升级", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Log.i(TAG, "下载apk,更新");
                try{
                    downLoadApk();
                }catch (Exception e){
                    Toast.makeText(SplashActivity.this, "请在权限设置里打开访问SD卡权限", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
        //当点取消按钮时进行登录
        builer.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                loginActivity();
            }
        });
        AlertDialog dialog = builer.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setIcon(R.drawable.icon);
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                    if(dialog!=null){
                        dialog.dismiss();
                    }
                    loginActivity();
                }
                return false;
            }
        });
        dialog.show();
    }


    /**
     * 强制家长端升级对话框
     */
    protected void QzUpdataDialog() {
        AlertDialog.Builder builer = new AlertDialog.Builder(SplashActivity.this);
        builer.setTitle("发现优成长新版本");
        builer.setMessage(info_last.getData().getVersion_info());
        builer.setCancelable(false);
        builer.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                    dialog.dismiss();
                    finish();
                }
                return false;
            }
        });
        //当点确定按钮时从服务器上下载 新的apk 然后安装
        builer.setPositiveButton("立即升级", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                try{
                    downLoadApk();
                }catch (Exception e){
                    Toast.makeText(SplashActivity.this, "请在权限设置里打开访问SD卡权限", Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        });
        AlertDialog dialog = builer.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setIcon(R.drawable.icon);
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                    if(dialog!=null){
                        dialog.dismiss();
                    }
                    finish();
                }
                return false;
            }
        });
        dialog.show();
    }

    /**
     * 从服务器中下载APK
     */
    File file;
    protected void downLoadApk() {
        final ProgressDialog pd;    //进度条对话框
        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setCanceledOnTouchOutside(false);
        pd.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                    dialog.dismiss();
                    finish();
                }
                return false;
            }
        });
        pd.setMessage("正在下载更新");
        pd.setIndeterminate(false);
        pd.show();
        final String url=Constants.PHP_URL+"v"+ AppUtils.getVerName()+"/download?down_src=parent&apkid=03091866737";
        new Thread() {
            @Override
            public void run() {
                try {
                    file = MyUpdateUtils.getFileFromServer(url, pd);
                    sleep(3000);
                    Message msg = new Message();
                    installProcess();
                    pd.dismiss(); //结束掉进度条对话框
                } catch (Exception e) {
                    e.printStackTrace();
                    Message msg = new Message();
                    msg.what = 603;
                    handler.sendMessage(msg);
                    pd.dismiss(); //结束掉进度条对话框

                }
            }
        }.start();
    }

    /**
     * 安装apk
     */
//    protected void installApk(File file) {
//        Intent intent = new Intent();
//        //执行动作
//        intent.setAction(Intent.ACTION_VIEW);
//        //执行的数据类型
//        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
//        startActivity(intent);
//        finish();

    //安装应用的流程
    private void installProcess() {
        boolean haveInstallPermission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //先获取是否有安装未知来源应用的权限
            haveInstallPermission = getPackageManager().canRequestPackageInstalls();
            if (!haveInstallPermission) {//没有权限
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startInstallPermissionSettingActivity();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(SplashActivity.this, "请开启打开未知来源权限", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                return;
            }
        }
        //有权限，开始安装应用程序
        installApk(file);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void     startInstallPermissionSettingActivity() {
        Uri packageURI = Uri.parse("package:" + getPackageName());
        //注意这个是8.0新API
        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI);
        startActivityForResult(intent, 10086);
    }

    //安装应用
    private void installApk(File apk) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            intent.setDataAndType(Uri.fromFile(apk), "application/vnd.android.package-archive");
            startActivity(intent);
        } else {//Android7.0之后获取uri要用contentProvider
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                String packageName = this.getApplication().getPackageName();
                Uri contentUri = FileProvider.getUriForFile(this, packageName + ".fileprovider", apk);
                intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
            }

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10086) {
            installProcess();

        }
    }

    /**
     * 获取versioncode 和versionname
     */
    public void getMyVersion() {
        // 获取自己的版本信息
        PackageManager pm = getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
            // 版本号
            versionCode = packageInfo.versionCode;
            // 版本名
            versionName = packageInfo.versionName;
            tv_verson.setText("V"+versionName);
        } catch (PackageManager.NameNotFoundException e) {

        }
    }

    private void checkAdvertion() {
//        String url=Constants.PHP_URL+"v"+ AppUtils.getVerName()+"/bannerHome";
//        Log.e("家长端广告信息", "url:" + url);
//        StringRequest postsr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                Log.e("家长端广告信息", "response:" + response);
//                AdRoot adRoot = new Gson().fromJson(response, AdRoot.class);
//                tourl=adRoot.getData().tourl;
//                if(adRoot.getData()!=null && !TextUtils.isEmpty(adRoot.getData().imgurl)){ //有广告
//                    iv_splash.setVisibility(View.VISIBLE);
//                    skipTv.setVisibility(View.VISIBLE);
//                    Glide.with(SplashActivity.this).load(adRoot.getData().imgurl).centerCrop().crossFade().into(iv_splash);
//                    countDownNext();
//                }else { //无广告
//                    handler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            loginActivity();
//                        }
//                    },1500);
//                }
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError volleyError) {
//                LogUtil.e("家长端广告信息", "error:" + volleyError);
//            }
//        }) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                //创建一个集合，放的是keyvalue的key是参数名与value是参数值
//                Map<String, String> map = new HashMap<String, String>();
//                return map;
//            }
//        };
//        postsr.setRetryPolicy(new DefaultRetryPolicy(5000,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        MyApplication.getRequestQueue().add(postsr);


        String adversterurl = sp.getString("adversterurl", "");
        if(!TextUtils.isEmpty(adversterurl)){
            iv_splash.setVisibility(View.VISIBLE);
            skipTv.setVisibility(View.VISIBLE);
            Glide.with(SplashActivity.this).load(adversterurl).centerCrop().crossFade().into(iv_splash);
            countDownNext();
        }else {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    loginActivity();
                }
            },1500);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_splash:
                String tourl = sp.getString("tourl", "");
                if(tourl!=null){
                    if (timer != null) {
                        timer.cancel();
                        timer = null;
                    }
                    Intent intent = new Intent(SplashActivity.this, FunctionActivity.class);
                    intent.putExtra("urlPath", tourl);
                    startActivity(intent);
                    finish();
                }
                break;
            case R.id.skipTv:
                if (timer != null) {
                    timer.cancel();
                    timer = null;
                }
                loginActivity();
                break;
        }
    }

    /**
     * 闪烁时长
     */
    private final long blinkTime = 1000;
    /**
     * 等待总时长
     */
    private final long totalTime = 5000;
    /**
     * 广告时长
     */
    private long adTime = 0;

    private Timer timer;
    private void countDownNext() {
        adTime = blinkTime;//加上延时
        skipTv.setVisibility(View.VISIBLE);
        skipTv.setText("跳过 | " + (totalTime - adTime) / 1000);
        if (timer == null) {
            timer = new Timer();
        }else {
            timer.cancel();
        }
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                adTime += 1000;

                if (adTime >= totalTime) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (timer != null) {
                                timer.cancel();
                                timer = null;
                            }
                            loginActivity();
                        }
                    });
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        skipTv.setText("跳过 | " + (totalTime - adTime) / 1000);
                    }
                });

            }
        }, 0, 1000);
    }
}
