package com.hzkc.parent.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hzkc.parent.R;
import com.hzkc.parent.appcation.MyApplication;
import com.hzkc.parent.event.ChildLocationPathDataEvent;
import com.hzkc.parent.event.DeleteChildEvent;
import com.hzkc.parent.event.OrderDeleteChildEvent;
import com.hzkc.parent.event.RequestChildLoactionEvent;
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
import com.hzkc.parent.jsondata.LocationData;
import com.hzkc.parent.utils.LogUtil;
import com.hzkc.parent.utils.ToastUtils;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;

import java.util.List;

/**
 * 孩子状态界面
 */
public class ChildStateActivity extends BaseActivity implements View.OnClickListener {
    private TextView tvTopTitle;
    private TextView tvName;
    private ImageView ivFinish;
    private ImageView ivHead;
    private TextView  btn_delete,tv_app_name,tv_address;
    private RelativeLayout re_xiezaima;
    private static final String TAG = "AddChildActivity";

    private String parentUUID;
    private String childuuid;
    private Runnable runnable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_state);
        EventBus.getDefault().register(this);
        initView();
        initData();
    }
    private void initData() {
        sp.edit().putString("isstate","1").commit();
        EventBus.getDefault().post(new RequestChildLoactionEvent(childuuid,parentUUID),"requestLoactionPath");
    }

    /**
     * 处理最后获取的位置
     */
    @Subscriber(mode = ThreadMode.ASYNC, tag = "LoactionPathData1")
    public void RequestLocationEvent(ChildLocationPathDataEvent dataEvent) {
        String getData = sp.getString(childuuid + "LocationPath", "");
        final List<LocationData> locationDataList = new Gson().fromJson(getData, new TypeToken<List<LocationData>>() {}.getType());
        if(locationDataList!=null && locationDataList.size()>0){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tv_address.setText(locationDataList.get(locationDataList.size()-1).b+"      ");
                }
            });
        }
    }

    private void initView() {
        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        tvName = (TextView) findViewById(R.id.tv_name);
        ivFinish = (ImageView) findViewById(R.id.iv_left);
        ivHead = (ImageView) findViewById(R.id.iv_head);
        re_xiezaima= (RelativeLayout) findViewById(R.id.re_xiezaima);
        btn_delete= (TextView) findViewById(R.id.btn_delete);
        tv_app_name= (TextView) findViewById(R.id.tv_app_name);
        tv_address= (TextView) findViewById(R.id.tv_address);
        tvTopTitle.setText("孩子状态");
        ivFinish.setVisibility(View.VISIBLE);
        ivFinish.setOnClickListener(this);
        re_xiezaima.setOnClickListener(this);
        btn_delete.setOnClickListener(this);

        phonePsw = sp.getString("phonePsw", "");
        parentUUID = sp.getString("parentUUID", "");
        childuuid = sp.getString("ChildUUID", "");
        selectchildUUID = getIntent().getStringExtra("ChildUUID");
        //最后使用的app
        String lastapp=sp.getString("lastapp","暂无");
        tv_app_name.setText(lastapp);
        //最后使用的app
//        String lastadress=sp.getString("lastadress","未获取到位置");
//        tv_address.setText(lastadress+"  ");


        childDao = GreenDaoManager.getInstance().getSession().getChildsTableDao();
        childCopntrlDao = GreenDaoManager.getInstance().getSession().getChildContrlFlagDao();
        ChildsTable findChild = childDao.queryBuilder().where(ChildsTableDao.Properties.Childuuid.eq(selectchildUUID)).build().unique();
        if (findChild != null) {
            tvName.setText(findChild.getName());
            if (findChild.getSex().equals(CmdCommon.FLAG_BOY)) {
                ivHead.setImageResource(R.drawable.child_detail_icon_boy);
            } else if (findChild.getSex().equals(CmdCommon.FLAG_GIRL)) {
                ivHead.setImageResource(R.drawable.child_detail_icon_girl);
            }
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_left://返回界面
                finish();
                break;
            case R.id.re_xiezaima://获取卸载码
                lookChildUnbindCode();
                break;
            case R.id.btn_delete://删除孩子
                showDialogIsDeleteChild();
                break;
            default:
                break;
        }
    }

    /**
     * 查看孩子端卸载码
     */
    private String phonePsw;
    private String selectchildUUID;
    private void lookChildUnbindCode() {
        AlertDialog.Builder builer = new AlertDialog.Builder(this);
        builer.setCancelable(false);
        builer.setTitle("优成长");
        builer.setIcon(R.drawable.icon);
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
                    Intent intent3 = new Intent(ChildStateActivity.this, ChildUnbindCodeActivity.class);
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
     * 删除孩子弹出对话框
     * */
    private void showDialogIsDeleteChild() {
        AlertDialog.Builder builer = new AlertDialog.Builder(this);
        builer.setCancelable(false);
        builer.setTitle("优成长");
        builer.setIcon(R.drawable.icon);
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
                } else if (psw.equals(phonePsw)) {//执行删除孩子操作
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
     * 删除孩子数据
     */
    private ProgressDialog pd;
    private boolean deleteChildFlag;
    private ChildsTableDao childDao;
    private ChildContrlFlagDao childCopntrlDao;
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
                default:
                    break;
            }
        }
    };
    private void deleteChild() {
        deleteChildFlag = false;
        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }
        pd = new ProgressDialog(ChildStateActivity.this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage("正在删除孩子，请稍候");
        pd.show();
        EventBus.getDefault().post(new DeleteChildEvent(childuuid, parentUUID), "deleteChild");
        handler.postDelayed(runnable, 5000);
    }


    /**
     * 删除孩子信息返回结果
     */
    @Subscriber(mode = ThreadMode.ASYNC, tag = "orederdeleteChild")
    public void RequestDeleteChildEvent(OrderDeleteChildEvent data) {
        deleteChildFlag = true;
        handler.removeCallbacks(runnable);
        if (data.isRegistered.equals("0")) {
            NetPlanDataBeanDao netPlanDataBeanDao = GreenDaoManager.getInstance().getSession().getNetPlanDataBeanDao();
            AppDataBeanDao appDao = GreenDaoManager.getInstance().getSession().getAppDataBeanDao();
            List<NetPlanDataBean> list = netPlanDataBeanDao.queryBuilder().where(NetPlanDataBeanDao.Properties.Childuuid.eq(childuuid)).build().list();
            List<AppDataBean> appList = appDao.queryBuilder().where(AppDataBeanDao.Properties.Childuuid.eq(childuuid)).build().list();
            ChildsTable findChild = childDao.queryBuilder().where(ChildsTableDao.Properties.Childuuid.eq(selectchildUUID)).build().unique();
            ChildContrlFlag findChildFlag = childCopntrlDao.queryBuilder().where(ChildContrlFlagDao.Properties.Childuuid.eq(selectchildUUID)).build().unique();
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
            LogUtil.i(TAG, "deleteChild: childuuid" + childuuid + ",selectchildUUID:" + selectchildUUID);
            if (selectchildUUID.equals(childuuid)) {
                List<ChildsTable> childlist = childDao.queryBuilder().build().list();
                if (childlist.size() > 0) {
                    sp.edit().putString("ChildUUID", childlist.get(0).getChilduuid()).putString("childName", childlist.get(0).getName())
                            .putString("childSex", childlist.get(0).getSex()).commit();
                    LogUtil.e(TAG, "deleteChild: " + childlist.get(0).getChilduuid() + childlist.get(0).getName());
                } else {
                    sp.edit().putString("ChildUUID", "").putString("childName", "").putString("childSex", "").commit();
                }
            }
            sp.edit().putString(childuuid + "first", "").commit();


            //删除孩子的childVersion
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
            Toast.makeText(this, "删除孩子成功", Toast.LENGTH_SHORT).show();
            pd.dismiss();
            //通知跟新小孩头像
            Intent intent = new Intent("touxiang");
            sendBroadcast(intent);

            finish();
        } else {
            Toast.makeText(this, "删除孩子失败", Toast.LENGTH_SHORT).show();
            pd.dismiss();
            finish();
        }
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

}
