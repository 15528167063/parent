package com.hzkc.parent.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.hzkc.parent.event.ChangePswDataEvent;
import com.hzkc.parent.event.RequestChangePswEvent;
import com.hzkc.parent.mina.Constants;
import com.hzkc.parent.utils.LogUtil;
import com.hzkc.parent.utils.NetworkUtil;
import com.hzkc.parent.utils.SignatureAlgorithm;
import com.hzkc.parent.utils.ToastUtils;
import com.jaeger.library.StatusBarUtil;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;

import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

import static com.hzkc.parent.mina.Constants.isUseMob;

public class FindPasswordActivity extends BasesActivity implements View.OnClickListener {

    private TextView tvTopTitle;
//    private ImageView ivShowConfirmPsw;
//    private ImageView ivShowPsw;
    private ImageView ivFinish;
    private EditText etPhoneNum;
    private EditText etYzm;
    private TextView btGetYzm;
    private TextView btRegistSubmit;
    private TimerTask tt;
    private Timer tm;

    private boolean flag1;
    private boolean flag2;

    private int TIME = 60;//倒计时60s
    public String country = "86";//中国区号
    private String phone;
    private static final int CODE_REPEAT = 1; //重新发送
    Handler hd = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == CODE_REPEAT) {
                btGetYzm.setEnabled(true);
                btRegistSubmit.setEnabled(true);
                tm.cancel();//取消任务
                tt.cancel();//取消任务
                TIME = 60;//时间重置
                btGetYzm.setText("重新发送验证码");
                btGetYzm.setTextColor(getResources().getColor(R.color.newcolor));
            } else {
                btGetYzm.setText("重新获取"+"("+TIME + "s)");
                btGetYzm.setTextColor(getResources().getColor(R.color.text_shallow_color));
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
                    doNetRequest();
                    //toast("验证成功，正在进入主界面");
                } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {//获取验证码成功
                    toast("获取验证码成功");
                } else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {//如果你调用了获取国家区号类表会在这里回调
                    //返回支持发送验证码的国家列表
                }
            } else {//错误等在这里（包括验证失败）
                //错误码请参照http://wiki.mob.com/android-api-错误码参考/这里我就不再继续写了
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
                //hd.removeCallbacks(runnable);
            }
        }
    };
    private EditText password;
    private EditText confirmPassword;
    private String pwd;
    //private Runnable runnable;
    private String codeResult;

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
        setContentView(R.layout.activity_find_password);
        StatusBarUtil.setColorNoTranslucent(this,getResources().getColor(R.color.newcolor));
        EventBus.getDefault().register(this);
        SMSSDK.registerEventHandler(eh); //注册短信回调（记得销毁，避免泄露内存）
        initView();
    }

    /**
     * 初始化数据
     */
    private void initView() {
        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        ivFinish = (ImageView) findViewById(R.id.iv_finish);
//        ivShowPsw = (ImageView) findViewById(R.id.iv_show_psw);
//        ivShowConfirmPsw = (ImageView) findViewById(R.id.iv_show_confirm_psw);
        etPhoneNum = (EditText) findViewById(R.id.et_phone_num);
        etYzm = (EditText) findViewById(R.id.et_yzm);
        btGetYzm = (TextView) findViewById(R.id.bt_get_yzm);
        btRegistSubmit = (TextView) findViewById(R.id.bt_regist_submit);
        password = (EditText) findViewById(R.id.et_password);
        confirmPassword = (EditText) findViewById(R.id.et_confirm_password);

        ivFinish.setVisibility(View.VISIBLE);
//        ivShowConfirmPsw.setImageResource(R.drawable.btn_eye_on);
//        ivShowPsw.setImageResource(R.drawable.btn_eye_on);

        //初始化学生的状态
        flag1 = false;
        flag2 = false;

        codeResult="";

        tvTopTitle.setText("忘记密码");
        ivFinish.setOnClickListener(this);
        btGetYzm.setOnClickListener(this);
        btRegistSubmit.setOnClickListener(this);
//        ivShowPsw.setOnClickListener(this);
//        ivShowConfirmPsw.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_finish:
                finish();
                break;
            case R.id.bt_get_yzm://获取短信验证码
                getMsgCode();
                break;
            case R.id.bt_regist_submit://提交注册信息
                submitRegistMsg();
                break;
//            case R.id.iv_show_psw://控制密码的显示与隐藏
//                setshowPsw();
//                break;
//            case R.id.iv_show_confirm_psw://控制确认密码的显示与隐藏
//                setshowConPsw();
//                break;
            default:
                break;
        }
    }

    /**
     * 提交验证码
     */
    private void submitRegistMsg() {
        //获得用户输入的验证码
        String code = etYzm.getText().toString().trim();
        phone = etPhoneNum.getText().toString().trim();
        pwd = password.getText().toString().trim();
        String checkPwd = confirmPassword.getText().toString().trim();
        boolean isNet = NetworkUtil.isConnected();
        if (!isNet) {
            ToastUtils.showToast(MyApplication.getContext(),"网络不通，请检查网络再试");
            return;
        }
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
            toast("请先输入手机号");
            return;
        }
        if (TextUtils.isEmpty(pwd) || TextUtils.isEmpty(checkPwd)) {
            toast("密码不能为空");
            return;
        } else if (pwd.length() < 6) {
            toast("密码长度不能低于6位");
            return;
        }
        if (!pwd.equals(checkPwd)) {
            toast("两次输入密码不一致，请重新输入！");
            return;
        }
        if (!TextUtils.isEmpty(code)) {//判断验证码是否为空
            //验证
            if(isUseMob){
                //验证
                SMSSDK.submitVerificationCode(country, phone, code);
            }else{
                UseHongJinCheck(phone,code);
            }
        } else {//如果用户输入的内容为空，提醒用户
            toast("请输入验证码后再提交");
            return;
        }
        //doNetRequest();
    }

    /**
     * 处理网络请求数据
     */
    private void doNetRequest() {
        LogUtil.i(TAG, pwd + "doNetRequest: " + phone);
        EventBus.getDefault().post(new RequestChangePswEvent(phone, pwd), "changePsw");
    }

    /**
     * 获取验证码
     */
    private void getMsgCode() {
        phone = etPhoneNum.getText().toString().trim();
        if (!TextUtils.isEmpty(phone)) {
            //定义需要匹配的正则表达式的规则
            String REGEX_MOBILE_SIMPLE = "[1][123456789]\\d{9}";
            //把正则表达式的规则编译成模板
            Pattern pattern = Pattern.compile(REGEX_MOBILE_SIMPLE);
            //把需要匹配的字符给模板匹配，获得匹配器
            Matcher matcher = pattern.matcher(phone);
            // 通过匹配器查找是否有该字符，不可重复调用重复调用matcher.find()
            if (matcher.find()) {//匹配手机号是否存在
                alterWarning();
            } else {
                ToastUtils.showToast(FindPasswordActivity.this,"手机号格式错误");
            }
        } else {
            ToastUtils.showToast(FindPasswordActivity.this,"请先输入手机号");
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
                dialog.dismiss(); //关闭dialog
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
                //Toast.makeText(RegisterUserActivity.this, "已取消" + which, Toast.LENGTH_SHORT).show();
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
            toast("输入验证码错误");
            //hd.removeCallbacks(runnable);
        }
    }

    /**
     * 销毁短信注册
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        SMSSDK.unregisterEventHandler(eh);
    }

    /**
     * 修改密码
     */
    @Subscriber(mode = ThreadMode.ASYNC, tag = "ChangePswData")
    public void RequestChangePswEvent(ChangePswDataEvent data) {
        String isChangePsw = data.changeFlag;
        LogUtil.e(TAG, "RequestLoginEvent: 请求登录");
        if (isChangePsw.equals("0")) {   //修改成功
            ToastUtils.showToast(FindPasswordActivity.this,"密码修改成功，正在进入登录界面");
            sp.edit().putString("phonePsw", pwd)
                    .commit();
            finish();
        } else {
            ToastUtils.showToast(FindPasswordActivity.this,"该用户不存在，请注册！");
            etPhoneNum.setText("");
            password.setText("");
            confirmPassword.setText("");
        }
    }

}
