package com.hzkc.parent.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.flyco.tablayout.SlidingTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.hzkc.parent.R;
import com.hzkc.parent.fragment.information.ActivityFragment;
import com.hzkc.parent.utils.LogUtil;
import com.jaeger.library.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/7/24.
 */

public class MyOrderActivity extends BaseActivity implements OnTabSelectListener, View.OnClickListener {
    private ViewPager viewPager;
    private SlidingTabLayout slidingTabLayout;
    private TextView tv_title;
    private ImageView iv_back;


    private MyPagerAdapter mAdapter;
    private List<Integer> list = new ArrayList<>();   //fragment 对应的ID
    private List<String> titleList = new ArrayList<>();   //fragment 对应的标题
    private ArrayList<Fragment> mFragments = new ArrayList<>(); //fragment 对应的界面
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order);
        StatusBarUtil.setColorNoTranslucent(this,getResources().getColor(R.color.blue));
        initView();
        initDatas();
    }

    private void initView() {
        viewPager = (ViewPager) findViewById(R.id.vp);
        slidingTabLayout = (SlidingTabLayout)findViewById(R.id.sliding_classes);
        tv_title = (TextView) findViewById(R.id.tv_top_title);
        iv_back=(ImageView)findViewById(R.id.iv_finish);
        tv_title.setText("我的订单");
        iv_back.setVisibility(View.VISIBLE);
        iv_back.setOnClickListener(this);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initDatas() {
        list.add(0);
        list.add(1);
        list.add(2);
        list.add(3);
        titleList.add("全部");
        titleList.add("追踪");
        titleList.add("完成");
        titleList.add("其他");
        mFragments.add(ActivityFragment.getInstance(0));
        mFragments.add(ActivityFragment.getInstance(1));
        mFragments.add(ActivityFragment.getInstance(2));
        mFragments.add(ActivityFragment.getInstance(0));
        setTab();
    }

    private int flag = 0;
    private void setTab() {
        if (flag == 0) {
            mAdapter = new MyOrderActivity.MyPagerAdapter(this.getSupportFragmentManager());
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
        LogUtil.e("-----2选择了" + position);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_finish:
                finish();
                break;
        }
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
