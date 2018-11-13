package com.hzkc.parent.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.hzkc.parent.Bean.OpinionBackBean;
import com.hzkc.parent.Bean.OpinionBackRootBean;
import com.hzkc.parent.R;
import com.hzkc.parent.adapter.MyOpinionListviewAdapter;
import com.hzkc.parent.appcation.MyApplication;
import com.hzkc.parent.mina.Constants;
import com.hzkc.parent.utils.AppUtils;
import com.hzkc.parent.utils.LogUtil;
import com.hzkc.parent.utils.NetworkUtil;
import com.hzkc.parent.utils.ToastUtils;
import com.jaeger.library.StatusBarUtil;

import org.simple.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OpinionBackActivity extends BaseActivity implements View.OnClickListener {
    private TextView tvTopTitle;
    private ImageView ivFinish;
    private TextView tvSubmit,tv_submit;
    private EditText etOpinionContent,et_contact;
    // private ListView lvOpinion;
    private List<OpinionBackBean> opinionList;
    private MyOpinionListviewAdapter adapter;
    private String user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opinion_back);
        StatusBarUtil.setColorNoTranslucent(this,getResources().getColor(R.color.newcolor));
        initView();
    }

    private void initView() {
        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        ivFinish = (ImageView) findViewById(R.id.iv_finish_back);
        tvSubmit = (TextView) findViewById(R.id.tv_useropinion_submit);
        tv_submit = (TextView) findViewById(R.id.tv_submit);
        etOpinionContent = (EditText) findViewById(R.id.et_opinion_content);
        et_contact = (EditText) findViewById(R.id.et_contact);
        //lvOpinion = (ListView) findViewById(R.id.lv_opinion);
        tvTopTitle.setText("用户反馈");
        ivFinish.setVisibility(View.VISIBLE);
        tv_submit.setVisibility(View.VISIBLE);
        tv_submit.setOnClickListener(this);
        ivFinish.setOnClickListener(this);
        tvSubmit.setOnClickListener(this);
        user = sp.getString("phoneNum", "");
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_finish_back:
                finish();
                break;
            case R.id.tv_submit:
                userSubmit();
                break;
            case R.id.tv_useropinion_submit:
                //submitOpinion();//带listview的提交
                if(isQQClientAvailable(this)) {
                    String url = "mqqapi://card/show_pslcard?src_type=internal&version=1&uin=" + 433717321
                            + "&card_type=group&source=qrcode";
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                }else{
                    ToastUtils.showToast(MyApplication.getContext(), "您还没有安装QQ，请先安装QQ客户端");
                }
                break;
            default:
                break;
        }
    }
    //http://IP地址:端口/YczApi/public/index.php/FeedBack/content/contact/src.asp
    private void userSubmit() {
        final String content = etOpinionContent.getText().toString().trim();
        final String contact = et_contact.getText().toString().trim();
        if(TextUtils.isEmpty(content)){
            Toast.makeText(this,"输入为空，请重新输入",Toast.LENGTH_SHORT).show();
            return;
        }
        showLoading();
        final String userid= sp.getString("parentUUID", "");

        String url=Constants.PHP_URL+"v"+ AppUtils.getVerName()+"/feedBack";
        Log.e("意见反馈接口", "url:" + url);
        StringRequest postsr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("意见反馈完成接口", "updateDatas:" + response);
                dissloading();
                OpinionBackRootBean opinionBackRootBean =null;
                try {
                    opinionBackRootBean=  new Gson().fromJson(response, OpinionBackRootBean.class);
                }catch ( Exception e){
                    Toast.makeText(OpinionBackActivity.this,"提交成功，请耐心等候工作人员的处理",Toast.LENGTH_SHORT).show();
                    etOpinionContent.setText("");
                    et_contact.setText("");
                    return;
                }
                if(opinionBackRootBean.getCode()==1){
                    Toast.makeText(OpinionBackActivity.this,"提交成功，请耐心等候工作人员的处理",Toast.LENGTH_SHORT).show();
                    etOpinionContent.setText("");
                    et_contact.setText("");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                dissloading();
                ToastUtils.showToast(MyApplication.getContext(), "提交数据失败");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //创建一个集合，放的是keyvalue的key是参数名与value是参数值
                Map<String, String> map = new HashMap<String, String>();
                map.put("user_id", userid);
                map.put("back_src", "parent");
                map.put("contact", contact);
                map.put("content", content);
                return map;
            }
        };
        postsr.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.getRequestQueue().add(postsr);
    }

    /**
     * 带listview的提交
     * */
    private void submitOpinion() {
        String content = etOpinionContent.getText().toString().trim();
        String useropinion = sp.getString("useropinion", "");
        boolean isNet = NetworkUtil.isConnected();
        if (!isNet) {
            ToastUtils.showToast(OpinionBackActivity.this,"网络不通，请检查网络再试");
            return;
        }
        if(!TextUtils.isEmpty(content)){
            Date d = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time = sdf.format(d);
            LogUtil.i(TAG, "submitOpinion: "+d);
            String s=user+"#2###2#"+time+"#2###2#"+content+"#2###2#"+"#1###1#";
            sp.edit().putString("useropinion",useropinion+s).commit();
            OpinionBackBean bean = new OpinionBackBean();
            bean.setUser(user);
            bean.setTime(time);
            bean.setContent(content);
            etOpinionContent.setText("");
            opinionList.add(bean);
            adapter.notifyDataSetChanged();
        }else{
            Toast.makeText(this,"输入为空，请重新输入",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
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
