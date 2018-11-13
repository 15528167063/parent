package com.hzkc.parent.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hzkc.parent.Bean.FriendsListBean;
import com.hzkc.parent.MainActivity;
import com.hzkc.parent.R;
import com.hzkc.parent.activity.HandleAcitivity;
import com.hzkc.parent.activity.MyCommentTzActivity;
import com.hzkc.parent.adapter.WeiBoAdapter;
import com.hzkc.parent.appcation.MyApplication;
import com.hzkc.parent.jsondata.LoadingFooter;
import com.hzkc.parent.mina.Constants;
import com.hzkc.parent.utils.LogUtil;
import com.hzkc.parent.utils.ToastUtils;
import com.hzkc.parent.view.EndLessOnScrollListener;

import java.util.ArrayList;
import java.util.List;

import static android.widget.Toast.makeText;

/**
 * Created by lenovo-s on 2016/10/20.
 */

public class FindFragment extends BaseFragment implements View.OnClickListener {

    private View v;
    private TextView topTitle;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    //private TextView tvSave;
    private ImageView ivRefresh;
    //请求的id
    private int idCount;
    private WeiBoAdapter mAdapter;
    private Context mContext;
    private boolean myflag;


    LinearLayoutManager linearLayoutManager;
    //微博结构体
    private ArrayList<FriendsListBean> mDatas;

    protected int mState = LoadingFooter.Normal;
    private String parentUUID;


    @Override
    public View initView() {
        v = View.inflate(getActivity(), R.layout.fragment_find, null);
        return v;
    }

    @Override
    public void iniData() {
        topTitle = (TextView) v.findViewById(R.id.tv_top_title);
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swiperefreshlayout);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.fragment_home_content);
        //tvSave = (TextView) v.findViewById(R.id.tv_save);
        ivRefresh = (ImageView) v.findViewById(R.id.iv_refresh);
        ivRefresh.setImageResource(R.drawable.find_write);
        ivRefresh.setVisibility(View.VISIBLE);
        parentUUID = sp.getString("parentUUID", "");
        //ivRefresh.setText("发帖子");
        topTitle.setText("发现");
        myflag=true;
        mContext = getActivity();

        myReceiver=new Receiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("updata");
        getActivity().registerReceiver(myReceiver, filter);

        ivRefresh.setOnClickListener(this);

        initRecyclerView();
        initListener();
        updateDatas();



    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_refresh:
                Intent i = new Intent(getActivity(), HandleAcitivity.class);
                startActivity(i);
                sendMessages();
                break;
            default:
                break;
        }
    }

    public Receiver myReceiver;
    public class Receiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            if("updata".equals(intent.getAction())){
                updateDatas();
            }
        }
    }
    /**
     * 初始化
     */
    private void initRecyclerView() {
        mDatas = new ArrayList<>();
        mAdapter = new WeiBoAdapter(mContext, mDatas);
        linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

    }

    /**
     * 初始加载数据
     */
    public void updateDatas() {
        setState(LoadingFooter.Normal);
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
                idCount = 0;
                String url1 = Constants.FIND_URL_SEND_NEW_MSG+"ncid="+parentUUID;
                LogUtil.e("初始加载数据","检测是否有回复:"+url1);
                //检测是否有回复
                StringRequest stringRequest1 = new StringRequest(Request.Method.GET, url1,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(final String response) {
                                LogUtil.e("检测是否有回复", "updateDatas:" + response);
                                //final String s="64";
                                LogUtil.e("updateDatas:00000000000000000000000000000000000000:"+TextUtils.isEmpty(response));
                                if(!TextUtils.isEmpty(response)){
                                    LogUtil.e("updateDatas:11111111111111111111111111111111111:"+response);
                                    mAdapter.changNewMsgIcon(new WeiBoAdapter.NewMsgListener() {
                                        @Override
                                        public void SetTheMsgIcon(TextView tv) {
                                            tv.setVisibility(View.VISIBLE);
                                            tv.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    LogUtil.e("SetTheMsgIcon"+response);
                                                    Intent intent = new Intent(mContext,MyCommentTzActivity.class);
                                                    intent.putExtra("myhf",response);
                                                    startActivity(intent);
                                                    sendMessages();
                                                }
                                            });
                                            sp.edit().putString("findnewmsg",response).commit();
                                        }
                                    });
                                    mAdapter.notifyDataSetChanged();
                                }else{
                                    final String msgid = sp.getString("findnewmsg", "");
                                    LogUtil.e("updateDatas:22222222222222222222222222222222222:"+msgid);
                                    if(!TextUtils.isEmpty(msgid)){
                                        mAdapter.changNewMsgIcon(new WeiBoAdapter.NewMsgListener() {
                                            @Override
                                            public void SetTheMsgIcon(TextView tv) {
                                                tv.setVisibility(View.VISIBLE);
                                                tv.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        LogUtil.e("SetTheMsgIcon"+msgid);
                                                        Intent intent = new Intent(mContext,MyCommentTzActivity.class);
                                                        intent.putExtra("myhf",msgid);
                                                        startActivity(intent);
                                                        sendMessages();
                                                    }
                                                });
                                            }
                                        });
                                        mAdapter.notifyDataSetChanged();
                                    }
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        LogUtil.e("检测是否有回复","检测是否有回复失败");
                    }
                });
//                String url2 = Constants.FIND_URL_INIT+idCount;

                String url2 = Constants.FIND_URL_INIT+idCount+"&userid="+parentUUID;
                LogUtil.e("检测是否有回复",url2);
                //请求朋友圈列表
                StringRequest stringRequest2 = new StringRequest(Request.Method.GET, url2,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                LogUtil.e("初始加载数据", "updateDatas:" + response);
                                List<FriendsListBean> mList = new Gson().fromJson(response, new TypeToken<List<FriendsListBean>>() {}.getType());
                                LogUtil.e("初始加载数据", "mList:" + mList.size());
                                LogUtil.e("初始加载数据", "mDatas:" + mList.size());
                                mDatas.clear();
                                mDatas.addAll(mList);
                                LogUtil.e("初始加载数据", "mDatas:" + mList.size());
                                mAdapter.notifyDataSetChanged();
                                hideRefreshView();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hideRefreshView();
                        if(myflag){
                            ToastUtils.showToast(MyApplication.getContext(), "初始加载数据失败");
                        }
                        //makeText(getActivity(), "初始加载数据失败。。。", Toast.LENGTH_SHORT).show();
                    }
                });
                stringRequest1.setRetryPolicy(new DefaultRetryPolicy(5000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                stringRequest2.setRetryPolicy(new DefaultRetryPolicy(5000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                Volley.newRequestQueue(getActivity()).add(stringRequest1);
                Volley.newRequestQueue(getActivity()).add(stringRequest2);
            }
        });
    }

    /**
     * 下拉刷新
     */
    private void initPullRefresh() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setState(LoadingFooter.Normal);
                mSwipeRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(true);
                        idCount = 0;
//                        String url = Constants.FIND_URL_INIT+idCount;

                        String url = Constants.FIND_URL_INIT+idCount+"&userid="+parentUUID;
                        //请求朋友圈列表
                        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        LogUtil.e("下拉刷新加载数据", "updateDatas:" + response);
                                        List<FriendsListBean> mList =
                                                new Gson().fromJson(response, new TypeToken<List<FriendsListBean>>() {
                                                }.getType());
                                        LogUtil.e("下拉刷新加载数据", "mList:" + mList.size());
                                        LogUtil.e("下拉刷新加载数据", "mDatas:" + mList.size());
                                        mDatas.clear();
                                        mDatas.addAll(mList);
                                        LogUtil.e("下拉刷新加载数据", "mDatas:" + mList.size());
                                        mAdapter.notifyDataSetChanged();
                                        hideRefreshView();
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                hideRefreshView();
                                if(myflag){
                                    ToastUtils.showToast(MyApplication.getContext(), "下拉刷新加载数据");
                                }
                            }
                        });
                        Volley.newRequestQueue(getActivity()).add(stringRequest);
                    }
                });
            }
        });
    }

    /**
     * 上拉刷新，下拉加载更多
     */
    private void initListener() {
        initPullRefresh();
        initLoadMoreListener();
    }

    /**
     * 上拉加载更多
     */
    private void initLoadMoreListener() {
        mRecyclerView.addOnScrollListener(new EndLessOnScrollListener() {
            @Override
            public void onLoadNextPage(View v) {
                LogUtil.e("上拉加载更多", "正在加载中请稍后...");
                if (mState == LoadingFooter.Loading) {
                    LogUtil.e("上拉加载更多", "正在加载中请稍后...");
                    return;
                }
                setState(LoadingFooter.Loading);
                new Thread() {
                    @Override
                    public void run() {
                        idCount = idCount + 10;
//                        String url = Constants.FIND_URL_INIT + idCount;
                        String url = Constants.FIND_URL_INIT+idCount+"&userid="+parentUUID;
                        LogUtil.e("initLoadMoreListener","上拉加载更多....");
                        //请求朋友圈列表
                        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        hideRefreshView();
                                        LogUtil.e("上拉加载更多数据", "updateDatas:" + response);
                                        List<FriendsListBean> mList = new Gson().fromJson(response, new TypeToken<List<FriendsListBean>>() {}.getType());
                                        LogUtil.e("初始加载数据", "mList:" + mList.size());
                                        LogUtil.e("初始加载数据", "mDatas:" + mDatas.size());
                                        int lastIndex = mDatas.size();
                                        if (mDatas.addAll(mList)) {
                                            LogUtil.e("初始加载数据", "Normal:" + mList.size());
                                            setState(LoadingFooter.Normal);
                                            mAdapter.notifyItemRangeInserted(lastIndex, mList.size());
                                        } else {//加载失败
                                            LogUtil.e("初始加载数据", "TheEnd:" + mList.size());
                                            setState(LoadingFooter.TheEnd);
                                        }
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                setState(LoadingFooter.NetWorkError);
                                if(myflag){
                                    ToastUtils.showToast(MyApplication.getContext(), "上拉加载更多数据失败");
                                }
                            }
                        });
                        Volley.newRequestQueue(getActivity()).add(stringRequest);
                    }
                }.start();
            }
        });
    }

    /**
     * 隐藏下拉刷新图标
     */
    public void hideRefreshView() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    /**
     * 设置脚布局状态
     */
    protected void setState(int mState) {
        this.mState = mState;
        ((MainActivity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                changeAdaperState();
            }
        });
    }

    /**
     * 改变底部bottom的样式
     */
    protected void changeAdaperState() {
        if (mAdapter != null && mAdapter.mFooterHolder != null) {
            mAdapter.mFooterHolder.setData(mState);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        String refresh = sp.getString("refresh", "");
        LogUtil.e("onResume:1111111111111111111111111111111111111:"+refresh);
        if(!TextUtils.isEmpty(refresh)){
            updateDatas();
            sp.edit().putString("refresh","").commit();
        }
        final String msgid = sp.getString("findnewmsg", "");
        LogUtil.e("onResume:22222222222222222222222222222222222:"+TextUtils.isEmpty(msgid));
        if(!TextUtils.isEmpty(msgid)){
            mAdapter.changNewMsgIcon(new WeiBoAdapter.NewMsgListener() {
                @Override
                public void SetTheMsgIcon(TextView tv) {
                    tv.setVisibility(View.VISIBLE);
                    tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            LogUtil.e("SetTheMsgIcon:"+msgid);
                            Intent intent = new Intent(mContext,MyCommentTzActivity.class);
                            intent.putExtra("myhf",msgid);
                            startActivity(intent);
                            sendMessages();
                        }
                    });
                }
            });
            mAdapter.notifyDataSetChanged();
        }else{
            mAdapter.changNewMsgIcon(new WeiBoAdapter.NewMsgListener() {
                @Override
                public void SetTheMsgIcon(TextView tv) {
                    tv.setVisibility(View.INVISIBLE);
                }
            });
            mAdapter.notifyDataSetChanged();
        }
    }
    /**
     * 吐司的一个小方法
     */
    private void toast(final String str) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Toast.makeText(RegisterUserActivity.this, str, Toast.LENGTH_SHORT).show();
                ToastUtils.showToast(MyApplication.getContext(), str);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(myReceiver);
        myflag=false;
    }

    public void sendMessages(){
        Intent intent1 = new Intent();
        intent1.setAction("updata");
        mContext.sendBroadcast(intent1);
    }
}
