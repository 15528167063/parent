package com.hzkc.parent.solider.activity;

import android.app.AlertDialog;
import android.content.Intent;
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
import com.hzkc.parent.activity.BaseActivity;
import com.hzkc.parent.event.RequestWhiteAppListEvent;
import com.hzkc.parent.event.WhiteAppBackEvent;
import com.hzkc.parent.greendao.entity.AppDataBean;
import com.hzkc.parent.solider.fragment.BlackAppFragment;
import com.hzkc.parent.solider.fragment.TeQuanFragment;
import com.hzkc.parent.solider.fragment.TotleAppFragment;
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
public class AppManageSoliderActivity extends BaseActivity implements View.OnClickListener, OnTabSelectListener {
    private TextView tvTopTitle,tv_save,tv_name;
    private ImageView ivFinish,iv_add;
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
        setContentView(R.layout.activity_appmanages);
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

        sp.edit().putBoolean("addapp",false).commit();
        viewPager=(ViewPager) findViewById(R.id.vp);
        slidingTabLayout=(SlidingTabLayout) findViewById(R.id.sliding_classes);
        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);


        tv_save = (TextView) findViewById(R.id.tv_save1);
        tv_name = (TextView) findViewById(R.id.tv_name);
        iv_add = (ImageView) findViewById(R.id.iv_add);
        ivFinish = (ImageView) findViewById(R.id.iv_finish);

        String currentname = sp.getString("childName","");
        tv_name.setText(currentname+"的应用列表，你可以进行黑白名单操作");

        tvTopTitle.setText("应用管理");
        ivFinish.setVisibility(View.VISIBLE);
        ivFinish.setOnClickListener(this);
        tv_save.setVisibility(View.VISIBLE);
        iv_add.setVisibility(View.VISIBLE);
        tv_save.setOnClickListener(this);
        iv_add.setOnClickListener(this);
        childuuid=getIntent().getStringExtra("ChildUUID");
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
    }
    private void initDatas() {
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
                if(sp.getBoolean("addapp",false)){
//                    showPop();
                    enSure();
                    return;
                }else {
                    finish();
                }
                break;
            case R.id.tv_save1:
                if(datas.size()==0){
                    return;
                }
                showLoading();
                sp.edit().putBoolean("addapp",false).commit();
                EventBus.getDefault().post(new RequestWhiteAppListEvent(childUUID, parentUUID,datas), "requestWhiteApp");
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(pd!=null&&pd.isShowing()){
                            pd.dismiss();
                        }
                    }
                },7000);
                break;
            case R.id.iv_add://返回界面
                if(sp.getBoolean("addapp",false)){
//                    showPop();
                    enSure();
                    return;
                }else {
                    Intent intent=new Intent(this,MineAppManageActivity.class);
                    intent.putExtra("ChildUUID",childuuid);
                    startActivity(intent);
                }
                break;
            default:
                break;
        }
    }


    @Override
    public void onBackPressed() {
        if(sp.getBoolean("addapp",false)){
//            showPop();
            enSure();
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
    @Subscriber(mode = ThreadMode.ASYNC, tag = "whiteAppDataBack1")
    private void OrderChildAppListBack (WhiteAppBackEvent returndata) {
        Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
        datas.clear();
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
     * 自动保存
     */
    public void enSure() {
        sp.edit().putBoolean("addapp",false).commit();
        EventBus.getDefault().post(new RequestWhiteAppListEvent(childUUID, parentUUID,datas), "requestWhiteApp");
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(pd!=null&&pd.isShowing()){
                    pd.dismiss();
                }
            }
        },7000);
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
                sp.edit().putBoolean("addapp",false).commit();
                EventBus.getDefault().post(new RequestWhiteAppListEvent(childUUID, parentUUID,datas), "requestWhiteApp");
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

    public List<AppDataBean> getDatas() {
        return datas;
    }

    public void setDatas(ArrayList<AppDataBean> datas) {
        this.datas = datas;
    }

    private List<AppDataBean> datas = new ArrayList<>(); //临时保存的app列表
    public  void  addDatas(AppDataBean dataBean) {
        for (int i = 0; i < datas.size(); i++) {
            if(datas.get(i).getApppackgename().equals(dataBean.getApppackgename())){
                datas.remove(i);
                break;
            }
        }
        datas.add(dataBean);
    }
}
