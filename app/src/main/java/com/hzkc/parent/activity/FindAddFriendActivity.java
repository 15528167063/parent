package com.hzkc.parent.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.hzkc.parent.R;
import com.hzkc.parent.mina.Constants;
import com.hzkc.parent.utils.LogUtil;

public class FindAddFriendActivity extends BaseActivity implements View.OnClickListener {

    private TextView tvTopTitle;
    private TextView tvFinishMine;
    private SimpleDraweeView ivPyIcon;
    private TextView tvPyName;
    private TextView tvPyUserid;
    private TextView btAddPy;
    private String nc;
    private String pic;
    private String userid;
    private ProgressDialog progressDialog;
    private String parentUUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_add_friend);
        initView();
    }

    private void initView() {
        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        tvFinishMine = (TextView) findViewById(R.id.tv_finish_mine);
        ivPyIcon = (SimpleDraweeView) findViewById(R.id.iv_py_icon);
        tvPyName = (TextView) findViewById(R.id.tv_py_name);
        tvPyUserid = (TextView) findViewById(R.id.tv_py_userid);
        btAddPy = (TextView) findViewById(R.id.bt_add_py);

        tvTopTitle.setText("基本信息");
        tvFinishMine.setText("发现");
        tvFinishMine.setVisibility(View.VISIBLE);
        nc = getIntent().getStringExtra("nc");
        pic = getIntent().getStringExtra("pic");
        userid = getIntent().getStringExtra("userid");

        ivPyIcon.setImageURI(Constants.FIND_URL_TX+pic+".jpg");
        tvPyName.setText("用户昵称："+nc);
        tvPyUserid.setText("用户PID："+userid);
        parentUUID = sp.getString("parentUUID", "");

        btAddPy.setOnClickListener(this);
        tvFinishMine.setOnClickListener(this);
        LogUtil.e("FindAddFriendActivity","nc:"+nc+",pic:"+pic+",userid:"+userid);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_finish_mine:
                finish();
                break;
            case R.id.bt_add_py://添加好友
//                addFriend();
                break;
            default:
                break;
        }
    }
//    /**
//     * 添加好友
//     * */
//    private void addFriend() {
//        if(parentUUID.equals(userid)){
//            ToastUtils.showToast(MyApplication.getContext(), "不能添加自己为好友");
//            return;
//        }
//        progressDialog = new ProgressDialog(this);
//        progressDialog.setMessage("正在发送请求……");
//        progressDialog.setCanceledOnTouchOutside(false);
//        progressDialog.show();
//        new Thread(new Runnable() {
//            public void run() {
//                try {
//                    //输入添加验证
//                    EMClient.getInstance().contactManager().addContact(userid,
//                            "加个好友呗");
//                    runOnUiThread(new Runnable() {
//                        public void run() {
//                            progressDialog.dismiss();
//                            String s1 = "发送请求成功,等待对方验证";
//                            EaseUser eu = new EaseUser(userid);
//                            eu.setAvatar(Constants.FIND_URL_TX+pic+".jpg");
//                            eu.setOriginalAvatar(pic);
//                            eu.setNickname(nc);
//                            ToastUtils.showToast(MyApplication.getContext(), s1);
//                            finish();
//                        }
//                    });
//                } catch (final Exception e) {
//                    runOnUiThread(new Runnable() {
//                        public void run() {
//                            progressDialog.dismiss();
//                            //String s2 = "请求添加好友失败";
//                            String s2 = "请求添加好友失败";
//                            //Toast.makeText(getApplicationContext(), s2 + e.getMessage(), Toast.LENGTH_LONG).show();
//                            ToastUtils.showToast(MyApplication.getContext(), s2);
//                            LogUtil.e("FindAddFriendActivity","请求添加好友失败:"+e.getMessage());
//                        }
//                    });
//                }
//            }
//        }).start();
//    }
}
