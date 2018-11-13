package com.hzkc.parent.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.hzkc.parent.Bean.MineItemBean;
import com.hzkc.parent.Bean.UpDownMsgInfo;
import com.hzkc.parent.R;
import com.hzkc.parent.adapter.SettingHelpListAdapter;
import com.hzkc.parent.appcation.MyApplication;
import com.hzkc.parent.mina.Constants;
import com.hzkc.parent.utils.LogUtil;
import com.hzkc.parent.utils.MyUpdateUtils;
import com.hzkc.parent.utils.MyUtils;
import com.hzkc.parent.utils.ToastUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SettingAndHelpActivity extends BaseActivity implements View.OnClickListener {

    private TextView tvTopTitle;
    private ImageView ivFinish;
    private ListView lvSetHelp;

    private int versionCode;// 版本号
    private String versionName;// 版本名
    private int[] minepagerIcon = {R.drawable.about_us_gnjs, R.drawable.about_us_applist,
            R.drawable.about_us_yhfk, R.drawable.about_us_lxwm};
    private String[] minepagerName = {"功能介绍", "下载孩子端", "检查更新", "关于我们"};
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case 601:
                    //对话框通知用户升级程序
                    showUpdataDialog();
                    break;
                case 602:
                    //服务器超时
                    if(pd!=null&&pd.isShowing()){
                        pd.dismiss();
                    }
                    Toast.makeText(getApplication(), "获取服务器更新信息失败", Toast.LENGTH_SHORT).show();
                    break;
                case 603:
                    //下载apk失败
                    Toast.makeText(getApplication(), "下载新版本失败", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private UpDownMsgInfo info;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_and_help);
        initView();
        initData();
    }

    private void initData() {
        // 获取自己的版本信息
        PackageManager pm = getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
            // 版本号
            versionCode = packageInfo.versionCode;
            // 版本名
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {

        }
        List<MineItemBean> mlist = new ArrayList<>();
        for (int i = 0; i < minepagerName.length; i++) {
            MineItemBean bean = new MineItemBean();
            bean.setTvName(minepagerName[i]);
            bean.setIvIcon(minepagerIcon[i]);
            mlist.add(bean);
        }
        LogUtil.e("mlist:"+mlist.size());
        SettingHelpListAdapter adapter = new SettingHelpListAdapter(mlist, this);
        lvSetHelp.setAdapter(adapter);
        lvSetHelp.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0://功能介绍
                        Intent intent3 = new Intent(SettingAndHelpActivity.this, FindArticleActivity.class);
                        startActivity(intent3);
                        //Toast.makeText(SettingAndHelpActivity.this, "功能介绍", Toast.LENGTH_SHORT).show();
                        break;
                    case 1://下载孩子端
                        //Toast.makeText(SettingAndHelpActivity.this, "孩子手机配置向导", Toast.LENGTH_SHORT).show();
                        Intent intent1 = new Intent(SettingAndHelpActivity.this, ChildDownloadActivity.class);
                        startActivity(intent1);
                        break;
                    case 2://检测更新
                        checkVerion();
                        break;
                    case 3://关于我们
                        Intent intent4 = new Intent(SettingAndHelpActivity.this, AboutMineActivity.class);
                        startActivity(intent4);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void initView() {

        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        ivFinish = (ImageView) findViewById(R.id.iv_finish);
        lvSetHelp = (ListView) findViewById(R.id.lv_sethelp);


        ivFinish.setVisibility(View.VISIBLE);
        tvTopTitle.setText("设置与帮助");
        ivFinish.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_finish:
                finish();
                break;
        }
    }
    /**
     * 联网检测版本
     * */
    private void checkVerion() {
        pd = new ProgressDialog(this);
        pd.setMessage("正在登录中，请稍候");
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setCancelable(false);
        pd.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                    pd.dismiss();
                }
                return false;
            }
        });
        pd.show();
        String url = Constants.FIND_URL_API_UP;
        //注册完成接口
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        LogUtil.e("注册完成接口", "updateDatas:" + response);
                        if(TextUtils.isEmpty(response)){
                            if(pd!=null&&pd.isShowing()){
                                pd.dismiss();
                            }
                            ToastUtils.showToast(MyApplication.getContext(), "已经是最新版本");
                            return;
                        }
                        info = new Gson().fromJson(response, UpDownMsgInfo.class);
//                        if (info.getVersion().equals("V"+versionCode+"."+versionName)) {
//                            LogUtil.i(TAG, "版本号相同无需升级" + versionName);
//                            ToastUtils.showToast(MyApplication.getContext(), "已经是最新版本...");
//                        } else {
//                            LogUtil.i(TAG, "版本号不同 ,提示用户升级 ");
//                            Message msg = Message.obtain();
//                            msg.what = 601;
//                            handler.sendMessage(msg);
//                        }
                        String currentVersion="V" + versionCode + "." + versionName;
                        Boolean update = MyUtils.isNeedUpdate(SettingAndHelpActivity.this, currentVersion, info.getVersion());
                        if(!update){
                            LogUtil.i(TAG, "版本号相同无需升级：：" + currentVersion);
                            if(pd!=null&&pd.isShowing()){
                                pd.dismiss();
                            }
                           ToastUtils.showToast(MyApplication.getContext(), "已经是最新版本");
                        }else{
                            LogUtil.i(TAG, "版本号不同 ,提示用户升级：" + currentVersion);
                            Message msg = Message.obtain();
                            msg.what = 601;
                            handler.sendMessage(msg);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // 待处理
                Message msg = new Message();
                msg.what = 602;
                handler.sendMessage(msg);
                LogUtil.e("注册完成接口", "error:" + error);
            }
        });
        Volley.newRequestQueue(this).add(stringRequest);
    }
    /**
     *
     * 弹出对话框通知用户更新程序
     */
    protected void showUpdataDialog() {
        if(pd!=null&&pd.isShowing()){
            pd.dismiss();
        }
        AlertDialog.Builder builer = new AlertDialog.Builder(this) ;
        builer.setTitle("发现新版本");
        builer.setMessage(MyUtils.decodeString64(info.getDescription()));
        //当点确定按钮时从服务器上下载 新的apk 然后安装
        builer.setPositiveButton("下载更新", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Log.i(TAG,"下载apk,更新");
                downLoadApk();
            }
        });
        //当点取消按钮时进行登录
        builer.setNegativeButton("下次更新", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog dialog = builer.create();
        dialog.show();
    }

    /**
     * 从服务器中下载APK
     */
    protected void downLoadApk() {
        final ProgressDialog pd;    //进度条对话框
        pd = new  ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setMessage("正在下载更新");
        pd.show();
        new Thread(){
            @Override
            public void run() {
                try {
                    File file = MyUpdateUtils.getFileFromServer(Constants.FIND_URL_API_DOWNLOAD, pd);
                    sleep(3000);
                    installApk(file);
                    pd.dismiss(); //结束掉进度条对话框
                } catch (Exception e) {
                    Message msg = new Message();
                    msg.what = 603;
                    handler.sendMessage(msg);
                    pd.dismiss(); //结束掉进度条对话框
                    e.printStackTrace();
                }
            }}.start();
    }
    /**
     * 安装apk
     */
    protected void installApk(File file) {
//        Intent intent = new Intent();
//        //执行动作
//        intent.setAction(Intent.ACTION_VIEW);
//        //执行的数据类型
//        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
//        startActivity(intent);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {     //兼容7.0
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(this, "com.yoyoyt.learner.fileprovider", file);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        if (getPackageManager().queryIntentActivities(intent, 0).size() > 0) {
            startActivity(intent);
        }

    }
}
