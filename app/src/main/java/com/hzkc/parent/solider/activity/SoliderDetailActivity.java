package com.hzkc.parent.solider.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hzkc.parent.R;
import com.hzkc.parent.activity.BaseActivity;
import com.hzkc.parent.activity.EyeProtectActivity;
import com.hzkc.parent.activity.MyMemeberActivity;
import com.hzkc.parent.activity.StopInternetPlanActivity;
import com.hzkc.parent.appcation.MyApplication;
import com.hzkc.parent.event.ChangeChildDataEvent;
import com.hzkc.parent.event.DeleteChildEvent;
import com.hzkc.parent.event.InitEvent;
import com.hzkc.parent.event.OrderChildDataEvent;
import com.hzkc.parent.event.OrderDeleteChildEvent;
import com.hzkc.parent.event.SlbhDataEvent;
import com.hzkc.parent.event.SlbhEvent;
import com.hzkc.parent.event.YjgkDataEvent;
import com.hzkc.parent.event.YjgkEvent;
import com.hzkc.parent.greendao.GreenDaoManager;
import com.hzkc.parent.greendao.entity.AppDataBean;
import com.hzkc.parent.greendao.entity.ChildContrlFlag;
import com.hzkc.parent.greendao.entity.ChildsTable;
import com.hzkc.parent.greendao.entity.NetPlanDataBean;
import com.hzkc.parent.greendao.gen.AppDataBeanDao;
import com.hzkc.parent.greendao.gen.ChildContrlFlagDao;
import com.hzkc.parent.greendao.gen.ChildsTableDao;
import com.hzkc.parent.greendao.gen.NetPlanDataBeanDao;
import com.hzkc.parent.jsondata.CmdCommon;
import com.hzkc.parent.mina.Constants;
import com.hzkc.parent.utils.LogUtil;
import com.hzkc.parent.utils.ToastUtils;
import com.hzkc.parent.view.AffirmDialog;
import com.jaeger.library.StatusBarUtil;
import com.wx.wheelview.adapter.ArrayWheelAdapter;
import com.wx.wheelview.widget.WheelView;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class SoliderDetailActivity extends BaseActivity implements View.OnClickListener {
    private TextView tvTopTitle;
    private TextView tvSave;
    private ImageView ivYJGK;
    private ImageView ivSLBH;
    private ImageView ivFinish;
    private EditText etChildName;
    private EditText etChilSchool;
    private RadioGroup rgChildSex;
    private RadioButton rbSexNan;
    private RadioButton rbSexNv;
    private ImageView ivChildIcon;
    private TextView tvChildName,tv_gk;
    private LinearLayout llChildClass,llChildSchool;
    private TextView tvChildClass;
    private RelativeLayout rlChildUnbindcode;
    private RelativeLayout rlChildJzsw;
    private RelativeLayout rlChildSlbh;
    private TextView btChildDelete;
    String[] mChildClass = {"幼儿园小班", "幼儿园中班", "幼儿园大班", "一年级", "二年级",
            "三年级", "四年级", "五年级", "六年级", "七年级", "八年级", "九年级"};
    private List<String> list;
    private View view;
    private PopupWindow mPopupWindow;
    private WheelView wheelView;
    private Button btWheelCancel;
    private Button btWheelConfirm;
    private boolean slbhFlag;
    private boolean deleteChildFlag;
    private String childuuid;
    private String selectchildUUID;
    private String childbanji;
    private String childname;
    private String childnianji;
    private String childsex;
    private String parentUUID;
    private ChildsTableDao childDao;
    private ChildContrlFlagDao childCopntrlDao;
    private String childName;
    private String childClass;
    private ProgressDialog pd;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 101:
                    if (!deleteChildFlag) {
                        ToastUtils.showToast(MyApplication.getContext(), "当前连接不正常，请稍后再试");
                    }
                    if (pd != null && pd.isShowing()) {
                        pd.dismiss();
                    }
                    break;
                case 204://处理一键管控反馈
                    ordeyjgk(msg);
                    break;
                case 205://处理视力保护反馈
                    ordeslbh(msg);
                    break;
                default:
                    break;
            }
        }
    };
    private String yjgk;
    private String slbh;
    private String childScool,childFrom;
    private Runnable runnable;
    private String phonePsw;
    public String type;
    public boolean  isVip=false;
    public String   viplist ;
    private AffirmDialog affirmDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solider_detail);
        StatusBarUtil.setColorNoTranslucent(this,getResources().getColor(R.color.blue));
        childFrom=getIntent().getStringExtra("childFrom");
        initView();
        initData();
        initDao();
        viplist = sp.getString("viplist", "");
        isVip = sp.getBoolean("isVip", false);
        EventBus.getDefault().register(this);
        parentUUID = sp.getString("parentUUID", "");
        phonePsw = sp.getString("phonePsw", "");
        type=getIntent().getStringExtra("type");
        affirmDialog = new AffirmDialog(this);
        affirmDialog.setTitleText("您还不是VIP会员，不能使用VIP功能，是否立即充值?");
        affirmDialog.setAffirmClickListener(this);
        if(type!=null){
            childuuid = getIntent().getStringExtra("ChildUUID");
        }else {
            childuuid = sp.getString("ChildUUID", "");
        }

        runnable = new Runnable() {
            public void run() {
                Message msg = Message.obtain();
                msg.what = 101;
                handler.sendMessage(msg);
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();
        boolean childFlag2 = sp.getBoolean("childFlag2", false);
        if (childFlag2) {
            boolean slbh = sp.getBoolean("slbh", false);
            if (slbh) {
                ivSLBH.setImageResource(R.drawable.clock_on);
            } else {
                ivSLBH.setImageResource(R.drawable.clock_off);
            }
            sp.edit().putBoolean("childFlag1", false).commit();
        }
    }

    private void initView() {
        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        tvSave = (TextView) findViewById(R.id.tv_save);
        tv_gk = (TextView) findViewById(R.id.tv_gk);
        ivFinish = (ImageView) findViewById(R.id.iv_finish);
        ivYJGK = (ImageView) findViewById(R.id.iv_yjgk);
        ivSLBH = (ImageView) findViewById(R.id.iv_slbh);
        etChildName = (EditText) findViewById(R.id.et_child_name);
        etChilSchool = (EditText) findViewById(R.id.et_child_school);
        rgChildSex = (RadioGroup) findViewById(R.id.rg_child_sex);
        rbSexNan = (RadioButton) findViewById(R.id.rb_sex_nan);
        rbSexNv = (RadioButton) findViewById(R.id.rb_sex_nv);
        llChildClass = (LinearLayout) findViewById(R.id.ll_child_class);
        llChildSchool = (LinearLayout) findViewById(R.id.ll_child_school);
        tvChildClass = (TextView) findViewById(R.id.tv_child_class);
        rlChildUnbindcode = (RelativeLayout) findViewById(R.id.rl_child_unbindcode);
        rlChildJzsw = (RelativeLayout) findViewById(R.id.rl_child_jzsw);
        rlChildSlbh = (RelativeLayout) findViewById(R.id.rl_child_slbh);
        btChildDelete = (TextView) findViewById(R.id.bt_child_delete);
        ivChildIcon = (ImageView) findViewById(R.id.iv_child_icon);
        tvChildName = (TextView) findViewById(R.id.tv_child_name);


        tvTopTitle.setText("士兵资料");
        ivFinish.setVisibility(View.VISIBLE);
        tvSave.setVisibility(View.VISIBLE);

        rlChildUnbindcode.setVisibility(View.VISIBLE);
        llChildClass.setOnClickListener(this);
        tvSave.setOnClickListener(this);
        ivFinish.setOnClickListener(this);
        rlChildJzsw.setOnClickListener(this);
        rlChildSlbh.setOnClickListener(this);
        btChildDelete.setOnClickListener(this);
        ivSLBH.setOnClickListener(this);
        ivYJGK.setOnClickListener(this);
        rlChildUnbindcode.setOnClickListener(this);
        deleteChildFlag = false;
        yjgk = "";
        slbh = "";
    }

    private void initData() {
        list = new ArrayList<>();
        for (int i = 0; i < mChildClass.length; i++) {
            list.add(mChildClass[i]);
        }
        initWheelView();
    }

    /**
     * 初始化数据库
     */
    private void initDao() {
        Intent intent = getIntent();
        selectchildUUID = intent.getStringExtra("ChildUUID");
        LogUtil.e(TAG, "selectchildUUID" +selectchildUUID);
        childDao = GreenDaoManager.getInstance().getSession().getChildsTableDao();
        childCopntrlDao = GreenDaoManager.getInstance().getSession().getChildContrlFlagDao();
        ChildsTable findChild = childDao.queryBuilder().where(ChildsTableDao.Properties.Childuuid.eq(selectchildUUID)).build().unique();
        ChildContrlFlag findChildFlag = childCopntrlDao.queryBuilder().where(ChildContrlFlagDao.Properties.Childuuid.eq(selectchildUUID)).build().unique();
        LogUtil.e(TAG, "initDao: name" + findChild + ",sex:" + findChildFlag);
        if (findChild != null) {
            String yjgkflag = findChildFlag.getYjgkflag();
            childbanji = findChild.getSchool();
            childname = findChild.getName();
            childnianji = findChild.getNianji();
            childsex = findChild.getSex();
            LogUtil.e(TAG, "initDao: name" + childname + ",sex:" + childsex + ",nianji:" + childnianji + ",banji:" + childbanji);
            if (yjgkflag.equals(CmdCommon.CMD_FLAG_OPEN)) {//一键管控
                ivYJGK.setImageResource(R.drawable.clock_on);
                tv_gk.setText("解控：");
            } else {
                ivYJGK.setImageResource(R.drawable.clock_off);
                tv_gk.setText("受控：");
            }
        } else {
            LogUtil.e(TAG, "InternetFragment的方法执行了init命令");
            String parentUUID = sp.getString("parentUUID", "");
            EventBus.getDefault().post(new InitEvent(parentUUID), "init");
        }
        LogUtil.e(TAG, "initDao: name" + childname + ",sex:" + childsex + ",nianji:" + childnianji + ",banji:" + childbanji);
        if(childName==null && childsex==null&& childnianji==null &&childnianji==null){
            Toast.makeText(this, "请前往选择士兵界面选中士兵", Toast.LENGTH_SHORT).show();
            String childNames=sp.getString("childNames","");
            String ChildUUID=sp.getString("ChildUUIDs","");
            String childSexs=sp.getString("childSexs","");
            sp.edit().putString("ChildUUID", ChildUUID).putString("childName",childNames).putString("childSex",childSexs).commit();
            return;
        }
        etChildName.setText(childname);
        tvChildName.setText(childname);


        if(TextUtils.isEmpty(childbanji)){
            llChildClass.setVisibility(View.GONE);
        }else {
            llChildClass.setVisibility(View.GONE);
        }
        if(TextUtils.isEmpty(childnianji)){
            llChildSchool.setVisibility(View.GONE);
        }else {
            llChildSchool.setVisibility(View.GONE);
        }


        etChilSchool.setText(childbanji);
        if (childsex.equals(CmdCommon.FLAG_BOY)) {
            rbSexNan.setChecked(true);
            ivChildIcon.setImageResource(R.drawable.nanshibing);
        } else if (childsex.equals(CmdCommon.FLAG_GIRL)) {
            rbSexNv.setChecked(true);
            ivChildIcon.setImageResource(R.drawable.nvshibing);
        }
        rgChildSex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_sex_nan:
                        ivChildIcon.setImageResource(R.drawable.nanshibing);
                        childsex = CmdCommon.FLAG_BOY;
                        break;
                    case R.id.rb_sex_nv:
                        ivChildIcon.setImageResource(R.drawable.nvshibing);
                        childsex = CmdCommon.FLAG_GIRL;
                        break;
                    default:
                        break;
                }
            }
        });
        tvChildClass.setText(childnianji);
    }

    /**
     * 初始化滚轮
     */
    private void initWheelView() {
        view = View.inflate(SoliderDetailActivity.this, R.layout.dialogue_wheel_class, null);
        wheelView = (WheelView) view.findViewById(R.id.wheelview);
        btWheelCancel = (Button) view.findViewById(R.id.bt_wheel_cancel);
        btWheelConfirm = (Button) view.findViewById(R.id.bt_wheel_confirm);
        //初始化滚轮控件
        wheelView.setWheelAdapter(new ArrayWheelAdapter(SoliderDetailActivity.this)); // 文本数据源
        wheelView.setWheelSize(5);
        wheelView.setSelection(0);
        wheelView.setWheelClickable(true);
        wheelView.setSkin(WheelView.Skin.Holo); // common皮肤
        wheelView.setWheelData(list);  // 数据集合
        //设置PopupWindow
        mPopupWindow = new PopupWindow(view,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.setTouchable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
        mPopupWindow.getContentView().setFocusableInTouchMode(true);
        mPopupWindow.getContentView().setFocusable(true);
        mPopupWindow.setAnimationStyle(R.style.anim_menu_bottombar);
        //设置点击事件
        btWheelCancel.setOnClickListener(this);
        btWheelConfirm.setOnClickListener(this);
        //设置滑轮的点击事件
        wheelView.setOnWheelItemClickListener(new WheelView.OnWheelItemClickListener() {
            @Override
            public void onItemClick(int position, Object o) {
                tvChildClass.setText(mChildClass[position]);
                mPopupWindow.dismiss();
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.affirm_cancel:
                if (affirmDialog != null) {
                    affirmDialog.hide();
                }
                break;
            case R.id.affirm_confirm:
                if (affirmDialog != null) {
                    affirmDialog.hide();
                }
                Intent intents = new Intent(this, MyMemeberActivity.class);
                startActivity(intents);
                break;
            case R.id.iv_finish:
                finish();
                break;
            case R.id.ll_child_class://选择年级
                wheelView.setSelection(list.indexOf(tvChildClass.getText().toString()));
                mPopupWindow.showAtLocation(findViewById(R.id.ll_child_detail), Gravity.BOTTOM, 0, 0);
                break;
            case R.id.bt_wheel_cancel://取消wheelview
                mPopupWindow.dismiss();
                break;
            case R.id.bt_wheel_confirm://获取当前滚轮位置的数据
                String selectedClass = (String) wheelView.getSelectionItem();
                tvChildClass.setText(selectedClass);
                mPopupWindow.dismiss();
                break;
            case R.id.tv_save://保存
                deleteChildFlag = false;
                saveChild();
                break;
            case R.id.rl_child_jzsw://禁止上网计划
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
                    Toast.makeText(this, "士兵正处于团控状态，请稍后再试", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent1 = new Intent(this, StopInternetPlanActivity.class);
                intent1.putExtra("ChildUUID", selectchildUUID);
                startActivity(intent1);
                break;
            case R.id.iv_slbh://视力保护
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
                    Toast.makeText(this, "士兵正处于团控状态，请稍后再试", Toast.LENGTH_SHORT).show();
                    return;
                }
                setSLBH();
                handler.postDelayed(runnable, 10000);
                break;
            case R.id.rl_child_slbh:
                //如果是团控
                if(viplist.contains(Constants.VIP_EYE_PROTECT)){
                    if(!isVip){
                        if (affirmDialog != null) {
                            affirmDialog.show();
                        }
                        return;
                    }
                }
                if(sp.getBoolean("team"+childuuid,false)){
                    Toast.makeText(this, "士兵正处于团控状态，请稍后再试", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent2 = new Intent(this, EyeProtectActivity.class);
                intent2.putExtra("ChildUUID", selectchildUUID);
                startActivity(intent2);
                break;
            case R.id.iv_yjgk://一键管控
                boolean isOneControll=false;
                if(!TextUtils.isEmpty(viplist)) {
                    String[] sourceStrArray = viplist.split(",");
                    for (int i = 0; i < sourceStrArray.length; i++) {
                        if (sourceStrArray[i].equals(Constants.VIP_ONE_CONTROLL)) {
                            isOneControll = true;
                        }
                    }
                }
                //如果是团控
                if(isOneControll){
                    if(!isVip){
                        if (affirmDialog != null) {
                            affirmDialog.show();
                        }
                        return;
                    }
                }

                //如果是团控
                if(sp.getBoolean("team"+childuuid,false)){
                    Toast.makeText(this, "士兵正处于团控状态，请稍后再试", Toast.LENGTH_SHORT).show();
                    return;
                }
                setYJGK();
                handler.postDelayed(runnable, 10000);
                break;
            case R.id.bt_child_delete://删除士兵
                showDialogIsDeleteChild();
                break;
            case R.id.rl_child_unbindcode://查看士兵端卸载码
                lookChildUnbindCode();
                break;
            default:
                break;
        }
    }
    /**
     * 删除士兵弹出对话框
     * */
    private void showDialogIsDeleteChild() {
        AlertDialog.Builder builer = new AlertDialog.Builder(this);
        builer.setCancelable(false);
        builer.setTitle("删除士兵");
        builer.setIcon(R.drawable.log_ycz);
        View dialogView = View.inflate(this, R.layout.dialog_child_delete, null);
        final EditText EtPsw = (EditText) dialogView.findViewById(R.id.et_psw);
        builer.setView(dialogView);
        builer.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                    dialog.dismiss();
                }
                return false;
            }
        });
        builer.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Log.i(TAG, "确定");
                String psw = EtPsw.getText().toString().trim();
                if (TextUtils.isEmpty(psw)) {
                    ToastUtils.showToast(MyApplication.getContext(), "输入密码为空");
                } else if (psw.equals(phonePsw)) {//执行删除士兵操作
                    dialog.dismiss();
                    deleteChild();
                } else {
                    ToastUtils.showToast(MyApplication.getContext(), "输入密码错误");
                    dialog.dismiss();
                }
            }
        });
        builer.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Log.i(TAG, "取消");
                dialog.dismiss();
            }
        });
        builer.show();
    }

    /**
     * 查看士兵端卸载码
     */
    private void lookChildUnbindCode() {
        AlertDialog.Builder builer = new AlertDialog.Builder(this);
        builer.setCancelable(false);
        builer.setTitle("查看卸载码");
        builer.setIcon(R.drawable.log_ycz);
        View dialogView = View.inflate(this, R.layout.dialog_child_unbindcode, null);
        final EditText EtPsw = (EditText) dialogView.findViewById(R.id.et_psw);
        builer.setView(dialogView);
        builer.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                    dialog.dismiss();
                }
                return false;
            }
        });
        builer.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Log.i(TAG, "确定");
                String psw = EtPsw.getText().toString().trim();
                if (TextUtils.isEmpty(psw)) {
                    ToastUtils.showToast(MyApplication.getContext(), "输入密码为空");
                } else if (psw.equals(phonePsw)) {
                    dialog.dismiss();
                    Intent intent3 = new Intent(SoliderDetailActivity.this, SoliderUnbindCodeActivity.class);
                    intent3.putExtra("ChildUUID", selectchildUUID);
                    startActivity(intent3);
                } else {
                    ToastUtils.showToast(MyApplication.getContext(), "输入密码错误");
                    dialog.dismiss();
                }
            }
        });
        builer.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Log.i(TAG, "取消");
                dialog.dismiss();
            }
        });
        builer.show();
    }


    /**
     * 删除士兵数据
     */
    private void deleteChild() {
        deleteChildFlag = false;
        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }
        pd = new ProgressDialog(SoliderDetailActivity.this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage("正在删除士兵，请稍候");
        pd.show();
        EventBus.getDefault().post(new DeleteChildEvent(childuuid, parentUUID), "deleteChild");
        handler.postDelayed(runnable, 10000);
    }

    /**
     * 保存士兵数据
     */
    private void saveChild() {
        childName = etChildName.getText().toString().trim();
        childScool = etChilSchool.getText().toString().trim();
        childClass = tvChildClass.getText().toString().trim();
        if(TextUtils.isEmpty(childName)){
            ToastUtils.showToast(MyApplication.getContext(), "输入昵称为空");
            return;
        }
        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }
        pd = new ProgressDialog(SoliderDetailActivity.this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);
        //pd.setTitle("提示");
        pd.setMessage("正在修改士兵，请稍候");
        pd.show();
        if (rbSexNan.isChecked()) {
            childsex = CmdCommon.FLAG_BOY;
        } else {
            childsex = CmdCommon.FLAG_GIRL;
        }
        EventBus.getDefault().post(new ChangeChildDataEvent(selectchildUUID, childName, childsex, childClass, childScool,"0"), "changeChildData");
        handler.postDelayed(runnable, 10000);
    }

    /**
     * 一键管控
     */
    private void setYJGK() {
        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }
        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setCancelable(false);
        pd.setMessage("正在执行操作，请稍候");
        pd.show();
        deleteChildFlag = false;
        ChildContrlFlagDao childDao = GreenDaoManager.getInstance().getSession().getChildContrlFlagDao();
        ChildContrlFlag findChild = childDao.queryBuilder().where(ChildContrlFlagDao.Properties.Childuuid.eq(selectchildUUID)).build().unique();
        if (findChild != null) {
            if (findChild.getYjgkflag().equals(CmdCommon.CMD_FLAG_OPEN)) {
                if (selectchildUUID.equals(childuuid)) {
                    sp.edit().putBoolean("yjgk"+childuuid, false).putBoolean("childFlag1", true).commit();
                }
                yjgk = CmdCommon.CMD_FLAG_CLOSE;
            } else {
                yjgk = CmdCommon.CMD_FLAG_OPEN;
                if (selectchildUUID.equals(childuuid)) {
                    sp.edit().putBoolean("yjgk"+childuuid, true).putBoolean("childFlag1", true).commit();
                }
            }
        } else {
            ChildContrlFlag childFlag = new ChildContrlFlag(null, selectchildUUID, parentUUID, "", ""
                    , CmdCommon.CMD_FLAG_CLOSE, CmdCommon.CMD_FLAG_CLOSE
                    , CmdCommon.CMD_FLAG_CLOSE, "30", "5", CmdCommon.CMD_FLAG_CLOSE, CmdCommon.CMD_FLAG_CLOSE);
            childDao.insert(childFlag);
            yjgk = CmdCommon.CMD_FLAG_CLOSE;
            if (selectchildUUID.equals(childuuid)) {
                sp.edit().putBoolean("yjgk"+childuuid, false).putBoolean("childFlag1", true).commit();
            }
        }
        EventBus.getDefault().post(new YjgkEvent(parentUUID, selectchildUUID, yjgk), "yjgk");
        LogUtil.e(TAG, "一键管控: " + yjgk);
        Log.e("parent","msg",new Exception());
    }


    /**
     * 视力保护
     */
    private void setSLBH() {
        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }
        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setCancelable(false);
        pd.setMessage("正在执行操作，请稍候");
        pd.show();
        deleteChildFlag = false;
        String t1 = "";
        String t2 = "";
        ChildContrlFlagDao childDao = GreenDaoManager.getInstance().getSession().getChildContrlFlagDao();
        ChildContrlFlag findChild = childDao.queryBuilder().where(ChildContrlFlagDao.Properties.Childuuid.eq(selectchildUUID)).build().unique();
        if (findChild != null) {
            t1 = findChild.getSlbhSpacetime();
            t2 = findChild.getSlbhResttime();
            if (findChild.getSlbhflag().equals(CmdCommon.CMD_FLAG_OPEN)) {
                if (selectchildUUID.equals(childuuid)) {
                    sp.edit().putBoolean("slbh", false).putBoolean("childFlag2", true).commit();
                }
                slbh = CmdCommon.CMD_FLAG_CLOSE;
            } else {
                slbh = CmdCommon.CMD_FLAG_OPEN;
                if (selectchildUUID.equals(childuuid)) {
                    sp.edit().putBoolean("slbh", true).putBoolean("childFlag2", true).commit();
                }
            }
            childDao.update(findChild);
        } else {
            t1 = "30";
            t2 = "5";
            ChildContrlFlag childFlag = new ChildContrlFlag(null, selectchildUUID, parentUUID, "", ""
                    , CmdCommon.CMD_FLAG_CLOSE, CmdCommon.CMD_FLAG_CLOSE
                    , CmdCommon.CMD_FLAG_CLOSE, "30", "5", CmdCommon.CMD_FLAG_CLOSE, CmdCommon.CMD_FLAG_CLOSE);
            childDao.insert(childFlag);
            slbh = CmdCommon.CMD_FLAG_CLOSE;
            if (selectchildUUID.equals(childuuid)) {
                sp.edit().putBoolean("slbh", false).putBoolean("childFlag2", true).commit();
            }
        }
        EventBus.getDefault().post(new SlbhEvent(parentUUID, selectchildUUID, slbh, t1, t2), "slbh");
        LogUtil.e(TAG, "视力保护: " + slbh);
    }

    @Override
    protected void onDestroy() {
        deleteChildFlag = true;
        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    /**
     * 修改士兵信息返回结果
     */
    @Subscriber(mode = ThreadMode.ASYNC, tag = "orederChildData")
    public void RequestChangeChildEvent(OrderChildDataEvent data) {
        deleteChildFlag = true;
        handler.removeCallbacks(runnable);
        String isChangePsw = data.isRegistered;
        LogUtil.e(TAG, "RequestChangeChildEvent: 修改士兵信息返回结果");
        if (isChangePsw.equals("0")) {   //修改成功
            ChildsTableDao childDao = GreenDaoManager.getInstance().getSession().getChildsTableDao();
            ChildsTable findChild = childDao.queryBuilder().where(ChildsTableDao.Properties.Childuuid.eq(selectchildUUID)).build().unique();
            if (findChild != null) {
                findChild.setSex(childsex);
                findChild.setNianji(childClass);
                findChild.setName(childName);
                findChild.setSchool(childScool);
                childDao.update(findChild);
                if (selectchildUUID.equals(childuuid)) {
                    sp.edit().putString("childName", childName).putString("childSex", childsex).commit();
                }
            }
            Toast.makeText(this, "士兵信息修改成功", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "士兵信息修改失败,请稍后修改", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /**
     * 删除士兵信息返回结果
     */
    @Subscriber(mode = ThreadMode.ASYNC, tag = "orederdeleteChild")
    public void RequestDeleteChildEvent(OrderDeleteChildEvent data) {
        deleteChildFlag = true;
        handler.removeCallbacks(runnable);
        if (data.isRegistered.equals("0")) {
            NetPlanDataBeanDao netPlanDataBeanDao = GreenDaoManager.getInstance().getSession().getNetPlanDataBeanDao();
            AppDataBeanDao appDao = GreenDaoManager.getInstance().getSession().getAppDataBeanDao();
            List<NetPlanDataBean> list = netPlanDataBeanDao.queryBuilder()
                    .where(NetPlanDataBeanDao.Properties.Childuuid.eq(childuuid))
                    .build().list();
            List<AppDataBean> appList = appDao.queryBuilder()
                    .where(AppDataBeanDao.Properties.Childuuid.eq(childuuid))
                    .build().list();
            ChildsTable findChild = childDao.queryBuilder().where(ChildsTableDao.Properties.Childuuid.eq(selectchildUUID)).build().unique();
            ChildContrlFlag findChildFlag = childCopntrlDao.queryBuilder().where(ChildContrlFlagDao.Properties.Childuuid.eq(selectchildUUID)).build().unique();
            sp.edit().putBoolean("yjgk"+childuuid,false).commit();
            if (findChild != null) {
                childDao.delete(findChild);
            }
            if (findChildFlag != null) {
                childCopntrlDao.delete(findChildFlag);
            }
            if (list.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    netPlanDataBeanDao.delete(list.get(i));
                }
            }
            if (appList.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    appDao.delete(appList.get(i));
                }
            }
            // todo:
            LogUtil.i(TAG, "deleteChild: childuuid" + childuuid + ",selectchildUUID:" + selectchildUUID);
            if (selectchildUUID.equals(childuuid)) {
                List<ChildsTable> childlist = childDao.queryBuilder().build().list();
                if (childlist.size() > 0) {
                    sp.edit().putString("ChildUUID", childlist.get(0).getChilduuid())
                            .putString("childName", childlist.get(0).getName())
                            .putString("childSex", childlist.get(0).getSex())
                            .commit();
                    LogUtil.i(TAG, "deleteChild: " + childlist.get(0).getChilduuid() + childlist.get(0).getName());
                } else {
                    sp.edit()
                            .putString("ChildUUID", "")
                            .putString("childName", "")
                            .putString("childSex", "")
                            .commit();
                    LogUtil.i(TAG, "deleteChild: 111111111111");
                }
            }
            sp.edit().putString(childuuid + "first", "").commit();
            //删除士兵的childVersion
            String childVersion = sp.getString("childVersion", "");
            String[] split = childVersion.split(";");
            for (int i = 0; i < split.length; i++) {
                if (split[i].contains(childuuid)) {
                    split[i] = "";
                }
            }
            String childInfos = "";
            for (int i = 0; i < split.length; i++) {
                childInfos += split[i];
            }
            sp.edit().putString("childVersion", childInfos).commit();
            Toast.makeText(this, "删除士兵成功", Toast.LENGTH_SHORT).show();
            pd.dismiss();
            finish();
        } else {
            Toast.makeText(this, "删除士兵失败", Toast.LENGTH_SHORT).show();
            pd.dismiss();
            finish();
        }
    }

    /**
     * 一键管控功能反馈
     */
    @Subscriber(mode = ThreadMode.ASYNC, tag = "yjgkdata")
    public void yjgkDataEvent(YjgkDataEvent dataEvent) {
        deleteChildFlag = true;
        handler.removeCallbacks(runnable);
        Message obtain = Message.obtain();
        LogUtil.e(TAG, "一键管控功能反馈:yjgkDataEvent");
        obtain.obj = dataEvent.yjgkState;
        obtain.what = 204;
        handler.sendMessage(obtain);
    }

    /**
     * 视力保护功能反馈
     */
    @Subscriber(mode = ThreadMode.ASYNC, tag = "slbhata")
    public void slbhDataEvent(SlbhDataEvent dataEvent) {
        deleteChildFlag = true;
        handler.removeCallbacks(runnable);
        Message obtain = Message.obtain();
        LogUtil.e(TAG, "视力保护功能反馈:slbhDataEvent");
        obtain.obj = dataEvent.slbhState;
        obtain.what = 205;
        handler.sendMessage(obtain);
    }

    /**
     * 处理一键管控反馈
     */
    private void ordeyjgk(Message msg) {
        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }
        ChildContrlFlagDao childDao = GreenDaoManager.getInstance().getSession().getChildContrlFlagDao();
        ChildContrlFlag findChild = childDao.queryBuilder().where(ChildContrlFlagDao.Properties.Childuuid.eq(childuuid)).build().unique();
        LogUtil.e("ordeyjgk", "处理一键管控反馈:" + yjgk);
        if (findChild != null) {
            if (yjgk.equals(CmdCommon.CMD_FLAG_CLOSE)) {
                ToastUtils.showToast(MyApplication.getContext(), "管控功能关闭");
                sp.edit().putBoolean("yjgk"+childuuid,false).commit();
                ivYJGK.setImageResource(R.drawable.clock_off);
                tv_gk.setText("受控：");
                findChild.setYjgkflag(CmdCommon.CMD_FLAG_CLOSE);
            } else {
                ToastUtils.showToast(MyApplication.getContext(), "管控功能开启");
                sp.edit().putBoolean("yjgk"+childuuid,true).commit();
                ivYJGK.setImageResource(R.drawable.clock_on);
                tv_gk.setText("解控：");
                findChild.setYjgkflag(CmdCommon.CMD_FLAG_OPEN);
            }
            childDao.update(findChild);
        } else {
            ChildContrlFlag childFlag = new ChildContrlFlag(null, childuuid, parentUUID, "", ""
                    , CmdCommon.CMD_FLAG_CLOSE, CmdCommon.CMD_FLAG_CLOSE
                    , CmdCommon.CMD_FLAG_CLOSE, "30", "5", CmdCommon.CMD_FLAG_CLOSE, CmdCommon.CMD_FLAG_CLOSE);
            childDao.insert(childFlag);
            ivYJGK.setImageResource(R.drawable.clock_off);
            ToastUtils.showToast(MyApplication.getContext(), "管控功能关闭");
            sp.edit().putBoolean("yjgk"+childuuid,false).commit();
        }
    }

    /**
     * 处理视力保护反馈
     */
    private void ordeslbh(Message msg) {
        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }
        ChildContrlFlagDao childDao = GreenDaoManager.getInstance().getSession().getChildContrlFlagDao();
        ChildContrlFlag findChild = childDao.queryBuilder().where(ChildContrlFlagDao.Properties.Childuuid.eq(selectchildUUID)).build().unique();
        LogUtil.e("ordeslbh", "处理视力保护反馈:" + slbh);
        if (findChild != null) {
            if (slbh.equals(CmdCommon.CMD_FLAG_CLOSE)) {
                ToastUtils.showToast(MyApplication.getContext(), "视力保护功能关闭");
                ivSLBH.setImageResource(R.drawable.clock_off);
                findChild.setSlbhflag(CmdCommon.CMD_FLAG_CLOSE);
            } else {
                ToastUtils.showToast(MyApplication.getContext(), "视力保护功能开启");
                ivSLBH.setImageResource(R.drawable.clock_on);
                findChild.setSlbhflag(CmdCommon.CMD_FLAG_OPEN);
            }
            childDao.update(findChild);
        } else {
            ChildContrlFlag childFlag = new ChildContrlFlag(null, childuuid, parentUUID, "", ""
                    , CmdCommon.CMD_FLAG_CLOSE, CmdCommon.CMD_FLAG_CLOSE
                    , CmdCommon.CMD_FLAG_CLOSE, "30", "5", CmdCommon.CMD_FLAG_CLOSE, CmdCommon.CMD_FLAG_CLOSE);
            childDao.insert(childFlag);
            ivSLBH.setImageResource(R.drawable.clock_off);
            ToastUtils.showToast(MyApplication.getContext(), "视力保护功能关闭");
        }
    }
}
