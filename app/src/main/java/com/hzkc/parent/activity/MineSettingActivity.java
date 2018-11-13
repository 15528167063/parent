package com.hzkc.parent.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hzkc.parent.R;
import com.hzkc.parent.mina.Constants;
import com.hzkc.parent.utils.DataCleanManager;
import com.hzkc.parent.utils.GlideCacheUtil;
import com.jaeger.library.StatusBarUtil;

public class MineSettingActivity extends BaseActivity implements View.OnClickListener {

    private ImageView iv_notice_on,iv_notice_off;
    private ImageView ivFinish;
    private TextView tvTopTitle;
    private TextView tv_numb;
    private RelativeLayout re_clear;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_mine);
        StatusBarUtil.setColorNoTranslucent(this,getResources().getColor(R.color.blue));
        initView();
        initData();
    }

    private void initView() {
        iv_notice_on = (ImageView) findViewById(R.id.iv_notice_on);
        iv_notice_off = (ImageView) findViewById(R.id.iv_notice_off);
        tv_numb = (TextView) findViewById(R.id.tv_numb);
        re_clear = (RelativeLayout) findViewById(R.id.re_clear);
        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        ivFinish = (ImageView) findViewById(R.id.iv_finish);
        tvTopTitle.setText("应用设置");
        ivFinish.setVisibility(View.VISIBLE);
        ivFinish.setOnClickListener(this);
        iv_notice_on.setOnClickListener(this);
        iv_notice_off.setOnClickListener(this);
        re_clear.setOnClickListener(this);
        boolean notienadle = sp.getBoolean("notienadle", true);
        if(notienadle){
            iv_notice_on.setVisibility(View.VISIBLE);
            iv_notice_off.setVisibility(View.GONE);
        }else {
            iv_notice_on.setVisibility(View.GONE);
            iv_notice_off.setVisibility(View.VISIBLE);
        }
    }

    private void initData() {
        String cacheSize = GlideCacheUtil.getInstance().getCacheSize(this);
        tv_numb.setText(cacheSize);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_finish:
                finish();
                break;
            case R.id.re_clear:
                clearContent();
                break;
            case R.id.iv_notice_on:
                noticeDialog();
                break;
            case R.id.iv_notice_off:
                sp.edit().putBoolean("notienadle",true).commit();
                iv_notice_on.setVisibility(View.VISIBLE);
                iv_notice_off.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    private void clearContent() {
        AlertDialog.Builder builer = new AlertDialog.Builder(this);
        builer.setTitle("温馨提示！");
        builer.setMessage("是否确认清除缓存内容");
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
        builer.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                DataCleanManager.cleanApplicationData(MineSettingActivity.this, Constants.SD_IMAGE_CACHE );
                GlideCacheUtil.getInstance().clearImageDiskCache(MineSettingActivity.this);
                Toast.makeText(MineSettingActivity.this, "清除成功", Toast.LENGTH_SHORT).show();
                tv_numb.setText("0.0Byte");
                dissloading();
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
    private void noticeDialog() {
        AlertDialog.Builder builer = new AlertDialog.Builder(this);
        builer.setTitle("温馨提示！");
        builer.setMessage("关闭通知将有可能不能正常接收通知消息，是否关闭？");
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
        builer.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                sp.edit().putBoolean("notienadle",false).commit();
                iv_notice_on.setVisibility(View.GONE);
                iv_notice_off.setVisibility(View.VISIBLE);
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

}
