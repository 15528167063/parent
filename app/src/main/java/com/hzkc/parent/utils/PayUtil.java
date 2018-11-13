package com.hzkc.parent.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.hzkc.parent.Bean.AlipayResult;
import com.hzkc.parent.Bean.PayResult;
import com.hzkc.parent.Bean.WechatBean;
import com.hzkc.parent.activity.ParResultActivity;
import com.hzkc.parent.appcation.MyApplication;
import com.hzkc.parent.event.BuyMemberSuccess;
import com.hzkc.parent.mina.Constants;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.simple.eventbus.EventBus;

import java.util.Map;

/**
 * Created by Administrator on 2017/12/11.
 */
// http://www.ycz365.com/ali/Wxpay/order.php?id=001&userid=19f2de7529eb4861839e38981be31b75
//http://www.ycz365.com/ali/Wxpay/order.php?id=1&userid=ac5cae876644447182fe5653519bfd49
public class PayUtil {
    /**
     * 微信支付（获取订单）
     */
    public static  void payWechat(String  id, String userid, final String paytype) {
        String url= Constants.FIND_URL+"ali/Wxpay/order.php?id="+id+"&userid="+userid;
        Log.e("-----请求微信支付订单链接：",url);
        StringRequest stringRequest1 = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String result) {
                        if(result.toString().equals("0")){
                            dataChanged();
                        }else {
                            payWeiXin(result);
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
     * 微信支付（启动支付界面）
     */
    public static void payWeiXin(String result) {
        Log.e("-----------微信支付订单返回数据",result);
        WechatBean wechatPayBean=new Gson().fromJson(result,WechatBean.class);
        sendReqToWeiXin(wechatPayBean);
    }

    public static void sendReqToWeiXin(WechatBean chargeBean) {
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
    public static void payali(String  id, String userid, final Activity activity) {
        String url= Constants.FIND_URL+"ali/ali/order.php?id="+id+"&userid="+userid;
        Log.e("-------------url",url);
        StringRequest stringRequest1 = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String result) {
                        Log.e("-------------result",result);
                        if(result.toString().equals("0")){
                            dataChanged();
                        }else {
                            payAlipay(result.trim(),activity);
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
//        String result="app_id=2017051707261635&biz_content=%7B%22body%22%3A%22%5Cu5546%5Cu54c1%5Cu8be6%5Cu60c5%22%2C%22out_trade_no%22%3A%22001151306165970501%22%2C%22product_code%22%3A%22QUICK_MSECURITY_PAY%22%2C%22subject%22%3A%22%5Cu8d2d%5Cu4e70%5Cu4f1a%5Cu5458%22%2C%22timeout_express%22%3A%2220m%22%2C%22total_amount%22%3A%220.01%22%7D&charset=utf-8&format=json&method=alipay.trade.app.pay&notify_url=http%3A%2F%2Fwww.ycz365.com%2Fali%2Falipay%2Forder.php&sign_type=RSA&timestamp=2017-12-12+14%3A54%3A19&version=1.0&sign=HdZHb2TqQtNMOHK1O1OVq8Y0GVVqOzdSge9fzhnR14UOd2bq9%2BMklDk67WnED84VU7VoYLKbe829SW%2BbJhO%2FqfLkqOFcjTqAOmwb1I7eK7XxMGkJtLU1Phwc8QXJgeL%2BFMLV6yfGLaZX54JxcTqhUrR4OX7sS0LKvI5pXrTYWoY%3D\n";
//        payAlipay(result,activity);
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
