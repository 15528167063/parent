package com.hzkc.parent.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.hzkc.parent.R;
import com.hzkc.parent.event.RequestChildAppListEvent;
import com.hzkc.parent.event.RequestWhiteAppListEvent;
import com.hzkc.parent.event.WhiteAppBackEvent;
import com.hzkc.parent.fragment.txappmanage.AppInstallFragment;
import com.hzkc.parent.fragment.txappmanage.AppManageFragment;
import com.hzkc.parent.greendao.entity.AppDataBean;
import com.jaeger.library.StatusBarUtil;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;

/**
 * Created by Administrator on 2017/10/24.
 */
public class AppManageLastActivity extends BaseActivity implements View.OnClickListener {
    ImageView headView;
    TextView head_confirm;
    RadioGroup re_group;
    RadioButton re_white;
    RadioButton re_manage;
    FrameLayout im_main_layout;
    public AppManageFragment appFragment;
    public AppInstallFragment installFragment;
    private int currentTabIndex = -1;
    private Fragment[] fragments;
    public int positions =0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_observer_updata);
        EventBus.getDefault().register(this);
        ButterKnife.inject(this);
        StatusBarUtil.setColorNoTranslucent(this, getResources().getColor(R.color.blue));
        initView();
        initData();
    }
    protected void initView() {
        childUUID = getIntent().getStringExtra("ChildUUID");
        parentUUID = sp.getString("parentUUID", "");
        headView= (ImageView) findViewById(R.id.head_back);
        head_confirm= (TextView) findViewById(R.id.head_confirm);
        re_group= (RadioGroup) findViewById(R.id.re_group);
        re_white= (RadioButton) findViewById(R.id.re_white);
        re_manage= (RadioButton) findViewById(R.id.re_manage);
        im_main_layout= (FrameLayout) findViewById(R.id.im_main_layout);
        appFragment=new AppManageFragment();
        installFragment=new AppInstallFragment();
        headView.setOnClickListener(this);
        head_confirm.setOnClickListener(this);
    }
    private void initData() {
        fragments = new Fragment[]{ appFragment,installFragment};
        if (positions == 0) {
            head_confirm.setVisibility(View.VISIBLE);
            re_white.setChecked(true);
        } else if (positions == 1) {
            head_confirm.setVisibility(View.GONE);
            re_manage.setChecked(true);
        }
    }

    @OnCheckedChanged({R.id.re_white, R.id.re_manage})
    public void onGroupClickMethod(RadioButton radioButton, boolean isCheckedId) {
        Log.e(TAG, "sessionIdle: ----------");
        switch (radioButton.getId()) {
            case R.id.re_white:
                if (isCheckedId) {
                    head_confirm.setVisibility(View.VISIBLE);
                    onTabClicked(0);
                }
                break;
            case R.id.re_manage:
                if(sp.getBoolean("addapp",false)){
                    isfinsh=false;
                    onTabClicked(0);
                    showPop();
                }else if (isCheckedId) {
                    head_confirm.setVisibility(View.GONE);
                    onTabClicked(1);
                }
                break;
        }
    }
    public boolean isfinsh=true;
    public void onTabClicked(int index) {
        if (currentTabIndex != index) {
            FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
            if (currentTabIndex != -1) {
                trx.hide(fragments[currentTabIndex]);
            }
            if (!fragments[index].isAdded()) {
                trx.add(R.id.im_main_layout, fragments[index]);
            }
            trx.show(fragments[index]).commit();
        }
        currentTabIndex = index;
    }

    public Handler handler=new Handler();
    private String childUUID;
    private String parentUUID;
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.head_back:
                if(sp.getBoolean("addapp",false)){
                    showPop();
                }else {
                    finish();
                }
                break;
            case R.id.head_confirm:
                if(datas.size()==0){
                    return;
                }
                if(datas.size()>=60){
                    Toast.makeText(this, "添加的数据过多，请分批设置", Toast.LENGTH_SHORT).show();
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
            default:
                break;
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
                if(isfinsh){
                    finish();
                }else {
                    isfinsh=true;
                    sp.edit().putBoolean("addapp",false).commit();
                    EventBus.getDefault().post(new RequestChildAppListEvent(childUUID, parentUUID), "requestchildApp");  //因为修改刷新的本地数据，所以取消时要重新获取数据回复到修改之前
                    head_confirm.setVisibility(View.GONE);
                    onTabClicked(1);
                }
            }
        });
        tv_queding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ad.dismiss();
                showLoading();
                sp.edit().putBoolean("addapp",false).commit();
                if(!isfinsh){
                    isfinsh=true;
                    head_confirm.setVisibility(View.GONE);
                    onTabClicked(1);
                }
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

    /**
     * 保存应用列表
     * */
    @Subscriber(mode = ThreadMode.ASYNC, tag = "whiteAppDataBack1")
    private void OrderChildAppListBack (WhiteAppBackEvent returndata) {
        datas.clear();
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

    private List<AppDataBean> datas = new ArrayList<>(); //临时保存的app列表
    public List<AppDataBean> getDatas() {
        return datas;
    }

    public void setDatas(ArrayList<AppDataBean> datas) {
        this.datas = datas;
    }
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