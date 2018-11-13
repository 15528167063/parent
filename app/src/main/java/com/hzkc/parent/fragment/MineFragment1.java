package com.hzkc.parent.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.hzkc.parent.Bean.MineItemBean;
import com.hzkc.parent.Bean.ShareBean;
import com.hzkc.parent.Bean.ShareResultBean;
import com.hzkc.parent.R;
import com.hzkc.parent.activity.AccountManagerActivity;
import com.hzkc.parent.activity.ChangeNcAndIocnActivity;
import com.hzkc.parent.activity.ChildManagerActivity;
import com.hzkc.parent.activity.HelpCenterActivity;
import com.hzkc.parent.activity.LoginActivity;
import com.hzkc.parent.activity.OpinionBackActivity;
import com.hzkc.parent.adapter.MineFragmentListAdapter;
import com.hzkc.parent.appcation.MyApplication;
import com.hzkc.parent.greendao.GreenDaoManager;
import com.hzkc.parent.greendao.gen.AppDataBeanDao;
import com.hzkc.parent.greendao.gen.AppUseBeanDao;
import com.hzkc.parent.greendao.gen.ChildContrlFlagDao;
import com.hzkc.parent.greendao.gen.ChildsTableDao;
import com.hzkc.parent.greendao.gen.LoveTrailDataDao;
import com.hzkc.parent.greendao.gen.NetPlanDataBeanDao;
import com.hzkc.parent.greendao.gen.PhoneDataDao;
import com.hzkc.parent.mina.Constants;
import com.hzkc.parent.utils.AppUtils;
import com.hzkc.parent.utils.GlideCircleTransform;
import com.hzkc.parent.utils.LogUtil;
import com.hzkc.parent.utils.NetworkUtil;
import com.hzkc.parent.utils.ToastUtils;
import com.hzkc.parent.view.AffirmDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

public class MineFragment1 extends BaseFragment implements View.OnClickListener {

    private View v;
    private TextView topTitle;
    private LinearLayout idChildManger;
    private LinearLayout idAccountManager;
    private ImageView ivMineChildIcon;
    //    private String[] minepagerName = {"优会员","分享应用", "功能介绍", "联系我们", "用户反馈", "退出当前账号"};
    private String[] minepagerName = {"VIP会员","分享应用", "帮助中心", "我要推广", "用户反馈", "退出当前账号"};
    private int[] minepagerIcon = {R.drawable.mine_jlb,R.drawable.mine_fxyy, R.drawable.mine_gnjs,
            R.drawable.appshare, R.drawable.mine_yhfk, R.drawable.mine_tcdqzh};
    private ImageView ivMineSex;
    private ListView lvMine;
    private static final String TAG = "MineFragment";
    private String childuuid;
    private String childName;
    private String childSex;
    private boolean isNet;
    private String vipDate;
    private ImageView ivVipIcon;
    private TextView tvVipTime;
    private TextView tvVipOuttime;
    private String parentUUID;
    private String nc;
    private String txid;
    private TextView tv_name;
    @Override
    public View initView() {
        v = View.inflate(getActivity(), R.layout.fragment_mine, null);
        return v;
    }

    /**
     * 初始化控件
     */
    @Override
    public void iniData() {
        childuuid = sp.getString("ChildUUID", "");
        childName = sp.getString("childName", "");
        childSex = sp.getString("childSex", "");
        vipDate = sp.getString("viptime", "");
        nc = sp.getString("nc", "");
        txid = sp.getString("txid", "");
        topTitle = (TextView) v.findViewById(R.id.tv_top_title);
        idChildManger = (LinearLayout) v.findViewById(R.id.id_child_manger);
        idAccountManager = (LinearLayout) v.findViewById(R.id.id_account_manager);
        ivMineChildIcon = (ImageView) v.findViewById(R.id.iv_mine_child_icon);
        ivMineSex = (ImageView) v.findViewById(R.id.iv_mine_sex);
        lvMine = (ListView) v.findViewById(R.id.lv_mine);
        ivVipIcon = (ImageView) v.findViewById(R.id.iv_vip_icon);
        tvVipTime = (TextView) v.findViewById(R.id.tv_vip_time);
        tvVipOuttime = (TextView) v.findViewById(R.id.tv_vip_outtime);
        parentUUID = sp.getString("parentUUID", "");
        //设置条目点击事件
        idChildManger.setOnClickListener(this);
        //修改头像昵称
        ivMineChildIcon.setOnClickListener(this);
        idAccountManager.setOnClickListener(this);
        isNet = NetworkUtil.isConnected();
        //设置toolbar文本
        topTitle.setText("我的");
        //初始化listview
        getVipData();

    }


    /**
     * 初始化listview
     * */
    private void initLv(Boolean isShare,Boolean isTuiguan,String des) {
        List<MineItemBean> mlist = new ArrayList<>();
        for (int i = 0; i < minepagerName.length; i++) {
            MineItemBean bean = new MineItemBean();
            bean.setTvName(minepagerName[i]);
            bean.setIvIcon(minepagerIcon[i]);
            mlist.add(bean);
        }
        MineFragmentListAdapter adapter = new MineFragmentListAdapter(mlist, getActivity(),isShare,isTuiguan,parentUUID,des);
        lvMine.setAdapter(adapter);

        View view= LayoutInflater.from(getActivity()).inflate(R.layout.fragment_mne_footer,null);

        ImageView iv_head= (ImageView) view.findViewById(R.id.iv_mine_child_icon);
        tv_name= (TextView) view.findViewById(R.id.tv_child_name);
        LinearLayout line1= (LinearLayout) view.findViewById(R.id.id_child_manger);
        LinearLayout line2= (LinearLayout) view.findViewById(R.id.id_account_manager);
        tv_name.setText(nc+"");
        Glide.with(getContext()).load(Constants.FIND_URL_TX+txid+".jpg").transform(new GlideCircleTransform(getContext())).error(R.drawable.wddd_icon).diskCacheStrategy(DiskCacheStrategy.ALL).into(iv_head);

        iv_head.setOnClickListener(this);
        line1.setOnClickListener(this);
        line2.setOnClickListener(this);
        lvMine.addHeaderView(view);
        affirmDialog = new AffirmDialog(getActivity());
        affirmDialog.setTitleText("确认退出当前账号？");
        affirmDialog.setAffirmClickListener(this);


        lvMine.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
//                    case 2://我的订单
//                        Intent intent = new Intent(getActivity(), MyOrderActivity.class);
//                        startActivity(intent);
//                        break;
                    case 2://分享应用
                        showShare(1);
                        break;
                    case 3://功能介绍、帮助中心
//                        Intent intent1 = new Intent(getActivity(), FindArticleActivity.class);
                        Intent intent1 = new Intent(getActivity(), HelpCenterActivity.class);
                        startActivity(intent1);

                        break;
                    case 4://联系我们
//                        Intent intent3 = new Intent(getActivity(), TouchMineActivity.class);
//                        startActivity(intent3);
                        showShare(2);
                        break;
                    case 5://用户反馈
                        if (!isNet) {
                            ToastUtils.showToast(MyApplication.getContext(), "网络不通，请检查网络再试");
                            return;
                        }
                        Intent intent4 = new Intent(getActivity(), OpinionBackActivity.class);
                        startActivity(intent4);
                        break;
                    case 6://退出当前账号
                        if (affirmDialog != null) {
                            affirmDialog.show();
                        }
                        break;
                    default:
                        break;
                }
            }
        });
    }
    private AffirmDialog affirmDialog;
    private void tuichu() {
        sp.edit().putString("childName", "")
                .putString("ChildUUID", "")
                .putString("parentUUID", "")
                .putString("childSex", "")
                .putString("token", "")
                .putString("tokens", "")
//                .putBoolean("isVip", false)
                .putString("headimage","")    //清空本地头像，昵称，版本，推送id值，加密token,
                .putString("loginImFlag", "")
                .putString("childVersion", "")
                .putString("loginImFlag","")
                .putString("nc", "").commit();
        ChildsTableDao childDao = GreenDaoManager.getInstance().getSession().getChildsTableDao();
        ChildContrlFlagDao childCopntrlDao = GreenDaoManager.getInstance().getSession().getChildContrlFlagDao();
        AppDataBeanDao appdao = GreenDaoManager.getInstance().getSession().getAppDataBeanDao();
        NetPlanDataBeanDao netPlanDao = GreenDaoManager.getInstance().getSession().getNetPlanDataBeanDao();
        LoveTrailDataDao lovePlanDao = GreenDaoManager.getInstance().getSession().getLoveTrailDataDao();
        AppUseBeanDao appUseBeanDao = GreenDaoManager.getInstance().getSession().getAppUseBeanDao();
        PhoneDataDao phoneDataDao = GreenDaoManager.getInstance().getSession().getPhoneDataDao();
        phoneDataDao.deleteAll();
        appUseBeanDao.deleteAll();
        lovePlanDao.deleteAll();
        netPlanDao.deleteAll();
        childDao.deleteAll();
        childCopntrlDao.deleteAll();
        appdao.deleteAll();
        Intent intent5 = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent5);
        getActivity().finish();
    }

    /**
     * 条目点击事件
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_child_manger://孩子管理
                if (!isNet) {
                    ToastUtils.showToast(MyApplication.getContext(), "网络不通，请检查网络再试");
                    return;
                }
                if (!TextUtils.isEmpty(childuuid)) {
                    Intent intent1 = new Intent(getActivity(), ChildManagerActivity.class);
                    startActivity(intent1);
                } else {
                    ToastUtils.showToast(MyApplication.getContext(), "请添加孩子");
                    return;
                }
                break;
            case R.id.id_account_manager://账号管理
                Intent intent2 = new Intent(getActivity(), AccountManagerActivity.class);
                startActivity(intent2);
                break;
            case R.id.iv_mine_child_icon://头像修改昵称
                Intent intent3 = new Intent(getActivity(), ChangeNcAndIocnActivity.class);
                intent3.putExtra("join","0");
                startActivityForResult(intent3,1004);
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
                logout();
                break;
            default:
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //checkVip();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ShareSDK.stopSDK(getActivity());
    }

    /**
     * 分享的设置
     */
    private void showShare(final int type) {
        ShareSDK.initSDK(getActivity());
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间等使用
        oks.setTitle("优成长-科学管控孩子，手机专业防沉迷");
        // titleUrl是标题的网络链接，QQ和QQ空间等使用
        if(type==1){
            oks.setTitleUrl("http://www.ycz365.com/");
        }else {
            oks.setTitleUrl("http://www.ycz365.com/Yczshare/index.php?code="+parentUUID);
        }
        // text是分享文本，所有平台都需要这个字段
        oks.setText("我正在使用优成长，赶紧跟我一起来体验！");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        oks.setImageUrl("http://www.ycz365.com/login_icon.png");
        // url仅在微信（包括好友和朋友圈）中使用
        if(type==1){
            oks.setUrl("http://www.ycz365.com/");
        }else {
            oks.setUrl("http://www.ycz365.com/Yczshare/index.php?code="+parentUUID);
        }

        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        //oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
//        oks.setSiteUrl("http://www.ycz365.com/");
        if(type==1){
            oks.setSiteUrl("http://www.ycz365.com/");
        }else {
            oks.setSiteUrl("http://www.ycz365.com/Yczshare/index.php?code="+parentUUID);
        }
        oks.setCallback(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                LogUtil.e("setCallback","分享成功........................................");
                LogUtil.e("setCallback","分享成功........................................");
                LogUtil.e("setCallback","分享成功........................................");
                sp.edit().putString("shareApp","shareApp").commit();
                if(type==1){
                    showResult();
                }

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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1004){
            nc = sp.getString("nc", "");
            tv_name.setText(nc+"");
        }
    }

    /**
     * 判断是不是vip
     * */
    private void checkVip() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
        Date nowDate = new Date();
        long t1 = nowDate.getTime();
        long t2 = Long.parseLong(vipDate);
        if(t1-t2>0){//过期
            ivVipIcon.setImageResource(R.drawable.minepager_vip_outtime_icon);
            tvVipOuttime.setVisibility(View.VISIBLE);
        }else {//未过期
            ivVipIcon.setImageResource(R.drawable.minepager_vip_icon);
            tvVipOuttime.setVisibility(View.GONE);
        }
        String vipTime = df.format(t2);
        LogUtil.e(vipTime);
        tvVipTime.setText(vipTime);
    }
    /**
     * 退出环信
     */
    private void logout() {
        final ProgressDialog pd = new ProgressDialog(getActivity());
        String st = getResources().getString(R.string.Are_logged_out);
        pd.setMessage(st);
        pd.setCanceledOnTouchOutside(false);
        pd.show();
        tuichu();
    }

    //
//    public  Boolean isShare=false,isTuiGuang=false;
//    private void getVipData() {
//        String parentUUID1 = sp.getString("parentUUID", "");
//        String url = Constants.FIND_URL+"YczApi/public/index.php/GetShareDesc/"+parentUUID1+".asp";
//        Log.e("-------------url",url);// {"code":"200","message":"OK","result":[{"isactivity":"Y","describe":"分享有礼","ischannel":"N"}]}
//        StringRequest stringRequest1 = new StringRequest(Request.Method.GET, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        Log.e("-------------分享有礼",response);
//                        ShareBean shareBean = new Gson().fromJson(response, ShareBean.class);
//                        if(shareBean.code.equals("200")){
//                            if(shareBean.result.get(0).isactivity.equals("Y")){
//                                isShare=true;
//                            }else {
//                                isShare=false;
//                            }
//                            if(shareBean.result.get(0).ischannel.equals("Y")){
//                                isTuiGuang=true;
//                            }else {
//                                isTuiGuang=false;
//                            }
//                        }
//                        initLv(isShare,isTuiGuang,shareBean.result.get(0).describe);
//                        return;
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                isShare=false;
//                isTuiGuang=false;
//                initLv(isShare,isTuiGuang,"");
//            }
//        });
//        stringRequest1.setRetryPolicy(new DefaultRetryPolicy(10000,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        Volley.newRequestQueue(getActivity()).add(stringRequest1);
//    }
    public  Boolean isShare=false,isTuiGuang=false;
    private void getVipData() {
        String url=Constants.PHP_URL+"v"+ AppUtils.getVerName()+"/getShareDesc";
        Log.e("分享有礼", "url:" + url);
        StringRequest postsr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("-------------分享有礼",response);
                ShareBean shareBean = new Gson().fromJson(response, ShareBean.class);
                if(shareBean.code.equals("1")){
                    if(shareBean.data.isActivity.equals("1")){
                        isShare=true;
                    }else {
                        isShare=false;
                    }
                    if(shareBean.data.ischannel.equals("1")){
                        isTuiGuang=true;
                    }else {
                        isTuiGuang=false;
                    }
                }
                initLv(isShare,isTuiGuang,shareBean.data.describe);
                return;

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                isShare=false;
                isTuiGuang=false;
                initLv(isShare,isTuiGuang,"");

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //创建一个集合，放的是keyvalue的key是参数名与value是参数值
                Map<String, String> map = new HashMap<String, String>();
                map.put("user_id",parentUUID);
                return map;
            }
        };
        postsr.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.getRequestQueue().add(postsr);
    }
    private void showResult()  {
        String parentUUID1 = sp.getString("parentUUID", "");
        String url=Constants.PHP_URL+"v"+ AppUtils.getVerName()+"/sharing";
        Log.e("分享回调", "url:" + url);
        StringRequest postsr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("---分享回调response：",response);
                ShareResultBean shareBean = new Gson().fromJson(response, ShareResultBean.class);
                if(shareBean.code==1){
                    showResult(shareBean);
                }
                return;

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //创建一个集合，放的是keyvalue的key是参数名与value是参数值
                Map<String, String> map = new HashMap<String, String>();
                map.put("user_id",parentUUID);
                return map;
            }
        };
        postsr.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.getRequestQueue().add(postsr);
    }





    private void showResult(ShareResultBean shareBean) {
        final AlertDialog ad=new AlertDialog.Builder(getActivity()).create();
        ad.show();
        ad.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        Window window = ad.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));  // 有白色背景，加这句代码

        View view= LayoutInflater.from(getActivity()).inflate(R.layout.deteledialog,null);
        TextView tv_phone = (TextView) view.findViewById(R.id.tv_phone);
        TextView tv_phone1 = (TextView) view.findViewById(R.id.tv_phone1);
        TextView tv_queding = (TextView) view.findViewById(R.id.tv_queding);
        tv_phone.setText(shareBean.data.status);
        tv_phone1.setText("获得vip天数:"+shareBean.data.getvip);
        ad.setView(view);
        tv_queding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ad.dismiss();
            }
        });
        window.setContentView(view);

    }
}
