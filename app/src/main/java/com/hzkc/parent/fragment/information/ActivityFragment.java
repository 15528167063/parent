package com.hzkc.parent.fragment.information;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.hzkc.parent.Bean.InformatRootbean;
import com.hzkc.parent.Bean.OtherBean0;
import com.hzkc.parent.Bean.OtherBean1;
import com.hzkc.parent.Bean.TeachingBean2;
import com.hzkc.parent.R;
import com.hzkc.parent.activity.InforH5Activity;
import com.hzkc.parent.adapter.base.RecyclerViewDivider;
import com.hzkc.parent.adapter.rv.ViewHolder;
import com.hzkc.parent.adapter.rv.mul.BaseMulTypeAdapter;
import com.hzkc.parent.adapter.rv.mul.IMulTypeHelper;
import com.hzkc.parent.appcation.MyApplication;
import com.hzkc.parent.fragment.BaseFragment;
import com.hzkc.parent.mina.Constants;
import com.hzkc.parent.utils.AppUtils;
import com.hzkc.parent.utils.DensityUtil;
import com.hzkc.parent.utils.ToastUtils;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jcodecraeer.xrecyclerview.progressindicator.AVLoadingIndicatorView.BallSpinFadeLoader;
import static com.jcodecraeer.xrecyclerview.progressindicator.AVLoadingIndicatorView.LineSpinFadeLoader;

/**
 * Created by lenovo-s on 2016/10/20.
 */

public class ActivityFragment extends BaseFragment implements XRecyclerView.LoadingListener {   //育儿新版php


    private View v;
    private int id;
    private XRecyclerView recycleview;
    private LinearLayout noContentL;//无数据
    private List<IMulTypeHelper> mDatas;

    public static Fragment getInstance(int classesId) {
        ActivityFragment fragment = new ActivityFragment();
        fragment.id = classesId;
        return fragment;
    }
    @Override
    public View initView() {
        v = View.inflate(getActivity(), R.layout.fragment_teach, null);
        recycleview=(XRecyclerView)v.findViewById(R.id.contact_list);
        noContentL=(LinearLayout)v.findViewById(R.id.ll_no_content);
        return v;
    }

    @Override
    public void iniData() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recycleview.setLayoutManager(layoutManager);
        recycleview.setLoadingListener(this);
        recycleview.setRefreshProgressStyle(LineSpinFadeLoader);
        recycleview.setLoadingMoreProgressStyle(BallSpinFadeLoader);
        recycleview.setLoadingMoreEnabled(true);
        recycleview.addItemDecoration(new RecyclerViewDivider(getContext(), LinearLayoutManager.VERTICAL, DensityUtil.dip2px(getContext(),8), R.color.blue));
        showLoading();
        pageIndex=0;
        httpgetDatas(pageIndex,true);
    }
    public int pageIndex=0;
    private void httpgetDatas(final int Index, final boolean isrefresh) {
        String url=Constants.PHP_URL+"v"+ AppUtils.getVerName()+"/info";
        Log.e("资讯信息列表", "url:" + url);
        StringRequest postsr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("-------------response",response);
                dissloading();
                if(isrefresh){
                    recycleview.refreshComplete();
                    mDatas=initDatas(response,true);
                    initRecycly();
                }else {
                    recycleview.loadMoreComplete();
                    mDatas=initDatas(response,false);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                dissloading();
                if(isrefresh){
                    recycleview.refreshComplete();
                }else {
                    recycleview.loadMoreComplete();
                }
                ToastUtils.showToast(MyApplication.getContext(), "加载数据失败");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //创建一个集合，放的是keyvalue的key是参数名与value是参数值
                Map<String, String> map = new HashMap<String, String>();
                map.put("label_id",id+"");
                map.put("page",Index+ "");
                return map;
            }
        };
        postsr.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.getRequestQueue().add(postsr);
    }

    public  List<IMulTypeHelper> datas = new ArrayList<>();
    public List<IMulTypeHelper> initDatas(String result,boolean isrefrsh) {
        InformatRootbean s =null;
        try {
            s = new Gson().fromJson(result, InformatRootbean.class);
        }catch (Exception e){
            e.printStackTrace();
        }

        if (isrefrsh) {
            datas.clear();
        }

        if (s == null || s.getData() == null ) {
            recycleview.setNoMore(true);
            return null;//"title_img":["119.23.38.125:8085\/images\/title\/"],
        }
        for (int i = 0; i < s.getData().size(); i++) {
            if (s.getData().get(i).getString() == null || s.getData().get(i).getString().size() == 0 ) {
                datas.add(new OtherBean0(s.getData().get(i).getInfor_id()+"", s.getData().get(i).getPartyurl(), s.getData().get(i).getCreate_time(), s.getData().get(i).getString(), s.getData().get(i).getTitle(), null, s.getData().get(i).getCreate_time(),s.getData().get(i).getView()));
            } else if (s.getData().get(i).getString().size() == 1) {
                datas.add(new OtherBean1(s.getData().get(i).getInfor_id()+"", s.getData().get(i).getPartyurl(), s.getData().get(i).getCreate_time(), s.getData().get(i).getString(), s.getData().get(i).getTitle(), null,s.getData().get(i).getCreate_time(),s.getData().get(i).getView()));
            } else if (s.getData().get(i).getString().size() == 3) {
                datas.add(new TeachingBean2(s.getData().get(i).getInfor_id()+"", s.getData().get(i).getPartyurl(), s.getData().get(i).getCreate_time(), s.getData().get(i).getString(), s.getData().get(i).getTitle(),null));
            }
        }
        return datas;
    }
    @Override
    public void onRefresh() {
        pageIndex=0;
        httpgetDatas(pageIndex,true);
    }

    @Override
    public void onLoadMore() {
        pageIndex=pageIndex+10;
        httpgetDatas(pageIndex,false);
    }

    public void initRecycly(){

        if(mDatas==null || mDatas.size()==0){
            noContentL.setVisibility(View.VISIBLE);
            return;
        }
        noContentL.setVisibility(View.GONE);
        recycleview.setAdapter(new BaseMulTypeAdapter(getActivity(), mDatas) {
            @Override
            public void convert(ViewHolder holder, final IMulTypeHelper iMulTypeHelper) {
                super.convert(holder, iMulTypeHelper);

                switch (iMulTypeHelper.getItemLayoutId()) {
                    case R.layout.item_teach_type0:

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                OtherBean0 mulTypeMulBean0 = (OtherBean0) iMulTypeHelper;
                                Intent intent=new Intent(getActivity(), InforH5Activity.class);
                                intent.putExtra("title", mulTypeMulBean0.title);
                                intent.putExtra("messageId", mulTypeMulBean0.getInfor_id());
                                startActivity(intent);
                            }
                        });
                        break;
                    case R.layout.item_teach_type1:

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                OtherBean1 mulTypeMulBean0 = (OtherBean1) iMulTypeHelper;
                                Intent intent = new Intent(getActivity(), InforH5Activity.class);
                                intent.putExtra("title", mulTypeMulBean0.title);
                                intent.putExtra("messageId", mulTypeMulBean0.getInfor_id());
                                startActivity(intent);
                            }
                        });
                        break;
                    case R.layout.item_teach_type2:
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                TeachingBean2 mulTypeMulBean0 = (TeachingBean2) iMulTypeHelper;
                                Intent intent = new Intent(getActivity(), InforH5Activity.class);
                                intent.putExtra("title", mulTypeMulBean0.title);
                                intent.putExtra("messageId", mulTypeMulBean0.getInfor_id());
                                startActivity(intent);
                            }
                        });
                        break;
                }
            }
        });
    }
}
