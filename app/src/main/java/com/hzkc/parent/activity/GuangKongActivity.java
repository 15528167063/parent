package com.hzkc.parent.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hzkc.parent.R;

import org.simple.eventbus.EventBus;

/**
 * 管控设置
 */
public class GuangKongActivity extends BaseActivity implements View.OnClickListener {
    private TextView tvTopTitle,tv_shili,tv_plan,tv_app;
    private RelativeLayout re_shili,re_plan,re_app;
    private ImageView ivFinish;
    private  String  childuuid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guankongset);
        initView();
    }

    private void initView() {
        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        tv_shili = (TextView) findViewById(R.id.tv_shili);
        tv_plan = (TextView) findViewById(R.id.tv_plan);
        tv_app = (TextView) findViewById(R.id.tv_app);
        re_app = (RelativeLayout) findViewById(R.id.re_app);
        re_plan = (RelativeLayout) findViewById(R.id.re_plan);
        re_shili = (RelativeLayout) findViewById(R.id.re_shili);
        ivFinish = (ImageView) findViewById(R.id.iv_left);
        tvTopTitle.setText("管控设置");
        ivFinish.setVisibility(View.VISIBLE);
        ivFinish.setOnClickListener(this);
        re_app.setOnClickListener(this);
        re_plan.setOnClickListener(this);
        re_shili.setOnClickListener(this);

        childuuid=getIntent().getStringExtra("ChildUUID");
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_left://返回界面
                finish();
                break;
            case R.id.re_shili://视力保护
                Intent intent1 = new Intent(GuangKongActivity.this, EyeProtectActivity.class);
                intent1.putExtra("ChildUUID", childuuid);
                startActivity(intent1);
                break;
            case R.id.re_plan://管控计划
                Intent intent2 = new Intent(GuangKongActivity.this, StopInternetPlanActivity.class);
                intent2.putExtra("ChildUUID", childuuid);
                startActivity(intent2);
                break;
            case R.id.re_app://应用管理
                Intent intent3 = new Intent(GuangKongActivity.this, AppManageActivity.class);
                intent3.putExtra("ChildUUID", childuuid);
                startActivity(intent3);
                break;
            default:
                break;
        }
    }



    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

}
