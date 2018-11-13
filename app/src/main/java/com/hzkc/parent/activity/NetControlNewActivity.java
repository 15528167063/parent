package com.hzkc.parent.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
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
import com.hzkc.parent.Bean.NetInfoBean;
import com.hzkc.parent.Bean.NetRoot;
import com.hzkc.parent.R;
import com.hzkc.parent.adapter.MyNetAdapter;
import com.hzkc.parent.appcation.MyApplication;
import com.hzkc.parent.event.WWWControlBackEvent;
import com.hzkc.parent.mina.Constants;
import com.hzkc.parent.utils.AppUtils;
import com.hzkc.parent.utils.LogUtil;
import com.hzkc.parent.utils.NetworkUtil;
import com.hzkc.parent.utils.ToastUtils;
import com.hzkc.parent.view.SubjectDialog;
import com.jaeger.library.StatusBarUtil;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NetControlNewActivity extends BaseActivity implements View.OnClickListener, XRecyclerView.LoadingListener, SubjectDialog.subjectClickListener {
    private TextView tvSelectAll;
    private TextView tvRemoveApp;
    private TextView tvTopTitle,tv_no_content;
    private ImageView ivFinish;
    private ImageView ivAddPlan,iv_choose;
    private LinearLayout flKong;
    private XRecyclerView lvNetList;
    private String childUUID,childFrom;
    private MyNetAdapter adapter;
    private List<NetInfoBean> list;
    boolean allFlag = false;
    private ProgressDialog pd;
    private boolean stopFlag;
    public int page=1;
    private String[] course = {"按时间查询", "按次数查询"};
    private SubjectDialog subjectDialog;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 106:
                    if (!stopFlag) {
                        ToastUtils.showToast(MyApplication.getContext(),"网络连接不通畅,请稍后再试");
                    }
                    if (pd!=null&&pd.isShowing()){
                        pd.dismiss();
                    }
                    break;
                case 107:
                    if(pd!=null&&pd.isShowing()){
                        pd.dismiss();
                    }
                    break;
                default:
                    break;
            }
        }
    };
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net_control_new);
        StatusBarUtil.setColorNoTranslucent(this,getResources().getColor(R.color.newcolor));
        EventBus.getDefault().register(this);
        stopFlag=false;
        initView();
        initData();
        runnable = new Runnable() {
            @Override
            public void run() {
                Message msg = Message.obtain();
                msg.what = 106;
                handler.sendMessage(msg);
            }
        };
    }

    private void initView() {
        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        tvRemoveApp = (TextView) findViewById(R.id.tv_remove_app);
        tvSelectAll = (TextView) findViewById(R.id.tv_select_all);
        ivFinish = (ImageView) findViewById(R.id.iv_finish_back);
        iv_choose = (ImageView) findViewById(R.id.iv_choose);
        ivAddPlan = (ImageView) findViewById(R.id.iv_bj);
        flKong = (LinearLayout) findViewById(R.id.fl_kongbai);
        lvNetList = (XRecyclerView) findViewById(R.id.lv_net_list);
        tv_no_content= (TextView) findViewById(R.id.tv_no_content);
        subjectDialog = new SubjectDialog(this);
        subjectDialog.setClickListener(this);
        childUUID = getIntent().getStringExtra("ChildUUID");
        childFrom = "ycz";
        String netData = sp.getString(childUUID + "netList", "1");
        if(netData.equals("1")){
            sp.edit().putString(childUUID + "netList", "").commit();
        }

        if(childFrom.equals("ycz")){
            tvTopTitle.setText("网址浏览记录");
        }
        ivFinish.setVisibility(View.VISIBLE);
        ivAddPlan.setVisibility(View.VISIBLE);
        iv_choose.setVisibility(View.VISIBLE);
        ivFinish.setOnClickListener(this);
        ivAddPlan.setOnClickListener(this);
        tvRemoveApp.setOnClickListener(this);
        tvSelectAll.setOnClickListener(this);
        iv_choose.setOnClickListener(this);
        list = new ArrayList<>();

        lvNetList.setLoadingListener(this);
        lvNetList.setLoadingMoreEnabled(true);
        lvNetList.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initData() {
        httpgetDatas(page, true,type);
    }

    @Override
    public void onClick(View v) {
        boolean isNet = NetworkUtil.isConnected();
        switch (v.getId()) {
            case R.id.iv_finish_back:
                finish();
                break;
            case R.id.iv_bj:
                Intent intent = new Intent(NetControlNewActivity.this, NetControlActivity.class);
                intent.putExtra("ChildUUID", childUUID);
                startActivityForResult(intent, 0);
                break;
            case R.id.iv_choose:
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
        if(pd!=null&&pd.isShowing()){
            pd.dismiss();
        }
        stopFlag=true;
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
    /**
     * 请求孩子的管控计划
     * */
    @Subscriber(mode = ThreadMode.ASYNC, tag = "wwwcontrolback")
    private void OrderChildStopPlan (WWWControlBackEvent requestData) {
        LogUtil.i(TAG, "OrderChildStopPlan: 请求孩子的管控计划");
        stopFlag=true;
        handler.removeCallbacks(runnable);
        Message msg = Message.obtain();
        msg.what = 107;
        handler.sendMessage(msg);
    }

    public List<NetInfoBean> data=new ArrayList<>();
    private void httpgetDatas(final int Index, final boolean isrefresh, final String type) {
        String url= Constants.PHP_URL+"v"+ AppUtils.getVerName()+"/getBrowseRecord";
        Log.e("信息列表", "url:" + url);
        StringRequest postsr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("-------------response",response);
                dissloading();
                NetRoot messageRoot =null;
                try {
                     messageRoot = new Gson().fromJson(response, NetRoot.class);
                }catch (Exception e){
                    if(isrefresh){
                        flKong.setVisibility(View.VISIBLE);
                        lvNetList.setVisibility(View.GONE);
                        return;
                    }else {
                        lvNetList.loadMoreComplete();
                        return;
                    }

                }
                if(isrefresh){
                    data.clear();
                    lvNetList.refreshComplete();
                    data=messageRoot.getData();
                    DealMessage(messageRoot.getData());
                }else {
                    lvNetList.loadMoreComplete();
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
                    lvNetList.refreshComplete();
                }else {
                    lvNetList.loadMoreComplete();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //创建一个集合，放的是keyvalue的key是参数名与value是参数值
                Map<String, String> map = new HashMap<String, String>();
                map.put("child_id",childUUID);
                map.put("page",Index+"");
                map.put("sort",type);
                map.put("pagesize",10+"");
                return map;
            }
        };
        postsr.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.getRequestQueue().add(postsr);
    }

    private void DealMessage( List<NetInfoBean> lists) {
        if (lists==null ||lists.size() < 1) {//网址数据为0
            flKong.setVisibility(View.VISIBLE);
            lvNetList.setVisibility(View.GONE);
            return;
        }
        flKong.setVisibility(View.GONE);
        lvNetList.setVisibility(View.VISIBLE);
        adapter = new MyNetAdapter(lists, NetControlNewActivity.this);
        lvNetList.setAdapter(adapter);
    }

    @Override
    public void onRefresh() {
        page=1;
        httpgetDatas(page,true,type);
    }

    @Override
    public void onLoadMore() {
        page++;
        httpgetDatas(page,false,type);
    }

    public  String type="time";//按时间  按次数
    @Override
    public void subjectClick(String subject, int index) {
        if(subject.equals("按时间查询")){
            type="time";
            httpgetDatas(page,true,type);
        }else {
            type="num";
            httpgetDatas(page,true,type);
        }
    }
}
