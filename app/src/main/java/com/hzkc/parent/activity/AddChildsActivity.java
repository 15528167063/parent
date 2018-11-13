package com.hzkc.parent.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hzkc.parent.R;

/**
 * 添加孩子界面
 */
public class AddChildsActivity extends BaseActivity implements View.OnClickListener {
    private TextView tv_btn1,tv_btn2,tvTopTitle;
    private ImageView ivFinish;
    private static final String TAG = "AddChildActivity";
    private String parentUUID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_children);
        initView();
        initData();
    }
    private void initView() {
        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        ivFinish = (ImageView) findViewById(R.id.iv_left);
        tv_btn1 = (TextView) findViewById(R.id.tv_btn1);
        tv_btn2 = (TextView) findViewById(R.id.tv_btn2);

        tvTopTitle.setText("添加小孩");
        ivFinish.setVisibility(View.VISIBLE);
        ivFinish.setOnClickListener(this);
        tv_btn1.setOnClickListener(this);
        tv_btn2.setOnClickListener(this);
    }
    private void initData() {
        parentUUID = getIntent().getStringExtra("parentZxing");
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_left://返回界面
                finish();
                break;
            case R.id.tv_btn1://返回界面
                Intent intent = new Intent(AddChildsActivity.this, ParentZxingActivity.class);
                intent.putExtra("parentZxing", parentUUID);
                startActivity(intent);
                break;
            case R.id.tv_btn2://返回界面
                Intent intent1 = new Intent(AddChildsActivity.this, ChildDownloadActivity.class);
                startActivity(intent1);
                break;
            default:
                break;
        }
    }
}
