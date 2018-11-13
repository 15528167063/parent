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
import android.widget.CheckBox;
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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.hzkc.parent.Bean.VipRootBean;
import com.hzkc.parent.MainActivity;
import com.hzkc.parent.R;
import com.hzkc.parent.activity.AppManageActivity;
import com.hzkc.parent.activity.AppManageLastActivity;
import com.hzkc.parent.activity.AppUseActivity;
import com.hzkc.parent.activity.ChildLocationActivity;
import com.hzkc.parent.activity.ChildNewDetailActivity;
import com.hzkc.parent.activity.ChildStateActivity;
import com.hzkc.parent.activity.EyeProtectActivity;
import com.hzkc.parent.activity.HelpCenterActivity;
import com.hzkc.parent.activity.LoginActivity;
import com.hzkc.parent.activity.LoveTrailActivity;
import com.hzkc.parent.activity.NetControlNewActivity;
import com.hzkc.parent.activity.ParentZxingActivity;
import com.hzkc.parent.activity.PhoneQinQingActivity;
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
import com.hzkc.parent.event.SlbhEvent;
import com.hzkc.parent.event.StopInternetPlanEvent;
import com.hzkc.parent.event.YjgkDataEvent;
import com.hzkc.parent.event.YjgkEvent;
import com.hzkc.parent.event.YjspTuanEvent;
import com.hzkc.parent.greendao.GreenDaoManager;
import com.hzkc.parent.greendao.entity.AppDataBean;
import com.hzkc.parent.greendao.entity.ChildContrlFlag;
import com.hzkc.parent.greendao.entity.NetPlanDataBean;
import com.hzkc.parent.greendao.gen.AppDataBeanDao;
import com.hzkc.parent.greendao.gen.ChildContrlFlagDao;
import com.hzkc.parent.greendao.gen.NetPlanDataBeanDao;
import com.hzkc.parent.jsondata.AppUsageData;
import com.hzkc.parent.jsondata.CmdCommon;
import com.hzkc.parent.mina.Constants;
import com.hzkc.parent.utils.AppUtils;
import com.hzkc.parent.utils.GlideCircleTransform;
import com.hzkc.parent.utils.LogUtil;
import com.hzkc.parent.utils.NetworkUtil;
import com.hzkc.parent.utils.ToastUtils;
import com.hzkc.parent.view.AffirmDialog;
import com.hzkc.parent.view.BatteryView;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.Date;
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

public class InternetFragment extends BaseFragment implements View.OnClickListener {
    private static final String TAG = "InternetFragment";
    private View v;
    private ImageView tvMainHzzl;
    private ImageView tvMainXzhz;
    private TextView tvMainHzzt;
    private ImageView ivMainHzdw;
    private ImageView ivMainIcon,iv_help;
    private ImageView tvMainTjhz;
    private TextView tv_name;
    private TextView tv_state;
    private TextView tv_butter;
    private TextView tv_no_login,tv_no_vip;
    private TextView tvAddChlid;
    private TextView tvFinish;
    private ImageView ivAddFinish;
    private AlertDialog showStartTime;
    private SwipeRefreshLayout mSwipeRefreshLayout;
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
//                    getDataFromDb(false);
                    String power = sp.getString("power" + childuuid, "");
                    if(!TextUtils.isEmpty(power)){
                        iv_butter.setPower(Integer.parseInt(power));
                        tv_butter.setText(power+"%");
                    }

                    viewgroup.removeAllViews();
                    tv_app_number.setText("共使用了"+0+"个应用");
                    isVip = sp.getBoolean("isVip", false);
                    if(viplist.contains(Constants.VIP_APP_ZHIZHONG)){
                        if(!isVip){
                            tv_no_login.setVisibility(View.GONE);
                            tv_no_vip.setVisibility(View.VISIBLE);
                            return;
                        }
                    }
                    tv_no_login.setVisibility(View.VISIBLE);
                    tv_no_login.setText("暂无动态信息");
                    break;
                case 201://处理初始化数据
                    orderAddChildData(msg);
                    break;
                case 204://处理一键管控反馈
                    boolean yjgkclose = sp.getBoolean("yjgkclose", false);
                    if(!yjgkclose){
                        ordeyjgk(msg);
                    }
                    sp.edit().putBoolean("yjgkclose",false).commit();
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
    private String childztl;
    private String childimage;
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
    public boolean  isOneControllVip=false; //判断锁屏图标是不是vip
    public String   viplist ;
    private AffirmDialog affirmDialog;
    private BatteryView iv_butter;
    private LinearLayout viewgroup;
    private RelativeLayout re_dt;
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
        iv_help = (ImageView) v.findViewById(R.id.iv_helps);
        iv_butter = (BatteryView) v.findViewById(R.id.iv_butter);
        tv_state = (TextView) v.findViewById(R.id.tv_state);
        tv_butter = (TextView) v.findViewById(R.id.tv_butter);
        tv_no_login = (TextView) v.findViewById(R.id.tv_no_login);
        tv_no_vip = (TextView) v.findViewById(R.id.tv_no_vip);
        tv_app_number = (TextView) v.findViewById(R.id.tv_app_number);
        iv_controll = (ImageView) v.findViewById(R.id.iv_controll);
        iv_shili = (RelativeLayout) v.findViewById(R.id.iv_shili);
        iv_gksz = (RelativeLayout) v.findViewById(R.id.iv_gksz);
        iv_ydgj = (RelativeLayout) v.findViewById(R.id.iv_ydgj);
        re_dt = (RelativeLayout) v.findViewById(R.id.re_dt);
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


        viplist = sp.getString("viplist", "");
        isVip = sp.getBoolean("isVip", false);
        if(!TextUtils.isEmpty(viplist)) {
            String[] sourceStrArray = viplist.split(",");
            for (int i = 0; i < sourceStrArray.length; i++) {
                if (sourceStrArray[i].equals(Constants.VIP_ONE_CONTROLL)) {
                    isOneControllVip=true;
                }
            }
        }
        if(!TextUtils.isEmpty(viplist)){
            if (viplist.contains(Constants.VIP_EYE_PROTECT)){
                iv_slbh_vip.setImageResource(R.drawable.soye_eye_vip);
            }
            if (viplist.contains(Constants.VIP_CONTROLL_PLAN)){
                iv_gkjh_vip.setImageResource(R.drawable.soye_plan_vip);
            }
            if (viplist.contains(Constants.VIP_SPORY_LINE)){
                iv_ydgj_vip.setImageResource(R.drawable.soye_ydgj_vip);
            }
            if (viplist.contains(Constants.VIP_APP_MANAGE)) {
                iv_yygl_vip.setImageResource(R.drawable.soye_yygl_vip);
            }
            if (viplist.contains(Constants.VIP_APP_QQHM)) {
                iv_wzgl_vip.setImageResource(R.drawable.soye_wzgl_vip);
            }
            if (viplist.contains(Constants.VIP_WEB_CHOOSE)) {
                iv_qqhm_vip.setImageResource(R.drawable.soye_qqhm_vip);
            }
            if (viplist.contains(Constants.VIP_APP_ZHIZHONG)) {
                iv_dhgl_vip.setImageResource(R.drawable.soye_yytj_vip);
            }
        }
        iv_controll.setOnClickListener(this);
        iv_shili.setOnClickListener(this);
        iv_gksz.setOnClickListener(this);
        iv_ydgj.setOnClickListener(this);
        iv_swgl.setOnClickListener(this);
        iv_ycs.setOnClickListener(this);
        iv_wb.setOnClickListener(this);
        iv_qqhm.setOnClickListener(this);
        iv_dhgm.setOnClickListener(this);
        iv_help.setOnClickListener(this);
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.sf_refresh);
        yjgkFlag=false;
        yjgk="";
        tvMainHzzl.setOnClickListener(this);
        tvMainXzhz.setOnClickListener(this);
        tvMainHzzt.setOnClickListener(this);
        ivMainHzdw.setOnClickListener(this);
        ivMainIcon.setOnClickListener(this);
        tvMainTjhz.setOnClickListener(this);
        tv_vip.setOnClickListener(this);
        iv_vip.setOnClickListener(this);
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
                if(TextUtils.isEmpty(sp.getString("parentUUID", ""))){
                    mSwipeRefreshLayout.setRefreshing(true);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mSwipeRefreshLayout.setRefreshing(false);
                            Toast.makeText(getActivity(), "您还未登录，无法刷新数据", Toast.LENGTH_SHORT).show();
                        }
                    },1000);
                    return;
                }
                mSwipeRefreshLayout.setRefreshing(true);
                Log.d("Swipe", "Refreshing Number");
                sp.edit().putBoolean("isinit",false).commit();
                EventBus.getDefault().post(new InitEvent(parentUUID), "init");
                handler.postDelayed(runnable2, 5000);
            }
        });
        if(!TextUtils.isEmpty(parentUUID)){
            getVipdata();//获取vip信息
            EventBus.getDefault().post(new RequestAppUseTimeEvent(childuuid),"requestAppUseTime");
        }else {
            tv_vip.setText("立即开通");
            iv_vip.setImageResource(R.drawable.sy_vip_off);
        }
    }


    /**
     * 获取vip信息
     */
    private void getVipdata() {
        showLoading();
        final String userid= sp.getString("parentUUID", "");
        String url=Constants.PHP_URL+"v"+ AppUtils.getVerName()+"/vippage";;
        LogUtil.e("vip模块", "url:" + url);
        StringRequest postsr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtil.e("vip模块", "vip模块:" );
                dissloading();
                VipRootBean vipRoot =null;
                try {
                    vipRoot = new Gson().fromJson(response, VipRootBean.class);
                }catch (Exception e){
                    return;
                }
                ((MainActivity)getActivity()).setVipRoot(vipRoot);
                frashView(vipRoot);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                dissloading();
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
            tv_vip.setText("到期日:"+vipendtime);
            iv_vip.setImageResource(R.drawable.sy_vip_on);
        } else if(vipRoot.getData().getVipinfo().getViplvl().equals("2")){
            String vipendtime = vipRoot.getData().getVipinfo().getVipendtime();
            tv_vip.setText("试用期:"+vipendtime);
            iv_vip.setImageResource(R.drawable.sy_vip_on);
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
            tv_name.setText("暂无小孩");
        }else {
            tv_name.setText(childName);
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
     * 显示未登录对话框
     * */
    public void showNologDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity(),R.style.MyAlertDialog);
        View dialogView =  View.inflate(getActivity(), R.layout.dialog_internet_no_login, null);
        tvAddChlid = (TextView) dialogView.findViewById(R.id.tv_add_chlid);
        tvFinish = (TextView) dialogView.findViewById(R.id.tv_finish);
        ivAddFinish = (ImageView) dialogView.findViewById(R.id.iv_add_fish);
        tvAddChlid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showStartTime.dismiss();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                //使用uuid生成一个特定的字符串
                startActivity(intent);
                getActivity().finish();
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
     * (提示一键管控和视力保护和管控计划不能一起开)
     * */
    public CheckBox cb_box;
    public TextView tv_content,tvFinish1;
    public AlertDialog showDialog;
    public void showTiShiDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity(),R.style.MyAlertDialog);
        //builder.setCancelable(false);
        View dialogView =  View.inflate(getActivity(), R.layout.dialog_internet_tishi, null);
        tvFinish1 = (TextView) dialogView.findViewById(R.id.tv_finish);
        tv_content = (TextView) dialogView.findViewById(R.id.tv_content);
        cb_box = (CheckBox) dialogView.findViewById(R.id.cb_box);
        tvFinish1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog.dismiss();
                if(cb_box.isChecked()){
                    sp.edit().putBoolean("sptishi",true).commit();
                }
                if(sp.getBoolean("team"+childuuid,false)){
                    Toast.makeText(getActivity(), "孩子正处于团控状态，请稍后再试", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    yjgk();
                    handler.postDelayed(runnable, 6000);
                }
            }
        });
        builder.setView(dialogView);
        showDialog = builder.show();
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
            case R.id.tv_vip:
            case R.id.iv_vip:
                if (TextUtils.isEmpty(parentUUID)) {
                    showNologDialog();
                    return;
                }
//                Intent intent7 = new Intent(getActivity(), MyNewMemeberActivity.class);
//                startActivity(intent7);
                Toast.makeText(getActivity(), "公测版，原会员不受影响", Toast.LENGTH_SHORT).show();
                break;
            case R.id.iv_main_hzdw://孩子地理位置
                if (TextUtils.isEmpty(parentUUID)) {
                    showNologDialog();
                    return;
                }
                if (TextUtils.isEmpty(childuuid)) {
                    showDialog();
                    return;
                }

                hzdlwz(isNet);
                break;

            case R.id.tv_main_hzzl://孩子资料
                if (TextUtils.isEmpty(parentUUID)) {
                    showNologDialog();
                    return;
                }
                if (TextUtils.isEmpty(childuuid)) {
                    showDialog();
                    return;
                }
                Intent intent = new Intent(getActivity(), ChildNewDetailActivity.class);
                intent.putExtra("ChildUUID",childuuid);
                intent.putExtra("childFrom",childFrom);
                startActivityForResult(intent,1001);
                break;
            case R.id.tv_main_xzhz://选择孩子
                if (TextUtils.isEmpty(parentUUID)) {
                    showNologDialog();
                    return;
                }
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
                if (TextUtils.isEmpty(parentUUID)) {
                    showNologDialog();
                    return;
                }
                if (TextUtils.isEmpty(childuuid)) {
                    showDialog();
                    return;
                }
                Intent intent1=new Intent(getActivity(), ChildStateActivity.class);
                intent1.putExtra("ChildUUID",childuuid);
                startActivity(intent1);
                break;
            case R.id.tv_main_tjhz://添加孩子
                if (TextUtils.isEmpty(parentUUID)) {
                    showNologDialog();
                    return;
                }
                tjhz();
                break;
            case R.id.iv_controll:
                if (TextUtils.isEmpty(parentUUID)) {
                    showNologDialog();
                    return;
                }
                //如果是团控
                if (TextUtils.isEmpty(childuuid)) {
                    showDialog();
                    return;
                }
                isVip = sp.getBoolean("isVip", false);
                if(isOneControllVip){
                    if(!isVip){
                        if (affirmDialog != null) {
                            affirmDialog.show();
                        }
                        return;
                    }
                }

                checkParentModle("4");
                //提示一键管控和视力保护和管控计划不能一起开
                boolean sptishi = sp.getBoolean("sptishi",false);
                boolean aBoolean = sp.getBoolean("yjgk" + childuuid, false);
                if(!sptishi  && !aBoolean){
                    showTiShiDialog();
                    return;
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

                if (TextUtils.isEmpty(parentUUID)) {
                    showNologDialog();
                    return;
                }
                if (TextUtils.isEmpty(childuuid)) {
                    showDialog();
                    return;
                }
                isVip = sp.getBoolean("isVip", false);
                if(viplist.contains(Constants.VIP_EYE_PROTECT)){
                    if(!isVip){
                        if (affirmDialog != null) {
                            affirmDialog.show();
                        }
                        return;
                    }
                }

                //如果是团控
                if(sp.getBoolean("team"+childuuid,false)){
                    Toast.makeText(getActivity(), "孩子正处于团控状态，请稍后再试", Toast.LENGTH_SHORT).show();
                }else {
                    checkParentModle("10");
                    Intent intent11 = new Intent(getActivity(), EyeProtectActivity.class);
                    intent11.putExtra("ChildUUID", childuuid);
                    startActivity(intent11);
                }
                break;

            case R.id.iv_wb://网址黑白名单
                if (TextUtils.isEmpty(parentUUID)) {
                    showNologDialog();
                    return;
                }
                if (TextUtils.isEmpty(childuuid)) {
                    showDialog();
                    return;
                }
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
                    checkParentModle("14");
                    Intent intent12 = new Intent(getActivity(), NetControlNewActivity.class);
                    intent12.putExtra("ChildUUID", childuuid);
                    startActivity(intent12);
                }
                break;
            case R.id.iv_qqhm:
                if (TextUtils.isEmpty(parentUUID)) {
                    showNologDialog();
                    return;
                }
                if (TextUtils.isEmpty(childuuid)) {
                    showDialog();
                    return;
                }
                isVip = sp.getBoolean("isVip", false);
                if(viplist.contains(Constants.VIP_APP_QQHM)){
                    if(!isVip){
                        if (affirmDialog != null) {
                            affirmDialog.show();
                        }
                        return;
                    }
                }
                checkParentModle("44");
                Intent intents = new Intent(getActivity(), PhoneQinQingActivity.class);
                intents.putExtra("ChildUUID", childuuid);
                startActivity(intents);
                break;
            case R.id.iv_dhgm: //应用统计
                if (TextUtils.isEmpty(parentUUID)) {
                    showNologDialog();
                    return;
                }
                if (TextUtils.isEmpty(childuuid)) {
                    showDialog();
                    return;
                }
                isVip = sp.getBoolean("isVip", false);
                if(viplist.contains(Constants.VIP_APP_ZHIZHONG)){
                    if(!isVip){
                        if (affirmDialog != null) {
                            affirmDialog.show();
                        }
                        return;
                    }
                }
                Intent intentt = new Intent(getActivity(), AppUseActivity.class);
                intentt.putExtra("ChildUUID", childuuid);
                startActivity(intentt);
                break;
            case R.id.iv_gksz:
                if (TextUtils.isEmpty(parentUUID)) {
                    showNologDialog();
                    return;
                }
                if (TextUtils.isEmpty(childuuid)) {
                    showDialog();
                    return;
                }
                isVip = sp.getBoolean("isVip", false);
                if(viplist.contains(Constants.VIP_CONTROLL_PLAN)){
                    if(!isVip){
                        if (affirmDialog != null) {
                            affirmDialog.show();
                        }
                        return;
                    }
                }
                //如果是团控
                if(sp.getBoolean("team"+childuuid,false)){
                    Toast.makeText(getActivity(), "孩子正处于团控状态，请稍后再试", Toast.LENGTH_SHORT).show();
                }else {
                    checkParentModle("14");
                    Intent intent21 = new Intent(getActivity(), StopInternetPlanActivity.class);
                    intent21.putExtra("ChildUUID", childuuid);
                    startActivity(intent21);
                }
                break;
            case R.id.iv_ydgj:
                if (TextUtils.isEmpty(parentUUID)) {
                    showNologDialog();
                    return;
                }
                if (TextUtils.isEmpty(childuuid)) {
                    showDialog();
                    return;
                }
                isVip = sp.getBoolean("isVip", false);
                if(viplist.contains(Constants.VIP_SPORY_LINE)){
                    if(!isVip){
                        if (affirmDialog != null) {
                            affirmDialog.show();
                        }
                        return;
                    }
                }
                //如果是团控
                if(sp.getBoolean("team"+childuuid,false)){
                    Toast.makeText(getActivity(), "孩子正处于团控状态，请稍后再试", Toast.LENGTH_SHORT).show();
                }else {
                    checkParentModle("11");
                    Intent intent3 = new Intent(getActivity(), LoveTrailActivity.class);
//                Intent intent3 = new Intent(getActivity(), LocationHistoryActivity.class);
                    intent3.putExtra("ChildUUID", childuuid);
                    startActivity(intent3);
                }
                break;
            case R.id.iv_swgl:     //应用白名单
                if (TextUtils.isEmpty(parentUUID)) {
                    showNologDialog();
                    return;
                }
                if (TextUtils.isEmpty(childuuid)) {
                    showDialog();
                    return;
                }
                isVip = sp.getBoolean("isVip", false);
                if(viplist.contains(Constants.VIP_APP_MANAGE)){
                    if(!isVip){
                        if (affirmDialog != null) {
                            affirmDialog.show();
                        }
                        return;
                    }
                }
                //如果是团控
                if(sp.getBoolean("team"+childuuid,false)){
                    Toast.makeText(getActivity(), "孩子正处于团控状态，请稍后再试", Toast.LENGTH_SHORT).show();
                }else {
                    checkParentModle("32");
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
                if (TextUtils.isEmpty(parentUUID)) {
                    showNologDialog();
                    return;
                }
                if (TextUtils.isEmpty(childuuid)) {
                    showDialog();
                    return;
                }
                Toast.makeText(getActivity(), "更多功能正在研发中，敬请期待！", Toast.LENGTH_SHORT).show();
//                //如果是团控
//                if(sp.getBoolean("team"+childuuid,false)){
//                    Toast.makeText(getActivity(), "孩子正处于团控状态，请稍后再试", Toast.LENGTH_SHORT).show();
//                }else {
//                    Intent intent6 = new Intent(getActivity(), MoreFunctionActivity.class);
//                    intent6.putExtra("ChildUUID", childuuid);
//                    intent6.putExtra("childFrom", childFrom);
//                    startActivity(intent6);
//                }
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
//                Intent intentes = new Intent(getActivity(), MyNewMemeberActivity.class);
//                startActivity(intentes);
                Toast.makeText(getActivity(), "公测版，原会员不受影响", Toast.LENGTH_SHORT).show();
                break;
            case R.id.iv_helps: //帮助中心
                Intent intent8=new Intent(getActivity(),HelpCenterActivity.class);
                startActivity(intent8);
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
        childLineState = sp.getString(childuuid + "online", "");
        if (childLineState.equals(CmdCommon.CMD_OFFLINE)) {//下线
            ToastUtils.showToast(MyApplication.getContext(), "孩子处于离线状态,无法获取孩子当前位置");
            return;
        }
        Intent intent = new Intent(getActivity(), ChildLocationActivity.class);
        intent.putExtra("ChildUUID", childuuid);
        startActivity(intent);
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
            Intent intent = new Intent(getActivity(), ChildNewDetailActivity.class);
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
        childimage = sp.getString("childImage", "");
        childLineState = sp.getString(childuuid + "online", "");
        boolean childFlag1 = sp.getBoolean("childFlag1", false);
        boolean childFlag2 = sp.getBoolean("childFlag2", false);

        boolean yjgk1 = sp.getBoolean("yjgk"+childuuid, false);
        boolean yjgk2 = sp.getBoolean("team"+childuuid, false);
        if(TextUtils.isEmpty(childuuid)){
            if(isOneControllVip){
                iv_controll.setImageResource(R.drawable.soye_vip_off);
            }else {
                iv_controll.setImageResource(R.drawable.soye_off);
            }
            tv_state.setText("您还没有绑定小孩，快去添加孩子吧！");
            tv_name.setText("暂无小孩");
            Glide.with(this).load(R.drawable.mine_head__01).into(tvMainHzzl);
        }
        if(!TextUtils.isEmpty(childName)){
            tv_name.setText(childName);
        }
        if (yjgk1) {
            if(isOneControllVip){
                iv_controll.setImageResource(R.drawable.soye_vip_on);
            }else {
                iv_controll.setImageResource(R.drawable.soye_on);
            }
            setChildState("锁屏中");
        } else {
            if(isOneControllVip){
                iv_controll.setImageResource(R.drawable.soye_vip_off);
            }else {
                iv_controll.setImageResource(R.drawable.soye_off);
            }
            setChildState("在线自由状态");
        }
        if(sp.getBoolean("team"+childuuid,false)){
            if(isOneControllVip){
                iv_controll.setImageResource(R.drawable.soye_vip_on);
            }else {
                iv_controll.setImageResource(R.drawable.soye_on);
            }
            setChildState("锁屏中");
        }
        if (!TextUtils.isEmpty(childuuid)) {
            getAppList();// //判断applist是否(提前获取app列表保存本地)
        }
        if (TextUtils.isEmpty(parentUUID)) {
            tv_state.setText("您还没有登陆，请前往登陆！");
            viewgroup.setVisibility(View.GONE);
            tv_butter.setVisibility(View.INVISIBLE);
            tv_no_login.setVisibility(View.VISIBLE);
            tv_no_login.setText("您还没有登陆，请前往登陆！");
        }else {
            if(TextUtils.isEmpty(childuuid)){
                tv_no_login.setVisibility(View.VISIBLE);
                tv_no_login.setText("您还没绑定孩子，快去添加孩子吧！");
                tv_no_vip.setVisibility(View.GONE);
            }else {
                tv_no_login.setVisibility(View.GONE);
            }
            tv_butter.setVisibility(View.VISIBLE);
            viewgroup.setVisibility(View.VISIBLE);
        }

        String power = sp.getString("power" + childuuid, "");
        if(!TextUtils.isEmpty(power)){
            iv_butter.setPower(Integer.parseInt(power));
            tv_butter.setText(power+"%");
        }

        if(childimage!=null){
            Glide.with(this).load(childimage).transform(new GlideCircleTransform(getActivity())).diskCacheStrategy(DiskCacheStrategy.ALL).error(R.drawable.mine_head__01).into(tvMainHzzl);
        }else{
            Glide.with(this).load(R.drawable.mine_head__01).into(tvMainHzzl);
        }
    }
    /**
     *  判断applist是否(提前获取app列表保存本地)
     */
    private void getAppList() {
        AppDataBeanDao dao = GreenDaoManager.getInstance().getSession().getAppDataBeanDao();
        List<AppDataBean> mlist = dao.queryBuilder().where(AppDataBeanDao.Properties.Childuuid.eq(childuuid)).build().list();
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
        LogUtil.e(TAG, "----orderAddChildData: " + dataEvent.childUUID + dataEvent.childSex + dataEvent.childName + dataEvent.childFrom);
        ((MainActivity)getActivity()).initData();
        if (!TextUtils.isEmpty(newChildUUID)) {
            if (TextUtils.isEmpty(childuuid)) {
                childuuid = newChildUUID;
                childName = dataEvent.childName;
                childSex = dataEvent.childSex;
                childFrom = dataEvent.childFrom;
                childztl= dataEvent.childztl;
                childimage= dataEvent.childimage;
                sp.edit().putString("ChildUUID", childuuid)
                        .putString("childName", childName)
                        .putString("childSex", childSex)
                        .putString("childFrom", childFrom)
                        .putString("childztl", childztl)
                        .putString("childImage", childimage)
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

        /////////////////////  关闭管控和视力保护
        if(!yjgk.equals(CmdCommon.CMD_FLAG_CLOSE)){
            if (findChild.getSlbhflag().equals(CmdCommon.CMD_FLAG_OPEN) ) {
                String slbhSpacetime = findChild.getSlbhSpacetime();
                String slbhResttime = findChild.getSlbhResttime();
                EventBus.getDefault().post(new SlbhEvent(parentUUID, childuuid, "2", slbhSpacetime, slbhResttime), "slbh");
                findChild.setSlbhflag(CmdCommon.CMD_FLAG_CLOSE);
                childDao.update(findChild);
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
        LogUtil.e("ordeyjgk","处理一键管控反馈:"+yjgk);
        if (findChild != null) {
            if (yjgk.equals(CmdCommon.CMD_FLAG_CLOSE)) {
                ToastUtils.showToast(MyApplication.getContext(),"一键管控功能关闭");
                findChild.setYjgkflag(CmdCommon.CMD_FLAG_CLOSE);
                if(isOneControllVip){
                    iv_controll.setImageResource(R.drawable.soye_vip_off);
                }else {
                    iv_controll.setImageResource(R.drawable.soye_off);
                }
//                tv_state.setText("一键管控未开启");
                setChildState("在线自由状态");
                sp.edit().putBoolean("yjgk"+childuuid,false).commit();
            } else {
                ToastUtils.showToast(MyApplication.getContext(), "一键管控功能开启");
                findChild.setYjgkflag(CmdCommon.CMD_FLAG_OPEN);
                sp.edit().putBoolean("yjgk" + childuuid, true).commit();
                if(isOneControllVip){
                    iv_controll.setImageResource(R.drawable.soye_vip_on);
                }else {
                    iv_controll.setImageResource(R.drawable.soye_on);
                }
//                tv_state.setText("一键管控开启中");
                setChildState("锁屏中");
            }
            childDao.update(findChild);
        } else {
            ChildContrlFlag childFlag = new ChildContrlFlag(null, childuuid, parentUUID, "", ""
                    , CmdCommon.CMD_FLAG_CLOSE, CmdCommon.CMD_FLAG_CLOSE
                    , CmdCommon.CMD_FLAG_CLOSE, "30", "5"
                    , CmdCommon.CMD_FLAG_CLOSE, CmdCommon.CMD_FLAG_CLOSE);
            childDao.insert(childFlag);
            ToastUtils.showToast(MyApplication.getContext(),"一键管控功能关闭");
            sp.edit().putBoolean("yjgk"+childuuid,false).commit();
            if(isOneControllVip){
                iv_controll.setImageResource(R.drawable.soye_vip_off);
            }else {
                iv_controll.setImageResource(R.drawable.soye_off);
            }
//            tv_state.setText("一键管控未开启");
            setChildState("在线自由状态");
        }
    }

    /**
     * 增加孩子信息数据
     */
    @Subscriber(mode = ThreadMode.ASYNC, tag = "ChildMessageData")
    public void ChildMessageDataEvent(ChildMessageDataEvent dataEvent) {
        Message obtain = Message.obtain();
        LogUtil.e(TAG, "增加孩子信息数据:ChildMessageDataEvent");
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
            if(isOneControllVip){
                iv_controll.setImageResource(R.drawable.soye_vip_on);
            }else {
                iv_controll.setImageResource(R.drawable.soye_on);
            }
//            tv_state.setText("一键管控开启中");
            setChildState("锁屏中");
        } else {
            if(isOneControllVip){
                iv_controll.setImageResource(R.drawable.soye_vip_off);
            }else {
                iv_controll.setImageResource(R.drawable.soye_off);
            }
//            tv_state.setText("一键管控未开启");
            setChildState("在线自由状态");
        }
        if(sp.getBoolean("team"+childuuid,false)){
            if(isOneControllVip){
                iv_controll.setImageResource(R.drawable.soye_vip_on);
            }else {
                iv_controll.setImageResource(R.drawable.soye_on);
            }
//            tv_state.setText("一键管控开启中");
            setChildState("锁屏中");
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
        if(requestCode==1001){
//            getDataFromDb(false);
            EventBus.getDefault().post(new RequestAppUseTimeEvent(childuuid),"requestAppUseTime");
            if(resultCode==-1){
                String childuuid = sp.getString("ChildUUID", "");
                if(!TextUtils.isEmpty(childuuid))
                ((MainActivity)getActivity()).initData();
                EventBus.getDefault().post(new RequestAppUseTimeEvent(childuuid),"requestAppUseTime");
            }

        }
        if(requestCode==1002 && resultCode==-1){ //视力保护和管控计划
            ((MainActivity)getActivity()).initData();
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
        tv_app_number.setText("共使用了"+datas.size()+"个应用");
        String power = sp.getString("power" + childuuid, "");
        if(!TextUtils.isEmpty(power)){
            iv_butter.setPower(Integer.parseInt(power));
            tv_butter.setText(power+"%");
        }

        isVip = sp.getBoolean("isVip", false);
        if(viplist.contains(Constants.VIP_APP_ZHIZHONG)){
            if(!isVip){
                tv_no_vip.setVisibility(View.VISIBLE);
                viewgroup.setVisibility(View.GONE);
                return;
            }
        }
        tv_no_vip.setVisibility(View.GONE);
        viewgroup.setVisibility(View.VISIBLE);
        if(datas==null || datas.size()==0){
            tv_no_login.setVisibility(View.VISIBLE);
            tv_no_login.setText("暂无动态信息");
            return;
        }
        tv_no_login.setVisibility(View.GONE);
        viewgroup.removeAllViews();
        for (int i = 0; i < datas.size(); i++) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_sy_new, null);
            RelativeLayout re1 = (RelativeLayout) view.findViewById(R.id.rv_1);
            RelativeLayout re2 = (RelativeLayout) view.findViewById(R.id.rv_2);
            ImageView iv_head = (ImageView)view.findViewById(R.id.iv_head);
            TextView tv_name = (TextView)view.findViewById(R.id.tv_name);
            TextView tv_time = (TextView)view.findViewById(R.id.tv_time);
            TextView tv_month = (TextView)view.findViewById(R.id.tv_month);
            TextView tv_request_time = (TextView)view.findViewById(R.id.tv_request_time);
            String url= Constants.APP_IMAGE_URL+parentUUID+"/"+childuuid+"/"+datas.get(i).getAppPkgName()+".jpg";
            Glide.with(getActivity()).load(url).placeholder(R.drawable.soye_mrtb).error(R.drawable.soye_mrtb).into(iv_head);
            tv_name.setText(datas.get(i).c);
            int usetime = Integer.parseInt(datas.get(i).getAppUsage());
            if(usetime/60>0){
                if(usetime/(60*60)>0){
                    tv_time.setText("使用了"+usetime/3600+"小时"+(usetime%3600)/60+"分");
                }else{
                    tv_time.setText("使用了"+usetime/60+"分"+usetime%60+"秒");
                }
            }else {
                tv_time.setText("使用了"+usetime+"秒");
            }
            if(!TextUtils.isEmpty(datas.get(i).getAppTime())){
                SimpleDateFormat df = new SimpleDateFormat("MM-dd HH:mm");//设置日期格式
                Date nowDate = new Date();
                long ss = Long.parseLong(datas.get(i).getAppTime());
                Log.e("-------df.format(ss)--",df.format(ss));
                String[] a=df.format(ss).split(" ");
                tv_month.setText(a[0]);
                tv_request_time.setText(a[1]);

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
    //    /**
//     *从本地获取应用追踪数据//[{\"c\":\"设置\",\"d\":1538033310974,\"a\":\"com.android.settings\",\"b\":\"312\"}
//     * 10004977 噢噢噢  10005353 噢噢  10007786 chjj
//     */
    public void getDataFromDb(boolean isMain) {
        tv_no_login.setVisibility(View.VISIBLE);
        tv_no_login.setText("暂无动态信息");
//        viewgroup.removeAllViews();
//        childuuid = sp.getString("ChildUUID", "");
//
//        if(TextUtils.isEmpty(childuuid)){
//            tv_no_login.setVisibility(View.VISIBLE);
//            tv_app_number.setText("共使用了0个应用");
//            tv_no_login.setText("您还没绑定孩子，快去添加孩子吧！");
//            return;
//        }
//        AppUseBeanDao dao = GreenDaoManager.getInstance().getSession().getAppUseBeanDao();
//        List<AppUseBean> list = dao.queryBuilder().where(AppUseBeanDao.Properties.Childuuid.eq(childuuid)).build().list();
//        tv_app_number.setText("共使用了"+list.size()+"个应用");
//        isVip = sp.getBoolean("isVip", false);
//        if(viplist.contains(Constants.VIP_APP_ZHIZHONG)){
//            if(!isVip){
//                tv_no_login.setVisibility(View.GONE);
//                tv_no_vip.setVisibility(View.VISIBLE);
//                return;
//            }
//        }
//        tv_no_login.setVisibility(View.VISIBLE);
//        tv_no_vip.setVisibility(View.GONE);
//
//        if(list==null || list.size()==0){
//            if(isMain){
//                LogUtil.e("处理App使用数据","list.size()==0    网络请求");
//                EventBus.getDefault().post(new RequestAppUseTimeEvent(childuuid),"requestAppUseTime");
//                isMain=false;
//            }else {
//                tv_no_login.setVisibility(View.VISIBLE);
//                tv_no_login.setText("暂无动态信息");
//            }
//            return;
//        }
//        LogUtil.e("处理App使用数据","本地请求");
//        for (int i = 0; i < list.size(); i++) {
//            LogUtil.e("---------"+"i"+"-------",list.get(i).getA()+"childuuid"+childuuid);
//        }
//        tv_no_login.setVisibility(View.GONE);
//        for (int i = 0; i < list.size(); i++) {
//            View view= LayoutInflater.from(getActivity()).inflate(R.layout.item_sy_new,null);
//            RelativeLayout re1 = (RelativeLayout)view.findViewById(R.id.rv_1);
//            RelativeLayout re2 = (RelativeLayout)view.findViewById(R.id.rv_2);
//            ImageView iv_heads = (ImageView)view.findViewById(R.id.iv_head);
//            TextView tv_name = (TextView)view.findViewById(R.id.tv_name);
//            TextView tv_request_time = (TextView)view.findViewById(R.id.tv_request_time);
//            TextView tv_time = (TextView)view.findViewById(R.id.tv_time);
//            tv_name.setText(list.get(i).getA());
//
//            String url= Constants.APP_IMAGE_URL+parentUUID+"/"+childuuid+"/"+list.get(i).getB()+".jpg";
//            Glide.with(getActivity()).load(url).placeholder(R.drawable.soye_mrtb).error(R.drawable.soye_mrtb).into(iv_heads);
//            int usetime = Integer.parseInt(list.get(i).getC());
//            if(usetime/60>0){
//                if(usetime/(60*60)>0){
//                    tv_time.setText("停留了"+usetime/3600+"小时"+(usetime%3600)/60+"分");
//                }else{
//                    tv_time.setText("停留了"+usetime/60+"分"+usetime%60+"秒");
//                }
//            }else {
//                tv_time.setText("停留了"+usetime+"秒");
//            }
//            if(!TextUtils.isEmpty(list.get(i).getD())){
//                SimpleDateFormat df = new SimpleDateFormat("HH:mm");//设置日期格式
//                long ss = Long.parseLong(list.get(i).getD());
//                tv_request_time.setText( df.format(ss));
//            }
//            if(i==0){
//                re1.setVisibility(View.VISIBLE);
//                re2.setVisibility(View.GONE);
//            }else {
//                re2.setVisibility(View.VISIBLE);
//                re1.setVisibility(View.GONE);
//            }
//            viewgroup.addView(view);
//        }


    }

    /**
     * 获取家长端模块活跃度
     */
    private void checkParentModle(final String model_id) {

        String url=Constants.PHP_URL+"v"+ AppUtils.getVerName()+"/ModuleActivity";
        LogUtil.i("家长端的模块活跃度接口", "url:" + url);
        StringRequest postsr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtil.i("家长端模块活跃情况", "模块活跃度:" + response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                LogUtil.i("家长端模块活跃情况", "error:" + volleyError);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //创建一个集合，放的是keyvalue的key是参数名与value是参数值
                Map<String, String> map = new HashMap<String, String>();
                map.put("user_id", parentUUID);
                map.put("model_id", model_id);
                return map;
            }
        };
        postsr.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.getRequestQueue().add(postsr);
    }



    /**
     * 显示状态
     */
    private void setChildState(String state) {
        if (childLineState.equals(CmdCommon.CMD_OFFLINE)) {//下线
            tv_state.setText("孩子已离线，关心下孩子吧！");
            return;
        }else {
            if(state.equals("锁屏中")){
                tv_state.setText(state);
                return;
            }
            ChildContrlFlagDao childDao = GreenDaoManager.getInstance().getSession().getChildContrlFlagDao();
            ChildContrlFlag findChild = childDao.queryBuilder().where(ChildContrlFlagDao.Properties.Childuuid.eq(childuuid)).build().unique();
            NetPlanDataBeanDao dao  = GreenDaoManager.getInstance().getSession().getNetPlanDataBeanDao();
            List<NetPlanDataBean>  planlist = dao.queryBuilder().where(NetPlanDataBeanDao.Properties.Childuuid.eq(childuuid)).orderDesc(NetPlanDataBeanDao.Properties._id).build().list();
            if(findChild!=null){
                if (findChild.getSlbhflag()!=null &&findChild.getSlbhflag().equals(CmdCommon.CMD_FLAG_OPEN) ) {
                    tv_state.setText("视力保护中");
                    return;
                }
                if(planlist!=null && planlist.size()>0){
                    for (int i = 0; i < planlist.size(); i++) {
                        if(planlist.get(i).getPlanflag().equals("1")){
                            tv_state.setText("管控计划中");
                            return;
                        }
                    }
                }
            }
            if(TextUtils.isEmpty(childuuid)){
                tv_state.setText("您还没有绑定小孩，快去添加孩子吧！");
            }else {
                tv_state.setText(state);
            }

        }
    }
}