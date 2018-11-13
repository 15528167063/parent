package com.hzkc.parent.solider.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hzkc.parent.R;
import com.hzkc.parent.activity.BaseActivity;
import com.hzkc.parent.utils.DensityUtil;
import com.jaeger.library.StatusBarUtil;
import com.uuzuche.lib_zxing.activity.CodeUtils;


public class ParentZxingsActivity extends BaseActivity {

    private TextView tvTopTitle;
    private TextView tvDownload;
    private ImageView ivFinish;
    private ImageView ivParentZxing;
    public Bitmap mBitmap = null;
    private static final String TAG = "ParentZxingActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parents_zxing);
        StatusBarUtil.setColorNoTranslucent(this,getResources().getColor(R.color.blue));
        initView();
    }

    private void initView() {

        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        tvDownload = (TextView) findViewById(R.id.tv_download_child);
        ivFinish = (ImageView) findViewById(R.id.iv_finish);
        ivParentZxing = (ImageView) findViewById(R.id.iv_parent_zxing);
        tvTopTitle.setText("士兵绑定");
        ivFinish.setVisibility(View.VISIBLE);
        ivFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ParentZxingsActivity.this, ChildDownloadsActivity.class);
                startActivity(intent);
            }
        });
        /**
         * 根据传入的数据生成二维码图片
         */
        Intent intent = getIntent();
        String url = intent.getStringExtra("parentZxing");

        if (TextUtils.isEmpty(url)) {
            Toast.makeText(ParentZxingsActivity.this, "您的输入为空!", Toast.LENGTH_SHORT).show();
            return;
        }
//        mBitmap = CodeUtils.createImage("parent:"+url, 400, 400, BitmapFactory.decodeResource(getResources(), R.drawable.my_icon));
        mBitmap = CodeUtils.createImage("parent:"+url,  DensityUtil.dip2px(ParentZxingsActivity.this,200),  DensityUtil.dip2px(ParentZxingsActivity.this,200), BitmapFactory.decodeResource(getResources(), R.drawable.my_icon));
        ivParentZxing.setImageBitmap(mBitmap);
    }
}
