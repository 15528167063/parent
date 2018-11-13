package com.hzkc.parent.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hzkc.parent.R;
import com.hzkc.parent.appcation.MyApplication;
import com.hzkc.parent.event.ChangePswDataEvent;
import com.hzkc.parent.event.RequestChangePswEvent;
import com.hzkc.parent.greendao.GreenDaoManager;
import com.hzkc.parent.greendao.gen.AppDataBeanDao;
import com.hzkc.parent.greendao.gen.AppUseBeanDao;
import com.hzkc.parent.greendao.gen.ChildContrlFlagDao;
import com.hzkc.parent.greendao.gen.ChildsTableDao;
import com.hzkc.parent.greendao.gen.LoveTrailDataDao;
import com.hzkc.parent.greendao.gen.NetPlanDataBeanDao;
import com.hzkc.parent.greendao.gen.PhoneDataDao;
import com.hzkc.parent.utils.ActivityCollector;
import com.hzkc.parent.utils.ToastUtils;
import com.jaeger.library.StatusBarUtil;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;

public class ChangePasswordActivity extends BaseActivity implements View.OnClickListener {

    private TextView tvTopTitle;
    private ImageView ivFinish;
    private EditText etNowPassword;
    private EditText etNewPassword;
    private EditText etConfirmPassword;
    private TextView btComfirm;

    private boolean flag1;
    private String psw;
    private String phoneNum;
    private String conNewPwd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        StatusBarUtil.setColorNoTranslucent(this,getResources().getColor(R.color.blue));
        EventBus.getDefault().register(this);
        initView();
    }

    private void initView() {
        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        ivFinish = (ImageView) findViewById(R.id.iv_finish);
        etNowPassword = (EditText) findViewById(R.id.et_now_password);
        etNewPassword = (EditText) findViewById(R.id.et_new_password);
        etConfirmPassword = (EditText) findViewById(R.id.et_confirm_password);
        btComfirm = (TextView) findViewById(R.id.bt_comfirm);
        ivFinish.setVisibility(View.VISIBLE);
        //初始化学生的状态
        flag1 = false;
        psw = sp.getString("phonePsw", "");
        phoneNum = sp.getString("phoneNum", "");
        tvTopTitle.setText("修改密码");
        ivFinish.setOnClickListener(this);
        btComfirm.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_finish:
                finish();
                break;
            case R.id.bt_comfirm://确定修改密码
                changePwd();
                break;
            default:
                break;
        }
    }

    private void changePwd() {
        String nowPwd = etNowPassword.getText().toString().trim();
        String newPwd = etNewPassword.getText().toString().trim();
        conNewPwd = etConfirmPassword.getText().toString().trim();
        if(TextUtils.isEmpty(nowPwd)&&TextUtils.isEmpty(newPwd)&&TextUtils.isEmpty(conNewPwd)){
            Toast.makeText(this, "输入密码有空，请输入后按确定", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!nowPwd.equals(psw)){
            Toast.makeText(this, "输入密码不正确，请重新输入", Toast.LENGTH_SHORT).show();
            etNowPassword.setText("");
            etNowPassword.requestFocus();
            return;
        }
        if (newPwd.length() < 6) {
            etNewPassword.setText("");
            etConfirmPassword.setText("");
            etNewPassword.requestFocus();
            ToastUtils.showToast(MyApplication.getContext(),"输入密码长度不能低于6位");
            return;
        }
        if(!newPwd.equals(conNewPwd)){
            Toast.makeText(this, "两次输入密码不正确，请重新输入", Toast.LENGTH_SHORT).show();
            etNewPassword.setText("");
            etConfirmPassword.setText("");
            etNewPassword.requestFocus();
            return;
        }
        EventBus.getDefault().post(new RequestChangePswEvent(phoneNum, conNewPwd), "changePsw");
    }


    /**
     * 控制当前密码的显示与隐藏
     */
    private void setShowNOwpsw() {
        flag1 = !flag1;
        if (flag1) {
            //如果选中，显示密码
            etNowPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            //ivShowNowPassword.setImageResource(R.drawable.btn_eye_off);
        } else {
            //否则隐藏密码
            etNowPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            //ivShowNowPassword.setImageResource(R.drawable.btn_eye_on);
        }
        //设置光标到最后一位
        etNowPassword.setSelection(etNowPassword.getText().length());
        //设置焦点
        etNowPassword.requestFocus();
    }

    /**
     * 修改密码
     */
    @Subscriber(mode = ThreadMode.ASYNC, tag = "ChangePswData")
    public void RequestChangePswEvent(ChangePswDataEvent data) {
        String isChangePsw = data.changeFlag;
        if (isChangePsw.equals("0")) {   //修改成功
            ToastUtils.showToast(MyApplication.getContext(),"修改成功");
            logout();
        } else {
            ToastUtils.showToast(MyApplication.getContext(), "修改失败，请稍后再试");
            finish();
        }
    }
    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
    /**
     * 退出登陆
     */
    private void logout() {
        final ProgressDialog pd = new ProgressDialog(this);
        String st = getResources().getString(R.string.Are_logged_out);
        pd.setMessage(st);
        pd.setCanceledOnTouchOutside(false);
        pd.show();
        tuichu();
    }
    private void tuichu() {
        sp.edit().putString("childName", "")
                .putString("ChildUUID", "")
                .putString("parentUUID", "")
                .putString("childSex", "")
                .putString("childztl", "")
                .putString("childImage", "")
                .putString("token", "")
                .putString("tokens", "")
                .putBoolean("devicetoken", false)
                .putString("loginImFlag", "")
                .putString("childVersion", "")
                .putString("loginImFlag","")
                .putString("headimage","")    //清空本地头像，昵称，版本，推送id值，加密token,
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
        Intent intent5 = new Intent(this, LoginActivity.class);
        startActivity(intent5);
        ActivityCollector.finishAll();
    }
}
