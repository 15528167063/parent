package com.hzkc.parent.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.hzkc.parent.Bean.ApilRootBean;
import com.hzkc.parent.R;
import com.hzkc.parent.appcation.MyApplication;
import com.hzkc.parent.event.BuyMemberSuccess;
import com.hzkc.parent.mina.Constants;
import com.hzkc.parent.utils.AppUtils;
import com.hzkc.parent.utils.ToastUtils;
import com.jaeger.library.StatusBarUtil;

import org.simple.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

public class ParResultActivity extends BaseActivity implements View.OnClickListener {
    private TextView tv_sucess;
    private ImageView iv_finish;
    private TextView tv_finish;
    public String trade_no,out_trade_no;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_history);
        StatusBarUtil.setColorNoTranslucent(this,getResources().getColor(R.color.blue));
        initView();
        initData();
    }

    private void initView() {
        trade_no=getIntent().getStringExtra("trade_no");
        out_trade_no=getIntent().getStringExtra("out_trade_no");
        tv_sucess= (TextView) findViewById(R.id.tv_sucess);
        tv_finish= (TextView) findViewById(R.id.tv_finish);
        iv_finish= (ImageView) findViewById(R.id.iv_head);
        tv_finish.setOnClickListener(this);
    }

    private void initData() {
        getVipResult();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_finish:
                finish();
                break;
            default:
                break;
        }
    }
    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }


    private void getVipResult() {
        showLoading();
//        String url= Constants.FIND_URL+"ali/status.php?trade_no="+trade_no+"&out_trade_no="+out_trade_no;
//        Log.e("------TRADE",url);
//        StringRequest stringRequest1 = new StringRequest(Request.Method.GET, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        Log.e("---------TRADE",response);
//                        dissloading();
//                        if(response.equals("TRADE_SUCCESS")){
//                            tv_sucess.setText("支付成功");
//                            MyApplication.getContext().getSharedPreferences("info", MyApplication.getContext().MODE_PRIVATE).edit().putBoolean("isVip",true).commit();
//                            iv_finish.setImageResource(R.drawable.buy_success);
//                            EventBus.getDefault().post(new BuyMemberSuccess(0), "pay");
//                        }else {
//                            tv_sucess.setText("支付失败");
//                            iv_finish.setImageResource(R.drawable.buy_fail);
//                        }
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                dissloading();
//                ToastUtils.showToast(MyApplication.getContext(), "加载数据失败");
//            }
//        });
//        stringRequest1.setRetryPolicy(new DefaultRetryPolicy(15000,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        Volley.newRequestQueue(this).add(stringRequest1);

        String url=Constants.PHP_URL+"v"+ AppUtils.getVerName()+"/getStatus";
        Log.e("请求支付宝TRADE", "url:" + url);
        StringRequest postsr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("---------TRADE",response);
                dissloading();
                ApilRootBean apilRootBean = new Gson().fromJson(response, ApilRootBean.class);
                if(apilRootBean.getCode()==1){
                    tv_sucess.setText("支付成功");
                    MyApplication.getContext().getSharedPreferences("info", MyApplication.getContext().MODE_PRIVATE).edit().putBoolean("isVip",true).commit();
                    iv_finish.setImageResource(R.drawable.new_login_rg);
                    EventBus.getDefault().post(new BuyMemberSuccess(0), "pay");
                }else {
                    tv_sucess.setText("支付失败");
                    iv_finish.setImageResource(R.drawable.buy_fail);
                }
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
//                map.put("trade_no", trade_no);
                map.put("out_trade_no", out_trade_no);
                return map;
            }
        };
        postsr.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.getRequestQueue().add(postsr);
    }
}
