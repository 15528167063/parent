package com.hzkc.parent.solider.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.github.mikephil.charting.charts.BarChart;
import com.google.gson.Gson;
import com.hzkc.parent.Bean.TeamRoot;
import com.hzkc.parent.R;
import com.hzkc.parent.activity.AppUseActivity;
import com.hzkc.parent.activity.SoliderLocationActivity;
import com.hzkc.parent.appcation.MyApplication;
import com.hzkc.parent.event.ChildLineFlagEvent;
import com.hzkc.parent.event.ChildMessageDataEvent;
import com.hzkc.parent.event.ChildOnlineAndOfflineEvent;
import com.hzkc.parent.event.InitEvent;
import com.hzkc.parent.event.InitRefreshEvent;
import com.hzkc.parent.event.RequestChildAppListEvent;
import com.hzkc.parent.event.YjgkDataEvent;
import com.hzkc.parent.event.YjspTuanEvent;
import com.hzkc.parent.fragment.BaseFragment;
import com.hzkc.parent.greendao.GreenDaoManager;
import com.hzkc.parent.greendao.entity.AppDataBean;
import com.hzkc.parent.greendao.entity.ChildContrlFlag;
import com.hzkc.parent.greendao.gen.AppDataBeanDao;
import com.hzkc.parent.greendao.gen.ChildContrlFlagDao;
import com.hzkc.parent.jsondata.CmdCommon;
import com.hzkc.parent.mina.Constants;
import com.hzkc.parent.solider.activity.AppManageSoliderActivity;
import com.hzkc.parent.solider.activity.ChooseChildrenActivity;
import com.hzkc.parent.solider.activity.ControllDetailActivity;
import com.hzkc.parent.solider.activity.ParentZxingsActivity;
import com.hzkc.parent.solider.activity.SoliderDetailActivity;
import com.hzkc.parent.solider.activity.TeamControllActivity;
import com.hzkc.parent.utils.LogUtil;
import com.hzkc.parent.utils.MPChartHelper;
import com.hzkc.parent.utils.NetworkUtil;
import com.hzkc.parent.utils.ToastUtils;
import com.hzkc.parent.view.AffirmDialog;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;


/**
 * 成长守护界面fragment
 * Created by lenovo-s on 2016/10/20.
 */

public class InternetsFragment extends BaseFragment implements View.OnClickListener {
    private static final String TAG = "InternetFragment";
    private View v;
    private TextView tvMainHzzl;
    private TextView tvMainXzhz;
    private TextView tvMainHzzt;
    private ImageView ivMainHzdw;
    private ImageView ivMainIcon;
    private TextView tvMainTjhz;
    private TextView tvAddChlid;
    private TextView tvFinish,tv_num;
    private ImageView ivAddFinish;
    private AlertDialog showStartTime;
    private TextView  tv_top_title;
    private ImageView iv_head,iv_blue_add;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView tv_yjgk;
    private String childuuid;
    private String childName;
    private boolean yjgkFlag;
    private LinearLayout linearLayout;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 201://处理初始化数据
                    orderAddChildData(msg);
                    break;
                case 204://处理一键管控反馈
                    ordeyjgk(msg);
                    break;
                case 205://处理刷新界面数据
                    orderInit();
                    break;
                case 206:
                    onResume();
                    break;
                case 106:
                    if (!yjgkFlag) {
                        ToastUtils.showToast(MyApplication.getContext(),"网络连接不通畅,请稍后再试");
                    }
                    if(pd!=null&&pd.isShowing()){
                        pd.dismiss();
                    }
                    break;
                case 108:
                    if(!sp.getBoolean("isinit",false)){
                        getActivity().finish();
                        return;
                    }
                    ToastUtils.showToast(MyApplication.getContext(),"刷新数据失败,请检查网络稍后再试");
                    mSwipeRefreshLayout.setRefreshing(false);
                    break;
                default:
                    break;
            }
        }
    };

    private String parentUUID;
    private String childSex;
    private String childFrom;
    private String headurl;
    private String childLineState;
    private ProgressDialog pd;
    private String yjgk;
    private Runnable runnable;
    private Runnable runnable2;
    private MeBroadcastReceiver meBroadcastReceiver;
    private IntentFilter mFilter;
    private LinearLayout iv_controll,iv_shili,iv_gksz,iv_ydgj,iv_swgl;
    private ImageView iv_ycs;
    public boolean  isVip=false;
    public String   viplist,currentname ;
    public boolean   isOneControll=false ;
    private AffirmDialog affirmDialog;
    private BarChart LBarChartView;
    private TextView tv_detail;
    @Override
    public View initView() {
        v = View.inflate(getActivity(), R.layout.fragment_internets, null);
        return v;
    }
    /**
     * 初始化页面数据
     */
    @Override
    public void iniData() {
        tv_top_title = (TextView) v.findViewById(R.id.tv_top_title);
        iv_head = (ImageView) v.findViewById(R.id.iv_head);
        iv_blue_add = (ImageView) v.findViewById(R.id.iv_add_blue);
        LBarChartView = (BarChart)v. findViewById(R.id.frameNewBase);
        tv_detail = (TextView)v. findViewById(R.id.tv_detail);
        linearLayout = (LinearLayout) v. findViewById(R.id.lin1);
        iv_head.setVisibility(View.VISIBLE);
        iv_blue_add.setVisibility(View.VISIBLE);
        tv_top_title.setText("武警支队团控");
        iv_head.setOnClickListener(this);
        linearLayout.setOnClickListener(this);
        iv_blue_add.setOnClickListener(this);
        tv_detail.setOnClickListener(this);
        getControllChilid();
        tvMainHzzl = (TextView) v.findViewById(R.id.tv_main_hzzl);
        tvMainXzhz = (TextView) v.findViewById(R.id.tv_main_xzhz);
        tvMainHzzt = (TextView) v.findViewById(R.id.tv_main_hzzt);
        ivMainHzdw = (ImageView) v.findViewById(R.id.iv_main_hzdw);
        ivMainIcon = (ImageView) v.findViewById(R.id.iv_main_icon);
        tvMainTjhz = (TextView) v.findViewById(R.id.tv_main_tjhz);
        iv_controll = (LinearLayout) v.findViewById(R.id.iv_controll);
        tv_yjgk = (TextView) v.findViewById(R.id.tv_yjgk);
        tv_num = (TextView) v.findViewById(R.id.tv_num);
        iv_controll = (LinearLayout) v.findViewById(R.id.iv_controll);
        iv_shili = (LinearLayout) v.findViewById(R.id.iv_shili);
        iv_gksz = (LinearLayout) v.findViewById(R.id.iv_gksz);
        iv_ydgj = (LinearLayout) v.findViewById(R.id.iv_ydgj);
        iv_swgl = (LinearLayout) v.findViewById(R.id.iv_swgl);
        iv_ycs = (ImageView) v.findViewById(R.id.iv_ycs);
        viplist = sp.getString("viplist", "");
        isVip = sp.getBoolean("isVip", false);
        currentname = sp.getString("childName","士兵");

        if(!TextUtils.isEmpty(viplist)){
            String[] sourceStrArray = viplist.split(",");
            for (int i = 0; i < sourceStrArray.length; i++) {
                if(sourceStrArray[i].equals(Constants.VIP_ONE_CONTROLL)){
                    isOneControll=true;
                    break;
                }else {
                    isOneControll=false;
                }
            }
        }
        iv_controll.setOnClickListener(this);
        tv_yjgk.setOnClickListener(this);
        iv_controll.setOnClickListener(this);
        iv_shili.setOnClickListener(this);
        iv_gksz.setOnClickListener(this);
        iv_ydgj.setOnClickListener(this);
        iv_swgl.setOnClickListener(this);
        iv_ycs.setOnClickListener(this);
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.sf_refresh);

        yjgkFlag=false;
        yjgk="";

        tvMainHzzl.setOnClickListener(this);
        tvMainXzhz.setOnClickListener(this);
        tvMainHzzt.setOnClickListener(this);
        ivMainHzdw.setOnClickListener(this);
        ivMainIcon.setOnClickListener(this);
        tvMainTjhz.setOnClickListener(this);
        meBroadcastReceiver = new MeBroadcastReceiver();
        mFilter = new IntentFilter();
        mFilter.addAction("touxiang");
        getActivity().registerReceiver(meBroadcastReceiver, mFilter);
        affirmDialog = new AffirmDialog(getActivity());
        affirmDialog.setTitleText("您还不是VIP会员，不能使用VIP功能，是否立即充值?");
        affirmDialog.setAffirmClickListener(this);
        initRecyclerview();
        //注册事键
        EventBus.getDefault().register(this);



        runnable = new Runnable() {
            @Override
            public void run() {
                Message msg = Message.obtain();
                msg.what = 106;
                handler.sendMessage(msg);
            }
        };
        runnable2 = new Runnable() {
            @Override
            public void run() {
                Message msg = Message.obtain();
                msg.what = 108;
                handler.sendMessage(msg);
            }
        };
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(true);
                Log.d("Swipe", "Refreshing Number");
                sp.edit().putBoolean("isinit",false).commit();
                EventBus.getDefault().post(new InitEvent(parentUUID), "init");
                getControllChilid();
                handler.postDelayed(runnable2, 5000);
            }
        });
    }


    /**
     * 初始化RecycleView以及条目点击事键
     */
    private void initRecyclerview() {
        childuuid = sp.getString("ChildUUID", "");
        parentUUID = sp.getString("parentUUID", "");
        childName = sp.getString("childName", "");
        childSex = sp.getString("childSex", "");
        childFrom = sp.getString("childFrom", "");
        headurl= "";
        //判断男孩还是女孩
        if(childSex.equals("1")){
            Glide.with(getActivity()).load(headurl).error(R.drawable.child_detail_icon_boy).into(ivMainIcon);
        }else  if(childSex.equals("0")){
            Glide.with(getActivity()).load(headurl).error(R.drawable.child_detail_icon_girl).into(ivMainIcon);
        }else {
            Glide.with(getActivity()).load(headurl).error(R.drawable.child_detail_icon).into(ivMainIcon);
        }
    }
    /**
     * 显示添加士兵对话框
     * */
    public void showDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity(),R.style.MyAlertDialog);
        //builder.setCancelable(false);
        View dialogView =  View.inflate(getActivity(), R.layout.dialog_internet_add_child, null);
        tvAddChlid = (TextView) dialogView.findViewById(R.id.tv_add_chlid);
        tvFinish = (TextView) dialogView.findViewById(R.id.tv_finish);
        ivAddFinish = (ImageView) dialogView.findViewById(R.id.iv_add_fish);
        tvAddChlid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showStartTime.dismiss();
                Intent intent = new Intent(getActivity(), ParentZxingsActivity.class);
                //使用uuid生成一个特定的字符串
                intent.putExtra("parentZxing", parentUUID);
                startActivity(intent);
            }
        });
        tvFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showStartTime.dismiss();
            }
        });
        ivAddFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showStartTime.dismiss();
            }
        });
        builder.setView(dialogView);
        showStartTime = builder.show();
    }
    /**
     * 添加士兵的点击事键
     */
    @Override
    public void onClick(View v) {
        boolean isNet = NetworkUtil.isConnected();
        if (!isNet) {
            ToastUtils.showToast(MyApplication.getContext(),"网络不通，请检查网络再试");
            return;
        }
        switch (v.getId()) {
            case R.id.iv_add_blue://添加士兵
                tjhz();
                break;
            case R.id.iv_head://选择士兵
                if (TextUtils.isEmpty(childuuid)) {
                    showDialog();
                    return;
                }
                chooseChildren();
                break;
            case R.id.iv_controll: //士兵资料
                if (TextUtils.isEmpty(childuuid)) {
                    showDialog();
                    return;
                }
                if(sp.getBoolean("team"+childuuid,false)){
                    Toast.makeText(getActivity(), "士兵正处于团控状态，请稍后再试", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    Intent intents = new Intent(getActivity(), SoliderDetailActivity.class);
                    intents.putExtra("ChildUUID",childuuid);
                    intents.putExtra("childFrom",childFrom);
                    startActivity(intents);
                }
                break;
            case R.id.iv_shili://士兵定位
                if (TextUtils.isEmpty(childuuid)) {
                    showDialog();
                    return;
                }
                if(sp.getBoolean("team"+childuuid,false)){
                    Toast.makeText(getActivity(), "士兵正处于团控状态，请稍后再试", Toast.LENGTH_SHORT).show();
                }else {
                    hzdlwz(isNet);
                }
                break;
            case R.id.iv_gksz:  //应用管理
                if (TextUtils.isEmpty(childuuid)) {
                    showDialog();
                    return;
                }
                if(sp.getBoolean("team"+childuuid,false)){
                    Toast.makeText(getActivity(), "士兵正处于团控状态，请稍后再试", Toast.LENGTH_SHORT).show();
                }else {
                    Intent intent4 = new Intent(getActivity(), AppManageSoliderActivity.class);
                    intent4.putExtra("ChildUUID", childuuid);
                    startActivity(intent4);
                }
                break;
            case R.id.iv_ydgj:  //应用追踪
                if (TextUtils.isEmpty(childuuid)) {
                    showDialog();
                    return;
                }
                if(sp.getBoolean("team"+childuuid,false)){
                    Toast.makeText(getActivity(), "士兵正处于团控状态，请稍后再试", Toast.LENGTH_SHORT).show();
                }else {
                    Intent intent3 = new Intent(getActivity(), AppUseActivity.class);
                    intent3.putExtra("ChildUUID", childuuid);
                    startActivity(intent3);
                }
                break;
            case R.id.iv_swgl:     //团控计划
                if (TextUtils.isEmpty(childuuid)) {
                    showDialog();
                    return;
                }
                if(sp.getBoolean("team"+childuuid,false)){
                    Toast.makeText(getActivity(), "士兵正处于团控状态，请稍后再试", Toast.LENGTH_SHORT).show();
                }else {
                    Intent intent = new Intent(getActivity(), TeamControllActivity.class);
                    intent.putExtra("ChildUUID", childuuid);
                    startActivity(intent);
                }
                break;
            case R.id.tv_detail:     //团控详情
                if (TextUtils.isEmpty(childuuid)) {
                    showDialog();
                    return;
                }

                Intent intent = new Intent(getActivity(), ControllDetailActivity.class);
                intent.putExtra("ChildUUID", childuuid);
                intent.putExtra("type",1);
                startActivity(intent);
                break;
            case R.id.lin1:
                break;
            default:

        }
    }

    private void chooseChildren() {
        Intent intent = new Intent(getActivity(), ChooseChildrenActivity.class);
        intent.putExtra("childUUid",childuuid);
        startActivityForResult(intent,1003);
    }






    /**
     * 士兵地理位置
     * */
    private void hzdlwz(boolean isNet) {
        if (!isNet) {
            ToastUtils.showToast(MyApplication.getContext(), "网络不通，请检查网络再试");
            return;
        }
        if (!TextUtils.isEmpty(childuuid)) {
//            childLineState = sp.getString(childuuid + "online", "");
//            if (childLineState.equals(CmdCommon.CMD_OFFLINE)) {//下线
//                ToastUtils.showToast(MyApplication.getContext(), "士兵处于离线状态,无法获取士兵当前位置");
//                return;
//            }
            Intent intent = new Intent(getActivity(), SoliderLocationActivity.class);
            intent.putExtra("ChildUUID", childuuid);
            startActivity(intent);
        } else {
            showDialog();
            return;
        }
    }


    /**
     * 添加士兵
     * */
    private void tjhz() {
        Intent intent = new Intent(getActivity(), ParentZxingsActivity.class);
        intent.putExtra("parentZxing", parentUUID);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        childuuid = sp.getString("ChildUUID", "");
        parentUUID = sp.getString("parentUUID", "");
        childName = sp.getString("childName", "");
        childSex = sp.getString("childSex", "");
        childFrom = sp.getString("childFrom", "");
        childLineState = sp.getString(childuuid + "online", "");
        boolean childFlag1 = sp.getBoolean("childFlag1", false);
        boolean childFlag2 = sp.getBoolean("childFlag2", false);

        boolean yjgk1 = sp.getBoolean("yjgk"+childuuid, false);
        boolean yjgk2 = sp.getBoolean("team"+childuuid, false);
        Log.e("-------yjgk"+childuuid,yjgk1+"");
        Log.e("-------team"+childuuid,yjgk2+"");
        if (yjgk1) {
//            tv_yjgk.setText("一键管控(已开启)");
//            if(isOneControll){
//                iv_controll.setImageResource(R.drawable.vip_1_1);
//            }else {
//                iv_controll.setImageResource(R.drawable.main_close);
//            }
//        } else {
//            tv_yjgk.setText("一键管控");
//            if(isOneControll){
//                iv_controll.setImageResource(R.drawable.vip_1_2);
//            }else {
//                iv_controll.setImageResource(R.drawable.main_no);
//            }
        }
        if(sp.getBoolean("team"+childuuid,false)){
//            tv_yjgk.setText("一键管控(团控)");
//            if(isOneControll){
//                iv_controll.setImageResource(R.drawable.vip_1_1);
//            }else {
//                iv_controll.setImageResource(R.drawable.main_close);
//            }
        }

        //判断男孩还是女孩
        if(childSex.equals("1")){
            Glide.with(getActivity()).load(headurl).error(R.drawable.child_detail_icon_boy).into(ivMainIcon);
        }else  if(childSex.equals("0")){
            Glide.with(getActivity()).load(headurl).error(R.drawable.child_detail_icon_girl).into(ivMainIcon);
        }else {
            Glide.with(getActivity()).load(headurl).error(R.drawable.child_detail_icon).into(ivMainIcon);
        }

        if (!TextUtils.isEmpty(childuuid)) {
            getAppData(childuuid);
        }
    }
    /**
     * 获取应用列表
     */
    private AppDataBeanDao dao;
    private void getAppData(String childUUID) {
        dao = GreenDaoManager.getInstance().getSession().getAppDataBeanDao();
        List<AppDataBean> mlist = dao.queryBuilder().where(AppDataBeanDao.Properties.Childuuid.eq(childUUID)).build().list();
        if (mlist==null  ||  mlist.size() == 0) {
            sp.edit().putBoolean("isapplist",true).commit();     //作用是主动获取app列表
            sp.edit().putBoolean("issave",true).commit();   //作用是发送消息  mianservice保存数据后就不用执行下面的程序了
            EventBus.getDefault().post(new RequestChildAppListEvent(childUUID, parentUUID), "requestchildApp");
        }
    }

    /**
     * 处理添加士兵数据
     */
    private void orderAddChildData(Message msg) {
        ChildMessageDataEvent dataEvent = (ChildMessageDataEvent) msg.obj;
        String newChildUUID = dataEvent.childUUID;
        LogUtil.i(TAG, "orderAddChildData: " + dataEvent.childUUID + dataEvent.childSex + dataEvent.childName + dataEvent.childFrom);
        if (!TextUtils.isEmpty(newChildUUID)) {
            if (TextUtils.isEmpty(childuuid)) {
                childuuid = newChildUUID;
                childName = dataEvent.childName;
                childSex = dataEvent.childSex;
                childFrom = dataEvent.childFrom;
                sp.edit().putString("ChildUUID", childuuid)
                        .putString("childName", childName)
                        .putString("childSex", childSex)
                        .putString("childFrom", childFrom)
                        .commit();

                Message obtain = Message.obtain();
                obtain.what = 206;
                handler.sendMessage(obtain);
            }
        }
    }

    /**
     * 一键管控功能
     */
//    private void yjgk() {
//        yjgkFlag=false;
//        if(pd!=null&&pd.isShowing()){
//            pd.dismiss();
//        }
//        pd = new ProgressDialog(getActivity());
//        pd.setMessage("正在执行操作，请稍候");
//        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        pd.setCancelable(false);
//        pd.show();
//        ChildContrlFlagDao childDao = GreenDaoManager.getInstance().getSession().getChildContrlFlagDao();
//        ChildContrlFlag findChild = childDao.queryBuilder().where(ChildContrlFlagDao.Properties.Childuuid.eq(childuuid)).build().unique();
//        if (findChild != null) {
//            if (findChild.getYjgkflag().equals(CmdCommon.CMD_FLAG_OPEN)) {
//                yjgk = CmdCommon.CMD_FLAG_CLOSE;
//            } else {
//                yjgk = CmdCommon.CMD_FLAG_OPEN;
//            }
//        } else {
//            ChildContrlFlag childFlag = new ChildContrlFlag(null, childuuid, parentUUID, "", ""
//                    , CmdCommon.CMD_FLAG_CLOSE, CmdCommon.CMD_FLAG_CLOSE
//                    , CmdCommon.CMD_FLAG_CLOSE, "30", "5", CmdCommon.CMD_FLAG_CLOSE, CmdCommon.CMD_FLAG_CLOSE);
//            childDao.insert(childFlag);
//            yjgk = CmdCommon.CMD_FLAG_CLOSE;
//        }
//        LogUtil.e(TAG, "一键管控: " + yjgk);
//        sp.edit().putBoolean("isyjgk",false).commit();
//        EventBus.getDefault().post(new YjgkEvent(parentUUID, childuuid, yjgk), "yjgk");
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if(!sp.getBoolean("isyjgk",false)){
//                    getActivity().finish();
//                }
//            }
//        },2000);
//
//    }

    /**
     * 一键管控功能反馈
     */
    @Subscriber(mode = ThreadMode.ASYNC, tag = "yjgkdata")
    public void yjgkDataEvent(YjgkDataEvent dataEvent) {
        yjgkFlag=true;
        handler.removeCallbacks(runnable);
        Message obtain = Message.obtain();
        LogUtil.e(TAG, "一键管控功能反馈:yjgkDataEvent");
        obtain.obj = dataEvent.yjgkState;
        obtain.what = 204;
        handler.sendMessage(obtain);
    }
    /**
     * 团控功能反馈
     */
    @Subscriber(mode = ThreadMode.ASYNC, tag = "yjgkteam")
    public void yjgkDataEvent1(YjspTuanEvent dataEvent) {
        Message obtain = Message.obtain();
        String s = dataEvent.yjspState;
        if(s.equals("1")){
            sp.edit().putBoolean("team"+dataEvent.childuuid,true).commit();
        }else {
            sp.edit().putBoolean("team"+dataEvent.childuuid,false).commit();
        }
        LogUtil.e(TAG, "团控功能反馈:yjgkDataEvent"+s);
        obtain.obj = dataEvent.yjspState;
        obtain.what = 206;
        handler.sendMessage(obtain);
    }

    /**
     * 处理一键管控反馈
     * */
    private void ordeyjgk(Message msg) {
        if(pd!=null&&pd.isShowing()){
            pd.dismiss();
        }
        ChildContrlFlagDao childDao = GreenDaoManager.getInstance().getSession().getChildContrlFlagDao();
        ChildContrlFlag findChild = childDao.queryBuilder().where(ChildContrlFlagDao.Properties.Childuuid.eq(childuuid)).build().unique();
        LogUtil.e("ordeyjgk","处理一键管控反馈:"+yjgk);
        if (findChild != null) {
            if (yjgk.equals(CmdCommon.CMD_FLAG_CLOSE)) {
                ToastUtils.showToast(MyApplication.getContext(),"一键管控功能关闭");
                findChild.setYjgkflag(CmdCommon.CMD_FLAG_CLOSE);
//                tv_yjgk.setText("一键管控");
//                if(isOneControll){
//                    iv_controll.setImageResource(R.drawable.vip_1_2);
//                }else {
//                    iv_controll.setImageResource(R.drawable.main_no);
//                }
                sp.edit().putBoolean("yjgk"+childuuid,false).commit();
            } else {
                ToastUtils.showToast(MyApplication.getContext(), "一键管控功能开启");
                findChild.setYjgkflag(CmdCommon.CMD_FLAG_OPEN);
//                tv_yjgk.setText("一键管控(已开启)");
//                sp.edit().putBoolean("yjgk" + childuuid, true).commit();
//                if (isOneControll) {
//                    iv_controll.setImageResource(R.drawable.vip_1_1);
//                } else {
//                    iv_controll.setImageResource(R.drawable.main_close);
//                }
            }
            childDao.update(findChild);
        } else {
            ChildContrlFlag childFlag = new ChildContrlFlag(null, childuuid, parentUUID, "", ""
                    , CmdCommon.CMD_FLAG_CLOSE, CmdCommon.CMD_FLAG_CLOSE
                    , CmdCommon.CMD_FLAG_CLOSE, "30", "5", CmdCommon.CMD_FLAG_CLOSE, CmdCommon.CMD_FLAG_CLOSE);
            childDao.insert(childFlag);
            ToastUtils.showToast(MyApplication.getContext(),"一键管控功能关闭");
            sp.edit().putBoolean("yjgk"+childuuid,false).commit();
//            tv_yjgk.setText("一键管控");
//            if(isOneControll){
//                iv_controll.setImageResource(R.drawable.vip_1_2);
//            }else {
//                iv_controll.setImageResource(R.drawable.main_no);
//            }
        }
    }

    /**
     * 增加士兵信息数据
     */
    @Subscriber(mode = ThreadMode.ASYNC, tag = "ChildMessageData")
    public void ChildMessageDataEvent(ChildMessageDataEvent dataEvent) {
        Message obtain = Message.obtain();
        LogUtil.e(TAG, "增加士兵信息数据:ChildMessageDataEvent");
        obtain.obj = dataEvent;
        obtain.what = 201;
        handler.sendMessage(obtain);
    }

    /**
     * 处理上线下线数据
     */
    @Subscriber(mode = ThreadMode.ASYNC, tag = "OnOfflineData")
    public void OnlineOfflineDataEvent(ChildOnlineAndOfflineEvent dataEvent) {
        Message obtain = Message.obtain();
        LogUtil.e(TAG, "处理上线下线数据:OnlineOfflineDataEvent");
        ChildContrlFlagDao flagDao = GreenDaoManager.getInstance().getSession().getChildContrlFlagDao();
        List<ChildContrlFlag> mlist = flagDao.queryBuilder().where(ChildContrlFlagDao.Properties.Parentuuid.eq(parentUUID)).build().list();
        if(mlist.size()==0){
            LogUtil.e("orderChildLineData","暂无士兵");
            return;
        }
        obtain.obj = dataEvent;
        obtain.what = 202;
        handler.sendMessage(obtain);
    }

    /**
     * 处理所有士兵的在线状态
     */
    @Subscriber(mode = ThreadMode.ASYNC, tag = "childLineData")
    public void ChildLineDataEvent(ChildLineFlagEvent dataEvent) {
        Message obtain = Message.obtain();
        LogUtil.e(TAG, "处理所有士兵的在线状态:ChildLineDataEvent");
        obtain.obj = dataEvent;
        obtain.what = 203;
        handler.sendMessage(obtain);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.e("InternetFragment","onDestroy111");
        EventBus.getDefault().unregister(this);
        getActivity().unregisterReceiver(meBroadcastReceiver);
        ShareSDK.stopSDK(getActivity());
    }

    /**
     * 分享的设置
     */
    private void showShare() {
        ShareSDK.initSDK(getActivity());
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间等使用
        oks.setTitle("优成长-士兵的健康成长的守护");
        // titleUrl是标题的网络链接，QQ和QQ空间等使用
        oks.setTitleUrl("http://www.ycz365.com/");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("我正在使用优成长，赶紧跟我一起来体验！");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片login_iocn
        oks.setImageUrl("http://www.ycz365.com/login_icon.png");
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://www.ycz365.com/");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        //oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://www.ycz365.com/");
        oks.setCallback(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                LogUtil.e("setCallback","分享成功........................................");
                LogUtil.e("setCallback","分享成功........................................");
                LogUtil.e("setCallback","分享成功........................................");
                sp.edit().putString("shareApp","shareApp").commit();
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                LogUtil.e("setCallback","分享失败........................................");
                LogUtil.e("setCallback","分享失败........................................");
                LogUtil.e("setCallback","分享失败........................................");
            }

            @Override
            public void onCancel(Platform platform, int i) {
                LogUtil.e("setCallback","分享取消........................................");
                LogUtil.e("setCallback","分享取消........................................");
                LogUtil.e("setCallback","分享取消........................................");
            }

        });
        // 启动分享GUI
        oks.show(getActivity());
    }

    /**
     * 刷新界面数据
     */
    @Subscriber(mode = ThreadMode.ASYNC, tag = "InitRefreshData")
    public void initRefreshDataEvent(InitRefreshEvent dataEvent) {
        Message obtain = Message.obtain();
        LogUtil.e(TAG, "刷新界面数据:initRefreshDataEvent");
        obtain.what = 205;
        handler.sendMessage(obtain);
    }
    /**
     * 处理刷新界面数据
     * */
    private void orderInit() {
        yjgkFlag=false;
        yjgk="";
        childuuid = sp.getString("ChildUUID", "");
        parentUUID = sp.getString("parentUUID", "");
        childName = sp.getString("childName", "");
        childSex = sp.getString("childSex", "");
        childLineState = sp.getString(childuuid + "online", "");

        handler.removeCallbacks(runnable2);
        ToastUtils.showToast(MyApplication.getContext(),"刷新完成");
        mSwipeRefreshLayout.setRefreshing(false);

        LogUtil.e(TAG, "ChildUUID:" + childuuid + ",parentUUID:" + parentUUID + ",childName:" + childName + ",childSex:" + childSex+",childLineState"+childLineState);
        boolean childFlag1 = sp.getBoolean("childFlag1", false);
        boolean childFlag2 = sp.getBoolean("childFlag2", false);

        boolean yjgk1 = sp.getBoolean("yjgk"+childuuid, false);
        boolean yjgk2 = sp.getBoolean("team"+childuuid, false);
        if (yjgk1) {
//            tv_yjgk.setText("一键管控(已开启)");
//            if(isOneControll){
//                iv_controll.setImageResource(R.drawable.vip_1_1);
//            }else {
//                iv_controll.setImageResource(R.drawable.main_close);
//            }
//        } else {
//            tv_yjgk.setText("一键管控");
//            if(isOneControll){
//                iv_controll.setImageResource(R.drawable.vip_1_2);
//            }else {
//                iv_controll.setImageResource(R.drawable.main_no);
//            }
        }
        if(sp.getBoolean("team"+childuuid,false)){
//            tv_yjgk.setText("一键管控(团控)");
//            if(isOneControll){
//                iv_controll.setImageResource(R.drawable.vip_1_1);
//            }else {
//                iv_controll.setImageResource(R.drawable.main_close);
//            }
        }

        //判断男孩还是女孩
        if(childSex.equals("1")){
            Glide.with(getActivity()).load(headurl).error(R.drawable.child_detail_icon_boy).into(ivMainIcon);
        }else  if(childSex.equals("0")){
            Glide.with(getActivity()).load(headurl).error(R.drawable.child_detail_icon_girl).into(ivMainIcon);
        }else {
            Glide.with(getActivity()).load(headurl).error(R.drawable.child_detail_icon).into(ivMainIcon);
        }

    }
    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     */
    public void backgroundAlpha(Activity context, float bgAlpha) {
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.alpha = bgAlpha;
        context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        context.getWindow().setAttributes(lp);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1003){
            backgroundAlpha(getActivity(), 1f);
        }
        onResume();
    }

    public class MeBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("touxiang".equals(intent.getAction())) {
                childuuid = sp.getString("ChildUUID", "");
                parentUUID = sp.getString("parentUUID", "");
                childName = sp.getString("childName", "");
                childSex = sp.getString("childSex", "");
                childLineState = sp.getString(childuuid + "online", "");
                if(TextUtils.isEmpty(childSex)){
                    Glide.with(getActivity()).load(headurl).error(R.drawable.mainpager_icon).into(ivMainIcon);
                }
                //判断男孩还是女孩
                if(childSex.equals("1")){
                    Glide.with(getActivity()).load(headurl).error(R.drawable.child_detail_icon_boy).into(ivMainIcon);
                }else  if(childSex.equals("0")){
                    Glide.with(getActivity()).load(headurl).error(R.drawable.child_detail_icon_girl).into(ivMainIcon);
                }else {
                    Glide.with(getActivity()).load(headurl).error(R.drawable.child_detail_icon).into(ivMainIcon);
                }
            }
        }
    }

    private void initNewBarDatas(TeamRoot teamRoot,int type) {
        final List<Float> ydatas = new ArrayList<>();
        final List<String> description = new ArrayList<>();
        float a=0f;
        float b=0f;
        float c=0f;
        tv_num.setText(teamRoot.getResult().size()+"");
        if(type==1) {
            for (int i = 0; i < teamRoot.getResult().size(); i++) {
                if (teamRoot.getResult().get(i).getState().equals("1")) {  //团控
                    a++;
                } else if (teamRoot.getResult().get(i).getState().equals("2")) {  //不受控
                    c++;
                }else if (TextUtils.isEmpty(teamRoot.getResult().get(i).getState())) {  //不受控
                    b++;
                }
                else {
                    b++;   //托管
                }
            }
        }
        ydatas.add(c);
        ydatas.add(a);
        ydatas.add(b);

        description.add("可能已脱管");
        description.add("受控");
        description.add("不受控");
        MPChartHelper.setBarChart(LBarChartView,description,ydatas,"标题",13,null);
    }
    /**
     * 获得团控的数据
     */
    private TeamRoot teamRoot;
    private void getControllChilid() {
        String parentUUID1 = sp.getString("parentUUID", "");
        String url = Constants.FIND_URL+"Ardpolice/public/index.php/GetSoldierData/"+parentUUID1+".php";
        Log.e("-------------url2",url);
        StringRequest stringRequest1 = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("-------------response",response);
                        teamRoot = new Gson().fromJson(response, TeamRoot.class);
                        if(teamRoot.getCode().equals("201")){
                            Toast.makeText(getActivity(), "参数不完整", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(teamRoot.getCode().equals("202")){   //没有绑定小孩
                            initNewBarDatas(teamRoot,0);
                            return;
                        }
                        initNewBarDatas(teamRoot,1); //有绑定小孩
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        stringRequest1.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(getActivity()).add(stringRequest1);
    }

}

