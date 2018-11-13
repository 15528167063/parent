package com.hzkc.parent.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.hzkc.parent.Bean.VipHistoryBean;
import com.hzkc.parent.Bean.VipHistoryNewData;
import com.hzkc.parent.R;
import com.hzkc.parent.adapter.MyVipHistoryAdapter;
import com.hzkc.parent.appcation.MyApplication;
import com.hzkc.parent.mina.Constants;
import com.hzkc.parent.utils.AppUtils;
import com.hzkc.parent.utils.ToastUtils;
import com.hzkc.parent.view.SubjectDialog;
import com.jaeger.library.StatusBarUtil;

import org.simple.eventbus.EventBus;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderHistoryActivity extends BaseActivity implements View.OnClickListener, SubjectDialog.subjectClickListener {
    private TextView tvTopTitle;
    private ImageView iv_finish;
    private TextView tvNull;
    private TextView tv_time;
    private ListView lvOrderHistory;
    private MyVipHistoryAdapter adapter;
    private SubjectDialog subjectDialog;
    private String[] course = {"2017", "2018", "2019", "2020", "2021", "2022", "2023", "2024"};
    public String year;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);
        StatusBarUtil.setColorNoTranslucent(this,getResources().getColor(R.color.blue));
        initView();
        initData();
    }

    private void initView() {
        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        iv_finish = (ImageView) findViewById(R.id.iv_finish);
        tv_time = (TextView) findViewById(R.id.tv_zhangdan1);
        tvNull= (TextView) findViewById(R.id.tv_null);
        lvOrderHistory = (ListView) findViewById(R.id.lv_order_history);
        Calendar a= Calendar.getInstance();
        tv_time.setVisibility(View.VISIBLE);
        tv_time.setText(a.get(Calendar.YEAR)+"");
        year=a.get(Calendar.YEAR)+"";
        tvTopTitle.setText("充值");
        iv_finish.setVisibility(View.VISIBLE);
        iv_finish.setOnClickListener(this);
        tv_time.setOnClickListener(this);
        subjectDialog = new SubjectDialog(this);
        subjectDialog.setClickListener(this);
    }

    private void initData() {
        getVipHistory();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_finish:
                finish();
                break;
            case R.id.tv_zhangdan1:
                //选择科目
                if (subjectDialog != null) {
                    subjectDialog.setPickerData(course);
                    subjectDialog.show();
                }
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

    @Override
    public void subjectClick(String subject, int index) {
        year=subject;
        tv_time.setText(subject);
        getVipHistory();
    }


    private void getVipHistory() {
        showLoading();
        final String userid= sp.getString("parentUUID", "");
        String url=Constants.PHP_URL+"v"+ AppUtils.getVerName()+"/rechargeBill";;
        Log.e("vip充值账单", "url:" + url);
        StringRequest postsr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("---------getVipHistory",response);
                dissloading();
                VipHistoryBean vipHistoryBean =null;
                try {
                    vipHistoryBean = new Gson().fromJson(response, VipHistoryBean.class);
                }catch (Exception e){
                    tvNull.setVisibility(View.VISIBLE);
                    return;
                }
                List<VipHistoryNewData> data = vipHistoryBean.getData();
                adapter=new MyVipHistoryAdapter(data,OrderHistoryActivity.this,OrderHistoryActivity.this);
                lvOrderHistory.setAdapter(adapter);
                if(data.size()==0){
                    tvNull.setVisibility(View.VISIBLE);
                }else {
                    tvNull.setVisibility(View.GONE);
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
                map.put("user_id", userid);
                map.put("year", year);
                return map;
            }
        };
        postsr.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.getRequestQueue().add(postsr);

    }
}
