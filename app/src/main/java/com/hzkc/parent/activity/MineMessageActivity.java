package com.hzkc.parent.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hzkc.parent.R;
import com.hzkc.parent.jsondata.CmdCommon;

import org.simple.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MineMessageActivity extends BaseActivity implements View.OnClickListener {
    private TextView tvTopTitle;
    private ImageView ivFinish;
    private TextView tvSave;
    private ImageView ivAccountIcon;
    private TextView tvMyAccount;
    private ImageView ivVipIcon;
    private TextView tvVipTime;
    private TextView tvVipOuttime;
    private LinearLayout llTaocan1;
    private ImageView ivTaocan1;
    private LinearLayout llTaocan2;
    private ImageView ivTaocan2;
    private LinearLayout llTaocan3;
    private ImageView ivTaocan3;
    private LinearLayout llTaocan4;
    private ImageView ivTaocan4;
    private LinearLayout llWx;
    private ImageView ivWx;
    private LinearLayout llZfb;
    private ImageView ivZfb;
    private TextView tvSubmitPay;
    private String taocan;
    private String zhifu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_message);
        initView();
        initData();
    }

    private void initView() {
        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        ivFinish = (ImageView) findViewById(R.id.iv_finish);
        tvSave = (TextView) findViewById(R.id.tv_save);
        ivAccountIcon = (ImageView) findViewById(R.id.iv_account_icon);
        tvMyAccount = (TextView) findViewById(R.id.tv_my_account);
        ivVipIcon = (ImageView) findViewById(R.id.iv_vip_icon);
        tvVipTime = (TextView) findViewById(R.id.tv_vip_time);
        tvVipOuttime = (TextView) findViewById(R.id.tv_vip_outtime);
        llTaocan1 = (LinearLayout) findViewById(R.id.ll_taocan_1);
        ivTaocan1 = (ImageView) findViewById(R.id.iv_taocan_1);
        llTaocan2 = (LinearLayout) findViewById(R.id.ll_taocan_2);
        ivTaocan2 = (ImageView) findViewById(R.id.iv_taocan_2);
        llTaocan3 = (LinearLayout) findViewById(R.id.ll_taocan_3);
        ivTaocan3 = (ImageView) findViewById(R.id.iv_taocan_3);
        llTaocan4 = (LinearLayout) findViewById(R.id.ll_taocan_4);
        ivTaocan4 = (ImageView) findViewById(R.id.iv_taocan_4);
        llWx = (LinearLayout) findViewById(R.id.ll_wx);
        ivWx = (ImageView) findViewById(R.id.iv_wx);
        llZfb = (LinearLayout) findViewById(R.id.ll_zfb);
        ivZfb = (ImageView) findViewById(R.id.iv_zfb);
        tvSubmitPay = (TextView) findViewById(R.id.tv_submit_pay);

        tvSave.setText("订单历史");
        tvTopTitle.setText("我的订单");

        ivFinish.setVisibility(View.VISIBLE);
        tvSave.setVisibility(View.VISIBLE);
        tvSave.setOnClickListener(this);
        ivFinish.setOnClickListener(this);
        llTaocan1.setOnClickListener(this);
        llTaocan2.setOnClickListener(this);
        llTaocan3.setOnClickListener(this);
        llTaocan4.setOnClickListener(this);
        llWx.setOnClickListener(this);
        llZfb.setOnClickListener(this);
        tvSubmitPay.setOnClickListener(this);
    }

    private void initData() {
        String phoneNum = sp.getString("phoneNum", "");
        String childSex = sp.getString("childSex", "");
        tvMyAccount.setText(phoneNum);
        if (childSex.equals(CmdCommon.FLAG_BOY)) {//男孩
            ivAccountIcon.setImageResource(R.drawable.wddd_icon_boy);
        } else if (childSex.equals(CmdCommon.FLAG_GIRL)) {//女孩
            ivAccountIcon.setImageResource(R.drawable.wddd_icon_girl);
        } else {//字段不存在
            ivAccountIcon.setImageResource(R.drawable.wddd_icon);
        }
        checkVip();
        taocan="1000";
        zhifu="10";
    }
    /**
     * 判断是不是vip
     * */
    private void checkVip() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
        Date nowDate = new Date();
        long t1 = nowDate.getTime();
        String vipDate = sp.getString("viptime", "");
        long t2 = Long.parseLong(vipDate);
        if(t1-t2>0){//过期
            ivVipIcon.setImageResource(R.drawable.minepager_vip_outtime_icon);
            tvVipOuttime.setVisibility(View.VISIBLE);
        }else {//未过期
            ivVipIcon.setImageResource(R.drawable.minepager_vip_icon);
            tvVipOuttime.setVisibility(View.GONE);
        }
        String vipTime = df.format(t2);
        tvVipTime.setText(vipTime);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_finish:
                finish();
                break;
            case R.id.tv_save://订单历史
                Intent intent = new Intent(MineMessageActivity.this, OrderHistoryActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_taocan_1://套餐一
                setTacocanState(ivTaocan1);
                taocan="1000";
                break;
            case R.id.ll_taocan_2://套餐二
                setTacocanState(ivTaocan2);
                taocan="0100";
                break;
            case R.id.ll_taocan_3://套餐三
                setTacocanState(ivTaocan3);
                taocan="0010";
                break;
            case R.id.ll_taocan_4://套餐四
                setTacocanState(ivTaocan4);
                taocan="0001";
                break;
            case R.id.ll_wx://微信
                setPayState(ivWx);
                zhifu="10";
                break;
            case R.id.ll_zfb://支付宝
                setPayState(ivZfb);
                zhifu="01";
                break;
            case R.id.tv_submit_pay://提交订单
                Submit();
                break;
            default:
                break;
        }
    }
    /**
     * 设置支付方式
     * */
    private void setPayState(ImageView iv) {
        ivWx.setImageResource(R.drawable.white_list_check_n);
        ivZfb.setImageResource(R.drawable.white_list_check_n);
        iv.setImageResource(R.drawable.white_list_check_y);
    }

    /**
     * 设置套餐
     * */
    private void setTacocanState(ImageView iv) {
        ivTaocan1.setImageResource(R.drawable.white_list_check_n);
        ivTaocan2.setImageResource(R.drawable.white_list_check_n);
        ivTaocan3.setImageResource(R.drawable.white_list_check_n);
        ivTaocan4.setImageResource(R.drawable.white_list_check_n);
        iv.setImageResource(R.drawable.white_list_check_y);
    }
    /**
     * 提交订单
     * */
    private void Submit() {
        String selectTaocan="套餐1";
        String selectPay="微信";
        if(taocan.equals("1000")){
            selectTaocan = "套餐1";
        } else if(taocan.equals("0100")){
            selectTaocan = "套餐2";
        } else if(taocan.equals("0010")){
            selectTaocan = "套餐3";
        } else if(taocan.equals("0001")){
            selectTaocan = "套餐4";
        }
        if(zhifu.equals("10")){
            selectPay = "微信";
        } else if(zhifu.equals("01")){
            selectPay = "支付宝";
        }
        if(selectPay.equals("支付宝")){
            Toast.makeText(this,"支付宝支付方式暂不支持，请选择其他支付方式",Toast.LENGTH_SHORT).show();
        }
        Toast.makeText(this,"selectTaocan:"+selectTaocan+",selectPay:"+selectPay,Toast.LENGTH_SHORT).show();
    }
    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
