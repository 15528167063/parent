package com.hzkc.parent.fragment.information;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.flyco.tablayout.SlidingTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.google.gson.Gson;
import com.hzkc.parent.Bean.InforTitlePhpBean;
import com.hzkc.parent.Bean.InforTitleRootBean;
import com.hzkc.parent.R;
import com.hzkc.parent.appcation.MyApplication;
import com.hzkc.parent.fragment.BaseFragment;
import com.hzkc.parent.mina.Constants;
import com.hzkc.parent.utils.AppUtils;
import com.hzkc.parent.utils.LogUtil;
import com.hzkc.parent.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;


public class InformationFragment extends BaseFragment implements OnTabSelectListener {

    private View v;
    private int id;
    ViewPager viewPager;
    SlidingTabLayout slidingTabLayout;

    private List<String> listid =new ArrayList<>() ;   //fragment 对应的ID
    private List<String> titleList = new ArrayList<>();   //fragment 对应的标题
    private List<InforTitlePhpBean> titleList1 = new ArrayList<>();   //fragment 对应的标题
    private ArrayList<Fragment> mFragments = new ArrayList<>(); //fragment 对应的界面
    private MyPagerAdapter mAdapter;



    public static Fragment getInstance(int classesId) {
        InformationFragment fragment = new InformationFragment();
        fragment.id = classesId;
        return fragment;
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View initView() {
        if(v==null){
            v = View.inflate(getActivity(), R.layout.fragment_information, null);
            finById();
            iniDatas();
        }
        return v;
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void finById() {
        viewPager = (ViewPager) v.findViewById(R.id.vp);
        slidingTabLayout = (SlidingTabLayout) v.findViewById(R.id.sliding_classes);
    }


    @Override
    public void iniData() {
    }

    public void iniDatas() {
        getTitleData();

    }



    private void getTitleData() {
        showLoading();
        String url = Constants.PHP_URL + "v" + AppUtils.getVerName() + "/infoLabel";
        Log.e("---------咨询标题：",url);
        StringRequest postsr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("---------咨询标题返回数据：",response);
                dissloading();
                InforTitleRootBean inforTitleRootBean =null;
                try {
                    inforTitleRootBean = new Gson().fromJson(response, InforTitleRootBean.class);
                }catch (Exception e){
                    return;
                }
                List<InforTitlePhpBean> mList=inforTitleRootBean.data;
                if(mList!=null || mList.size()>0){
                    titleList1.addAll(mList);
                }
                setData();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                dissloading();
                ToastUtils.showToast(MyApplication.getContext(), "加载数据失败");
            }
        });
        postsr.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.getRequestQueue().add(postsr);
    }




    private void setData() {
        if(titleList1.size()>5){
            slidingTabLayout.setTabSpaceEqual(false);
        }else {
            slidingTabLayout.setTabSpaceEqual(true);
        }
        for (int i = 0; i <titleList1.size() ; i++) {
            titleList.add(titleList1.get(i).label_name);
            listid.add(titleList1.get(i).label_id);
            int ii = Integer.parseInt(titleList1.get(i).label_id);
            mFragments.add(ActivityFragment.getInstance(ii));
        }
        setTab();
    }

    private int flag = 0;
    private void setTab() {
        if (flag == 0) {
            mAdapter = new MyPagerAdapter(getChildFragmentManager());
            viewPager.setAdapter(mAdapter);
            slidingTabLayout.setViewPager(viewPager);
            slidingTabLayout.setOnTabSelectListener(this);
            viewPager.setCurrentItem(0);
            flag++;
        }
    }



    @Override
    public void onTabSelect(int position) {
        LogUtil.e("----1选择了" + position);
    }

    @Override
    public void onTabReselect(int position) {
        LogUtil.e("----2选择了" + position);
    }

    /**
     * ViewpagerAdapter适配器
     */
    private class MyPagerAdapter extends FragmentPagerAdapter {
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titleList.get(position);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }
    }

}
