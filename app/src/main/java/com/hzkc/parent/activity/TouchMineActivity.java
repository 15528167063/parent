package com.hzkc.parent.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hzkc.parent.R;
import com.hzkc.parent.appcation.MyApplication;
import com.hzkc.parent.utils.LogUtil;
import com.hzkc.parent.utils.ToastUtils;
import com.jaeger.library.StatusBarUtil;

import java.util.List;

public class TouchMineActivity extends BaseActivity implements View.OnClickListener {

    private TextView tvTopTitle;
    private ImageView ivFinish;
    private TextView tvConnectPhone;
    private LinearLayout llConnectPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_touch_mine);
        StatusBarUtil.setColorNoTranslucent(this,getResources().getColor(R.color.blue));
        initView();
    }


    private void initView() {
        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        ivFinish = (ImageView) findViewById(R.id.iv_finish);
        tvConnectPhone = (TextView) findViewById(R.id.tv_connect_phone);
        llConnectPhone = (LinearLayout) findViewById(R.id.ll_connect_phone);
        tvTopTitle.setText("联系我们");
        tvConnectPhone.setText("465431753");
        ivFinish.setVisibility(View.VISIBLE);
        ivFinish.setOnClickListener(this);
        llConnectPhone.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_finish:
                finish();
                break;
            case R.id.ll_connect_phone:
                if(isQQClientAvailable(this)) {
                    String url = "mqqapi://card/show_pslcard?src_type=internal&version=1&uin=" + 465431753
                            + "&card_type=wpa&source=qrcode";
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                }else{
                    ToastUtils.showToast(MyApplication.getContext(), "您还没有安装QQ，请先安装QQ客户端");
                }
                break;
            default:
                break;
        }
    }
    /**
     * 判断 用户是否安装QQ客户端
     */
    public static boolean isQQClientAvailable(Context context) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                LogUtil.e("pn = "+pn);
                if (pn.equalsIgnoreCase("com.tencent.qqlite") || pn.equalsIgnoreCase("com.tencent.mobileqq")) {
                    return true;
                }
            }
        }
        return false;
    }
}
