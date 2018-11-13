package com.hzkc.parent.fragment.appmanage;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.flyco.tablayout.SlidingTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.hzkc.parent.R;
import com.hzkc.parent.fragment.BaseFragment;
import com.hzkc.parent.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by lenovo-s on 2016/10/20.
 */

public class AppManageFragment extends BaseFragment implements View.OnClickListener, OnTabSelectListener {
    private View v;
    private int id;
    private String childUUID;
    private String parentUUID;
    SlidingTabLayout slidingTabLayout;
    ViewPager viewPager;
    private List<Integer> list =new ArrayList<>() ;   //fragment 对应的ID
    private List<String> titleList = new ArrayList<>();   //fragment 对应的标题
    private ArrayList<Fragment> mFragments = new ArrayList<>(); //fragment 对应的界面
    private MyPagerAdapter mAdapter;
    public AppManageFragment() {
    }
    public static Fragment getInstance(int classesId) {
        AppManageFragment fragment = new AppManageFragment();
        fragment.id = classesId;
        return fragment;
    }
    @Override
    public View initView() {
        v = View.inflate(getActivity(), R.layout.activity_appmanage1, null);
        ButterKnife.inject(this, v);
        childUUID = getActivity().getIntent().getStringExtra("ChildUUID");
        parentUUID = sp.getString("parentUUID", "");
        sp.edit().putBoolean("addapp",false).commit();
        viewPager=(ViewPager) v.findViewById(R.id.vp);
        slidingTabLayout=(SlidingTabLayout) v.findViewById(R.id.sliding_classes);
        viewPager.setOffscreenPageLimit(5);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                if(position==0){
                    ((TotleAppFragment)mFragments.get(position)).updata();
                }else if(position==1){
                    ((BlackAppFragment)mFragments.get(position)).updata();
                } else if(position==2){
                    ((TeQuanFragment)mFragments.get(position)).updata();
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        return v;
    }

    @Override
    public void iniData() {
        list.add(0);
        list.add(1);
        list.add(2);
        titleList.add("白名单");
        titleList.add("黑名单");
        titleList.add("特权名单");
        mFragments.add(TotleAppFragment.getInstance(0));
        mFragments.add(BlackAppFragment.getInstance(1));
        mFragments.add(TeQuanFragment.getInstance(2));
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
    public void onClick(View view) {
    }

    @Override
    public void onTabSelect(int position) {
        LogUtil.e("----1选择了" + position);
    }

    @Override
    public void onTabReselect(int position) {
        LogUtil.e("----2选择了" + position);
    }

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
