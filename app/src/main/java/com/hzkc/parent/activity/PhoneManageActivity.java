package com.hzkc.parent.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.flyco.tablayout.SlidingTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.hzkc.parent.R;
import com.hzkc.parent.event.RequestWhiteAppListEvent;
import com.hzkc.parent.event.WhiteAppBackEvent;
import com.hzkc.parent.fragment.phonemanage.BlackPhoneFragment;
import com.hzkc.parent.fragment.phonemanage.WhitePhoneFragment;
import com.hzkc.parent.utils.LogUtil;
import com.jaeger.library.StatusBarUtil;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * 管控设置
 */
public class PhoneManageActivity extends BaseActivity implements View.OnClickListener, OnTabSelectListener {
    private TextView tvTopTitle,tv_save;
    private ImageView ivFinish;
    private  String  childuuid;

    private View v;
    SlidingTabLayout slidingTabLayout;
    ViewPager viewPager;


    private List<Integer> list =new ArrayList<>() ;   //fragment 对应的ID
    private List<String> titleList = new ArrayList<>();   //fragment 对应的标题
    private ArrayList<Fragment> mFragments = new ArrayList<>(); //fragment 对应的界面
    private MyPagerAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_manage);
        EventBus.getDefault().register(this);
        StatusBarUtil.setColorNoTranslucent(this,getResources().getColor(R.color.blue));
        initView();
        initDatas();
    }

    private String childUUID;
    private String parentUUID;
    private void initView() {
        childUUID = getIntent().getStringExtra("ChildUUID");
        parentUUID = sp.getString("parentUUID", "");
            
        sp.edit().putBoolean("addapps",false).commit();
        viewPager=(ViewPager) findViewById(R.id.vp);
        slidingTabLayout=(SlidingTabLayout) findViewById(R.id.sliding_classes);
        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        tv_save = (TextView) findViewById(R.id.tv_save1);
        ivFinish = (ImageView) findViewById(R.id.iv_finish);
        tvTopTitle.setText("黑白名单");
        ivFinish.setVisibility(View.VISIBLE);
        ivFinish.setOnClickListener(this);
        tv_save.setVisibility(View.VISIBLE);
        tv_save.setOnClickListener(this);
        childuuid=getIntent().getStringExtra("ChildUUID");
        viewPager.setOffscreenPageLimit(2);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }
            @Override
            public void onPageSelected(int position) {
                if(position==0){
                    ((WhitePhoneFragment)mFragments.get(position)).updata();
                }else if(position==1){
                    ((BlackPhoneFragment)mFragments.get(position)).updata();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
    private void initDatas() {
        list.add(0);
        list.add(1);
        titleList.add("白名单");
        titleList.add("黑名单");
        mFragments.add(WhitePhoneFragment.getInstance(0));
        mFragments.add(BlackPhoneFragment.getInstance(1));
        setTab();
    }
    private int flag = 0;
    private void setTab() {
        if (flag == 0) {
            mAdapter = new MyPagerAdapter(getSupportFragmentManager());
            viewPager.setAdapter(mAdapter);
            slidingTabLayout.setViewPager(viewPager);
            slidingTabLayout.setOnTabSelectListener(this);
            viewPager.setCurrentItem(0);
            flag++;
        }
    }

    public Handler handler=new Handler();
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_finish://返回界面
                if(sp.getBoolean("addapps",false)){
                    showPop();
                    return;
                }else {
                    finish();
                }
                break;
            case R.id.tv_save1:
                showLoading();
                sp.edit().putBoolean("addapps",false).commit();
                EventBus.getDefault().post(new RequestWhiteAppListEvent(childUUID, parentUUID,null), "updatePhonedata");
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(pd!=null&&pd.isShowing()){
                            pd.dismiss();
                        }
                    }
                },7000);
                break;
            default:
                break;
        }
    }


    @Override
    public void onBackPressed() {
        if(sp.getBoolean("addapps",false)){
            showPop();
            return;
        }else {
            finish();
        }
        super.onBackPressed();
    }

    @Override
    public void onTabSelect(int position) {
        LogUtil.e("----1选择了" + position);
    }

    @Override
    public void onTabReselect(int position) {
        LogUtil.e("-----2选择了" + position);
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

    /**
     * 请求孩子应用列表
     * */
    @Subscriber(mode = ThreadMode.ASYNC, tag = "whitephone")
    private void OrderChildAppListBack (WhiteAppBackEvent returndata) {
        Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
        dissloading();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if(pd!=null&&pd.isShowing()){
            pd.dismiss();
        }
    }
    /**
     * 点击注销时弹出确认框打电话
     */
    public void showPop() {
        final AlertDialog ad = new AlertDialog.Builder(this).create();
        View view = LayoutInflater.from(this).inflate(R.layout.deteledialog, null);
        TextView tv_phone = (TextView) view.findViewById(R.id.tv_phone);
        TextView tv_queding = (TextView) view.findViewById(R.id.tv_queding);
        TextView tv_cancle = (TextView) view.findViewById(R.id.tv_cancle);
        ad.setView(view);
        tv_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ad.dismiss();
                finish();
            }
        });
        tv_queding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ad.dismiss();
                showLoading();
                sp.edit().putBoolean("addapps",false).commit();
                EventBus.getDefault().post(new RequestWhiteAppListEvent(childUUID, parentUUID,null), "updatePhonedata");
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(pd!=null&&pd.isShowing()){
                            pd.dismiss();
                        }
                    }
                },7000);
            }
        });
        ad.show();
    }

}
