package com.hzkc.parent.fragment;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.hzkc.parent.Bean.VipRootBean;
import com.hzkc.parent.MainActivity;
import com.hzkc.parent.R;
import com.hzkc.parent.activity.AppManageActivity;
import com.hzkc.parent.activity.AppManageLastActivity;
import com.hzkc.parent.activity.ChildDetailActivity;
import com.hzkc.parent.activity.ChildLocationActivity;
import com.hzkc.parent.activity.ChildStateActivity;
import com.hzkc.parent.activity.EyeProtectActivity;
import com.hzkc.parent.activity.LoveTrailActivity;
import com.hzkc.parent.activity.MoreFunctionActivity;
import com.hzkc.parent.activity.MyMemeberActivity;
import com.hzkc.parent.activity.NetControlNewActivity;
import com.hzkc.parent.activity.ParentZxingActivity;
import com.hzkc.parent.activity.StopInternetPlanActivity;
import com.hzkc.parent.appcation.MyApplication;
import com.hzkc.parent.event.AppUseDataEvent;
import com.hzkc.parent.event.ChildLineFlagEvent;
import com.hzkc.parent.event.ChildMessageDataEvent;
import com.hzkc.parent.event.ChildOnlineAndOfflineEvent;
import com.hzkc.parent.event.InitEvent;
import com.hzkc.parent.event.InitRefreshEvent;
import com.hzkc.parent.event.RequestAppUseTimeEvent;
import com.hzkc.parent.event.RequestChildAppListEvent;
import com.hzkc.parent.event.YjgkDataEvent;
import com.hzkc.parent.event.YjgkEvent;
import com.hzkc.parent.event.YjspTuanEvent;
import com.hzkc.parent.greendao.GreenDaoManager;
import com.hzkc.parent.greendao.entity.AppDataBean;
import com.hzkc.parent.greendao.entity.AppUseBean;
import com.hzkc.parent.greendao.entity.ChildContrlFlag;
import com.hzkc.parent.greendao.gen.AppDataBeanDao;
import com.hzkc.parent.greendao.gen.AppUseBeanDao;
import com.hzkc.parent.greendao.gen.ChildContrlFlagDao;
import com.hzkc.parent.jsondata.AppUsageData;
import com.hzkc.parent.jsondata.CmdCommon;
import com.hzkc.parent.mina.Constants;
import com.hzkc.parent.utils.AppUtils;
import com.hzkc.parent.utils.LogUtil;
import com.hzkc.parent.utils.NetworkUtil;
import com.hzkc.parent.utils.ToastUtils;
import com.hzkc.parent.view.AffirmDialog;
import com.hzkc.parent.view.BatteryView;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;


/**
 * 成长守护界面fragment
 * Created by lenovo-s on 2016/10/20.
 */

public class InternetFragment11 extends BaseFragment implements View.OnClickListener {
    private static final String TAG = "InternetFragment";
    private View v;
    private ImageView tvMainHzzl;
    private ImageView tvMainXzhz;
    private TextView tvMainHzzt;
    private ImageView ivMainHzdw;
    private ImageView ivMainIcon;
    private ImageView tvMainTjhz;
    private TextView tv_name;
    private TextView tv_state;
    private TextView tvAddChlid;
    private TextView tvFinish;
    private ImageView ivAddFinish;
    private AlertDialog showStartTime;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView tv_yjgk;
    private String childuuid;
    private String childName;
    private boolean yjgkFlag;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 301://处理刷新界面数据
                    List<AppUsageData> obj = (List<AppUsageData>)msg.obj;
                    getdatas(obj);
                    break;
                case 302://处理刷新界面数据
                   getDataFromDb();
                    break;
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
    private RelativeLayout iv_shili,iv_gksz,iv_ydgj,iv_swgl,iv_ycs,iv_wb,iv_qqhm,iv_dhgm;
    private ImageView  iv_slbh_vip, iv_gkjh_vip, iv_yygl_vip, iv_ydgj_vip, iv_wzgl_vip, iv_qqhm_vip, iv_dhgl_vip,iv_controll;
    private ImageView  iv_vip;
    private TextView  tv_vip,tv_app_number;
    public boolean  isVip=false;
    public String   viplist ;
    public boolean   isOneControll=false ;
    private AffirmDialog affirmDialog;
    private BatteryView iv_butter;
    private LinearLayout viewgroup;
    private int vipflag=1;//默认每次进来调用一次vip接口
    @Override
    public View initView() {
        v = View.inflate(getActivity(), R.layout.fragment_internet_new, null);
        return v;
    }
    /**
     * 初始化页面数据
     */
    @Override
    public void iniData() {
        tvMainHzzl = (ImageView) v.findViewById(R.id.tv_main_hzzl);
        tvMainXzhz = (ImageView) v.findViewById(R.id.tv_main_xzhz);
        tvMainHzzt = (TextView) v.findViewById(R.id.tv_main_hzzt);
        ivMainHzdw = (ImageView) v.findViewById(R.id.iv_main_hzdw);
        ivMainIcon = (ImageView) v.findViewById(R.id.iv_main_icon);
        tvMainTjhz = (ImageView) v.findViewById(R.id.tv_main_tjhz);
        iv_butter = (BatteryView) v.findViewById(R.id.iv_butter);
        tv_state = (TextView) v.findViewById(R.id.tv_state);
        tv_app_number = (TextView) v.findViewById(R.id.tv_app_number);
        iv_controll = (ImageView) v.findViewById(R.id.iv_controll);
        tv_yjgk = (TextView) v.findViewById(R.id.tv_yjgk);
        iv_shili = (RelativeLayout) v.findViewById(R.id.iv_shili);
        iv_gksz = (RelativeLayout) v.findViewById(R.id.iv_gksz);
        iv_ydgj = (RelativeLayout) v.findViewById(R.id.iv_ydgj);
        iv_swgl = (RelativeLayout) v.findViewById(R.id.iv_swgl);
        iv_ycs = (RelativeLayout) v.findViewById(R.id.iv_ycs);
        iv_wb = (RelativeLayout) v.findViewById(R.id.iv_wb);
        iv_qqhm = (RelativeLayout) v.findViewById(R.id.iv_qqhm);
        iv_dhgm = (RelativeLayout) v.findViewById(R.id.iv_dhgm);
        viewgroup = (LinearLayout) v.findViewById(R.id.viewgroup);

        tv_name= (TextView) v.findViewById(R.id.tv_name);
        tv_vip = (TextView) v.findViewById(R.id.tv_vip);
        iv_vip= (ImageView) v.findViewById(R.id.iv_vip);
        iv_slbh_vip = (ImageView) v.findViewById(R.id.iv_slbh_vip);
        iv_gkjh_vip = (ImageView) v.findViewById(R.id.iv_gksz_vip);
        iv_yygl_vip = (ImageView) v.findViewById(R.id.iv_yygl_vip);
        iv_ydgj_vip = (ImageView) v.findViewById(R.id.iv_ydgj_vip);
        iv_wzgl_vip = (ImageView) v.findViewById(R.id.iv_wzgl_vip);
        iv_qqhm_vip = (ImageView) v.findViewById(R.id.iv_qqhm_vip);
        iv_dhgl_vip = (ImageView) v.findViewById(R.id.iv_dhgm_vip);
        iv_butter.setPower(80);

        viplist = sp.getString("viplist", "");
        isVip = sp.getBoolean("isVip", false);
        if(!TextUtils.isEmpty(viplist)){
            String[] sourceStrArray = viplist.split(",");
            for (int i = 0; i < sourceStrArray.length; i++) {
                if(sourceStrArray[i].equals(Constants.VIP_ONE_CONTROLL)){
                    isOneControll=true;
                    if(tv_yjgk.getText().toString().equals("一键管控")){
                        iv_controll.setImageResource(R.drawable.vip_1_2);
                    }else {
                        iv_controll.setImageResource(R.drawable.vip_1_1);
                    }
                    break;
                }else {
                    isOneControll=false;
                    if(tv_yjgk.getText().toString().equals("一键管控")){
                        iv_controll.setImageResource(R.drawable.sy_off);
                    }else {
                        iv_controll.setImageResource(R.drawable.sy_on);
                    }
                }
            }
            if (viplist.contains(Constants.VIP_EYE_PROTECT)){
                iv_slbh_vip.setVisibility(View.VISIBLE);
            }
            if (viplist.contains(Constants.VIP_CONTROLL_PLAN)){
                iv_gkjh_vip.setVisibility(View.VISIBLE);
            }
            if (viplist.contains(Constants.VIP_SPORY_LINE)){
                iv_ydgj_vip.setVisibility(View.VISIBLE);
            }
            if (viplist.contains(Constants.VIP_APP_MANAGE)) {
                iv_yygl_vip.setVisibility(View.VISIBLE);
            }
        }
        iv_controll.setOnClickListener(this);
        tv_yjgk.setOnClickListener(this);
        iv_shili.setOnClickListener(this);
        iv_gksz.setOnClickListener(this);
        iv_ydgj.setOnClickListener(this);
        iv_swgl.setOnClickListener(this);
        iv_ycs.setOnClickListener(this);
        iv_wb.setOnClickListener(this);
        iv_qqhm.setOnClickListener(this);
        iv_dhgm.setOnClickListener(this);
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.sf_refresh);

//        Toast.makeText(getActivity(),  MyApplication.getChannelName(getActivity()), Toast.LENGTH_SHORT).show();
        yjgkFlag=false;
        yjgk="";

        tvMainHzzl.setOnClickListener(this);
        tvMainXzhz.setOnClickListener(this);
        tvMainHzzt.setOnClickListener(this);
        ivMainHzdw.setOnClickListener(this);
        ivMainIcon.setOnClickListener(this);
        tvMainTjhz.setOnClickListener(this);
        tv_vip.setOnClickListener(this);
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
                handler.postDelayed(runnable2, 5000);
            }
        });
        getVipdata();//获取vip信息
        EventBus.getDefault().post(new RequestAppUseTimeEvent(childuuid),"requestAppUseTime");
    }

    /**
     * 获取vip信息
     */
    private void getVipdata() {
        showLoading();
        final String userid= sp.getString("parentUUID", "");
        String url=Constants.PHP_URL+"v"+ AppUtils.getVerName()+"/vippage";;
        Log.e("vip模块", "url:" + url);
        StringRequest postsr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("vip模块", "vip模块:" + response);
                dissloading();
//                VipRootBean vipRoot = new Gson().fromJson(response, VipRootBean.class);
                VipRootBean vipRoot =null;
                try{
                    vipRoot = new Gson().fromJson(response, VipRootBean.class);
                }catch (Exception e){
                    tv_vip.setText("立即开通会员");
                    iv_vip.setImageResource(R.drawable.sy_vip_off);
                    return;
                }
                ((MainActivity)getActivity()).setVipRoot(vipRoot);
                frashView(vipRoot);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                dissloading();
                ToastUtils.showToast(MyApplication.getContext(), "加载数据失败");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //创建一个集合，放的是keyvalue的key是参数名与value是参数值
                Map<String, String> map = new HashMap<String, String>();
                map.put("user_id", userid);
                map.put("goods_src", "Android");
                return map;
            }
        };
        postsr.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.getRequestQueue().add(postsr);
    }

    private void frashView(VipRootBean vipRoot) {
        if(vipRoot.getData().getVipinfo().getViplvl().equals("0")){
            tv_vip.setText("立即开通会员");
            iv_vip.setImageResource(R.drawable.sy_vip_off);
        }else if(vipRoot.getData().getVipinfo().getViplvl().equals("1")){
            String vipendtime = vipRoot.getData().getVipinfo().getVipendtime();
            tv_vip.setText(vipendtime+"到期");
            iv_vip.setImageResource(R.drawable.sy_vip_on);
        } else if(vipRoot.getData().getVipinfo().getViplvl().equals("2")){
            String vipendtime = vipRoot.getData().getVipinfo().getVipendtime();
            tv_vip.setText("vip试用"+vipendtime+"到期");
            iv_vip.setImageResource(R.drawable.sy_vip_off);
        }
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
        if(TextUtils.isEmpty(childName)){
            tv_name.setVisibility(View.INVISIBLE);
        }else {
            tv_name.setText(childName);
            tv_name.setVisibility(View.VISIBLE);
        }
        if(childSex.equals("1")){
            Glide.with(getActivity()).load(headurl).error(R.drawable.child_detail_icon_boy).into(ivMainIcon);
        }else  if(childSex.equals("0")){
            Glide.with(getActivity()).load(headurl).error(R.drawable.child_detail_icon_girl).into(ivMainIcon);
        }else {
            Glide.with(getActivity()).load(headurl).error(R.drawable.child_detail_icon).into(ivMainIcon);
        }

    }
    /**
     * 显示添加孩子对话框
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
                Intent intent = new Intent(getActivity(), ParentZxingActivity.class);
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
     * 添加孩子的点击事键
     */
    @Override
    public void onClick(View v) {
        boolean isNet = NetworkUtil.isConnected();
        if (!isNet) {
            ToastUtils.showToast(MyApplication.getContext(),"网络不通，请检查网络再试");
            return;
        }
        switch (v.getId()) {
//            case R.id.iv_main_grzx://个人中心
//                if (TextUtils.isEmpty(childuuid)) {
//                    showDialog();
//                    return;
//                }
//                Toast.makeText(getActivity(), "头像要不要", Toast.LENGTH_SHORT).show();
//                break;
            case R.id.tv_vip:
                if(tv_vip.getText().equals("立即开通")){
                    Intent intent = new Intent(getActivity(), MyMemeberActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.iv_main_hzdw://孩子地理位置
                if (TextUtils.isEmpty(childuuid)) {
                    showDialog();
                    return;
                }
                hzdlwz(isNet);
                break;

            case R.id.tv_main_hzzl://孩子资料
                if (TextUtils.isEmpty(childuuid)) {
                    showDialog();
                    return;
                }
                Intent intent = new Intent(getActivity(), ChildDetailActivity.class);
                intent.putExtra("ChildUUID",childuuid);
                intent.putExtra("childFrom",childFrom);
                startActivity(intent);
                break;
            case R.id.tv_main_xzhz://选择孩子
                if (TextUtils.isEmpty(childuuid)) {
                    showDialog();
                    return;
                }
                if (!TextUtils.isEmpty(childuuid)) {
//                    Intent intent2=new Intent(getActivity(), ChooseActivity.class);
//                    intent2.putExtra("childUUid",childuuid);
//                    backgroundAlpha(getActivity(), 0.5f);
//                    startActivityForResult(intent2,1003);
                    MainActivity.open();
                } else {
                    showDialog();
                    return;
                }
                break;
            case R.id.tv_main_hzzt://孩子状态
                if (TextUtils.isEmpty(childuuid)) {
                    showDialog();
                    return;
                }
                Intent intent1=new Intent(getActivity(), ChildStateActivity.class);
                intent1.putExtra("ChildUUID",childuuid);
                startActivity(intent1);
                break;
            case R.id.tv_main_tjhz://添加孩子
                tjhz();
                break;
            case R.id.iv_controll:
                //如果是团控
                if (TextUtils.isEmpty(childuuid)) {
                    showDialog();
                    return;
                }
                if(isOneControll){
                    if(!isVip){
                        if (affirmDialog != null) {
                            affirmDialog.show();
                        }
                        return;
                    }
                }
                if(sp.getBoolean("team"+childuuid,false)){
                    Toast.makeText(getActivity(), "孩子正处于团控状态，请稍后再试", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    yjgk();
                    handler.postDelayed(runnable, 6000);
                }
                break;
            case R.id.iv_shili://private ImageView iv_controll,iv_shili,iv_gksz,iv_ydgj,iv_swgl,iv_ycs;
                isVip = sp.getBoolean("isVip", false);
                if(viplist.contains(Constants.VIP_EYE_PROTECT)){
                    if(!isVip){
                        if (affirmDialog != null) {
                            affirmDialog.show();
                        }
                        return;
                    }
                }
                if (TextUtils.isEmpty(childuuid)) {
                    showDialog();
                    return;
                }
                //如果是团控
                if(sp.getBoolean("team"+childuuid,false)){
                    Toast.makeText(getActivity(), "孩子正处于团控状态，请稍后再试", Toast.LENGTH_SHORT).show();
                }else {
                    Intent intent11 = new Intent(getActivity(), EyeProtectActivity.class);
                    intent11.putExtra("ChildUUID", childuuid);
                    startActivity(intent11);
                }
                break;

            case R.id.iv_wb://网址黑白名单
                isVip = sp.getBoolean("isVip", false);
                if(viplist.contains(Constants.VIP_WEB_CHOOSE)){
                    if(!isVip){
                        if (affirmDialog != null) {
                            affirmDialog.show();
                        }
                        return;
                    }
                }
                if(sp.getBoolean("team"+childuuid,false)){
                    Toast.makeText(getActivity(), "孩子正处于团控状态，请稍后再试", Toast.LENGTH_SHORT).show();
                }else {
                    Intent intent21 = new Intent(getActivity(), NetControlNewActivity.class);
                    intent21.putExtra("ChildUUID", childuuid);
                    startActivity(intent21);
                }
                break;
            case R.id.iv_gksz:
                isVip = sp.getBoolean("isVip", false);
                if(viplist.contains(Constants.VIP_CONTROLL_PLAN)){
                    if(!isVip){
                        if (affirmDialog != null) {
                            affirmDialog.show();
                        }
                        return;
                    }
                }
                if (TextUtils.isEmpty(childuuid)) {
                    showDialog();
                    return;
                }
                //如果是团控
                if(sp.getBoolean("team"+childuuid,false)){
                    Toast.makeText(getActivity(), "孩子正处于团控状态，请稍后再试", Toast.LENGTH_SHORT).show();
                }else {
                    Intent intent21 = new Intent(getActivity(), StopInternetPlanActivity.class);
                    intent21.putExtra("ChildUUID", childuuid);
                    startActivity(intent21);
                }
                break;
            case R.id.iv_ydgj:
                isVip = sp.getBoolean("isVip", false);
                if(viplist.contains(Constants.VIP_SPORY_LINE)){
                    if(!isVip){
                        if (affirmDialog != null) {
                            affirmDialog.show();
                        }
                        return;
                    }


                }
                if (TextUtils.isEmpty(childuuid)) {
                    showDialog();
                    return;
                }
                //如果是团控
                if(sp.getBoolean("team"+childuuid,false)){
                    Toast.makeText(getActivity(), "孩子正处于团控状态，请稍后再试", Toast.LENGTH_SHORT).show();
                }else {
                    Intent intent3 = new Intent(getActivity(), LoveTrailActivity.class);
//                Intent intent3 = new Intent(getActivity(), LocationHistoryActivity.class);
                    intent3.putExtra("ChildUUID", childuuid);
                    startActivity(intent3);
                }
                break;
            case R.id.iv_swgl:     //应用白名单
                isVip = sp.getBoolean("isVip", false);
                if(viplist.contains(Constants.VIP_APP_MANAGE)){
                    if(!isVip){
                        if (affirmDialog != null) {
                            affirmDialog.show();
                        }
                        return;
                    }
                }
                if (TextUtils.isEmpty(childuuid)) {
                    showDialog();
                    return;
                }
                //如果是团控
                if(sp.getBoolean("team"+childuuid,false)){
                    Toast.makeText(getActivity(), "孩子正处于团控状态，请稍后再试", Toast.LENGTH_SHORT).show();
                }else {
                    if(childFrom.equals("ycz")){
                        Intent intent4 = new Intent(getActivity(), AppManageActivity.class);
//                        Intent intent4 = new Intent(getActivity(), AppManageLastActivity.class);
                        intent4.putExtra("ChildUUID", childuuid);
                        startActivity(intent4);
                    }else {
                        Intent intent4 = new Intent(getActivity(), AppManageLastActivity.class);
                        intent4.putExtra("ChildUUID", childuuid);
                        startActivity(intent4);
                    }
                }
                break;
            case R.id.iv_ycs://更多功能
                if (TextUtils.isEmpty(childuuid)) {
                    showDialog();
                    return;
                }
                //如果是团控
                if(sp.getBoolean("team"+childuuid,false)){
                    Toast.makeText(getActivity(), "孩子正处于团控状态，请稍后再试", Toast.LENGTH_SHORT).show();
                }else {
                    Intent intent6 = new Intent(getActivity(), MoreFunctionActivity.class);
                    intent6.putExtra("ChildUUID", childuuid);
                    intent6.putExtra("childFrom", childFrom);
                    startActivity(intent6);
                }
                break;
            case R.id.affirm_cancel:
                if (affirmDialog != null) {
                    affirmDialog.hide();
                }
                break;
            case R.id.affirm_confirm:
                if (affirmDialog != null) {
                    affirmDialog.hide();
                }
                Intent intents = new Intent(getActivity(), MyMemeberActivity.class);
                startActivity(intents);
                break;
            default:
                break;
        }
    }



    /**
     * 孩子地理位置
     * */
    private void hzdlwz(boolean isNet) {
        if (!isNet) {
            ToastUtils.showToast(MyApplication.getContext(), "网络不通，请检查网络再试");
            return;
        }
        if (!TextUtils.isEmpty(childuuid)) {
            childLineState = sp.getString(childuuid + "online", "");
            if (childLineState.equals(CmdCommon.CMD_OFFLINE)) {//下线
                ToastUtils.showToast(MyApplication.getContext(), "孩子处于离线状态,无法获取孩子当前位置");
                return;
            }
            Intent intent = new Intent(getActivity(), ChildLocationActivity.class);
            intent.putExtra("ChildUUID", childuuid);
            startActivity(intent);
        } else {
            showDialog();
            return;
        }
    }


    /**
     * 孩子头像
     * */
    private void hztx(boolean isNet) {
        if (!isNet) {
            ToastUtils.showToast(MyApplication.getContext(),"网络不通，请检查网络再试");
            return;
        }
        if (!TextUtils.isEmpty(childuuid)) {
            Intent intent = new Intent(getActivity(), ChildDetailActivity.class);
            intent.putExtra("ChildUUID", childuuid);
            startActivity(intent);
        } else {
            Intent intent = new Intent(getActivity(), ParentZxingActivity.class);
            //使用uuid生成一个特定的字符串
            intent.putExtra("parentZxing", parentUUID);
            startActivity(intent);
        }
    }

    /**
     * 添加孩子
     * */
    private void tjhz() {
        Intent intent = new Intent(getActivity(), ParentZxingActivity.class);
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
            tv_yjgk.setText("一键管控(已开启)");
            if(isOneControll){
                iv_controll.setImageResource(R.drawable.vip_1_1);
            }else {
                iv_controll.setImageResource(R.drawable.sy_on);
            }
        } else {
            tv_yjgk.setText("一键管控");
            if(isOneControll){
                iv_controll.setImageResource(R.drawable.vip_1_2);
            }else {
                iv_controll.setImageResource(R.drawable.sy_off);
            }
        }
        if(sp.getBoolean("team"+childuuid,false)){
            tv_yjgk.setText("一键管控(团控)");
            if(isOneControll){
                iv_controll.setImageResource(R.drawable.vip_1_1);
            }else {
                iv_controll.setImageResource(R.drawable.sy_on);
            }
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
            getAppList();// //判断applist是否(提前获取app列表保存本地)
        }


    }
    /**
     *  判断applist是否(提前获取app列表保存本地)
     */
    private void getAppList() {
        AppDataBeanDao dao = GreenDaoManager.getInstance().getSession().getAppDataBeanDao();
        List<AppDataBean> mlist = dao.queryBuilder().where(AppDataBeanDao.Properties.Childuuid.eq(childuuid)).build().list();
        Log.e("------------mlist", (mlist==null)+"--------------------"+mlist.size());
        if (mlist==null || mlist.size() == 0) {//没有app数据
            sp.edit().putBoolean("issave",true).commit();
            sp.edit().putBoolean("isapplist",true).commit();    //主动申请才修改appp
            EventBus.getDefault().post(new RequestChildAppListEvent(childuuid, parentUUID), "requestchildApp");
        }
    }

    /**
     * 处理添加孩子数据
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
    private void yjgk() {
        yjgkFlag=false;
        if(pd!=null&&pd.isShowing()){
            pd.dismiss();
        }
        pd = new ProgressDialog(getActivity());
        pd.setMessage("正在执行操作，请稍候");
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setCancelable(false);
        pd.show();
        ChildContrlFlagDao childDao = GreenDaoManager.getInstance().getSession().getChildContrlFlagDao();
        ChildContrlFlag findChild = childDao.queryBuilder().where(ChildContrlFlagDao.Properties.Childuuid.eq(childuuid)).build().unique();
        if (findChild != null) {
            if (findChild.getYjgkflag().equals(CmdCommon.CMD_FLAG_OPEN)) {
                yjgk = CmdCommon.CMD_FLAG_CLOSE;
            } else {
                yjgk = CmdCommon.CMD_FLAG_OPEN;
            }
        } else {
            ChildContrlFlag childFlag = new ChildContrlFlag(null, childuuid, parentUUID, "", ""
                    , CmdCommon.CMD_FLAG_CLOSE, CmdCommon.CMD_FLAG_CLOSE
                    , CmdCommon.CMD_FLAG_CLOSE, "30", "5", CmdCommon.CMD_FLAG_CLOSE, CmdCommon.CMD_FLAG_CLOSE);
            childDao.insert(childFlag);
            yjgk = CmdCommon.CMD_FLAG_CLOSE;
        }
        LogUtil.e(TAG, "一键管控: " + yjgk);
        sp.edit().putBoolean("isyjgk",false).commit();
        EventBus.getDefault().post(new YjgkEvent(parentUUID, childuuid, yjgk), "yjgk");
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!sp.getBoolean("isyjgk",false)){
                    getActivity().finish();
                }
            }
        },2000);

    }

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
                tv_yjgk.setText("一键管控");
                if(isOneControll){
                    iv_controll.setImageResource(R.drawable.vip_1_2);
                }else {
                    iv_controll.setImageResource(R.drawable.sy_off);
                }
                sp.edit().putBoolean("yjgk"+childuuid,false).commit();
            } else {
                ToastUtils.showToast(MyApplication.getContext(), "一键管控功能开启");
                findChild.setYjgkflag(CmdCommon.CMD_FLAG_OPEN);
                tv_yjgk.setText("一键管控(已开启)");
                sp.edit().putBoolean("yjgk" + childuuid, true).commit();
                if (isOneControll) {
                    iv_controll.setImageResource(R.drawable.vip_1_1);
                } else {
                    iv_controll.setImageResource(R.drawable.sy_on);
                }
            }
            childDao.update(findChild);
        } else {
            ChildContrlFlag childFlag = new ChildContrlFlag(null, childuuid, parentUUID, "", ""
                    , CmdCommon.CMD_FLAG_CLOSE, CmdCommon.CMD_FLAG_CLOSE
                    , CmdCommon.CMD_FLAG_CLOSE, "30", "5", CmdCommon.CMD_FLAG_CLOSE, CmdCommon.CMD_FLAG_CLOSE);
            childDao.insert(childFlag);
            ToastUtils.showToast(MyApplication.getContext(),"一键管控功能关闭");
            sp.edit().putBoolean("yjgk"+childuuid,false).commit();
            tv_yjgk.setText("一键管控");
            if(isOneControll){
                iv_controll.setImageResource(R.drawable.vip_1_2);
            }else {
                iv_controll.setImageResource(R.drawable.sy_off);
            }
        }
    }

    /**
     * 增加孩子信息数据
     */
    @Subscriber(mode = ThreadMode.ASYNC, tag = "ChildMessageData1")
    public void ChildMessageDataEvent(ChildMessageDataEvent dataEvent) {
        Message obtain = Message.obtain();
        Log.e(TAG, "增加孩子信息数据:ChildMessageDataEvent");
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
            LogUtil.e("orderChildLineData","暂无孩子");
            return;
        }
        obtain.obj = dataEvent;
        obtain.what = 202;
        handler.sendMessage(obtain);
    }

    /**
     * 处理所有孩子的在线状态
     */
    @Subscriber(mode = ThreadMode.ASYNC, tag = "childLineData")
    public void ChildLineDataEvent(ChildLineFlagEvent dataEvent) {
        Message obtain = Message.obtain();
        LogUtil.e(TAG, "处理所有孩子的在线状态:ChildLineDataEvent");
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
        oks.setTitle("优成长-孩子的健康成长的守护");
        // titleUrl是标题的网络链接，QQ和QQ空间等使用
        oks.setTitleUrl("http://www.ycz365.com/");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("我正在使用优成长，赶紧跟我一起来体验！");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
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
            tv_yjgk.setText("一键管控(已开启)");
            if(isOneControll){
                iv_controll.setImageResource(R.drawable.vip_1_1);
            }else {
                iv_controll.setImageResource(R.drawable.sy_on);
            }
        } else {
            tv_yjgk.setText("一键管控");
            if(isOneControll){
                iv_controll.setImageResource(R.drawable.vip_1_2);
            }else {
                iv_controll.setImageResource(R.drawable.sy_off);
            }
        }
        if(sp.getBoolean("team"+childuuid,false)){
            tv_yjgk.setText("一键管控(团控)");
            if(isOneControll){
                iv_controll.setImageResource(R.drawable.vip_1_1);
            }else {
                iv_controll.setImageResource(R.drawable.sy_on);
            }
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
    /**
     * 处理App使用数据{"a":"2","b":"10004835","c":"447","d":"17","e":"[{\"a\":\"com.gionee.amisystem\",\"b\":\"804247\"},{\"a\":\"com.qihoo.appstore\",\"b\":\"42241\"},{\"a\":\"com.gionee.gnservice\",\"b\":\"1062\"},{\"a\":\"com.tencent.mtt\",\"b\":\"611626\"},{\"a\":\"com.hzkc.parent\",\"b\":\"4087\"}]","f":"5","g":"1529474762000"}
     */
    @Subscriber(mode = ThreadMode.ASYNC, tag = "AppUseTimeData")
    public void AppUseDataEvent(final AppUseDataEvent dataEvent) {
        handler.removeCallbacks(runnable);
        if (dataEvent.datas == null) {
            Message msg = Message.obtain();
            msg.obj=dataEvent.datas ;
            msg.what=302;
            handler.sendMessage(msg);
            return;
        }
        Message msg = Message.obtain();
        msg.obj=dataEvent.datas ;
        msg.what=301;
        handler.sendMessage(msg);
    }
    private void getdatas( List<AppUsageData>  datas) {
        tv_app_number.setText("目前使用了"+datas.size()+"个应用");
        for (int i = 0; i < datas.size(); i++) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_sy_new, null);
            RelativeLayout re1 = (RelativeLayout) view.findViewById(R.id.rv_1);
            RelativeLayout re2 = (RelativeLayout) view.findViewById(R.id.rv_2);
            ImageView iv_head = (ImageView)view.findViewById(R.id.iv_head);
            TextView tv_name = (TextView)view.findViewById(R.id.tv_name);
            TextView tv_time = (TextView)view.findViewById(R.id.tv_time);
            String url= Constants.APP_IMAGE_URL+parentUUID+"/"+childuuid+"/"+datas.get(i).getAppPkgName()+".jpg";
            Glide.with(getActivity()).load(url).placeholder(R.drawable.soye_mrtb).error(R.drawable.soye_mrtb).into(iv_head);

            String s= (Integer.parseInt(datas.get(i).getAppUsage())) / 1000 + "";
            int usetime = Integer.parseInt(s);
            if(usetime/60>0){
                if(usetime/(60*60)>0){
                    tv_time.setText("停留了"+usetime/3600+"小时"+(usetime%3600)/60+"分");
                }else{
                    tv_time.setText("停留了"+usetime/60+"分");
                }
            }else {
                tv_time.setText("停留了"+usetime+"秒");
            }
            if (i == 0) {
                re1.setVisibility(View.VISIBLE);
                re2.setVisibility(View.GONE);
            } else {
                re2.setVisibility(View.VISIBLE);
                re1.setVisibility(View.GONE);
            }
            viewgroup.addView(view);
        }
    }
    /**
     *从本地获取应用追踪数据
     */
    private void getDataFromDb() {
        AppUseBeanDao dao = GreenDaoManager.getInstance().getSession().getAppUseBeanDao();
        List<AppUseBean> list = dao.queryBuilder().where(AppUseBeanDao.Properties.Childuuid.eq(childuuid)).build().list();
        tv_app_number.setText("目前使用了"+list.size()+"个应用");
        if(list==null || list.size()==0){
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            View view= LayoutInflater.from(getActivity()).inflate(R.layout.item_sy_new,null);
            RelativeLayout re1 = (RelativeLayout)view.findViewById(R.id.rv_1);
            RelativeLayout re2 = (RelativeLayout)view.findViewById(R.id.rv_2);
            ImageView iv_head = (ImageView)view.findViewById(R.id.iv_head);
            TextView tv_name = (TextView)view.findViewById(R.id.tv_name);
            TextView tv_time = (TextView)view.findViewById(R.id.tv_time);
            String url= Constants.APP_IMAGE_URL+parentUUID+"/"+childuuid+"/"+list.get(i).getB()+".jpg";
            Glide.with(getActivity()).load(url).placeholder(R.drawable.soye_mrtb).error(R.drawable.soye_mrtb).into(iv_head);
            String s= (Integer.parseInt(list.get(i).getC())) / 1000 + "";
            int usetime = Integer.parseInt(s);
            if(usetime/60>0){
                if(usetime/(60*60)>0){
                    tv_time.setText(usetime/3600+"小时"+(usetime%3600)/60+"分");
                }else{
                    tv_time.setText(usetime/60+"分");
                }
            }else {
                tv_time.setText(usetime+"秒");
            }
            if(i==0){
                re1.setVisibility(View.VISIBLE);
                re2.setVisibility(View.GONE);
            }else {
                re2.setVisibility(View.VISIBLE);
                re1.setVisibility(View.GONE);
            }
            viewgroup.addView(view);
        }
    }
}