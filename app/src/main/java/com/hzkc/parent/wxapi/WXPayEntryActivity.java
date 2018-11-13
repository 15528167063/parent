package com.hzkc.parent.wxapi;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.hzkc.parent.appcation.MyApplication;
import com.hzkc.parent.event.BuyMemberSuccess;
import com.hzkc.parent.mina.Constants;
import com.hzkc.parent.utils.ToastUtils;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.simple.eventbus.EventBus;


public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

    private static final String TAG = "MicroMsg.SDKSample.WXPayEntryActivity";
    private IWXAPI api;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        api = WXAPIFactory.createWXAPI(this, Constants.WEIXIN_APPID);
        api.handleIntent(getIntent(), this);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
    }

    @Override
    public void onResp(BaseResp resp) {
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            switch (resp.errCode){
                case 0:
//                    String trade_no = MyApplication.getContext().getSharedPreferences("info", MODE_PRIVATE).getString("out_trade_no", "");
//                    checkPayMember(trade_no, Constants.WEIXIN_APPID);
                    MyApplication.getContext().getSharedPreferences("info", MyApplication.getContext().MODE_PRIVATE).edit().putBoolean("isVip",true).commit();
                    Toast.makeText(MyApplication.getContext(), "支付成功", Toast.LENGTH_SHORT).show();
                    dataChanged(1);
                    finish();
                    break;
                case -2:
                    Toast.makeText(MyApplication.getContext(), "取消支付", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                default:
                    Toast.makeText(MyApplication.getContext(), "支付失败", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
            }

        }
    }

    /**
     * 查看会员是否购买成功
     */
    public void checkPayMember(String tradeNO, String appId) {
        String url= Constants.FIND_URL+"ali/Wxpay/notify_url.php?out_trade_no="+tradeNO;
        Log.e("----查看会员是否购买url",url);
        StringRequest stringRequest1 = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String result) {
                        Log.e("--查看会员是否购买result",result);
                        if (result.contains("SUCCESS")){
                            Toast.makeText(MyApplication.getContext(), "支付成功", Toast.LENGTH_SHORT).show();
                            MyApplication.getContext().getSharedPreferences("info", MyApplication.getContext().MODE_PRIVATE).edit().putString("out_trade_no","").commit();
                            dataChanged(1);
                            finish();
                        }else {
                            Toast.makeText(MyApplication.getContext(), "支付失败", Toast.LENGTH_SHORT).show();
                            finish();
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
    public void dataChanged(int type) {
        EventBus.getDefault().post(new BuyMemberSuccess(type), "pay");
    }
}