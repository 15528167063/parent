package com.hzkc.parent.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.hzkc.parent.R;
import com.hzkc.parent.appcation.MyApplication;
import com.hzkc.parent.event.SlbhDataEvent;
import com.hzkc.parent.event.SlbhEvent;
import com.hzkc.parent.event.StopInternetPlanEvent;
import com.hzkc.parent.event.YjgkEvent;
import com.hzkc.parent.greendao.GreenDaoManager;
import com.hzkc.parent.greendao.entity.ChildContrlFlag;
import com.hzkc.parent.greendao.entity.NetPlanDataBean;
import com.hzkc.parent.greendao.gen.ChildContrlFlagDao;
import com.hzkc.parent.greendao.gen.NetPlanDataBeanDao;
import com.hzkc.parent.jsondata.CmdCommon;
import com.hzkc.parent.utils.LogUtil;
import com.hzkc.parent.utils.ToastUtils;
import com.jaeger.library.StatusBarUtil;
import com.wx.wheelview.adapter.ArrayWheelAdapter;
import com.wx.wheelview.widget.WheelView;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class EyeProtectActivity extends BaseActivity implements View.OnClickListener {
    private TextView tvTopTitle;
    private ImageView ivFinish;
    private WheelView wvTimeHour;
    private WheelView wvTimeMinute;
    private TextView btOpen;
    String[] mhour = {"5",  "10", "15",  "20", "25",  "30",  "35",  "40", "45", "50",
            "55", "60"};
    String[] mMinute = {"5",  "10", "15",  "20", "25",  "30",  "35",  "40", "45", "50",
            "55", "60"};
    private List<String> hourList;
    private List<String> minuteList;
    private String childuuid;
    private ChildContrlFlagDao dao;
    private String parentUUID;
    private boolean deleteChildFlag;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 101:
                    if(!deleteChildFlag){
                        ToastUtils.showToast(MyApplication.getContext(),"当前连接不正常，请稍后再试");
                    }
                    if(pd !=null&& pd.isShowing()){
                        pd.dismiss();
                    }
                    break;
                case 205://处理视力保护反馈
                    ordeslbh(msg);
                    break;
                default:
                    break;
            }
        }
    };
    private String slbh;
    private ProgressDialog pd;
    private String mTimeMinute;
    private String mTimeHour;
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eye_protect);
        StatusBarUtil.setColorNoTranslucent(this,getResources().getColor(R.color.newcolor));
        initView();
        initData();
        //初始化DAO数据库和wheelview的数据
        initDao();
        EventBus.getDefault().register(this);
        runnable = new Runnable() {
            public void run() {
                Message msg = Message.obtain();
                msg.what = 101;
                handler.sendMessage(msg);
            }
        };
    }

    private void initData() {
        hourList = new ArrayList<>();
        minuteList = new ArrayList<>();
        for (int i = 0; i < mhour.length; i++) {
            hourList.add(mhour[i]);
        }
        for (int i = 0; i < mMinute.length; i++) {
            minuteList.add(mMinute[i]);
        }
        //初始化小时
        wvTimeHour.setWheelAdapter(new ArrayWheelAdapter(EyeProtectActivity.this)); // 文本数据源
        wvTimeHour.setWheelSize(5);
        wvTimeHour.setLoop(true);
        WheelView.WheelViewStyle style1 = new WheelView.WheelViewStyle();
        style1.selectedTextColor=0xFF72ce9b;
        style1.textSize=18;
        style1.textColor = Color.GRAY;
        wvTimeHour.setStyle(style1);
        wvTimeHour.setWheelClickable(true);
        wvTimeHour.setSkin(WheelView.Skin.None); // common皮肤
        wvTimeHour.setWheelData(hourList);  // 数据集合
        //初始化分钟
        wvTimeMinute.setWheelAdapter(new ArrayWheelAdapter(EyeProtectActivity.this)); // 文本数据源
        wvTimeMinute.setWheelSize(5);
        wvTimeMinute.setLoop(true);
        WheelView.WheelViewStyle style2 = new WheelView.WheelViewStyle();
        style2.selectedTextColor=0xFF72ce9b;
        style2.textSize=18;
        style2.textColor = Color.GRAY;
        wvTimeMinute.setStyle(style2);
        wvTimeMinute.setWheelClickable(true);
        wvTimeMinute.setSkin(WheelView.Skin.None); // common皮肤
        wvTimeMinute.setWheelData(minuteList);  // 数据集合

    }
    /**
     * 初始化DAO数据库
     * */
    private void initDao() {
        Intent intent = getIntent();
        childuuid = intent.getStringExtra("ChildUUID");
        parentUUID = sp.getString("parentUUID", "");
        LogUtil.i(TAG, "initDao: "+childuuid);
        dao = GreenDaoManager.getInstance().getSession().getChildContrlFlagDao();
        ChildContrlFlag findData = dao.queryBuilder().where(ChildContrlFlagDao.Properties.Childuuid.eq(childuuid)).build().unique();
        if(findData != null){//修改数据库
            wvTimeHour.setSelection(hourList.indexOf(findData.getSlbhSpacetime()));
            wvTimeMinute.setSelection(minuteList.indexOf(findData.getSlbhResttime()));
            if(findData.getSlbhflag().equals(CmdCommon.CMD_FLAG_CLOSE)){
                btOpen.setText("开启");
            }else{
                btOpen.setText("关闭");
            }
        }else{//添加数据库 默认关闭
            ChildContrlFlag childFlag = new ChildContrlFlag(null,childuuid, parentUUID,"", ""
                    , CmdCommon.CMD_FLAG_CLOSE, CmdCommon.CMD_FLAG_CLOSE
                    , CmdCommon.CMD_FLAG_CLOSE,"30","5",CmdCommon.CMD_FLAG_CLOSE, CmdCommon.CMD_FLAG_CLOSE);
            dao.insert(childFlag);
            wvTimeHour.setSelection(hourList.indexOf("30"));
            wvTimeMinute.setSelection(minuteList.indexOf("5"));
        }
    }

    private void initView() {
        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        ivFinish = (ImageView) findViewById(R.id.iv_finish_back);
        wvTimeHour = (WheelView) findViewById(R.id.wv_time_hour);
        wvTimeMinute = (WheelView) findViewById(R.id.wv_time_minute);
        btOpen = (TextView) findViewById(R.id.bt_open);

        tvTopTitle.setText("视力保护");
        ivFinish.setVisibility(View.VISIBLE);
        ivFinish.setOnClickListener(this);
        btOpen.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_finish_back:
                finish();
                break;
            case R.id.bt_open://开启计划
                //提示一键管控和视力保护和管控计划不能一起开
                boolean sptishi = sp.getBoolean("slbhtishi",false);
                if(btOpen.getText().toString().equals("开启") && !sptishi ){
                    showTiShiDialog();
                    return;
                }
                openPlan();
                handler.postDelayed(runnable, 10000);
                break;
            default:
                break;
        }
    }
    /**
     * 执行计划
     * */
    private void openPlan() {
        if(pd !=null&& pd.isShowing()){
            pd.dismiss();
        }
        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setCancelable(false);
        pd.setMessage("正在执行操作，请稍候");
        pd.show();
        deleteChildFlag=false;
        mTimeMinute = (String) wvTimeMinute.getSelectionItem();
        mTimeHour = (String) wvTimeHour.getSelectionItem();
        LogUtil.i(TAG, "openPlan: hour"+ mTimeHour +"minute"+ mTimeMinute);
        ChildContrlFlag findData = dao.queryBuilder().where(ChildContrlFlagDao.Properties.Childuuid.eq(childuuid)).build().unique();
        if(findData != null){//修改数据库
            String planflag = findData.getSlbhflag();
            if(planflag.equals(CmdCommon.CMD_FLAG_CLOSE)){//开启
                slbh =CmdCommon.CMD_FLAG_OPEN;
                sp.edit().putBoolean("slbh", true).putBoolean("childFlag2", true).commit();
                LogUtil.i(TAG, "视力保护设置: 开启");
            }else{//关闭计划
                slbh =CmdCommon.CMD_FLAG_CLOSE;
                sp.edit().putBoolean("slbh", false).putBoolean("childFlag2", true).commit();
                LogUtil.i(TAG, "视力保护设置: 关闭");
            }
            EventBus.getDefault().post(new SlbhEvent(parentUUID, childuuid, slbh, mTimeHour, mTimeMinute), "slbh");
            LogUtil.e(TAG, "视力保护: " + slbh);
            //finish();
        }else{
            ToastUtils.showToast(MyApplication.getContext(), "孩子不存在");
        }
    }

    @Override
    protected void onDestroy() {
        deleteChildFlag=true;
        if(pd!=null&&pd.isShowing()){
            pd.dismiss();
        }
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    /**
     * 视力保护功能反馈
     */
    @Subscriber(mode = ThreadMode.ASYNC, tag = "slbhata")
    public void slbhDataEvent(SlbhDataEvent dataEvent) {
        deleteChildFlag=true;
        handler.removeCallbacks(runnable);
        Message obtain = Message.obtain();
        LogUtil.e(TAG, "视力保护功能反馈:slbhDataEvent");
        obtain.obj = dataEvent.slbhState;
        obtain.what = 205;
        handler.sendMessage(obtain);
    }

    /**
     * 处理视力保护反馈
     * */
    private void ordeslbh(Message msg) {
        if(pd !=null&& pd.isShowing()){
            pd.dismiss();
        }
        ChildContrlFlagDao childDao = GreenDaoManager.getInstance().getSession().getChildContrlFlagDao();
        ChildContrlFlag findChild = childDao.queryBuilder().where(ChildContrlFlagDao.Properties.Childuuid.eq(childuuid)).build().unique();
        /////////////////////
        if( btOpen.getText().equals("开启")){
            if (findChild.getYjgkflag().equals(CmdCommon.CMD_FLAG_OPEN) ) {
                EventBus.getDefault().post(new YjgkEvent(parentUUID, childuuid, "0"), "yjgk");
                findChild.setYjgkflag(CmdCommon.CMD_FLAG_CLOSE);
                childDao.update(findChild);
                sp.edit().putBoolean("yjgk"+childuuid,false).commit();
                sp.edit().putBoolean("yjgkclose",true).commit();
            }
            /////////////////////
            NetPlanDataBeanDao dao  = GreenDaoManager.getInstance().getSession().getNetPlanDataBeanDao();
            List<NetPlanDataBean>  planlist = dao.queryBuilder().where(NetPlanDataBeanDao.Properties.Childuuid.eq(childuuid)).orderDesc(NetPlanDataBeanDao.Properties._id).build().list();
            boolean isplan=false;
            for (int i = 0; i < planlist.size(); i++) {
                if(planlist.get(i).getPlanflag().equals("1")){
                    isplan=true;
                    planlist.get(i).setPlanflag("0");
                    dao.update(planlist.get(i));
                }
            }
            if(isplan){
                EventBus.getDefault().post(new StopInternetPlanEvent(childuuid,parentUUID), "stopInternetPlan");
            }
        }
        /////////////////////

        LogUtil.e("ordeslbh","处理视力保护反馈:"+slbh);
        if (findChild != null) {
            if (slbh.equals(CmdCommon.CMD_FLAG_CLOSE)) {
                ToastUtils.showToast(MyApplication.getContext(),"视力保护功能关闭");
                btOpen.setText("开启");
                findChild.setSlbhflag(CmdCommon.CMD_FLAG_CLOSE);
            } else {
                findChild.setSlbhSpacetime(mTimeHour);
                findChild.setSlbhResttime(mTimeMinute);
                ToastUtils.showToast(MyApplication.getContext(),"视力保护功能开启");
                btOpen.setText("关闭");
                findChild.setSlbhflag(CmdCommon.CMD_FLAG_OPEN);
            }
            childDao.update(findChild);
        } else {
            ChildContrlFlag childFlag = new ChildContrlFlag(null, childuuid, parentUUID, "", ""
                    , CmdCommon.CMD_FLAG_CLOSE, CmdCommon.CMD_FLAG_CLOSE
                    , CmdCommon.CMD_FLAG_CLOSE, "30", "5", CmdCommon.CMD_FLAG_CLOSE, CmdCommon.CMD_FLAG_CLOSE);
            childDao.insert(childFlag);
            btOpen.setText("开启");
            ToastUtils.showToast(MyApplication.getContext(),"视力保护功能关闭");
        }
    }

    /**
     * (提示一键管控和视力保护和管控计划不能一起开)
     * */
    public CheckBox cb_box;
    public TextView tv_content,tvFinish1;
    public AlertDialog showDialog;
    public void showTiShiDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this,R.style.MyAlertDialog);
        //builder.setCancelable(false);
        View dialogView =  View.inflate(this, R.layout.dialog_internet_tishi, null);
        tvFinish1 = (TextView) dialogView.findViewById(R.id.tv_finish);
        tv_content = (TextView) dialogView.findViewById(R.id.tv_content);
        cb_box = (CheckBox) dialogView.findViewById(R.id.cb_box);
        tvFinish1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog.dismiss();
                if(cb_box.isChecked()){
                    sp.edit().putBoolean("slbhtishi",true).commit();
                }
                openPlan();
                handler.postDelayed(runnable, 10000);
            }
        });
        builder.setView(dialogView);
        showDialog = builder.show();
    }
}
