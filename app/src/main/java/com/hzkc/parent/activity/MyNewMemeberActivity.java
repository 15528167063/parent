package com.hzkc.parent.activity;

import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import com.hzkc.parent.Bean.VipPriceBean;
import com.hzkc.parent.Bean.VipRootBean;
import com.hzkc.parent.R;
import com.hzkc.parent.adapter.MyNewVipAdapter;
import com.hzkc.parent.adapter.VipListAdapter;
import com.hzkc.parent.appcation.MyApplication;
import com.hzkc.parent.event.BuyMemberSuccess;
import com.hzkc.parent.mina.Constants;
import com.hzkc.parent.utils.AppUtils;
import com.hzkc.parent.utils.GlideCircleTransform;
import com.hzkc.parent.utils.PayUtil;
import com.hzkc.parent.utils.PayUtils;
import com.hzkc.parent.utils.ToastUtils;
import com.jaeger.library.StatusBarUtil;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyNewMemeberActivity extends BaseActivity implements View.OnClickListener {
    private TextView tvTopTitle,tv_name,tv_time,tv_kaitong;
    private RelativeLayout re_wx,re_apily;
    private ImageView ivLeft,iv_head_vip,iv_vip;
    private ImageView iv_wx,iv_apily;
    private LinearLayout lin_jiankong,lin_game,lin_sc;
    private HorizontalScrollView lin_vip_infor;
    private ListView lin_container;
    private List<CollectionBean> mList;
    private String txid;
    private String nc;
    private String parentUUID;
    private MyNewVipAdapter adapter;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 101:
                    orderInit();
                    break;
                case 102:
                    ToastUtils.showToast(MyApplication.getContext(),"刷新数据失败,请检查网络稍后再试");
                    mSwipeRefreshLayout.setRefreshing(false);
                    break;
                default:
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_new_member);
        StatusBarUtil.setColorNoTranslucent(this,getResources().getColor(R.color.newcolor));
        EventBus.getDefault().register(this);
        initView();
        initData();
    }
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private void initView() {
        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_time= (TextView) findViewById(R.id.tv_vip_time);
        tv_kaitong= (TextView) findViewById(R.id.bt_kaitong);
        re_wx= (RelativeLayout) findViewById(R.id.re_wx);
        re_apily= (RelativeLayout) findViewById(R.id.apily);
        tv_time= (TextView) findViewById(R.id.tv_vip_time);
        ivLeft = (ImageView) findViewById(R.id.iv_finish_back);
        iv_wx = (ImageView) findViewById(R.id.iv_wx);
        iv_apily = (ImageView) findViewById(R.id.iv_apily);
        iv_head_vip = (ImageView) findViewById(R.id.iv_head_vip);
        iv_vip = (ImageView) findViewById(R.id.iv_vip);
        lin_game=(LinearLayout) findViewById(R.id.lin_game);
        lin_sc=(LinearLayout) findViewById(R.id.lin_sc);
        lin_container=(ListView) findViewById(R.id.lin_container);
        lin_vip_infor=(HorizontalScrollView) findViewById(R.id.lin_vip_infor);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.sf_refresh);
        tvTopTitle.setText("VIP充值");
        ivLeft.setVisibility(View.VISIBLE);
        ivLeft.setOnClickListener(this);
        re_wx.setOnClickListener(this);
        re_apily.setOnClickListener(this);
        tv_kaitong.setOnClickListener(this);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(true);
                Message msg = Message.obtain();
                msg.what = 101;
                handler.sendMessage(msg);
            }
        });
    }
    private void initData() {
        //设置头像名称
         parentUUID= sp.getString("parentUUID", "");
        txid = sp.getString("txid", "");
        nc = sp.getString("nc", "");
        tv_name.setText(nc);
        String headimages = sp.getString("headimage", "");
        Glide.with(this).load(headimages).transform(new GlideCircleTransform(MyNewMemeberActivity.this)).diskCacheStrategy(DiskCacheStrategy.ALL).error(R.drawable.mine_head_02).into(iv_head_vip);
        //vip续费内容
        getVIPInfor();
    }

    public int number=1;
    public int singleprice=60;
    public  TextView tv_price,tv_youhui;
    public TextView tv_num;
    private  int payway=0; //支付方式0是微信 1是支付宝
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_finish_back:
                finish();
                break;
            case R.id.apily:
                payway=1;
                iv_wx.setImageResource(R.drawable.mine_check_no);
                iv_apily.setImageResource(R.drawable.mine_check);
                break;
            case R.id.re_wx:
                payway=0;
                iv_wx.setImageResource(R.drawable.mine_check);
                iv_apily.setImageResource(R.drawable.mine_check_no);
                break;
            case R.id.bt_kaitong:
                if(payway==0){
                    PayUtils.payWechat(vipPriceBean.getGoods_id(),parentUUID ,"0");
                }else {
                    PayUtils.payali(vipPriceBean.getGoods_id(), parentUUID,this);
                }
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
//                VipRootBean vipRoot = new Gson().fromJson(response, VipRootBean.class);
                VipRootBean vipRoot =null;
                try {
                     vipRoot = new Gson().fromJson(response, VipRootBean.class);
                }catch (Exception e){
                    return;
                }
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
                map.put("type", "vipinfo");
                return map;
            }
        };
        postsr.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.getRequestQueue().add(postsr);
    }
    public int flag=1;
    private void initVipView(VipRootBean datas) {
        initVipLayout(datas);
        if(flag==1){
            initViewLayout(datas);
        }
    }
    private  int lastposition=0;
    private VipPriceBean vipPriceBean;
    private void initVipLayout(VipRootBean datas) {
        List<VipPriceBean> price = datas.getData().getPrice();
        initVipData(price,datas);

    }

    /**
     * vip价格初始化
     */
    private void initVipData(final List<VipPriceBean> price, VipRootBean datas) {
        for (int i = 0; i < price.size(); i++) {
            View view = LayoutInflater.from(this).inflate(R.layout.item_vip_list, null);
            ImageView iv_tj=(ImageView) view.findViewById(R.id.iv_tj);
            TextView tv_name=(TextView) view.findViewById(R.id.tv_name);
            TextView tv_price_1=(TextView) view.findViewById(R.id.tv_price_1);
            TextView tv_price_2=(TextView) view.findViewById(R.id.tv_price_2);
            TextView tv_price_3=(TextView) view.findViewById(R.id.tv_price_3);
            vipPriceBean = price.get(0);
            tv_name.setText(price.get(i).getGoods_name());
            tv_price_1.setText(price.get(i).getPrice());
            tv_price_2.setText(price.get(i).getOriginal_price());
            int a = Integer.parseInt(price.get(i).getVip_time());
            Double b = Double.parseDouble(price.get(i).getPrice());
            double v = oneAfterPoint(b / a);
            doubles.add(v);
            tv_price_3.setText("折合 ￥"+v+"/月 ");
            tv_price_2.getPaint().setAntiAlias(true);//抗锯齿
            tv_price_2.getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG); //中划线
            final LinearLayout lin_all=(LinearLayout) view.findViewById(R.id.lin_all);
            if (i == 0) {
                lin_all.setBackgroundResource(R.drawable.shape_vip_bg);
                tv_kaitong.setText("立即以"+price.get(0).getPrice()+"元开通");
            }else {
                lin_all.setBackgroundResource(R.drawable.shape_vip_bg1);
            }
            final int finalI = i;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(lastposition>=0){
                        lin_sc.getChildAt(lastposition).findViewById(R.id.lin_all).setBackgroundResource(R.drawable.shape_vip_bg1);
                    }
                    vipPriceBean = price.get(finalI);
                    lin_all.setBackgroundResource(R.drawable.shape_vip_bg);
                    lastposition=finalI;
                    tv_kaitong.setText("立即以"+ vipPriceBean.getPrice()+"元开通");
                }
            });
            lin_sc.addView(view);
        }
        Double min = Collections.min(doubles);
        int index= doubles.indexOf(min);
        lin_sc.getChildAt(index).findViewById(R.id.iv_tj).setVisibility(View.VISIBLE);
        lin_sc.getChildAt(index).findViewById(R.id.tv_low).setVisibility(View.VISIBLE);
    }
    private List<Double>doubles=new ArrayList<>();
    private void initViewLayout(VipRootBean datas) {
        RecyclerView recycle= (RecyclerView)findViewById(R.id.recycle);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 4){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        recycle.setLayoutManager(layoutManager);
        VipListAdapter adapter=new VipListAdapter(datas.getData().getMdllist(),this);
        recycle.setAdapter(adapter);
    }


    private void frashView(VipRootBean vipRoot) {
        if(vipRoot.getData().getVipinfo().getViplvl().equals("0")){
            tv_time.setText("立即开通会员");
            iv_vip.setImageResource(R.drawable.sy_vip_off);
        }else if(vipRoot.getData().getVipinfo().getViplvl().equals("1")){
            String vipendtime = vipRoot.getData().getVipinfo().getVipendtime();
            tv_time.setText("到期日:"+vipendtime);
            iv_vip.setImageResource(R.drawable.sy_vip_on);
        } else if(vipRoot.getData().getVipinfo().getViplvl().equals("2")){
            String vipendtime = vipRoot.getData().getVipinfo().getVipendtime();
            tv_time.setText("试用期:"+vipendtime);
            iv_vip.setImageResource(R.drawable.sy_vip_on);
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
    private void orderInit() {
        ToastUtils.showToast(MyApplication.getContext(),"刷新完成");
        mSwipeRefreshLayout.setRefreshing(false);
    }
    public double oneAfterPoint(double d){
        String strD = String.valueOf(d*10);
        String[] strArr = strD.split("\\.");
        return Double.parseDouble(strArr[0])/10;
    }
}
