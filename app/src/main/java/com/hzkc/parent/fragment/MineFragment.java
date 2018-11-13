package com.hzkc.parent.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.hzkc.parent.Bean.MessageNumberRoot;
import com.hzkc.parent.Bean.ShareBean;
import com.hzkc.parent.Bean.ShareResultBean;
import com.hzkc.parent.Bean.VipRootBean;
import com.hzkc.parent.MainActivity;
import com.hzkc.parent.R;
import com.hzkc.parent.activity.AboutMineActivity;
import com.hzkc.parent.activity.AccountManagerActivity;
import com.hzkc.parent.activity.ChildManagerActivity;
import com.hzkc.parent.activity.LoginActivity;
import com.hzkc.parent.activity.MessageManagerActivity;
import com.hzkc.parent.activity.MineSettingActivity;
import com.hzkc.parent.activity.OpinionBackActivity;
import com.hzkc.parent.activity.OrderHistoryActivity;
import com.hzkc.parent.activity.ParentZxingActivity;
import com.hzkc.parent.appcation.MyApplication;
import com.hzkc.parent.mina.Constants;
import com.hzkc.parent.utils.AppUtils;
import com.hzkc.parent.utils.DensityUtil;
import com.hzkc.parent.utils.GlideCircleTransform;
import com.hzkc.parent.utils.LogUtil;
import com.hzkc.parent.utils.NetworkUtil;
import com.hzkc.parent.utils.ToastUtils;
import com.hzkc.parent.view.AffirmDialog;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import java.util.HashMap;
import java.util.Map;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

import static com.hzkc.parent.mina.Constants.PHP_URL;

public class MineFragment extends BaseFragment implements View.OnClickListener {

    private View view;
    private String childuuid;
    private String childName;
    private boolean isNet;
    private String vipDate;
    private String parentUUID;
    private String nc;
    private String txid,headurl;
    private TextView tv_vip;
    private ImageView iv_vip,iv_mine_child_icon;
    private TextView tv_name;
    private TextView tv_erweima,tv_wytg;
    private TextView tv_share,tv_mess_numb;
    private ImageView lin_message,iv_finish;
    private LinearLayout lin_zhgl;
    private LinearLayout lin_hzgl;
    private LinearLayout lin_vip;
    private LinearLayout lin_vip_zd;
    private LinearLayout lin_wytg;
    private LinearLayout lin_share;
    private LinearLayout lin_yhfk;
    private LinearLayout lin_gywm;
    private AffirmDialog affirmDialog;


    @Override
    public View initView() {
        view = View.inflate(getActivity(), R.layout.fragment_mine2, null);
        nc = sp.getString("nc", null);
        headurl = sp.getString("headimage", "");
        initViewData(view);
        return view;
    }

    /**
     * 初始化控件
     */


    public  String isActivity="";
    public  String isActivityflag="";
    public  String ischanner="";
    @Override
    public void iniData() {
        txid = sp.getString("txid", "");
        parentUUID = sp.getString("parentUUID", "");
        childName = sp.getString("childName", "");
        vipDate = sp.getString("viptime", "");
        childuuid = sp.getString("ChildUUID", "");
        isNet = NetworkUtil.isConnected();
        if(TextUtils.isEmpty(isActivity)){
            getVipData();
        }else {
            if(isActivityflag.equals("1")){
                tv_share.setVisibility(View.VISIBLE);
                tv_share.setText(isActivity);
            }else {
                tv_share.setVisibility(View.GONE);
            }
            if(ischanner.equals("1")){
                lin_wytg.setVisibility(View.VISIBLE);
            }
        }
    }
    /**
     * 条目点击事件
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lin_message: //消息列表
                if (TextUtils.isEmpty(parentUUID)) {
                    showNologDialog();
                    return;
                }
                Intent intenta = new Intent(getActivity(), MessageManagerActivity.class);
                startActivity(intenta);
                break;
            case R.id.lin_zhgl:       //账号管理
                if (TextUtils.isEmpty(parentUUID)) {
                    showNologDialog();
                    return;
                }
                Intent intent = new Intent(getActivity(), AccountManagerActivity.class);
                startActivity(intent);
                break;
            case R.id.lin_hzgl://孩子管理
                if (TextUtils.isEmpty(parentUUID)) {
                    showNologDialog();
                    return;
                }
                if (!isNet) {
                    ToastUtils.showToast(MyApplication.getContext(), "网络不通，请检查网络再试");
                    return;
                }
                if (!TextUtils.isEmpty(childuuid)) {
                    Intent intent1 = new Intent(getActivity(), ChildManagerActivity.class);
                    startActivity(intent1);
                } else {
                    showDialog();
                    return;
                }
                break;
            case R.id.lin_vip://vip管理

                Toast.makeText(getActivity(), "公测版，原会员不受影响", Toast.LENGTH_SHORT).show();
//                if (TextUtils.isEmpty(parentUUID)) {
//                    showNologDialog();
//                    return;
//                }
//                Intent intent2 = new Intent(getActivity(), MyNewMemeberActivity.class);
//                startActivity(intent2);
                break;
            case R.id.lin_vip_zd: //充值账单
                if (TextUtils.isEmpty(parentUUID)) {
                    showNologDialog();
                    return;
                }
                Intent intent3=new Intent(getActivity(),OrderHistoryActivity.class);
                startActivity(intent3);
                break;
            case R.id.lin_share: //我要分享
                showShare(1);
                break;
            case R.id.tv_wytg: //我要推广
                showShare(2);
                break;
            case R.id.tv_erweima: //推广二维码
                showPoP();
                break;
            case R.id.lin_yhfk://用户反馈
                if (TextUtils.isEmpty(parentUUID)) {
                    showNologDialog();
                    return;
                }
                if (!isNet) {
                    ToastUtils.showToast(MyApplication.getContext(), "网络不通，请检查网络再试");
                    return;
                }
                Intent intent4 = new Intent(getActivity(), OpinionBackActivity.class);
                startActivity(intent4);
                break;
            case R.id.lin_gywm://关于我们
                Intent intent5 = new Intent(getActivity(), AboutMineActivity.class);
                startActivity(intent5);
                break;
            case R.id.tv_vip:
            case R.id.iv_vip:
                if (TextUtils.isEmpty(parentUUID)) {
                    showNologDialog();
                    return;
                }
                Toast.makeText(getActivity(), "公测版，原会员不受影响", Toast.LENGTH_SHORT).show();
//                Intent intent6 = new Intent(getActivity(), MyNewMemeberActivity.class);
//                startActivity(intent6);
                break;
            case R.id.iv_finish:
                if (TextUtils.isEmpty(parentUUID)) {
                    showNologDialog();
                    return;
                }
                Intent intent7 = new Intent(getActivity(), MineSettingActivity.class);
                startActivity(intent7);
                break;
            default:
                break;
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        boolean headrefresh = sp.getBoolean("headrefresh", false);
        if(headrefresh){
            String headimages = sp.getString("headimage", null);
            Glide.with(this).load(headimages).transform(new GlideCircleTransform(getActivity())).diskCacheStrategy(DiskCacheStrategy.ALL).error(R.drawable.mine_head_02).into(iv_mine_child_icon);
            sp.edit().putBoolean("headrefresh",false).commit();//修改头像出去刷新我的界面
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (TextUtils.isEmpty(parentUUID)) {
            tv_vip.setText("立即开通");
            iv_vip.setImageResource(R.drawable.sy_vip_off);
            tv_name.setText("你还未进行登陆");
        }else {
            checkVip();
            getMessageNumb();
        }
    }

    /**
     * 检查vip信息
     */
    private void checkVip() {
        VipRootBean vipRoot = ((MainActivity) getActivity()).getVipRoot();
        if(vipRoot!=null){
            frashView(vipRoot);
        }
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
    @Override
    public void onDestroy() {
        super.onDestroy();
        ShareSDK.stopSDK(getActivity());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1004){
            nc = sp.getString("nc", "");
            tv_name.setText(nc+"");
        }
    }


    private void getVipData() {
        String url= PHP_URL+"v"+ AppUtils.getVerName()+"/getShareDesc";
        Log.e("分享有礼", "url:" + url);
        StringRequest postsr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("-------------分享有礼",response);
                ShareBean shareBean =null;
                try {
                    shareBean =  new Gson().fromJson(response, ShareBean.class);
                }catch (Exception e){
                    tv_share.setVisibility(View.GONE);
                    return;
                }

                if(shareBean.code.equals("1")){
                    if(shareBean.data.isActivity.equals("1")){
                        tv_share.setVisibility(View.VISIBLE);
                        tv_share.setText(shareBean.data.describe);
                        isActivity=shareBean.data.describe;
                        isActivityflag="1";
                    }else {
                        tv_share.setVisibility(View.GONE);
                    }
                    if(shareBean.data.ischannel.equals("1")){
                        lin_wytg.setVisibility(View.VISIBLE);
                        ischanner="1";
                    }

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

    /**
     * 获取提示消息条数{"code":1,"msg":"\u6210\u529f","data":{"count":0}}
     */
    private void getMessageNumb() {
        String url= PHP_URL+"v"+ AppUtils.getVerName()+"/isReadNews";
        Log.e("获取提示消息条数", "url:" + url);
        StringRequest postsr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("-------------获取提示消息条数",response);
                MessageNumberRoot messageNumberRoot =null;
                try{
                    messageNumberRoot = new Gson().fromJson(response, MessageNumberRoot.class);
                }catch (Exception e){
                    tv_mess_numb.setVisibility(View.GONE);
                    return;
                }
                if(messageNumberRoot.data.count==0){
                    tv_mess_numb.setVisibility(View.GONE);
                }else {
                    tv_mess_numb.setVisibility(View.VISIBLE);
                    if(messageNumberRoot.data.count<=99){
                        tv_mess_numb.setText(messageNumberRoot.data.count+"");
                    }else {
                        tv_mess_numb.setText("99+");
                    }
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
                map.put("userid",parentUUID);
                return map;
            }
        };
        postsr.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.getRequestQueue().add(postsr);
    }

    private void initViewData(View view) {
        tv_name= (TextView) view.findViewById(R.id.tv_child_name);
        tv_vip= (TextView) view.findViewById(R.id.tv_vip);
        tv_erweima= (TextView) view.findViewById(R.id.tv_erweima);
        tv_wytg= (TextView) view.findViewById(R.id.tv_wytg);
        tv_share= (TextView) view.findViewById(R.id.tv_share);
        tv_mess_numb= (TextView) view.findViewById(R.id.tv_mess_numb);

        iv_vip= (ImageView) view.findViewById(R.id.iv_vip);
        iv_mine_child_icon= (ImageView) view.findViewById(R.id.iv_mine_child_icons);
        lin_message= (ImageView) view.findViewById(R.id.lin_message);
        iv_finish= (ImageView) view.findViewById(R.id.iv_finish);
        lin_zhgl= (LinearLayout) view.findViewById(R.id.lin_zhgl);
        lin_hzgl= (LinearLayout) view.findViewById(R.id.lin_hzgl);
        lin_vip= (LinearLayout) view.findViewById(R.id.lin_vip);
        lin_vip_zd= (LinearLayout) view.findViewById(R.id.lin_vip_zd);
        lin_wytg= (LinearLayout) view.findViewById(R.id.lin_wytg);
        lin_share= (LinearLayout) view.findViewById(R.id.lin_share);
        lin_yhfk= (LinearLayout) view.findViewById(R.id.lin_yhfk);
        lin_gywm= (LinearLayout) view.findViewById(R.id.lin_gywm);
        String headimage = sp.getString("headimage", null);
        if(headimage!=null){
            Glide.with(this).load(headimage).transform(new GlideCircleTransform(getActivity())).diskCacheStrategy(DiskCacheStrategy.ALL).error(R.drawable.mine_head_02).into(iv_mine_child_icon);
        }
        if(nc!=null){
            tv_name.setText(nc);
        }

        lin_message.setOnClickListener(this);
        lin_zhgl.setOnClickListener(this);
        iv_finish.setOnClickListener(this);
        lin_hzgl.setOnClickListener(this);
        lin_vip.setOnClickListener(this);
        lin_vip_zd.setOnClickListener(this);
        lin_share.setOnClickListener(this);
        lin_yhfk.setOnClickListener(this);
        lin_gywm.setOnClickListener(this);
        tv_erweima.setOnClickListener(this);
        tv_wytg.setOnClickListener(this);
        tv_vip.setOnClickListener(this);
        iv_vip.setOnClickListener(this);
    }

    /**
     * 推广二维码
     */
    private void showPoP() {
//        final AlertDialog ad=new AlertDialog.Builder(getActivity()).create();
//        ad.show();
//        Window window = ad.getWindow();
//        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));  // 有白色背景，加这句代码
//        View view= LayoutInflater.from(getActivity()).inflate(R.layout.weweimadialog,null);
//        ImageView iv_img = (ImageView) view.findViewById(R.id.iv_img);
//        String url = Constants.FIND_URL+"YczH5/index.php?code=" + parentUUID;
//        Bitmap mBitmap = CodeUtils.createImage(url,  DensityUtil.dip2px(getActivity(),200),  DensityUtil.dip2px(getActivity(),200), BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.my_icon));
//        iv_img.setImageBitmap(mBitmap);
//        window.setContentView(view);

        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity(),R.style.MyAlertDialog);
        View dialogView =  View.inflate(getActivity(), R.layout.weweimadialog, null);
        ImageView iv_img = (ImageView) dialogView.findViewById(R.id.iv_img);
        String url = Constants.FIND_URL+"YczH5/index.php?code=" + parentUUID;
        Bitmap mBitmap = CodeUtils.createImage(url,  DensityUtil.dip2px(getActivity(),200),  DensityUtil.dip2px(getActivity(),200), BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.sy_hh_on));
        iv_img.setImageBitmap(mBitmap);
        builder.setView(dialogView);
        builder.show();
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
            oks.setTitleUrl(Constants.PHP_URL+"h5/Agent/index?code="+parentUUID);
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
            oks.setTitleUrl(Constants.PHP_URL+"h5/Agent/index?code="+parentUUID);
        }
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        //oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        if(type==1){
            oks.setSiteUrl("http://www.ycz365.com/");
        }else {
            oks.setTitleUrl(Constants.PHP_URL+"h5/Agent/index?code="+parentUUID);
        }
        oks.setCallback(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                LogUtil.e("setCallback","分享成功........................................");
                sp.edit().putString("shareApp","shareApp").commit();
                if(type==1){
                    showResult();
                }
            }
            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                LogUtil.e("setCallback","分享失败........................................");
            }
            @Override
            public void onCancel(Platform platform, int i) {
                LogUtil.e("setCallback","分享取消........................................");
            }
        });
        // 启动分享GUI
        oks.show(getActivity());
    }
    /**
     * 分享有礼回调
     */
    private void showResult()  {
        String parentUUID1 = sp.getString("parentUUID", "");
        String url= PHP_URL+"v"+ AppUtils.getVerName()+"/sharing";
        Log.e("分享回调", "url:" + url);
        StringRequest postsr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("---分享回调response：",response);
                ShareResultBean shareBean =null;
                try{
                    shareBean = new Gson().fromJson(response, ShareResultBean.class);
                }catch (Exception e){
                    return;
                }
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
    /**
     * 显示未登录对话框
     * */
    private TextView tvAddChlid;
    private TextView tvFinish;
    private ImageView ivAddFinish;
    private android.support.v7.app.AlertDialog showStartTime;
    public void showNologDialog(){
        android.support.v7.app.AlertDialog.Builder builder=new android.support.v7.app.AlertDialog.Builder(getActivity(),R.style.MyAlertDialog);
        //builder.setCancelable(false);
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
     * 显示添加孩子对话框
     * */
    public void showDialog(){
        android.support.v7.app.AlertDialog.Builder builder=new android.support.v7.app.AlertDialog.Builder(getActivity(),R.style.MyAlertDialog);
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
}
