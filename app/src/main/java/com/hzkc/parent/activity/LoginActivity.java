package com.hzkc.parent.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.google.gson.Gson;
import com.hzkc.parent.Bean.CheckIsXXTData;
import com.hzkc.parent.Bean.LoginE;
import com.hzkc.parent.Bean.Student;
import com.hzkc.parent.Bean.YzCodeMsgInfo;
import com.hzkc.parent.MainActivity;
import com.hzkc.parent.R;
import com.hzkc.parent.appcation.MyApplication;
import com.hzkc.parent.event.InitEvent;
import com.hzkc.parent.event.LoginDataEvent;
import com.hzkc.parent.event.RegiseterDataEvent;
import com.hzkc.parent.event.RequestLoginEvent;
import com.hzkc.parent.event.RequestRegisterEvent;
import com.hzkc.parent.mina.Constants;
import com.hzkc.parent.solider.activity.MainSoldActivity;
import com.hzkc.parent.utils.AppUtils;
import com.hzkc.parent.utils.LogUtil;
import com.hzkc.parent.utils.NetworkUtil;
import com.hzkc.parent.utils.SignatureAlgorithm;
import com.hzkc.parent.utils.ToastUtils;
import com.hzkc.parent.view.IdentitySelectedDialog;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pl.droidsonroids.gif.GifImageView;
import pub.devrel.easypermissions.EasyPermissions;


public class LoginActivity extends BasesActivity implements View.OnClickListener, IdentitySelectedDialog.onIdentitySelectListener {
    private TextView btLoginRegiste;
    private TextView btLogin;
    private TextView btLoginFindPassword;
    private TextView tv_mm,tv_dh,tv_look;
    private EditText phoneNum;
    private EditText psw;
    private View view_1,view_2;
    private boolean flag;
    private long BackTime = 0;
    private String mPhoneNum;
    private String mPsw;
    private ProgressDialog pd;
    private GifImageView gif;
    private boolean regFlag;
    private IdentitySelectedDialog identityDialog;
    private ImageView iv_delete,iv_eye,iv_eye_on;
    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new LoginActivity.MyLocationListener();
    /**
     * 倒数计时
     */
    Handler hd = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 106:
                    if (!regFlag) {
                        toast("网络连接不通畅,请稍后再试");
                    }
                    if(pd!=null&&pd.isShowing()){
                        pd.dismiss();
                    }
                    break;
                case 107:
                    tv_mm.setVisibility(View.VISIBLE);
                    break;
                default:
                    break;
            }
        }
    };
    private Runnable runnable;
    private String xxtnc;

    private LinearLayout linear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            try {
                Class decorViewClazz = Class.forName("com.android.internal.policy.DecorView");
                Field field = decorViewClazz.getDeclaredField("mSemiTransparentStatusBarColor");
                field.setAccessible(true);
                field.setInt(getWindow().getDecorView(), Color.TRANSPARENT);  //改为透明
            } catch (Exception e) {}
        }
        setContentView(R.layout.activity_new_login);

        initView();
        initlocation();
        runnable = new Runnable() {
            @Override
            public void run() {
                Message msg = Message.obtain();
                msg.what = 106;
                hd.sendMessage(msg);
            }
        };
    }


    public RadioButton iv_ycx,iv_xxt;
    public String deviceToken;
    private void initView() {
        iv_ycx = (RadioButton) findViewById(R.id.iv_ycz);
        iv_xxt = (RadioButton) findViewById(R.id.iv_xxt);
        btLoginRegiste = (TextView) findViewById(R.id.bt_login_registe);
        btLoginFindPassword = (TextView) findViewById(R.id.bt_login_find_password);
        btLogin = (TextView) findViewById(R.id.bt_login);
        tv_mm = (TextView) findViewById(R.id.tv_mima);
        tv_dh = (TextView) findViewById(R.id.tv_dh);
        tv_look = (TextView) findViewById(R.id.tv_look);
        iv_delete = (ImageView) findViewById(R.id.iv_delete);
        iv_eye = (ImageView) findViewById(R.id.iv_eye);
        iv_eye_on = (ImageView) findViewById(R.id.iv_eye_on);
        view_1 = findViewById(R.id.view_1);
        view_2 = findViewById(R.id.view_2);
        phoneNum = (EditText) findViewById(R.id.et_login_phone_num);
        psw = (EditText) findViewById(R.id.et_login_phone_psw);
        deviceToken = sp.getString("deviceToken", "");
        sp.edit().putString("childName", "")
                .putString("ChildUUID", "")
                .putString("childSex", "")
                .putString("token", "")
                .putString("loginImFlag", "")
                .putString("childVersion", "")
                .putString("loginImFlag","")
                .putString("nc", "").commit();
        String myNum = sp.getString("phoneNum", "");
        regFlag = false;
        phoneNum.setText(myNum);
        btLogin.setOnClickListener(this);
        btLoginFindPassword.setOnClickListener(this);
        btLoginRegiste.setOnClickListener(this);
        iv_eye.setOnClickListener(this);
        iv_eye_on.setOnClickListener(this);
        iv_delete.setOnClickListener(this);
        tv_look.setOnClickListener(this);
        iv_xxt.setOnClickListener(this);
        iv_ycx.setOnClickListener(this);
        phoneNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tv_dh.setVisibility(View.INVISIBLE);
                if(s.length()==0){
                    iv_delete.setVisibility(View.GONE);
                }else {
                    iv_delete.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()==11){
                    iv_delete.setVisibility(View.VISIBLE);
                }
            }
        });
        psw.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tv_mm.setVisibility(View.INVISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    /**
     * 吐司的一个小方法
     */
    private void toast(final String str) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtils.showToast(MyApplication.getContext(), str);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!AppUtils.isCameraCanUse()){
            requestPermissions();
        }
    }

    /**
     * 获取权限
     * */
    private void requestPermissions() {
        //要获取的权限
        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.SYSTEM_ALERT_WINDOW,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.READ_CONTACTS};
        if (EasyPermissions.hasPermissions(this, perms)) {
            // Have permissions, do the thing!
            LogUtil.e("已获取权限");
        } else {
            EasyPermissions.requestPermissions(this, "应用运行必要的权限", 0, perms);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
        LogUtil.e("----------------------------","-------------------------");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_login_find_password://忘记密码
                Intent intent1 = new Intent(this, FindPasswordActivity.class);
                startActivity(intent1);
                break;
            case R.id.bt_login_registe://注册
                Intent intent2 = new Intent(this, RegisterUserActivity.class);
                intent2.putExtra("reg","0");
                startActivity(intent2);
                break;
            case R.id.bt_login://登录
                login();
                break;
            case R.id.iv_delete://清空
                phoneNum.getText().clear();
                break;
            case R.id.tv_look://逛逛
                Intent intent3 = new Intent(this, MainActivity.class);
                startActivity(intent3);
                break;
            case R.id.iv_eye://查看密码
                iv_eye_on.setVisibility(View.VISIBLE);
                iv_eye.setVisibility(View.GONE);
                psw.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                break;
            case R.id.iv_eye_on://查看密码
                iv_eye_on.setVisibility(View.GONE);
                iv_eye.setVisibility(View.VISIBLE);
                psw.setTransformationMethod(PasswordTransformationMethod.getInstance());
                break;
            case R.id.iv_ycz:
                view_1.setBackgroundResource(R.color.textblack);
                view_2.setBackgroundResource(R.color.textblack);
                break;
            case R.id.iv_xxt:
                view_1.setBackgroundResource(R.color.textblusse);
                view_2.setBackgroundResource(R.color.textblusse);
                break;
            default:
                break;
        }
    }

    /**
     * 登录逻辑
     */
    private void login() {
        mPhoneNum = phoneNum.getText().toString().trim();
        sp.edit().putString("phoneNum1", mPhoneNum).commit();
        mPsw = psw.getText().toString().trim();
        if (!TextUtils.isEmpty(mPhoneNum)) {
            //定义需要匹配的正则表达式的规则
            String REGEX_MOBILE_SIMPLE = "[1][23456789]\\d{9}";
            //把正则表达式的规则编译成模板
            Pattern pattern = Pattern.compile(REGEX_MOBILE_SIMPLE);
            //把需要匹配的字符给模板匹配，获得匹配器
            Matcher matcher = pattern.matcher(mPhoneNum);
            // 通过匹配器查找是否有该字符，不可重复调用重复调用matcher.find()
            if (!matcher.find()) {//匹配手机号是否存在
//                toast("手机号格式错误");
                tv_dh.setVisibility(View.VISIBLE);
                return;
            }
        } else {
            toast("请先输入手机号");
            return;
        }
        if(mPsw.contains("\"")){
//            toast("输入密码有非法字符\"");
            tv_mm.setVisibility(View.VISIBLE);
            return;
        }
        if(mPsw.contains("\\")){
//            toast("输入密码有非法字符\\");
            tv_mm.setVisibility(View.VISIBLE);
            return;
        }
        if (TextUtils.isEmpty(mPsw)) {
//            toast("密码不能为空");
            tv_mm.setVisibility(View.VISIBLE);
            return;
        } else if (psw.length() < 6) {
//            toast("密码长度不能低于6位");
            tv_mm.setVisibility(View.VISIBLE);
            return;
        }
        boolean isNet = NetworkUtil.isConnected();
        if (!isNet) {
            toast("网络不通，请检查网络再试");
            return;
        }
//        if(System.currentTimeMillis() - exitTime > 5000) {
//            exitTime = currentTimeMillis();
//        } else {
//            toast("正在等待服务器数据，请稍候...");
//            return;
//        }
        requestPhoneAndPwd(mPhoneNum, mPsw);
        hd.postDelayed(runnable, 10000);
    }

    /**
     * 控制当前密码的显示与隐藏
     */
    private void setShowNOwpsw() {
        flag = !flag;
        if (flag) {
            //如果选中，显示密码
            psw.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            //ivShowNowPassword.setImageResource(R.drawable.btn_eye_on);
        } else {
            //否则隐藏密码
            psw.setTransformationMethod(PasswordTransformationMethod.getInstance());
            //ivShowNowPassword.setImageResource(R.drawable.btn_eye_off);
        }
        //设置光标到最后一位
        psw.setSelection(psw.getText().length());
        //设置焦点
        psw.requestFocus();
    }

    /**
     * 向服务器发送和获取数据
     */
    private void requestPhoneAndPwd(String phoneNum, String pwd) {
        if(pd!=null&&pd.isShowing()){
            pd.dismiss();
        }
        regFlag = false;
        pd = new ProgressDialog(this);
        //pd.setTitle("提示");
        pd.setMessage("正在登录中，请稍候");
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setCancelable(false);
        pd.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                    pd.dismiss();
                }
                return false;
            }
        });
        pd.show();
        if(iv_ycx.isChecked()) {   //若是优成长用户优成长登陆
            EventBus.getDefault().post(new RequestLoginEvent(phoneNum, pwd,deviceToken,locations), "requestLogin");
        }else {
            checkIsXXT();
        }
    }

    /**
     * 请求登录
     */
    @Subscriber(mode = ThreadMode.ASYNC, tag = "LoginData")
    public void RequestLoginEvent(LoginDataEvent data) {
        String isRegistered = data.isRegistered;
        LoginE loginE = new Gson().fromJson(data.e,LoginE.class);
        LogUtil.e(TAG, "RequestLoginEvent: 请求登录");
        regFlag = true;
        hd.removeCallbacks(runnable);
        if (isRegistered.equals("0") ) {   //登陆成功，
            EventBus.getDefault().post(new InitEvent(data.parentUUID), "init");
            try {
                getNc(data.parentUUID,loginE.a,loginE.f);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else if(isRegistered.equals("1")){//用户名错误
            pd.dismiss();
            if(iv_ycx.isChecked()){   //若是优成长用户优成长登陆
                this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv_mm.setVisibility(View.VISIBLE);
                        tv_mm.setText("登录失败，用户名不存在");
                    }
                });
                pd.dismiss();
            }else {                                      //若是校讯通用户校讯通登陆，先调用校讯通，有就掉登陆，登陆失败，检查不存在用户，是就在这边注册，密码错误就成共登陆
                regXXTuser(name);
            }
        } else if(isRegistered.equals("2")){//密码错误
            pd.dismiss();
            if(iv_xxt.isChecked()) {   //若是校讯通用户优成长登陆
                try {
                    getNc(data.parentUUID,"","");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            } else {
                this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv_mm.setVisibility(View.VISIBLE);
                        tv_mm.setText("登录失败，密码错误");
                    }
                });
            }
        } else if(isRegistered.equals("3")){//已经登录
            pd.dismiss();
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tv_mm.setVisibility(View.VISIBLE);
                    tv_mm.setText("登录失败，已经登录");
                }
            });
        } else {//未知（其他错误）
            toast("登录失败,错误未知");
            pd.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        if(pd!=null&&pd.isShowing()){
            pd.dismiss();
        }
        regFlag=true;
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        EventBus.getDefault().register(this);
        super.onStart();
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }
    @Override
    public void onBackPressed() {
        if(System.currentTimeMillis() - BackTime > 2000) {
            toast("再按一次退出程序");
            BackTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }
    /**
     * 获取昵称
     * */
    private void getNc(final String userid,final String name,final String headurl) throws UnsupportedEncodingException {

        String txid=mPhoneNum.substring(mPhoneNum.length()-2,mPhoneNum.length());
        toast("登录成功，正在进入主界面");
        LogUtil.e("------------headimage",headurl);//http://119.23.38.125:8086/images/headimg/parent/15406938144073.jpg//headImg":"http:\/\/119.23.38.125:8086\/images\/headimg\/parent\/15406941858735.jpg
        Date d = new Date();//http://119.23.38.125:8086/images/headimg/parent/15406942543693.jpg
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = sdf.format(d);
        sp.edit().putString("phoneNum", mPhoneNum)
                .putString("parentUUID", userid)
                .putString("phonePsw", mPsw)
                .putString("token", "token")
                .putString("regitTime",time)
                .putString("nc",name)
                .putString("txid",txid)
                .putString("headimage",headurl)
                .commit();

        //登陆成功  因为登陆成功之后有时会出现初始化数据还未获得   所以加一个延迟
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                pd.dismiss();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        },1500);
//        showPopWindow();

    }

    private void showPopWindow() {
        identityDialog = new IdentitySelectedDialog(this);
        identityDialog.setSelectListener(this);
        if (identityDialog != null) {
            identityDialog.initIdentity();
            try {
                identityDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public void identityClick(int index) {
        if(index==0){
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            sp.edit().putString("apptype", "jz").commit();
            finish();
        }else if(index==1){
            toast("登录成功，正在进入主界面");
            Intent intent = new Intent(LoginActivity.this, MainSoldActivity.class);
            startActivity(intent);
            sp.edit().putString("apptype", "wj").commit();
            finish();
        }else if(index==2){
            sp.edit().putString("phoneNum", "")
                    .putString("parentUUID", "")
                    .putString("phonePsw", "")
                    .putString("token", "")
                    .putString("regitTime","")
                    .putString("nc","")
                    .putString("txid","")
                    .commit();
        }
    }



    /**
     * 判断是否是校讯通账户
     * */
    private String name;
    private void checkIsXXT() {
        CheckIsXXTData data = new CheckIsXXTData();
        data.setMobtel(mPhoneNum);
        data.setPwd(mPsw);
        String mjson = new Gson().toJson(data);

        String token = SignatureAlgorithm.getMD5(Constants.type + mjson + Constants.sessionId);
        String url = Constants.CHECK_USER_ISXXT+"sessionId="+Constants.sessionId+"&token="+token+"&type="+Constants.type+"&data="+mjson;
        Log.e("checkIsXXT","url:"+url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        LogUtil.e("checkIsXXT","response:"+response);
                        final YzCodeMsgInfo msg = new Gson().fromJson(response, YzCodeMsgInfo.class);
                        Student ss = new Gson().fromJson(msg.getResult(), Student.class);
                        regFlag = true;
                        if(msg.getCode().equals("0")){//是校讯通账户  直接掉c++登录接口，若返回为账号不存在，就注册，返回密码错误，就登录成功
                            pd.dismiss();
                            if(TextUtils.isEmpty(ss.Name)){
                                ss.Name="YCZ"+mPhoneNum.substring(mPhoneNum.length()-4,mPhoneNum.length());
                            }
                            name=ss.Name;
                            EventBus.getDefault().post(new RequestLoginEvent(mPhoneNum, mPsw,deviceToken,locations), "requestLogin");
                        }else{
                            pd.dismiss();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tv_mm.setVisibility(View.VISIBLE);
                                    tv_mm.setText("登录失败,"+msg.getMsg());
                                }
                            });
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv_mm.setVisibility(View.VISIBLE);
                        tv_mm.setText("登录失败，用户名不存在");
                    }
                });
            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.getRequestQueue().add(stringRequest);
    }

    /**
     * 注册校讯通账户（在这边的服务器上注册）
     */
    private void regXXTuser(String name) {
        xxtnc = name;
        String phonetype=android.os.Build.MODEL;
        EventBus.getDefault().post(new RequestRegisterEvent(mPhoneNum, mPsw,xxtnc,phonetype,"ycz"), "requestRegiseter");
    }

    /**
     * 请求注册
     */
    @Subscriber(mode = ThreadMode.ASYNC, tag = "RegiseterData")
    public void RequestRegisterEvent(RegiseterDataEvent data) {
        String isRegistered = data.isRegistered;
        LogUtil.e(TAG, "RequestLoginEvent: 请求注册");
        if (isRegistered.equals("0")) {   //0 代表注册成功，
            try {
                sendRegist(data.userid);
                EventBus.getDefault().post(new RequestLoginEvent(mPhoneNum, mPsw,deviceToken,locations), "requestLogin");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            //pd.dismiss();
//            registerIM(data.userid);
        } else if (isRegistered.equals("1")) {//1 代表已经注册过
            pd.dismiss();
            toast("登录失败，出现未知错误");
        } else {// 未知（其他错误）
            pd.dismiss();
            toast("登录失败，出现未知错误");
        }
    }

    /**
     * 每次注册时启动
     */
    private void sendRegist(String userid) throws UnsupportedEncodingException {
        String url = Constants.FIND_URL_API_REG;
        //注册完成接口
        StringRequest stringRequest1 = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        LogUtil.e("注册完成接口", "response:" + response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogUtil.e("注册完成接口", "error:" + error);
            }
        });
        String url2 = Constants.FIND_URL_API_ADDUSER + "userid=" + userid + "&phone=" + mPhoneNum + "&txid=" + mPhoneNum.substring(mPhoneNum.length() - 2)+"&nc="+ URLEncoder.encode(xxtnc,"UTF-8")+"&yqm="+0;
        LogUtil.e("url2:"+url2);
        //通讯数据库中添加数据
        StringRequest stringRequest2 = new StringRequest(Request.Method.GET, url2,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        LogUtil.e("通讯数据库", "Response:" + response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogUtil.e("通讯数据库", "error:" + error);
            }
        });
        //设置超时时间
        stringRequest1.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        stringRequest2.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.getRequestQueue().add(stringRequest1);
        MyApplication.getRequestQueue().add(stringRequest2);
    }
    private void initlocation() {
        mLocationClient=new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(myListener );
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        option.setScanSpan(0);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        mLocationClient.setLocOption(option);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mLocationClient.start();
            }
        },1000);
    }
    public  Handler handler=new Handler();
    public  String locations="";
    //位置监听处理
    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            Log.e("你定位的城市是:",location.getProvince()+","+location.getCity()+","+location.getLatitude()+","+location.getLongitude());
            locations= location.getProvince() + "-" + location.getCity() + "-" +location.getDistrict() ;
        }
    }
}
