package com.hzkc.parent.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hzkc.parent.Bean.CommentBean;
import com.hzkc.parent.R;
import com.hzkc.parent.adapter.CommentListAdapter;
import com.hzkc.parent.appcation.MyApplication;
import com.hzkc.parent.mina.Constants;
import com.hzkc.parent.utils.LogUtil;
import com.hzkc.parent.utils.ToastUtils;
import com.hzkc.parent.view.XListView;
import com.jaeger.library.StatusBarUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.widget.Toast.makeText;

public class FindCommentListActivity extends BaseActivity implements View.OnClickListener, XListView.IXListViewListener {
    private TextView tvTopTitle;
    private ImageView ivFinish;
    private XListView lvCommentList;
    private LinearLayout linearComment;
    private LinearLayout ll_download;
    private TextView textComment;
    private String msgId;
    private String dztj;
    private ArrayList<CommentBean> commentDatas;
    private CommentListAdapter adapter;
    private int idCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_comment_list);
        StatusBarUtil.setColorNoTranslucent(this,getResources().getColor(R.color.blue));
        initIView();
        initData();
    }

    private void initIView() {
        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        ivFinish = (ImageView) findViewById(R.id.iv_finish);
        lvCommentList = (XListView) findViewById(R.id.lv_comment_list);
        linearComment = (LinearLayout) findViewById(R.id.linear_comment);
        ll_download = (LinearLayout) findViewById(R.id.ll_download);
        textComment = (TextView) findViewById(R.id.text_comment);

        ivFinish.setVisibility(View.VISIBLE);
        ll_download.setVisibility(View.VISIBLE);

        tvTopTitle.setText("评论详情");
        ivFinish.setOnClickListener(this);
        linearComment.setOnClickListener(this);
    }

    private void initData() {
        msgId = getIntent().getStringExtra("msgId");
        dztj = getIntent().getStringExtra("dztj");
        commentDatas = new ArrayList<>();
        lvCommentList.setPullRefreshEnable(true);
        lvCommentList.setPullLoadEnable(true);
        lvCommentList.setAutoLoadEnable(true);
        lvCommentList.setXListViewListener(this);
        lvCommentList.setRefreshTime(getTime());

        adapter = new CommentListAdapter(commentDatas, this);
        lvCommentList.setAdapter(adapter);
        updateDatas();
    }
    /**
     * 第一次联网更新数据
     * */
    private void updateDatas() {
        String url = Constants.FIND_URL_COMMENTLIST+"id="+msgId+"&yid="+0;
        //请求朋友圈列表
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ll_download.setVisibility(View.INVISIBLE);
                        LogUtil.e("加载评论数据", "updateDatas:" + response);
                        List<CommentBean> mList =
                                new Gson().fromJson(response, new TypeToken<List<CommentBean>>() {
                                }.getType());
                        LogUtil.e("加载评论数据", "mList:" + mList.size());
                        LogUtil.e("加载评论数据", "mDatas:" + mList.size());
                        commentDatas.clear();
                        commentDatas.addAll(mList);
                        LogUtil.e("加载评论数据", "mDatas:" + mList.size());
                        adapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ll_download.setVisibility(View.INVISIBLE);
                ToastUtils.showToast(MyApplication.getContext(), "加载评论数据失败");
            }
        });
        Volley.newRequestQueue(this).add(stringRequest);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_finish:
                finish();
                break;
            case R.id.linear_comment:
                comment();
                break;
            default:
                break;
        }
    }

    /**
     * 评论
     * */
    private void comment() {
        Intent intent = new Intent(this, FindCommentActivity.class);
        intent.putExtra("msgId",msgId);
        startActivity(intent);
    }
    /**
     * 下拉刷新
     * */
    @Override
    public void onRefresh() {
        idCount = 0;
        String url = Constants.FIND_URL_COMMENTLIST+"id="+msgId+"&yid="+idCount;
        //请求朋友圈列表
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        LogUtil.e("下拉刷新", "updateDatas:" + response);
                        List<CommentBean> mList =
                                new Gson().fromJson(response, new TypeToken<List<CommentBean>>() {
                                }.getType());
                        LogUtil.e("下拉刷新", "mList:" + mList.size());
                        LogUtil.e("下拉刷新", "mDatas:" + mList.size());
                        commentDatas.clear();
                        commentDatas.addAll(mList);
                        LogUtil.e("下拉刷新", "mDatas:" + mList.size());
                        adapter.notifyDataSetChanged();
                        onLoad();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ToastUtils.showToast(MyApplication.getContext(), "下拉刷新数据失败");
                onLoad();
            }
        });
        Volley.newRequestQueue(this).add(stringRequest);
    }
    /**
     * 上拉加载更多
     * */
    @Override
    public void onLoadMore() {
        idCount = idCount+10;
        String url = Constants.FIND_URL_COMMENTLIST+"id="+msgId+"&yid="+idCount;
        //请求朋友圈列表
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        LogUtil.e("上拉加载更多", "updateDatas:" + response);
                        List<CommentBean> mList =
                                new Gson().fromJson(response, new TypeToken<List<CommentBean>>() {
                                }.getType());
                        LogUtil.e("上拉加载更多", "mList:" + mList.size());
                        LogUtil.e("上拉加载更多", "mDatas:" + mList.size());
                        //commentDatas.clear();
                        if (commentDatas.addAll(mList)) {
                            LogUtil.e("初始加载数据", "Normal:" + mList.size());
                            onLoad();
                            adapter.notifyDataSetChanged();
                        } else {//加载失败
                            LogUtil.e("初始加载数据", "TheEnd:" + mList.size());
                            lvCommentList.stopRefresh();
                            lvCommentList.noLoadMore();
                            lvCommentList.setRefreshTime(getTime());
                        }
                        onLoad();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ToastUtils.showToast(MyApplication.getContext(), "上拉加载更多数据失败");
                onLoad();
            }
        });
        Volley.newRequestQueue(this).add(stringRequest);
    }

    private void onLoad() {
        lvCommentList.stopRefresh();
        lvCommentList.stopLoadMore();
        lvCommentList.setRefreshTime(getTime());
    }

    private String getTime() {
        return new SimpleDateFormat("MM-dd HH:mm", Locale.CHINA).format(new Date());
    }

    @Override
    public void onResume() {
        super.onResume();
        updateDatas();
    }
}
