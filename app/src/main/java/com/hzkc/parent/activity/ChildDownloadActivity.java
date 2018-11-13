package com.hzkc.parent.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hzkc.parent.R;
import com.hzkc.parent.mina.Constants;
import com.hzkc.parent.utils.DensityUtil;
import com.hzkc.parent.utils.LogUtil;
import com.jaeger.library.StatusBarUtil;
import com.uuzuche.lib_zxing.activity.CodeUtils;

public class ChildDownloadActivity extends BaseActivity {

    private TextView tvTopTitle;
    private ImageView ivFinish;
    private ImageView ivParentZxing;
    public Bitmap mBitmap = null;
    private static final String TAG = "ParentZxingActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_download);
        StatusBarUtil.setColorNoTranslucent(this,getResources().getColor(R.color.newcolor));
        initView();
    }

    private void initView() {

        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        ivFinish = (ImageView) findViewById(R.id.iv_finish_back);
        ivParentZxing = (ImageView) findViewById(R.id.iv_parent_zxing);
        tvTopTitle.setText("下载孩子端");
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
        String url = Constants.FIND_URL_API_CHILD_DOWNLOAD;
        LogUtil.e(TAG, "initView: "+url);
//        mBitmap = CodeUtils.createImage(url, 400, 400, BitmapFactory.decodeResource(getResources(), R.drawable.my_icon));
        mBitmap = CodeUtils.createImage(url, DensityUtil.dip2px(ChildDownloadActivity.this,200), DensityUtil.dip2px(ChildDownloadActivity.this,200), BitmapFactory.decodeResource(getResources(), R.drawable.my_icon));
        ivParentZxing.setImageBitmap(mBitmap);
    }
}
