package com.hzkc.parent.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.hzkc.parent.Bean.CollectionBean;
import com.hzkc.parent.Bean.VipRootBean;
import com.hzkc.parent.R;
import com.hzkc.parent.adapter.MyVipAdapter;
import com.hzkc.parent.adapter.VipListAdapter;
import com.hzkc.parent.appcation.MyApplication;
import com.hzkc.parent.event.BuyMemberSuccess;
import com.hzkc.parent.mina.Constants;
import com.hzkc.parent.utils.AppUtils;
import com.hzkc.parent.utils.GlideCircleTransform;
import com.hzkc.parent.utils.PayUtil;
import com.hzkc.parent.utils.ToastUtils;
import com.jaeger.library.StatusBarUtil;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyMemeberActivity extends BaseActivity implements View.OnClickListener {
    private TextView tvTopTitle,tv_name,tv_zhangdan,tv_time;
    private ImageView ivLeft,iv_head,iv_vip;
    private LinearLayout lin_jiankong,lin_game;
    private ListView lin_container;
    private List<CollectionBean> mList;
    private String txid;
    private String nc;
    private MyVipAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_member);
        StatusBarUtil.setColorNoTranslucent(this,getResources().getColor(R.color.newcolor));
        EventBus.getDefault().register(this);
        initView();
        initData();
    }

    private void initView() {
        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_zhangdan = (TextView) findViewById(R.id.tv_zhangdan);
        tv_time= (TextView) findViewById(R.id.tv_vip_time);
        ivLeft = (ImageView) findViewById(R.id.iv_finish_back);
        iv_head = (ImageView) findViewById(R.id.iv_head);
        iv_vip = (ImageView) findViewById(R.id.iv_vip);
        lin_game=(LinearLayout) findViewById(R.id.lin_game);
        lin_container=(ListView) findViewById(R.id.lin_container);
        tvTopTitle.setText("VIP充值");
        ivLeft.setVisibility(View.VISIBLE);
        tv_zhangdan.setVisibility(View.VISIBLE);
        ivLeft.setOnClickListener(this);
        tv_zhangdan.setOnClickListener(this);
    }
    private void initData() {
        //设置头像名称
        txid = sp.getString("txid", "");
        nc = sp.getString("nc", "");
        tv_name.setText(nc);
        Glide.with(MyMemeberActivity.this).load(Constants.FIND_URL_TX+ txid +".jpg").transform(new GlideCircleTransform(MyMemeberActivity.this)).diskCacheStrategy(DiskCacheStrategy.ALL).error(R.drawable.wddd_icon).into(iv_head);
        //vip续费内容
        getVIPInfor();
    }


    public int flag=1;
    private void initVipView(VipRootBean datas) {
        adapter=new MyVipAdapter(datas.getData().getPrice(),MyMemeberActivity.this,MyMemeberActivity.this);
        lin_container.setAdapter(adapter);
        if(flag==1){
            View view= LayoutInflater.from(MyMemeberActivity.this).inflate(R.layout.item_footer,null);
            initViewLayout(view,datas);
            lin_container.addFooterView(view);
        }
    }

    private void initViewLayout(View view,VipRootBean datas) {
        RecyclerView recycle= (RecyclerView)view.findViewById(R.id.recycle);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recycle.setLayoutManager(layoutManager);
        VipListAdapter adapter=new VipListAdapter(datas.getData().getMdllist(),this);
        recycle.setAdapter(adapter);

    }
    public int number=1;
    public int singleprice=60;
    public  TextView tv_price,tv_youhui;
    public TextView tv_num;
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_finish_back:
                finish();
                break;
            case R.id.tv_zhangdan:
                Intent intent=new Intent(MyMemeberActivity.this,OrderHistoryActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_add:
                number++;
                tv_num.setText(number+"");
                tv_price.setText(number*singleprice+"元");

                break;
            case R.id.tv_sub:
                if(number==1){
                    return;
                }
                number--;
                tv_num.setText(number+"", TextView.BufferType.EDITABLE);
                tv_price.setText(number*singleprice+"元");
                break;
            default:
                break;
        }
    }
    private void getVIPInfor() {
        showLoading();
        final String userid= sp.getString("parentUUID", "");
        String url=Constants.PHP_URL+"v"+ AppUtils.getVerName()+"/vippage";;
        Log.e("vip模块", "url:" + url);
        StringRequest postsr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("vip模块", "vip模块:" + response);
                dissloading();
                VipRootBean vipRoot = new Gson().fromJson(response, VipRootBean.class);
                frashView(vipRoot);
                initVipView(vipRoot);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                dissloading();
                ToastUtils.showToast(MyApplication.getContext(), "加载数据失败");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //创建一个集合，放的是keyvalue的key是参数名与value是参数值
                Map<String, String> map = new HashMap<String, String>();
                map.put("user_id", userid);
                map.put("goods_src", "Android");
                return map;
            }
        };
        postsr.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.getRequestQueue().add(postsr);
    }



    private void frashView(VipRootBean vipRoot) {
        if(vipRoot.getData().getVipinfo().getViplvl().equals("0")){
            tv_time.setText("开通VIP会员");
            iv_vip.setImageResource(R.drawable.vip_no);
        }else if(vipRoot.getData().getVipinfo().getViplvl().equals("1")){
            tv_time.setText(vipRoot.getData().getVipinfo().getVipendtime()+"到期");
            iv_vip.setImageResource(R.drawable.mine_vip);
        } else if(vipRoot.getData().getVipinfo().getViplvl().equals("2")){
            tv_time.setText("vip试用"+vipRoot.getData().getVipinfo().getVipendtime()+"到期");
            iv_vip.setImageResource(R.drawable.vip_no);
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
    @Subscriber(mode = ThreadMode.ASYNC, tag = "pay")
    public void onDotGone(BuyMemberSuccess item) {
        if (item != null) {
            Log.e("-----getVIPInfor","aaqaaaaa");
            flag=2;
            getVIPInfor();
        }
    }

    @Subscriber(mode = ThreadMode.ASYNC, tag = "outdata")
    public void onDotGone(PayUtil.outDataSuccess item) {
        if (item != null) {
            Toast.makeText(this, "该活动已过期，请重新选择购买类型", Toast.LENGTH_SHORT).show();
            flag=2;
            getVIPInfor();
        }
    }
}
