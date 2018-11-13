package com.hzkc.parent.fragment.information;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.hzkc.parent.Bean.TeachingBean0;
import com.hzkc.parent.Bean.TeachingBean1;
import com.hzkc.parent.Bean.TeachingRoot;
import com.hzkc.parent.R;
import com.hzkc.parent.activity.FunctionActivity;
import com.hzkc.parent.activity.WebDataCommdActivity;
import com.hzkc.parent.adapter.base.RecyclerViewDivider;
import com.hzkc.parent.adapter.rv.ViewHolder;
import com.hzkc.parent.adapter.rv.mul.BaseMulTypeAdapter;
import com.hzkc.parent.adapter.rv.mul.IMulTypeHelper;
import com.hzkc.parent.appcation.MyApplication;
import com.hzkc.parent.fragment.BaseFragment;
import com.hzkc.parent.mina.Constants;
import com.hzkc.parent.utils.DensityUtil;
import com.hzkc.parent.utils.ToastUtils;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.ArrayList;
import java.util.List;

import static com.jcodecraeer.xrecyclerview.progressindicator.AVLoadingIndicatorView.BallSpinFadeLoader;
import static com.jcodecraeer.xrecyclerview.progressindicator.AVLoadingIndicatorView.LineSpinFadeLoader;


public class TeachFragments extends BaseFragment implements XRecyclerView.LoadingListener {

    private View v;
    private int id;
    private XRecyclerView recycleview;
    private List<IMulTypeHelper> mDatas;

    public static Fragment getInstance(int classesId) {
        TeachFragments fragment = new TeachFragments();
        fragment.id = classesId;
        return fragment;
    }
    @Override
    public View initView() {
        v = View.inflate(getActivity(), R.layout.fragment_teach, null);
        recycleview=(XRecyclerView)v.findViewById(R.id.contact_list);
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
        httpgetDatas(pageIndex,true);
    }
    public int pageIndex=0;
    private void httpgetDatas(int Index, final boolean isrefresh) {
        String url2 = Constants.FIND_URL+"pyq/get_info.php?start="+Index;
        Log.e("-------------url2",url2);
        StringRequest stringRequest1 = new StringRequest(Request.Method.GET, url2,
                new Response.Listener<String>() {
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
            public void onErrorResponse(VolleyError error) {
                dissloading();
                if(isrefresh){
                    recycleview.refreshComplete();
                }else {
                    recycleview.loadMoreComplete();
                }
                ToastUtils.showToast(MyApplication.getContext(), "加载数据失败");
            }
        });
        stringRequest1.setRetryPolicy(new DefaultRetryPolicy(15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(getActivity()).add(stringRequest1);

    }
    public  List<IMulTypeHelper> datas = new ArrayList<>();
    public List<IMulTypeHelper> initDatas(String result,boolean isrefrsh) {
        TeachingRoot s =null;
        try {
            s = new Gson().fromJson(result, TeachingRoot.class);
        }catch (Exception e){
            return null;
        }
        if(isrefrsh){
            datas.clear();
        }
        if(s==null || s.result==null || s.result.result==null  || s.result.result.list==null){
            recycleview.setNoMore(true);
            return null;
        }
        for (int i = 0; i < s.result.result.list.size(); i++) {
            if(s.result.result.list.get(i).pic==null || TextUtils.isEmpty(s.result.result.list.get(i).pic)){
                datas.add(new TeachingBean0(s.result.result.list.get(i).src,s.result.result.list.get(i).weburl,s.result.result.list.get(i).time,s.result.result.list.get(i).pic,s.result.result.list.get(i).title,s.result.result.list.get(i).content));
            } else  {
                datas.add(new TeachingBean1(s.result.result.list.get(i).src,s.result.result.list.get(i).weburl,s.result.result.list.get(i).time,s.result.result.list.get(i).pic,s.result.result.list.get(i).title,s.result.result.list.get(i).content));
            }
        }
        return datas;
    }
//public List<IMulTypeHelper> initDatas(String result,boolean isrefrsh) {
//    TeachRt s =null;
//    try {
//        s = new Gson().fromJson(result, TeachRt.class);
//    }catch (Exception e){
//        return null;
//    }
//    if(isrefrsh){
//        datas.clear();
//    }
//    if(s==null || s.result==null || s.result.result==null  || s.result.result.list==null){
//        recycleview.setNoMore(true);
//        return null;
//    }
//    for (int i = 0; i < s.result.result.list.size(); i++) {
//        if(s.result.result.list.get(i).pic==null || TextUtils.isEmpty(s.result.result.list.get(i).pic)){
//            datas.add(new TeachingBean0(s.result.result.list.get(i).src,s.result.result.list.get(i).weburl,s.result.result.list.get(i).time,s.result.result.list.get(i).pic,s.result.result.list.get(i).title,s.result.result.list.get(i).content));
//        } else  {
//            datas.add(new TeachingBean1(s.result.result.list.get(i).src,s.result.result.list.get(i).weburl,s.result.result.list.get(i).time,s.result.result.list.get(i).pic,s.result.result.list.get(i).title,s.result.result.list.get(i).content));
//        }
//    }
//    return datas;
//}
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
        if(mDatas==null){
            return;
        }
        recycleview.setAdapter(new BaseMulTypeAdapter(getActivity(), mDatas) {
            @Override
            public void convert(ViewHolder holder, final IMulTypeHelper iMulTypeHelper) {
                super.convert(holder, iMulTypeHelper);

                switch (iMulTypeHelper.getItemLayoutId()) {
                    case R.layout.item_teach_type0:
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                TeachingBean0 mulTypeMulBean0 = (TeachingBean0) iMulTypeHelper;
                                if(TextUtils.isEmpty(mulTypeMulBean0.getContent())){
                                    Intent intent=new Intent(getActivity(), FunctionActivity.class);
                                    intent.putExtra("urlPath", mulTypeMulBean0.getWeburl());
                                    startActivity(intent);
                                }else {
                                    Intent intent=new Intent(getActivity(), WebDataCommdActivity.class);
                                    intent.putExtra("content", mulTypeMulBean0.content);
                                    intent.putExtra("title", mulTypeMulBean0.title);
                                    intent.putExtra("time", mulTypeMulBean0.time);
                                    intent.putExtra("laiyuan", mulTypeMulBean0.src);
                                    startActivity(intent);
                                }
                            }
                        });
                        break;
                    case R.layout.item_teach_type1:
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                TeachingBean1 mulTypeMulBean0 = (TeachingBean1) iMulTypeHelper;
                                if (TextUtils.isEmpty(mulTypeMulBean0.getContent())) {
                                    Intent intent = new Intent(getActivity(), FunctionActivity.class);
                                    intent.putExtra("urlPath", mulTypeMulBean0.getWeburl());
                                    startActivity(intent);
                                } else {
                                    Intent intent = new Intent(getActivity(), WebDataCommdActivity.class);
                                    intent.putExtra("content", mulTypeMulBean0.content);
                                    intent.putExtra("title", mulTypeMulBean0.title);
                                    intent.putExtra("time", mulTypeMulBean0.time);
                                    intent.putExtra("laiyuan", mulTypeMulBean0.src);
                                    startActivity(intent);
                                }
                            }
                        });
                        break;
                }
            }
        });
    }
}
