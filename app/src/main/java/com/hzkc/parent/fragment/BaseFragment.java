package com.hzkc.parent.fragment;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.umeng.analytics.MobclickAgent;

/**
 * Created by lenovo-s on 2016/10/20.
 */

public abstract class BaseFragment extends Fragment {

    public SharedPreferences sp;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        sp = getActivity().getSharedPreferences("info", getActivity().MODE_PRIVATE);
        View view=initView();
        iniData();
        return view;
    }

    public abstract void iniData();

    public abstract View initView();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isfirst=true;
    }

    /**
     * 友盟统计
     * */
    public boolean isfirst=false;
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("MainScreen"); //统计页面，"MainScreen"为页面名称，可自定义
    }
    public void onPause() {
        super.onPause();
        isfirst=false;
        MobclickAgent.onPageEnd("MainScreen");
    }

    private ProgressDialog pd;
    public void showLoading() {
        if(pd!=null&&pd.isShowing()){
            pd.dismiss();
        }
        pd = new ProgressDialog(getActivity());
        //pd.setTitle("提示");
        pd.setMessage("正在加载数据中，请稍候");
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setCancelable(true);
        pd.show();
    }
    public void dissloading() {
        if(pd!=null&&pd.isShowing()){
            pd.dismiss();
        }
    }
}
