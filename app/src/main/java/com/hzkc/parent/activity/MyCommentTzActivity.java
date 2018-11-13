package com.hzkc.parent.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hzkc.parent.Bean.CommentBean;
import com.hzkc.parent.R;
import com.hzkc.parent.adapter.CommentListAdapter;
import com.hzkc.parent.appcation.MyApplication;
import com.hzkc.parent.mina.Constants;
import com.hzkc.parent.utils.LogUtil;
import com.hzkc.parent.utils.ToastUtils;
import com.hzkc.parent.view.MultiImageView;
import com.hzkc.parent.view.XListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.widget.Toast.makeText;

public class MyCommentTzActivity extends BaseActivity implements View.OnClickListener, XListView.IXListViewListener {
    private TextView tvTopTitle;
    private ImageView ivFinish;
    private XListView lvCommentList;
    private LinearLayout linearComment;
    private LinearLayout linearLoad;
    private TextView textComment;
    private String myhf;
    private ArrayList<CommentBean> commentDatas;
    private CommentListAdapter adapter;

    private SimpleDraweeView profileImg;
    private TextView profileName;
    private TextView profileTime;
    private TextView weiboContent;
    private MultiImageView multiImagView;
    private CardView cardView;
    private View view;
    private String txid;
    private String nc;
    private int idCount;
    private String phoneNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_comment_tz);
        initIView();
        initData();
    }

    private void initIView() {
        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        ivFinish = (ImageView) findViewById(R.id.iv_finish);
        lvCommentList = (XListView) findViewById(R.id.lv_comment_list);
        linearComment = (LinearLayout) findViewById(R.id.linear_comment);
        linearLoad = (LinearLayout) findViewById(R.id.ll_download);
        textComment = (TextView) findViewById(R.id.text_comment);
        nc = sp.getString("nc", "");
        phoneNum = sp.getString("phoneNum", "");
        txid = nc.substring(nc.length() - 1);

        sp.edit().putString("findnewmsg","").commit();

        ivFinish.setVisibility(View.VISIBLE);
        linearLoad.setVisibility(View.VISIBLE);

        tvTopTitle.setText("评论详情");
        textComment.setText("回复");
        ivFinish.setOnClickListener(this);
        linearComment.setOnClickListener(this);
    }

    private void initData() {
        myhf = getIntent().getStringExtra("myhf");
        commentDatas = new ArrayList<>();

        lvCommentList.setPullRefreshEnable(true);
        lvCommentList.setPullLoadEnable(true);
        lvCommentList.setAutoLoadEnable(true);
        lvCommentList.setXListViewListener(this);
        lvCommentList.setRefreshTime(getTime());

        adapter = new CommentListAdapter(commentDatas, this);
        view = View.inflate(MyCommentTzActivity.this, R.layout.list_item_my_status,null);
        profileImg = (SimpleDraweeView) view.findViewById(R.id.profile_img);
        cardView = (CardView) view.findViewById(R.id.cardView);
        profileName = (TextView) view.findViewById(R.id.profile_name);
        profileTime = (TextView) view.findViewById(R.id.profile_time);
        weiboContent = (TextView) view.findViewById(R.id.weibo_content);
        multiImagView = (MultiImageView) view.findViewById(R.id.multiImagView);

        profileName.setText(nc);
        profileImg.setImageURI((Constants.FIND_URL_TX+ phoneNum.substring(phoneNum.length()-2)+".jpg"));

        lvCommentList.addHeaderView(view);
        lvCommentList.setAdapter(adapter);
        updateDatas();
    }
    /**
     * 初始联网更新数据
     * */
    private void updateDatas() {
        //请求评论列表
        String url1 = Constants.FIND_URL_COMMENT_TZ+"id="+myhf;
        StringRequest stringRequest1 = new StringRequest(Request.Method.GET, url1,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        LogUtil.e("请求评论详情", "updateDatas:" + response);
                        weiboContent.setText(response);
                        linearLoad.setVisibility(View.INVISIBLE);
                        adapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                linearLoad.setVisibility(View.INVISIBLE);
                ToastUtils.showToast(MyApplication.getContext(), "请求评论详情失败");
            }
        });

        //请求评论列表
        String url2 = Constants.FIND_URL_COMMENTLIST+"id="+myhf+"&yid="+0;
        StringRequest stringRequest2 = new StringRequest(Request.Method.GET, url2,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        LogUtil.e("请求评论列表", "updateDatas:" + response);
                        List<CommentBean> mList =
                                new Gson().fromJson(response, new TypeToken<List<CommentBean>>() {
                                }.getType());
                        LogUtil.e("请求评论列表", "mList:" + mList.size());
                        LogUtil.e("请求评论列表", "mDatas:" + mList.size());
                        commentDatas.clear();
                        commentDatas.addAll(mList);
                        LogUtil.e("请求评论列表", "mDatas:" + mList.size());
                        adapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ToastUtils.showToast(MyApplication.getContext(), "请求评论列表失败");
            }
        });
        Volley.newRequestQueue(this).add(stringRequest1);
        Volley.newRequestQueue(this).add(stringRequest2);



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
        intent.putExtra("msgId",myhf);
        startActivity(intent);
    }

    /**
     * 下拉刷新
     * */
    @Override
    public void onRefresh() {
        idCount = 0;
        String url = Constants.FIND_URL_COMMENTLIST+"id="+myhf+"&yid="+idCount;
        LogUtil.e("下拉刷新", "mList:" + url);
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
                ToastUtils.showToast(MyCommentTzActivity.this, "下拉刷新数据失败");
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
        String url = Constants.FIND_URL_COMMENTLIST+"id="+myhf+"&yid="+idCount;
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
                ToastUtils.showToast(MyCommentTzActivity.this, "上拉加载更多数据失败");
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
        requestComment();
    }
    /**
     * 请求评论列表
     * */
    private void requestComment() {
        //请求评论列表
        String url2 = Constants.FIND_URL_COMMENTLIST+"id="+myhf+"&yid="+0;
        StringRequest stringRequest2 = new StringRequest(Request.Method.GET, url2,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        LogUtil.e("请求评论列表", "updateDatas:" + response);
                        List<CommentBean> mList =
                                new Gson().fromJson(response, new TypeToken<List<CommentBean>>() {
                                }.getType());
                        LogUtil.e("请求评论列表", "mList:" + mList.size());
                        LogUtil.e("请求评论列表", "mDatas:" + mList.size());
                        commentDatas.clear();
                        commentDatas.addAll(mList);
                        LogUtil.e("请求评论列表", "mDatas:" + mList.size());
                        adapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ToastUtils.showToast(MyApplication.getContext(), "请求评论列表失败");
            }
        });
        Volley.newRequestQueue(this).add(stringRequest2);
    }
}
