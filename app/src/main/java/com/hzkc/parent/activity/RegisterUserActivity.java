package com.hzkc.parent.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.hzkc.parent.Bean.SMSErrorMsg;
import com.hzkc.parent.Bean.YZCodeData;
import com.hzkc.parent.Bean.YzCodeMsgInfo;
import com.hzkc.parent.R;
import com.hzkc.parent.appcation.MyApplication;
import com.hzkc.parent.event.RegiseterDataEvent;
import com.hzkc.parent.event.RequestRegisterEvent;
import com.hzkc.parent.mina.Constants;
import com.hzkc.parent.utils.LogUtil;
import com.hzkc.parent.utils.NetworkUtil;
import com.hzkc.parent.utils.SignatureAlgorithm;
import com.hzkc.parent.utils.ToastUtils;
import com.jaeger.library.StatusBarUtil;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class RegisterUserActivity extends BasesActivity implements View.OnClickListener {
    private TextView tvTopTitle;
    private ImageView ivFinish;
    private EditText etPhoneNum;
    private EditText etYzm;
    private TextView btGetYzm;
    private TextView btRegistSubmit;
    private TextView tvUserPlan;
    private CheckBox cbUserPlan;
    private TimerTask tt;
    private Timer tm;
    private String pwd,pwd2;
    private EditText password,et_password_2;
    private boolean regFlag;

    private boolean flag1;
    private boolean flag2;

    private int TIME = 60;//倒计时60s
    public String country = "86";//中国区号
    private String phone;
    private static final int CODE_REPEAT = 1; //重新发送

    private boolean isUseMob=false;
    /**
     * 倒数计时
     */
    Handler hd = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CODE_REPEAT:
                    btGetYzm.setEnabled(true);
                    btRegistSubmit.setEnabled(true);
                    tm.cancel();//取消任务
                    tt.cancel();//取消任务
                    TIME = 60;//时间重置
                    btGetYzm.setText("重新发送验证码");
                    btGetYzm.setTextColor(getResources().getColor(R.color.newcolor));
                    break;
                case 106:
                    if (!regFlag) {
                        //llDownload.setVisibility(View.GONE);
                        toast("网络连接不通畅,请稍后再试");
                    }
                    if(pd!=null&&pd.isShowing()){
                        pd.dismiss();
                    }
                    break;
                default:
                    btGetYzm.setText("重新获取"+"("+TIME + "s)");
                    btGetYzm.setTextColor(getResources().getColor(R.color.text_shallow_color));
                    break;
            }
        }
    };
    /**
     * 短信验证回调
     */
    EventHandler eh = new EventHandler() {
        @Override
        public void afterEvent(int event, int result, Object data) {
            if (result == SMSSDK.RESULT_COMPLETE) {
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                    //处理网络请求数据
                    doNetRequest();
                    //toast("验证成功，正在进入主界面");
                } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {//获取验证码成功
                    //toast("验证成功，正在进入主界面");
                    toast("获取验证码成功");
                } else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {//如果你调用了获取国家区号类表会在这里回调
                    //返回支持发送验证码的国家列表
                }
            } else {//错误等在这里（包括验证失败）
                //错误码请参照http://wiki.mob.com/android-api-错误码参考/这里我就不再继续写了
                ((Throwable) data).printStackTrace();
                String jsonMsg=data.toString().substring(data.toString().indexOf("{"));
                LogUtil.e("短信验证回调","短信验证回调:"+jsonMsg);
                try{
                    SMSErrorMsg msg = new Gson().fromJson(jsonMsg, SMSErrorMsg.class);
                    String str = msg.getDetail();
                    LogUtil.e("短信验证回调","短信验证回调:"+str);
                    toast(str);
                }catch (Exception e){
                    e.printStackTrace();
                    toast("验证错误,请稍后再试");
                }
                regFlag = true;
                if(pd!=null&&pd.isShowing()){
                    pd.dismiss();
                }
                hd.removeCallbacks(runnable);
            }
        }
    };
    private String reg;
    private ProgressDialog pd;
    private Runnable runnable;
    private String codeResult;
    private String phonetype;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_regist);
        StatusBarUtil.setColorNoTranslucent(this,getResources().getColor(R.color.newcolor));
        EventBus.getDefault().register(this);
        SMSSDK.registerEventHandler(eh); //注册短信回调（记得销毁，避免泄露内存）
        phonetype=android.os.Build.MODEL+";"+android.os.Build.VERSION.RELEASE;
        initView();


        runnable = new Runnable() {
            @Override
            public void run() {
                Message msg = Message.obtain();
                msg.what = 106;
                hd.sendMessage(msg);
            }
        };
    }

    /**
     * 初始化数据
     */
    private void initView() {
        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        ivFinish = (ImageView) findViewById(R.id.iv_finish);
        etPhoneNum = (EditText) findViewById(R.id.et_phone_num);
        etYzm = (EditText) findViewById(R.id.et_yzm);
        btGetYzm = (TextView) findViewById(R.id.bt_get_yzm);
        btRegistSubmit = (TextView) findViewById(R.id.bt_regist_submit);
        cbUserPlan = (CheckBox) findViewById(R.id.cb_user_plan);
        tvUserPlan = (TextView) findViewById(R.id.tv_user_plan);
        password = (EditText) findViewById(R.id.et_password);
        et_password_2 = (EditText) findViewById(R.id.et_password_2);
        ivFinish.setVisibility(View.VISIBLE);
        reg = getIntent().getStringExtra("reg");
        regFlag = false;
        //初始化学生的状态
        flag1 = false;
        flag2 = false;

        codeResult="";
        //代理邀请暂时不显示

        tvTopTitle.setText("用户注册");
        ivFinish.setOnClickListener(this);
        btGetYzm.setOnClickListener(this);
        btRegistSubmit.setOnClickListener(this);
        tvUserPlan.setOnClickListener(this);
    }

    
    public int clicknumb=0;
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_finish:
                LogUtil.e("iv_finish", reg);
                if (reg.equals("1")) {
                    Intent intent = new Intent(RegisterUserActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
                finish();
                break;
            case R.id.bt_get_yzm://获取短信验证码
                if(clicknumb<2){
                    clicknumb++;
                    getMsgCode();
                }else {
                    Toast.makeText(this, "获取验证码异常，请直接跳过验证码步骤进行注册登陆", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.bt_regist_submit://提交注册信息
                submitRegistMsg();
                break;
            case R.id.tv_user_plan://进入用户协议界面
                Intent intent1 = new Intent(RegisterUserActivity.this, MyUserAgreementActivity.class);
                startActivity(intent1);
                break;
            default:
                break;
        }
    }

    /**
     * 提交注册信息和验证码
     */
    private void submitRegistMsg() {
        //获得用户输入的验证码
        phone = etPhoneNum.getText().toString().trim();
        String code = etYzm.getText().toString().trim();
        pwd = password.getText().toString().trim();
        pwd2=  et_password_2.getText().toString().trim();
        if (!TextUtils.isEmpty(phone)) {
            //定义需要匹配的正则表达式的规则
            String REGEX_MOBILE_SIMPLE = "[1][123456789]\\d{9}";
            //把正则表达式的规则编译成模板
            Pattern pattern = Pattern.compile(REGEX_MOBILE_SIMPLE);
            //把需要匹配的字符给模板匹配，获得匹配器
            Matcher matcher = pattern.matcher(phone);
            // 通过匹配器查找是否有该字符，不可重复调用重复调用matcher.find()
            if (!matcher.find()) {//匹配手机号是否存在
                toast("手机号格式错误");
                return;
            }
        } else {
            toast( "请先输入手机号");
            return;
        }
        if (TextUtils.isEmpty(pwd)) {
            toast("当前密码为空");
            return;
        }
        if (!pwd.equals(pwd2)) {
            toast("两次输入的密码不一致，请重新输入");
            return;
        }
        if (pwd.length()<6) {
            toast("请输入6位以上的密码");
            return;
        }
        if (pwd.contains("\"")) {
            toast("输入密码有非法字符\"");
            return;
        }
        if (pwd.contains("\\")) {
            toast("输入密码有非法字符\\");
            return;
        }
        boolean isNet = NetworkUtil.isConnected();
        if (!isNet) {
            toast("网络不通，请检查网络再试");
            return;
        }
        if(!cbUserPlan.isChecked()){
            toast("请确认用户协议");
            return;
        }

        if(pd!=null&&pd.isShowing()){
            pd.dismiss();
        }
        pd = new ProgressDialog(this);
        pd.setMessage("正在注册中，请稍候");
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setCancelable(false);
        pd.show();

        if (!TextUtils.isEmpty(code)) {//判断验证码是否为空
            if(isUseMob){
                //验证
                SMSSDK.submitVerificationCode(country, phone, code);
            }else{
                UseHongJinCheck(phone,code);
            }
        } else {//如果用户输入的内容为空，提醒用户
            if(clicknumb>=2){
                UseHongJinCheck(phone,code);
            }else {
                toast("请输入验证码后再提交");
                pd.dismiss();
                return;
            }
        }
        //doNetRequest();
        hd.postDelayed(runnable, 5000);
    }

    /**
     * 获取验证码
     */
    private void getMsgCode() {
        phone = etPhoneNum.getText().toString().trim();
        boolean isNet = NetworkUtil.isConnected();
        if (!isNet) {
            toast("网络不通，请检查网络再试");
            return;
        }
        if (!TextUtils.isEmpty(phone)) {
            //定义需要匹配的正则表达式的规则
            String REGEX_MOBILE_SIMPLE = "[1][23456789]\\d{9}";
            //把正则表达式的规则编译成模板
            Pattern pattern = Pattern.compile(REGEX_MOBILE_SIMPLE);
            //把需要匹配的字符给模板匹配，获得匹配器
            Matcher matcher = pattern.matcher(phone);
            // 通过匹配器查找是否有该字符，不可重复调用重复调用matcher.find()
            if (matcher.find()) {//匹配手机号是否存在
                alterWarning();
            } else {
                toast("手机号格式错误");
            }
        } else {
            toast("请先输入手机号");
        }
    }

    /**
     * 弹出对话框
     */
    private void alterWarning() {
        // 2. 通过sdk发送短信验证
        AlertDialog.Builder builder = new AlertDialog.Builder(this);  //先得到构造器
        builder.setTitle("提示"); //设置标题
        builder.setMessage("我们将要发送到" + phone + "验证"); //设置内容
        builder.setIcon(R.drawable.icon);//设置图标，图片id即可
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            //设置确定按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss(); //关闭dialogqdsadsaddasdasdawdawd
                // 2. 通过sdk发送短信验证（请求获取短信验证码，在监听（eh）中返回）
                if(isUseMob){//使用mob的短信验证
                    SMSSDK.getVerificationCode(country, phone);
                }else{//使用公司的短信验证
                    useHongJinSend(phone);
                }
                //做倒计时操作
                //Toast.makeText(RegisterUserActivity.this, "已发送" + which, Toast.LENGTH_SHORT).show();
                btGetYzm.setEnabled(false);
                btRegistSubmit.setEnabled(true);
                tm = new Timer();
                tt = new TimerTask() {
                    @Override
                    public void run() {
                        hd.sendEmptyMessage(TIME--);
                    }
                };
                tm.schedule(tt, 0, 1000);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() { //设置取消按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        //参数都设置完成了，创建并显示出来
        builder.create().show();
    }
    /**
     * 获取公司的短信验证码
     * */
    private void useHongJinSend(String phone) {
        YZCodeData data = new YZCodeData();
        data.setMobtel(phone);
        data.setContent("【优成长】此验证码用于注册或忘记密码，请勿泄露。");
        String mjson = new Gson().toJson(data);

        String token = SignatureAlgorithm.getMD5(Constants.type + mjson + Constants.sessionId);
        String url = Constants.REG_SEND_YZCODE+"sessionId="+Constants.sessionId+"&token="+token+"&type="+Constants.type+"&data="+mjson;

        LogUtil.e("useHongJinSend","url:"+url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        LogUtil.e("useHongJinSend","response:"+response);
                        YzCodeMsgInfo msg = new Gson().fromJson(response, YzCodeMsgInfo.class);
                        if(msg.getMsg().trim().equals("成功")){
                            toast("获取验证码成功");
                            codeResult = msg.getResult();
                        }else{
                            toast("获取验证码失败,原因:"+msg.getMsg());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                toast("获取验证码失败");
            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.getRequestQueue().add(stringRequest);


    }
    /**
     * 使用公司的验证码进行验证
     * */
    private void UseHongJinCheck(String phone, final String code) {
        if(codeResult.equals(code)){
            doNetRequest();
        }else{
            if(clicknumb>=2){
                doNetRequest();
            }else {
                toast("输入验证码错误");
                hd.removeCallbacks(runnable);
            }
        }
    }

    /**
     * 销毁短信注册
     */
    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        if(pd!=null&&pd.isShowing()){
            pd.dismiss();
        }
        regFlag = true;
        SMSSDK.unregisterEventHandler(eh);
        super.onDestroy();
    }

    /**
     * 监听返回
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (reg.equals("1")) {
                Intent intent = new Intent(RegisterUserActivity.this, LoginActivity.class);
                startActivity(intent);
            }
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    /**
     * 处理网络请求数据
     */
    private void doNetRequest() {
        LogUtil.i(TAG, pwd + "doNetRequest: " + phone+" phonetype "+phonetype);
        EventBus.getDefault().post(new RequestRegisterEvent(phone, pwd, "ycz_"+phone.substring(7,11),phonetype,"ycz"), "requestRegiseter");
    }


    /**
     * 请求注册
     */
    @Subscriber(mode = ThreadMode.ASYNC, tag = "RegiseterData")
    public void RequestRegisterEvent(RegiseterDataEvent data) {
        String isRegistered = data.isRegistered;
        regFlag = true;
        hd.removeCallbacks(runnable);
        LogUtil.e(TAG, "RequestLoginEvent: 请求登录");
        if (isRegistered.equals("0")) {   //0 代表注册成功，
            try {
                sendRegist(data.userid);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            toast("注册成功!");
            pd.dismiss();
            Intent intent = new Intent(RegisterUserActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else if (isRegistered.equals("1")) {//1 代表已经注册过
            pd.dismiss();
            toast("该账户已经注册过，请勿重复注册");
            phone = etPhoneNum.getText().toString().trim();
            Intent intent = new Intent(RegisterUserActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {// 未知（其他错误）
            pd.dismiss();
            toast("注册失败，出现未知错误");
            etYzm.setText("");
            password.setText("");
            et_password_2.setText("");
            etPhoneNum.setText("");
            finish();
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
        String url2 ;
        url2=Constants.FIND_URL_API_ADDUSER + "userid=" + userid + "&phone=" + phone + "&txid=" + phone.substring(phone.length() - 2)+"&nc="+ URLEncoder.encode("111","UTF-8")+"&yqm="+0;
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

//    /**
//     * 修改昵称
//     * */
//    private void changeNc(String userid) throws UnsupportedEncodingException {
//        String url = Constants.FIND_URL_API_CHANGE_NC+"userid="+userid+"&nc="+ URLEncoder.encode(myNc,"UTF-8");
//        //注册完成接口
//        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        ToastUtils.showToast(RegisterUserActivity.this,"昵称添加成功");
//                        sp.edit().putString("nc",myNc).commit();
//                        finish();
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                ToastUtils.showToast(RegisterUserActivity.this,"昵称添加失败");
//
//            }
//        });
//        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        MyApplication.getRequestQueue().add(stringRequest);
//    }
}
