package com.hzkc.parent.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hzkc.parent.R;
import com.jaeger.library.StatusBarUtil;

public class HelpCenterActivity extends BaseActivity implements View.OnClickListener {
    private TextView tvTopTitle ,iv_01,iv_02,iv_03,iv_04,iv_05;
    private ImageView ivFinish;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        ininView();
        initData();
    }

    private void ininView() {
        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        ivFinish = (ImageView) findViewById(R.id.iv_finish_back);
        iv_01 = (TextView) findViewById(R.id.iv_01);
        iv_02 = (TextView) findViewById(R.id.iv_02);
        iv_03 = (TextView) findViewById(R.id.iv_03);
        iv_04 = (TextView) findViewById(R.id.iv_04);
        iv_05 = (TextView) findViewById(R.id.iv_05);
        StatusBarUtil.setColorNoTranslucent(this,getResources().getColor(R.color.newcolor));
        tvTopTitle.setText("帮助中心");
        ivFinish.setVisibility(View.VISIBLE);

        iv_01.setOnClickListener(this);
        iv_02.setOnClickListener(this);
        iv_03.setOnClickListener(this);
        iv_04.setOnClickListener(this);
        iv_05.setOnClickListener(this);
        ivFinish.setOnClickListener(this);

    }

    private void initData() {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_finish_back:
                finish();
                break;
            case R.id.iv_01:
                Intent intent1 = new Intent(HelpCenterActivity.this, FindArticleActivity.class);
                intent1.putExtra("type","1");
                startActivity(intent1);
                break;
            case R.id.iv_02:
                Intent intent2 = new Intent(HelpCenterActivity.this, FindArticleActivity.class);
                intent2.putExtra("type","2");
                startActivity(intent2);
                break;
            case R.id.iv_03:
                Intent intent3 = new Intent(HelpCenterActivity.this, FindArticleActivity.class);
                intent3.putExtra("type","3");
                startActivity(intent3);
                break;
            case R.id.iv_04:
                Intent intent4 = new Intent(HelpCenterActivity.this, FindArticleActivity.class);
                intent4.putExtra("type","4");
                startActivity(intent4);
                break;
            case R.id.iv_05:
                String url = "mqqapi://card/show_pslcard?src_type=internal&version=1&uin=" + "2492288105"
                        + "&card_type=wpa&source=qrcode";
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                break;
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
