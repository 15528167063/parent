package com.hzkc.parent.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hzkc.parent.R;

public class ConnectMineActivity extends BaseActivity implements View.OnClickListener {
    private TextView tvTopTitle;
    private ImageView ivFinish;
    private LinearLayout ll1;
    private LinearLayout ll2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coneect_mine);
        initview();
    }

    private void initview() {
        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        ivFinish = (ImageView) findViewById(R.id.iv_finish);
        ll1 = (LinearLayout) findViewById(R.id.ll_1);
        ll2 = (LinearLayout) findViewById(R.id.ll_2);
        tvTopTitle.setText("联系我们");
        ivFinish.setVisibility(View.VISIBLE);
        ivFinish.setOnClickListener(this);
        ll1.setOnClickListener(this);
        ll2.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_1://打开微信界面
                Intent intent2 = new Intent();
                ComponentName cmp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI");// 报名该有activity
                intent2.setAction(Intent.ACTION_MAIN);
                intent2.addCategory(Intent.CATEGORY_LAUNCHER);
                intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent2.setComponent(cmp);
                startActivityForResult(intent2, 0);
                break;
            case R.id.ll_2:// 打开QQ群介绍界面(对QQ群号)
                String url = "mqqapi://card/show_pslcard?src_type=internal&version=1&uin=" + 398551126
                        + "&card_type=group&source=qrcode";
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                break;
            case R.id.iv_finish:
                finish();
                break;
            default:
                break;
        }
    }
}
