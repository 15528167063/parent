package com.hzkc.parent.activity;

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
import com.uuzuche.lib_zxing.activity.CodeUtils;

public class ChildLoadActivity extends BaseActivity {

    private TextView tvTopTitle;
    private ImageView ivFinish;
    private ImageView ivLoadZxing;
    public Bitmap mBitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_load);

        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        ivFinish = (ImageView) findViewById(R.id.iv_finish);
        ivLoadZxing = (ImageView) findViewById(R.id.iv_load_zxing);

        tvTopTitle.setText("孩子端下载");
        ivFinish.setVisibility(View.VISIBLE);
        ivFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        /**
         * 根据传入的数据生成二维码图片
         */
        Intent intent = getIntent();
        String url = intent.getStringExtra("childZxing");
        if (TextUtils.isEmpty(url)) {
            Toast.makeText(ChildLoadActivity.this, "您的输入为空!", Toast.LENGTH_SHORT).show();
            return;
        }
        mBitmap = CodeUtils.createImage(url, 400, 400, BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        ivLoadZxing.setImageBitmap(mBitmap);
    }
}
