package com.hzkc.parent.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.hzkc.parent.Bean.MessageResult;
import com.hzkc.parent.Bean.MessageRoot;
import com.hzkc.parent.R;
import com.hzkc.parent.adapter.MyMessageAdapter;
import com.hzkc.parent.appcation.MyApplication;
import com.hzkc.parent.mina.Constants;
import com.hzkc.parent.utils.AppUtils;
import com.jaeger.library.StatusBarUtil;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageManagerActivity extends BaseActivity implements View.OnClickListener, XRecyclerView.LoadingListener {
    private TextView tvTopTitle;
    private ImageView ivFinish;
    private LinearLayout fl_kongbai;
    private XRecyclerView mRecyclerView;
    private List<MessageResult> list;
    private MyMessageAdapter adapter;
    private String parentUUID;
    private int  page=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_manager);
        StatusBarUtil.setColorNoTranslucent(this,getResources().getColor(R.color.blue));
        initView();
        initData();
    }
    private void initData() {
        httpgetDatas(page, true);
    }
    @Override
    public void onResume() {
        super.onResume();
    }

    private void initView() {
        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        ivFinish = (ImageView) findViewById(R.id.iv_finish);
        mRecyclerView = (XRecyclerView) findViewById(R.id.lv_message_list);
        fl_kongbai = (LinearLayout) findViewById(R.id.fl_kongbai);
        parentUUID = sp.getString("parentUUID", "");
        ivFinish.setVisibility(View.VISIBLE);
        ivFinish.setOnClickListener(this);
        tvTopTitle.setText("我的消息");
        list=new ArrayList<>();
        mRecyclerView.setLoadingListener(this);
        mRecyclerView.setLoadingMoreEnabled(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_finish:
                finish();
                break;
            default:
                break;
        }
    }
    public Handler handler=new Handler();
    @Override
    public void onRefresh() {
        page=1;
        httpgetDatas(page,true);
    }

    @Override
    public void onLoadMore() {
        page++;
        httpgetDatas(page,false);
    }
    public List<MessageResult> data=new ArrayList<>();
    private void httpgetDatas(final int Index, final boolean isrefresh) {
        String url= Constants.PHP_URL+"v"+ AppUtils.getVerName()+"/getNewsList";
        Log.e("信息列表", "url:" + url);
        StringRequest postsr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("-------------response",response);
                dissloading();
                MessageRoot messageRoot =null;
                try{
                    messageRoot = new Gson().fromJson(response, MessageRoot.class);
                }catch (Exception e){
                    if(isrefresh){
                        fl_kongbai.setVisibility(View.VISIBLE);
                        mRecyclerView.setVisibility(View.GONE);
                    }else {
                        mRecyclerView.loadMoreComplete();
                    }
                    return;
                }
                if(isrefresh){
                    data.clear();
                    mRecyclerView.refreshComplete();
                    data=messageRoot.getData();
                    DealMessage(messageRoot.getData());
                }else {
                    mRecyclerView.loadMoreComplete();
                    data.addAll(messageRoot.getData());
                    adapter.notifyDataSetChanged();
                    return;
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                dissloading();
                if(isrefresh){
                    mRecyclerView.refreshComplete();
                }else {
                    mRecyclerView.loadMoreComplete();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //创建一个集合，放的是keyvalue的key是参数名与value是参数值
                Map<String, String> map = new HashMap<String, String>();
                map.put("uid",parentUUID);
                map.put("page",Index+"");
                Log.e("-------------map", "map:" + map.toString());
                return map;
            }
        };
        postsr.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.getRequestQueue().add(postsr);
    }

    private void DealMessage(final List<MessageResult> data) {
        if(data==null ||data.size()==0){
            fl_kongbai.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
            return;
        }
        fl_kongbai.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
        adapter=new MyMessageAdapter(data,this);
        adapter.setOnItemClickListener(new MyMessageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if(!TextUtils.isEmpty(data.get(position).getTo_url())){
                    Intent intent = new Intent(MessageManagerActivity.this, FunctionActivity.class);
                    intent.putExtra("urlPath", data.get(position).getTo_url());
                    startActivity(intent);
                }
            }
        });
        mRecyclerView.setAdapter(adapter);;
    }
}
