package com.hzkc.parent.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.hzkc.parent.Bean.AlipayResult;
import com.hzkc.parent.Bean.ApilRootBean;
import com.hzkc.parent.Bean.PayResult;
import com.hzkc.parent.Bean.WechatResultBean;
import com.hzkc.parent.Bean.WechatRootBean;
import com.hzkc.parent.activity.ParResultActivity;
import com.hzkc.parent.appcation.MyApplication;
import com.hzkc.parent.event.BuyMemberSuccess;
import com.hzkc.parent.mina.Constants;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.simple.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

/**
 * （新版版php）
 */

public class PayUtils {
    /**
     * 微信支付（获取订单）
     */
    public static  void payWechat(final String  id, final String userid, final String paytype) {
        String url=Constants.PHP_URL+"v"+ AppUtils.getVerName()+"/getAppPay";
        Log.e("请求微信支付订单链接", "url:" + url);
        StringRequest postsr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.toString().equals("0")){
                    dataChanged();
                }else {
                    payWeiXin(response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                ToastUtils.showToast(MyApplication.getContext(), "加载数据失败");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //创建一个集合，放的是keyvalue的key是参数名与value是参数值
                Map<String, String> map = new HashMap<String, String>();
                map.put("goods_id", id);
                map.put("user_id", userid);
                map.put("pay_type", "wechat");
                Log.e("请求微信支付订单参数", "map:" + map.toString());
                return map;
            }
        };
        postsr.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.getRequestQueue().add(postsr);
    }


    /**
     * 微信支付（启动支付界面）
     */
    public static void payWeiXin(String result) {
        Log.e("-----------微信支付订单返回数据",result);
        WechatRootBean wechatPayBean=new Gson().fromJson(result,WechatRootBean.class);
        WechatResultBean resultBean = wechatPayBean.getResult();
        sendReqToWeiXin(resultBean);
    }

    public static void sendReqToWeiXin(WechatResultBean chargeBean) {
        IWXAPI api = WXAPIFactory.createWXAPI(MyApplication.getContext(),Constants.WEIXIN_APPID);
        PayReq request = new PayReq();
        String out_trade_no = chargeBean.getOut_trade_no();
        MyApplication.getContext().getSharedPreferences("info", MyApplication.getContext().MODE_PRIVATE).edit().putString("out_trade_no",out_trade_no).commit();
        request.appId = chargeBean.getAppid();
        request.partnerId = chargeBean.getPartnerid();
        request.prepayId = chargeBean.getPrepayid();
        request.packageValue = "Sign=WXPay";
        request.nonceStr = chargeBean.getNoncestr();
        request.timeStamp = chargeBean.getTimestamp();
        request.sign = chargeBean.getSign();
        api.sendReq(request);
    }


    /**
     * 支付宝支付（获取订单）62149216 0303 6001
     */
    public static  void payali(final String  id, final String userid,final Activity activity) {
        String url=Constants.PHP_URL+"v"+ AppUtils.getVerName()+"/getAppPay";
        Log.e("请求支付宝支付订单链接", "url:" + url);
        StringRequest postsr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {
                Log.e("-------------result",result);
                if(result.toString().equals("0")){
                    dataChanged();
                }else {
                    ApilRootBean payBean=new Gson().fromJson(result,ApilRootBean.class);
                    payAlipay(payBean.getResult(),activity);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                ToastUtils.showToast(MyApplication.getContext(), "加载数据失败");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //创建一个集合，放的是keyvalue的key是参数名与value是参数值
                Map<String, String> map = new HashMap<String, String>();
                map.put("goods_id", id);
                map.put("user_id", userid);
                map.put("pay_type", "alipay");
                Log.e("请求微信支付订单参数", "map:" + map.toString());
                return map;
            }
        };
        postsr.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.getRequestQueue().add(postsr);
    }


    /**
     * 支付宝支付（启动支付界面）
     */
    private static final int SDK_PAY_FLAG = 1;
    public static void payAlipay(final String orderInfo, final Activity activity) {
        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(activity);
                Map<String, String> result = alipay.payV2(orderInfo, true);
                Log.e("-----msp", result.toString());

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };
        // 必须异步调用
        Thread authThread = new Thread(payRunnable);
        authThread.start();
    }

    private static Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    Log.e("-----------SDK_PAY_FLAG",SDK_PAY_FLAG+"");
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    Log.e("-----------resultInfo",resultInfo+""+resultStatus);
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。

                        AlipayResult alipayResult = new Gson().fromJson(resultInfo, AlipayResult.class);
                        Intent intent=new Intent(MyApplication.getContext(), ParResultActivity.class);
                        intent.putExtra("trade_no",alipayResult.alipay_trade_app_pay_response.trade_no);
                        intent.putExtra("out_trade_no",alipayResult.alipay_trade_app_pay_response.out_trade_no);
                        try {
                            MyApplication.getContext().startActivity(intent);
                        }catch (Exception e){
                            e.printStackTrace();
                            getVipResult(alipayResult.alipay_trade_app_pay_response.trade_no,alipayResult.alipay_trade_app_pay_response.out_trade_no);
                        }

                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        Toast.makeText(MyApplication.getContext(), "支付失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
            }
        };
    };

    public static  void getVipResult(String trade_no,String out_trade_no) {
        String url= Constants.FIND_URL+"ali/status.php?trade_no="+trade_no+"&out_trade_no="+out_trade_no;
        Log.e("------TRADE",url);
        StringRequest stringRequest1 = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("---------TRADE",response);
                        if(response.equals("TRADE_SUCCESS")){
                            Toast.makeText(MyApplication.getContext(), "支付成功", Toast.LENGTH_SHORT).show();
                            MyApplication.getContext().getSharedPreferences("info", MyApplication.getContext().MODE_PRIVATE).edit().putBoolean("isVip",true).commit();
                            EventBus.getDefault().post(new BuyMemberSuccess(0), "pay");
                        }else {
                            Toast.makeText(MyApplication.getContext(), "支付失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ToastUtils.showToast(MyApplication.getContext(), "加载数据失败");
            }
        });
        stringRequest1.setRetryPolicy(new DefaultRetryPolicy(15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(MyApplication.getContext()).add(stringRequest1);
    }

    /**
     * 注册一个eventbus，通知数据更新
     */
    public static  void dataChanged() {
        EventBus.getDefault().post(new outDataSuccess(), "outdata");
    }

    public static class outDataSuccess {
        public outDataSuccess() {
        }
    }





}
