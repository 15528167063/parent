package com.hzkc.parent.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hzkc.parent.R;

public class MyUserAgreementActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView ivFinish;
    private TextView tvTopTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_user_agreement);
        initView();
    }
    private void initView() {
        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        ivFinish = (ImageView) findViewById(R.id.iv_finish);

        tvTopTitle.setText("用户最终许可协议");

        ivFinish = (ImageView) findViewById(R.id.iv_finish);
        ivFinish.setVisibility(View.VISIBLE);
        ivFinish.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_finish:
                finish();
                break;
            default:
                break;
        }
    }
}
