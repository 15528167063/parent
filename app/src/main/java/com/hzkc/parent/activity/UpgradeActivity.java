package com.hzkc.parent.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

import com.hzkc.parent.MainActivity;
import com.hzkc.parent.R;


public class UpgradeActivity extends BaseActivity {


    AlertDialog ad = null;

    private void showProcess(String titile, String message) {
        ad.setTitle(titile);
        ad.setMessage(message);
        ad.show();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_upgrade);
        ad = new AlertDialog.Builder(this).setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(ad!=null&&ad.isShowing()){
                    ad.dismiss();
                }
                Intent intent = new Intent(UpgradeActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }).create();
        ad.setCanceledOnTouchOutside(false);
        ad.setIcon(R.drawable.icon);
        ad.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                    if(ad!=null&&ad.isShowing()){
                        ad.dismiss();
                    }
                    finish();
                }
                return false;
            }
        });
//        String title = getIntent().getStringExtra("title");
        String msg = getIntent().getStringExtra("msg");
//        showProcess(title, msg);
        showProcess("优成长","您的孩子 "+msg+" 申请使用手机,请前往设置！");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
