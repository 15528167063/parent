package com.hzkc.parent.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hzkc.parent.R;
import com.hzkc.parent.mina.Constants;
import com.hzkc.parent.utils.MyUpdateUtils;
import com.jaeger.library.StatusBarUtil;

import java.io.File;

public class AboutMineActivity extends BaseActivity implements View.OnClickListener {

    private ImageView ivFinish;
    private TextView tvTopTitle;
    private String versionName,updatemessage;
    private int versionCode;
    private TextView tvVersion;
    private TextView tv_xbb_no;
    private TextView tv_xbb;
    private boolean isupdate;
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 603:
                    //下载apk失败
                    Toast.makeText(getApplication(), "下载失败,请在权限管理中打开必要的权限", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_mine);
        StatusBarUtil.setColorNoTranslucent(this,getResources().getColor(R.color.blue));
        isupdate = sp.getBoolean("isupdate", false);
        updatemessage = sp.getString("updatemessage","");
        initView();
        initData();
    }


    private void initView() {
        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        tv_xbb = (TextView) findViewById(R.id.tv_xbb);
        tvVersion = (TextView) findViewById(R.id.tv_bb);
        tv_xbb_no = (TextView) findViewById(R.id.tv_xbb_no);
        ivFinish = (ImageView) findViewById(R.id.iv_finish);
        tvTopTitle.setText("关于我们");

        ivFinish = (ImageView) findViewById(R.id.iv_finish);
        ivFinish.setVisibility(View.VISIBLE);
        if(isupdate){
            tv_xbb_no.setVisibility(View.GONE);
            tv_xbb.setVisibility(View.VISIBLE);
        }else {
            tv_xbb_no.setVisibility(View.VISIBLE);
            tv_xbb.setVisibility(View.GONE);
        }
        ivFinish.setOnClickListener(this);
        tv_xbb.setOnClickListener(this);
    }

    private void initData() {
        // 获取自己的版本信息
        PackageManager pm = getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
            versionCode=packageInfo.versionCode;
            // 版本名
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {

        }
        tvVersion.setText("V"+versionName);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_finish:
                finish();
                break;
            case R.id.tv_xbb:
                updataVerson();
                break;
            default:
                break;
        }
    }

    private void updataVerson() {
        AlertDialog.Builder builer = new AlertDialog.Builder(this);
        builer.setTitle("发现家长端新版本");
        builer.setMessage(updatemessage);
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
                    Toast.makeText(AboutMineActivity.this, "请在权限设置里打开访问SD卡权限", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
        //当点取消按钮时进行登录
        builer.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dissloading();
            }
        });
        AlertDialog dialog = builer.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setIcon(R.drawable.icon);
        dialog.show();
    }


    /**
     * 从服务器中下载APK
     */
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
        new Thread() {
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
            }
        }.start();
    }

    /**
     * 安装apk
     */
    protected void installApk(File file) {
        Intent intent = new Intent();
        //执行动作
        intent.setAction(Intent.ACTION_VIEW);
        //执行的数据类型
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        startActivity(intent);
        finish();
    }
}
