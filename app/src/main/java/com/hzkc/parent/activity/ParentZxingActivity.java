package com.hzkc.parent.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hzkc.parent.R;
import com.hzkc.parent.mina.Constants;
import com.hzkc.parent.utils.DensityUtil;
import com.jaeger.library.StatusBarUtil;
import com.uuzuche.lib_zxing.activity.CodeUtils;


public class ParentZxingActivity extends BaseActivity {

    private TextView tvTopTitle;
    private TextView tvDownload;
    private ImageView ivFinish;
    private ImageView ivParentZxing,ivDownZxing;
    public Bitmap mBitmap = null;
    private static final String TAG = "ParentZxingActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_zxing);
        StatusBarUtil.setColorNoTranslucent(this,getResources().getColor(R.color.newcolor));
        initView();
    }

    private void initView() {

        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        tvDownload = (TextView) findViewById(R.id.tv_download_child);
        ivFinish = (ImageView) findViewById(R.id.iv_finish_back);
        ivParentZxing = (ImageView) findViewById(R.id.iv_parent_zxing);
        ivDownZxing = (ImageView) findViewById(R.id.iv_down_zxing);
        tvTopTitle.setText("添加小孩");
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
                Intent intent = new Intent(ParentZxingActivity.this, ChildDownloadActivity.class);
                startActivity(intent);
            }
        });
        /**
         * 根据传入的数据生成二维码图片
         */
        Intent intent = getIntent();
        String url = intent.getStringExtra("parentZxing");

        if (TextUtils.isEmpty(url)) {
            Toast.makeText(ParentZxingActivity.this, "您的输入为空!", Toast.LENGTH_SHORT).show();
            return;
        }


        Log.e("parent","parent:"+url);
        mBitmap = CodeUtils.createImage("parent:"+url,  DensityUtil.dip2px(ParentZxingActivity.this,200),  DensityUtil.dip2px(ParentZxingActivity.this,200), BitmapFactory.decodeResource(getResources(), R.drawable.sy_hh_on));
        ivParentZxing.setImageBitmap(mBitmap);

        String urls = Constants.FIND_URL_API_CHILD_DOWNLOAD;
        Log.e("child","child:"+urls);
        mBitmap = CodeUtils.createImage(urls, DensityUtil.dip2px(ParentZxingActivity.this,200), DensityUtil.dip2px(ParentZxingActivity.this,200), BitmapFactory.decodeResource(getResources(), R.drawable.sy_hh_on));
        ivDownZxing.setImageBitmap(mBitmap);
    }
}
